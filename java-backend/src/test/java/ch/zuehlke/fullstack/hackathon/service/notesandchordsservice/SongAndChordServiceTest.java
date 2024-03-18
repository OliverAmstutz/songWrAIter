package ch.zuehlke.fullstack.hackathon.service.notesandchordsservice;

import ch.zuehlke.fullstack.hackathon.model.PromptInputDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class SongAndChordServiceTest {

    @Autowired
    private SongAndChordService service;

        @Test
    void createPromptFromInput_correctInput() {
        PromptInputDto exampleDto = PromptInputDto.builder()
                .topic("Brush teeth")
                .genre("Heavy Metal")
                .instruments(
                        List.of("Guitar", "Bass")
                )
                .mood("excited")
                .build();

        String promptFromInput = SongAndChordService.createPromptFromInput(exampleDto);

        assertThat(promptFromInput).isEqualTo("This could Brush teeth Heavy Metal Guitar, Bass excited be the final prompt");
    }

    @Test
    void generateNotesAndChordsFromInput_happyCase() {
        PromptInputDto exampleDto = PromptInputDto.builder()
                .topic("Brush teeth")
                .genre("Heavy Metal")
                .instruments(
                        List.of("Guitar", "Bass")
                )
                .mood("excited")
                .build();

        var result = service.generateNotesAndChordsFromInput(exampleDto);
        System.out.println(result);

    }
}