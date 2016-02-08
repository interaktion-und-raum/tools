#include "Synthesizer.h"

void setup() {
  /* to set custum parameters an array of 128 values must be passed to `Synthesizer.setParameters()` */
  int mPresets[128] = {
    26, 10, 0, 0, 17, 1, 127, 0, 0, 0, 0, 64, 64, 127, 4, 0, 0, 0, 0, 0, 0, 71, 70, 127, 4, 0, 0, 0, 0, 0, 0, 76, 61, 127, 4, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 126, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 12, 64, 127, 64, 0, 0, 0, 0, 0, 1, 0, 46, 0, 64,
  };
  Synthesizer.setParameters(mPresets);
}

void loop() {
  Synthesizer.noteOn(millis() % 12 + 40, 100);
  delay(25);
  Synthesizer.noteOff();
  delay(100);
}
