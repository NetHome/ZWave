package nu.nethome.zwave;

import jssc.SerialPortException;
import nu.nethome.zwave.messages.framework.Message;

/**
 *
 */
public interface ZWavePort {
    void setReceiver(MessageProcessor receiver);

    void sendMessage(Message message) throws PortException;

    void sendMessage(byte[] message) throws PortException;

    boolean isOpen();

    void close()throws PortException;
}
