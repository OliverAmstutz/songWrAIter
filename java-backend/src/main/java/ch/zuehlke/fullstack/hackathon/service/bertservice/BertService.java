package ch.zuehlke.fullstack.hackathon.service.bertservice;


import ch.zuehlke.fullstack.hackathon.model.BertPromptDto;
import ch.zuehlke.fullstack.hackathon.model.BertPromptInput;
import ch.zuehlke.fullstack.hackathon.service.notesandchordsservice.SongtextAndChordsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
public class BertService {
    @Value("${replicateApiKey}")
    private String apiKey;

    public String generateSongFromChords(final SongtextAndChordsDto songtextAndChords) throws InterruptedException {
        //build input from songtextandChords
        String chords = String.join("|", songtextAndChords.chorusChords()); //TODO: extend with verses
        String notes = String.join("|", Collections.nCopies(songtextAndChords.chorusChords().size(), "?"));
        int tempo = 120;
        int seed = -1;
        int sample_width = 80;
        int time_signature = 4;

        BertPromptDto input = new BertPromptDto(chords, notes, tempo, seed, sample_width, time_signature);
        BertPromptInput bertPromptInput = new BertPromptInput("58bdc2073c9c07abcc4200fe808e15b1a555dbb1390e70f5daa6b3d81bd11fb1", input);

        //call bert
        Map result = WebClient.create("https://api.replicate.com/v1/predictions")
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Token %s".formatted(apiKey))
                .bodyValue(bertPromptInput)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        log.info(result.toString());

        //Map zurück auf Song
        //download songs from bert urls

        return (String) result.get("id");
    }

    private Map pollResult(Map result) {
        return WebClient.create(((Map) result.get("urls")).get("get").toString())
                .get()
                .header("Authorization", "Token %s".formatted(apiKey))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}