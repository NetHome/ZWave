package nu.nethome.zwave;

import jssc.SerialPort;
import jssc.SerialPortException;
import nu.nethome.zwave.messages.framework.UndecodedMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ZWaveSerialPortTest {

    private ZWaveSerialPort zPort;
    private byte[] rawMessage1 = {0, 1, 2};
    private UndecodedMessage.Message message1;
    private byte[] rawMessage2 = {0, 1, 3};
    private UndecodedMessage.Message message2;
    private ZWaveRawSerialPort rawPort;
    private ZWaveRawSerialPort.Receiver receiver;

    @Before
    public void setUp() throws Exception {
        rawPort = mock(ZWaveRawSerialPort.class);
        zPort = new ZWaveSerialPort(rawPort);
        ArgumentCaptor<ZWaveRawSerialPort.Receiver> captor = ArgumentCaptor.forClass(ZWaveRawSerialPort.Receiver.class);
        verify(rawPort).setReceiver(captor.capture());
        receiver = captor.getValue();
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

    byte[] ack = {ZWaveRawSerialPort.ACK};

    @Test
    public void sendTwoMessagesWithAckBetween() throws Exception {
        zPort.sendMessage(message1);
        verifyMessageSent(rawMessage1);

        receiver.receiveFrameByte(ZWaveRawSerialPort.ACK);

        zPort.sendMessage(message2);
        verifyMessageSent(rawMessage2);
    }

    @Test
    public void resendsMessageOnNAK() throws Exception {
        zPort.sendMessage(message1);
        verify(rawPort, times(1)).sendMessage(message1.encode());

        receiver.receiveFrameByte(ZWaveRawSerialPort.NAK);

        verify(rawPort, times(2)).sendMessage(message1.encode());
    }

    private void verifyMessageSent(byte[] rawMessage1) throws PortException {
        verify(rawPort).sendMessage(rawMessage1);
    }
}
