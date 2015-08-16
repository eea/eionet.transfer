package eionet.transfer.util;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class HumaneTest {

    @Test
    public void nosize() {
        assertEquals("0", Humane.humaneSize(0L));
    }

    @Test
    public void singleDigit() {
        assertEquals("9", Humane.humaneSize(9L));
    }

    @Test
    public void underThousand() {
        assertEquals("145", Humane.humaneSize(145L));
    }

    @Test
    public void kilo() {
        assertEquals("1.23 KB", Humane.humaneSize(1234L));
    }

    @Test
    public void kiloRoundUp() {
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

    @Test
    public void mega10() {
        assertEquals("14.6 MB", Humane.humaneSize(14560000L));
    }

    @Test
    public void giga() {
        assertEquals("1.46 GB", Humane.humaneSize(1456000000L));
    }

    @Test
    public void giga100() {
        assertEquals("146 GB", Humane.humaneSize(145600000000L));
    }

}
