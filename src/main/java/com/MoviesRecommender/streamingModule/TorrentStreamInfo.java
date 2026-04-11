package com.MoviesRecommender.streamingModule;

import lombok.*;
import java.util.List;

/**
 * DTO for Torrentio Stremio addon API response.
 * Represents a single torrent stream option for a movie.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TorrentStreamInfo {
    private String infoHash;
    private String title;
    private Integer fileIdx;
    private List<String> sources; // tracker URLs

    /**
     * Build a magnet URI from the infoHash and tracker sources.
     */
    public String toMagnetUri() {
        StringBuilder sb = new StringBuilder("magnet:?xt=urn:btih:");
        sb.append(infoHash);
        if (sources != null) {
            for (String tracker : sources) {
                if (tracker.startsWith("tracker:")) {
                    sb.append("&tr=").append(tracker.substring(8));
                }
            }
        }
        return sb.toString();
    }
}
