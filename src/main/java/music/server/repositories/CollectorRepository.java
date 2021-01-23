package music.server.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import music.server.entities.Collector;

public interface CollectorRepository extends JpaRepository<Collector, Integer> {
  @Query(value = "SELECT * FROM Collector WHERE user_id = :userId AND song_id = :songId", nativeQuery = true)
  public Collector findByUserIdAndSong(int userId, int songId);

  public List<Collector> findByUserId(int id, Pageable pageable);
}
