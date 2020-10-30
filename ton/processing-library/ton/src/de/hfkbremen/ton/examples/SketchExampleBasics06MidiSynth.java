package de.hfkbremen.ton.examples;

import de.hfkbremen.ton.SynthUtil;
import de.hfkbremen.ton.Ton;
import processing.core.PApplet;

/**
 * this examples shows how to control a midi instrument. make sure to set up the midi configuration properly in system
 * control.
 */
public class SketchExampleBasics06MidiSynth extends PApplet {

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        SynthUtil.dumpMidiOutputDevices();
        Ton.init("midi", "Bus 1"); // name of an available midi out device
    }

    public void draw() {
        background(Ton.isPlaying() ? 255 : 0);
    }

    public void mousePressed() {
        /* `instrument` in this context is equivalent to *MIDI channels*. this also means that sound characteristics
        ( e.g `osc_type` ) are not available. */
        Ton.instrument(mouseX > width / 2.0 ? 1 : 0);
        int mNote = 45 + (int) random(0, 12);
        Ton.noteOn(mNote, 127);
    }

    public void mouseReleased() {
        Ton.noteOff();
    }

    public static void main(String[] args) {
        PApplet.main(SketchExampleBasics06MidiSynth.class.getName());
    }

}
