package de.hfkbremen.ton.examples;

import de.hfkbremen.ton.SynthUtil;
import de.hfkbremen.ton.Synthesizer;
import processing.core.PApplet;

/**
 * this examples shows how to control a midi instrument. make sure to set up the midi configuration properly in system
 * control.
 */
public class SketchExample11MidiSynth extends PApplet {

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        SynthUtil.dumpMidiOutputDevices();
        Synthesizer.init("midi", "Bus 1"); // name of an available midi out device
    }

    public void draw() {
        background(Synthesizer.isPlaying() ? 255 : 0);
    }

    public void mousePressed() {
        /* `instrument` in this context is equivalent to *MIDI channels*. this also means that sound characteristics
        ( e.g `osc_type` ) are not available. */
        Synthesizer.instrument(mouseX > width / 2.0 ? 1 : 0);
        int mNote = 45 + (int) random(0, 12);
        Synthesizer.noteOn(mNote, 127);
    }

    public void mouseReleased() {
        Synthesizer.noteOff();
    }

    public static void main(String[] args) {
        PApplet.main(SketchExample11MidiSynth.class.getName());
    }

}
