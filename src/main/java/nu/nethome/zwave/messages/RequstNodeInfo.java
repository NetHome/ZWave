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
 * Controller responds with: ApplicationUpdate(0x49)
 */
public class RequstNodeInfo {

    public static final byte REQUEST_ID = (byte)0x60;

    public static class Request extends MessageAdaptor {
        private int node;


        public Request(int node) {
            super(REQUEST_ID, Type.REQUEST);
            this.node = node;
        }

        @Override
        protected void addRequestData(ByteArrayOutputStream result) throws IOException {
            super.addRequestData(result);
            result.write(node);
        }
    }

    public static class Event extends MessageAdaptor {
        public final int nodeId;

        public Event(byte[] message) throws DecoderException {
            super(message, REQUEST_ID, Type.REQUEST);
            in.read(); // ??
            nodeId = in.read();
        }

        public static class Processor extends MessageProcessorAdaptor<Event> {
            @Override
            public Event process(byte[] command) throws DecoderException {
                return process(new Event(command));
            }
        }

        @Override
        public String toString() {
            return String.format("{\"RequestNodeInfo.Event\": {\"node\": %d}}", nodeId);
        }
    }

    public static class Response extends MessageAdaptor {
        public final int nodeId;

        public Response(byte[] message) throws DecoderException {
            super(message, REQUEST_ID, Type.RESPONSE);
            nodeId = in.read();
        }

        public static class Processor extends MessageProcessorAdaptor<Response> {
            @Override
            public Response process(byte[] command) throws DecoderException {
                return process(new Response(command));
            }
        }

        @Override
        public String toString() {
            return String.format("{\"RequestNodeInfo.Response\": {\"node\": %d}}", nodeId);
        }
    }
}
