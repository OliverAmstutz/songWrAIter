package ch.zuehlke.fullstack.hackathon.service.bertservice;

import ch.zuehlke.fullstack.hackathon.model.DallePromptInput;
import ch.zuehlke.fullstack.hackathon.model.ImageDto;
import ch.zuehlke.fullstack.hackathon.model.Song;
import ch.zuehlke.fullstack.hackathon.service.SongCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    @Value("${apiKey}")
    private String apiKey;

    private final SongCache songCache;

    public void generateImageFrom(final String topic, final String mood, UUID songId) {
        String imagePrompt = createPromptFromInput(topic, mood);
        log.info("Image user prompt: {}", imagePrompt);
        DallePromptInput dallePromptInput = new DallePromptInput("dall-e-2", imagePrompt, 1, "512x512");

        ImageDto image = WebClient.create("https://api.openai.com/v1/images/generations")
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer %s" .formatted(apiKey))
                .bodyValue(dallePromptInput)
                .retrieve()
                .bodyToMono(ImageDto.class)
                .block();

        log.info("Created dall-e result: {}", image);
        Song song = songCache.getById(songId);
        if(song != null && image != null) {
            songCache.updateSong(song.imageUrl(image.data().get(0).url()));
        }
    }

    public static String createPromptFromInput(final String topic, final String mood) {
        return "Generate an artistic illustration of " + topic + " in an "+mood+" mood. Focus on creating a visually captivating image in the style of a children's drawing. Please refrain from generating textual descriptions and prioritize drawing the requested scene.";
    }
}