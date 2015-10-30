package nu.nethome.zwave;

import jssc.SerialPortException;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.Queue;

/**
 * An extension of the ZWavePort that adds a send queue and the ability to resend messages that was not properly
 * received by the ZWave controller.
 */
public class QueueingZWavePort {
    public static final int SEND_TIMEOUT_MS = 2000;
    private ZWavePort port;
    private ZWavePort.Receiver externalReceiver;
    private Queue<byte[]> sendQueue = new ArrayDeque<>();
    private byte[] outstandingMessage = null;
    private Date currentSendTime;

    public QueueingZWavePort(String portName, ZWavePort.Receiver receiver) throws PortException {
        externalReceiver = receiver;
        port = createZWavePort(portName);
    }

    ZWavePort createZWavePort(String portName) throws PortException {
        return new ZWavePort(portName, new ZWavePort.Receiver() {
            @Override
            public void receiveMessage(byte[] message) {
                passThroughMessage(message);
            }

            @Override
            public void receiveFrameByte(byte frameByte) {
                handleFrameByte(frameByte);
            }
        });
    }

    private void passThroughMessage(byte[] message) {
        externalReceiver.receiveMessage(message);
    }

    private void handleFrameByte(byte frameByte) {
        try {
        switch (frameByte) {
            case ZWavePort.ACK: {
                processNextMessage();
                break;
            }
            case ZWavePort.NAK:
            case ZWavePort.CAN: {
                resendCurrentMessage();
            }
        }
        } catch (SerialPortException e) {
            // Nothing to do...
        }
    }

    public void sendMessage(byte[] message) throws SerialPortException {
        sendQueue.offer(message);
        sendNextMessage();
    }

    private synchronized void sendNextMessage() throws SerialPortException {
        if (outstandingMessage != null && (getNow().getTime() - currentSendTime.getTime()) > SEND_TIMEOUT_MS){
            // Handle timeout
            outstandingMessage = null;
        }
        if (outstandingMessage == null && !sendQueue.isEmpty()) {
            outstandingMessage = sendQueue.poll();
            port.sendMessage(outstandingMessage);
            currentSendTime = getNow();
        }
    }

    Date getNow() {
        return new Date();
    }

    private synchronized void processNextMessage() throws SerialPortException {
        outstandingMessage = null;
        sendNextMessage();
    }

    private synchronized void resendCurrentMessage() throws SerialPortException {
        if (outstandingMessage != null) {
            port.sendMessage(outstandingMessage);
        }
    }

    public void close() {
        port.close();
    }
}
