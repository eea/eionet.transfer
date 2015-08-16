package eionet.transfer.util;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class HumaneTest {

    @Test
    public void underThousand() {
        assertEquals("145", Humane.humaneSize(145L));
    }

    @Test
    public void kilo() {
        assertEquals("1.46 KB", Humane.humaneSize(1456L));
    }

    @Test
    public void kilo10() {
        assertEquals("14.6 KB", Humane.humaneSize(14560L));
    }

    @Test
    public void kilo100() {
        assertEquals("146 KB", Humane.humaneSize(145600L));
    }

    @Test
    public void mega() {
        assertEquals("1.46 MB", Humane.humaneSize(1456000L));
    }

}
