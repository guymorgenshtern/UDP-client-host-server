Premise:
To send messages from client —> host —> server, and back again. The client is to encode a message into a byte array and start the interaction, where the host and the server are waiting to receive. The server decodes the received message, determines if it is read or write, and sends the appropriate reply.

Client: It begins the interaction and sends messages out first, then receives
Host: Acts as an intermediate between the Client and Server. Receives first, and then sends out
Server: Where “logic” is performed. Receives a message first, decodes it, then sends a reply back to the host to send to the client

Setup:
- Enter Eclipse
- File —> Open Projects from File System 
- Locate and open the downloaded submission titled “xxxxxxxxx_Assignment2”
- Run files in this specific order:
	- Server
	- Host
	- Client
- Alternatively, create a run group with Server, Host, Client and ensure the files are in that respective order top to bottom
- Running multiple files will create multiple consoles. To see all console output cycle through the consoles of each class using the “Display selected console” button in the Console View window
