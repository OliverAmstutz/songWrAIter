package ch.zuehlke.fullstack.hackathon.controller;

import ch.zuehlke.fullstack.hackathon.model.CreateSongDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/song")
@RequiredArgsConstructor
public class SongController {

    @Operation(summary = "Create a new Song",
            description = "This endpoint can be used to create a new song")
    @ApiResponse(responseCode = "200", description = "Successfully triggered song creation")
    @ApiResponse(responseCode = "500", description = "Something failed internally")
    @PostMapping
    public ResponseEntity<CreateSongDto> createSong(@RequestBody CreateSongDto createSongDto) {
        return new ResponseEntity<>(createSongDto, HttpStatus.OK);
    }
}
