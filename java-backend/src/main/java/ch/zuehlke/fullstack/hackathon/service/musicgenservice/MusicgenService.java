package ch.zuehlke.fullstack.hackathon.service.musicgenservice;

import ch.zuehlke.fullstack.hackathon.model.musicgen.CreateSongDto;
import ch.zuehlke.fullstack.hackathon.model.musicgen.ModelInput;
import ch.zuehlke.fullstack.hackathon.model.musicgen.MusicgenRequestDto;
import ch.zuehlke.fullstack.hackathon.model.musicgen.MusicgenResponseDto;
import ch.zuehlke.fullstack.hackathon.service.notesandchordsservice.SongAndChordService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MusicgenService {

    private final String apiKey;

    private final SongAndChordService chatGpt;

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    private final Map<String, Future<?>> scheduledJobs = new ConcurrentHashMap<>();

    public MusicgenService(@Value("${apiKeyPhil}") String apiKey, SongAndChordService chatGpt) {
        this.apiKey = apiKey;
        this.chatGpt = chatGpt;
    }

    public void generateSong(CreateSongDto createSongDto) {
        String version = "b05b1dff1d8c6dc63d14b0cdb42135378dcb87f6373b0d3d341ede46e59e2b38";
        int topK = 250;
        int topP = 0;
        int temperature = 1;
        int classifierFreeGuidance = 3;
        int duration = 20;
        String modelVersion = "stereo-large";
        String normalizationStrategy = "peak";
        String prompt = chatGpt.generateMusicgenPrompt(createSongDto);

        //        MusicgenSong song = new MusicgenSong();

        ModelInput requestDto = new ModelInput(
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

        String jobUrl = response
                .urls()
                .get()
                .toString();
        ScheduledFuture<?> job = executor.scheduleWithFixedDelay(() -> {
            MusicgenResponseDto pollResponse = pollResult(jobUrl);
            String status = pollResponse.status();
            log.info("Current status: {}", status);

            if (jobFailed(status)) {
                cancelAndRemoveJob(jobUrl);
                log.info("Cancelling job: {}", jobUrl);
            }
            if (jobSucceded(status)) {
                log.info(
                        "Completed musicgen job: {}",
                        pollResponse
                                .urls()
                                .get()
                                .toString());
            }
        }, 3, 2, TimeUnit.SECONDS);
        scheduledJobs.put(jobUrl, job);
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
