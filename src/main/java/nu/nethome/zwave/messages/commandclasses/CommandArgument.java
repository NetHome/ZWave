package nu.nethome.zwave.messages.commandclasses;

/**
 * Arguments to an application Command. Contains the node that issued the command and optionally the instance
 * in the target node that the command is intended for.
 */
public class CommandArgument {
    final public int sourceNode;
    final public Integer targetInstance;

    public CommandArgument(int sourceNode, Integer targetInstance) {
        this.sourceNode = sourceNode;
        this.targetInstance = targetInstance;
    }

    public CommandArgument(int sourceNode) {
        this.sourceNode = sourceNode;
        this.targetInstance = null;
    }

    boolean hasInstance() {
        return targetInstance != null;
    }
}
