package ch.zuehlke.fullstack.hackathon.service.replicate;

import ch.zuehlke.fullstack.hackathon.model.BertPromptDto;
import ch.zuehlke.fullstack.hackathon.model.MusicGenPromptDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Service
@Slf4j
public class ReplicateApi {

    private static final String URL = "https://api.replicate.com/v1/predictions";

    private static final String BERT_MODEL = "58bdc2073c9c07abcc4200fe808e15b1a555dbb1390e70f5daa6b3d81bd11fb1";

    private static final String MUSICGEN_MODEL = "b05b1dff1d8c6dc63d14b0cdb42135378dcb87f6373b0d3d341ede46e59e2b38";

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    private final WebClient webClient;

    private final Map<String, Future<?>> scheduledJobs = new ConcurrentHashMap<>();

    public ReplicateApi(@Value("${replicateApiKey}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(URL)
                .defaultHeader("Authorization", "Token %s".formatted(apiKey))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public ReplicateResult<Map<String, String>> createBertJob(BertPromptDto bertPrompt) {
        var body = new ReplicateInput<>(BERT_MODEL, bertPrompt);
        return webClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ReplicateResult<Map<String, String>>>() {
                })
                .block();
    }

    public ReplicateResult<String> createMusicGenJob(MusicGenPromptDto prompt) {
        var body = new ReplicateInput<>(MUSICGEN_MODEL, prompt);
        return webClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ReplicateResult<String>>() {
                })
                .block();
    }

    public <T> void pollForResults(String jobUrl, Consumer<ReplicateResult<T>> songUpdater) {
        ScheduledFuture<?> job = executor.scheduleWithFixedDelay(() -> {
            var pollResult = pollResult(jobUrl);
            var status = pollResult.status();

            log.info("Current status: {}", status);

            if (pollResult.isDone()) {
                scheduledJobs.get(jobUrl).cancel(true);
                scheduledJobs.remove(jobUrl);
                log.info("Cancelling job: {}", jobUrl);
            }

            if (pollResult.isSucceeded()) {
                songUpdater.accept(pollResult);
                log.info("Completed bert job: {}", jobUrl);
            }
        }, 3, 2, TimeUnit.SECONDS);

        scheduledJobs.put(jobUrl, job);
    }

    public ReplicateResult pollResult(String jobUrl) {
        return webClient
                .mutate()
                .baseUrl(jobUrl)
                .build()
                .get()
                .retrieve()
                .bodyToMono(ReplicateResult.class)
                .block();
    }
}
