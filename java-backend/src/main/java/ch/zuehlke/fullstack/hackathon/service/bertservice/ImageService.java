package ch.zuehlke.fullstack.hackathon.service.bertservice;

import ch.zuehlke.fullstack.hackathon.model.DallePromptInput;
import ch.zuehlke.fullstack.hackathon.model.ImageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    @Value("${apiKey}")
    private String apiKey;

    public ImageDto generateImageFrom(final String topic) {
        String imagePrompt = createPromptFromInput(topic);
        log.info("Image user prompt: {}", imagePrompt);
        DallePromptInput dallePromptInput = new DallePromptInput("dall-e-2", imagePrompt, 1, "512x512");

        Map result = WebClient.create("https://api.openai.com/v1/images/generations")
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer %s" .formatted(apiKey))
                .bodyValue(dallePromptInput)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        log.info("Created dall-e result: {}", result);

        return mapImageUrl(result);
    }

    private ImageDto mapImageUrl(Map result) {
        return new ImageDto((String) result.get("url"));
    }

    public static String createPromptFromInput(final String topic) {
        return "Generate an artistic illustration of " + topic + ".\n"
                + "Focus on creating a visually captivating image in the style of a children's drawing.\n"
                + "Please refrain from generating textual descriptions and prioritize drawing the requested scene.";
    }
}