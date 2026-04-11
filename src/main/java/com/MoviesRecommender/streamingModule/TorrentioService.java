package com.MoviesRecommender.streamingModule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to fetch torrent stream info from Torrentio (Stremio addon).
 * Uses the standard Stremio addon protocol:
 *   GET {addonUrl}/stream/movie/{imdbId}.json
 * Returns a list of available torrent streams sorted by quality preference.
 */
@Service
@Slf4j
public class TorrentioService {

    @Value("${torrentio.addon.url}")
    private String addonUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TorrentioService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetch available torrent streams for a movie from Torrentio.
     * @param imdbId The IMDB ID of the movie (e.g., "tt1375666")
     * @return List of TorrentStreamInfo sorted by quality, empty list if none found
     */
    public List<TorrentStreamInfo> getStreams(String imdbId) {
        List<TorrentStreamInfo> streams = new ArrayList<>();

        try {
            String url = addonUrl + "/stream/movie/" + imdbId + ".json";
            log.info("Fetching Torrentio streams from: {}", url);

            // Some Torrentio instances may need a User-Agent header
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String body = response.getBody();

            if (body == null) {
                log.warn("Empty response from Torrentio for IMDB ID: {}", imdbId);
                return streams;
            }

            JsonNode root = objectMapper.readTree(body);
            JsonNode streamsNode = root.get("streams");

            if (streamsNode == null || streamsNode.isEmpty()) {
                log.warn("No streams found in Torrentio response for IMDB ID: {}", imdbId);
                return streams;
            }

            for (JsonNode streamNode : streamsNode) {
                TorrentStreamInfo info = new TorrentStreamInfo();

                if (streamNode.has("infoHash")) {
                    info.setInfoHash(streamNode.get("infoHash").asText());
                } else {
                    // Skip streams without infoHash (e.g., direct URL streams that need debrid)
                    continue;
                }

                if (streamNode.has("title")) {
                    info.setTitle(streamNode.get("title").asText());
                }

                if (streamNode.has("fileIdx")) {
                    info.setFileIdx(streamNode.get("fileIdx").asInt());
                }

                if (streamNode.has("sources")) {
                    List<String> sources = new ArrayList<>();
                    for (JsonNode source : streamNode.get("sources")) {
                        sources.add(source.asText());
                    }
                    info.setSources(sources);
                }

                streams.add(info);
            }

            log.info("Found {} torrent streams for IMDB ID: {}", streams.size(), imdbId);

        } catch (Exception e) {
            log.error("Error fetching Torrentio streams for IMDB ID: {}", imdbId, e);
        }

        return streams;
    }

    /**
     * Select the best stream from available options.
     * Prioritizes: 1080p > 720p > any available
     */
    public TorrentStreamInfo selectBestStream(List<TorrentStreamInfo> streams) {
        if (streams == null || streams.isEmpty()) {
            return null;
        }

        // Try to find 1080p stream
        for (TorrentStreamInfo stream : streams) {
            if (stream.getTitle() != null && stream.getTitle().contains("1080p")) {
                log.info("Selected 1080p stream: {}", stream.getTitle());
                return stream;
            }
        }

        // Try to find 720p stream
        for (TorrentStreamInfo stream : streams) {
            if (stream.getTitle() != null && stream.getTitle().contains("720p")) {
                log.info("Selected 720p stream: {}", stream.getTitle());
                return stream;
            }
        }

        // Fall back to first available stream
        TorrentStreamInfo fallback = streams.get(0);
        log.info("Selected fallback stream: {}", fallback.getTitle());
        return fallback;
    }
}
