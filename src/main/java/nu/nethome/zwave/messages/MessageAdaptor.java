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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MessageAdaptor implements Message {
    public static final byte Z_WAVE_REQUEST = 0;
    private final int requestId;
    private final Type type;
    protected ByteArrayInputStream in;

    public MessageAdaptor(int requestId, Type type) {
        this.requestId = requestId;
        this.type = type;
    }

    public MessageAdaptor(byte[] messageData, int requestId, Type type) throws DecoderException {
        in = new ByteArrayInputStream(messageData);
        this.requestId = requestId;
        this.type = type;
        DecoderException.assertTrue(in.read() == (type == Type.REQUEST ? 0 : 1), "Unexpected message type");
        DecoderException.assertTrue(in.read() == requestId, "Unexpected message type");
    }

    public static int decodeMessageId(byte[] message) {
        return (message != null && message.length >= 2) ? ((int)(message[1])) & 0xFF : 0;
    }

    @Override
    public byte[] encode() {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            addRequestData(result);
            return result.toByteArray();
        } catch (IOException e) {
            // This should not happen
            return new byte[0];
        }
    }

    protected void addRequestData(ByteArrayOutputStream result) throws IOException {
        result.write(Z_WAVE_REQUEST);
        result.write(requestId);
    }

    @Override
    public int getRequestId() {
        return requestId;
    }

    @Override
    public Type getType() {
        return type;
    }
}
