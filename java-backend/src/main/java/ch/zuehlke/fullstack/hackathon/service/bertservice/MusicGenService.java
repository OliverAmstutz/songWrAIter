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

        replicateApi.pollForResults(result.getJobUrl(), (ReplicateResult<String> pollResult) -> {
            var output = pollResult.output();

            var songToUpdate = songCache.getById(song.id());
            Song updatedSong = songToUpdate.musicGenUrls(new SongUrls(output, null, null));
            songCache.updateSong(updatedSong);
        });

        return id;
    }

    @NotNull
    private static String buildChords(SongtextAndChordsDto songtextAndChords) {
        String chorusChords = String.join("|", songtextAndChords.chorusChords());
        String verseChords = String.join("|", songtextAndChords.verseChords());
        return verseChords + "|" + chorusChords + "|" + verseChords + "|" + chorusChords;
    }
}