package org.suai.net;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {

    private static Message m1;
    private static Message m2;
    private static Message m3;
    private static Message m4;

    @Before
    public void setUp() throws Exception {
        m1 = new Message(0, "h");
        m2 = new Message(0, "h1");

        int[] rgb = {1, 2, 3, 4, 5};

        m3 = new Message(0, rgb);
        m4 = new Message(0, rgb);
    }

    @Test
    public void getType() {
        Assert.assertEquals(m1.getType(), m2.getType());
    }

    @Test
    public void getMessage() {
        Assert.assertNotEquals(m1.getMessage(), m2.getMessage());
    }

    @Test
    public void getRgb() {
        Assert.assertEquals(m3.getRgb(), m4.getRgb());
    }
}