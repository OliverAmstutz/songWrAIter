package ch.zuehlke.fullstack.hackathon.service.notesandchordsservice;

import lombok.Builder;
import lombok.ToString;

import java.util.List;

@Builder
public record SongtextAndChordsDto(String verseSongtext, List<String> verseChords, String chorusSongtext,  List<String> chorusChords) {
}
