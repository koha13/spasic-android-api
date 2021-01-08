package music.server.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import music.server.entities.Playlist;
import music.server.entities.Song;
import music.server.entities.User;
import music.server.models.PlaylistModel;
import music.server.models.PlaylistAddToEndPoint;
import music.server.repositories.PlaylistRepository;
import music.server.repositories.SongRepository;
import music.server.utils.Entity2DTO;

@Service
public class PlaylistService {
    @Autowired
    private PlaylistRepository plRepository;
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private SongService songService;

    public PlaylistModel addPL(String name) {
        if (plRepository.findByName(name) != null) {
            throw new RuntimeException("Playlist's name is already taken");
        }
        User user = userService.getCurrentUser();
        Playlist playlist = new Playlist(name, "", user);
        plRepository.save(playlist);
        User userRepo = userService.getUserRepo();
        List<Song> likedSong = userRepo.getLikedSong();
        PlaylistModel result = new PlaylistModel(playlist.getId(), playlist.getName(), playlist.getAvatar(),
                Entity2DTO.toSongModelList(playlist.getSongs(), likedSong));
        return result;
    }

    public List<PlaylistModel> getAllPlayList() {
        User user = userService.getUserRepo();
        List<Song> likedSong = user.getLikedSong();
        List<Playlist> playlists = plRepository.findByUserId(user.getId());
        List<PlaylistModel> result = new ArrayList<>();
        result.add(new PlaylistModel(-1, "Loved", null, Entity2DTO.toSongModelList(likedSong, likedSong)));
        for (int i = 0; i < playlists.size(); i++) {
            result.add(new PlaylistModel(playlists.get(i).getId(), playlists.get(i).getName(),
                    playlists.get(i).getAvatar(), Entity2DTO.toSongModelList(playlists.get(i).getSongs(), likedSong)));
        }
        return result;
    }

    public PlaylistModel getPlayListById(int id) {
        User user = userService.getCurrentUser();
        Playlist playlist = plRepository.findByIdAndUserId(id, user.getId());
        User userRepo = userService.getUserRepo();
        List<Song> likedSong = userRepo.getLikedSong();
        PlaylistModel result = new PlaylistModel(playlist.getId(), playlist.getName(), playlist.getAvatar(),
                Entity2DTO.toSongModelList(playlist.getSongs(), likedSong));
        return result;
    }

    public void deletePlayListById(int id) {
        User user = userService.getCurrentUser();
        System.out.println(user.getId());
        Playlist pl = plRepository.findByIdAndUserId(id, user.getId());
        plRepository.delete(pl);
    }

    public void addSong(int id, int idSong) {
        if (id == -1) {
            userService.likeSong(idSong);
        } else {
            Song song = songRepository.findById(idSong).get();
            Playlist pl = plRepository.findById(id).get();
            if (pl.addSong(song)) {
              User user = userService.getUserRepo();
              songService.updateCollector(song, user, 30);
              plRepository.save(pl);
            }
        }
    }

    public void deleteSong(int id, int idSong) {
        if (id == -1) {
            userService.unlikeSong(idSong);
        } else {
            Song song = songRepository.findById(idSong).get();
            Playlist pl = plRepository.findById(id).get();
            if (pl.deleteSong(song)) {
              User user = userService.getUserRepo();
              songService.updateCollector(song, user, -30);
                plRepository.save(pl);
            }
        }
    }

    public List<PlaylistAddToEndPoint> checkSong(int idSong) {
        User user = userService.getCurrentUser();
        List<Playlist> playlists = plRepository.findByUserId(user.getId());
        List<PlaylistAddToEndPoint> result = new ArrayList<>();
        for (int i = 0; i < playlists.size(); i++) {
            int check = 0;
            for (int j = 0; j < playlists.get(i).getSongs().size(); j++) {
                if (idSong == playlists.get(i).getSongs().get(j).getId()) {
                    result.add(Entity2DTO.toPLEndPoint(playlists.get(i), true));
                    check = 1;
                    break;
                }
            }
            if (check == 0)
                result.add(Entity2DTO.toPLEndPoint(playlists.get(i), false));
        }
        return result;
    }

}