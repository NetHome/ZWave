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

import java.util.Arrays;

/**
 *
 */
public class ApplicationUpdate {

    public static final byte REQUEST_ID = (byte) 0x49;
    public static final int NODE_INFO_RECEIVED = 0x84;
    public static final int NODE_INFO_REQ_DONE = 0x82;
    public static final int NODE_INFO_REQ_FAILED = 0x81;
    public static final int ROUTING_PENDING = 0x80;
    public static final int NEW_ID_ASSIGNED = 0x40;
    public static final int DELETE_DONE = 0x20;
    public static final int SUC_ID = 0x10;
    public static final int COMMAND_CLASS_MARK = 239;


    public static class Event extends MessageAdaptor {
        public final int nodeId;
        public final int updateState;
        public final byte[] supportedCommandClasses;
        public final byte[] controlledCommandClasses;


        public Event(byte[] message) throws DecoderException {
            super(message, REQUEST_ID, Type.REQUEST);
            updateState = in.read();
            if (updateState == NODE_INFO_RECEIVED) {
                nodeId = in.read();
                int numberOfCommandClasses = in.read() - 3;
                in.read();
                in.read();
                in.read();
                byte[] allCommandClasses = new byte[numberOfCommandClasses];
                in.read(allCommandClasses, 0, numberOfCommandClasses);
                int separatorPosition = find(allCommandClasses, (byte) COMMAND_CLASS_MARK);
                supportedCommandClasses = Arrays.copyOfRange(allCommandClasses, 0, separatorPosition);
                controlledCommandClasses = Arrays.copyOfRange(allCommandClasses, separatorPosition + (separatorPosition == numberOfCommandClasses ? 0 : 1), numberOfCommandClasses);
            } else {
                nodeId = 0;
                supportedCommandClasses = new byte[0];
                controlledCommandClasses = new byte[0];
            }
        }

        public int find(byte[] array, byte value) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == value) {
                    return i;
                }
            }
            return array.length;
        }

        public static class Processor extends MessageProcessorAdaptor<Event> {
            @Override
            public Event process(byte[] command) throws DecoderException {
                return process(new Event(command));
            }
        }

        @Override
        public String toString() {
            return String.format("{\"ApplicationUpdate.Event\": {\"updateState\": %d, \"node\": %d, \"supportedClasses\": [%s], \"controlledClasses\": [%s]}}",
                    updateState, nodeId, asStringList(supportedCommandClasses), asStringList(controlledCommandClasses));
        }
    }

    private static String asStringList(byte[] supportedCommandClasses1) {
        String commandClassesString = "";
        String separator = "";
        for (byte commandClass : supportedCommandClasses1) {
            int cc = ((int) commandClass) & 0xFF;
            commandClassesString += String.format("%s%d", separator, cc);
            separator = ",";
        }
        return commandClassesString;
    }
}
