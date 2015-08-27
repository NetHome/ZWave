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

import nu.nethome.zwave.messages.DecoderException;
import nu.nethome.zwave.messages.MessageAdaptor;

import java.io.ByteArrayInputStream;

/*
 * event,ZWave_Message,Direction,Out,Value,0020
 */
public class MemoryGetId  {

    public static final byte MEMORY_GET_ID = (byte) 0x20;

    public static class Request extends MessageAdaptor {
        public Request() {
            super(MEMORY_GET_ID, Type.REQUEST);
        }
    }

    public static class Response extends MessageAdaptor {

        public static final int EXPECTED_LENGTH = 7;
        public final int homeId;
        public final int nodeId;

        public Response(byte[] message) throws DecoderException {
            super(message, MEMORY_GET_ID, Type.RESPONSE);
            homeId = (in.read() << 24) + (in.read() << 16) + (in.read() << 8) + in.read();
            nodeId = in.read();
        }

        @Override
        public String toString() {
            return String.format("MemoryGetId.Response(homeId=%X, nodeId = %d)", homeId, nodeId);
        }

        public static class Processor extends MessageProcessorAdaptor<Response> {
            @Override
            public Response process(byte[] command) throws DecoderException {
                return process(new Response(command));
            }
        }
    }
}
