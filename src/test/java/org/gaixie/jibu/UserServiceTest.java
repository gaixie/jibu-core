package org.gaixie.jibu.service;

import org.gaixie.jibu.JibuTestSupport;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserServiceTest extends JibuTestSupport {

    @Before
    public void setup() throws Exception {
	clearTable();
    }


    @Test
    public void testGet() throws Exception {
        Assert.assertNull(null);
    }

    @After
    public void tearDown() {
	clearTable();
    }
}
