#ifndef BEAT_H_
#define BEAT_H_

class BeatListener {
public :
    virtual void event(int pBeat) = 0;
};

class Beat {

private :
    typedef void (*BEAT_CALLBACK)(int);

    unsigned long mEventTime;
    unsigned long mTicksMicros;
    unsigned int mCurrentBeat;
    bool stopped;

    BEAT_CALLBACK mCallbackFunc;
    BeatListener* mBeatListener;

public:

    Beat() : stopped(false) {
        set(60000000 / 120);
        mCurrentBeat = -1;
    }

    inline
    void start() {
        mEventTime = micros() + mTicksMicros;
        stopped = false;
    }

    inline
    void set(unsigned int pDelayMicros) {
        mTicksMicros = pDelayMicros;
    }

    inline
    void setBPM(float bpm) {
        set((unsigned int) (60000000 / bpm));
    }

    inline
    bool ready() {
        unsigned long mNow = micros();
        if ((mNow < mEventTime) || stopped) {
            return false;
        }
        mEventTime = mNow - (mNow - mEventTime) + mTicksMicros;
        mCurrentBeat++;
        return true;
    }

    inline
    void stop() {
        stopped = true;
    }

    inline
    unsigned int current() {
        return mCurrentBeat;
    }

    inline
    void setCallback(BEAT_CALLBACK pCallbackFunc) {
        mCallbackFunc = pCallbackFunc;
    }

    inline
    void setCallback(BeatListener* pBeatListener) {
        mBeatListener = pBeatListener;
    }

    inline
    void update() {
        if (ready()) {
            if (*mCallbackFunc) {
                (*mCallbackFunc)(current());
            }
            if (mBeatListener) {
                mBeatListener->event(current());
            }
        }
    }

};

#endif /* BEAT_H_ */
