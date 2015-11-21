package nu.nethome.zwave;

import nu.nethome.zwave.messages.ApplicationCommand;
import nu.nethome.zwave.messages.SendData;
import nu.nethome.zwave.messages.commandclasses.CommandArgument;
import nu.nethome.zwave.messages.commandclasses.SwitchBinaryCommandClass;
import nu.nethome.zwave.messages.commandclasses.framework.Command;
import nu.nethome.zwave.messages.commandclasses.framework.CommandCode;
import nu.nethome.zwave.messages.commandclasses.framework.MultiCommandProcessor;
import nu.nethome.zwave.messages.framework.DecoderException;
import nu.nethome.zwave.messages.framework.Message;
import nu.nethome.zwave.messages.framework.MultiMessageProcessor;

import java.io.IOException;

/**
 *
 */
public class Examples {

    public static final byte LAMP_NODE = (byte) 4;

    /**
     * This is an example of sending a command to a node. Commands i Z-Wave are grouped in Command Classes,
     * and each Command Class has a number of commands. The Command Class used to switch lamps and such things
     * on and off is called SwitchBinary and has the commands: "Set", "Get" and "Report".
     * Commands are sent to the node with the SendData-message.
     *
     * @throws IOException
     */
    static public void switchLampOn() throws IOException {
        NetHomePort zWavePort = new NetHomePort("ssgserver2", 8005);    // Open the port to the ZWave USB Stick

        Command onCommand = new SwitchBinaryCommandClass.Set(true);     // Create the command
        Message message = new SendData.Request(LAMP_NODE, onCommand);   // Create a message with the command
        zWavePort.sendMessage(message);                                 // Send the message
        zWavePort.close();
    }

    /**
     * This is an example of receiving a command from a node. When issuing the "Get"-command from the
     * SwitchBinary Command Class, the result comes as an ApplicationCommand Request with a  "Report"-command to
     * the host node and you have to implement a handler for that command to get the result. Note that the result will
     * be delivered asynchronously by the port.
     *
     * @throws IOException
     */
    static public void getLampState() throws IOException, InterruptedException {
        NetHomePort zWavePort = new NetHomePort("ssgserver2", 8005);
        MultiMessageProcessor messageProcessor = new MultiMessageProcessor();

        // Add a processor for the SwitchBinary.Report Command
        messageProcessor.addCommandProcessor(new SwitchBinaryCommandClass.Report.Processor(){
            @Override
            protected SwitchBinaryCommandClass.Report process(SwitchBinaryCommandClass.Report command, CommandArgument node) throws DecoderException {
                System.out.println("State of node " + node.sourceNode + " is " + (command.isOn ? "On" : "Off"));
                return command;
            }
        });
        zWavePort.setReceiver(messageProcessor);                        // Give the message processor to the port

        Command getCommand = new SwitchBinaryCommandClass.Get();        // Create the Get command
        Message message = new SendData.Request(LAMP_NODE, getCommand);  // Create the Message with the command
        zWavePort.sendMessage(message);                                 // Send the message

        // Wait a while for the response. The response will be an asynchronous call from the node that will end up in the
        // SwitchBinaryCommandClass.Report.Processor we created which will print the result
        Thread.sleep(5000);

        zWavePort.close();
    }
}
