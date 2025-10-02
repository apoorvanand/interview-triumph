'''
real time messaging system like whatsapp
1- functional requirement - 1 on 1 chat, group chat. is_typing ..., blocking users, online or last seen indicator, status, story, read recripts, vanishing messages 

Non functional requirement- 
low latency, throughput, high availability, guaranteed message ordering within a chat, end to end encryption

2. High level Design 
- clients - Mobile/web
- load balancers
- chat servers - websocket , workers 
- Message Services
- Message Queue 
- Database = cassandra/scyallaDB - store all messages, 
- Presence service
- Discovery Service 

3. Key concepts -
1. Persistent connections
2. Handline offline users 
3. message ordering 
4. read receipt
5. videos etc
