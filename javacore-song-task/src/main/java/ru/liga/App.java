package ru.liga;


import com.leff.midi.MidiFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.domain.Note;

import java.io.File;

public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {

        logger.debug("Start of program");
        File file = new File(args[0]);
        MidiFile midiFile = new MidiFile(file);
        if (args[1].equals("analyze")) {
            AnalyzeVoice analyzer = new AnalyzeVoice();
            analyzer.analyze(midiFile);
            LogInfo.log(analyzer.voiceNotes,analyzer.diapazonInfo, analyzer.duration,analyzer.amount);
        }
        if (args[1].equals("change")) {

            ChangeTrack.change(midiFile, file, Integer.parseInt(args[3]), Integer.parseInt(args[5]));

        }
        logger.debug("End of program");
    }


}
