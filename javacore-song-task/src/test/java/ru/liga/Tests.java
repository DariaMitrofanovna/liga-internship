package ru.liga;

import com.leff.midi.MidiFile;

import static org.junit.Assert.*;
import static ru.liga.AnalyzeVoice.*;
import static ru.liga.songtask.domain.NoteSign.E_3;

import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOn;
import org.junit.Test;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;

import java.util.List;

public class Tests {

    MidiFile midiFile = MidiFileForTests.createMidifile();
    List<MidiTrack> midiTracks = midiFile.getTracks();
    List<Note> voiceNotes = eventsToNotes(midiTracks.get(1).getEvents());

    @Test
    public void testChangeTempo() {
        MidiFile midiFile2 = MidiFileForTests.createMidifile();
        ChangeTrack.changeTempo(midiFile2, 20);
        assertEquals(273, getTempo(midiFile2).getBpm(), 1);
    }

    @Test
    public void testChangeTrans() {
        MidiFile midiFile3 = MidiFileForTests.createMidifile();
        ChangeTrack.changeTrans(midiFile3, 2);
        MidiEvent n = (MidiEvent) midiFile3.getTracks().get(1).getEvents().toArray()[1];
        int value = ((NoteOn) n).getNoteValue();
        assertEquals(5, value);
    }

    @Test
    public void testGetDoubleCountOfTextEvents() {

        assertEquals(160, getDoubleCountOfTextEvents(midiTracks));
    }

    @Test
    public void testGetCountsOfNotesInTracks() {

        assertEquals(java.util.Optional.of(0), java.util.Optional.of(getCountsOfNotesInTracks(midiTracks).get(0)));
        assertEquals(java.util.Optional.of(320), java.util.Optional.of(getCountsOfNotesInTracks(midiTracks).get(1)));
        assertEquals(java.util.Optional.of(0), java.util.Optional.of(getCountsOfNotesInTracks(midiTracks).get(2)));
    }

    @Test
    public void testGetTempo() {

        assertEquals(228, getTempo(midiFile).getBpm(), 1);
    }

    @Test
    public void testGetDiapason() {

        assertEquals(61, getDiapason(voiceNotes));
    }


    @Test
    public void testGetDuration() {

        assertEquals(2, getDuration(voiceNotes, midiFile).size());
        assertEquals(1, (int) getDuration(voiceNotes, midiFile).get(4802));

    }

    @Test
    public void testGetAmount() {
        assertEquals(2, getDuration(voiceNotes, midiFile).size());
        assertEquals(2, (int) getAmount(voiceNotes).get(E_3));
    }


}