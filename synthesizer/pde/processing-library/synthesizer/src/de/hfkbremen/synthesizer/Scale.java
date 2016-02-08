package de.hfkbremen.synthesizer;

public class Scale {

    public static final int[] HALF_TONE = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    public static final int[] CHROMATIC = HALF_TONE;

    public static final int[] FIFTH = {0, 7};

    public static final int[] MINOR = {0, 2, 3, 5, 7, 8, 10};

    public static final int[] MAJOR = {0, 2, 4, 5, 7, 9, 11};

    public static final int[] MINOR_CHORD = {0, 3, 7};

    public static final int[] MAJOR_CHORD = {0, 4, 7};

    public static final int[] MINOR_CHORD_7 = {0, 3, 7, 11};

    public static final int[] MAJOR_CHORD_7 = {0, 4, 7, 11};

    public static final int[] MINOR_PENTATONIC = {0, 3, 5, 7, 10};

    public static final int[] MAJOR_PENTATONIC = {0, 4, 5, 7, 11};

    public static final int[] OCTAVE = {0};

    public static final int[] DIMINISHED = {0, 3, 6, 9};

    public static final int[] FIBONACCI = {1 - 1, 1 - 1, 2 - 1, 3 - 1, 5 - 1, 8 - 1};

    public static int note(int[] pScale, int pBaseNote, int pNoteStepOffset) {
        if (pNoteStepOffset >= 0) {
            final int i = pNoteStepOffset % pScale.length;
            final int mOctave = pNoteStepOffset / pScale.length;
            return pBaseNote + mOctave * 12 + pScale[i];
        } else {
            /**
             * @TODO this looks ridiculous
             */
            final int mOctave = (int) Math.ceil(Math.abs((float) pNoteStepOffset / pScale.length)) - 1;
            final int i = ((pScale.length - 1) - Math.abs((pNoteStepOffset + 1) % pScale.length));
            final int mOffset = 12 - pScale[i];
            return pBaseNote - mOffset - mOctave * 12;
        }
    }

    public static final int NOTE_A0 = 9;

    public static final int NOTE_A1 = 21;

    public static final int NOTE_A2 = 33;

    public static final int NOTE_A3 = 45;

    public static final int NOTE_A4 = 57;

    public static final int NOTE_A5 = 69;

    public static final int NOTE_A6 = 81;

    public static final int NOTE_A7 = 93;

    public static final int NOTE_A8 = 105;

    public static final int NOTE_A9 = 117;

    public static final int NOTE_C1 = 12;

    public static final int NOTE_C2 = 24;

    public static final int NOTE_C3 = 36;

    public static final int NOTE_C4 = 48;

    public static final int NOTE_C5 = 60;

    public static final int NOTE_C6 = 72;

    public static final int NOTE_C7 = 84;

    public static final int NOTE_C8 = 96;

    public static final int NOTE_C9 = 108;

    public static final int NOTE_C10 = 120;

}
