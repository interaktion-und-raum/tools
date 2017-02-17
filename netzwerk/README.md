# netzwerk

- star-shaped network based on OSC protocol
- schaubild of architecture

## manual

- run server `NetzwerkServer` ( e.g `AppBroadcastingServer` ( i.e `SketchAppBroadcastingServer` ) )
- create client `NetzwerkClient`
    - specify server name or IP
    - specify sender name
- connect to server with `connect()`
- send messages via `send()`
    - message gets send to server
    - server relays message to all connected clients ( inkl oneself )
- receive messages via `receive()`
    - method get s called automatically when a message is received
    - note: asynchron

### message types

- anatomy of a message
- every message has a *sender* 
- every message has a *tag* 
- every message has *data*
- data can be 1f, 2f, 3f, or String
- the variations of the send methods are
    - `send(float)`
    - `receive(float)`
- there are other methods for sending messages as well

## dependencies

- [oscP5](http://dm-hb.de/db)
- [controlP5](http://dm-hb.de/da)

## releases

### 20170217

- intial release

## feature request

- `send_direct` send messages directly to other machines
- connect servers directly to form 
- auto-connect client to server when sending first message
- @NetzwerkServer add timestamp and message_type ( broadcast, server ) to log