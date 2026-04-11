package com.MoviesRecommender.streamingModule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to resolve movie titles to IMDB IDs using the TMDB API.
 * Two-step process:
 *   1. Search for movie by title -> get TMDB ID
 *   2. Get movie details by TMDB ID -> get IMDB ID
 * Results are cached to avoid redundant API calls.
 */
@Service
@Slf4j
public class TmdbService {

    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3";

    @Value("${tmdb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Cache: movie title -> IMDB ID
    private final Map<String, String> imdbIdCache = new ConcurrentHashMap<>();

    public TmdbService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Get the IMDB ID for a given movie title.
     * @param title The movie title to search for
     * @return The IMDB ID (e.g., "tt1375666") or null if not found
     */
    public String getImdbId(String title) {
        // Check cache first
        if (imdbIdCache.containsKey(title)) {
            log.info("TMDB cache hit for title: {}", title);
            return imdbIdCache.get(title);
        }

        try {
            // Step 1: Search for movie by title
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String searchUrl = TMDB_BASE_URL + "/search/movie?api_key=" + apiKey + "&query=" + encodedTitle;

            log.info("TMDB search request for title: {}", title);
            String searchResponse = restTemplate.getForObject(searchUrl, String.class);
            JsonNode searchJson = objectMapper.readTree(searchResponse);
            JsonNode results = searchJson.get("results");

            if (results == null || results.isEmpty()) {
                log.warn("No TMDB results found for title: {}", title);
                return null;
            }

            // Get the first result's TMDB ID
            long tmdbId = results.get(0).get("id").asLong();
            log.info("Found TMDB ID {} for title: {}", tmdbId, title);

            // Step 2: Get movie details to get IMDB ID
            String detailUrl = TMDB_BASE_URL + "/movie/" + tmdbId + "?api_key=" + apiKey;
            String detailResponse = restTemplate.getForObject(detailUrl, String.class);
            JsonNode detailJson = objectMapper.readTree(detailResponse);

            JsonNode imdbNode = detailJson.get("imdb_id");
            if (imdbNode == null || imdbNode.isNull()) {
                log.warn("No IMDB ID found for TMDB ID: {}", tmdbId);
                return null;
            }

            String imdbId = imdbNode.asText();
            log.info("Resolved IMDB ID: {} for title: {}", imdbId, title);

            // Cache the result
            imdbIdCache.put(title, imdbId);
            return imdbId;

        } catch (Exception e) {
            log.error("Error resolving IMDB ID for title: {}", title, e);
            return null;
        }
    }
}
