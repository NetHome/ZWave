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
import nu.nethome.zwave.messages.commands.CommandClass;

import java.io.ByteArrayOutputStream;

/**
 * 004AFF060000
 * 004AFF010000
 * 00 04 0002 06: 31 05 0422000B
 * 00 04 0006 07: 60 0D 0007200100
 * 00 04   06 03: 85 02 09
 *
 * 00 04 0006 02: 84 07
 * 00 04 0006 03: 80 0364
 * 00 04 0006 03: 80 0364
 */
public class SwitchBinary implements CommandClass {

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
            public Report process(byte[] command, int node) throws DecoderException {
                return process(new Report(command));
            }
        }

        @Override
        public String toString() {
            return String.format("SwitchBinary.Report(value:%d)", isOn ? 1 : 0);
        }
    }
}
