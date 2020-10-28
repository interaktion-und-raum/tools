#ifndef OSCIL16_H_
#define OSCIL16_H_

#include "Arduino.h"
#include "synthesizer.h"
#include "fixmath.h"
#include <util/atomic.h>

// fractional bits for oscillator index precision
#define OSCIL_F_BITS 16
#define OSCIL_F_BITS_AS_MULTIPLIER 65536

// phmod_proportion is an 15n16 fixed-point number
#define OSCIL_PHMOD_BITS 16

template <uint16_t NUM_TABLE_CELLS, uint16_t UPDATE_RATE>
class Oscil16 {


public:
	Oscil16(const int16_t * TABLE_NAME):table(TABLE_NAME){}
	Oscil16() {}
	
	inline int16_t next() {
		incrementPhase();
		return readTable();
	}

	inline void setFreq (int frequency) {
		ATOMIC_BLOCK(ATOMIC_RESTORESTATE)
		{
			phase_increment_fractional = ((unsigned long)frequency) * ((OSCIL_F_BITS_AS_MULTIPLIER*NUM_TABLE_CELLS)/UPDATE_RATE);
		}
	}

private:

	inline void incrementPhase() {
		phase_fractional += phase_increment_fractional;
	}

	inline int16_t readTable() {
		return (int16_t)(table[(phase_fractional >> OSCIL_F_BITS) & (NUM_TABLE_CELLS - 1)]);
//		return (int16_t)pgm_read_byte_near(table + ((phase_fractional >> OSCIL_F_BITS) & (NUM_TABLE_CELLS - 1)));
	}

	unsigned long phase_fractional;
	volatile unsigned long phase_increment_fractional; 
	const int16_t * table;
};

#endif /* OSCIL16_H_ */
