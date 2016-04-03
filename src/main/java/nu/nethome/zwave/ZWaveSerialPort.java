package nu.nethome.zwave;

import jssc.SerialPort;
import jssc.SerialPortException;
import nu.nethome.zwave.messages.framework.DecoderException;
import nu.nethome.zwave.messages.framework.Message;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Queue;

/**
 * An extension of the ZWavePortRaw that adds a send queue and the ability to resend messages that was not properly
 * received by the ZWave controller.
 */
public class ZWaveSerialPort implements ZWavePort {
    public static final int SEND_TIMEOUT_MS = 2000;
    private MessageProcessor externalReceiver;
    private Queue<byte[]> sendQueue = new ArrayDeque<>();
    private byte[] outstandingMessage = null;
    private Date currentSendTime;
    private ZWaveRawSerialPort port;

    public ZWaveSerialPort(String portName) throws PortException {
        port = new ZWaveRawSerialPort(portName);
        port.setReceiver(new Receiver());
    }

    ZWaveSerialPort(ZWaveRawSerialPort port) throws PortException {
        this.port = port;
        this.port.setReceiver(new Receiver());
    }

    @Override
    public void setReceiver(MessageProcessor receiver) {
        externalReceiver = receiver;
    }

    private void passThroughMessage(byte[] message)  {
        try {
            externalReceiver.process(message);
        } catch (DecoderException e) {
            // ignore
        }
    }

    protected void processFrameByte(int frameByte)  {
        try {
        switch (frameByte) {
            case ZWaveRawSerialPort.ACK: {
                processNextMessage();
                break;
            }
            case ZWaveRawSerialPort.NAK:
            case ZWaveRawSerialPort.CAN: {
                resendCurrentMessage();
            }
        }
        } catch (PortException e) {
            // Nothing to do...
        }
    }

    @Override
    public void sendMessage(Message message) throws PortException {
        this.sendMessage(message.encode());
    }

    @Override
    public void sendMessage(byte[] message) throws PortException {
        sendQueue.offer(message);
        sendNextMessage();
    }

    @Override
    public void close() {
        this.port.close();
    }

    @Override
    public boolean isOpen() {
        return port.isOpen();
    }

    private synchronized void sendNextMessage() throws PortException {
        if (outstandingMessage != null && (getNow().getTime() - currentSendTime.getTime()) > SEND_TIMEOUT_MS){
            // Handle timeout
            outstandingMessage = null;
        }
        if (outstandingMessage == null && !sendQueue.isEmpty()) {
            outstandingMessage = sendQueue.poll();
            this.port.sendMessage(outstandingMessage);
            currentSendTime = getNow();
        }
    }

    Date getNow() {
        return new Date();
    }

    private synchronized void processNextMessage() throws PortException {
        outstandingMessage = null;
        sendNextMessage();
    }

    private synchronized void resendCurrentMessage() throws PortException {
        if (outstandingMessage != null) {
            this.port.sendMessage(outstandingMessage);
        }
    }

    private class Receiver implements ZWaveRawSerialPort.Receiver {
        @Override
        public void receiveMessage(byte[] message) {
            passThroughMessage(message);
        }

        @Override
        public void receiveFrameByte(byte frameByte) {
            processFrameByte(frameByte);
        }
    }
}
