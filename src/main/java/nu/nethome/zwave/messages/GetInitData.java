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

import java.util.ArrayList;
import java.util.List;

public class GetInitData {

    public enum ControllerMode {
        SLAVE,
        CONTROLLER
    }

    public enum ControllerType {
        PRIMARY,
        SECONDARY
    }

    private static final int NUMBER_OF_NODE_BYTES = 29;
    public static final byte REQUEST_ID = (byte) 0x02;

    public static class Request extends MessageAdaptor {
        public Request() {
            super(REQUEST_ID, Type.REQUEST);
        }
    }

    public static class Response extends MessageAdaptor {
        public final ControllerMode mode;
        public final ControllerType type;
        public final List<Integer> nodes;

        public Response(byte[] message) throws DecoderException {
            super(message, REQUEST_ID, Type.RESPONSE);
            int unknown = in.read();
            int controller = in.read();
            mode = (controller & 0x01) != 0 ? ControllerMode.SLAVE : ControllerMode.CONTROLLER;
            type = (controller & 0x04) != 0 ? ControllerType.SECONDARY : ControllerType.PRIMARY;
            if (in.read() != NUMBER_OF_NODE_BYTES) {
                throw new DecoderException("Wrong number of node bytes");
            }
            int nodeId = 1;
            List<Integer> nodes = new ArrayList<>();
            for (int nodeByteCounter = 0; nodeByteCounter < NUMBER_OF_NODE_BYTES; nodeByteCounter++) {
                int nodeByte = in.read();
                for (int bit = 0; bit < 8; bit++) {
                    if ((nodeByte & 1) == 1) {
                        nodes.add(nodeId);
                    }
                    nodeByte >>= 1;
                    nodeId++;
                }
            }
            this.nodes = nodes;
        }

        @Override
        public String toString() {
            String nodesString = "";
            String separator = "";
            for (int node : nodes) {
                nodesString += separator + Integer.toString(node);
                separator = ",";
            }
            return String.format("{\"GetInitData.Response\":{\"mode\":\"%s\", \"type\":\"%s\", \"nodes\":[%s]}}", mode.name(), type.name(), nodesString);
        }

        public static class Processor extends MessageProcessorAdaptor<Response> {
            @Override
            public Response process(byte[] command) throws DecoderException {
                return process(new Response(command));
            }
        }
    }
}
