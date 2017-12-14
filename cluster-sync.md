#### Cluster Synchronization
In order to to synchronize data across multiple node Clustering mechanism is required. Following components would require data synchronization.
#### WebSocket Cluster
As we know, WebSocket protocol is not a stateless protocol hence connection persist on individual server node. In order to communicate with specific client, server need to first find out the server node on which client is connected and then passes message to the node with client info and message data.

This discovery mechanism can be simplified by using Redis Pub/Sub feature. Each time a node is connected with redis, it creates a new channel using node id as name. Now whenever a client is connected, its session information along with channel_name is stored as map in Redis where client id serves as key. Thus whenever we need to send message to client 1, Redis can lookup channel_name against client 1 and message is published to that channel. On receiving end, persisted connection is retrieved and published message is sent to client.

Life cycle of WebSocket is Following:
- On connect events
- On disconnect events
- On receive events
