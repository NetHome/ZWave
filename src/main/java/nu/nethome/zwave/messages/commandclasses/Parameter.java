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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Configuration parameters are numeric values used to configure the behaviour of nodes. They are read and set
 * with the Configuration command class.
 */
public class Parameter {
    public final int value;
    public final int length;

    public Parameter(int value, int length) {
        this.value = value;
        this.length = length;
    }

    public Parameter(ByteArrayInputStream in) {
        length = in.read();
        int parameterValue = 0;
        int readByte = 0;
        for (int i = 0; i < length; i++) {
            parameterValue <<= 8;
            readByte = in.read();
            parameterValue |= readByte;
        }
        if (((parameterValue >> ((length - 1) * 8)) & 0x80) != 0) {
            int signExtender = 0xFFFFFFFF << (8 * length);
            parameterValue |= signExtender;
        }
        value = parameterValue;
    }

    public void write(ByteArrayOutputStream result) {
        result.write(length);
        for (int i = 0; i < length; i++) {
            result.write((value >> ((length - i - 1) * 8)) & 0xFF);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter parameter = (Parameter) o;

        if (length != parameter.length) return false;
        if (value != parameter.value) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value;
        result = 31 * result + length;
        return result;
    }

    @Override
    public String toString() {
        return String.format("%d(%d)", value, length);
    }
}
