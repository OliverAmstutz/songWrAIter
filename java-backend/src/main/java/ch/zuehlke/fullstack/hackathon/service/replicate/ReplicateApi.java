package ch.zuehlke.fullstack.hackathon.service.replicate;

import ch.zuehlke.fullstack.hackathon.model.BertPromptDto;
import ch.zuehlke.fullstack.hackathon.model.MusicGenPromptDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ReplicateApi {

    private static final String URL = "https://api.replicate.com/v1/predictions";

    private static final String BERT_MODEL = "58bdc2073c9c07abcc4200fe808e15b1a555dbb1390e70f5daa6b3d81bd11fb1";
    private static final String MUSICGEN_MODEL = "b05b1dff1d8c6dc63d14b0cdb42135378dcb87f6373b0d3d341ede46e59e2b38";

    private final WebClient webClient;

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

    public ReplicateResult<Map<String, String>> pollBertResult(String jobUrl) {
        return webClient
                .mutate()
                .baseUrl(jobUrl)
                .build()
                .get()
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

    public ReplicateResult<String> pollMusicGenResult(String jobUrl) {
        return webClient
                .mutate()
                .baseUrl(jobUrl)
                .build()
                .get()
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ReplicateResult<String>>() {
                })
                .block();
    }
}
