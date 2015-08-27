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
import nu.nethome.zwave.messages.commands.CommandAdapter;
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
public class Association implements CommandClass {

    private static final int SET_ASSOCIATION = 0x01;
    private static final int GET_ASSOCIATION = 0x02;
    private static final int ASSOCIATION_REPORT = 0x03;
    private static final int REMOVE_ASSOCIATION = 0x04;
    private static final int GET_GROUPINGS = 0x05;
    private static final int REPORT_GROUPINGS = 0x06;

    public static final int COMMAND_CLASS = 0x85;

    public static class Get extends CommandAdapter {
        public final int associationId;

        public Get(int associationId) {
            super(COMMAND_CLASS, GET_ASSOCIATION);
            this.associationId = associationId;
        }

        @Override
        protected void addCommandData(ByteArrayOutputStream result) {
            super.addCommandData(result);
            result.write(associationId);
        }
    }

    public static class GetGroupings extends CommandAdapter {

        public GetGroupings() {
            super(COMMAND_CLASS, GET_GROUPINGS);
        }

        @Override
        protected void addCommandData(ByteArrayOutputStream result) {
            super.addCommandData(result);
        }
    }

    public static class Report extends CommandAdapter {
        public final int associationId;
        public final int maxAssociations;
        public final int reportsToFollow;
        public final int[] nodes;

        public Report(byte[] data) throws DecoderException {
            super(data, COMMAND_CLASS, ASSOCIATION_REPORT);
            associationId = in.read();
            maxAssociations = in.read();
            reportsToFollow = in.read();
            int numberOfNodes = data.length - 5;
            nodes = new int[numberOfNodes];
            for (int i = 0; i < numberOfNodes; i++) {
                nodes[i] = in.read();
            }
        }

        public static class Processor extends CommandProcessorAdapter<Report> {
            @Override
            public Report process(byte[] command, int node) throws DecoderException {
                return process(new Report(command));
            }
        }
    }
}
