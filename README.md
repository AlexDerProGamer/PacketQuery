# PacketQuery
A simple event driven [Netty](https://github.com/netty/netty) based messaging API that sends messages through custom packet objects written in Java 21.\
These packets are serialized and deserialized using [Jackson](https://github.com/FasterXML/jackson-databind).
Uses [TinyLog](https://github.com/tinylog-org/tinylog) for logging messages.

Note: Only localhost connections are supported
Requires Java 21

## Add as Dependency
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
```xml
<dependency>
    <groupId>com.github.alexderprogamer</groupId>
    <artifactId>packetquery</artifactId>
    <version>version</version>
</dependency>
```
Get the latest version [here](https://github.com/AlexDerProGamer/PacketQuery/releases/latest/)

## Example Usage

### Initialization

#### Server
```java
Server server = PacketQuery.initServer(8000);
```

#### Client
```java
Client client = PacketQuery.initClient("my client", 8000);
```
There can't be two clients connected with the same name

### Packet Sending

#### Create a Packet
```java
Packet packet = new PacketBuilder()
        .write("my.test.message")
        .write("Hello")
        .write("It's me! :D")
        .build();
```

#### Server
```java
server.sendPacketToClient("my client", packet);
```
If the client is not connected/no client has the given name nothing will happen

#### Client
```java
client.sendPacketToServer(packet);
```

### Packet Handling

#### Server
```java
@Override
public void onClientMessageReceive(String clientName, Packet packet) {
    String channel = packet.read();
    if(channel.equals("my.test.message")){
        String message = packet.read();
        String message2 = packet.read();
        System.out.println(clientName + ": " + message + ", " + message2);
    }
}
```
The class needs to implement ClientPacketMessageEvent

```java
PacketQuery.addClientMessageListener(new ClientPacketMessageListener());
```

#### Client
```java
@Override
public void onServerMessageReceive(Packet packet) {
    String channel = packet.read();
    if(channel.equals("my.test.message")){
        String message = packet.read();
        String message2 = packet.read();
        System.out.println("Server: " + message + ", " + message2);
    }
}
```
The class needs to implement ServerPacketMessageEvent

```java
PacketQuery.addServerMessageListener(new ServerPacketMessageListener());
```
