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

#include "Synthesizer.h"

IntervalTimer synthTimer;
CFOSynthesizer Synthesizer;

int64_t filterSamplesLP24dB[4];
int64_t filterSamplesHP24dB[8];
int64_t filterSamplesMoogLadder[4];

float filterCoefficientsMoogLadderFloat[8][256];

const uint8_t programPresets[] = {
#include <Presets.h>
};

const uint16_t sineTable[] = {
#include <FrictionSineTable16bitHex.inc>
};

const uint16_t waveTable[] = {
#include <FrictionWaveTable.inc>
};

// Table of MIDI note values to frequency in Hertz
const float hertzTable[] = {
#include <FrictionHertzTable.inc>	
};

const int64_t filterCoefficient[] = {
#include <filterCoefficients_1poleLP.inc>
};

const float fcMoog[] = {
#include <filterCutoffFrequenciesMoogLadder.inc>
};

const int64_t filterCoefficientsMoogLadder[] = {
#include <filterCoefficientsMoogLadder.inc>
};

// Used in the functions that set the envelope timing
const uint32_t envTimeTable[] = {
#include <envTimeTable.inc>
};
const float semitoneTable[] = {
#include <semitoneTable.inc>
};
const uint32_t portamentoTimeTable[] = {
#include <portamentoTimeTable.inc>
};

//////////////////////////////////////////////////////////
//
// SYNTH INTERRUPT
//
//////////////////////////////////////////////////////////

void synth_isr(void) {
    Synthesizer.synth_isr_C();
}

void CFOSynthesizer::synth_isr_C() {
    output2T3DAC();
    envelope1();
    envelope2();
    if (is12bit) {
        synthInterrupt12bitSineFM();
    } else {
        synthInterrupt8bitFM();
    }

    amplifier();

    if (lowpass) filterLP6dB();
    if (highpass) filterHP6dB();
    if (lowpass24dB) filterLP24dB();
    if (highpass24dB) filterHP24dB();
    if (moogLadder) filterMoogLadder();
}

/////////////////////////////////////////////////////////
//
//	8 BIT OSCILLATOR - WAVETABLE - 16 WAVEFORMS
//
/////////////////////////////////////////////////////////

void CFOSynthesizer::synthInterrupt8bitFM() {

    dPhase1 = dPhase1 + (period1 - dPhase1) / portamento;
    modulator1 = (fmAmount1 * fmOctaves1 * (*osc1modSource_ptr)) >> 10;
    modulator1 = (modulator1 * (*osc1modShape_ptr)) >> 16;
    modulator1 = (modulator1 * int64_t(dPhase1)) >> 16;
    modulator1 = (modulator1 >> ((modulator1 >> 31) & zeroFM));
    accumulator1 = accumulator1 + dPhase1 + modulator1;
    index1 = accumulator1 >> 24;
    oscil1 = waveTable[index1 + waveForm1];
    oscil1 -= 128;
    oscil1 <<= 8;
    sample = (oscil1 * gain1);

    dPhase2 = dPhase2 + (period2 - dPhase2) / portamento;
    modulator2 = (fmAmount2 * fmOctaves2 * (*osc2modSource_ptr)) >> 10;
    modulator2 = (modulator2 * (*osc2modShape_ptr)) >> 16;
    modulator2 = (modulator2 * int64_t(dPhase2)) >> 16;
    modulator2 = (modulator2 >> ((modulator2 >> 31) & zeroFM));
    accumulator2 = accumulator2 + dPhase2 + modulator2;
    index2 = accumulator2 >> 24;
    oscil2 = waveTable[index2 + waveForm2];
    oscil2 -= 128;
    oscil2 <<= 8;
    sample += (oscil2 * gain2);

    dPhase3 = dPhase3 + (period3 - dPhase3) / portamento;
    modulator3 = (fmAmount3 * fmOctaves3 * (*osc3modSource_ptr)) >> 10;
    modulator3 = (modulator3 * (*osc3modShape_ptr)) >> 16;
    modulator3 = (modulator3 * int64_t(dPhase3)) >> 16;
    modulator3 = (modulator3 >> ((modulator3 >> 31) & zeroFM));
    accumulator3 = accumulator3 + dPhase3 + modulator3;
    index3 = accumulator3 >> 24;
    oscil3 = waveTable[index3 + waveForm3];
    oscil3 -= 128;
    oscil3 <<= 8;
    sample += (oscil3 * gain3);

    sample >>= 18;
}

/////////////////////////////////////////////////////////
//
//	12 BIT OSCILLATOR - SINETABLE
//
/////////////////////////////////////////////////////////

void CFOSynthesizer::synthInterrupt12bitSineFM() {

    dPhase1 = dPhase1 + (period1 - dPhase1) / portamento;
    modulator1 = (fmAmount1 * fmOctaves1 * (*osc1modSource_ptr)) >> 10;
    modulator1 = (modulator1 * (*osc1modShape_ptr)) >> 16;
    modulator1 = (modulator1 * int64_t(dPhase1)) >> 16;
    modulator1 = (modulator1 >> ((modulator1 >> 31) & zeroFM));
    accumulator1 = accumulator1 + dPhase1 + modulator1;
    index1 = accumulator1 >> 20;
    oscil1 = sineTable[index1];
    index1 = accumulator1 >> 20;
    oscil1 -= 32768;
    sample = (oscil1 * gain1);

    dPhase2 = dPhase2 + (period2 - dPhase2) / portamento;
    modulator2 = (fmAmount2 * fmOctaves2 * (*osc2modSource_ptr)) >> 10;
    modulator2 = (modulator2 * (*osc2modShape_ptr)) >> 16;
    modulator2 = (modulator2 * int64_t(dPhase2)) >> 16;
    modulator2 = (modulator2 >> ((modulator2 >> 31) & zeroFM));
    accumulator2 = accumulator2 + dPhase2 + modulator2;
    index2 = accumulator2 >> 20;
    oscil2 = sineTable[index2];
    oscil2 -= 32768;
    sample += (oscil2 * gain2);

    dPhase3 = dPhase3 + (period3 - dPhase3) / portamento;
    modulator3 = (fmAmount3 * fmOctaves3 * (*osc3modSource_ptr)) >> 10;
    modulator3 = (modulator3 * (*osc3modShape_ptr)) >> 16;
    modulator3 = (modulator3 * int64_t(dPhase3)) >> 16;
    modulator3 = (modulator3 >> ((modulator3 >> 31) & zeroFM));
    accumulator3 = accumulator3 + dPhase3 + modulator3;
    index3 = accumulator3 >> 20;
    oscil3 = sineTable[index3];
    oscil3 -= 32768;
    sample += (oscil3 * gain3);

    sample >>= 18;
}

void CFOSynthesizer::synthInterrupt12bitSawFM() {

    dPhase1 = dPhase1 + (period1 - dPhase1) / portamento;
    modulator1 = (fmAmount1 * fmOctaves1 * (*osc1modSource_ptr)) >> 10;
    modulator1 = (modulator1 * (*osc1modShape_ptr)) >> 16;
    modulator1 = (modulator1 * int64_t(dPhase1)) >> 16;
    modulator1 = (modulator1 >> ((modulator1 >> 31) & zeroFM));
    accumulator1 = accumulator1 + dPhase1 + modulator1;
    //	index1 = accumulator1 >> 20;
    //	oscil1 = sineTable[index1];
    oscil1 = accumulator1 >> 16;
    oscil1 -= 32768;
    sample = (oscil1 * gain1);

    dPhase2 = dPhase2 + (period2 - dPhase2) / portamento;
    modulator2 = (fmAmount2 * fmOctaves2 * (*osc2modSource_ptr)) >> 10;
    modulator2 = (modulator2 * (*osc2modShape_ptr)) >> 16;
    modulator2 = (modulator2 * int64_t(dPhase2)) >> 16;
    modulator2 = (modulator2 >> ((modulator2 >> 31) & zeroFM));
    accumulator2 = accumulator2 + dPhase2 + modulator2;
    //	index2 = accumulator2 >> 20;
    //	oscil2 = sineTable[index2];
    oscil2 = accumulator2 >> 16;
    oscil2 -= 32768;
    sample += (oscil2 * gain2);

    dPhase3 = dPhase3 + (period3 - dPhase3) / portamento;
    modulator3 = (fmAmount3 * fmOctaves3 * (*osc3modSource_ptr)) >> 10;
    modulator3 = (modulator3 * (*osc3modShape_ptr)) >> 16;
    modulator3 = (modulator3 * int64_t(dPhase3)) >> 16;
    modulator3 = (modulator3 >> ((modulator3 >> 31) & zeroFM));
    accumulator3 = accumulator3 + dPhase3 + modulator3;
    //	index3 = accumulator3 >> 20;
    //	oscil3 = sineTable[index3];
    oscil3 = accumulator3 >> 16;
    oscil3 -= 32768;
    sample += (oscil3 * gain3);

    sample >>= 18;
}

/////////////////////////////////////////////////////////
//
//	ENVELOPES
//
/////////////////////////////////////////////////////////

void CFOSynthesizer::envelope1() {
    if (envelopeOn1) {
        // Attack
        if (env1Stage == 1) {
            env1 += 1; // to make sure the envelope increases when (MAX_ENV_GAIN-env1) is smaller than attack1
            env1 += (MAX_ENV_GAIN - env1) / attack1;
            if (velPeak1 < env1) {
                env1 = velPeak1;
                env1Stage = 2;
            }
        }// Decay
        else if (env1Stage == 2) {
            env1 += -1; // to make sure the envelope decreases when (velSustain1-env1) is smaller than decay1
            env1 += (velSustain1 - env1) / decay1;
            if (env1 < velSustain1 || MAX_ENV_GAIN < env1) {
                env1 = velSustain1;
                env1Stage = 3;
            }
        }// Sustain
        else if (env1Stage == 3) {
            env1 = velSustain1;
        }// Release
        else if (env1Stage == 4) {
            env1 += -1; // to make sure the envelope decreases when (0-env1) is smaller than release1
            env1 += (0 - env1) / release1;
            if (env1 < 0 || MAX_ENV_GAIN < env1) {
                env1 = 0;
                env1Stage = 0;
            }
        }// No gain
        else if (env1Stage == 0) {
            env1 = 0;
        }

    } else {
        env1 = 65535;
    }
}

void CFOSynthesizer::envelope2() {
    if (envelopeOn2) {
        // Attack
        if (env2Stage == 1) {
            env2 += 1; // to make sure the envelope increases when (MAX_ENV_GAIN-env2) is smaller than attack1
            env2 += (MAX_ENV_GAIN - env2) / attack2;
            if (velPeak2 < env2) {
                env2 = velPeak2;
                env2Stage = 2;
            }
        }// Decay
        else if (env2Stage == 2) {
            env2 += -1; // to make sure the envelope decreases when (velSustain2-env2) is smaller than decay2
            env2 += (velSustain2 - env2) / decay2;
            if (env2 < velSustain2 || MAX_ENV_GAIN < env2) {
                env2 = velSustain2;
                env2Stage = 3;
            }
        }// Sustain
        else if (env2Stage == 3) {
            env2 = velSustain2;
        }// Release
        else if (env2Stage == 4) {
            env2 += -1; // to make sure the envelope decreases when (0-env2) is smaller than release2
            env2 += (0 - env2) / release2;
            if (env2 < 0 || MAX_ENV_GAIN < env2) {
                env2 = 0;
                env2Stage = 0;
            }
        }// No gain
        else if (env2Stage == 0) {
            env2 = 0;
            //accumulator1 = 0;
            //accumulator2 = 0;
            //accumulator3 = 0;
        }

    } else {
        env2 = 65535;
    }
}

void CFOSynthesizer::amplifier() {
    sample = (env1 * sample) >> 16;
}

/////////////////////////////////////////////////////////
//
//	SEND SAMPLE TO DAC ON TEENSY 3.1 PIN A14
//
/////////////////////////////////////////////////////////

void CFOSynthesizer::output2T3DAC() {
    sample += 32768;
    analogWrite(A14, sample >> 4);
}

/////////////////////////////////////
//
//	INITIALIZING FUNCTION
//
/////////////////////////////////////

void CFOSynthesizer::init() {
    generateFilterCoefficientsMoogLadder();

    sampleRate = SAMPLE_RATE;
    sample = 0;
    set12bit(false);
    setPortamento(0);

    dPhase1 = 0;
    dPhase2 = 0;
    dPhase3 = 0;
    modulator1 = 0;
    modulator2 = 0;
    modulator3 = 0;
    fullSignal = 65535;
    invertSignal = -65535;
    noSignal = 0;

    osc1modSource_ptr = &oscil3;
    osc2modSource_ptr = &oscil1;
    osc3modSource_ptr = &oscil2;
    osc1modShape_ptr = &fullSignal;
    osc2modShape_ptr = &fullSignal;
    osc3modShape_ptr = &fullSignal;

    amp_modSource_ptr = &env1;
    amp_modShape_ptr = &fullSignal;

    setFM1Source(3);
    setFM2Source(1);
    setFM3Source(2);
    setFM1Shape(0);
    setFM2Shape(0);
    setFM3Shape(0);

    fmToZeroHertz(true);
    accumulator1 = 0;
    accumulator2 = 0;
    accumulator3 = 0;
    index1 = 0;
    index2 = 0;
    index3 = 0;
    oscil1 = 0;
    oscil2 = 0;
    oscil3 = 0;

    // waveform setup
    setWaveform(0);

    // frequency setup
    setFrequency(440);
    setSemitone1(0);
    setSemitone2(0);
    setSemitone3(0);
    setDetune(0);
    setOsc1LFO(false);
    setOsc2LFO(false);
    setOsc3LFO(false);

    // gain setup
    setGain(1.0f);
    setGain1(1.0f);
    setGain2(1.0f);
    setGain3(1.0f);

    // envelope setup
    setEnv1Stage(0);
    disableEnvelope1();
    env1 = 0;

    setEnv1Attack(4);
    setEnv1Decay(90);
    setEnv1Sustain(32);
    setEnv1Release(64);
    setEnv1VelSustain(0);

    setEnv2Stage(0);
    disableEnvelope2();
    env2 = 0;

    setEnv2Attack(8);
    setEnv2Decay(36);
    setEnv2Sustain(0);
    setEnv2Release(64);
    setEnv2VelSustain(0);

    //FM setup
    setFM1(0);
    setFM2(0);
    setFM3(0);
    setFMoctaves(0);

    // DAC setup
    dacSetA = 0;
    dacSetB = 0;
    dacSetA |= (DAC_A << DAC_AB) | (0 << DAC_BUF) | (1 << DAC_GA) | (1 << DAC_SHDN);
    dacSetB |= (DAC_B << DAC_AB) | (0 << DAC_BUF) | (1 << DAC_GA) | (1 << DAC_SHDN);

    analogWriteResolution(12);

    // filter setup
    setCutoff((uint16_t) BIT_16);
    //	setCutoff(BIT_16);
    setResonance(BIT_16);
    setFilterType(0);

    cutoffModSource_ptr = &env2;
    resonanceModSource_ptr = &fullSignal;
    cutoffModShape_ptr = &fullSignal;
    resonanceModShape_ptr = &fullSignal;

    setCutoffModSource(2);
    setCutoffModAmount(BIT_16);
    setCutoffModDirection(1);

    enableEnvelope1();
    enableEnvelope2();

    cli();
    synthTimer.begin(synth_isr, 1000000.0 / sampleRate);
    sei();
}

void CFOSynthesizer::generateFilterCoefficientsMoogLadder() {
    for (int i = 0; i < 256; i++) {
        float T = 1.0f / float(SAMPLE_RATE);
        float wd = 2.0f * PI * fcMoog[i];
        float wa = (2.0f / T) * tan(wd * T / 2.0f);
        //    float g = tan(wd*T/2.0f);
        float g = wa * (T / 2.0f);
        float gg = g * g;
        float ggg = g * g * g;
        float G = g * g * g * g;
        float Gstage = g / (1.0 + g);

        filterCoefficientsMoogLadderFloat[0][i] = wd;
        filterCoefficientsMoogLadderFloat[1][i] = wa;
        filterCoefficientsMoogLadderFloat[2][i] = g;
        filterCoefficientsMoogLadderFloat[3][i] = gg;
        filterCoefficientsMoogLadderFloat[4][i] = ggg;
        filterCoefficientsMoogLadderFloat[5][i] = G;
        filterCoefficientsMoogLadderFloat[6][i] = Gstage;
        filterCoefficientsMoogLadderFloat[7][i] = 0;
    }
}

/////////////////////////////////////
//
//	FILTER FUNCTIONS
//
/////////////////////////////////////

void CFOSynthesizer::setCutoff(uint16_t c) {
    cutoff = c;
}

void CFOSynthesizer::setResonance(uint32_t res) {
    resonance = res;
    k = res;
}

void CFOSynthesizer::setCutoffModAmount(int32_t amount) {
    if (amount >= 65536) cutoffModAmount = 65535;
    else if (amount < -65536) cutoffModAmount = -65536;
    else cutoffModAmount = amount;
    //	cutoffModAmount = amount;
}

void CFOSynthesizer::setCutoffModDirection(int32_t direction) {
    if (direction >= 0) cutoffModDirection = 1;
    else cutoffModDirection = -1;
}

void CFOSynthesizer::filterLP6dB() {
    int64_t mod = (int64_t(cutoffModAmount) * (int64_t(*cutoffModSource_ptr))) >> 16;
    int64_t c = (mod + int64_t(cutoff));
    if (c > 65535) c = 65535;
    else if (c < 0) c = 0;
    //	c = ((((c * 32768) >> 15) + 65536) >> 1);

    b1 = filterCoefficient[c >> 8];
    a0 = BIT_32 - b1;

    sample = (a0 * sample + b1 * lastSampleOutLP) >> 32;
    lastSampleOutLP = sample;
}

void CFOSynthesizer::filterLP24dB() { // BROKEN?????

    int64_t mod = (int64_t(cutoffModAmount) * (int64_t(*cutoffModSource_ptr))) >> 16;
    int64_t c = (mod + int64_t(cutoff));
    if (c > 65535) c = 65535;
    else if (c < 0) c = 0;

    b1 = filterCoefficient[c >> 8];
    a0 = BIT_32 - b1;

    x0 = sample;

    y1 = filterSamplesLP24dB[0];
    y2 = filterSamplesLP24dB[1];
    y3 = filterSamplesLP24dB[2];
    y4 = filterSamplesLP24dB[3];

    y1 = (a0 * x0 + b1 * y1) >> 32;
    y2 = (a0 * y1 + b1 * y2) >> 32;
    y3 = (a0 * y2 + b1 * y3) >> 32;
    y4 = (a0 * y3 + b1 * y4) >> 32;

    filterSamplesLP24dB[0] = y1;
    filterSamplesLP24dB[1] = y2;
    filterSamplesLP24dB[2] = y3;
    filterSamplesLP24dB[3] = y4;

    sample = y4;
}

void CFOSynthesizer::filterHP24dB() {

    int64_t mod = (int64_t(cutoffModAmount) * (int64_t(*cutoffModSource_ptr))) >> 16;
    int64_t c = (mod + int64_t(cutoff));
    if (c > 65535) c = 65535;
    else if (c < 0) c = 0;

    b1 = filterCoefficient[c >> 8];
    a0 = (BIT_32 + b1) >> 1;
    a1 = -a0;

    xNew = sample;
    xOld = filterSamplesHP24dB[0];
    yOld = filterSamplesHP24dB[4];
    yNew = (a0 * xNew + a1 * xOld + b1 * yOld) >> 32;
    x1 = xNew;
    y1 = yNew;

    xNew = y1;
    xOld = filterSamplesHP24dB[1];
    yOld = filterSamplesHP24dB[5];
    yNew = (a0 * xNew + a1 * xOld + b1 * yOld) >> 32;
    x2 = xNew;
    y2 = yNew;

    xNew = y2;
    xOld = filterSamplesHP24dB[2];
    yOld = filterSamplesHP24dB[6];
    yNew = (a0 * xNew + a1 * xOld + b1 * yOld) >> 32;
    x3 = xNew;
    y3 = yNew;

    xNew = y3;
    xOld = filterSamplesHP24dB[3];
    yOld = filterSamplesHP24dB[7];
    yNew = (a0 * xNew + a1 * xOld + b1 * yOld) >> 32;
    x4 = xNew;
    y4 = yNew;

    filterSamplesHP24dB[0] = x1;
    filterSamplesHP24dB[1] = x2;
    filterSamplesHP24dB[2] = x3;
    filterSamplesHP24dB[3] = x4;

    filterSamplesHP24dB[4] = y1;
    filterSamplesHP24dB[5] = y2;
    filterSamplesHP24dB[6] = y3;
    filterSamplesHP24dB[7] = y4;

    sample = y4;
}

void CFOSynthesizer::filterMoogLadder() {
    int64_t mod = (int64_t(cutoffModAmount) * (int64_t(*cutoffModSource_ptr))) >> 16;
    int64_t c = (mod + int64_t(cutoff));
    if (c > 65535) {
        c = 65535;
    } else if (c < 0) {
        c = 0;
    }

    int fc = c >> 8;
    if (fc > 234) {
        fc = 234;
    }
    x0 = sample;
    u = x0;
    Gstage = filterCoefficientsMoogLadder[1024 + fc];

    v1 = ((u - z1) * Gstage) >> 32;
    y1 = (v1 + z1);
    z1 = y1 + v1;

    v2 = ((y1 - z2) * Gstage) >> 32;
    y2 = (v2 + z2);
    z2 = y2 + v2;

    v3 = ((y2 - z3) * Gstage) >> 32;
    y3 = (v3 + z3);
    z3 = y3 + v3;

    v4 = ((y3 - z4) * Gstage) >> 32;
    y4 = (v4 + z4);
    z4 = y4 + v4;

    sample = y4;
}

void CFOSynthesizer::filterHP6dB() {
    sampleInHP = sample;

    int64_t mod = (int64_t(cutoffModAmount) * (int64_t(*cutoffModSource_ptr))) >> 16;
    int64_t c = (mod + int64_t(cutoff));
    if (c > 65535) {
        c = 65535;
    } else if (c < 0) {
        c = 0;
    }

    b1 = filterCoefficient[c >> 8];
    a0 = (BIT_32 + b1) >> 1;
    a1 = -a0;

    sampleOutHP = (a0 * sampleInHP + a1 * lastSampleInHP + b1 * lastSampleOutHP) >> 32;

    lastSampleInHP = sampleInHP;
    lastSampleOutHP = sampleOutHP;
    sample = sampleOutHP;
}

void CFOSynthesizer::setFilterType(uint8_t type) {
    switch (type) {
        case LP6:
            lowpass = true;
            highpass = false;
            lowpass24dB = false;
            highpass24dB = false;
            moogLadder = false;
            break;
        case HP6:
            lowpass = false;
            highpass = true;
            lowpass24dB = false;
            highpass24dB = false;
            moogLadder = false;
            break;
        case BP6:
            lowpass = true;
            highpass = true;
            lowpass24dB = false;
            highpass24dB = false;
            moogLadder = false;
            break;
        case THRU:
            lowpass = false;
            highpass = false;
            lowpass24dB = false;
            highpass24dB = false;
            moogLadder = false;
            break;
        case LP24:
            lowpass = false;
            highpass = false;
            lowpass24dB = true;
            highpass24dB = false;
            moogLadder = false;
            break;
        case HP24:
            lowpass = false;
            highpass = false;
            lowpass24dB = false;
            highpass24dB = true;
            moogLadder = false;
            break;
        case BP24:
            lowpass = false;
            highpass = false;
            lowpass24dB = true;
            highpass24dB = true;
            moogLadder = false;
            break;
        case MOOG:
            lowpass = false;
            highpass = false;
            lowpass24dB = false;
            highpass24dB = false;
            moogLadder = true;
            break;
        default:
            break;
    }
}

void CFOSynthesizer::setCutoffModShape(uint8_t shape) {
    switch (shape) {
        case 0:
            cutoffModShape_ptr = &fullSignal;
            break;
        case 1:
            cutoffModShape_ptr = &env1;
            break;
        case 2:
            cutoffModShape_ptr = &env2;
            break;
        case 3:
            cutoffModShape_ptr = &oscil1;
            break;
        case 4:
            cutoffModShape_ptr = &oscil2;
            break;
        case 5:
            cutoffModShape_ptr = &oscil3;
            break;
        default:
            cutoffModShape_ptr = &fullSignal;
            break;
    }
}

void CFOSynthesizer::setCutoffModSource(uint8_t source) {
    switch (source) {
        case 0:
            cutoffModSource_ptr = &fullSignal;
            break;
        case 1:
            cutoffModSource_ptr = &env1;
            break;
        case 2:
            cutoffModSource_ptr = &env2;
            break;
        case 3:
            cutoffModSource_ptr = &oscil1;
            break;
        case 4:
            cutoffModSource_ptr = &oscil2;
            break;
        case 5:
            cutoffModSource_ptr = &oscil3;
            break;
        default:
            cutoffModSource_ptr = &fullSignal;
            break;
    }
}

void CFOSynthesizer::setResonanceModShape(uint8_t shape) {
    switch (shape) {
        case 0:
            resonanceModShape_ptr = &fullSignal;
            break;
        case 1:
            resonanceModShape_ptr = &env1;
            break;
        case 2:
            resonanceModShape_ptr = &env2;
            break;
        case 3:
            resonanceModShape_ptr = &oscil1;
            break;
        case 4:
            resonanceModShape_ptr = &oscil2;
            break;
        case 5:
            resonanceModShape_ptr = &oscil3;
            break;
        default:
            resonanceModShape_ptr = &fullSignal;
            break;
    }
}

void CFOSynthesizer::setResonanceModSource(uint8_t source) {
    switch (source) {
        case 0:
            resonanceModSource_ptr = &fullSignal;
            break;
        case 1:
            resonanceModSource_ptr = &env1;
            break;
        case 2:
            resonanceModSource_ptr = &env2;
            break;
        case 3:
            resonanceModSource_ptr = &oscil1;
            break;
        case 4:
            resonanceModSource_ptr = &oscil2;
            break;
        case 5:
            resonanceModSource_ptr = &oscil3;
            break;
        default:
            resonanceModSource_ptr = &fullSignal;
            break;
    }
}

/////////////////////////////////////
//
//	FREQUENCY AND DETUNE FUNCTIONS
//
/////////////////////////////////////

void CFOSynthesizer::setFrequency(float freq) {
    frequency = freq;
    if (!osc1LFO) {
        setFrequency1(freq);
    }
    if (!osc2LFO) {
        setFrequency2(freq);
    }
    if (!osc3LFO) {
        setFrequency3(freq);
    }
}

void inline CFOSynthesizer::setFrequency1(float freq) {
    frequency1 = freq;
    period1 = int32_t(((frequency1 * semi1 * (1 + detune1 + bend)) * PERIOD_MAX) / SAMPLE_RATE);
}

void inline CFOSynthesizer::setFrequency2(float freq) {
    frequency2 = freq;
    period2 = int32_t(((frequency2 * semi2 * (1 + detune2 + bend)) * PERIOD_MAX) / SAMPLE_RATE);
}

void inline CFOSynthesizer::setFrequency3(float freq) {
    frequency3 = freq;
    period3 = int32_t(((frequency3 * semi3 * (1 + detune3 + bend)) * PERIOD_MAX) / SAMPLE_RATE);
}

void CFOSynthesizer::setSemitone1(int8_t semi) {
    if (-25 < semi && semi < 25) {
        semi1 = semitoneTable[semi + 24];
    } else if (semi < -24) {
        semi1 = semitoneTable[0];
    } else {
        semi1 = semitoneTable[48];
    }
    setFrequency1(frequency1);
}

void CFOSynthesizer::setSemitone2(int8_t semi) {
    if (-25 < semi && semi < 25) {
        semi2 = semitoneTable[semi + 24];
    } else if (semi < -24) {
        semi2 = semitoneTable[0];
    } else {
        semi2 = semitoneTable[48];
    }
    setFrequency2(frequency2);
}

void CFOSynthesizer::setSemitone3(int8_t semi) {
    if (-25 < semi && semi < 25) {
        semi3 = semitoneTable[semi + 24];
    } else if (semi < -24) {
        semi3 = semitoneTable[0];
    } else {
        semi3 = semitoneTable[48];
    }
    setFrequency3(frequency3);
}

void CFOSynthesizer::setDetune(float detune) {
    detune1 = 0.0;
    detune2 = detune * 0.123456789;
    detune3 = -detune;
    setFrequency2(frequency2);
    setFrequency3(frequency3);
}

void CFOSynthesizer::setDetune1(float detune) {
    detune1 = detune;
    setFrequency1(frequency1);
}

void CFOSynthesizer::setDetune2(float detune) {
    detune2 = detune;
    setFrequency2(frequency2);
}

void CFOSynthesizer::setDetune3(float detune) {
    detune3 = detune;
    setFrequency3(frequency3);
}

void CFOSynthesizer::pitchBend(float b) {
    bend = b;
    setFrequency1(frequency1);
    setFrequency2(frequency2);
    setFrequency3(frequency3);
}

void CFOSynthesizer::setOsc1LFO(bool lfo) {
    osc1LFO = lfo;
}

void CFOSynthesizer::setOsc2LFO(bool lfo) {
    osc2LFO = lfo;
}

void CFOSynthesizer::setOsc3LFO(bool lfo) {
    osc3LFO = lfo;
}

void CFOSynthesizer::setFM1(uint8_t fm) {
    fmAmount1 = fm;
}

void CFOSynthesizer::setFM2(uint8_t fm) {
    fmAmount2 = fm;
}

void CFOSynthesizer::setFM3(uint8_t fm) {
    fmAmount3 = fm;
}

void CFOSynthesizer::setFMoctaves(uint8_t octs) {
    fmOctaves1 = octs;
    fmOctaves2 = octs;
    fmOctaves3 = octs;
}

void CFOSynthesizer::setFM1octaves(uint8_t octs) {
    if (octs < 1) octs = 1;
    fmOctaves1 = octs;
}

void CFOSynthesizer::setFM2octaves(uint8_t octs) {
    if (octs < 1) octs = 1;
    fmOctaves2 = octs;
}

void CFOSynthesizer::setFM3octaves(uint8_t octs) {
    if (octs < 1) octs = 1;
    fmOctaves3 = octs;
}

void CFOSynthesizer::setFM1Source(uint8_t source) {
    // @todo define constants here?
    switch (source) {
        case 0:
            osc1modSource_ptr = &fullSignal;
            break;
        case 1:
            osc1modSource_ptr = &oscil1;
            break;
        case 2:
            osc1modSource_ptr = &oscil2;
            break;
        case 3:
            osc1modSource_ptr = &oscil3;
            break;
        default:
            osc1modSource_ptr = &fullSignal;
            break;
    }
}

void CFOSynthesizer::setFM2Source(uint8_t source) {
    switch (source) {
        case 0:
            osc2modSource_ptr = &fullSignal;
            break;
        case 1:
            osc2modSource_ptr = &oscil1;
            break;
        case 2:
            osc2modSource_ptr = &oscil2;
            break;
        case 3:
            osc2modSource_ptr = &oscil3;
            break;
        default:
            osc1modSource_ptr = &fullSignal;
            break;
    }
}

void CFOSynthesizer::setFM3Source(uint8_t source) {
    switch (source) {
        case 0:
            osc3modSource_ptr = &fullSignal;
            break;
        case 1:
            osc3modSource_ptr = &oscil1;
            break;
        case 2:
            osc3modSource_ptr = &oscil2;
            break;
        case 3:
            osc3modSource_ptr = &oscil3;
            break;
        default:
            osc1modSource_ptr = &fullSignal;
            break;
    }
}

void CFOSynthesizer::setFM1Shape(uint8_t shape) {
    // @todo define constants here?
    switch (shape) {
        case 0:
            osc1modShape_ptr = &fullSignal;
            break;
        case 1:
            osc1modShape_ptr = &env1;
            break;
        case 2:
            osc1modShape_ptr = &env2;
            break;
        case 3:
            osc1modShape_ptr = &oscil1;
            break;
        case 4:
            osc1modShape_ptr = &oscil2;
            break;
        case 5:
            osc1modShape_ptr = &oscil3;
            break;
        default:
            osc1modShape_ptr = &fullSignal;
            break;
    }
}

void CFOSynthesizer::setFM2Shape(uint8_t shape) {
    switch (shape) {
        case 0:
            osc2modShape_ptr = &fullSignal;
            break;
        case 1:
            osc2modShape_ptr = &env1;
            break;
        case 2:
            osc2modShape_ptr = &env2;
            break;
        case 3:
            osc2modShape_ptr = &oscil1;
            break;
        case 4:
            osc2modShape_ptr = &oscil2;
            break;
        case 5:
            osc2modShape_ptr = &oscil3;
            break;
        default:
            osc2modShape_ptr = &fullSignal;
            break;
    }
}

void CFOSynthesizer::setFM3Shape(uint8_t shape) {
    switch (shape) {
        case 0:
            osc3modShape_ptr = &fullSignal;
            break;
        case 1:
            osc3modShape_ptr = &env1;
            break;
        case 2:
            osc3modShape_ptr = &env2;
            break;
        case 3:
            osc3modShape_ptr = &oscil1;
            break;
        case 4:
            osc3modShape_ptr = &oscil2;
            break;
        case 5:
            osc3modShape_ptr = &oscil3;
            break;
        default:
            osc3modShape_ptr = &fullSignal;
            break;
    }
}

void CFOSynthesizer::fmToZeroHertz(bool zero) {
    if (!zero) {
        zeroFM = 1;
    } else {
        zeroFM = 0;
    }
}

void CFOSynthesizer::setPortamento(int32_t port) {
    if (port == 0) {
        port = 1;
    }
    portamento = port;
    //	portamento = envTimeTable[uint8_t(port)];
}

/////////////////////////////////////
//
//	WAVEFORM FUNCTIONS
//
/////////////////////////////////////

void CFOSynthesizer::setWaveform(uint16_t waveForm) {
    waveForm1 = waveForm * 256;
    waveForm2 = waveForm * 256;
    waveForm3 = waveForm * 256;
}

void CFOSynthesizer::setWaveform1(uint16_t waveForm) {
    waveForm1 = waveForm * 256;
}

void CFOSynthesizer::setWaveform2(uint16_t waveForm) {
    waveForm2 = waveForm * 256;
}

void CFOSynthesizer::setWaveform3(uint16_t waveForm) {
    waveForm3 = waveForm * 256;
}


/////////////////////////////////////
//
//	GAIN FUNCTIONS
//
/////////////////////////////////////

void CFOSynthesizer::setGain(float value) {
    gain = uint16_t(value * 65535);
    gain1 = gain;
    gain2 = gain;
    gain3 = gain;
}

void CFOSynthesizer::setGain1(float value) {
    gain1 = uint16_t(value * 65535);
}

void CFOSynthesizer::setGain2(float value) {
    gain2 = uint16_t(value * 65535);
}

void CFOSynthesizer::setGain3(float value) {
    gain3 = uint16_t(value * 65535);
}

float CFOSynthesizer::getGain() {
    return float(gain) / 65535.0;
}

float CFOSynthesizer::getGain1() {
    return float(gain1) / 65535.0;
}

float CFOSynthesizer::getGain2() {
    return float(gain2) / 65535.0;
}

float CFOSynthesizer::getGain3() {
    return float(gain3) / 65535.0;
}

/////////////////////////////////////
//
//	NOTE_ON/OFF FUNCTIONS
//
/////////////////////////////////////

void CFOSynthesizer::noteOn(uint8_t note, uint8_t vel) {
    env1Stage = 1;
    env2Stage = 1;
    setEnv1VelSustain(vel);
    setEnv2VelSustain(vel);
    setEnv1VelPeak(vel);
    setEnv2VelPeak(vel);
    notePlayed = note;
    frequency16bit = hertzTable[notePlayed];
    setFrequency(frequency16bit);
}

void CFOSynthesizer::noteOn(uint8_t note) {
    noteOn(note, 127);
}

void CFOSynthesizer::noteOff(uint8_t note) {
    // this acts as *only specific notes off*
    if (notePlayed == note) {
        noteOff();
    }
}

void CFOSynthesizer::noteOff() {
    // this acts as *all notes off*
    env1Stage = 4;
    env2Stage = 4;
}

float CFOSynthesizer::getNoteFrequency(uint8_t note) {
    return hertzTable[note];
}

/////////////////////////////////////
//
//	ENVELOPE FUNCTIONS
//
/////////////////////////////////////

// ENVELOPE 1

void CFOSynthesizer::enableEnvelope1() {
    envelopeOn1 = true;
}

void CFOSynthesizer::disableEnvelope1() {
    envelopeOn1 = false;
}

void CFOSynthesizer::setEnv1Stage(uint8_t stage) {
    env1Stage = stage;
}

void CFOSynthesizer::setEnv1Attack(uint8_t att) {
    if (att > 127) att = 127;
    attack1 = envTimeTable[att];
}

void CFOSynthesizer::setEnv1Decay(uint8_t dec) {
    if (dec > 127) dec = 127;
    decay1 = envTimeTable[dec];
}

void CFOSynthesizer::setEnv1Sustain(uint8_t sus) {
    sustain1 = ((sus * MAX_ENV_GAIN) / 128);
}

void CFOSynthesizer::setEnv1Release(uint8_t rel) {
    if (rel > 127) rel = 127;
    release1 = envTimeTable[rel];
}

void CFOSynthesizer::setEnv1VelSustain(uint8_t vel) {
    velSustain1 = vel * (sustain1 / 128);
}

void CFOSynthesizer::setEnv1VelPeak(uint8_t vel) {
    velPeak1 = vel * (MAX_ENV_GAIN / 128);
}

// ENVELOPE 2

void CFOSynthesizer::enableEnvelope2() {
    envelopeOn2 = true;
}

void CFOSynthesizer::disableEnvelope2() {
    envelopeOn2 = false;
}

void CFOSynthesizer::setEnv2Stage(uint8_t stage) {
    env2Stage = stage;
}

void CFOSynthesizer::setEnv2Attack(uint8_t att) {
    if (att > 127) att = 127;
    attack2 = envTimeTable[att];
}

void CFOSynthesizer::setEnv2Decay(uint8_t dec) {
    if (dec > 127) {
        dec = 127;
    }
    decay2 = envTimeTable[dec];
}

void CFOSynthesizer::setEnv2Sustain(uint8_t sus) {
    sustain2 = ((sus * MAX_ENV_GAIN) / 128);
}

void CFOSynthesizer::setEnv2Release(uint8_t rel) {
    rel = clamp127(rel);
    release2 = envTimeTable[rel];
}

void CFOSynthesizer::setEnv2VelSustain(uint8_t vel) {
    velSustain2 = vel * (sustain2 / 128);
}

void CFOSynthesizer::setEnv2VelPeak(uint8_t vel) {
    velPeak2 = vel * (MAX_ENV_GAIN / 128);
}

void CFOSynthesizer::setParameters(int pParameters[]) {
    for (uint8_t i = 0; i < 128; i++) {
        if (i == SYSTEM_CALL) {
            continue;
        }
        if (i == PRESET_RECALL) {
            continue;
        }
        controller(i, pParameters[i]);
    }
}

void CFOSynthesizer::preset(uint8_t pPreset) {
    if (pPreset > NUM_PRESETS - 1) {
        pPreset = NUM_PRESETS - 1;
    }

    if (pPreset < 0) {
        pPreset = 0;
    }

    const int mOffset = pPreset * PRESET_SIZE;
    for (uint8_t i = 0; i < 128; i++) {
        if (i == SYSTEM_CALL) {
            continue;
        }
        if (i == PRESET_RECALL) {
            continue;
        }
        int mID = mOffset + i;
        controller(i, programPresets[mID]);
    }
}

void CFOSynthesizer::controller(uint8_t number, uint8_t value) {
    value = clamp127(value);
    number = clamp127(number);

    switch (number) {
        case IS_12_BIT:
            if (value) set12bit(true);
            else set12bit(false);
            break;
        case PORTAMENTO:
            setPortamento(portamentoTimeTable[value]);
            break;
        case CUTOFF:
            setCutoff(value * 512);
            break;
        case RESONANCE:
            setResonance(value * 512);
            break;
        case FILTER_TYPE:
            setFilterType(value);
            break;
        case CUTOFF_MOD_AMOUNT:
            setCutoffModAmount((value - 64) * 1024);
            break;
        case CUTOFF_SOURCE:
            setCutoffModSource(value);
            break;
        case CUTOFF_SHAPE:
            setCutoffModShape(value);
            break;
        case ZERO_HZ_FM:
            if (value) fmToZeroHertz(true);
            else fmToZeroHertz(false);
            break;
        case FM_OCTAVES:
            setFMoctaves(value + 1);
            break;
        case LFO1:
            if (value) {
                setOsc1LFO(true);
                setFrequency1(getNoteFrequency(value) / 1024.0);
            } else {
                setOsc1LFO(false);
            }
            break;
        case LFO2:
            if (value) {
                setOsc2LFO(true);
                setFrequency2(getNoteFrequency(value) / 1024.0);
            } else {
                setOsc2LFO(false);
            }
            break;
        case LFO3:
            if (value) {
                setOsc3LFO(true);
                setFrequency3(getNoteFrequency(value) / 1024.0);
            } else {
                setOsc3LFO(false);
            }
            break;
        case DETUNE1:
            setDetune1(map(value, 0, 127, -100, 100)*0.0005946);
            break;
        case DETUNE2:
            setDetune2(map(value, 0, 127, -100, 100)*0.0005946);
            //setDetune2((value-64.0)*0.0005946);
            //setDetune2(value/5120.0);
            break;
        case DETUNE3:
            setDetune3(map(value, 0, 127, -100, 100)*0.0005946);
            //setDetune3((value-64.0)*0.0005946);
            //setDetune3(value/5120.0);
            break;
        case SEMITONE1:
            //			if(15 < value && value < 113) {
            //				int8_t val = (((value-16)/2)-24);
            //				setSemitone1(val);
            //			} else if (value < 16) {
            //				setSemitone1(-24);				
            //			} else {
            //				setSemitone1(24);
            //			}
            if (40 <= value && value <= 88) {
                setSemitone1(value - 64);
            } else if (value < 40) {
                setSemitone1(-24);
            } else {
                setSemitone1(24);
            }
            break;
        case SEMITONE2:
            //			if(15 < value && value < 113) {
            //				int8_t val = (((value-16)/2)-24);
            //				setSemitone2(val);
            //			} else if (value < 16) {
            //				setSemitone2(-24);				
            //			} else {
            //				setSemitone2(24);
            //			}
            if (40 <= value && value <= 88) {
                setSemitone2(value - 64);
            } else if (value < 40) {
                setSemitone2(-24);
            } else {
                setSemitone2(24);
            }
            break;
        case SEMITONE3:
            //			if(15 < value && value < 113) {
            //				int8_t val = (((value-16)/2)-24);
            //				setSemitone3(val);
            //			} else if (value < 16) {
            //				setSemitone3(-24);				
            //			} else {
            //				setSemitone3(24);
            //			}
            if (40 <= value && value <= 88) {
                setSemitone3(value - 64);
            } else if (value < 40) {
                setSemitone3(-24);
            } else {
                setSemitone3(24);
            }
            break;
        case GAIN1:
            setGain1(value / 127.0);
            break;
        case GAIN2:
            setGain2(value / 127.0);
            break;
        case GAIN3:
            setGain3(value / 127.0);
            break;
        case WAVEFORM1:
            setWaveform1(value);
            break;
        case WAVEFORM2:
            setWaveform2(value);
            break;
        case WAVEFORM3:
            setWaveform3(value);
            break;
        case FM1:
            setFM1(value);
            break;
        case FM2:
            setFM2(value);
            break;
        case FM3:
            setFM3(value);
            break;
            //		case FM1_OCTAVES:
            //			setFM1octaves(value+1);
            //			break;
            //		case FM2_OCTAVES:
            //			setFM2octaves(value+1);
            //			break;
            //		case FM3_OCTAVES:
            //			setFM3octaves(value+1);
            //			break;
        case FM1_SOURCE:
            setFM1Source(value);
            break;
        case FM2_SOURCE:
            setFM2Source(value);
            break;
        case FM3_SOURCE:
            setFM3Source(value);
            break;
        case FM1_SHAPE:
            setFM1Shape(value);
            break;
        case FM2_SHAPE:
            setFM2Shape(value);
            break;
        case FM3_SHAPE:
            setFM3Shape(value);
            break;
        case ENV1_ENABLE:
            //            			if(value<64) enableEnvelope1();
            //            			else disableEnvelope1();
            break;
        case ENV1_ATTACK:
            setEnv1Attack(value);
            break;
        case ENV1_DECAY:
            setEnv1Decay(value);
            break;
        case ENV1_SUSTAIN:
            setEnv1Sustain(value);
            break;
        case ENV1_RELEASE:
            setEnv1Release(value);
            break;
        case ENV2_ENABLE:
            //			if(value<64) enableEnvelope2();
            //			else disableEnvelope2();
            break;
        case ENV2_ATTACK:
            setEnv2Attack(value);
            break;
        case ENV2_DECAY:
            setEnv2Decay(value);
            break;
        case ENV2_SUSTAIN:
            setEnv2Sustain(value);
            break;
        case ENV2_RELEASE:
            setEnv2Release(value);
            break;
        case PRESET_RECALL:
            preset(value);
            break;
        default:
            break;
    }
}

void CFOSynthesizer::set12bit(bool b) {
    is12bit = b;
}
