#include "ofApp.h"

//--------------------------------------------------------------
void ofApp::setup(){
    // listen on the given port
    cout << "listening for osc messages on port " << PORT << "\n";
    mReceiver.setup(PORT);
    
    // open an outgoing connection to HOST:PORT
    mSender.setup("127.0.0.1", 12000);
    
    current_msg_string = 0;
    // ofGetElapsedTimef()
    ofBackground(30, 30, 130);
}

void ofApp::processMessage(ofxOscMessage m) {
    if (m.getAddress() == SERVER_PATTERN_PING && m.getNumArgs() == 1 && m.getArgType(0) == OFXOSC_TYPE_INT32) {
        cout << "port: " << m.getRemotePort() << " + " << m.getArgAsInt(0) << endl;
//        ofxOscSender mPing;
//        mPing.setup(m.getRemoteIp(), m.getArgAsInt(0));
        ofxOscMessage mPingMessage;
        mPingMessage.setAddress(SERVER_PATTERN_PING);
        mSender.sendMessage(mPingMessage);
        //        NetAddress mNetAddress = new NetAddress(m.netAddress().address(), m.get(0).intValue());
        //        mOSC.send(m, mNetAddress);
    } else if (m.getAddress() == SERVER_PATTERN_CONNECT_SERVER && m.getNumArgs() == 1 && m.getArgType(0) == OFXOSC_TYPE_STRING) {
        //        cout << "### this server should connect to another server with the address: " << m.get(0).stringValue());
    } else if (m.getAddress() == SERVER_PATTERN_CONNECT && m.getNumArgs() == 1 && m.getArgType(0) == OFXOSC_TYPE_INT32) {
        cout << "### connecting " << m.getRemoteIp() << ":" << m.getRemotePort() << " on port: " << m.getArgAsInt(0) << endl;
        connect(NetAddress(m));
        cout << "### number of connected clients: " << mRecipients.size() << endl;
    } else if (m.getAddress() == SERVER_PATTERN_DISCONNECT && m.getNumArgs() == 1 && m.getArgType(0) == OFXOSC_TYPE_INT32) {
        cout << "### disconnecting " << m.getRemoteIp() << ":" << m.getRemotePort() << " on port: " << m.getArgAsInt(0) << endl;
        disconnect(NetAddress(m));
    } else {
        /*
         * if pattern matching was not successful, then broadcast the incoming
         * message to all addresses in the netAddresList.
         */
        //        mOSC.send(m, mNetAddressList);
//        mSender.sendMessage(m);
        cout << "### broadcasting message" << endl;
    }
}

void ofApp::connect(NetAddress n) {
    if (!contains(n)) {
        mRecipients.push_back(n);
    } else {
        cout << "--- " << n.IP << ":" << n.port << " is already connected." << endl;
    }
}

void ofApp::disconnect(NetAddress n) {
    if (!remove(n)) {
        cout << "--- " << n.IP << ":" << n.port << " is not connected." << endl;
    }
}

bool ofApp::remove(NetAddress pNetAddress) {
    for (auto it = mRecipients.begin(); it!=mRecipients.end(); ++it) {
        if ((*it).IP == pNetAddress.IP && (*it).port == pNetAddress.port) {
            mRecipients.erase(it);
            return true;
        }
    }
    return false;
}

bool ofApp::contains(NetAddress pNetAddress) {
    for (auto mNetAddress : mRecipients) {
        if (mNetAddress.IP == pNetAddress.IP && mNetAddress.port == pNetAddress.port) {
            return true;
        }
    }
    return false;
}


//--------------------------------------------------------------
void ofApp::update(){
    // check for waiting messages
    while(mReceiver.hasWaitingMessages()){
        // get the next message
        ofxOscMessage m;
        mReceiver.getNextMessage(m);
        processMessage(m);
        
        string msg_string;
        msg_string = m.getAddress();
        msg_string += ": ";
        for(int i = 0; i < m.getNumArgs(); i++){
            // get the argument type
            msg_string += m.getArgTypeName(i);
            msg_string += ":";
            // display the argument - make sure we get the right type
            if(m.getArgType(i) == OFXOSC_TYPE_INT32){
                msg_string += ofToString(m.getArgAsInt32(i));
            }
            else if(m.getArgType(i) == OFXOSC_TYPE_FLOAT){
                msg_string += ofToString(m.getArgAsFloat(i));
            }
            else if(m.getArgType(i) == OFXOSC_TYPE_STRING){
                msg_string += m.getArgAsString(i);
            }
            else{
                msg_string += "unknown";
            }
        }
        // add to the list of strings to display
        msg_strings[current_msg_string] = msg_string;
        current_msg_string = (current_msg_string + 1) % NUM_MSG_STRINGS;
        // clear the next line
        msg_strings[current_msg_string] = "---";
        
        //        // check for mouse moved message
        //        if(m.getAddress() == "/mouse/position"){
        //            // both the arguments are int32's
        //            mouseX = m.getArgAsInt32(0);
        //            mouseY = m.getArgAsInt32(1);
        //        }
        //        // check for mouse button message
        //        else if(m.getAddress() == "/mouse/button"){
        //            // the single argument is a string
        //            mouseButtonState = m.getArgAsString(1);
        //        } else {
        //            // unrecognized message: display on the bottom of the screen
        //            string msg_string;
        //            msg_string = m.getAddress();
        //            msg_string += ": ";
        //            for(int i = 0; i < m.getNumArgs(); i++){
        //                // get the argument type
        //                msg_string += m.getArgTypeName(i);
        //                msg_string += ":";
        //                // display the argument - make sure we get the right type
        //                if(m.getArgType(i) == OFXOSC_TYPE_INT32){
        //                    msg_string += ofToString(m.getArgAsInt32(i));
        //                }
        //                else if(m.getArgType(i) == OFXOSC_TYPE_FLOAT){
        //                    msg_string += ofToString(m.getArgAsFloat(i));
        //                }
        //                else if(m.getArgType(i) == OFXOSC_TYPE_STRING){
        //                    msg_string += m.getArgAsString(i);
        //                }
        //                else{
        //                    msg_string += "unknown";
        //                }
        //            }
        //            // add to the list of strings to display
        //            msg_strings[current_msg_string] = msg_string;
        //            current_msg_string = (current_msg_string + 1) % NUM_MSG_STRINGS;
        //            // clear the next line
        //            msg_strings[current_msg_string] = "";
        //        }
    }
}

//--------------------------------------------------------------
void ofApp::draw(){
    string buf;
    buf = "listening for osc messages on port" + ofToString(PORT);
    ofDrawBitmapString(buf, 10, 20);
    
    //    // draw mouse state
    //    buf = "mouse: " + ofToString(mouseX, 4) +  " " + ofToString(mouseY, 4);
    //    ofDrawBitmapString(buf, 430, 20);
    //    ofDrawBitmapString(mouseButtonState, 580, 20);
    
    for(int i = 0; i < NUM_MSG_STRINGS; i++){
        ofDrawBitmapString(msg_strings[i], 10, 40 + 15 * i);
    }
}

//--------------------------------------------------------------
void ofApp::keyPressed(int key){
    
}

//--------------------------------------------------------------
void ofApp::keyReleased(int key){
    
}

//--------------------------------------------------------------
void ofApp::mouseMoved(int x, int y ){
    
}

//--------------------------------------------------------------
void ofApp::mouseDragged(int x, int y, int button){
    
}

//--------------------------------------------------------------
void ofApp::mousePressed(int x, int y, int button){
    
}

//--------------------------------------------------------------
void ofApp::mouseReleased(int x, int y, int button){
    
}

//--------------------------------------------------------------
void ofApp::mouseEntered(int x, int y){
    
}

//--------------------------------------------------------------
void ofApp::mouseExited(int x, int y){
    
}

//--------------------------------------------------------------
void ofApp::windowResized(int w, int h){
    
}

//--------------------------------------------------------------
void ofApp::gotMessage(ofMessage msg){
    
}

//--------------------------------------------------------------
void ofApp::dragEvent(ofDragInfo dragInfo){
    
}
