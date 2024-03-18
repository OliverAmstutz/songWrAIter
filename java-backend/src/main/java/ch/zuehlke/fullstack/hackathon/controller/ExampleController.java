package ch.zuehlke.fullstack.hackathon.controller;

import ch.zuehlke.fullstack.hackathon.model.PromptInputDto;
import ch.zuehlke.fullstack.hackathon.service.notesandchordsservice.SongAndChordService;
import ch.zuehlke.fullstack.hackathon.service.notesandchordsservice.SongtextAndChordsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExampleController {

    private final SongAndChordService songAndChordService;

    @Operation(summary = "Get OpenApi answer",
            description = "tbd")
    @ApiResponse(responseCode = "201", description = "Successfully returned example")
    @ApiResponse(responseCode = "500", description = "Something failed internally")
    @PostMapping("/song")
    public ResponseEntity<SongtextAndChordsDto> getMessageOfTheDayExample(@RequestBody final PromptInputDto promptInputDto) {

        try {
            var response = songAndChordService.generateNotesAndChordsFromInput(promptInputDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception exception) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
