# Block Talk - Distributed Systems Course Project
A peer-to-peer, room-based, cryptographically secure chat service.

**Group Members**
- Clinton Cabiles
- Jan Clarin
- Riley Lahd

## Server-side:

1. Start a ServerManager like so: `java server.ServerManager <port>`

2. Start 1 or more Servers: `java server.Server <port>`

3. Link server(s) to ServerManager: (Inside ServerManager) `<server ip> <server port>`

4. Stop listening for servers and start listening for clients on ServerManager: (Inside ServerManager) `c`

## Client-side:

1. Start client(s): `java BlockTalkClientProgram`

2. Follow prompts to enter user information. Ensure that an unused port is entered.

3. Follow prompt about room key. This needs to be the same across clients in order to join the same room.

4. Follow prompts to enter server information.

5. If the room key entered was able to decrypt a room key from the server, the client will join the existing chat room.

6. Otherwise, the client will be registered as the host with the server.

**Afterwards, clients can send messages by typing a message followed by `<Enter>`**

## To test:

1. Follow server-side instructions above to start the server manager and server(s)

2. Follow client-side instructions to start multiple clients connected to the same server manager

3. For multiple clients to be part of the same room, they must enter the same room key when prompted

4. Once multiple clients are in the same room, send messages as instructed above
