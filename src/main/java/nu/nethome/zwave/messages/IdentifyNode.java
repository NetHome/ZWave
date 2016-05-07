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

public class IdentifyNode {

    public static final int REQUEST_ID = 0x41;

    public static class Request extends MessageAdaptor {
        final public int node;

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

    public static class Response extends MessageAdaptor {
        public final boolean isListening;
        public final boolean isRouting;
        public final boolean isFrequentlyListening;
        public final int version;
        public final int basicDeviceClass;
        public final int genericDeviceClass;
        public final int specificDeviceClass;

        public Response(byte[] message) throws DecoderException {
            super(message, REQUEST_ID, Type.RESPONSE);
            int byte1 = in.read();
            this.isListening = (byte1 & 0x80) != 0;
            this.isRouting = (byte1 & 0x40) != 0;
            this.version = (byte1 & 0x07) + 1;
            int byte2 = in.read();
            this.isFrequentlyListening = (byte2 & 0x60) != 0;
            in.read();
            this.basicDeviceClass = in.read();
            this.genericDeviceClass = in.read();
            this.specificDeviceClass = in.read();
        }

        @Override
        public String toString() {
            return String.format("{\"IdentifyNode.Response\":{\"isListening\":%b, \"isRouting\":%b, \"isFrequentlyListening\":%b, \"version\":%d, \"basicDeviceClass\":%d, \"genericDeviceClass\":%d, \"specificDeviceClass\":%d, }}",
                    isListening, isRouting, isFrequentlyListening, version, basicDeviceClass, genericDeviceClass, specificDeviceClass);
        }

        public static class Processor extends MessageProcessorAdaptor<Response> {
            @Override
            public Response process(byte[] command) throws DecoderException {
                return process(new Response(command));
            }
        }
    }

}
