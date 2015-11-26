package nu.nethome.zwave;

import jssc.SerialPort;
import jssc.SerialPortException;
import nu.nethome.zwave.messages.framework.UndecodedMessage;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
public class ZWavePortTest {

    private SerialPort serialPort;
    private ZWavePort zPort;
    private byte[] rawMessage1 = {0, 1, 2};
    private UndecodedMessage.Message message1;
    private byte[] rawMessage2 = {0, 1, 3};
    private UndecodedMessage.Message message2;

    @Before
    public void setUp() throws Exception {
        serialPort = mock(SerialPort.class);
        zPort = new ZWavePort("Test", serialPort);
        message1 = new UndecodedMessage.Message(rawMessage1);
        message2 = new UndecodedMessage.Message(rawMessage2);
    }

    @Test
    public void sendMessage() throws Exception {
        zPort.sendMessage(message1);

        verifyMessageSent(rawMessage1);
    }

    @Test
    public void sendTwoMessagesStoresOne() throws Exception {
        zPort.sendMessage(message1);
        zPort.sendMessage(message2);

        verifyMessageSent(rawMessage1);
    }

    byte[] ack = {ZWavePortRaw.ACK};

    @Test
    public void sendTwoMessagesWithAckBetween() throws Exception {
        zPort.sendMessage(message1);
        verifyMessageSent(rawMessage1);

        when(serialPort.readBytes(eq(1), anyInt())).thenReturn(ack);

        zPort.readMessage(serialPort);
        zPort.sendMessage(message2);
        verifyMessageSent(rawMessage2);
    }

    private void verifyMessageSent(byte[] rawMessage1) throws SerialPortException {
        verify(serialPort).writeBytes(rawMessage1);
    }
}
