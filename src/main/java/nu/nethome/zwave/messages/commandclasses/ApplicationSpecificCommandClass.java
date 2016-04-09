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

package nu.nethome.zwave.messages.commandclasses;


import nu.nethome.zwave.messages.commandclasses.framework.CommandAdapter;
import nu.nethome.zwave.messages.commandclasses.framework.CommandClass;
import nu.nethome.zwave.messages.commandclasses.framework.CommandCode;
import nu.nethome.zwave.messages.commandclasses.framework.CommandProcessorAdapter;
import nu.nethome.zwave.messages.framework.DecoderException;

import java.io.ByteArrayOutputStream;

public class ApplicationSpecificCommandClass implements CommandClass {

    public static final int GET = 0x04;
    public static final int REPORT = 0x05;
    public static final int COMMAND_CLASS = 0x72;

    public static class Get extends CommandAdapter {

        public Get() {
            super(COMMAND_CLASS, GET);
        }

        @Override
        protected void addCommandData(ByteArrayOutputStream result) {
            super.addCommandData(result);
        }
    }

    public static class Report extends CommandAdapter {
        public final int manufacturer;
        public final int deviceType;
        public final int deviceId;

        public Report(byte[] data) throws DecoderException {
            super(data, COMMAND_CLASS, REPORT);
            int hiByte = in.read() << 8;
            manufacturer = hiByte + in.read();
            hiByte = in.read() << 8;
            deviceType = hiByte + in.read();
            hiByte = in.read() << 8;
            deviceId = hiByte + in.read();
        }

        public static class Processor extends CommandProcessorAdapter<Report> {
            @Override
            public CommandCode getCommandCode() {
                return new CommandCode(COMMAND_CLASS, REPORT);
            }

            @Override
            public Report process(byte[] command, CommandArgument argument) throws DecoderException {
                return process(new Report(command), argument);
            }
        }

        @Override
        public String toString() {
            return String.format("{\"ApplicationSpecific.Report\": {\"manufacturer\": %d, \"deviceType\": %d, \"deviceId\": %d", manufacturer, deviceType, deviceId);
        }
    }
}
