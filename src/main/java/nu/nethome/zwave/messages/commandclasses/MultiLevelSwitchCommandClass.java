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
import nu.nethome.zwave.messages.commandclasses.framework.CommandCode;
import nu.nethome.zwave.messages.commandclasses.framework.CommandProcessorAdapter;
import nu.nethome.zwave.messages.framework.DecoderException;

import java.io.ByteArrayOutputStream;

public class MultiLevelSwitchCommandClass implements CommandClass {


    public static final int SWITCH_MULTILEVEL_SET = 0x01;
    public static final int SWITCH_MULTILEVEL_GET = 0x02;
    public static final int SWITCH_MULTILEVEL_REPORT = 0x03;
    public static final int SWITCH_MULTILEVEL_START_LEVEL_CHANGE = 0x04;
    public static final int SWITCH_MULTILEVEL_STOP_LEVEL_CHANGE = 0x05;
    public static final int SWITCH_MULTILEVEL_SUPPORTED_GET = 0x06;
    public static final int SWITCH_MULTILEVEL_SUPPORTED_REPORT = 0x07;

    public static final int COMMAND_CLASS = 0x26;

    public static class Set extends CommandAdapter {
        public final int level;

        public Set(int level) {
            super(COMMAND_CLASS, SWITCH_MULTILEVEL_SET);
            this.level = level;
        }

        public Set(byte[] data) throws DecoderException {
            super(data, COMMAND_CLASS, SWITCH_MULTILEVEL_SET);
            level = in.read();
        }

        public static class Processor extends CommandProcessorAdapter<Set> {
            @Override
            public CommandCode getCommandCode() {
                return new CommandCode(COMMAND_CLASS, SWITCH_MULTILEVEL_SET);
            }

            @Override
            public Set process(byte[] command, CommandArgument argument) throws DecoderException {
                return process(new Set(command), argument);
            }
        }

        @Override
        protected void addCommandData(ByteArrayOutputStream result) {
            super.addCommandData(result);
            result.write(level);
        }
    }

    public static class Get extends CommandAdapter {
        public Get() {
            super(COMMAND_CLASS, SWITCH_MULTILEVEL_GET);
        }
    }

    public static class Report extends CommandAdapter {
        public final int level;

        public Report(int level) {
            super(COMMAND_CLASS, SWITCH_MULTILEVEL_REPORT);
            this.level = level;
        }

        public Report(byte[] data) throws DecoderException {
            super(data, COMMAND_CLASS, SWITCH_MULTILEVEL_REPORT);
            level = in.read();
        }

        @Override
        protected void addCommandData(ByteArrayOutputStream result) {
            super.addCommandData(result);
            result.write(level);
        }

        public static class Processor extends CommandProcessorAdapter<Report> {

            @Override
            public CommandCode getCommandCode() {
                return new CommandCode(COMMAND_CLASS, SWITCH_MULTILEVEL_REPORT);
            }

            @Override
            public Report process(byte[] command, CommandArgument argument) throws DecoderException {
                return process(new Report(command), argument);
            }
        }

        @Override
        public String toString() {
            return String.format("{\"MultiLevelSwitch.Report\":{\"level\": %d}}", level);
        }
    }

    public static class StartLevelChange extends CommandAdapter {

        private static final int DIRECTION_BIT = 1<<6;
        private static final int IGNORE_START_POSITION_BIT = 1<<5;
        public enum Direction {UP, DOWN}

        public final Direction direction;
        public final Integer startLevel;
        public final int duration;

        public StartLevelChange(int startLevel, Direction direction) {
            super(COMMAND_CLASS, SWITCH_MULTILEVEL_START_LEVEL_CHANGE);
            this.direction = direction;
            this.startLevel = startLevel;
            duration = 0xFF;
        }

        public StartLevelChange(Direction direction) {
            super(COMMAND_CLASS, SWITCH_MULTILEVEL_START_LEVEL_CHANGE);
            this.direction = direction;
            this.startLevel = null;
            duration = 0xFF;
        }

        public StartLevelChange(Direction direction, int duration) {
            super(COMMAND_CLASS, SWITCH_MULTILEVEL_START_LEVEL_CHANGE);
            this.direction = direction;
            this.startLevel = null;
            this.duration = duration;
        }

        public StartLevelChange(byte[] data) throws DecoderException {
            super(data, COMMAND_CLASS, SWITCH_MULTILEVEL_START_LEVEL_CHANGE);
            int mode = in.read();
            int startLevel = in.read();
            direction = ((mode & DIRECTION_BIT) != 0) ? Direction.DOWN : Direction.UP;
            this.startLevel = ((mode & IGNORE_START_POSITION_BIT) != 0) ? null : startLevel;
            if (in.available() > 0) {
                duration = in.read();
            } else {
                duration = 0xFF;
            }
        }

        public static class Processor extends CommandProcessorAdapter<Set> {
            @Override
            public CommandCode getCommandCode() {
                return new CommandCode(COMMAND_CLASS, SWITCH_MULTILEVEL_START_LEVEL_CHANGE);
            }

            @Override
            public Set process(byte[] command, CommandArgument argument) throws DecoderException {
                return process(new Set(command), argument);
            }
        }

        @Override
        protected void addCommandData(ByteArrayOutputStream result) {
            super.addCommandData(result);
            int mode = (direction == Direction.DOWN) ? DIRECTION_BIT : 0;
            int tempStartLevel = 0;
            if (startLevel == null) {
                mode |= IGNORE_START_POSITION_BIT;
            } else {
                tempStartLevel = startLevel;
            }
            result.write(mode);
            result.write(tempStartLevel);
            result.write(duration);
        }
    }

    public static class StopLevelChange extends CommandAdapter {

        public StopLevelChange() {
            super(COMMAND_CLASS, SWITCH_MULTILEVEL_STOP_LEVEL_CHANGE);
        }

        public StopLevelChange(byte[] data) throws DecoderException {
            super(data, COMMAND_CLASS, SWITCH_MULTILEVEL_STOP_LEVEL_CHANGE);
        }

        public static class Processor extends CommandProcessorAdapter<Set> {
            @Override
            public CommandCode getCommandCode() {
                return new CommandCode(COMMAND_CLASS, SWITCH_MULTILEVEL_STOP_LEVEL_CHANGE);
            }

            @Override
            public Set process(byte[] command, CommandArgument argument) throws DecoderException {
                return process(new Set(command), argument);
            }
        }

        @Override
        protected void addCommandData(ByteArrayOutputStream result) {
            super.addCommandData(result);
        }
    }
}
