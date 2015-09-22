package nu.nethome.zwave;

import jssc.SerialPortException;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.Queue;

/**
 *
 */
public class QueueingZWavePort {
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
        if (outstandingMessage == null && !sendQueue.isEmpty()) {
            outstandingMessage = sendQueue.poll();
            port.sendMessage(outstandingMessage);
            currentSendTime = new Date();
        }
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
}
