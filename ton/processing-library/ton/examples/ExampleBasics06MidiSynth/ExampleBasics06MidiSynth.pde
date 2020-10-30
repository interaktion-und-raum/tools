import de.hfkbremen.ton.*; 
import controlP5.*; 
import ddf.minim.*; 
import com.jsyn.unitgen.*; 


void settings() {
    size(640, 480);
}
void setup() {
    SynthUtil.dumpMidiOutputDevices();
    Ton.init("midi", "Bus 1"); // name of an available midi out device
}
void draw() {
    background(Ton.isPlaying() ? 255 : 0);
}
void mousePressed() {
    /* `instrument` in this context is equivalent to *MIDI channels*. this also means that sound characteristics
    ( e.g `osc_type` ) are not available. */
    Ton.instrument(mouseX > width / 2.0 ? 1 : 0);
    int mNote = 45 + (int) random(0, 12);
    Ton.noteOn(mNote, 127);
}
void mouseReleased() {
    Ton.noteOff();
}
