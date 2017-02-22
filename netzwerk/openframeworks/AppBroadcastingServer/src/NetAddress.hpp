//
//  NetAddress.hpp
//  AppBroadcastingServer
//
//  Created by dpp on 21.02.17.
//
//

#ifndef NetAddress_hpp
#define NetAddress_hpp

#include "ofMain.h"
#include "ofxOsc.h"

class NetAddress {
public:
    NetAddress() {}
    NetAddress(ofxOscMessage m) {
        IP = m.getRemoteIp();
        port = m.getRemotePort();
    }
    string IP;
    int port;
};

#endif /* NetAddress_hpp */
