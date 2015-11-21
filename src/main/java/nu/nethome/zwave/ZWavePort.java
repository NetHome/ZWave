package nu.nethome.zwave;

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
public class ZWavePort extends ZWavePortRaw {
    public static final int SEND_TIMEOUT_MS = 2000;
    private MessageProcessor externalReceiver;
    private Queue<byte[]> sendQueue = new ArrayDeque<>();
    private byte[] outstandingMessage = null;
    private Date currentSendTime;

    public ZWavePort(String portName) throws PortException {
        super(portName);
        setReceiver(new MessageProcessor() {
            @Override
            public Message process(byte[] message) throws DecoderException, IOException {
                return passThroughMessage(message);
            }
        });
    }

    @Override
    public void setReceiver(MessageProcessor receiver) {
        externalReceiver = receiver;
    }

    private Message passThroughMessage(byte[] message) throws DecoderException, IOException {
        return externalReceiver.process(message);
    }

    protected void processMessage(int frameByte) {
        try {
        switch (frameByte) {
            case ZWavePortRaw.ACK: {
                processNextMessage();
                break;
            }
            case ZWavePortRaw.NAK:
            case ZWavePortRaw.CAN: {
                resendCurrentMessage();
            }
        }
        } catch (SerialPortException e) {
            // Nothing to do...
        }
    }

    public void sendMessage(Message message) throws SerialPortException {
        this.sendMessage(message.encode());
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
            sendMessage(outstandingMessage);
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
            sendMessage(outstandingMessage);
        }
    }

}
