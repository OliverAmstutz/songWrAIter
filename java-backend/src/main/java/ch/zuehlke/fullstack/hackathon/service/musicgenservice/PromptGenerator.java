package ch.zuehlke.fullstack.hackathon.service.musicgenservice;

import ch.zuehlke.fullstack.hackathon.model.musicgen.CreateSongDto;

public class PromptGenerator {
    // todo: change dto for chatgpt input
    public static String create(CreateSongDto createSongDto) {
        return "";
        //        return String.format(
        //                // todo: use chat gpt prompt
        //                "A song in the style of %s, in a %s mood. " +
        //                        "Use the following instruments: %s. The chord progression is: %s",
        //                createSongDto.style(),
        //                createSongDto.mood(),
        //                createSongDto.instruments(),
        //                );
    }
}
