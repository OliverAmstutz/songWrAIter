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

        replicateApi.pollForResults(result.getJobUrl(), (ReplicateResult<Map<String, String>> pollResult) -> {
            var urls = pollResult.output();

            var songToUpdate = songCache.getById(song.id());
            var updatedSong = songToUpdate.bertUrls(new SongUrls(urls.get("mp3"), urls.get("score"), urls.get("midi")));
            songCache.updateSong(updatedSong);
        });

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