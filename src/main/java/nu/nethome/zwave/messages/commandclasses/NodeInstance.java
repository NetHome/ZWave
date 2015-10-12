package nu.nethome.zwave.messages.commandclasses;

/**
 * Represents an instance within a node. If the node does not support multi instances
 */
public class NodeInstance {
    final public int node;
    final public Integer instance;

    public NodeInstance(int node, Integer instance) {
        this.node = node;
        this.instance = instance;
    }

    public NodeInstance(int node) {
        this.node = node;
        this.instance = null;
    }

    boolean hasInstance() {
        return instance != null;
    }
}
