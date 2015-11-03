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
import nu.nethome.zwave.messages.commandclasses.framework.CommandProcessorAdapter;
import nu.nethome.zwave.messages.framework.DecoderException;

import java.io.ByteArrayOutputStream;

/**
 *
 */
public class SwitchBinaryCommandClass implements CommandClass {

    public static final int SWITCH_BINARY_SET = 0x01;
    public static final int SWITCH_BINARY_GET = 0x02;
    public static final int SWITCH_BINARY_REPORT = 0x03;

    public static final byte COMMAND_CLASS = (byte) 0x25;

    public static class Set extends CommandAdapter {
        public final boolean isOn;

        public Set(boolean on) {
            super(COMMAND_CLASS, SWITCH_BINARY_SET);
            isOn = on;
        }

        public Set(byte[] data) throws DecoderException {
            super(data, COMMAND_CLASS, SWITCH_BINARY_SET);
            isOn = (in.read() > 0);
        }

        public static class Processor extends CommandProcessorAdapter<Set> {
            @Override
            public Set process(byte[] command, CommandArgument argument) throws DecoderException {
                return process(new Set(command), argument);
            }
        }

        @Override
        protected void addCommandData(ByteArrayOutputStream result) {
            super.addCommandData(result);
            result.write(isOn ? 0xFF : 0);
        }
    }

    public static class Get extends CommandAdapter {
        public Get() {
            super(COMMAND_CLASS, SWITCH_BINARY_GET);
        }
    }

    public static class Report extends CommandAdapter {
        public final boolean isOn;

        public Report(byte[] data) throws DecoderException {
            super(data, COMMAND_CLASS, SWITCH_BINARY_REPORT);
            isOn = (in.read() > 0);
        }

        @Override
        protected void addCommandData(ByteArrayOutputStream result) {
            super.addCommandData(result);
            result.write(isOn ? 0xFF : 0);
        }

        public static class Processor extends CommandProcessorAdapter<Report> {
            @Override
            public Report process(byte[] command, CommandArgument argument) throws DecoderException {
                return process(new Report(command), argument);
            }
        }

        @Override
        public String toString() {
            return String.format("{\"SwitchBinary.Report\":{\"value\": %d}}", isOn ? 1 : 0);
        }
    }
}
