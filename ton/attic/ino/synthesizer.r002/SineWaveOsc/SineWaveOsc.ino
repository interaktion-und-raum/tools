#include "synthesizer.h"
#include "Oscil16.h"
#include "tables/sin2048_int16.h"

Oscil16 <SIN2048_NUM_CELLS, AUDIO_RATE> mSin(SIN2048_DATA);

uint32_t mCounter = 0;
int mSample;

void setup() {
  synth_setup();
  mSin.setFreq(220);
  Serial.begin(9600);
}

void loop() {
  Serial.println(mSample);
  delay(50);
}

int updateAudio() {
  mCounter++;
  if (mCounter > (AUDIO_RATE >> 3)) {
    mCounter = 0;
    mSin.setFreq(55 * (int)random(5));
  }

  mSample = mSin.next();
  return mSample;
}
