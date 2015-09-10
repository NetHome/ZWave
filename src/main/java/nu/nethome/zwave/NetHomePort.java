package nu.nethome.zwave;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class NetHomePort {
    public static interface Receiver {
        void receiveMessage(byte[] message);
    }

    private Receiver receiver;
    private boolean isOpen;
    private DataOutputStream outputStream;
    private BufferedReader inputStream;
    private Socket clientSocket;

    public NetHomePort(String address, int port, Receiver receiver) throws IOException {
        this.receiver = receiver;
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

    public void sendMessage(byte[] message) throws IOException {
        outputStream.write(String.format("event,ZWave_Message,Direction,Out,Value,%s\n\r", Hex.asHexString(message)).getBytes());
        outputStream.flush();
    }

    public void close() throws IOException {
        isOpen = false;
        inputStream.close();
        outputStream.close();
        clientSocket.close();
    }

    private void receiveLoop() {
        try {
            while (isOpen) {
                String[] line = inputStream.readLine().split(",");
                if (line.length > 6 && line[0].equals("event") && line[1].equals("ZWave_Message") && line[3].equals("In")) {
                    receiver.receiveMessage(Hex.hexStringToByteArray(line[5]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
