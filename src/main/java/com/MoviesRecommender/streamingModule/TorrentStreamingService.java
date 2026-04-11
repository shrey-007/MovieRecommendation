package com.MoviesRecommender.streamingModule;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.libtorrent4j.*;
import org.libtorrent4j.alerts.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Core service that manages torrent downloads using libtorrent4j.
 * Handles sequential downloading for video streaming and provides
 * byte-range access to partially downloaded torrent files.
 */
@Service
@Slf4j
public class TorrentStreamingService {

    @Value("${torrent.download.dir}")
    private String downloadDir;

    private SessionManager sessionManager;

    // Track active torrents: infoHash -> TorrentHandle
    private final Map<String, TorrentHandle> activeTorrents = new ConcurrentHashMap<>();

    // Track torrent file paths: infoHash -> video file path
    private final Map<String, String> torrentFilePaths = new ConcurrentHashMap<>();

    // Track torrent readiness: infoHash -> is metadata received
    private final Map<String, CountDownLatch> metadataLatches = new ConcurrentHashMap<>();

    // Store handles from metadata alerts: infoHash -> TorrentHandle
    private final Map<String, TorrentHandle> metadataHandles = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("Initializing libtorrent4j session...");
        sessionManager = new SessionManager();

        // Set up alert listener for metadata and piece completion events
        sessionManager.addListener(new AlertListener() {
            @Override
            public int[] types() {
                return null; // listen to all alert types
            }

            @Override
            public void alert(Alert<?> alert) {
                if (alert instanceof MetadataReceivedAlert) {
                    MetadataReceivedAlert metaAlert = (MetadataReceivedAlert) alert;
                    TorrentHandle handle = metaAlert.handle();
                    String hash = handle.infoHash().toHex().toLowerCase();
                    log.info("Metadata received for torrent: {}", hash);

                    // Store the handle from the alert
                    metadataHandles.put(hash, handle);

                    CountDownLatch latch = metadataLatches.get(hash);
                    if (latch != null) {
                        latch.countDown();
                    }
                } else if (alert instanceof TorrentFinishedAlert) {
                    TorrentFinishedAlert finAlert = (TorrentFinishedAlert) alert;
                    log.info("Torrent download complete: {}",
                            finAlert.handle().infoHash().toHex());
                } else if (alert instanceof AddTorrentAlert) {
                    AddTorrentAlert addAlert = (AddTorrentAlert) alert;
                    if (addAlert.error().isError()) {
                        log.error("Error adding torrent: {}", addAlert.error().message());
                    } else {
                        TorrentHandle handle = addAlert.handle();
                        String hash = handle.infoHash().toHex().toLowerCase();
                        log.info("Torrent added to session: {}", hash);

                        // If torrent already has metadata (from a previous add or cache)
                        if (handle.status().hasMetadata()) {
                            metadataHandles.put(hash, handle);
                            CountDownLatch latch = metadataLatches.get(hash);
                            if (latch != null) {
                                latch.countDown();
                            }
                        }
                    }
                }
            }
        });

        // Start with default session params
        sessionManager.start(new SessionParams(new SettingsPack()));

        // Create download directory
        File dir = new File(downloadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        log.info("libtorrent4j session started. Download dir: {}", downloadDir);
    }

    @PreDestroy
    public void destroy() {
        log.info("Shutting down libtorrent4j session...");
        if (sessionManager != null && sessionManager.isRunning()) {
            sessionManager.stop();
        }
    }

    /**
     * Start streaming from an infoHash and magnet URI.
     * Downloads sequentially for video playback.
     * @param infoHash The torrent infoHash
     * @param magnetUri The full magnet URI
     * @return The infoHash on success, or null on failure
     */
    public String startStreamingFromInfoHash(String infoHash, String magnetUri) {
        String normalizedHash = infoHash.toLowerCase();
        log.info("Starting torrent stream for infoHash: {}", normalizedHash);

        try {
            // Check if already active
            if (activeTorrents.containsKey(normalizedHash)) {
                log.info("Torrent already active: {}", normalizedHash);
                return normalizedHash;
            }

            // Create a latch for metadata
            CountDownLatch latch = new CountDownLatch(1);
            metadataLatches.put(normalizedHash, latch);

            // Download the torrent via magnet
            File saveDir = new File(downloadDir);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            sessionManager.download(magnetUri, saveDir);

            // Wait for metadata
            log.info("Waiting for torrent metadata (up to 90 seconds)...");
            boolean received = latch.await(90, TimeUnit.SECONDS);

            if (!received) {
                log.error("Timeout waiting for torrent metadata for: {}", normalizedHash);
                return null;
            }

            // Get the handle from the alert-stored map
            TorrentHandle foundHandle = metadataHandles.get(normalizedHash);
            if (foundHandle == null) {
                log.error("Could not find torrent handle for: {}", normalizedHash);
                return null;
            }

            activeTorrents.put(normalizedHash, foundHandle);

            // Enable sequential downloading using flags
            foundHandle.setFlags(TorrentFlags.SEQUENTIAL_DOWNLOAD);

            // Find the largest video file in the torrent
            TorrentInfo info = foundHandle.torrentFile();
            FileStorage files = info.files();
            int bestFileIdx = -1;
            long bestFileSize = 0;

            for (int i = 0; i < files.numFiles(); i++) {
                String fileName = files.fileName(i);
                String lower = fileName.toLowerCase();
                if (lower.endsWith(".mp4") || lower.endsWith(".mkv") ||
                        lower.endsWith(".avi") || lower.endsWith(".webm")) {
                    long fileSize = files.fileSize(i);
                    if (fileSize > bestFileSize) {
                        bestFileSize = fileSize;
                        bestFileIdx = i;
                    }
                }
            }

            if (bestFileIdx == -1) {
                // Fallback to largest file regardless of extension
                for (int i = 0; i < files.numFiles(); i++) {
                    long fileSize = files.fileSize(i);
                    if (fileSize > bestFileSize) {
                        bestFileSize = fileSize;
                        bestFileIdx = i;
                    }
                }
            }

            if (bestFileIdx >= 0) {
                String filePath = Paths.get(downloadDir, files.filePath(bestFileIdx)).toString();
                torrentFilePaths.put(normalizedHash, filePath);
                log.info("Selected video file: {} ({}MB)", files.fileName(bestFileIdx),
                        bestFileSize / (1024 * 1024));

                // Prioritize the video file's pieces
                prioritizeFile(foundHandle, info, bestFileIdx);
            }

            log.info("Torrent streaming started. InfoHash: {}", normalizedHash);
            return normalizedHash;

        } catch (Exception e) {
            log.error("Error starting torrent stream", e);
            return null;
        }
    }

    /**
     * Prioritize downloading pieces for a specific file.
     * Sets high priority for the first and last pieces (needed for video headers/moov atom).
     */
    private void prioritizeFile(TorrentHandle handle, TorrentInfo info, int fileIdx) {
        FileStorage files = info.files();
        int numPieces = info.numPieces();
        int pieceLength = info.pieceLength();

        // Set all file priorities - ignore all files except target
        for (int i = 0; i < files.numFiles(); i++) {
            if (i == fileIdx) {
                handle.filePriority(i, Priority.DEFAULT);
            } else {
                handle.filePriority(i, Priority.IGNORE);
            }
        }

        // Calculate which pieces belong to our file
        long fileOffset = files.fileOffset(fileIdx);
        long fileSize = files.fileSize(fileIdx);
        int firstPiece = (int) (fileOffset / pieceLength);
        int lastPiece = (int) ((fileOffset + fileSize - 1) / pieceLength);

        // Set piece deadlines for first critical pieces (video headers)
        int criticalPieces = Math.min(20, lastPiece - firstPiece + 1);
        for (int i = firstPiece; i < firstPiece + criticalPieces && i < numPieces; i++) {
            handle.setPieceDeadline(i, 500); // 500ms deadline = high priority
        }

        // Also prioritize last few pieces (moov atom for MP4)
        int endCritical = Math.min(5, lastPiece - firstPiece + 1);
        for (int i = lastPiece; i > lastPiece - endCritical && i >= 0; i--) {
            handle.setPieceDeadline(i, 1000); // 1s deadline for end pieces
        }

        log.info("Prioritized pieces {}-{} for file idx {} ({} total pieces)",
                firstPiece, lastPiece, fileIdx, numPieces);
    }

    /**
     * Get the total file length of the streaming video.
     */
    public long getFileLength(String infoHash) {
        String normalizedHash = infoHash.toLowerCase();
        String filePath = torrentFilePaths.get(normalizedHash);
        if (filePath == null) {
            return -1;
        }

        TorrentHandle handle = activeTorrents.get(normalizedHash);
        if (handle == null) {
            return -1;
        }

        TorrentInfo info = handle.torrentFile();
        if (info == null) {
            return -1;
        }

        FileStorage files = info.files();
        for (int i = 0; i < files.numFiles(); i++) {
            String path = Paths.get(downloadDir, files.filePath(i)).toString();
            if (path.equals(filePath)) {
                return files.fileSize(i);
            }
        }

        return -1;
    }

    /**
     * Read stream data from the partially downloaded torrent file.
     * Waits for the required pieces to be downloaded if needed.
     * @param infoHash The torrent infoHash
     * @param rangeStart Start byte position
     * @param rangeEnd End byte position (inclusive)
     * @return byte array of the requested range, or null if unavailable
     */
    public byte[] getStreamData(String infoHash, long rangeStart, long rangeEnd) {
        String normalizedHash = infoHash.toLowerCase();
        String filePath = torrentFilePaths.get(normalizedHash);
        TorrentHandle handle = activeTorrents.get(normalizedHash);

        if (filePath == null || handle == null) {
            log.error("No active torrent for infoHash: {}", normalizedHash);
            return null;
        }

        TorrentInfo info = handle.torrentFile();
        if (info == null) {
            return null;
        }

        int pieceLength = info.pieceLength();

        // Find the file offset within the torrent
        FileStorage files = info.files();
        long fileOffset = 0;
        for (int i = 0; i < files.numFiles(); i++) {
            String path = Paths.get(downloadDir, files.filePath(i)).toString();
            if (path.equals(filePath)) {
                fileOffset = files.fileOffset(i);
                break;
            }
        }

        // Calculate which pieces we need
        int firstPiece = (int) ((fileOffset + rangeStart) / pieceLength);
        int lastPiece = (int) ((fileOffset + rangeEnd) / pieceLength);

        // Set deadlines for the required pieces to prioritize them
        for (int i = firstPiece; i <= lastPiece; i++) {
            handle.setPieceDeadline(i, 500);
        }

        // Wait for pieces to be available (up to 30 seconds)
        int maxWaitMs = 30000;
        int waitedMs = 0;
        int sleepInterval = 200;

        while (waitedMs < maxWaitMs) {
            boolean allAvailable = true;
            for (int i = firstPiece; i <= lastPiece; i++) {
                if (!handle.havePiece(i)) {
                    allAvailable = false;
                    break;
                }
            }

            if (allAvailable) {
                break;
            }

            try {
                Thread.sleep(sleepInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
            waitedMs += sleepInterval;

            if (waitedMs % 5000 == 0) {
                log.info("Waiting for pieces {}-{} ... ({}s elapsed)", firstPiece, lastPiece, waitedMs / 1000);
            }
        }

        if (waitedMs >= maxWaitMs) {
            log.warn("Timeout waiting for pieces {}-{}", firstPiece, lastPiece);
        }

        // Read the data from the file on disk
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                log.error("Torrent file not yet created on disk: {}", filePath);
                return null;
            }

            int contentLength = (int) (rangeEnd - rangeStart + 1);
            byte[] data = new byte[contentLength];

            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                raf.seek(rangeStart);
                int bytesRead = raf.read(data, 0, contentLength);
                if (bytesRead < contentLength) {
                    log.warn("Only read {} bytes out of {} requested", bytesRead, contentLength);
                    if (bytesRead <= 0) return null;
                    byte[] partial = new byte[bytesRead];
                    System.arraycopy(data, 0, partial, 0, bytesRead);
                    return partial;
                }
            }

            return data;

        } catch (IOException e) {
            log.error("Error reading torrent file data", e);
            return null;
        }
    }

    /**
     * Get streaming progress info for logging/debugging.
     */
    public String getProgressInfo(String infoHash) {
        String normalizedHash = infoHash.toLowerCase();
        TorrentHandle handle = activeTorrents.get(normalizedHash);
        if (handle == null) {
            return "No active torrent";
        }

        TorrentStatus status = handle.status();
        return String.format("Progress: %.1f%% | Download: %.1f KB/s | Peers: %d | State: %s",
                status.progress() * 100,
                status.downloadRate() / 1024.0,
                status.numPeers(),
                status.state().name());
    }

    /**
     * Check if a torrent is ready enough to start streaming (has some initial pieces).
     */
    public boolean isReadyToStream(String infoHash) {
        String normalizedHash = infoHash.toLowerCase();
        TorrentHandle handle = activeTorrents.get(normalizedHash);
        if (handle == null) {
            return false;
        }

        TorrentInfo info = handle.torrentFile();
        if (info == null) {
            return false;
        }

        int pieceLength = info.pieceLength();
        String filePath = torrentFilePaths.get(normalizedHash);
        if (filePath == null) {
            return false;
        }

        // Find file offset
        FileStorage files = info.files();
        long fileOffset = 0;
        for (int i = 0; i < files.numFiles(); i++) {
            String path = Paths.get(downloadDir, files.filePath(i)).toString();
            if (path.equals(filePath)) {
                fileOffset = files.fileOffset(i);
                break;
            }
        }

        int firstPiece = (int) (fileOffset / pieceLength);
        int checkPieces = Math.min(5, info.numPieces() - firstPiece);

        for (int i = firstPiece; i < firstPiece + checkPieces; i++) {
            if (!handle.havePiece(i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get content type based on file extension.
     */
    public String getContentType(String infoHash) {
        String normalizedHash = infoHash.toLowerCase();
        String filePath = torrentFilePaths.get(normalizedHash);
        if (filePath == null) {
            return "video/mp4";
        }
        String lower = filePath.toLowerCase();
        if (lower.endsWith(".mkv")) {
            return "video/x-matroska";
        } else if (lower.endsWith(".avi")) {
            return "video/x-msvideo";
        } else if (lower.endsWith(".webm")) {
            return "video/webm";
        }
        return "video/mp4";
    }
}
