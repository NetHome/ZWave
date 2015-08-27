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


import nu.nethome.zwave.messages.commands.Command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SendData {

    public static final int TRANSMIT_OPTION_ACK = 0x01;
    public static final int TRANSMIT_OPTION_AUTO_ROUTE = 0x04;
    public static final int TRANSMIT_OPTION_EXPLORE = 0x20;
    public static final int TRANSMIT_OPTIONS_ALL = TRANSMIT_OPTION_ACK | TRANSMIT_OPTION_AUTO_ROUTE | TRANSMIT_OPTION_EXPLORE;

    public static final int REQUEST_ID = 0x13;

    private static int nextCallbackId = 0;

    public static class Request extends MessageAdaptor {
        public final byte node;
        public final Command command;
        public final int transmitOptions;
        public final int callbackId;

        public Request(byte node, Command command, int transmitOptions) {
            super(REQUEST_ID, Type.REQUEST);
            this.node = node;
            this.command = command;
            this.transmitOptions = transmitOptions;
            callbackId = nextCallbackId;
            nextCallbackId = (nextCallbackId + 1) & 0xFF;
        }

        @Override
        protected void addRequestData(ByteArrayOutputStream result) throws IOException {
            byte[] commandData = command.encode();
            super.addRequestData(result);
            result.write(node);
            result.write(commandData.length);
            result.write(commandData);
            result.write(transmitOptions);
            result.write(callbackId);
        }
    }

    public static class Response extends MessageAdaptor {

        public final int callbackId;
        public final Integer status;

        public Response(byte[] message) throws DecoderException {
            super(message, REQUEST_ID, Type.RESPONSE);
            callbackId = in.read();
            if (message.length > 3) {
                status = in.read();
            } else {
                status = null;
            }
        }

        @Override
        public String toString() {
            return String.format("SendData.Response(callbackId=%d, status=%d)", callbackId, status == null ? -1 : status);
        }

        public static class Processor extends MessageProcessorAdaptor<Response> {
            @Override
            public Response process(byte[] command) throws DecoderException {
                return process(new Response(command));
            }
        }
    }
}
