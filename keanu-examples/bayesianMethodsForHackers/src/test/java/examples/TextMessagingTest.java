package examples;

import io.improbable.keanu.vertices.dbl.KeanuRandom;
import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class TextMessagingTest {

    @Before
    public void setup() {
        KeanuRandom.setDefaultRandomSeed(1);
    }

    @Test
    public void testWhenTextMessagingScenarioIsRunThenSwitchPointIsAccurate() {
        // act
        TextMessaging.TextMessagingResults output = TextMessaging.run();

        System.out.println("Switch Point Mode " + output.switchPointMode);
        System.out.println("Early Rate Mode " + output.earlyRateMode);
        System.out.println("Late Rate Mode " + output.lateRateMode);

        // assert
        assertThat(output.switchPointMode).isCloseTo(43, within(2));
        assertThat(output.earlyRateMode).isCloseTo(17, within(2d));
        assertThat(output.lateRateMode).isCloseTo(23, within(2d));
    }
}
