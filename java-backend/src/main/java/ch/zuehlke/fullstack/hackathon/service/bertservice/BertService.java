package ch.zuehlke.fullstack.hackathon.service.bertservice;

import ch.zuehlke.fullstack.hackathon.model.BertPromptDto;
import ch.zuehlke.fullstack.hackathon.model.Song;
import ch.zuehlke.fullstack.hackathon.model.SongUrls;
import ch.zuehlke.fullstack.hackathon.service.SongCache;
import ch.zuehlke.fullstack.hackathon.service.notesandchordsservice.SongtextAndChordsDto;
import ch.zuehlke.fullstack.hackathon.service.replicate.ReplicateApi;
import ch.zuehlke.fullstack.hackathon.service.replicate.ReplicateResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@Slf4j
public class BertService {
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    private final Map<String, Future<?>> scheduledJobs = new ConcurrentHashMap<>();

    private final SongCache songCache;

    private final ReplicateApi replicateApi;

    public BertService(SongCache songCache, ReplicateApi replicateApi) {
        this.songCache = songCache;
        this.replicateApi = replicateApi;
    }

    public String generateSongFromChords(final SongtextAndChordsDto songtextAndChords, final Song song) {
        String chords = buildChords(songtextAndChords);
        int chordsCount = countOccurrences(chords);

        String notes = String.join("|", Collections.nCopies(chordsCount + 1, "?"));
        var input = bertPromptDto(song, chords, notes);

        var result = replicateApi.createBertJob(input);

        log.info("Created bert job: {}", result);

        var id = (String) result.id();
        songCache.updateSong(song.bertId(id));

        pollResult(song.id(), result);

        return id;
    }

    @NotNull
    private static BertPromptDto bertPromptDto(Song song, String chords, String notes) {
        int tempo = song.genre().tempo;
        int seed = -1;
        int sampleWidth = 3; //Number of potential predictions to sample from. The higher, the more chaotic the output. Default is 10
        int timeSignature = song.genre().timeSignature;

        return new BertPromptDto(chords, notes, tempo, seed, sampleWidth, timeSignature);
    }

    private void pollResult(UUID songId, ReplicateResult<Map<String, String>> result) {
        String jobUrl = result.getJobUrl();
        ScheduledFuture<?> job = executor.scheduleWithFixedDelay(() -> {
            var pollResult = replicateApi.pollBertResult(jobUrl);

            String status = pollResult.status();

            log.info("Current status: {}", status);

            if (pollResult.isDone()) {
                scheduledJobs.get(jobUrl).cancel(true);
                scheduledJobs.remove(jobUrl);
                log.info("Cancelling job: {}", jobUrl);
            }

            if (pollResult.isSucceeded()) {
                Map<String, String> urls = pollResult.output();

                var song = songCache.getById(songId);
                Song updatedSong = song.bertUrls(new SongUrls(urls.get("mp3"), urls.get("score"), urls.get("midi")));
                songCache.updateSong(updatedSong);
                log.info("Completed bert job: {}", updatedSong);
            }
        }, 3, 2, TimeUnit.SECONDS);
        scheduledJobs.put(jobUrl, job);
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
        return verseChords + "|" + chorusChords + "|" + verseChords + "|" + chorusChords;
    }
}