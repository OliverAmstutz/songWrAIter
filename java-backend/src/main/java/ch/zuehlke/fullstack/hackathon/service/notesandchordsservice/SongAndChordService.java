package ch.zuehlke.fullstack.hackathon.service.notesandchordsservice;

import ch.zuehlke.fullstack.hackathon.model.PromptInputDto;
import ch.zuehlke.fullstack.hackathon.model.musicgen.CreateSongDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.access.InvalidInvocationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SongAndChordService {

    @Value("${apiKey}")
    private String apiKey;

    private OpenAiService openAiService;

    public SongtextAndChordsDto generateNotesAndChordsFromInput(
            final PromptInputDto promptInputDto) {
        final String userPrompt = createPromptFromInput(promptInputDto);
        log.info("User prompt: {}", userPrompt);
        ChatMessage promptMessage = new ChatMessage(ChatMessageRole.USER.value(), userPrompt);
        Optional<SongtextAndChordsDto> openAiResponse = getOpenAiResponse(List.of(promptMessage));

        if (openAiResponse.isPresent()) {
            log.info("Success! Result={}", openAiResponse.get());
            return openAiResponse.get();
        }
        return null;
    }

    public String generateMusicgenPrompt(CreateSongDto createSongDto) {
        String systemPrompt = """
                You are a prompter for a music-generative ai. Take the  
                following input: 
                         - genre (e.g classic)
                         - artist (e.g. avici)
                         - chord progression (e.g. E, G, A)
                         - beats per minute: (e.g. 100)
                         - time signature: (e.g. 3/4)
                         - mood: (e.g. upbeat, low, happy, powerful, sad, angry,  joy)
                         - instruments (e.g. guitar, drums)
                         
                        You respond in the following way, don't say anything else:
                        Classic song in the style of avici with chord progression 'E, G, A' leading up to a crescendo. Use violins,  
                        cellos, kettledrums and flutes
                """;
        String userPrompt = String.format(
                """
                        make a prompt from this input:
                                - genre: %s
                                - artist: %s
                                - chord progression: %s
                                - beats per minute: %s
                                - time signature: %s
                                - mood: %s
                                - instruments: %s
                        """,
                createSongDto.genre(),
                createSongDto.artist(),
                transformArrayToText(createSongDto.chordProgression()),
                createSongDto.beatsPerMinute(),
                createSongDto.timeSignature(),
                createSongDto.mood(),
                transformArrayToText(createSongDto.instruments()));
        log.info("User prompt: {}", userPrompt);
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt);
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), userPrompt);

        ChatCompletionRequest chatRequest = ChatCompletionRequest
                .builder()
                .messages(List.of(systemMessage, userMessage))
                .model("gpt-3.5-turbo")
                .maxTokens(600)
                .n(1)
                .build();

        Optional<String> openAiResponse = getOpenAiService()
                .createChatCompletion(chatRequest)
                .getChoices()
                .stream()
                .findFirst()
                .map(ChatCompletionChoice::getMessage)
                .map(ChatMessage::getContent);

        if (openAiResponse.isPresent()) {
            log.info("Success! Result={}", openAiResponse.get());
            return openAiResponse.get();
        }
        log.error("Error. Result={}", openAiResponse);
        throw new InvalidInvocationException("Chat gpt respondes with error");
    }

    private Optional<SongtextAndChordsDto> getOpenAiResponse(List<ChatMessage> messages) {
        ChatCompletionRequest chatRequest = ChatCompletionRequest
                .builder()
                .messages(messages)
                .model("gpt-3.5-turbo")
                .maxTokens(600)
                .n(1)
                .build();

        return getOpenAiService()
                .createChatCompletion(chatRequest)
                .getChoices()
                .stream()
                .findFirst()
                .map(ChatCompletionChoice::getMessage)
                .map(ChatMessage::getContent)
                .flatMap(this::parseNotesAndChords);
    }

    private Optional<SongtextAndChordsDto> parseNotesAndChords(String content) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            SongtextAndChordsDto songtextAndChordsDto = mapper.readValue(
                    content,
                    SongtextAndChordsDto.class);
            return Optional.of(songtextAndChordsDto);
        } catch (IOException e) {
            log.error("message = {}, stacktrace = {}", e.getMessage(), e.getStackTrace());
            return Optional.empty();
        }
    }

    public static String createPromptFromInput(final PromptInputDto promptInputDto) {
        return "Given the following specifications, generate a children song with one verse and " +
                "its chords and one chorus and its chords. The verse should be about 10 lines, " +
                "the chorus half\n\n"
                + "Topic: " + promptInputDto.topic() + "\n"
                + "The topic represents what the song is about.\n"
                + "Genre: " + promptInputDto.genre() + "\n"
                + "The genre is the style of the song.\n"
                + "Instruments: " + String.join(", ", promptInputDto.instruments()) + "\n"
                + "What instruments are played in the song\n"
                + "Mood: " + promptInputDto.mood() + "\n\n"
                + "This is the mood the song should inspire in the child.\n"
                + "Please structure your response as a json as follows:\n"
                + "- 'verseSongtext': A string containing the lyrics of the verse.\n"
                +
                "- 'verseChords': An array of strings, where each string represents the chords of" +
                " the verse.\n"
                + "- 'chorusSongtext': A string containing the lyrics of the chorus.\n"
                +
                "- 'chorusChords': An array of strings, where each string represents the chords " +
                "of the chorus.\n"
                + "Ensure the songtext and chords reflect the given specifications.";
    }

    @NotNull
    private static List<String> transformArrayToText(List<String> input) {
        return input
                .stream()
                .map(i -> i + ",")
                .toList();
    }

    private OpenAiService getOpenAiService() {
        if (openAiService == null) {
            this.openAiService = new OpenAiService(apiKey);
        }

        return openAiService;
    }
}