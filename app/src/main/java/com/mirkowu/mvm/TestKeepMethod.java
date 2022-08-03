package com.mirkowu.mvm;

import androidx.annotation.Keep;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class TestKeepMethod {
    public TestKeepMethod() {
    }

    public void testA() {
        System.out.println("testA");
        new InnerClass().testAAAAA();
    }

    //@Keep
    public void testB() {
        System.out.println("testB");
        new InnerClass().testBBB();
    }

    public class InnerClass{

        public void testAAAAA() {
            System.out.println("testAAAAA");
        }

        @Keep
        public void testBBB() {
            System.out.println("testBBB");
        }
    }
}
