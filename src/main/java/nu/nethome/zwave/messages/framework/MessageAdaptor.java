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

package nu.nethome.zwave.messages.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageAdaptor implements Message {
    public static final byte Z_WAVE_REQUEST = 0;
    public static final int NUMBER_OF_NODE_BYTES = 29;
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
        int requestType = in.read();
        DecoderException.assertTrue(requestType == (type == Type.REQUEST ? 0 : 1), "Unexpected request type: " + requestType);
        DecoderException.assertTrue(in.read() == requestId, "Unexpected message type");
    }

    public static MessageId decodeMessageId(byte[] message) {
        if (message == null || message.length < 2) {
            return new MessageId(0, Type.REQUEST);
        }
        return new MessageId(((int)(message[1])) & 0xFF, message[0] == 0 ? Type.REQUEST : Type.RESPONSE);
    }

    public static List<Integer> getNodesFromBitString(ByteArrayInputStream inputStream) throws DecoderException {
        int nodeId = 1;
        List<Integer> nodes = new ArrayList<>();
        for (int nodeByteCounter = 0; nodeByteCounter < NUMBER_OF_NODE_BYTES; nodeByteCounter++) {
            int nodeByte = inputStream.read();
            for (int bit = 0; bit < 8; bit++) {
                if ((nodeByte & 1) == 1) {
                    nodes.add(nodeId);
                }
                nodeByte >>= 1;
                nodeId++;
            }
        }
        return nodes;
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
