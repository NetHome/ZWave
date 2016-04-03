package nu.nethome.zwave;

import jssc.SerialPortException;
import nu.nethome.zwave.messages.framework.Message;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ZWaveNetHomePort implements ZWavePort{

    private MessageProcessor receiver;
    private boolean isOpen;
    private DataOutputStream outputStream;
    private BufferedReader inputStream;
    private Socket clientSocket;

    public ZWaveNetHomePort(String address, int port) throws IOException {
        clientSocket = new Socket(address, port);
        outputStream = new DataOutputStream(clientSocket.getOutputStream());
        inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outputStream.write("subscribe\r\n".getBytes());
        outputStream.flush();
        isOpen = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                receiveLoop();
            }
        }, "Port receive thread").start();
    }

    @Override
    public void setReceiver(MessageProcessor receiver) {
        this.receiver = receiver;
    }

    @Override
    public void sendMessage(Message message) throws PortException {
        this.sendMessage(message.encode());
    }

    @Override
    public void sendMessage(byte[] message) throws PortException {
        try {
            outputStream.write(String.format("event,ZWave_Message,Direction,Out,Value,%s\n\r", Hex.asHexString(message)).getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new PortException("Could not send message", e);
        }
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void close() throws PortException {
        isOpen = false;
        try {
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            throw new PortException("Could not close port", e);
        }
    }

    private void receiveLoop() {
        try {
            while (isOpen) {
                String[] line = inputStream.readLine().split(",");
                if (line.length > 6 && line[0].equals("event") && line[1].equals("ZWave_Message") && line[3].equals("In")) {
                    receiver.process(Hex.hexStringToByteArray(line[5]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
