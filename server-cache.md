## Server Caching Module
Server Caching Module will provide caching capabilities to various system components.

>  Following component would required Caching
-  User Session Caching
-  WebSocket Session Caching
- Active Tasks/Workflow Details Caching
- MRU Posts Caching

#### Distributed Cache:
Now a days multiple oss distributed caching solutions are available like memcache, redis and ehcache. The distributed cache server itself scales, manges and synchronizes indvidual cache node thus simplying the management process. By using distributed caching, we can also avoid the need of  cache synchronization in individual servers.

#### User Session Caching:
Shiro filter will use distributed cache instead of in memory to store user session. This will be enable multiple web servers to transparently access web user session.

#### Web Socket Session Caching:
As soon as a user is connected through web socket, a map value of user identifier, server id and web socket session info is placed in caching. By placing session info in cache, it can be access by all web servers to find out all the required information like web socket server id and session information.

#### Active Tasks/Workflow Details Caching:
Active Task and jobs status can be cached in order to put lock on task to avoid simultaneous changes. Once a task is marked locked, it cannot be edited by other users. It can also help to notify other readers about in progress changes.

#### MRU Posts Cache
Posts Most recently used can be cached in order to avoid querying same post again and again. The items are accessed through an adapter layer(like google cache). While putting post in cache, the specified timestamp value will be marked to expire cache. After expiration the post will be fetched from database and the fresh copy is placed in cache. We also need to define maximum no of posts to cache. Once the number is reached least recent used post will be evicted from cache.  
