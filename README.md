
# **Java Socket Project - CSCI 4211**
## By: Isaac Ruff
### Code Overview
<pre>
I was enjoying creating this, so I ended up implementing phase 2 in this submission. Reconnecting and offline clients work as intended.

My code consists of 5 class files and 6 classes.

The TCPServer class has 4 static elements: a port number and three ConcurrentHashMaps.

    I chose to use ConcurrentHashMaps as they are thread-safe.

        The first, msgQ, maps clientNames to ConcurrentLinkedQueues which contain that client's queue of messages.

        The second, newClientMsgQ, maps topics (weather and news) to lists of every message published in that topic, that way when a new client subscribes they are pushed all previous messages on that topic.
        
        The third, clientsByTopic, maps topics (weather and news) to their own ConcurrentHashMaps, which map clientNames to Mediators.
            Effectively, it lists which clients are subscribed to which topics, and bundles their names and Mediators for easy access.
    
    Mediator is TCPServer's inner class, which extends Thread and has a custom run(). Each instance mediates between the server and a client.
        Each mediator has the socket for the client it is managing, a clientName, a printWriter and inputStream, and three boolean flags (isActive, isPublisher, and listening)
        
        isActive shows whether a client should receive messages directly or whether they should be stored in Q.
        
        isPublisher is set to true whenever a client publishes, and stops publishers from being delivered messages.
        
        listening is set to true when the Mediator starts listening for messages. Used to stop multiple listening threads from starting
        
        Mediator's run method will connect its PrintWrite and InputStream to the clientSocket, then repeatedly loop as it listens for messages. When one is received, it is handled through switch statements
        which call one of several methods depending on the inputStream

            Connect sets this Mediator's isActive flag to true, assigns it the specific clientName, and sets listening to false before sending CONN_ACK to client

            Pub sets this Mediator's isPublisher flag to true, formats the incoming message, then checks if this topic is valid and if this Publisher is subscribed to the topic it is attempting to publish to.
            If it is, then the message gets added to the relevant ArrayList within newClientMsgQ, then a thread is started which checks each mapping of clientName to Mediator within clientsByTopic
            and for every client subscribed to the topic, it adds the message to their msgQ (creating one if it doesn't exist). If the client isActive the message will be pushed immediately
            if the client is offline it will remain in their msgQ until they reconnect.

            Sub checks if the topic is valid. If it is, it sends SUB_ACK to client and adds their name to the relevant topic in clientsByTopic, mapping its name to its Mediator. Then a listening thread is
            started which listens for new messages while the client is still connected (first it checks if this Mediator is already listening, to prevent multiple listening threads from starting, as reconnect
            also starts a listening thread).

            Reconnect is very similar to Sub, but it also looks through clientsByTopic and replaces the old Mediator with the new in the clientName to Mediator mapping. Then it sends RECONNECT_ACK to client
            and starts a new listening thread.

            Disconnect is handled in the switch statement, and simply sets isActive and listening to false before sending DISC_ACK to client.



The TCPClient class acts as a framework for individual publisher/subscriber classes. It has basic methods to send/receive messages to the server, as well as to connect/disconnect

The publisher and subscriber classes extend TCPClient
    
    Publisher has variables for a name and topic (which is the topic it is able to publish to). Upon initial connection Publisher automatically follows up with a SUB request for that topic.
    
        It also has a method for publishing messages to the server

    
    Subscriber has several static elements that work separately from the class itself. The static method 'printer' is run at the start of the program and uses a static ConcurrentLinkedQueue
    to prevent multiple threads from printing together. Instead, they all push to this Q which the printer method is constantly listening to, and whatever shows up there will be printed to stdout.
    It acts as a global printer for Subscriber messages (specifically messages from publishers, not ACKs)

        Subscriber's instance elements consist of a name, a boolean listening flag to help with thread termination, and a thread called msgThread which listens for messages from the server
            Subscriber has several instance methods.
            
            Subscribe will push a SUB message to the server, and on SUB_ACK will start actively listening for messages using msgThread.
        
            Disconnect will interrupt msgThread, wait for it to die, then run the inherited TCPClient disconnect method.
        
            getMsgs sets msgThread to listen for incoming messages.
        
            Reconnect runs the inherited Connect method, then sends RECONNECT to server. On RECONNECT_ACK a new listening thread is started

The Main class contains the main method which demonstrated the code's functionality as detailed in the test sequence document provided
</pre>
### How to Run
<pre>
Run the TCPServer main method which will begin listening for clients.
Run the main method in the Main class on the same machine (code can be edited to work with other machines as well, but I didn't implement that functionality for this submission).
All ACK's will be printed by client, and all messages will be printed by the Subscriber printer static method.
</pre>
### Workflow
<pre>
TCPServer is run, opens a socket on port 6789 and listens for clients.

Main is run.

Subscriber.printer is run, starts listening for Subscriber messages.

Publishers one and two connect to the server. After each CONN_ACK, a subscription request is automatically sent for their given topic.
    TCPServer receives the requests and associates the Publisher names with their Mediators upon connection (this is true of all clients going forward)
    then adds the mappings of names and Mediators to the clientByTopic map associated with their given topic.

Publishers one and two publish messages about their individual topics.
    TCPServer receives the requests and adds the messages to newClientMsgQ, then stops as there are no Subscribers to push to yet.
Publisher one attempts to publish to a non-existent topic.
    TCPServer checks if SPORTS is a key in clientsByTopic. Since it is not, an error is returned.

Subscriber one connects and sends a subscribe request for WEATHER, then starts listening for messages.
    TCPServer receives the request and adds this name/Mediator pair to the associated clientsByTopic map, then
    as it wasn't already in the map and is therefore a new Subscriber, it pushes all messages for that topic in newClientMsgQ to
    the Subscriber.
Subscriber one receives these messages, its msgThread pushes them to the Q that Subscriber.printer is listening to and they are printed to stdout
(this is true of all received messages going forward).
Subscriber one sends a subscribe request for a non-existent topic.
    TCPServer checks if SPORTS is a key in clientsByTopic. Since it is not, an error is returned.

Suscriber two connects and sends a subscribe request for NEWS, then starts listening for messages
    TCPServer receives the request and adds this name/Mediator pair to the associated clientsByTopic map, then
    as it wasn't already in the map and is therefore a new Subscriber, it pushes all messages for that topic in newClientMsgQ to
    the Subscriber.
Subscriber two receives these messages.

Suscriber three connects and sends subscribe requests for NEWS and WEATHER, then starts listening for messages.
    TCPServer receives the requests and adds this name/Mediator pair to both clientsByTopic maps, then
    as it wasn't already in the maps and is therefore a new Subscriber, it pushes all messages for both topics in newClientMsgQ to
    the Subscriber.
Subscriber three receives these messages.

Publisher one publishes a WEATHER message.
    TCPServer receives the request, adds the message to the newClientMsgQ ArrayList associated with WEATHER,
    and adds it to the msgQ of each client in the clientsByTopic map associated with WEATHER.
    Then, as Subscribers 1 and 3 are connected and have their isActive flag set to true, the messages are pushed to them from their msgQs.

Publisher two publishes a NEWS message.
    TCPServer receives the request, adds the message to the newClientMsgQ ArrayList associated with NEWS,
    and adds it to the msgQ of each client in the clientsByTopic map associated with NEWS.
    Then, as Subscribers 2 and 3 are connected and have their isActive flag set to true, the messages are pushed to them from their msgQs.

All subs disconnect. They kill their listening threads then send a DISC to TCPServer.
    TCPServer receives the requests, each Mediator kills its listening thread and sets its isActive flag to false, then sends DISC_ACK.
All subscribers receive DISC_ACK and close their sockets.

Publishers one and two each publish messages about their individual topics.
    TCPServer receives these messages and adds them to the newClientMsgQ ArrayList for each topic, then adds them to the msgQ of each client
    subscribed to the topic, but does not push them as the Mediators associated with the client names have isActive set to false.

Subscribers one and two reconnect
    TCPServer receives their reconnect requests, creates new Mediators for them, finds their name/Mediator mapping in clientsByTopic and replaces the old
    Mediator with the newly created one. Then the Mediators push all messages in their msgQs to their clients, then start listening for new messages to push.
Both subscribers receive a RECONNECT_ACK and start listening for messages, then receive the messages from the msgQs.

Publisher one publishes a message about WEATHER and Subscriber one receives it while it is added to Subscriber 3's msgQ.

All remaining clients disconnect as before.

Subscriber.printer is killed.

TCPServer is killed manually.
</pre>