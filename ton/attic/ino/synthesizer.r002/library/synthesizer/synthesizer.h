#ifndef SYNTHESIZER_H_
#define SYNTHESIZER_H_

/**
   # TODO

   - add support for ATmega328p + MCP4921
   - look at http://makezine.com/projects/make-35/advanced-arduino-sound-synthesis/

*/

#if defined(__MK20DX128__) || defined(__MK20DX256__) // TEENSY3
#include "IntervalTimer.h"
#else
// support for ATmega328p + MCP4921
#endif

#define AUDIO_BIAS ((uint16_t) 2048)
#define AUDIO_SCALER 0
#define AUDIO_CHANNEL_1_PIN A14
#define AUDIO_RATE 48000

int updateAudio();

void synth_isr();

/* --- ISR --- */

#if defined(__MK20DX128__) || defined(__MK20DX256__) // TEENSY3
IntervalTimer timer;

void synth_isr() {
    const unsigned int mData = ((updateAudio() << AUDIO_SCALER) + AUDIO_BIAS);
    analogWrite(AUDIO_CHANNEL_1_PIN, mData);
}
#else
// ATmega328p + DAC
#endif

/* --- */

void synth_setup() {
    analogWriteResolution(12);
    timer.begin(synth_isr, 1000000UL / AUDIO_RATE);
}

#endif // SYNTHESIZER_H_
