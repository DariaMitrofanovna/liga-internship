package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;
import ru.liga.songtask.util.SongUtils;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class AnalyzeVoice {

    private static List<MidiTrack> midiTracks;
    private static Logger logger = LoggerFactory.getLogger(AnalyzeVoice.class);
    List<Integer> diapazonInfo;
    Map<Integer, Integer> duration;
    Map<NoteSign, Integer> amount;
    List<Note> voiceNotes;


    public void analyze(MidiFile midiFile) {

        logger.debug("inside method: analize");
        getTracksOfMidifile(midiFile);
        int countOfTextEvents = getDoubleCountOfTextEvents(midiTracks);
        List<Integer> countsOfNotesInTracks = getCountsOfNotesInTracks(midiTracks);
        int voiceTrack = getVoiceTrack(countOfTextEvents, countsOfNotesInTracks);
        voiceNotes = eventsToNotes(midiTracks.get(voiceTrack).getEvents());
        getDiapason(voiceNotes);
        getDuration(voiceNotes, midiFile);
        getAmount(voiceNotes);
    }


    public static void getTracksOfMidifile(MidiFile midiFile) {
        logger.debug("inside method: getParametresOfMidifile");
        midiTracks = midiFile.getTracks();

    }

    public static int getDoubleCountOfTextEvents(List<MidiTrack> midiTracks) {
        logger.debug("inside method: getDoubleCountOfTextEvents");
        int countOfTextEvents = (int) midiTracks.stream()
                .flatMap(midiTrack -> midiTrack.getEvents().stream())
                .filter(midiEvent -> midiEvent.getClass().equals(Text.class))
                .count();

        logger.debug("countOfTextEvents : " + countOfTextEvents);
        return countOfTextEvents * 2;

    }

    public static List<Integer> getCountsOfNotesInTracks(List<MidiTrack> midiTracks) {
        logger.debug("inside method: getCountsOfNotesInTracks");


        List<Integer> countsOfNotesInTracks = new ArrayList<>();
        for (MidiTrack midiTrack : midiTracks) {
            int countsOfNotesInTrack = (int) midiTrack.getEvents().stream()
                    .filter(midiEvent -> (midiEvent.getClass().equals(NoteOn.class)) | (midiEvent.getClass().equals(NoteOff.class)))
                    .count();
            countsOfNotesInTracks.add(countsOfNotesInTrack);

        }

        logger.debug("countsOfNotesInTrack : ");
        countsOfNotesInTracks.forEach(n -> logger.debug(n.toString()));
        return countsOfNotesInTracks;

    }

    public static int getVoiceTrack(int countOfTextEvents, List<Integer> countsOfNotesInTracks) {
        logger.debug("inside method: getVoiceTrack");
        int voiceTrack = 0;
        int difference = 1000;
        for (int i = 0; i < countsOfNotesInTracks.size(); i++) {
            if ((Math.abs(countOfTextEvents - countsOfNotesInTracks.get(i)) < difference)
                    & (isNoChords(eventsToNotes(midiTracks.get(i).getEvents())))
            ) {
                voiceTrack = i;
                difference = countOfTextEvents - countsOfNotesInTracks.get(i);
            }
        }
        logger.debug("voiceTrack : " + voiceTrack);
        return voiceTrack;


    }


    private static boolean isNoChords(List<Note> midiTrack) {
        logger.debug("inside method: isNoChords");
        long EndTick = 0L;
        Note n;
        for (Iterator<Note> var3 = midiTrack.iterator(); var3.hasNext(); EndTick = n.startTick() + n.durationTicks()) {
            n = var3.next();
            if (EndTick > n.startTick()) {
                logger.debug("Track has chords");
                return false;
            }
        }
        logger.debug("Track has no chords");
        return true;
    }

    public List<Integer> getDiapason(List<Note> voiceNotes) {
        logger.debug("inside method: getDiapason");
        diapazonInfo = new ArrayList<>();
        int diapazon = 0;
        int minIndex = 0;
        int maxIndex = 0;

        for (int i = 1; i < voiceNotes.size(); i++) {
            if (voiceNotes.get(i).sign().getFrequencyHz() > voiceNotes.get(maxIndex).sign().getFrequencyHz()) {
                maxIndex = i;
            }
            if (voiceNotes.get(i).sign().getFrequencyHz() < voiceNotes.get(minIndex).sign().getFrequencyHz()) {
                minIndex = i;
            }
            diapazon = (voiceNotes.get(maxIndex).sign().getMidi() - voiceNotes.get(minIndex).sign().getMidi());
        }
        diapazonInfo.add(diapazon);
        diapazonInfo.add(minIndex);
        diapazonInfo.add(maxIndex);
        return diapazonInfo;
    }

    public Map<Integer, Integer> getDuration(List<Note> voiceNotes, MidiFile midiFile) {
        logger.debug("inside method: getDuration");
        duration = new HashMap<>();

        float bpm = getTempo(midiFile).getBpm();

        (voiceNotes.stream().map((n) -> SongUtils.tickToMs(bpm, midiFile.getResolution(), n.durationTicks()))
                .collect(Collectors.groupingBy((dur) -> dur, Collectors.counting()))).
                forEach((i, l) -> duration.put(i, Math.toIntExact(l)));
        return duration;

    }

    public Map<NoteSign, Integer> getAmount(List<Note> voiceNotes) {
        logger.debug("inside method: getAmount");
        amount = new HashMap<>();
        (voiceNotes.stream()
                .map(Note::sign)
                .collect(Collectors.groupingBy((sign) -> sign, Collectors.counting())))
                .forEach((sign, l) ->
                        amount.put(sign, Math.toIntExact(l))
                );
        return amount;
    }

    public static Tempo getTempo(MidiFile midiFile) {
        logger.debug("inside method: getTempo");
        return (Tempo) (midiFile.getTracks().get(0)).getEvents().stream().filter(event -> event instanceof Tempo).findFirst().get();

    }

    public static List<Note> eventsToNotes(TreeSet<MidiEvent> events) {
        logger.debug("inside method: eventsToNotes");

        List<Note> vbNotes = new ArrayList<>();

        Queue<NoteOn> noteOnQueue = new LinkedBlockingQueue<>();
        for (MidiEvent event : events) {
            if (event instanceof NoteOn || event instanceof NoteOff) {
                if (isEndMarkerNote(event)) {
                    NoteSign noteSign = NoteSign.fromMidiNumber(extractNoteValue(event));
                    if (noteSign != NoteSign.NULL_VALUE) {
                        NoteOn noteOn = noteOnQueue.poll();
                        if (noteOn != null) {
                            long start = noteOn.getTick();
                            long end = event.getTick();
                            vbNotes.add(
                                    new Note(noteSign, start, end - start));
                        }
                    }
                } else {
                    noteOnQueue.offer((NoteOn) event);
                }
            }
        }
        return vbNotes;
    }

    private static Integer extractNoteValue(MidiEvent event) {
        if (event instanceof NoteOff) {
            return ((NoteOff) event).getNoteValue();
        } else if (event instanceof NoteOn) {
            return ((NoteOn) event).getNoteValue();
        } else {
            return null;
        }
    }

    private static boolean isEndMarkerNote(MidiEvent event) {
        if (event instanceof NoteOff) {
            return true;
        } else if (event instanceof NoteOn) {
            return ((NoteOn) event).getVelocity() == 0;
        } else {
            return false;
        }

    }
}
