package org.suai.util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class MD5HashTest {

    @Test
    public void getHash() {
        String str1 = "Hello, world!";
        String str2 = "Hello, friend";

        Assert.assertEquals(MD5Hash.getHash(str1), MD5Hash.getHash(str1));
        Assert.assertNotEquals(MD5Hash.getHash(str1), MD5Hash.getHash(str2));
    }
}