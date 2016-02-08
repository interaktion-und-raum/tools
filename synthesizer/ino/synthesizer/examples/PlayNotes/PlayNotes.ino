#include "Synthesizer.h"

int MAX_NOTE_DURATION = 500;

void setup() {
  Synthesizer.preset(13);
}

void loop() {
  int mRandomNote = 50 + random(0, 5) * 7;
  int mRandomDuration = random(MAX_NOTE_DURATION);

  /* play a note. pitch is conform with midi protocol */
  Synthesizer.noteOn(mRandomNote, 127);
  delay(mRandomDuration);

  /* turn off a note. since this is a monophonic synth, it is not required to specify the actual pitch */
  Synthesizer.noteOff();
  delay(MAX_NOTE_DURATION - mRandomDuration);
}
