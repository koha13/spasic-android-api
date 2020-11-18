package music.server.entities;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collector {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  int id;
  @OneToOne
  @JoinColumn(name = "user_id")
  User user;
  @OneToOne
  @JoinColumn(name = "song_id")
  Song song;
  int point;

  public Collector(User user, Song song, int point) {
    this.user = user;
    this.song = song;
    this.point = point;
  }

}
