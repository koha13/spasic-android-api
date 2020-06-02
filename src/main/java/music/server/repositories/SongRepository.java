package music.server.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import music.server.entities.Song;

public interface SongRepository extends JpaRepository<Song, Integer> {
    public Song findByName(String name);

    public List<Song> findByAlbum(String album);

    public List<Song> findByArtists(String artists);

    @Query("SELECT s FROM Song s WHERE s.name LIKE CONCAT('%',:name,'%') ORDER BY s.id DESC")
    List<Song> findSongByNameLike(@Param("name") String key, Pageable pageable);

    @Query("SELECT s.album, MIN(s.artists), MIN(s.songImage) FROM Song s WHERE s.album LIKE CONCAT('%',:name,'%') GROUP BY album")
    List<Object[]> findSongByAlbumLike(@Param("name") String key, Pageable pageable);

    @Query("SELECT s.artists, MIN(s.songImage) FROM Song s WHERE s.artists LIKE CONCAT('%',:name,'%') GROUP BY artists")
    List<Object[]> findSongByArtistLike(@Param("name") String key, Pageable pageable);

}