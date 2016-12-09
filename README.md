# block-talk
CPSC 559 Project

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

2. Follow prompts to enter user information.

3. Follow prompts to enter server information.

4. If the room key entered was able to decrypt a room key from the server, the client will join the existing chat room.

5. Otherwise, the client will be registered as the host with the server.

After which, a client can send messages by typing a message followed by `<Enter>`
