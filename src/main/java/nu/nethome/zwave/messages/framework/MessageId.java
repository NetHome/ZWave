package nu.nethome.zwave.messages.framework;

/**
 *
 */
public class MessageId {
    public final int messageId;
    public final Message.Type type;

    public MessageId(int messageId, Message.Type type) {
        this.messageId = messageId;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageId messageId1 = (MessageId) o;

        if (messageId != messageId1.messageId) return false;
        if (type != messageId1.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = messageId;
        result = 31 * result + type.hashCode();
        return result;
    }
}
