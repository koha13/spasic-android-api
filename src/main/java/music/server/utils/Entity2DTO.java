package music.server.utils;

import music.server.entities.Song;

import java.util.ArrayList;
import java.util.List;

import music.server.entities.Playlist;
import music.server.models.AlbumModel;
import music.server.models.ArtistModel;
import music.server.models.PlaylistAddToEndPoint;
import music.server.models.SongModel;

public class Entity2DTO {

    public static SongModel toSongModel2(Song song, boolean isLike) {
        return new SongModel(song.getId(), song.getName(), song.getLength(), song.getSongImage(), song.getArtists(),
                song.getLyric(), song.getLink(), song.getAlbum(), isLike);
    }

    public static SongModel toSongModel2(Song song, boolean isLike, boolean isLyric) {
        return new SongModel(song.getId(), song.getName(), song.getLength(), song.getSongImage(), song.getArtists(),
                song.getLyric(), song.getLink(), song.getAlbum(), isLike);
    }

    public static SongModel toSongModel(Song song, List<Song> likedSong) {
        boolean isLike = false;
        for (int j = 0; j < likedSong.size(); j++) {
            if (likedSong.get(j).getId() == song.getId()) {
                isLike = true;
                break;
            }
        }
        return toSongModel2(song, isLike);
    }

    public static SongModel toSongModelWithLyric(Song song, List<Song> likedSong) {
        boolean isLike = false;
        for (int j = 0; j < likedSong.size(); j++) {
            if (likedSong.get(j).getId() == song.getId()) {
                isLike = true;
                break;
            }
        }
        return toSongModel2(song, isLike, true);
    }

    public static List<SongModel> toSongModelList(List<Song> songs, List<Song> likedSong) {
        List<SongModel> result = new ArrayList<>();
        for (int i = 0; i < songs.size(); i++) {
            boolean isLike = false;
            for (int j = 0; j < likedSong.size(); j++) {
                if (likedSong.get(j).getId() == songs.get(i).getId()) {
                    isLike = true;
                    break;
                }
            }
            result.add(Entity2DTO.toSongModel2(songs.get(i), isLike));
        }
        return result;
    }

    public static PlaylistAddToEndPoint toPLEndPoint(Playlist playlist, boolean check) {
        return new PlaylistAddToEndPoint(playlist.getId(), playlist.getName(), check);
    }

    public static List<AlbumModel> songsToAlbumModels(List<Object[]> songs) {
        List<AlbumModel> albumModels = new ArrayList<>();
        for (Object[] s : songs) {
            albumModels.add(new AlbumModel(s[0].toString(), s[1].toString(), s[2].toString()));
        }
        return albumModels;
    }

    public static List<ArtistModel> songsToArtistModels(List<Object[]> songs) {
        List<ArtistModel> artistModel = new ArrayList<>();
        for (Object[] s : songs) {
            artistModel.add(new ArtistModel(s[0].toString(), s[1].toString()));
        }
        return artistModel;
    }
}