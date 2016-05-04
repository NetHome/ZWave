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
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;
import nu.nethome.zwave.messages.framework.DecoderException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * ZWave communication port for sending raw ZWave messages via a serial port to a ZWave controller.
 * The ZWawePort handles the packaging of the ZWave messages, with frame start byte, message length encoding
 * and checksum, so the input and output from the port is raw ZWave byte strings with messages.
 */
@SuppressWarnings("UnusedDeclaration")
public class ZWaveRawSerialPort {
    public static interface Receiver {
        void receiveMessage(byte[] message);

        void receiveFrameByte(byte frameByte);
    }

    static final byte SOF = 0x01;
    static final byte ACK = 0x06;
    static final byte NAK = 0x15;
    static final byte CAN = 0x18;

    private static Logger logger = Logger.getLogger(ZWaveRawSerialPort.class.getName());

    String portName = "/dev/ttyAMA0";
    private Receiver receiver = new DummyProcessor();

    protected SerialPort serialPort;
    protected volatile boolean isOpen = false;

    public ZWaveRawSerialPort(String portName, Receiver receiver) throws PortException {
        this.receiver = receiver;
        this.portName = portName;
        serialPort = new SerialPort(this.portName);
        open();
    }

    public ZWaveRawSerialPort(String portName) throws PortException {
        this(portName, new DummyProcessor());
    }

    /**
     * Create for test
     */
    ZWaveRawSerialPort(String portName, SerialPort port) throws PortException {
        this.portName = portName;
        this.serialPort = port;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public static List<String> listAvailablePortNames() {
        return Arrays.asList(SerialPortList.getPortNames());
    }

    private void open() throws PortException {
        List<String> portList = Arrays.asList(SerialPortList.getPortNames());
        if (!portList.contains(portName)) {
            throw new PortException("Port " + portName + " not Found");
        }
        try {
            serialPort.openPort();
            if (!serialPort.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)) {
                throw new PortException("Could not set serial port parameters");
            }
        } catch (SerialPortException e) {
            throw new PortException("Could not open port " + portName, e);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                receiveLoop();
            }
        }, "Port receive thread").start();
        isOpen = true;
    }

    public void close() {
        isOpen = false;
        if (serialPort != null) {
            try {
                serialPort.closePort();
            } catch (SerialPortException e) {
                // Ignore
            }
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void sendMessage(byte[] message) throws PortException {
        try {
        serialPort.writeByte(SOF);
        byte messageLength = (byte) (message.length + 1);
        serialPort.writeByte(messageLength);
        serialPort.writeBytes(message);
        serialPort.writeByte(calculateChecksum(message, messageLength));
        } catch (SerialPortException e) {
            throw new PortException("Could not send message", e);
        }
    }

    private static byte calculateChecksum(byte[] buffer, byte messageLength) {
        byte checkSum = -1;
        checkSum ^= messageLength;
        for (byte messageByte : buffer) {
            checkSum ^= messageByte;
        }
        return checkSum;
    }

    private void receiveLoop() {
        SerialPort localPort = serialPort;
        try {
            synchronizeCommunication();
        } catch (SerialPortException e) {
            logger.warning("ZWave port Failed to write to serial port - exiting");
            return;
        }
        while (localPort.isOpened() && isOpen) {
            try {
                logger.fine("Starting read message");
                readMessage(localPort);
                logger.fine("Have read message");
            } catch (SerialPortException e) {
                logger.fine("Serial port exception " + e.getMessage());
                // Probably port is closed, ignore and will exit the while
            } catch (Exception e) {
                logger.fine("General exception processing ZWave message " + e.getMessage());
            }
        }
    }

    void readMessage(SerialPort localPort) throws SerialPortException, SerialPortTimeoutException, DecoderException, IOException {
        int frameByte = readByte(localPort, 3000);
        switch (frameByte) {
            case SOF:
                int messageLength;
                messageLength = readByte(localPort, 50);
                logger.fine("Read message length: " + messageLength);
                byte[] message = localPort.readBytes(messageLength - 1, 1000);
                logger.fine("Read " + message.length + "bytes");
                int checksum = readByte(localPort, 50);
                logger.fine("Read checksum: " + checksum);
                processMessage(message, checksum);
                break;
            case ACK:
            case NAK:
            case CAN:
                processMessage(frameByte);
                break;
            default:
                logger.warning(String.format("ZWave received unexpected frame byte %d, resynchronizing", frameByte & 0xFF));
                synchronizeCommunication();
                break;
        }
    }

    private void processMessage(byte[] message, int checksum) throws SerialPortException, DecoderException, IOException {
        // NYI Verify checksum
        sendResponse(ACK);
        receiver.receiveMessage(message);
    }

    protected void processMessage(int frameByte) throws DecoderException, IOException {
        receiver.receiveFrameByte((byte)frameByte);
    }

    private void synchronizeCommunication() throws SerialPortException {
        sendResponse(NAK);
    }

    private int readByte(SerialPort localPort, int timeout) throws SerialPortException, SerialPortTimeoutException {
        byte[] data = localPort.readBytes(1, timeout);
        return (int) data[0];
    }

    private boolean sendResponse(int message) throws SerialPortException {
        return serialPort.writeByte((byte) message);
    }

    private static class DummyProcessor implements Receiver {
        @Override
        public void receiveMessage(byte[] message) {
        }

        @Override
        public void receiveFrameByte(byte frameByte) {
        }
    }
}
