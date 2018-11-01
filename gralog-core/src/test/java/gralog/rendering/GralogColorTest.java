package gralog.rendering;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GralogColorTest {

    public GralogColorTest() {

    }

    @Test
    public void ColorNameTest() {
        for (GralogColor.Color c : GralogColor.Color.values()) {
            String expectedResult = c.name();
            GralogColor color = new GralogColor(c.value);
            assertEquals(expectedResult, color.name());
        }

        String expectedResult = "WHITE";
        GralogColor color = new GralogColor(0xFFFFFF);
        assertEquals(expectedResult, color.name());
    }


}
