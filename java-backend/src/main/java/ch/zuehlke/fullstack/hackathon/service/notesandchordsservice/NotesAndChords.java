package ch.zuehlke.fullstack.hackathon.service.notesandchordsservice;

import lombok.Builder;

import java.util.List;

@Builder
public record NotesAndChords(List<String> notes, List<String> chords) {
}
