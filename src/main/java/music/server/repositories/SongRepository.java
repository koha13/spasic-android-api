package music.server.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import music.server.entities.Song;
import music.server.models.AlbumModel;

public interface SongRepository extends JpaRepository<Song, Integer> {
    public Song findByName(String name);

    public List<Song> findByAlbum(String album);

    public List<Song> findByArtists(String artists);

    @Query("SELECT s FROM Song s WHERE s.name LIKE CONCAT('%',:name,'%') ORDER BY s.id DESC")
    List<Song> findSongByNameLike(@Param("name") String key, Pageable pageable);

    @Query("SELECT s FROM Song s WHERE s.album LIKE CONCAT('%',:name,'%') GROUP BY album")
    List<Song> findSongByAlbumLike(@Param("name") String key, Pageable pageable);

    @Query("SELECT s FROM Song s WHERE s.artists LIKE CONCAT('%',:name,'%') GROUP BY artists")
    List<Song> findSongByArtistLike(@Param("name") String key, Pageable pageable);

}