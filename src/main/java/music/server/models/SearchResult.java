package music.server.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private List<SongModel> songs;
    private List<AlbumModel> albums;
    private List<ArtistModel> artists;

}