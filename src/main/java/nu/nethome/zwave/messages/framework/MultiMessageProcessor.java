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

package nu.nethome.zwave.messages.framework;

import nu.nethome.zwave.MessageProcessor;
import nu.nethome.zwave.messages.ApplicationCommand;
import nu.nethome.zwave.messages.commandclasses.framework.CommandProcessor;
import nu.nethome.zwave.messages.commandclasses.framework.MultiCommandProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MultiMessageProcessor implements MessageProcessor {

    Map<MessageId, MessageProcessor> processors = new HashMap<>();
    MessageProcessor defaultMessageProcessor = new UndecodedMessage.Message.Processor();
    MultiCommandProcessor defaultCommandProcessor = new MultiCommandProcessor();

    public MultiMessageProcessor() {
        addMessageProcessor(ApplicationCommand.REQUEST_ID, Message.Type.REQUEST, new ApplicationCommand.Request.Processor(defaultCommandProcessor));
    }

    @Override
    public Message process(byte[] message) throws DecoderException {
        MessageProcessor processor = processors.get(MessageAdaptor.decodeMessageId(message));
        if (processor != null) {
            return processor.process(message);
        }
        return defaultMessageProcessor.process(message);
    }

    public void addMessageProcessor(int messageId, Message.Type type, MessageProcessor processor) {
        processors.put(new MessageId(messageId, type), processor);
    }

    public void addCommandProcessor(CommandProcessor processor) {
        defaultCommandProcessor.addCommandProcessor(processor);
    }

    public void setDefaultMessageProcessor(MessageProcessor defaultMessageProcessor) {
        this.defaultMessageProcessor = defaultMessageProcessor;
    }

    public MultiCommandProcessor getDefaultCommandProcessor() {
        return defaultCommandProcessor;
    }
}
