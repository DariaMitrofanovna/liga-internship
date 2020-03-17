package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.Text;
import com.leff.midi.event.meta.TimeSignature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MidiFileForTests {
    public static MidiFile createMidifile() {
        MidiTrack tempoTrack = new MidiTrack();
        MidiTrack noteTrack = new MidiTrack();
        MidiTrack textTrack = new MidiTrack();
        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

        Tempo t = new Tempo();
        t.setBpm(228);

        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(t);


        for (int i = 0; i < 80; i++) {
            int channel = 0, pitch = 1 + i, velocity = 100;
            NoteOn on = new NoteOn(i * 480, channel, pitch, velocity);
            NoteOff off = new NoteOff(i * 480 + 120, channel, pitch, 0);
            Text text = new Text(i * 480, 120, "text" + i);

            noteTrack.insertEvent(on);
            noteTrack.insertEvent(off);
            textTrack.insertEvent(text);

            noteTrack.insertNote(channel, pitch + 2, velocity, i * 480, 120);
        }


        ArrayList<MidiTrack> tracks = new ArrayList<MidiTrack>();
        tracks.add(tempoTrack);
        tracks.add(noteTrack);
        tracks.add(textTrack);


        MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);


        File output = new File("midiFileForTests.mid");
        try {
            midi.writeToFile(output);
        } catch (IOException e) {
            System.err.println(e);
        }
        return midi;
    }
}
