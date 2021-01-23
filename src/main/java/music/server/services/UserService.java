package music.server.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import music.server.config.CustomUserDetails;
import music.server.entities.Collector;
import music.server.entities.Song;
import music.server.entities.User;
import music.server.exceptionhandle.ApiMissingException;
import music.server.exceptionhandle.UsernameIsAlreadyTakenException;
import music.server.models.ChangePasswordRequest;
import music.server.models.LoginResponse;
import music.server.models.SignupRequest;
import music.server.models.SuggestRequest;
import music.server.repositories.CollectorRepository;
import music.server.repositories.SongRepository;
import music.server.repositories.UserRepository;

@Service
public class UserService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  SongRepository songRepository;

  @Autowired
  CollectorRepository colRepository;

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private SongService songService;

  public LoginResponse signup(SignupRequest signupRequest) throws Exception {
    User userAdmin = getCurrentUser();
    if (userAdmin.getRole().compareTo("admin") != 0)
      throw new Exception("Only admin can add use");
    User userCheck = userRepository.findByUsername(signupRequest.getUsername());
    if (userCheck != null) {
      throw new UsernameIsAlreadyTakenException();
    }
    String username = signupRequest.getUsername();
    String password = signupRequest.getPassword();
    String passwordEncode = passwordEncoder.encode(password);
    String role = signupRequest.getRole();

    User user = new User(username, passwordEncode, role);
    userRepository.save(user);
    return getLoginResponse(username, password);
  }

  private User login(String username, String password) {
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(username, password));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    User user = getCurrentUser();
    return user;
  }

  public LoginResponse getLoginResponse(String username, String password) {
    User user = login(username, password);
    String jwt = jwtService.generateToken(new CustomUserDetails(user));
    LoginResponse loginResponse = new LoginResponse(user.getId(), user.getUsername(), jwt, user.getRole());
    return loginResponse;
  }

  public LoginResponse checkToken(String token) throws JsonParseException, JsonMappingException, IOException {
    User user = jwtService.getUserFromToken(token);
    if (user != null) {
      String jwt = jwtService.generateToken(new CustomUserDetails(user));
      LoginResponse loginResponse = new LoginResponse(user.getId(), user.getUsername(), jwt, user.getRole());
      return loginResponse;
    } else
      throw new RuntimeException("Token is invalid!");
  }

  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof AnonymousAuthenticationToken) {
      throw new ApiMissingException();
    }
    CustomUserDetails cusUser = (CustomUserDetails) authentication.getPrincipal();
    return cusUser.getUser();
  }

  public User getUserRepo() {
    int id = getCurrentUser().getId();
    return userRepository.findById(id).get();
  }

  public LoginResponse changePass(ChangePasswordRequest c) {
    User user = getCurrentUser();
    if (passwordEncoder.matches(c.getOldPass(), user.getPassword())) {
      user.setPassword(passwordEncoder.encode(c.getNewPass()));
      userRepository.save(user);
      String jwt = jwtService.generateToken(new CustomUserDetails(user));
      LoginResponse loginResponse = new LoginResponse(user.getId(), user.getUsername(), jwt, user.getRole());
      return loginResponse;
    } else
      throw new RuntimeException("Old password not match");
  }

  public void likeSong(int idSong) {
    User user = getCurrentUser();
    User userRepo = userRepository.findById(user.getId()).get();
    Song song = songService.getSongById(idSong);
    userRepo.likeSong(song);
    userRepository.save(userRepo);
    songService.updateCollector(song, userRepo, 3);
  }

  public void unlikeSong(int idSong) {
    User user = getCurrentUser();
    User userRepo = userRepository.findById(user.getId()).get();
    Song song = songService.getSongById(idSong);
    userRepo.unlikeSong(song);
    userRepository.save(userRepo);
    songService.updateCollector(song, userRepo, 50);
  }

  public void updateSuggest(List<SuggestRequest> sg) {
    for (int i = 0; i < sg.size(); i++) {
      User u = userRepository.findById(Integer.parseInt(sg.get(i).getUserId())).get();
      String songSg = sg.get(i).getSongId();
      String[] path = songSg.split(",");
      u.setSuggestSongs(new ArrayList<Song>());
      for (int j = 0; j < path.length; j++) {
        Song s = songRepository.findById(Integer.parseInt(path[j])).get();
        if (s != null) {
          u.getSuggestSongs().add(s);
        }
      }
      Pageable pageable = PageRequest.of(0, 50);
      List<Song> ss2 = songRepository.findAll(pageable).getContent();
      List<Song> ss = new ArrayList<>(ss2);
      Collections.shuffle(ss);
      for (int j = 0; j < ss.size(); j++) {
        if (!u.getSuggestSongs().contains(ss.get(j))) {
          u.getSuggestSongs().add(ss.get(j));
        }
      }
      userRepository.save(u);
    }
  }

  public List<String> getAllRating() {
    List<String> rs = new ArrayList<>();
    List<Collector> cols = colRepository.findAll();
    for (int i = 0; i < cols.size(); i++) {
      String r = "" + cols.get(i).getUser().getId() + " " + cols.get(i).getSong().getId() + " "
          + cols.get(i).getPoint();
      rs.add(r);
    }
    return rs;
  }

  // public void getSong(int page, int size) {
  // User user = getUserRepo();
  // List<Song> songs = user.getSuggestSongs().subList(page * size, page * size +
  // size);
  // }
}