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

package nu.nethome.zwave.messages.commandclasses.framework;

import nu.nethome.zwave.Hex;
import nu.nethome.zwave.messages.commandclasses.CommandArgument;
import nu.nethome.zwave.messages.framework.DecoderException;

import java.util.Arrays;

public class UndecodedCommand extends CommandAdapter {
    byte[] commandData;

    public UndecodedCommand(byte[] commandData) throws DecoderException {
        super(commandData);
        this.commandData = commandData;
    }

    @Override
    public byte[] encode() {
        return commandData;
    }

    public static class Processor extends CommandProcessorAdapter<UndecodedCommand> {
        @Override
        public CommandCode getCommandCode() {
            return null;
        }

        @Override
        public UndecodedCommand process(byte[] command, CommandArgument argument) throws DecoderException {
            return process(new UndecodedCommand(command), argument);
        }
    }

    @Override
    public String toString() {
        return String.format("{\"Unknown.Command\": {\"commandClass\": %d, \"command\": %d], \"data\": \"%s\"}}", getCommandClass(), getCommand(), Hex.asHexString(Arrays.copyOfRange(commandData, 2, commandData.length)));
    }
}
