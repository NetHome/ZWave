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

package nu.nethome.zwave.messages.commandclasses.framework;

import nu.nethome.zwave.messages.commandclasses.CommandArgument;
import nu.nethome.zwave.messages.commandclasses.MultiInstanceCommandClass;
import nu.nethome.zwave.messages.framework.DecoderException;

import java.util.HashMap;
import java.util.Map;

public class MultiCommandProcessor implements CommandProcessor {

    private Map<CommandCode, CommandProcessor> processors = new HashMap<>();
    private CommandProcessor defaultProcessor;

    public MultiCommandProcessor() {
        this.defaultProcessor = new UndecodedCommand.Processor();
        addCommandProcessor(new MultiInstanceCommandClass.EncapsulationV2.Processor(this));
    }

    public MultiCommandProcessor(CommandProcessor defaultProcessor) {
        this.defaultProcessor = defaultProcessor;
    }

    @Override
    public CommandCode getCommandCode() {
        throw new IllegalStateException("MultiCommandProcessor have no command code");
    }

    @Override
    public Command process(byte[] message, CommandArgument argument) throws DecoderException {
        CommandProcessor processor = processors.get(CommandAdapter.decodeCommandCode(message));
        if (processor != null) {
            return processor.process(message, argument);
        }
        return defaultProcessor.process(message, argument);
    }

    public void addCommandProcessor(CommandProcessor processor) {
        processors.put(processor.getCommandCode(), processor);
    }

    public void setDefaultProcessor(CommandProcessor defaultProcessor) {
        this.defaultProcessor = defaultProcessor;
    }
}
