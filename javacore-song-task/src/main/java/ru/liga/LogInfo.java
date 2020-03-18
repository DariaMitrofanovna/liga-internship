package ru.liga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;

import java.util.List;
import java.util.Map;

public class LogInfo {
    private static Logger logger = LoggerFactory.getLogger(LogInfo.class);

    public static void log(List<Note> voiceNotes, List<Integer> diapazonInfo, Map<Integer, Integer> duration, Map<NoteSign, Integer> amount) {
        logger.info("Diapazon: {}", diapazonInfo.get(0));
        logger.info("Bottom: " + voiceNotes.get(diapazonInfo.get(1)).sign().getNoteName());
        logger.info("Top: " + voiceNotes.get(diapazonInfo.get(2)).sign().getNoteName());
        logger.info("Amount of notes by duration:");
        duration.forEach((key, value) ->
                logger.info(key.toString() + "ms - " + value.toString()));
        logger.info("List of notes with the number of occurrences:");
        amount.forEach((key, value) ->
                logger.info(key.toString() + " - " + value.toString()));
    }

}
