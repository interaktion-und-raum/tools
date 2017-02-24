/* 
 Copyright (c) 2013 Science Friction. 
 All right reserved.
 
 This library is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your optionosc1modShape_ptr) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser Public License for more details.
 
 You should have received a copy of the GNU Lesser Public License
 along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 
 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 + author: Jakob Bak
 + contact: j.bak@ciid.dk
 */

#ifndef SYNTHESIZER_H
#define	SYNTHESIZER_H

#include <Arduino.h>
#include <Math.h>
#include <IntervalTimer.h>

class CFOSynthesizer {
public:

    CFOSynthesizer() {
        init();
    }

    static const int MAX_ENV_GAIN = 65535;
    static const int SAMPLE_RATE = 48000;
    static const uint8_t DAC_CS = 10; // Digital 10

    // Constants for bitvalues for DAC output A and B 
    static const uint8_t DAC_A = 0;
    static const uint8_t DAC_B = 1;

    // Constants for positions for control bits in dacSettings 
    static const uint8_t DAC_AB = 7;
    static const uint8_t DAC_BUF = 6;
    static const uint8_t DAC_GA = 5;
    static const uint8_t DAC_SHDN = 4;

    static const uint8_t LP6 = 0;
    static const uint8_t HP6 = 1;
    static const uint8_t BP6 = 2;
    static const uint8_t LP24 = 6;
    static const uint8_t HP24 = 4;
    static const uint8_t BP24 = 5;
    static const uint8_t MOOG = 3;

    static const uint8_t THRU = 7;

    static const uint8_t PRESET_SIZE = 128;
    static const uint8_t NUM_PRESETS = 48;

    static const uint8_t SYSTEM_CALL = 0;
    static const uint8_t PRESET_RECALL = 1;

    static const uint8_t IS_12_BIT = 3;
    static const uint8_t CUTOFF = 4;
    static const uint8_t ZERO_HZ_FM = 5;
    static const uint8_t FM_OCTAVES = 6;
    static const uint8_t RESONANCE_DECLARED_TWICE = 7;
    static const uint8_t PORTAMENTO = 8;
    static const uint8_t FILTER_TYPE = 9;

    static const uint8_t LFO1 = 10;
    static const uint8_t SEMITONE1 = 11;
    static const uint8_t DETUNE1 = 12;
    static const uint8_t GAIN1 = 13;
    static const uint8_t WAVEFORM1 = 14;
    static const uint8_t FM1 = 15;
    static const uint8_t FM1_OCTAVES = 16;
    static const uint8_t FM1_SOURCE = 17;
    static const uint8_t FM1_SHAPE = 18;
    static const uint8_t FREQUENCY1 = 19;

    static const uint8_t LFO2 = 20;
    static const uint8_t SEMITONE2 = 21;
    static const uint8_t DETUNE2 = 22;
    static const uint8_t GAIN2 = 23;
    static const uint8_t WAVEFORM2 = 24;
    static const uint8_t FM2 = 25;
    static const uint8_t FM2_OCTAVES = 26;
    static const uint8_t FM2_SOURCE = 27;
    static const uint8_t FM2_SHAPE = 28;
    static const uint8_t FREQUENCY2 = 29;

    static const uint8_t LFO3 = 30;
    static const uint8_t SEMITONE3 = 31;
    static const uint8_t DETUNE3 = 32;
    static const uint8_t GAIN3 = 33;
    static const uint8_t WAVEFORM3 = 34;
    static const uint8_t FM3 = 35;
    static const uint8_t FM3_OCTAVES = 36;
    static const uint8_t FM3_SOURCE = 37;
    static const uint8_t FM3_SHAPE = 38;
    static const uint8_t FREQUENCY3 = 39;

    static const uint8_t SONG_PART = 40;
    static const uint8_t SONG_KEY = 41; // essentially offset of MIDI note number
    static const uint8_t SONG_SCALE = 42; // minor, major, etc

    static const uint8_t CUTOFF_MOD_AMOUNT = 70;
    static const uint8_t CUTOFF_MOD_DIRECTION = 71;
    static const uint8_t CUTOFF_SOURCE = 72;
    static const uint8_t CUTOFF_SHAPE = 73;
    static const uint8_t RESONANCE_MOD_AMOUNT = 74;
    static const uint8_t RESONANCE_MOD_DIRECTION = 75;
    static const uint8_t RESONANCE_SOURCE = 76;
    static const uint8_t RESONANCE_SHAPE = 77;
    static const uint8_t CUTOFF_FREQUENCY = 78;
    static const uint8_t RESONANCE = 79;

    static const uint8_t ENV0_VELOCITY = 102;
    static const uint8_t ENV0_ENABLE = 103;
    static const uint8_t ENV0_ATTACK = 104;
    static const uint8_t ENV0_DECAY = 105;
    static const uint8_t ENV0_SUSTAIN = 106;
    static const uint8_t ENV0_RELEASE = 107;

    static const uint8_t ENV1_VELOCITY = 112;
    static const uint8_t ENV1_ENABLE = 113;
    static const uint8_t ENV1_ATTACK = 114;
    static const uint8_t ENV1_DECAY = 115;
    static const uint8_t ENV1_SUSTAIN = 116;
    static const uint8_t ENV1_RELEASE = 117;

    static const uint8_t ENV2_VELOCITY = 122;
    static const uint8_t ENV2_ENABLE = 123;
    static const uint8_t ENV2_ATTACK = 124;
    static const uint8_t ENV2_DECAY = 125;
    static const uint8_t ENV2_SUSTAIN = 126;
    static const uint8_t ENV2_RELEASE = 127;

    // PRESETS
    void preset(uint8_t p);
    void controller(uint8_t number, uint8_t value);
    void setParameters(int pParameters[]);

    void envelope1();
    void envelope2();
    void envelopeRC();
    void amplifier();
    void sendToDAC(); // sending both sound and cutoff
    void output2DAC(); // sending only sound
    void output2T3DAC(); // sending sample to Teensy3.1 DAC on pin 14

    // FILTER FUNCTIONS
    void filter();
    void filterLP6dB();
    void filterHP6dB();
    void filterLP24dB();
    void filterHP24dB();
    void filterMoogLadder();
    void setCutoff(uint16_t c);
    //	void setResonance(uint8_t res);
    void setResonance(uint32_t res);
    void setFilterType(uint8_t type);
    void setCutoffModAmount(int32_t amount);
    void setCutoffModDirection(int32_t direction);
    void setCutoffModSource(uint8_t source);
    void setResonanceModSource(uint8_t source);
    void setCutoffModShape(uint8_t shape);
    void setResonanceModShape(uint8_t shape);

    // FREQUENCY AND DETUNE FUNCTIONS
    void setFrequency(float frequency);
    void setFrequency1(float frequency1);
    void setFrequency2(float frequency2);
    void setFrequency3(float frequency3);
    void setSemitone1(int8_t semi);
    void setSemitone2(int8_t semi);
    void setSemitone3(int8_t semi);
    void setDetune(float detune);
    void setDetune1(float detune);
    void setDetune2(float detune);
    void setDetune3(float detune);
    void setOsc1LFO(bool lfo);
    void setOsc2LFO(bool lfo);
    void setOsc3LFO(bool lfo);
    void setFM1(uint8_t fm);
    void setFM2(uint8_t fm);
    void setFM3(uint8_t fm);
    void setFMoctaves(uint8_t octs); // THIS SHOULD PROBABLY BE CALLED SOMETHING ELSE
    void setFM1octaves(uint8_t octs);
    void setFM2octaves(uint8_t octs);
    void setFM3octaves(uint8_t octs);
    void setFM1Source(uint8_t source);
    void setFM2Source(uint8_t source);
    void setFM3Source(uint8_t source);
    void setFM1Shape(uint8_t shape);
    void setFM2Shape(uint8_t shape);
    void setFM3Shape(uint8_t shape);
    void fmToZeroHertz(bool); // THIS SHOULD PROBABLY BE CALLED SOMETHING ELSE
    void pitchBend(float b); // NOT IMPLEMENTED
    void setPortamento(int32_t port);
    void set12bit(bool b);

    // WAVEFORM FUNCTIONS
    void setWaveform(uint16_t waveForm); // JUST FOR 8bit WAVEFORMS
    void setWaveform1(uint16_t waveForm); //
    void setWaveform2(uint16_t waveForm); //
    void setWaveform3(uint16_t waveForm); //

    // GAIN FUNCTIONS
    void setGain(float value); // 0.0 - 1.0          
    void setGain1(float value); // 0.0 - 1.0         
    void setGain2(float value); // 0.0 - 1.0         
    void setGain3(float value); // 0.0 - 1.0         
    float getGain(); // 0.0 - 1.0         
    float getGain1(); // 0.0 - 1.0         
    float getGain2(); // 0.0 - 1.0         
    float getGain3(); // 0.0 - 1.0         

    // NOTE FUNCTIONS
    void noteOn(uint8_t note, uint8_t vel); // 0 - 127
    void noteOn(uint8_t note); // 0 - 127
    void noteOff(uint8_t note); // 0 - 127
    void noteOff();

    void allNotesOff() {
        noteOff();
    }
    float getNoteFrequency(uint8_t note); // 0 - 127

    // ENVELOPE FUNCTIONS
    void enableEnvelope1();
    void disableEnvelope1();
    void setEnv1Stage(uint8_t stage1); // 0 - 4
    void setEnv1Attack(uint8_t att); // 0 - 127              
    void setEnv1Decay(uint8_t dec); // 0 - 127               
    void setEnv1Sustain(uint8_t sus); // 0 - 127             
    void setEnv1Release(uint8_t rel); // 0 - 127             
    void setEnv1VelSustain(uint8_t vel); // 0 - 127
    void setEnv1VelPeak(uint8_t vel); // 0 - 127

    void enableEnvelope2();
    void disableEnvelope2();
    void setEnv2Stage(uint8_t stage); // 0 - 4
    void setEnv2Attack(uint8_t att); // 0 - 127              
    void setEnv2Decay(uint8_t dec); // 0 - 127               
    void setEnv2Sustain(uint8_t sus); // 0 - 127             
    void setEnv2Release(uint8_t rel); // 0 - 127             
    void setEnv2VelSustain(uint8_t vel); // 0 - 127
    void setEnv2VelPeak(uint8_t vel); // 0 - 127

    void synth_isr_C();

private:

    static const uint64_t BIT_16 = 65536;
    static const uint64_t BIT_32 = 4294967296;
    static const uint64_t PERIOD_MAX = BIT_32;

    bool lowpass;
    bool highpass;
    bool lowpass24dB;
    bool highpass24dB;
    bool moogLadder;

    bool osc1LFO;
    bool osc2LFO;
    bool osc3LFO;

    bool is12bit;

    int32_t oscil1;
    int32_t oscil2;
    int32_t oscil3;
    int32_t lastOscil1;
    int32_t lastOscil2;
    int32_t lastOscil3;
    int64_t integralOfOscil1;
    int64_t integralOfOscil2;
    int64_t integralOfOscil3;
    int64_t derivativeOfOscil1;
    int64_t derivativeOfOscil2;
    int64_t derivativeOfOscil3;

    // TIMER VARIABLES
    uint32_t sampleRate;

    // WAVEFORM VARIABLES
    uint16_t waveForm1;
    uint16_t waveForm2;
    uint16_t waveForm3;
    uint16_t waveForm;
    uint16_t waveform;
    int64_t waveformVector[5];
    uint16_t phaseDistortion1;
    uint16_t phaseDistortion2;
    uint16_t phaseDistortion3;
    uint32_t waveformPosition1;
    uint32_t waveformPosition2;
    uint32_t waveformPosition3;
    //    int16_t waveformVector[8];
    bool sine;
    bool saw;
    bool square;

    // FREQUENCY VARIABLES
    uint16_t frequency16bit;
    float frequency;
    float frequency1;
    float frequency2;
    float frequency3;
    float semi1;
    float semi2;
    float semi3;
    float detune1;
    float detune2;
    float detune3;
    float bend;

    // OSCILLATOR VARIABLES
    int32_t period1;
    int32_t period2;
    int32_t period3;
    int32_t portamento;
    volatile int32_t dPhase1;
    volatile int32_t dPhase2;
    volatile int32_t dPhase3;
    uint32_t accumulator1;
    uint32_t accumulator2;
    uint32_t accumulator3;
    int32_t vectorAccumulator1;
    int32_t vectorAccumulator2;
    int32_t vectorAccumulator3;
    int32_t index1;
    int32_t index2;
    int32_t index3;
    int32_t fraction1;
    int32_t fraction2;
    int32_t fraction3;
    int64_t modulator1;
    int64_t modulator2;
    int64_t modulator3;
    int32_t fullSignal;
    int32_t invertSignal;
    int32_t noSignal;
    int32_t *osc1modSource_ptr;
    int32_t *osc2modSource_ptr;
    int32_t *osc3modSource_ptr;
    int32_t *amp_modSource_ptr;
    int32_t *osc1modShape_ptr;
    int32_t *osc2modShape_ptr;
    int32_t *osc3modShape_ptr;
    int32_t *amp_modShape_ptr;
    int32_t zeroFM;
    int32_t fmAmount1;
    int32_t fmAmount2;
    int32_t fmAmount3;
    int32_t fmOctaves1;
    int32_t fmOctaves2;
    int32_t fmOctaves3;

    int32_t gain;
    int32_t gain1;
    int32_t gain2;
    int32_t gain3;

    // FILTER VARIABLES

    int64_t a0;
    int64_t a1;
    int64_t a2;
    int64_t a3;
    int64_t a4;

    int64_t b0;
    int64_t b1;
    int64_t b2;
    int64_t b3;
    int64_t b4;

    int64_t x0;
    int64_t x1;
    int64_t x2;
    int64_t x3;
    int64_t x4;

    int64_t y0;
    int64_t y1;
    int64_t y2;
    int64_t y3;
    int64_t y4;

    int64_t xNew;
    int64_t xOld;
    int64_t yNew;
    int64_t yOld;
    int64_t feedbackSample;

    volatile int64_t u;
    int64_t g;
    int64_t gg;
    int64_t ggg;
    int64_t G;
    int64_t Gstage;
    volatile int64_t S;

    volatile int64_t k;
    int64_t v1;
    int64_t v2;
    int64_t v3;
    int64_t v4;
    int64_t z1;
    int64_t z2;
    int64_t z3;
    int64_t z4;

    uint16_t cutoff;
    uint32_t resonance;

    int32_t cutoffModAmount;
    int32_t cutoffModDirection;
    int32_t *cutoffModSource_ptr;
    int32_t *resonanceModSource_ptr;
    int32_t *cutoffModShape_ptr;
    int32_t *resonanceModShape_ptr;


    int64_t lastSampleOutLP;
    int64_t lastSampleInLP;
    int64_t sampleOutLP;
    int64_t sampleInLP;
    int64_t lastSampleOutHP;
    int64_t lastSampleInHP;
    int64_t sampleOutHP;
    int64_t sampleInHP;

    // ENVELOPE VARIABLES
    bool envelopeOn1;
    int32_t env1;
    int32_t env1Stage;
    int32_t attack1;
    int32_t decay1;
    int32_t sustain1;
    int32_t release1;
    int32_t velSustain1;
    int32_t velPeak1;
    int32_t envTarget;

    bool envelopeOn2;
    int32_t env2;
    int32_t env2Stage;
    int32_t attack2;
    int32_t decay2;
    int32_t sustain2;
    int32_t release2;
    int32_t velSustain2;
    int32_t velPeak2;

    // NOTE VARIABLE
    uint8_t notePlayed;

    // final sample that goes to the DAC
    volatile int64_t sample;

    // the two bytes that go to the DAC over SPI for VCF and VCA
    volatile uint8_t dacSPIA0;
    volatile uint8_t dacSPIA1;
    volatile uint8_t dacSPIB0;
    volatile uint8_t dacSPIB1;
    volatile uint8_t dacSetA;
    volatile uint8_t dacSetB;

    // INITIALIZER
    void init();
    void generateFilterCoefficientsMoogLadder();

    // AUDIO INTERRUPT SERVICE ROUTINE
    void synthInterrupt8bit();
    void synthInterrupt8bitFM();
    void synthInterrupt12bitSine();
    void synthInterrupt12bitSineFM();
    void synthInterrupt12bitSawFM();
    void phaseDistortionOscillator();

    inline uint8_t clamp127(uint8_t i) {
        if (i > 127) {
            i = 127;
        } else if (i < 0) {
            i = 0;
        }
        return i;
    }
};

extern CFOSynthesizer Synthesizer;

#endif	/* SYNTHESIZER_H */

