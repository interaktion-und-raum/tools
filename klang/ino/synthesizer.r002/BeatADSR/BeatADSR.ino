#include "synthesizer.h"
#include "Oscil16.h"
#include "tables/sin2048_int16.h"
#include "Beat.h"

class Foobar : public BeatListener {
  public:
    void event(int pBeat) {
      Serial.print("listener beat: ");
      Serial.println(pBeat);
    }
};

Oscil16 <SIN2048_NUM_CELLS, AUDIO_RATE> mSin(SIN2048_DATA);

int mSample;
Beat mBeat;
Foobar f;

const int LED_PIN = 13;
bool mState = 0;

void setup() {
  pinMode(LED_PIN, OUTPUT);
  synth_setup();
  mSin.setFreq(220);
  
  Serial.begin(9600);
  delay(1000);
  Serial.println("### setup()");

  mBeat.setCallback(beat);
  mBeat.setCallback(&f);
  mBeat.setBPM(120 * 4);
  mBeat.start();
}

void loop() {
  mBeat.update();
}

void beat(int pBeat) {
  Serial.print("beat: ");
  Serial.println(pBeat);
  mSin.setFreq(55 * (int)random(5));
  mState = !mState;
  digitalWrite(LED_PIN, mState);
}

int updateAudio() {
  mSample = mSin.next();
  return mSample;
}
