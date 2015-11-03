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
 * Configuration command class is used to read and write configuration parameters in nodes
 */
public class ConfigurationCommandClass implements CommandClass {

    public static final int SET_CONFIGURATION = 0x04;
    public static final int GET_CONFIGURATION = 0x05;
    public static final int REPORT_CONFIGURATION = 0x06;

    public static final int COMMAND_CLASS = 0x70;

    public static class Get extends CommandAdapter {
        public final int configurationId;

        public Get(int configurationId) {
            super(COMMAND_CLASS, GET_CONFIGURATION);
            this.configurationId = configurationId;
        }

        @Override
        protected void addCommandData(ByteArrayOutputStream result) {
            super.addCommandData(result);
            result.write(configurationId);
        }
    }

    public static class Set extends CommandAdapter {
        public final int configurationId;
        public final Parameter parameter;

        public Set(int configurationId, Parameter parameter) {
            super(COMMAND_CLASS, SET_CONFIGURATION);
            this.configurationId = configurationId;
            this.parameter = parameter;
        }

        @Override
        protected void addCommandData(ByteArrayOutputStream result) {
            super.addCommandData(result);
            result.write(configurationId);
            parameter.write(result);
        }
    }

    public static class Report extends CommandAdapter {
        public final int configurationId;
        public final Parameter parameter;

        public Report(byte[] data) throws DecoderException {
            super(data, COMMAND_CLASS, REPORT_CONFIGURATION);
            configurationId = in.read();
            parameter = new Parameter(in);
        }

        public static class Processor extends CommandProcessorAdapter<Report> {
            @Override
            public Report process(byte[] command, CommandArgument argument) throws DecoderException {
                return process(new Report(command), argument);
            }
        }

        @Override
        public String toString() {
            return String.format("{\"Parameter.Report\": {\"parameter\": %d, \"value\": %s)", configurationId, parameter.toString());
        }
    }
}
