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

import jssc.SerialPortException;
import nu.nethome.zwave.messages.framework.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Shell {
    ZWaveExecutor executor;
    ZWaveSerialPort port;
    private ZWaveNetHomePort ZWaveNetHomePort;

    public static void main(String[] args) throws PortException, IOException {
        if (args.length == 2) {
            new Shell().runWithNetHomePort(args[0], Integer.parseInt(args[1]));
        } else {
            new Shell().runWithLocalPort(args[0]);
        }
    }

    private void runWithLocalPort(String portname) throws PortException, IOException {
        port = new ZWaveSerialPort(portname);
        port.setReceiver(new MessageProcessor() {
            @Override
            public Message process(byte[] message) {
                return executor.processZWaveMessage(message);
            }
        });

        executor = new ZWaveExecutor(
                new ZWaveExecutor.MessageSender() {
                    @Override
                    public void sendZWaveMessage(byte[] zWaveMessage) {
                        try {
                            port.sendMessage(zWaveMessage);
                        } catch (PortException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new ZWaveExecutor.Printer() {
                    @Override
                    public void print(String message) {
                        System.out.print(message);
                    }

                    @Override
                    public void println(String message) {
                        System.out.println(message);
                    }
                }
        );
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();
        while (!line.equalsIgnoreCase("quit")) {
            executor.executeCommandLine(line);
            line = br.readLine();
        }
        port.close();
    }

    private void runWithNetHomePort(String address, int portNumber) throws PortException, IOException {
        ZWaveNetHomePort = new ZWaveNetHomePort(address, portNumber);
        ZWaveNetHomePort.setReceiver(new MessageProcessor() {
            @Override
            public Message process(byte[] message) {
                return executor.processZWaveMessage(message);
            }
        });

        executor = new ZWaveExecutor(
                new ZWaveExecutor.MessageSender() {
                    @Override
                    public void sendZWaveMessage(byte[] zWaveMessage) {
                        try {
                            ZWaveNetHomePort.sendMessage(zWaveMessage);
                        } catch (PortException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new ZWaveExecutor.Printer() {
                    @Override
                    public void print(String message) {
                        System.out.print(message);
                    }

                    @Override
                    public void println(String message) {
                        System.out.println(message);
                    }
                }
        );
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();
        while (!line.equalsIgnoreCase("quit")) {
            executor.executeCommandLine(line);
            line = br.readLine();
        }
        ZWaveNetHomePort.close();
    }
}
