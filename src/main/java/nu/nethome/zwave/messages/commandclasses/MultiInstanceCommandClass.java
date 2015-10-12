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
public class MultiInstanceCommandClass implements CommandClass {

    // Version 1
    private static final int GET = 0x04;
    private static final int REPORT = 0x05;
    private static final int V1_ENCAP = 0x06;

    // Version 2
    private static final int ENDPOINT_GET = 0x07;
    private static final int ENDPOINT_REPORT = 0x08;
    private static final int CAPABILITY_GET = 0x09;
    private static final int CAPABILITY_REPORT = 0x0a;
    private static final int ENDPOINT_FIND = 0x0b;
    private static final int ENDPOINT_FIND_REPORT = 0x0c;
    private static final int V2_ENCAP = 0x0d;

    public static final int COMMAND_CLASS = 0x60;
}
