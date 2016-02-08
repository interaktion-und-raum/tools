#include "Synthesizer.h"

int mPresets;

void setup() {
  mPresets = 0;
}

void loop() {
  /* there is a series of 48 presets */
  Synthesizer.preset(mPresets);
  mPresets++;
  mPresets %= Synthesizer.NUM_PRESETS;

  /* play a note. pitch is conform with midi protocol */
  Synthesizer.noteOn(57, 100);
  delay(200);

  /* turn off a note. since this is a monophonic synth, it is not required to specify the actual pitch */
  Synthesizer.noteOff();
  delay(200);
}
