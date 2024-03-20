package ch.zuehlke.fullstack.hackathon.service.bertservice;

import ch.zuehlke.fullstack.hackathon.model.MusicGenPromptDto;
import ch.zuehlke.fullstack.hackathon.model.Song;
import ch.zuehlke.fullstack.hackathon.model.SongUrls;
import ch.zuehlke.fullstack.hackathon.service.SongCache;
import ch.zuehlke.fullstack.hackathon.service.notesandchordsservice.SongtextAndChordsDto;
import ch.zuehlke.fullstack.hackathon.service.replicate.ReplicateApi;
import ch.zuehlke.fullstack.hackathon.service.replicate.ReplicateResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@Slf4j
public class MusicGenService {
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    private final Map<String, Future<?>> scheduledJobs = new ConcurrentHashMap<>();

    private final SongCache songCache;

    private final ReplicateApi replicateApi;

    public MusicGenService(SongCache songCache, ReplicateApi replicateApi) {
        this.songCache = songCache;
        this.replicateApi = replicateApi;
    }

    public String generateSongFromChords(final SongtextAndChordsDto songtextAndChords, final Song song) {
        String chords = buildChords(songtextAndChords);

        var input = new MusicGenPromptDto(
                "%s from the following chord progression: %s. It should be composed of the following instruments: %s. The melody should be '%s' and it should be of the genre '%s'"
                        .formatted(song.topic(), chords, song.instruments(), song.mood(), song.genre())
        );

        var result = replicateApi.createMusicGenJob(input);

        log.info("Created bert job: {}", result);

        var id = (String) result.id();
        songCache.updateSong(song.bertId(id));

        pollMusicGenResult(song.id(), result);

        return id;
    }

    private void pollMusicGenResult(UUID songId, ReplicateResult<String> result) {
        String jobUrl = result.getJobUrl();
        ScheduledFuture<?> job = executor.scheduleWithFixedDelay(() -> {
            var pollResult = replicateApi.pollMusicGenResult(jobUrl);

            String status = pollResult.status();

            log.info("Current status: {}", status);

            if (pollResult.isDone()) {
                scheduledJobs.get(jobUrl).cancel(true);
                scheduledJobs.remove(jobUrl);
                log.info("Cancelling job: {}", jobUrl);
            }

            if (pollResult.isSucceeded()) {
                String output = pollResult.output();

                var song = songCache.getById(songId);
                Song updatedSong = song.musicGenUrls(new SongUrls(output, null, null));
                songCache.updateSong(updatedSong);
                log.info("Completed bert job: {}", updatedSong);
            }
        }, 3, 2, TimeUnit.SECONDS);
        scheduledJobs.put(jobUrl, job);
    }

    @NotNull
    private static String buildChords(SongtextAndChordsDto songtextAndChords) {
        String chorusChords = String.join("|", songtextAndChords.chorusChords());
        String verseChords = String.join("|", songtextAndChords.verseChords());
        return verseChords + "|" + chorusChords + "|" + verseChords + "|" + chorusChords;
    }
}