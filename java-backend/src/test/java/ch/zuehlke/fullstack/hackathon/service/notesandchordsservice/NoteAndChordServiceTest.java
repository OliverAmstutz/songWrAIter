package ch.zuehlke.fullstack.hackathon.service.notesandchordsservice;

import ch.zuehlke.fullstack.hackathon.model.PromptInputDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class NoteAndChordServiceTest {

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

        String promptFromInput = NoteAndChordService.createPromptFromInput(exampleDto);

        assertThat(promptFromInput).isEqualTo("This could Brush teeth Heavy Metal Guitar, Bass excited be the final prompt");
    }
}