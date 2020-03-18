package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import org.junit.Test;
import ru.liga.songtask.domain.Note;
import java.util.List;
import static org.junit.Assert.*;
import static ru.liga.AnalyzeVoice.*;
import static ru.liga.songtask.domain.NoteSign.E_3;

public class AnalyzeVoiceTest {
    MidiFile midiFile = MidiFileForTests.createMidifile();
    List<MidiTrack> midiTracks = midiFile.getTracks();
    List<Note> voiceNotes = eventsToNotes(midiTracks.get(1).getEvents());
    AnalyzeVoice analyzer = new AnalyzeVoice();

    @Test
    public void testGetDoubleCountOfTextEvents() {

        assertEquals(160, getDoubleCountOfTextEvents(midiTracks));
    }

    @Test
    public void testGetCountsOfNotesInTracks() {

        assertEquals(0, (int) getCountsOfNotesInTracks(midiTracks).get(0));
        assertEquals(320, (int) getCountsOfNotesInTracks(midiTracks).get(1));
        assertEquals(0, (int) getCountsOfNotesInTracks(midiTracks).get(2));
    }

    @Test
    public void testGetTempo() {

        assertEquals(228, getTempo(midiFile).getBpm(), 1);
    }

    @Test
    public void testGetDiapason() {

        assertEquals(61, (int)analyzer.getDiapason(voiceNotes).get(0));
    }


    @Test
    public void testGetDuration() {

        assertEquals(2, analyzer.getDuration(voiceNotes, midiFile).size());
        assertEquals(1, (int) analyzer.getDuration(voiceNotes, midiFile).get(4802));

    }

    @Test
    public void testGetAmount() {
        assertEquals(2, analyzer.getDuration(voiceNotes, midiFile).size());
        assertEquals(2, (int) analyzer.getAmount(voiceNotes).get(E_3));
    }


}