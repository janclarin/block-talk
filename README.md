# block-talk
CPSC 559 Project

- Clinton Cabiles
- Jan Clarin
- Riley Lahd

## Server-side:

1. Start a ServerManager like so:

`java server.ServerManager <port>`

2. Start 1 or more Servers:

`java server.Server <ip> <port>`

2. Start 1 or more Servers:

`java server.Server <ip> <port>`

3. Link server(s) to ServerManager:

Inside ServerManager: `<server ip> <server port>`

4. Stop listening for servers and start listening for clients on ServerManager:

Inside ServerManager: `c`

## Client-side:

1. Start client(s):

`java BlockTalkClientProgram <ip> <port>`

2. Follow prompts
