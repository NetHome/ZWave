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

public class MeterCommandClass implements CommandClass {

    private static final int GET = 0x01;
    private static final int REPORT = 0x02;

    private static final int SUPPORTED_GET = 0x03;
    private static final int SUPPORTED_REPORT = 0x04;
    private static final int RESET = 0x05;

    public static final int COMMAND_CLASS = 0x32;

    public static class Get extends CommandAdapter {
        public Get() {
            super(COMMAND_CLASS, GET);
        }
    }

    public static class Report extends CommandAdapter {
        public final int meterType;
        public final int unit;
        public final double value;
        public final int precision;

        public Report(byte[] data) throws DecoderException {
            super(data, COMMAND_CLASS, REPORT);
            meterType = in.read();
            int dimensions = in.read();
            precision = dimensions >> 5;
            unit = (dimensions >> 3) & 0x03;
            int size = dimensions & 0x07;
            byte firstByte = (byte)in.read();
            long rawValue = firstByte;
            for (int i = 1; i < size; i++) {
                rawValue <<= 8;
                rawValue |= in.read();
            }
            value = ((double)rawValue) / Math.pow(10, precision);
        }

        @Override
        protected void addCommandData(ByteArrayOutputStream result) {
            super.addCommandData(result);
            result.write(meterType);
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
            String unit;
            try {
                unit = getUnit().unit;
            } catch (DecoderException e) {
                unit = "";
            }
            return String.format("{\"Meter.Report\":{\"value\": %f, \"unit\": %s}}", value, unit);
        }

        public MeterUnit getUnit() throws DecoderException {
            return MeterUnit.fromMeterScale(meterType, unit);
        }
    }
}
