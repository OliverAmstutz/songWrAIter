package ch.zuehlke.fullstack.hackathon.controller;

import ch.zuehlke.fullstack.hackathon.model.Genre;
import ch.zuehlke.fullstack.hackathon.model.PromptInputDto;
import ch.zuehlke.fullstack.hackathon.model.Song;
import ch.zuehlke.fullstack.hackathon.model.musicgen.CreateSongDto;
import ch.zuehlke.fullstack.hackathon.model.musicgen.MusicgenSong;
import ch.zuehlke.fullstack.hackathon.service.MusicgenSongCache;
import ch.zuehlke.fullstack.hackathon.service.SongCache;
import ch.zuehlke.fullstack.hackathon.service.bertservice.BertService;
import ch.zuehlke.fullstack.hackathon.service.musicgenservice.MusicgenService;
import ch.zuehlke.fullstack.hackathon.service.notesandchordsservice.SongAndChordService;
import ch.zuehlke.fullstack.hackathon.service.notesandchordsservice.SongtextAndChordsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/song")
@Slf4j
public class SongController {

    private final SongAndChordService service;

    private final BertService bertService;

    private final MusicgenService musicgenService;

    private final SongCache songCache;

    private final MusicgenSongCache musicgenSongCache;

    public SongController(SongAndChordService service, BertService bertService,
                          MusicgenService musicgenService,
                          SongCache songCache, MusicgenSongCache musicgenSongCache) {
        this.service = service;
        this.bertService = bertService;
        this.musicgenService = musicgenService;
        this.songCache = songCache;
        this.musicgenSongCache = musicgenSongCache;
    }

    @Operation(summary = "Create a new Song",
            description = "This endpoint can be used to create a new song")
    @ApiResponse(responseCode = "201", description = "Successfully triggered song creation")
    @ApiResponse(responseCode = "500", description = "Something failed internally")
    @PostMapping
    public ResponseEntity<Void> createSong(@RequestBody PromptInputDto createSongDto) {
        log.info("Starting song generation: {}", createSongDto);
        SongtextAndChordsDto songtextAndChordsDto = service.generateNotesAndChordsFromInput(
                createSongDto);
        log.info(
                "Chorus Song = {}, Chorus Chords = {}, Verse Song = {}, Verse Chords = {}",
                songtextAndChordsDto.chorusSongtext(),
                songtextAndChordsDto.chorusChords(),
                songtextAndChordsDto.verseSongtext(),
                songtextAndChordsDto.verseChords());
        var newlyCreatedSong = new Song(
                UUID.randomUUID(),
                createSongDto.topic(),
                Genre.mapGenre(createSongDto.genre()),
                createSongDto.instruments(),
                createSongDto.mood(),
                songtextAndChordsDto.verseSongtext(),
                songtextAndChordsDto.chorusSongtext());

        songCache.addNewSong(newlyCreatedSong);
        bertService.generateSongFromChords(songtextAndChordsDto, newlyCreatedSong);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Create a new musicgen Song",
            description = "This endpoint can be used to create a new musicgen song")
    @ApiResponse(responseCode = "201", description = "Successfully triggered song creation")
    @ApiResponse(responseCode = "500", description = "Something failed internally")
    @PostMapping("musicgen")
    public ResponseEntity<Void> createSongMusicGen(@RequestBody CreateSongDto songDto) {
        musicgenService.generateSong(songDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Get all songs",
            description = "This endpoint can be used to get all songs")
    @ApiResponse(responseCode = "200", description = "Successfully queries all songs")
    @ApiResponse(responseCode = "500", description = "Something failed internally")
    @GetMapping
    public ResponseEntity<List<Song>> getSongs() {
        List<Song> songs = songCache.getAllSongs();
        return new ResponseEntity<>(songs, HttpStatus.OK);
    }

    @Operation(summary = "Get all musicgen songs",
            description = "This endpoint can be used to get all musicgen songs")
    @ApiResponse(responseCode = "200", description = "Successfully queries all musicgen songs")
    @ApiResponse(responseCode = "500", description = "Something failed internally")
    @GetMapping("musicgen")
    public ResponseEntity<List<MusicgenSong>> getMusicgenSongs() {
        List<MusicgenSong> songs = musicgenSongCache.getAllSongs();
        return new ResponseEntity<>(songs, HttpStatus.OK);
    }
}