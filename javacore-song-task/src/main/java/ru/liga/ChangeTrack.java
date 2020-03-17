package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;



public class ChangeTrack {

    private static Logger logger = LoggerFactory.getLogger(ChangeTrack.class);

    public static void change(MidiFile midiFile, File file, int trans, int tempo) throws Exception {
        logger.debug("inside method: change");

        MidiFile changedMidiFile = changeTrans(midiFile, trans);
        changedMidiFile = changeTempo(changedMidiFile, tempo);
        String name = file.getName().replace(".mid", "") + "-trans" + trans + "-tempo" + tempo + ".mid";
        changedMidiFile.writeToFile(new File(file.getParentFile().getAbsolutePath() + File.separator + name));
        logger.debug("Midifile was changed");
    }


    public static MidiFile changeTrans(MidiFile midiFile, int trans) {
        logger.debug("inside method: changeTrans");
        midiFile.getTracks().stream()
                .flatMap(midiTrack -> midiTrack.getEvents().stream())
                .filter(midiEvent -> (midiEvent.getClass().equals(NoteOn.class)))
                .forEach(midiEvent -> ((NoteOn) midiEvent).setNoteValue(((NoteOn) midiEvent).getNoteValue() + trans));
        midiFile.getTracks().stream()
                .flatMap(midiTrack -> midiTrack.getEvents().stream())
                .filter(midiEvent -> (midiEvent.getClass().equals(NoteOff.class)))
                .forEach(midiEvent -> ((NoteOff) midiEvent).setNoteValue(((NoteOff) midiEvent).getNoteValue() + trans));

        logger.debug("Midifile was transposed in " + trans + " semitones");
        return midiFile;
    }

    public static MidiFile changeTempo(MidiFile changedMidiFile, float tempo) {
        logger.debug("inside method: changeTempo");
        changedMidiFile.getTracks().stream().
                flatMap(midiTrack -> midiTrack.getEvents().stream())
                .filter(midiEvent -> (midiEvent.getClass().equals(Tempo.class)))
                .forEach(midiEvent -> ((Tempo) midiEvent).setBpm(((Tempo) midiEvent).getBpm() * (1 + tempo / 100)));
        logger.debug("MidiFile tempo was increased by " + tempo + " percent");
        return changedMidiFile;

    }

}
