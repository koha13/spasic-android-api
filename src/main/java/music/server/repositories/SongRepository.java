package music.server.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import music.server.entities.Song;

public interface SongRepository extends JpaRepository<Song, Integer> {
    public Song findByName(String name);

    @Query("SELECT s FROM Song s WHERE LOWER(s.album) LIKE :album")
    public List<Song> findByAlbum(@Param("album") String album);

    @Query("SELECT s FROM Song s WHERE LOWER(s.artists) LIKE :artists")
    public List<Song> findByArtists(@Param("artists") String artists);

    @Query("SELECT s FROM Song s WHERE LOWER(s.name) LIKE CONCAT('%',:name,'%') ORDER BY s.id DESC")
    List<Song> findSongByNameLike(@Param("name") String key, Pageable pageable);

    @Query("SELECT s.album, MIN(s.artists), MIN(s.songImage) FROM Song s WHERE LOWER(s.album) LIKE CONCAT('%',:name,'%') GROUP BY album")
    List<Object[]> findSongByAlbumLike(@Param("name") String key, Pageable pageable);

    @Query("SELECT s.artists, MIN(s.songImage) FROM Song s WHERE LOWER(s.artists) LIKE CONCAT('%',:name,'%') GROUP BY artists")
    List<Object[]> findSongByArtistLike(@Param("name") String key, Pageable pageable);

    @Query("SELECT s FROM Song s ORDER BY s.play DESC")
    List<Song> findTop20ByPlay(Pageable pageable);

}