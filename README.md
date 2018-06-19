# P2P File Stream

[![Build Status](https://travis-ci.org/p2p-file-stream/p2p-file-stream.svg?branch=master)](https://travis-ci.org/p2p-file-stream/p2p-file-stream)

A file sharing application without persistence central storage. 


## Modules
This repo contains a multi-module Maven project. This diagram shows the dependencies:
```
+---------------+      +------------+     +------------+
|               |      |            |     |            |
| accountserver |      | rendezvous |     |   client   |
|               |      |            |     |            |
+------+--------+      +-----+------+     +------+-----+
       |                     |                   |
       |                     |                   |
       |                     V                   |
       |              +------------+             |
       |              |            |             |
       +------------> |   shared   | <-----------+
                      |            |
                      +------------+
```

The module `accountserver` contains a Spring Boot application that stores accounts and nicknames. It exposes a rest-api that is used by the clients. All requests must be authenticated using a `Authorization: Bearer (...)` header containing a valid Auth0-JWT. When a nickname is requested, a signed nickname-JWT is returned that contains claims about this nickname and account.

`rendezvous` is another Spring Boot application that has two tasks. It exposes a WebSocket endpoint for it's SessionService, that manages chat-requests and supplies clients with a chat-id. Users are authenticated using a nickname-JWT in the header. This acts as stateless authentication, because all information to construct a `Device` object is present in the token.

This server also contains a WebSocket relay service. It matches two clients that provided the same chat-id during the WebSocket handshake, and relays all the messages between them.

`shared` contains the domain-model and interfaces for communication. It also provides logic to encode and decode method calls on those interfaces to bytes, so that they can be send over WebSockets.

The Desktop client is a TornadoFX application (uses JavaFX). It contains the Graphical User Interface, the file streaming code, and it performs authentication using Auth0 in a `WebView`.
