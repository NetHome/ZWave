# OpenNetHome Java API for ZWave
This is an Java API for Sigma Design's serial protocol for ZWave controllers. It is a plain API without any application layer functionality.
The only external dependency is to the serial port interface jssc, but this is just located to a single class (ZWavePortRaw.java)
and it is simple to use any other serial port library instead.

Commands in Z-Wave are grouped in Command Classes, and each Command Class has a number of commands. In this API, each command
is a separate java-class, which is enclosed in a java class representing the Z-Wave Command Class. So to instantiate a command,
you specify **new [Command Class].[Command]**

For example, the Command Class used to switch lamps and such things on and off is called SwitchBinary and has the commands: "Set", "Get" and "Report".
The "Set" and "Get" commands are issued by the user of the API and the "Report" command is issued by the node towards the API.
To switch a lamp on you create a SwitchBinary.Set command object with the argument "true". Commands are sent to the node with the SendData-message.

In this example we switch on the lamp with node nr 17:
```java
  ZWavePort zWavePort = new ZWavePort("/dev/ttyAMA0");            // Open the port to the ZWave USB Stick
  
  Command onCommand = new SwitchBinaryCommandClass.Set(true);     // Create the command
  Message message = new SendData.Request(17, onCommand);          // Create a message with the command
  zWavePort.sendMessage(message);                                 // Send the message
  zWavePort.close();
```

There are more examples in the Examples.java file.

The interface jar is also executable and works as a very simple ZWave command line interface.
You can use it to send ZWave commands interactively and receive responses and events from the ZWave network.
This is very useful when exploring ZWave functions. The responses and events from the ZWave network are formatted as json,
to be able to use it as and machine interface as well.

Below is an example of an interactive session where a new node is entered in the network which is allocated node 8.
When the inclusion is ready the node (which is a lamp) is switched on.
Note, all rows enclosed in brackets {} are responses or events from the ZWave network, the other lines are written by the user:

    > AddNode ANY
    > {"AddNode.Event": {"status": "LEARN_READY", "node": 0}}
    > {"AddNode.Event": {"status": "NODE_FOUND", "node": 0}}
    > {"AddNode.Event": {"status": "ADDING_SLAVE", "node": 8}}
    > {"AddNode.Event": {"status": "PROTOCOL_DONE", "node": 8}}
    > AddNode STOP
    > {"AddNode.Event": {"status": "DONE", "node": 8}}
    > SwitchBinary.Set 8 1
    > {"SendData.Response": {"callbackId": 1, "status": -1}}
    > 

You can also type "h" to get help on which commands are available. All commands can also be abbreviated to the capital letters,
so the **SwitchBinary.Set**-command can also be written **SB.S**.

This API is still work in progress, so all messages and commands are not yet implemented.
Currently there are enough commands to include or exclude nodes, configure parameters and associations and to switch nodes on or off.

