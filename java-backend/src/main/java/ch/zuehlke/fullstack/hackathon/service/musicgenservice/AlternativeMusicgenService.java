package ch.zuehlke.fullstack.hackathon.service.musicgenservice;

import ch.zuehlke.fullstack.hackathon.model.musicgen.CreateSongDto;
import ch.zuehlke.fullstack.hackathon.model.musicgen.ModelInput;
import ch.zuehlke.fullstack.hackathon.model.musicgen.MusicgenRequestDto;
import ch.zuehlke.fullstack.hackathon.model.musicgen.MusicgenResponseDto;
import ch.zuehlke.fullstack.hackathon.model.musicgen.MusicgenSong;
import ch.zuehlke.fullstack.hackathon.service.MusicgenSongCache;
import ch.zuehlke.fullstack.hackathon.service.notesandchordsservice.SongAndChordService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AlternativeMusicgenService {

    private final String apiKey;

    private final SongAndChordService chatGpt;

    private final MusicgenSongCache cache;

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    private final Map<String, Future<?>> scheduledJobs = new ConcurrentHashMap<>();

    public AlternativeMusicgenService(@Value("${replicateApiKey}") String apiKey,
                                      SongAndChordService chatGpt,
                                      MusicgenSongCache cache) {
        this.apiKey = apiKey;
        this.chatGpt = chatGpt;
        this.cache = cache;
    }

    public void generateSong(CreateSongDto dto) {
        String version = "b05b1dff1d8c6dc63d14b0cdb42135378dcb87f6373b0d3d341ede46e59e2b38";
        int topK = 250;
        int topP = 0;
        int temperature = 1;
        int classifierFreeGuidance = 3;
        int duration = 20;
        String modelVersion = "stereo-large";
        String normalizationStrategy = "peak";
        String prompt = chatGpt.generateMusicgenPrompt(dto);

        MusicgenSong song = createMusicgenSongFromDto(dto, prompt);
        cache.addNewSong(song);

        ModelInput requestDto = createModelInput(
                prompt,
                topK,
                topP,
                temperature,
                classifierFreeGuidance,
                modelVersion,
                normalizationStrategy,
                duration);
        MusicgenRequestDto request = new MusicgenRequestDto(version, requestDto);

        MusicgenResponseDto response = createMusicgenJob(request);
        log.info("Created musicgen job: {}", response);

        URL songUrl = response
                .urls()
                .get();
        String songUrlString = songUrl.toString();

        ScheduledFuture<?> job = executor.scheduleWithFixedDelay(() -> {
            MusicgenResponseDto pollResponse = pollResult(songUrlString);
            String status = pollResponse.status();
            log.info("Current status: {}", status);

            if (jobFailed(status)) {
                cancelAndRemoveJob(songUrlString);
                log.info("Cancelling job: {}", songUrlString);
            }
            if (jobSucceded(status)) {
                log.info(
                        "Completed musicgen job: {}",
                        pollResponse
                                .urls()
                                .get()
                                .toString());
                var output = pollResponse.output();
                try {
                    cache.updateSong(song, new URL((String) output));
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 3, 2, TimeUnit.SECONDS);
        scheduledJobs.put(songUrlString, job);
    }


    @Nullable
    private MusicgenResponseDto createMusicgenJob(MusicgenRequestDto request) {
        return WebClient
                .create("https://api.replicate.com/v1/predictions")
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Token %s".formatted(apiKey))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(MusicgenResponseDto.class)
                .block();
    }

    @NotNull
    private ModelInput createModelInput(String prompt, int topK, int topP, int temperature,
                                        int classifierFreeGuidance, String modelVersion,
                                        String normalizationStrategy, int duration) {
        return new ModelInput(
                prompt,
                topK,
                topP,
                temperature,
                classifierFreeGuidance,
                modelVersion,
                normalizationStrategy,
                duration);
    }

    @NotNull
    private MusicgenSong createMusicgenSongFromDto(CreateSongDto dto, String prompt) {
        return new MusicgenSong(
                UUID.randomUUID(),
                dto.title(),
                dto.genre(),
                prompt,
                dto.chordProgression(),
                dto.artist(),
                dto.beatsPerMinute(),
                dto.timeSignature(),
                dto.mood(),
                dto.instruments(),
                null
        );
    }

    private void cancelAndRemoveJob(String jobUrl) {
        scheduledJobs
                .get(jobUrl)
                .cancel(true);
        scheduledJobs.remove(jobUrl);
    }

    private MusicgenResponseDto pollResult(String url) {
        return WebClient
                .create(url)
                .get()
                .header("Authorization", "Token %s".formatted(apiKey))
                .retrieve()
                .bodyToMono(MusicgenResponseDto.class)
                .block();
    }

    private boolean jobSucceded(String status) {
        return status.equals("succeeded");
    }

    private boolean jobFailed(String status) {
        return !status.equals("starting") && !status.equals("processing");
    }
}
