# ServerNew
Java Client Server program for project onsDOMein

****How to use the server****

**GebruikersApplicatie**
- Connect to the server via the proxy provided by onsDOMein -> connectClientToServer(String client_id) throws IOException
- Send the request for the HC or the server, to get config or state, via the protocol used (see below) ->
            sendRequest(String command, String requestFromId, String requestForId, String message) this call returns a String or Null.
- Close the connection when done -> closeConnection()

**HuisCentrale**
- Connect and close as with GebruikersApplicatie
- After receiving a request from a GebruikersApplicatie send the response with ->
            sendResponse(String command, String requestFromId, String requestForId, String message)

****Protocol****
The protocol used for communication between GA, server and HC is as follows:
command;id_sender;id_receiver;message

this is a String with delimiter a semicolon.

The requestFromId and requestForId must be unique and can be any kind of string.
The message contains the instruction for the HC or the Configuration of the HC send by the GA and should contain the following syntax
message:message:message etc.

This can be pin numbers (as String) and the state of that pin, as in key value pairs.

The command is very important, if you miss spell it you will get a 'not conform to protocol error'.
These commands can be send:
- setHc / getHc / setConfig / getConfig / setState / getState

The setHc and getHc will go to the Hc with this String syntax: command;requestFrom;message
The HC should respond with exactly the same command (if setHc is send setHc is the response) and the message can be a OK or NOK.
If a getHc command is send the message should contain the information requested in the format pin:value etc.
Do not use semicolon in the message part!
If a connection with a HC fails this is the return: No connection with HC

When using server request (setConfig / getConfig / setState / getState) following responses are possible:
- setConfig -> setConfigOK || setConfigNOK
- setState -> setStateOK || setStateNOK
- getConfig -> String with config message (pin:value etc.) This string is created by the GA so what ever you send, this is what you get back.
- getState -> see getConfig.

