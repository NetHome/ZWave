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
 *
 */
public class CentralSceneCommandClass implements CommandClass {

    public static final int CAPABILITY_GET = 0x01;
    public static final int CAPABILITY_REPORT = 0x02;
    public static final int SET = 0x03;

    public static final byte COMMAND_CLASS = (byte) 0x5B;

    public static class Set extends CommandAdapter {
        public final int counter;
        public final int press;
        public final int scene;

        public Set(int counter, int press, int scene) {
            super(COMMAND_CLASS, SET);
            this.counter = counter;
            this.press = press;
            this.scene = scene;
        }

        public Set(byte[] data) throws DecoderException {
            super(data, COMMAND_CLASS, SET);
            counter = in.read();
            press = in.read();
            scene = in.read();
        }

        public static class Processor extends CommandProcessorAdapter<Set> {
            @Override
            public Set process(byte[] command, CommandArgument argument) throws DecoderException {
                return process(new Set(command), argument);
            }
        }

        @Override
        protected void addCommandData(ByteArrayOutputStream result) {
            super.addCommandData(result);
            result.write(counter);
            result.write(press);
            result.write(scene);
        }

        @Override
        public String toString() {
            return String.format("{\"CentralScene.Set\":{\"counter\": %d, \"press\": %d, \"scene\": %d, }}", counter, press, scene);
        }
    }
}
