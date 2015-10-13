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

package nu.nethome.zwave.messages.commandclasses;

import nu.nethome.zwave.messages.commandclasses.framework.*;
import nu.nethome.zwave.messages.framework.DecoderException;
import nu.nethome.zwave.messages.framework.Message;
import nu.nethome.zwave.messages.framework.MessageProcessorAdaptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 */
public class MultiInstanceCommandClass implements CommandClass {

    // Version 1
    public static final int GET = 0x04;
    public static final int REPORT = 0x05;
    public static final int V1_ENCAP = 0x06;

    // Version 2
    public static final int ENDPOINT_GET = 0x07;
    public static final int ENDPOINT_REPORT = 0x08;
    public static final int CAPABILITY_GET = 0x09;
    public static final int CAPABILITY_REPORT = 0x0a;
    public static final int ENDPOINT_FIND = 0x0b;
    public static final int ENDPOINT_FIND_REPORT = 0x0c;
    public static final int V2_ENCAP = 0x0d;

    public static final int COMMAND_CLASS = 0x60;
    public static final int ENCAPSULATION_HEADER_LENGTH = 4;

    public static class Encapsulation extends CommandAdapter {
        public final int instance;
        public final Command command;

        public Encapsulation(int instance, Command command) {
            super(COMMAND_CLASS, V2_ENCAP);
            this.instance = instance;
            this.command = command;
        }

        @Override
        protected void addCommandData(ByteArrayOutputStream result) {
            super.addCommandData(result);
            result.write(1); // ??
            result.write(instance);
            byte[] commandData = command.encode();
            result.write(commandData, 0, commandData.length);
        }

        public Encapsulation(byte[] data) throws DecoderException {
            super(data, COMMAND_CLASS, V2_ENCAP);
            in.read(); // ?? Seems to be 0
            instance = in.read();
            int commandLength = data.length - ENCAPSULATION_HEADER_LENGTH;
            byte[] commandData = new byte[commandLength];
            in.read(commandData, 0, commandLength);
            command = new UndecodedCommand(commandData);
        }

        @Override
        public String toString() {
            return String.format("MultiInstance.Encapsulation(instance:%d, command:{%s})", instance, command.toString());
        }

        public static class Processor extends CommandProcessorAdapter<Encapsulation> {

            private CommandProcessor commandProcessor;

            public Processor(CommandProcessor commandProcessor) {
                this.commandProcessor = commandProcessor;
            }

            public Processor() {
                this.commandProcessor = new MultiCommandProcessor();
            }

            @Override
            public Encapsulation process(byte[] command, NodeInstance node) throws DecoderException {
                return process(new Encapsulation(command), node);
            }

            @Override
            protected Encapsulation process(Encapsulation command, NodeInstance node) throws DecoderException {
                Command realCommand = commandProcessor.process(command.command.encode(), new NodeInstance(node.node, command.instance));
                return new Encapsulation(command.instance, realCommand);
            }
        }
    }
}
