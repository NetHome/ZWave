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
import java.util.List;

public class GetRoutingInfo {

    public static final int REQUEST_ID = 0x80;
    private static final int FUNCTION_ID = 3;

    public static class Request extends MessageAdaptor {
        final public int node;
        private final boolean includeBadNodes;
        private final boolean includeNonRepeaters;

        public Request(int node, boolean includeBadNodes, boolean includeNonRepeaters) {
            super(REQUEST_ID, Type.REQUEST);
            this.node = node;
            this.includeBadNodes = includeBadNodes;
            this.includeNonRepeaters = includeNonRepeaters;
        }

        @Override
        protected void addRequestData(ByteArrayOutputStream result) throws IOException {
            super.addRequestData(result);
            result.write(node);
            result.write(includeBadNodes ? 0 : 1);
            result.write(includeNonRepeaters ? 0 : 1);
            result.write(FUNCTION_ID);
        }
    }

    public static class Response extends MessageAdaptor {
        public final List<Integer> nodes;

        public Response(byte[] message) throws DecoderException {
            super(message, REQUEST_ID, Type.RESPONSE);
            this.nodes = getNodesFromBitString(in);
        }

        @Override
        public String toString() {
            String nodesString = "";
            String separator = "";
            for (int node : nodes) {
                nodesString += separator + Integer.toString(node);
                separator = ",";
            }
            return String.format("{\"GetRoutingInfo.Response\":{\"nodes\":[%s]}}", nodesString);
        }

        public static class Processor extends MessageProcessorAdaptor<Response> {
            @Override
            public Response process(byte[] command) throws DecoderException {
                return process(new Response(command));
            }
        }
    }

}
