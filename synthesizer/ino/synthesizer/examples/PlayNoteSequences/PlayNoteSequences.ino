#include "Synthesizer.h"

static const int STEP_DURATION_MILLISECS = 1000 / ( 120 * 4 / 60 ); // for 120 bpm 
int mStepCounter = 0;
int mSequence[16] = {
  45, 45, 45, 45,
  45, 57, 45, 57,
  57, 69, 57, 69,
  45, 57, 45, 56,
};

void setup() {
  Synthesizer.preset(7);
}

void loop() {
  Synthesizer.noteOn(mSequence[mStepCounter], 127);
  delay(STEP_DURATION_MILLISECS / 2);

  Synthesizer.noteOff();
  delay(STEP_DURATION_MILLISECS / 2);

  mStepCounter++;
  mStepCounter %= 16;
}
