# Objective

The educational objective of the practice is to learn to use Client/Server programming mechanisms in JAVA. Specifically, it is necessary to learn how to program with:

- Sockets in JAVA (using the Java.net Socket API)
- Multi-request server with threads (JAVA)

## Tasks performed

- The client must have a manual mode (screen menu) and an automatic mode (play automatically according to the parameters entered)
- An implementation of the multi-threaded server must be done that serves both for the player to play against the *machine* (1 player) and to play against another connected client (2 player)
- The server must write a log of the interaction with the client to a file following the specified format.
- Perform system robustness and stress tests.
- Code, JUnit, JavaDoc, dossier with Diagrams, and self-evaluation of the practice.

## Design Notes

- Remember that two applications need to be designed, Client, and Server. They can have common classes, for example, ComUtils.
- The ComUtils class must be extended with your methods to follow the protocol thread by thread. If there are methods you don't need, you can delete them.
- You cannot use Java classes to serialize your objects, as they will not follow the requested protocol. Always use the primitives of ComUtils.
- Use JUnit to check the protocol, for example, if a command has been sent, you should expect to receive certain ones. Keep in mind that you could always receive an error message.
- For saving input commands, it is good practice to use a HashMap for each input option and its value and consult its value when necessary.
- The Client in automatic mode must make the most suitable decisions to win the game.
- Use two different implementations of the server's game control thread for the 1 player and 2 player cases. The game mode of the Server will be specified at runtime.

## Execution

    server> java -jar server.jar -h 
    Usage: java -jar server.jar -p <port> -m [1|2] 


    client> java -jar client -h
    Usage: java -jar client -s <server_machine> -p <port> [-i 0|1]

- In the server, the port option (-p) is specified where the listening port will be specified and the mode option (-m) is specified where it will be specified if it will be played in 1 player mode or 2 player mode. In the two-player version, the server will act as a communication proxy between the two clients of the players without intervening.

- In the client, the server machine option (-s) is specified where the IP of the server will be specified and the port option (-p) where the server listening port will be specified.

- If the interactive option (-i) is also specified in the client:

    - 0 means that the game runs in manual mode.
    - 1 will run in automatic mode making decisions.

- If not specified, the default mode is manual.

## Output

- The log file must be the textual version of what is being sent over the socket.
- Only the Server log file needs to be saved. You must save only the content of the communication per socket, both what is received and what is being sent. In case of an error, you must also save the message sent or received over the socket.
- The log file name must be constructed as follows:

- "Server"+Thread.currentThread().getName()+".log"

- Make sure to create a src folder for each application where the main classes are named Client and Server respectively.

## Deliveries

- Update code on Github through Pull Requests with Peer Code Review.
- Mandatory Test Session (Minimum one component of the pair).
- On CampusVirtual: 30/03/2021 23.55h.

## Evaluation

- In case the code **does not meet the specified requirements** or **does not follow the agreed protocol**, the practice will be **FAILED**. All submissions will be automatically executed against our servers and clients with different sets
