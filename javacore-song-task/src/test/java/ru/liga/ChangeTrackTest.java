package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOn;
import org.junit.Test;
import static org.junit.Assert.*;
import static ru.liga.AnalyzeVoice.getTempo;

public class ChangeTrackTest {

    @Test
    public void testChangeTempo() {
        MidiFile midiFile1 = MidiFileForTests.createMidifile();
        ChangeTrack.changeTempo(midiFile1, 20);
        assertEquals(273, getTempo(midiFile1).getBpm(), 1);
    }

    @Test
    public void testChangeTrans() {
        MidiFile midiFile2 = MidiFileForTests.createMidifile();
        ChangeTrack.changeTrans(midiFile2, 2);
        MidiEvent n = (MidiEvent) midiFile2.getTracks().get(1).getEvents().toArray()[1];
        int value = ((NoteOn) n).getNoteValue();
        assertEquals(5, value);
    }

}