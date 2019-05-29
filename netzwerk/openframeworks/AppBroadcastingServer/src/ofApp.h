#pragma once

#include "ofMain.h"
#include "ofxOsc.h"
#include "NetAddress.hpp"

// listen on port 12345
#define HOST "localhost"
#define PORT 32000

class ofApp : public ofBaseApp{
    
public:
    void setup();
    void update();
    void draw();
    
    void keyPressed(int key);
    void keyReleased(int key);
    void mouseMoved(int x, int y );
    void mouseDragged(int x, int y, int button);
    void mousePressed(int x, int y, int button);
    void mouseReleased(int x, int y, int button);
    void mouseEntered(int x, int y);
    void mouseExited(int x, int y);
    void windowResized(int w, int h);
    void dragEvent(ofDragInfo dragInfo);
    void gotMessage(ofMessage msg);
    
private:
    
    void processMessage(ofxOscMessage);
    void connect(NetAddress);
    void disconnect(NetAddress);
    bool contains(NetAddress);
    bool remove(NetAddress);
    
    static const int SERVER_DEFAULT_BROADCAST_PORT = 32000;
    const string SERVER_PATTERN_CONNECT = "/server/connect";
    const string SERVER_PATTERN_DISCONNECT = "/server/disconnect";
    const string SERVER_PATTERN_PING = "/server/ping";
    const string SERVER_PATTERN_CONNECT_SERVER = "/server/connect-server";
    static const int NUM_MSG_STRINGS = 20;
    ofTrueTypeFont font;
    ofxOscReceiver mReceiver;
    ofxOscSender mSender;
    vector<NetAddress> mRecipients;
    
    int current_msg_string;
    string msg_strings[NUM_MSG_STRINGS];
};
