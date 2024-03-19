package ch.zuehlke.fullstack.hackathon.service.bertservice;


import ch.zuehlke.fullstack.hackathon.model.BertPromptDto;
import ch.zuehlke.fullstack.hackathon.model.BertPromptInput;
import ch.zuehlke.fullstack.hackathon.model.Song;
import ch.zuehlke.fullstack.hackathon.model.SongUrls;
import ch.zuehlke.fullstack.hackathon.service.SongCache;
import ch.zuehlke.fullstack.hackathon.service.notesandchordsservice.SongtextAndChordsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;

@Service
@Slf4j
public class BertService {
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    private final Map<String, Future<?>> scheduledJobs = new ConcurrentHashMap<>();

    private final String apiKey;

    private final SongCache songCache;

    public BertService(@Value("${replicateApiKey}") String apiKey, SongCache songCache) {
        this.apiKey = apiKey;
        this.songCache = songCache;
    }

    public String generateSongFromChords(final SongtextAndChordsDto songtextAndChords, final Song song) {
        String chords = String.join("|", songtextAndChords.chorusChords()); //TODO: extend with verses
        String notes = String.join("|", Collections.nCopies(songtextAndChords.chorusChords().size(), "?"));
        int tempo = 120;
        int seed = -1;
        int sample_width = 80;
        int time_signature = 4;

        BertPromptDto input = new BertPromptDto(chords, notes, tempo, seed, sample_width, time_signature);
        BertPromptInput bertPromptInput = new BertPromptInput("58bdc2073c9c07abcc4200fe808e15b1a555dbb1390e70f5daa6b3d81bd11fb1", input);

        Map result = WebClient.create("https://api.replicate.com/v1/predictions")
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Token %s".formatted(apiKey))
                .bodyValue(bertPromptInput)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        log.info("Created bert job: {}", result);

        String jobUrl = ((Map) result.get("urls")).get("get").toString();
        ScheduledFuture<?> job = executor.scheduleWithFixedDelay(() -> {
            Map map = pollResult(jobUrl);

            String status = (String) map.get("status");

            log.info("Current status: {}", status);

            if (!status.equals("starting") && !status.equals("processing")) {
                scheduledJobs.get(jobUrl).cancel(true);
                scheduledJobs.remove(jobUrl);
                log.info("Cancelling job: {}", jobUrl);
            }
            if (status.equals("succeeded")) {
                Map<String, String> urls = (Map<String, String>) map.get("output");

                Song updatedSong = new Song(song, new SongUrls(urls.get("mp3"), urls.get("score"), urls.get("midi")));
                songCache.updateSong(updatedSong);
                log.info("Completed bert job: {}", updatedSong);
            }
        }, 3, 2, TimeUnit.SECONDS);
        scheduledJobs.put(jobUrl, job);

        return (String) result.get("id");
    }

    private Map pollResult(String url) {
        return WebClient.create(url)
                .get()
                .header("Authorization", "Token %s".formatted(apiKey))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}