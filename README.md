# Peer-to-Peer-Content-Distribution-and-Distributed-Query

In distributed computing, a peer is both a client and a server at the same time, being able to request services from other peers or providing services to other peers. 

-  The application allows peers to form an overlay and share information from the entire peer overlay. . The peers are to form an overlay by IP multicast and the information stored in a peer can be shared by any other peers. When a peer queries a question, it will multicast a request including the question to the peer overlay and the peer that has the answer to the question to respond to the requester peer. To query a question, a UDP datagram via IP multicast is used. Once the answer is found, replying the answer is to use another separate UDP
communication between the requester and the replier. 

Part 1: Peer overlay design and implementation 

1. Each peer has a unique ID.
2. Peers form a networking overlay by IP multicast
3. Each peer has a number of answers to some questions that can be shared by other peers.
4. Once a peer has a question, it needs to compose a request, including the question and
communication information such as reply IP address and port number.
5. Each peer can multicast a query request to all other peers in the overlay. After that, the peer
is waiting on the port for answers.
6. To simplify the problem, we assume that the answer to a queried question can always be
found from at least one peer.
7. Once the answer to the question is found, the replier peer (the one who has the answer) will
use a UDP datagram to send the answer to the requester peer (the one who asks the
question).
