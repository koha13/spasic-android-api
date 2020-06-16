package music.server.controllers;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import music.server.models.SongUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import music.server.models.AlbumModel;
import music.server.models.ArtistModel;
import music.server.models.SongModel;
import music.server.services.SongService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class SongController {

    @Autowired
    private SongService songService;

    @GetMapping("/song/{fileName}")
    public ResponseEntity<Resource> getSongById(final HttpServletResponse response,
            @PathVariable final String fileName) {
        return songService.serveSong(response, fileName);
    }

    @GetMapping("/songinfo/{id}")
    public SongModel getSongInfo(@PathVariable final Integer id) {
        return songService.getSongInfo(id);
    }

    @GetMapping("/scan")
    public String scan() throws Exception {
        final int count = songService.scanAllSong();
        return "Got " + count + " songs.";
    }

    @GetMapping("/songs")
    public List<SongModel> getAllSongs() {
        return songService.getAllSongs();
    }

    // Paging all songs
    @GetMapping("/allsongs")
    public List<SongModel> getSongsByPage(
            @RequestParam(name = "page", required = false, defaultValue = "0") final Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") final Integer size) {
        return songService.getSongsByPage(page, size);
    }

    @PostMapping("/song/upload")
    public SongModel uploadSong(@RequestParam("file") final MultipartFile file) {
        return songService.uploadSong(file);
    }

    @PostMapping("/song/update/{id}")
    public String updateSongInfo(@PathVariable final String id, @RequestBody final SongUpdateRequest req)
            throws Exception {
        if (songService.updateSongInfo(Integer.parseInt(id), req))
            return "OK";
        throw new Exception("Can not update this song");
    }

    @PostMapping("/song/delete/{id}")
    public void deleteSong(@PathVariable final String id) throws Exception {
        songService.deleteSong(Integer.parseInt(id));
    }

    @GetMapping("/searchsong")
    public List<SongModel> searchSong(@RequestParam final String key,
            @RequestParam(name = "page", required = false, defaultValue = "0") final Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") final Integer size) {
        return songService.searchSong(key, page, size);
    }

    @GetMapping("/searchalbum")
    public List<AlbumModel> searchAlbum(@RequestParam final String key,
            @RequestParam(name = "page", required = false, defaultValue = "0") final Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") final Integer size) {
        return songService.searchAlbum(key, page, size);
    }

    @GetMapping("/searchartist")
    public List<ArtistModel> searchArtist(@RequestParam final String key,
            @RequestParam(name = "page", required = false, defaultValue = "0") final Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") final Integer size) {
        return songService.searchArtist(key, page, size);
    }

    @GetMapping(value = "/album")
    public List<SongModel> findByAlbum(@RequestParam String key) {
        return songService.findByAlbum(key);
    }

    @GetMapping(value = "/artist")
    public List<SongModel> findByArtists(@RequestParam String key) {
        return songService.findByArtists(key);
    }

    @PostMapping(value = "/upPlay/{songId}")
    public void upPlaySongById(@PathVariable int songId){
        songService.upPlaySongById(songId);
    }

    @GetMapping(value = "/rank")
    public List<SongModel> getRank(){
        return songService.getSongRank();
    }
}