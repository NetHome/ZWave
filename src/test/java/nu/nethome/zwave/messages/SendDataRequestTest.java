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

package nu.nethome.zwave.messages;

import nu.nethome.zwave.Hex;
import nu.nethome.zwave.messages.commands.MultiInstanceAssociation;
import nu.nethome.zwave.messages.commands.SwitchBinary;
import org.junit.Test;

/**
 *
 */
public class SendDataRequestTest {

    public static final int TRANSMIT_OPTIONS = SendData.TRANSMIT_OPTION_ACK |
            SendData.TRANSMIT_OPTION_AUTO_ROUTE | SendData.TRANSMIT_OPTION_EXPLORE;

    @Test
    public void binarySwitch() throws Exception {
        SendData.Request request = new SendData.Request((byte) 2, new SwitchBinary.Set(true), TRANSMIT_OPTIONS);
        String result = Hex.asHexString(request.encode());
        System.out.println("event,ZWave_Message,Direction,Out,Value," + result);
//        request = new SendDataRequest((byte) 2, SwitchBinary.report(), TRANSMIT_OPTIONS);
//        result = Hex.asHexString(request.encode());
//        System.out.println("event,ZWave_Message,Direction,Out,Value," + result);
        request = new SendData.Request((byte) 2, new MultiInstanceAssociation.Get(2), TRANSMIT_OPTIONS);
        result = Hex.asHexString(request.encode());
        System.out.println("event,ZWave_Message,Direction,Out,Value," + result);
        GetInitData.Request request1 = new GetInitData.Request();
        result = Hex.asHexString(request1.encode());
        System.out.println("event,ZWave_Message,Direction,Out,Value," + result);
        //
        // 00 04 0006 06: 85 03 01 0A 00 01
        // 00 04 0006 06: 85 03 02 0A 00 02
        // 00 04 0006 0F: 8F 01 02 05 85 03 03 0A 00 05 85 03 03 0A 00 + 00 04 0006 03: 80 03 64
        // 00 04 0006 05: 85 03 04 0A 00 (4)
        // 00 04 0006 06: 85 03 02 0A 00 02 (2)

        // 00 04 0002 05: 85 03 01 05 00
        // 00 04 0002 06: 85 03 02 05 00 06

        // 1: 1.1
        // 2: 1.2, 6


    }

}
