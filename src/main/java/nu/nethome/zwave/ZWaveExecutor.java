/**
 * Copyright (C) 2005-2015, Stefan Strömberg <stestr@nethome.nu>
 *
 * This file is part of OpenNetHome.
 *
 * OpenNetHome is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenNetHome is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package nu.nethome.zwave;

import nu.nethome.zwave.messages.*;
import nu.nethome.zwave.messages.commands.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class ZWaveExecutor {

    public interface MessageSender {
        void sendZWaveMessage(byte[] zWaveMessage);
    }

    public interface Printer {
        void print(String Message);
    }

    public static final String ZWAVE_TYPE = "ZWave.Type";
    public static final String ZWAVE_MESSAGE_TYPE = "ZWave.MessageType";
    public static final String ZWAVE_EVENT_TYPE = "ZWave_Message";

    private MultiMessageProcessor messageProcessor;
    private MultiCommandProcessor commandProcessor;
    private final MessageSender sender;
    private final Printer printer;

    public ZWaveExecutor(MessageSender sender, Printer printer) {
        this.sender = sender;
        this.printer = printer;
        commandProcessor = new MultiCommandProcessor();
        commandProcessor.addCommandProcessor(new CommandCode(MultiInstanceAssociation.COMMAND_CLASS, MultiInstanceAssociation.ASSOCIATION_REPORT), new MultiInstanceAssociation.Report.Processor());
        commandProcessor.addCommandProcessor(new CommandCode(Configuration.COMMAND_CLASS, Configuration.REPORT_CONFIGURATION), new Configuration.Report.Processor());
        commandProcessor.addCommandProcessor(new CommandCode(SwitchBinary.COMMAND_CLASS, SwitchBinary.SWITCH_BINARY_REPORT), new SwitchBinary.Report.Processor());
        messageProcessor = new MultiMessageProcessor();
        messageProcessor.addMessageProcessor(MemoryGetId.MEMORY_GET_ID, new MemoryGetId.Response.Processor());
        messageProcessor.addMessageProcessor(SendData.REQUEST_ID, new SendData.Response.Processor());
        messageProcessor.addMessageProcessor(AddNode.REQUEST_ID, new AddNode.Event.Processor());
        messageProcessor.addMessageProcessor(ApplicationCommand.REQUEST_ID, new ApplicationCommand.Request.Processor(commandProcessor));
    }

    public String executeCommandLine(String commandLine) {
        try {
            CommandLineParser parameters = new CommandLineParser(commandLine);
            String command = parameters.getString();
            if (command.equalsIgnoreCase("MemoryGetId")) {
                sendRequest(new MemoryGetId.Request());
            } else if (command.equalsIgnoreCase("MultiInstanceAssociation.Get") || command.equalsIgnoreCase("MIA.Get")) {
                sendCommand(parameters.getInt(1), new MultiInstanceAssociation.Get(parameters.getInt(2)));
            } else if (command.equalsIgnoreCase("MultiInstanceAssociation.Set") || command.equalsIgnoreCase("MIA.Set")) {
                sendCommand(parameters.getInt(1), new MultiInstanceAssociation.Set(parameters.getInt(2), Collections.singletonList(parseAssociatedNode(parameters.getString(3)))));
            } else if (command.equalsIgnoreCase("MultiInstanceAssociation.Remove") || command.equalsIgnoreCase("MIA.Remove")) {
                sendCommand(parameters.getInt(1), new MultiInstanceAssociation.Remove(parameters.getInt(2), Collections.singletonList(parseAssociatedNode(parameters.getString(3)))));
            } else if (command.equalsIgnoreCase("Configuration.Get") || command.equalsIgnoreCase("C.Get")) {
                sendCommand(parameters.getInt(1), new Configuration.Get(parameters.getInt(2)));
            } else if (command.equalsIgnoreCase("Configuration.Set") || command.equalsIgnoreCase("C.Set")) {
                sendCommand(parameters.getInt(1), new Configuration.Set(parameters.getInt(2), new Parameter(parameters.getInt(3), parameters.getInt(4))));
            } else if (command.equalsIgnoreCase("SwitchBinary.Set") || command.equalsIgnoreCase("SB.Set")) {
                sendCommand(parameters.getInt(1), new SwitchBinary.Set(parameters.getInt(2) != 0));
            } else if (command.equalsIgnoreCase("SwitchBinary.Get") || command.equalsIgnoreCase("SB.Get")) {
                sendCommand(parameters.getInt(1), new SwitchBinary.Get());
            } else if (command.equalsIgnoreCase("AddNode")) {
                sendRequest(new AddNode.Request(AddNode.Request.InclusionMode.fromName(parameters.getString(1))));
            } else if (command.equalsIgnoreCase("Help") || command.equalsIgnoreCase("h")) {
                print("MemoryGetId");
                print("AddNode [ANY CONTROLLER SLAVE EXISTING STOP STOP_FAILED]");
                print("MultiInstanceAssociation.Get node association");
                print("MultiInstanceAssociation.Set node association associatedNode");
                print("MultiInstanceAssociation.Remove node association associatedNode");
                print("Configuration.Get node parameter");
                print("Configuration.Set node parameter value length");
                print("SwitchBinary.Get node");
                print("SwitchBinary.Set node [0 1]");
            }
        } catch (Exception | DecoderException e) {
            print("Error: " + e.getMessage());
        }
        return "";
    }

    private void sendCommand(int node, Command command) {
        sendRequest(new SendData.Request((byte) node, command, SendData.TRANSMIT_OPTIONS_ALL));
    }


    private void sendRequest(Message request) {
        sender.sendZWaveMessage(request.encode());
    }

    public void processZWaveMessage(byte[] message) {
        if (message.length == 1) {
            if (message[0] == ZWavePort.ACK) {
                print("ACK");
            } else if (message[0] == ZWavePort.NAK) {
                print("NAK");
            } else if (message[0] == ZWavePort.CAN) {
                print("CAN");
            } else if (message[0] == ZWavePort.SOF) {
                print("SOF");
            }
        } else {
            try {
                Message received = messageProcessor.process(message);
                if (received != null) {
                    print(received.toString() + "\n\r");
                } else {
                    print("Unknown message: " + Hex.asHexString(message));
                }
            } catch (DecoderException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private AssociatedNode parseAssociatedNode(String s) {
        String[] parts = s.split("\\.");
        if (parts.length == 2) {
            return new AssociatedNode(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
        return new AssociatedNode(Integer.parseInt(s));
    }

    private void print(String string) {
        printer.print(string);
    }

    static class CommandLineParser {
        Iterator<String> elementsIterator;
        List<String> elements;

        CommandLineParser(String line) {
            elements = Arrays.asList(line.split(" "));
            this.elementsIterator = elements.iterator();
        }

        public String getString() {
            return elementsIterator.next();
        }

        public int getInt() {
            return Integer.parseInt(getString());
        }

        public int getInt(int position) {
            return Integer.parseInt(getString(position));
        }

        private String getString(int position) {
            return elements.get(position);
        }
    }
}
