package org.suai.io;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;

public class IOFileTest {
    private IOFile ioFile;

    @Before
    public void setUp() {
        ioFile = new IOFile("/home/dmbldr/Study/Java/Course/MultiPaint/master-server/src/test/java/org/suai/io/test.txt");
    }

    @Test
    public void initUsers() {
        HashMap<String, String> users1 = ioFile.initUsers();
        HashMap<String, String> users2 = ioFile.initUsers();
        Assert.assertEquals(users1, users2);
    }

    @Test
    public void changeDataBase() {
        HashMap<String, String> users1 = ioFile.initUsers();
        users1.put("name2", "password2");
        ioFile.changeDataBase(users1);
        HashMap<String, String> users2 = ioFile.initUsers();
        Assert.assertEquals(users1, users2);
     }

    @Test
    public void registration() {
        ioFile.registration("name1", "password1");
    }
}