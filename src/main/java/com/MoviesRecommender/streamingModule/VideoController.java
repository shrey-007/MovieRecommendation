package com.MoviesRecommender.streamingModule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/videos")
@CrossOrigin("*")
@Slf4j
public class VideoController {

    @Autowired
    private TmdbService tmdbService;

    @Autowired
    private TorrentioService torrentioService;

    @Autowired
    private TorrentStreamingService torrentStreamingService;

    // Cache: movie title -> active infoHash (so we don't re-resolve for each chunk request)
    private final Map<String, String> titleToInfoHash = new ConcurrentHashMap<>();

    /**
     * Stream video in chunks using HTTP Range requests.
     * Pipeline: Movie Title -> TMDB (IMDB ID) -> Torrentio (infoHash) -> libtorrent4j (sequential download) -> HTTP stream
     *
     * Browser 0 to end ki range ki request bhejega but apan code mai 0 to 1MB tak ki range bhejege browser ko
     * and browser usko play kr dega phir browser ko pata padega ki uske paas 1MB se end tak ka data nhi hai
     * toh voh 1MB to end ka data ki request bhejega, but hum 1MB to 2MB ka data bhejege and so on
     */
    @GetMapping("/stream/range")
    public ResponseEntity<Resource> streamVideoRange(
            @RequestParam(value = "title", required = false) String title,
            @RequestHeader(value = "Range", required = false) String range) {

        if (title == null || title.isBlank()) {
            log.error("No movie title provided for streaming");
            return ResponseEntity.badRequest().build();
        }

        log.info("Stream request for movie: '{}', Range: {}", title, range);

        try {
            // Step 1: Resolve torrent (use cache if available)
            String infoHash = titleToInfoHash.get(title);

            if (infoHash == null) {
                infoHash = resolveTorrent(title);
                if (infoHash == null) {
                    log.error("Could not resolve torrent for movie: {}", title);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
                titleToInfoHash.put(title, infoHash);
            }

            // Step 2: Get file length from torrent metadata
            long fileLength = torrentStreamingService.getFileLength(infoHash);
            if (fileLength <= 0) {
                log.error("Could not determine file length for: {}", infoHash);
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            }

            // Step 3: If no Range header, return basic response with content info
            if (range == null) {
                // Browser is asking for the whole file - return headers so it knows to use Range requests
                HttpHeaders headers = new HttpHeaders();
                headers.add("Accept-Ranges", "bytes");
                headers.setContentLength(fileLength);
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.parseMediaType(torrentStreamingService.getContentType(infoHash)))
                        .build();
            }

            // Step 4: Parse Range header and calculate chunk boundaries
            long rangeStart;
            long rangeEnd;

            String[] ranges = range.replace("bytes=", "").split("-");
            rangeStart = Long.parseLong(ranges[0]);
            rangeEnd = rangeStart + 1024 * 1024 - 1; // 1MB chunks

            if (rangeEnd >= fileLength) {
                rangeEnd = fileLength - 1;
            }

            log.info("Serving range {}-{} of {} for '{}'", rangeStart, rangeEnd, fileLength, title);
            log.info("Torrent status: {}", torrentStreamingService.getProgressInfo(infoHash));

            // Step 5: Get the data from the torrent download
            byte[] data = torrentStreamingService.getStreamData(infoHash, rangeStart, rangeEnd);

            if (data == null) {
                log.error("Could not read stream data for range {}-{}", rangeStart, rangeEnd);
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            }

            long contentLength = data.length;
            // Adjust rangeEnd to match actual data length
            rangeEnd = rangeStart + contentLength - 1;

            // Step 6: Build response with proper headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
            headers.add("Accept-Ranges", "bytes");
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("X-Content-Type-Options", "nosniff");
            headers.setContentLength(contentLength);

            return ResponseEntity
                    .status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(torrentStreamingService.getContentType(infoHash)))
                    .body(new ByteArrayResource(data));

        } catch (Exception ex) {
            log.error("Error streaming video for title: {}", title, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint to check streaming status / readiness of a torrent.
     * Frontend can poll this to show buffering state.
     */
    @GetMapping("/stream/status")
    public ResponseEntity<Map<String, Object>> getStreamStatus(@RequestParam String title) {
        String infoHash = titleToInfoHash.get(title);
        Map<String, Object> status = new ConcurrentHashMap<>();

        if (infoHash == null) {
            status.put("state", "not_started");
            status.put("ready", false);
            return ResponseEntity.ok(status);
        }

        boolean ready = torrentStreamingService.isReadyToStream(infoHash);
        String progressInfo = torrentStreamingService.getProgressInfo(infoHash);

        status.put("state", ready ? "ready" : "buffering");
        status.put("ready", ready);
        status.put("progress", progressInfo);
        status.put("infoHash", infoHash);

        return ResponseEntity.ok(status);
    }

    /**
     * Endpoint to pre-start torrent resolution and download.
     * Called by frontend when page loads so user doesn't wait when pressing play.
     */
    @PostMapping("/stream/prepare")
    public ResponseEntity<Map<String, Object>> prepareStream(@RequestParam String title) {
        Map<String, Object> result = new ConcurrentHashMap<>();

        try {
            String infoHash = titleToInfoHash.get(title);
            if (infoHash == null) {
                infoHash = resolveTorrent(title);
                if (infoHash != null) {
                    titleToInfoHash.put(title, infoHash);
                }
            }

            if (infoHash != null) {
                result.put("success", true);
                result.put("infoHash", infoHash);
                result.put("message", "Torrent preparation started");
            } else {
                result.put("success", false);
                result.put("message", "Could not find torrent for this movie");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Full torrent resolution pipeline:
     *   Movie Title -> TMDB (IMDB ID) -> Torrentio (streams) -> libtorrent4j (start download)
     */
    private String resolveTorrent(String title) {
        // Step 1: Get IMDB ID from TMDB
        log.info("Step 1: Resolving IMDB ID for '{}'...", title);
        String imdbId = tmdbService.getImdbId(title);
        if (imdbId == null) {
            log.error("Could not find IMDB ID for: {}", title);
            return null;
        }
        log.info("Found IMDB ID: {} for '{}'", imdbId, title);

        // Step 2: Get torrent streams from Torrentio
        log.info("Step 2: Fetching Torrentio streams for {}...", imdbId);
        List<TorrentStreamInfo> streams = torrentioService.getStreams(imdbId);
        if (streams.isEmpty()) {
            log.error("No torrent streams found for IMDB ID: {}", imdbId);
            return null;
        }

        // Step 3: Select the best stream
        TorrentStreamInfo bestStream = torrentioService.selectBestStream(streams);
        if (bestStream == null) {
            log.error("Could not select a stream for: {}", title);
            return null;
        }
        log.info("Step 3: Selected stream - {}", bestStream.getTitle());

        // Step 4: Start torrent download with libtorrent4j
        String magnetUri = bestStream.toMagnetUri();
        log.info("Step 4: Starting torrent download...");

        String infoHash = torrentStreamingService.startStreamingFromInfoHash(
                bestStream.getInfoHash(), magnetUri);

        if (infoHash == null) {
            log.error("Failed to start torrent for: {}", title);
            return null;
        }

        log.info("Torrent streaming pipeline complete for '{}'. InfoHash: {}", title, infoHash);
        return infoHash;
    }
}