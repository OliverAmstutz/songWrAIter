package ch.zuehlke.fullstack.hackathon.controller;

import ch.zuehlke.fullstack.hackathon.model.PromptInputDto;
import ch.zuehlke.fullstack.hackathon.model.Song;
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

@RestController
@RequestMapping("/api/song")
@Slf4j
public class SongController {

    private final SongAndChordService service;

    public SongController(SongAndChordService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new Song",
            description = "This endpoint can be used to create a new song")
    @ApiResponse(responseCode = "201", description = "Successfully triggered song creation")
    @ApiResponse(responseCode = "500", description = "Something failed internally")
    @PostMapping
    public ResponseEntity<Void> createSong(@RequestBody PromptInputDto createSongDto) {
        SongtextAndChordsDto songtextAndChordsDto = service.generateNotesAndChordsFromInput(createSongDto);
        log.info("Chorus Song = {}, Chorus Chords = {}, Verse Song = {}, Verse Chords = {}", songtextAndChordsDto.chorusSongtext(), songtextAndChordsDto.chorusChords(), songtextAndChordsDto.verseSongtext(), songtextAndChordsDto.verseChords());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Get all songs",
            description = "This endpoint can be used to get all songs")
    @ApiResponse(responseCode = "200", description = "Successfully queries all songs")
    @ApiResponse(responseCode = "500", description = "Something failed internally")
    @GetMapping
    public ResponseEntity<List<Song>> getSongs() {
        List<Song> songs = service.getAllSongs();
        return new ResponseEntity<>(songs, HttpStatus.OK);
    }
}