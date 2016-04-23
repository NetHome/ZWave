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

public class RemoveNode {

    public static final byte REQUEST_ID = (byte) 0x4b;

    public static class Request extends MessageAdaptor {
        private ExclusionMode exclusionMode;

        public enum ExclusionMode {
            ANY(0x01),
            CONTROLLER(0x02),
            SLAVE(0x03),
            EXISTING(0x04),
            STOP(0x05),
            STOP_FAILED(0x06);

            private byte value;
            ExclusionMode(int value) {
                this.value = (byte)value;
            }

            public static ExclusionMode fromName(String name) throws DecoderException {
                for (ExclusionMode status : ExclusionMode.values()) {
                    if (status.name().equals(name)) {
                        return status;
                    }
                }
                throw new DecoderException("Unknown InclusionMode value");
            }

            public byte getValue() {
                return value;
            }
        }

        public Request(ExclusionMode exclusionMode) {
            super(REQUEST_ID, Type.REQUEST);
            this.exclusionMode = exclusionMode;
        }

        @Override
        protected void addRequestData(ByteArrayOutputStream result) throws IOException {
            super.addRequestData(result);
            result.write(exclusionMode.getValue());
            result.write(0xFF); // ??
        }
    }

    public static class Event extends MessageAdaptor {
        public final Status status;
        public final int nodeId;

        public enum Status {
            LEARN_READY(0x01),
            NODE_FOUND(0x02),
            REMOVING_SLAVE(0x03),
            REMOVING_CONTROLLER(0x04),
            PROTOCOL_DONE(0x05),
            DONE(0x06),
            FAILED(0x07);

            private byte value;

            Status(int value) {
                this.value = (byte)value;
            }

            public byte getValue() {
                return value;
            }

            public static Status fromValue(byte value) throws DecoderException {
                for (Status status : Status.values()) {
                    if (status.getValue() == value) {
                        return status;
                    }
                }
                throw new DecoderException("Unknown value");
            }
        }

        public Event(byte[] message) throws DecoderException {
            super(message, REQUEST_ID, Type.REQUEST);
            in.read(); // ??
            status = Status.fromValue((byte) in.read());
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
            return String.format("{\"RemoveNode.Event\": {\"status\": \"%s\", \"node\": %d}}", status.name(), nodeId);
        }
    }
}
