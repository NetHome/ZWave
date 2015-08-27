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

package nu.nethome.zwave.messages.commands;

import nu.nethome.zwave.Hex;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 */
public class ParameterTest {

    @Test
    public void canPackAndUnpack() throws Exception {
        verifyPackAndUnpack(new Parameter(5, 1));
        verifyPackAndUnpack(new Parameter(-5, 1));
        verifyPackAndUnpack(new Parameter(500, 2));
        verifyPackAndUnpack(new Parameter(-500, 2));
        verifyPackAndUnpack(new Parameter(100000, 3));
        verifyPackAndUnpack(new Parameter(-100000, 3));
    }

    @Test
    public void testFormat() throws Exception {
        Parameter parameter = new Parameter(0x0567, 2);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        parameter.write(out);
        assertThat(Hex.asHexString(out.toByteArray()), is("020567"));
    }

    private void verifyPackAndUnpack(Parameter parameter) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        parameter.write(out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Parameter readParameter = new Parameter(in);
        assertThat(parameter, is(readParameter));
    }
}
