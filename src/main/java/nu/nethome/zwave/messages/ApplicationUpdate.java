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

package nu.nethome.zwave.messages;

import nu.nethome.zwave.messages.framework.DecoderException;
import nu.nethome.zwave.messages.framework.MessageAdaptor;
import nu.nethome.zwave.messages.framework.MessageProcessorAdaptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 */
public class ApplicationUpdate {

    public static final byte REQUEST_ID = (byte)0x49;
    public static final int NODE_INFO_RECEIVED = 0x84;
    public static final int NODE_INFO_REQ_DONE = 0x82;
    public static final int NODE_INFO_REQ_FAILED = 0x81;
    public static final int ROUTING_PENDING = 0x80;
    public static final int NEW_ID_ASSIGNED = 0x40;
    public static final int DELETE_DONE = 0x20;
    public static final int SUC_ID = 0x10;


    public static class Event extends MessageAdaptor {
        public final int nodeId;
        public final int updateState;
        public final byte[] commandClasses;


        public Event(byte[] message) throws DecoderException {
            super(message, REQUEST_ID, Type.REQUEST);
            updateState = in.read();
            if (updateState == NODE_INFO_RECEIVED) {
                nodeId = in.read();
                int numberOfCommandClasses = in.read() - 3;
                in.read();
                in.read();
                in.read();
                commandClasses = new byte[numberOfCommandClasses];
                in.read(commandClasses, 0, numberOfCommandClasses);
            } else {
                nodeId = 0;
                commandClasses = new byte[0];
            }
        }

        public static class Processor extends MessageProcessorAdaptor<Event> {
            @Override
            public Event process(byte[] command) throws DecoderException {
                return process(new Event(command));
            }
        }

        @Override
        public String toString() {
            String commandClassesString = "";
            String separator = "";
            for (byte commandClass : commandClasses) {
                commandClassesString += String.format("%s%02X", separator, commandClass);
                separator = ",";
            }
            return String.format("ApplicationUpdate.Event(node:%d, classes:%s)", nodeId, commandClassesString);
        }
    }
}
