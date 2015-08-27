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

package nu.nethome.zwave;

import jssc.SerialPort;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

/**
 * 0120F9819C1C012F
 */
public class ZWavePortTest {

    public static final int MESSAGE_LENGTH = 10;
    private ZWavePort zWavePort;

    class Receiver implements ZWavePort.Receiver {

        public byte[] message = new byte[1];

        @Override
        public void receiveMessage(byte[] message) {
            this.message = message;
        }

        @Override
        public void receiveFrameByte(byte frameByte) {
            message[0] = frameByte;
        }
    }

    private SerialPort port;
    private Receiver receiver;

    @Before
    public void setUp() throws Exception {
        port = mock(SerialPort.class);
        receiver = new Receiver();
        zWavePort = new ZWavePort("Name", receiver, port);
    }

    @Test
    public void canReceiveNAK() throws Exception {
        byte[] nak = {ZWavePort.NAK};
        doReturn(nak).when(port).readBytes(eq(1), anyInt());

        zWavePort.readMessage(port);

        assertThat(receiver.message[0], is((byte)ZWavePort.NAK));
    }

    @Test
    public void canReceiveAndAcknowledge10ByteMessage() throws Exception {
        byte[] sof = {ZWavePort.SOF};
        byte[] length = {MESSAGE_LENGTH + 1};
        when(port.readBytes(eq(1), anyInt())).thenReturn(sof, length);
        byte[] portData = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        byte[] data = Arrays.copyOf(portData, MESSAGE_LENGTH);
        when(port.readBytes(MESSAGE_LENGTH, 1000)).thenReturn(portData);

        zWavePort.readMessage(port);

        assertThat(receiver.message, is(data));
        verify(port).writeByte((byte) ZWavePort.ACK);
    }

    @Test
    public void canSendMessageWithChecksum() throws Exception {
        byte[] interceptedMessage = Hex.hexStringToByteArray("01080120F9819C1C012F");
        byte[] message = Hex.hexStringToByteArray("0120F9819C1C01");
        byte checksum = Hex.hexStringToByteArray("2F")[0];

        zWavePort.sendMessage(message);

        verify(port).writeByte((byte)ZWavePort.SOF);
        verify(port).writeByte((byte)(message.length + 1));
        verify(port).writeBytes(message);
//        verify(port).writeBytes(interceptedMessage);
        verify(port).writeByte(checksum);
    }

}
