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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MultiMessageProcessor implements MessageProcessor {

    Map<Integer, MessageProcessor> processors = new HashMap<>();

    @Override
    public Message process(byte[] message) throws DecoderException, IOException {
        MessageProcessor processor = processors.get(MessageAdaptor.decodeMessageId(message));
        if (processor != null) {
            return processor.process(message);
        }
        return null;
    }

    public void addMessageProcessor(int messageId, MessageProcessor processor) {
        processors.put(messageId, processor);
    }
}
