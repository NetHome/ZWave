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
import nu.nethome.zwave.messages.commandclasses.*;
import nu.nethome.zwave.messages.commandclasses.framework.Command;
import nu.nethome.zwave.messages.framework.DecoderException;
import nu.nethome.zwave.messages.framework.Message;
import nu.nethome.zwave.messages.framework.MultiMessageProcessor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ZWaveExecutor {

    public interface MessageSender {
        void sendZWaveMessage(byte[] zWaveMessage);
    }

    public interface Printer {
        void println(String message);
        void print(String message);
    }

    private MultiMessageProcessor messageProcessor;
    private final MessageSender sender;
    private final Printer printer;

    public ZWaveExecutor(MessageSender sender, Printer printer) {
        this.sender = sender;
        this.printer = printer;
        messageProcessor = new MultiMessageProcessor();
        addMessageProcessors();
        addCommandProcessors();
        println("OpenNetHome ZWave Command Prompt. Type h for help");
        printPrompt();
    }

    private void addMessageProcessors() {
        messageProcessor.addMessageProcessor(MemoryGetId.MEMORY_GET_ID, Message.Type.RESPONSE, new MemoryGetId.Response.Processor());
        messageProcessor.addMessageProcessor(SendData.REQUEST_ID, Message.Type.REQUEST, new SendData.Request.Processor());
        messageProcessor.addMessageProcessor(SendData.REQUEST_ID, Message.Type.RESPONSE, new SendData.Response.Processor());
        messageProcessor.addMessageProcessor(AddNode.REQUEST_ID, Message.Type.REQUEST, new AddNode.Event.Processor());
        messageProcessor.addMessageProcessor(RemoveNode.REQUEST_ID, Message.Type.REQUEST, new RemoveNode.Event.Processor());
        messageProcessor.addMessageProcessor(GetInitData.REQUEST_ID, Message.Type.RESPONSE, new GetInitData.Response.Processor());
        messageProcessor.addMessageProcessor(ApplicationUpdate.REQUEST_ID, Message.Type.REQUEST, new ApplicationUpdate.Event.Processor());
        messageProcessor.addMessageProcessor(RequestNodeInfo.REQUEST_ID, Message.Type.RESPONSE, new RequestNodeInfo.Response.Processor());
        messageProcessor.addMessageProcessor(RequestNodeInfo.REQUEST_ID, Message.Type.REQUEST, new RequestNodeInfo.Event.Processor());
        messageProcessor.addMessageProcessor(IsFailedNode.REQUEST_ID, Message.Type.RESPONSE, new IsFailedNode.Response.Processor());
        messageProcessor.addMessageProcessor(GetRoutingInfo.REQUEST_ID, Message.Type.RESPONSE, new GetRoutingInfo.Response.Processor());
    }

    private void addCommandProcessors() {
        messageProcessor.addCommandProcessor(new MultiInstanceAssociationCommandClass.Report.Processor());
        messageProcessor.addCommandProcessor(new ConfigurationCommandClass.Report.Processor());
        messageProcessor.addCommandProcessor(new SwitchBinaryCommandClass.Report.Processor());
        messageProcessor.addCommandProcessor(new SwitchBinaryCommandClass.Set.Processor());
        messageProcessor.addCommandProcessor(new MultiLevelSwitchCommandClass.Report.Processor());
        messageProcessor.addCommandProcessor(new BasicCommandClass.Report.Processor());
        messageProcessor.addCommandProcessor(new BasicCommandClass.Set.Processor());
        messageProcessor.addCommandProcessor(new MultiInstanceCommandClass.EncapsulationV2.Processor(messageProcessor.getDefaultCommandProcessor()));
        messageProcessor.addCommandProcessor(new CentralSceneCommandClass.Set.Processor());
        messageProcessor.addCommandProcessor(new ApplicationSpecificCommandClass.Report.Processor());
    }

    public String executeCommandLine(String commandLine) {
        try {
            CommandLineParser parameters = new CommandLineParser(commandLine);
            String command = parameters.getString();
            if (command.equalsIgnoreCase("MemoryGetId") || command.equalsIgnoreCase("MGI")) {
                sendRequest(new MemoryGetId.Request());
            } else if (command.equalsIgnoreCase("GetInitData") || command.equalsIgnoreCase("GID")) {
                sendRequest(new GetInitData.Request());
            } else if (command.equalsIgnoreCase("IsFailedNode") || command.equalsIgnoreCase("IFN")) {
                sendRequest(new IsFailedNode.Request(parameters.getInt(1)));
            } else if (command.equalsIgnoreCase("GetRoutingInfo") || command.equalsIgnoreCase("GRI")) {
                sendRequest(new GetRoutingInfo.Request(parameters.getInt(1), true, true));
            } else if (command.equalsIgnoreCase("Association.Get") || command.equalsIgnoreCase("A.G")) {
                sendCommand(parameters.getInt(1), new AssociationCommandClass.Get(parameters.getInt(2)));
            }  else if (command.equalsIgnoreCase("MultiInstanceAssociation.Get") || command.equalsIgnoreCase("MIA.G")) {
                sendCommand(parameters.getInt(1), new MultiInstanceAssociationCommandClass.Get(parameters.getInt(2)));
            } else if (command.equalsIgnoreCase("MultiInstanceAssociation.Set") || command.equalsIgnoreCase("MIA.S")) {
                sendCommand(parameters.getInt(1), new MultiInstanceAssociationCommandClass.Set(parameters.getInt(2), Collections.singletonList(parseAssociatedNode(parameters.getString(3)))));
            } else if (command.equalsIgnoreCase("MultiInstanceAssociation.Remove") || command.equalsIgnoreCase("MIA.R")) {
                sendCommand(parameters.getInt(1), new MultiInstanceAssociationCommandClass.Remove(parameters.getInt(2), Collections.singletonList(parseAssociatedNode(parameters.getString(3)))));
            } else if (command.equalsIgnoreCase("Configuration.Get") || command.equalsIgnoreCase("C.G")) {
                sendCommand(parameters.getInt(1), new ConfigurationCommandClass.Get(parameters.getInt(2)));
            } else if (command.equalsIgnoreCase("Configuration.Set") || command.equalsIgnoreCase("C.S")) {
                sendCommand(parameters.getInt(1), new ConfigurationCommandClass.Set(parameters.getInt(2), new Parameter(parameters.getInt(3), parameters.getInt(4))));
            } else if (command.equalsIgnoreCase("SwitchBinary.Set") || command.equalsIgnoreCase("SB.S")) {
                sendCommand(parameters.getInt(1), new SwitchBinaryCommandClass.Set(parameters.getInt(2) != 0));
            } else if (command.equalsIgnoreCase("MultiLevelSwitch.Set") || command.equalsIgnoreCase("MLS.S")) {
                sendCommand(parameters.getInt(1), new MultiLevelSwitchCommandClass.Set(parameters.getInt(2)));
            } else if (command.equalsIgnoreCase("ApplicationSpecific.Get") || command.equalsIgnoreCase("AS.G")) {
                sendCommand(parameters.getInt(1), new ApplicationSpecificCommandClass.Get());
            } else if (command.equalsIgnoreCase("SwitchBinary.Get") || command.equalsIgnoreCase("SB.G")) {
                sendCommand(parameters.getInt(1), new SwitchBinaryCommandClass.Get());
            } else if (command.equalsIgnoreCase("MultiLevelSwitch.Get") || command.equalsIgnoreCase("MLS.G")) {
                sendCommand(parameters.getInt(1), new MultiLevelSwitchCommandClass.Get());
            } else if (command.equalsIgnoreCase("AddNode") || command.equalsIgnoreCase("AN")) {
                sendRequest(new AddNode.Request(AddNode.Request.InclusionMode.fromName(parameters.getString(1))));
            } else if (command.equalsIgnoreCase("RemoveNode") || command.equalsIgnoreCase("RN")) {
                sendRequest(new RemoveNode.Request(RemoveNode.Request.ExclusionMode.fromName(parameters.getString(1))));
            } else if (command.equalsIgnoreCase("RequestNodeInfo") || command.equalsIgnoreCase("RNI")) {
                sendRequest(new RequestNodeInfo.Request(parameters.getInt(1)));
            } else if (command.equalsIgnoreCase("Help") || command.equalsIgnoreCase("h")) {
                println("Messages:");
                println(" MemoryGetId");
                println(" GetInitData");
                println(" AddNode [ANY CONTROLLER SLAVE EXISTING STOP STOP_FAILED]");
                println(" RemoveNode [ANY CONTROLLER SLAVE EXISTING STOP STOP_FAILED]");
                println(" GetRoutingInfo node");
                println(" RequestNodeInfo node ");
                println(" IsFailedNode node ");
                println("Commands;");
                println(" MultiInstanceAssociation.Get node association");
                println(" MultiInstanceAssociation.Set node association associatedNode");
                println(" MultiInstanceAssociation.Remove node association associatedNode");
                println(" Configuration.Get node parameter");
                println(" Configuration.Set node parameter value length");
                println(" SwitchBinary.Get node");
                println(" SwitchBinary.Set node [0 1]");
                println(" MultiLevelSwitch.Get node");
                println(" MultiLevelSwitch.Set node [0 - 99]");
                println(" ApplicationSpecific.Get node");
            } else {
                println("Error: Unknown command");
            }
        } catch (Exception e) {
            println("Error: " + e.getMessage());
        }
        printPrompt();
        return "";
    }

    private void printPrompt() {
        print("> ");
    }

    private void sendCommand(int node, Command command) {
        sendRequest(new SendData.Request((byte) node, command, SendData.TRANSMIT_OPTIONS_ALL));
    }


    private void sendRequest(Message request) {
        sender.sendZWaveMessage(request.encode());
    }

    public Message processZWaveMessage(byte[] message) {
        Message result = null;
        if (message.length == 1) {
            if (message[0] == ZWaveRawSerialPort.ACK) {
                println("ACK");
            } else if (message[0] == ZWaveRawSerialPort.NAK) {
                println("NAK");
            } else if (message[0] == ZWaveRawSerialPort.CAN) {
                println("CAN");
            } else if (message[0] == ZWaveRawSerialPort.SOF) {
                println("SOF");
            }
        } else {
            try {
                result = messageProcessor.process(message);
                if (result != null) {
                    println(result.toString());
                } else {
                    println("Unknown message: " + Hex.asHexString(message));
                }
            } catch (DecoderException e) {
                println(e.getMessage());
            }
        }
        printPrompt();
        return result;
    }

    private AssociatedNode parseAssociatedNode(String s) {
        String[] parts = s.split("\\.");
        if (parts.length == 2) {
            return new AssociatedNode(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
        return new AssociatedNode(Integer.parseInt(s));
    }

    private void println(String string) {
        printer.println(string);
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
