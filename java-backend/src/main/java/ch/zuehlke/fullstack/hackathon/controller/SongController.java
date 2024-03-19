package ch.zuehlke.fullstack.hackathon.controller;

import ch.zuehlke.fullstack.hackathon.model.PromptInputDto;
import ch.zuehlke.fullstack.hackathon.model.Song;
import ch.zuehlke.fullstack.hackathon.service.SongCache;
import ch.zuehlke.fullstack.hackathon.service.bertservice.BertService;
import ch.zuehlke.fullstack.hackathon.service.notesandchordsservice.SongAndChordService;
import ch.zuehlke.fullstack.hackathon.service.notesandchordsservice.SongtextAndChordsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/song")
@Slf4j
public class SongController {

    private final SongAndChordService service;
    private final BertService bertService;

    private final SongCache songCache;

    public SongController(SongAndChordService service, BertService bertService, SongCache songCache) {
        this.service = service;
        this.bertService = bertService;
        this.songCache = songCache;
    }

    @Operation(summary = "Create a new Song",
            description = "This endpoint can be used to create a new song")
    @ApiResponse(responseCode = "201", description = "Successfully triggered song creation")
    @ApiResponse(responseCode = "500", description = "Something failed internally")
    @PostMapping
    public ResponseEntity<Void> createSong(@RequestBody PromptInputDto createSongDto) {
        log.info("Starting song generation: {}", createSongDto);
        SongtextAndChordsDto songtextAndChordsDto = service.generateNotesAndChordsFromInput(createSongDto);
        log.info("Chorus Song = {}, Chorus Chords = {}, Verse Song = {}, Verse Chords = {}", songtextAndChordsDto.chorusSongtext(), songtextAndChordsDto.chorusChords(), songtextAndChordsDto.verseSongtext(), songtextAndChordsDto.verseChords());
        var song = new Song(UUID.randomUUID(), createSongDto.topic(), createSongDto.genre(), createSongDto.instruments(), createSongDto.mood(), songtextAndChordsDto.verseSongtext(), songtextAndChordsDto.chorusSongtext());

        songCache.addNewSong(song);
        var bertId = bertService.generateSongFromChords(songtextAndChordsDto, song);

        songCache.updateSong(new Song(song, bertId));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @NotNull
    private static Song mapSong(final PromptInputDto createSongDto, final String randomBirdId, final SongtextAndChordsDto songtextAndChordsDto) {
        return new Song(UUID.randomUUID(), createSongDto.topic(), createSongDto.genre(), createSongDto.instruments(), createSongDto.mood(), randomBirdId, songtextAndChordsDto.verseSongtext(), songtextAndChordsDto.chorusSongtext(), null);
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
}