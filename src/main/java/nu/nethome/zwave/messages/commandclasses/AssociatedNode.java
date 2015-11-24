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

/**
 * An AssociatedNode is a reference to another node used in association references, that is when one node
 * holds an association to another node. The association could either be a simple node number, referring to the
 * node id of the associated node, but it can also contain a reference to an specific endpoint (or instance)
 * within that node. Associations without support for instances are handled by the AssociationCommandClass and
 * associations with instances are handled by the MultiInstanceAssociationCommandClass.
 */
public class AssociatedNode {
    public final int nodeId;
    public final Integer instance;

    public boolean isMultiInstance() {
        return instance != null;
    }

    public AssociatedNode(int nodeId, int instance) {
        this.nodeId = nodeId;
        this.instance = instance;
    }

    public AssociatedNode(int nodeId) {
        this.nodeId = nodeId;
        this.instance = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssociatedNode that = (AssociatedNode) o;

        if (nodeId != that.nodeId) return false;
        if (instance != null ? !instance.equals(that.instance) : that.instance != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = nodeId;
        result = 31 * result + (instance != null ? instance.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "" + nodeId + (isMultiInstance() ? ("." + instance) : "");
    }
}
