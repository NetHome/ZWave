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

package nu.nethome.zwave.messages.commands;


import nu.nethome.zwave.messages.DecoderException;
import nu.nethome.zwave.messages.commands.Command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public abstract class CommandAdapter implements Command {
    private final int commandClass;
    private final int command;
    protected ByteArrayInputStream in;

    @Override
    public byte[] encode() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        addCommandData(result);
        return result.toByteArray();
    }

    protected void addCommandData(ByteArrayOutputStream result) {
        result.write(commandClass);
        result.write(command);
    }

    protected CommandAdapter(int commandClass, int command) {
        this.commandClass = commandClass;
        this.command = command;
    }

    protected CommandAdapter(byte[] commandData) throws DecoderException {
        in = new ByteArrayInputStream(commandData);
        commandClass = in.read();
        command = in.read();
    }

    protected CommandAdapter(byte[] commandData, int commandClass, int command) throws DecoderException {
        this(commandData);
        DecoderException.assertTrue(this.commandClass == commandClass, "Wrong command class in Association");
        DecoderException.assertTrue(this.command == command, "Wrong command class in Association");
    }

    @Override
    public int getCommandClass() {
        return commandClass;
    }

    @Override
    public int getCommand() {
        return command;
    }

    public static CommandCode decodeCommandCode(byte[] message) throws DecoderException {
        if (message == null || message.length < 2){
            throw new DecoderException("Invalid command buffer");
        }
        return new CommandCode(((int)(message[0])) & 0xFF, ((int)(message[1])) & 0xFF);
    }
}
