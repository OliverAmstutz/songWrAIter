package ch.zuehlke.fullstack.hackathon.service.bertservice;


import ch.zuehlke.fullstack.hackathon.model.BertPromptDto;
import ch.zuehlke.fullstack.hackathon.model.BertPromptInput;
import ch.zuehlke.fullstack.hackathon.model.Song;
import ch.zuehlke.fullstack.hackathon.model.SongUrls;
import ch.zuehlke.fullstack.hackathon.service.SongCache;
import ch.zuehlke.fullstack.hackathon.service.notesandchordsservice.SongtextAndChordsDto;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
        String chords = buildChords(songtextAndChords);
        int chordsCount = countOccurrences(chords);

        String notes = String.join("|", Collections.nCopies(chordsCount+1, "?"));
        int tempo = song.genre().tempo;
        int seed = -1;
        int sampleWidth = 10; //Number of potential predictions to sample from. The higher, the more chaotic the output. Default is 10
        int timeSignature = song.genre().timeSignature;

        BertPromptDto input = new BertPromptDto(chords, notes, tempo, seed, sampleWidth, timeSignature);
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

        String id = (String) result.get("id");

        songCache.updateSong(new Song(song, id));

        return id;
    }

    private int countOccurrences(String chords) {
        int count = 0;
        for (int i = 0; i < chords.length(); i++) {
            if (chords.charAt(i) == '|') {
                count++;
            }
        }
        return count;
    }

    @NotNull
    private static String buildChords(SongtextAndChordsDto songtextAndChords) {
        String chorusChords = String.join("|", songtextAndChords.chorusChords());
        String verseChords = String.join("|", songtextAndChords.verseChords());
        return verseChords+"|"+chorusChords + "|" + verseChords+"|"+chorusChords;
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