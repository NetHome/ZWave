package nu.nethome.zwave.messages;

import nu.nethome.zwave.Hex;
import nu.nethome.zwave.messages.framework.DecoderException;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class GetInitDataTest {

    @Test
    public void canDecodeKnownData() throws Exception, DecoderException {

        GetInitData.Response response = new GetInitData.Response(Hex.hexStringToByteArray("010205081D23000000000000000000000000000000000000000000000000000000000500"));

        assertThat(response.mode, is(GetInitData.ControllerMode.CONTROLLER));
        assertThat(response.type, is(GetInitData.ControllerType.PRIMARY));
        assertThat(response.nodes, hasItems(0, 1, 5));
    }
}
