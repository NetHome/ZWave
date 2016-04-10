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
 * Request the controller for information about which command classes a specific node supports.
 * The actual result of the request will be returned as an ApplicationUpdate-message.
 */
public class IsFailedNode {

    public static final byte REQUEST_ID = (byte)0x62;

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
        public final boolean isFailed;

        public Event(byte[] message) throws DecoderException {
            super(message, REQUEST_ID, Type.REQUEST);
            isFailed = in.read() != 0;
        }

        public static class Processor extends MessageProcessorAdaptor<Event> {
            @Override
            public Event process(byte[] command) throws DecoderException {
                return process(new Event(command));
            }
        }

        @Override
        public String toString() {
            return String.format("{\"IsFailedNode.Event\": {\"isFailed\": %b}}", isFailed);
        }
    }

    public static class Response extends MessageAdaptor {
        public final boolean isFailed;

        public Response(byte[] message) throws DecoderException {
            super(message, REQUEST_ID, Type.RESPONSE);
            isFailed = in.read() != 0;
        }

        public static class Processor extends MessageProcessorAdaptor<Response> {
            @Override
            public Response process(byte[] command) throws DecoderException {
                return process(new Response(command));
            }
        }

        @Override
        public String toString() {
            return String.format("{\"IsFailedNode.Response\": {\"isFailed\": %b}}", isFailed);
        }
    }
}
