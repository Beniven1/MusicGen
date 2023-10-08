//https://github.com/Beniven1/MusicGen
import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class SongGenerator {
    private static final int DEFAULT_VELOCITY = 64;
    private static final int DEFAULT_TEMPO = 120;
    private static final int TICKS_PER_BEAT = 120;

    public static void generateSong(int length, String outputFilePath) {
        try {
            Sequence sequence = new Sequence(Sequence.PPQ, TICKS_PER_BEAT);
            Track track = sequence.createTrack();

            int previousNote = 60; // Starting note

            for (int i = 0; i < length; i++) {
                int note = previousNote + (int) (Math.random() * 5) - 2; // Random note within a smaller range
                int duration = (int) (Math.random() * 100) + 100; // Random duration between 100 and 200

                MidiEvent noteOnEvent = createNoteOnEvent(note, DEFAULT_VELOCITY, i * TICKS_PER_BEAT);
                MidiEvent noteOffEvent = createNoteOffEvent(note, i * TICKS_PER_BEAT + duration);

                track.add(noteOnEvent);
                track.add(noteOffEvent);

                previousNote = note; // Remember the previous note for the next iteration
            }

            // Set tempo
            MetaMessage tempoMessage = new MetaMessage();
            int microsecondsPerMinute = 60000000;
            int tempoValue = microsecondsPerMinute / DEFAULT_TEMPO;
            tempoMessage.setMessage(0x51, new byte[]{(byte) (tempoValue >> 16), (byte) (tempoValue >> 8), (byte) tempoValue}, 3);
            track.add(new MidiEvent(tempoMessage, length * TICKS_PER_BEAT));

            // Write MIDI sequence to file
            File outputFile = new File(outputFilePath);
            MidiSystem.write(sequence, 1, outputFile);
        } catch (InvalidMidiDataException | IOException e) {
            e.printStackTrace();
        }
    }

    private static MidiEvent createNoteOnEvent(int note, int velocity, long tick) throws InvalidMidiDataException {
        ShortMessage noteOnMessage = new ShortMessage();
        noteOnMessage.setMessage(ShortMessage.NOTE_ON, 0, note, velocity);
        return new MidiEvent(noteOnMessage, tick);
    }

    private static MidiEvent createNoteOffEvent(int note, long tick) throws InvalidMidiDataException {
        ShortMessage noteOffMessage = new ShortMessage();
        noteOffMessage.setMessage(ShortMessage.NOTE_OFF, 0, note, 0);
        return new MidiEvent(noteOffMessage, tick);
    }

    public static void main(String[] args) {
        generateSong(100, "output.mid"); // Generate a song with 10 notes and save it as "output.mid"
    }
}