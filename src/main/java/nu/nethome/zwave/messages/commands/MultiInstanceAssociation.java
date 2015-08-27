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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Modifies Association Groups in a node. The MultiInstanceAssociation command class is an extension of the
 * Association command class, where MultiInstanceAssociation can handle nodes with multiple instances/endpoints
 */
public class MultiInstanceAssociation implements CommandClass {

    public static final int SET_ASSOCIATION = 0x01;
    public static final int GET_ASSOCIATION = 0x02;
    public static final int ASSOCIATION_REPORT = 0x03;
    public static final int REMOVE_ASSOCIATION = 0x04;

    public static final int COMMAND_CLASS = 0x8E;

    public static class Get extends CommandAdapter {
        public final int group;

        public Get(int group) {
            super(COMMAND_CLASS, GET_ASSOCIATION);
            this.group = group;
        }

        @Override
        protected void addCommandData(ByteArrayOutputStream result) {
            super.addCommandData(result);
            result.write(group);
        }
    }

    public static class Set extends CommandAdapter {
        public final int group;
        public final List<AssociatedNode> nodes;

        public Set(int group, List<AssociatedNode> nodes) {
            this(group, nodes, SET_ASSOCIATION);
        }

        Set(int group, List<AssociatedNode> node, int command) {
            super(COMMAND_CLASS, command);
            this.group = group;
            this.nodes = node;
        }

        @Override
        protected void addCommandData(ByteArrayOutputStream result) {
            super.addCommandData(result);
            result.write(group);
            for (AssociatedNode node : nodes) {
                if (!node.isMultiInstance()) {
                    result.write(node.nodeId);
                }
            }
            result.write(0);
            for (AssociatedNode node : nodes) {
                if (node.isMultiInstance()) {
                    result.write(node.nodeId);
                    result.write(node.instance);
                }
            }
        }
    }

    public static class Remove extends Set {
        public Remove(int group, List<AssociatedNode> nodes) {
            super(group, nodes, REMOVE_ASSOCIATION);
        }
    }

    public static class Report extends CommandAdapter {
        public final int group;
        public final int maxAssociations;
        public final int reportsToFollow;
        public final AssociatedNode[] nodes;

        public Report(byte[] data) throws DecoderException {
            super(data, COMMAND_CLASS, ASSOCIATION_REPORT);
            group = in.read();
            maxAssociations = in.read();
            reportsToFollow = in.read();
            int numberOfNodeBytes = (data.length - 5);
            ArrayList<AssociatedNode> associatedNodes = new ArrayList<>();
            int nextNode = in.read();
            int readBytes = 1;
            // First comes the nodes without instance as single bytes followed by a single 0-byte
            while (nextNode != 0 && readBytes < numberOfNodeBytes) {
                associatedNodes.add(new AssociatedNode(nextNode));
                nextNode = in.read();
                readBytes++;
            }
            // Then comes the nodes with instance id:s
            while (readBytes < numberOfNodeBytes) {
                int nodeId = in.read();
                int instanceId = in.read();
                associatedNodes.add(new AssociatedNode(nodeId, instanceId));
                readBytes += 2;
            }
            nodes = associatedNodes.toArray(new AssociatedNode[associatedNodes.size()]);
        }

        @Override
        public String toString() {
            String nodesString = "";
            String separator = "";
            for (AssociatedNode node : nodes) {
                nodesString += separator + node;
                separator = ", ";
            }
            return String.format("MultiInstanceAssociation.Report(group:%d, max:%d, following: %d, nodes: %s)", group, maxAssociations, reportsToFollow, nodesString);
        }

        public static class Processor extends CommandProcessorAdapter<Report> {
            @Override
            public Report process(byte[] command, int node) throws DecoderException {
                return process(new Report(command));
            }
        }
    }
}
