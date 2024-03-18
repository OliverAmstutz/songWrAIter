package ch.zuehlke.fullstack.hackathon.service.notesandchordsservice;

import ch.zuehlke.fullstack.hackathon.model.PromptInputDto;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteAndChordService {

    @Value("${apiKey}")
    private String apiKey;
    private OpenAiService openAiService;

    public NotesAndChords generateNotesAndChordsFromInput(final PromptInputDto promptInputDto) {

        final String prompt = createPromptFromInput(promptInputDto);






        return new NotesAndChords(
                List.of("A4", "B5"),
                List.of("a8", "bs")
        );
    }

    public static String createPromptFromInput(final PromptInputDto promptInputDto) {
        String instruments = String.join(", ", promptInputDto.instruments());

        return String.format("This could %s %s %s %s be the final prompt",
                promptInputDto.topic(),
                promptInputDto.genre(),
                instruments,
                promptInputDto.mood());
    }

    public Optional<String> getMessageOfTheDay() {
        ChatMessage message = new ChatMessage(ChatMessageRole.USER.value(), "Write a message of the day for a software engineer.");
        List<ChatMessage> messages = List.of(message);
        ChatCompletionRequest chatRequest = ChatCompletionRequest.builder()
                .messages(messages)
                .model("gpt-3.5-turbo")
                .maxTokens(100)
                .n(1)
                .build();

        return getOpenAiService().createChatCompletion(chatRequest).getChoices().stream()
                .findFirst()
                .map(ChatCompletionChoice::getMessage)
                .map(ChatMessage::getContent);
    }

    private OpenAiService getOpenAiService() {
        if (openAiService == null) {
            this.openAiService = new OpenAiService(apiKey);
        }

        return openAiService;
    }

}
