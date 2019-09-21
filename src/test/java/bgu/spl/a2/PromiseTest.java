package bgu.spl.a2;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PromiseTest {

    private Promise<Integer> tst1;

    @Before
    public void setUp() throws Exception{
        this.tst1 = new Promise<>();
    }
    @After
   public void tearDown() throws Exception{
    }


    @Test
   public void get() {
        try {
            tst1.resolve(1);
            Integer check1 = tst1.get();
            assertEquals(1, check1.intValue());

            Promise<Integer> tst2 = new Promise<>();
            try {
                tst2.get();
                Assert.fail();
            } catch (IllegalStateException ignored) {
            }    //should throw exception - ignore if it did.
        } catch (Exception exc) {
            Assert.fail();
        }
    }

    @Test
    public void isResolved() {
        try {
            tst1.resolve(5);
            boolean check = tst1.isResolved();
            assertEquals(true, check);

            Promise<Integer> tst2 = new Promise<>();
            try {
                check = tst2.isResolved();
                assertEquals(false, check);
            } catch (Exception exc) {
                Assert.fail();
            }
        } catch (Exception exc) {
            Assert.fail();
        }
    }

    @Test
   public void resolve() {
        try {
            assertEquals(false, tst1.isResolved());
            try {
                tst1.resolve(10);
                try {
                    assertEquals(true, tst1.isResolved());
                } catch (Exception t) {
                    Assert.fail();
                }
                Integer resVal = tst1.get();
                assertEquals(10, resVal.intValue());
            } catch (Exception e) {
                Assert.fail();
            }

            try {
                tst1.resolve(11); //try to resolve more than 1 time
                Assert.fail();
            } catch (IllegalStateException ignored) {
            }
        } catch (Exception exc) {
            Assert.fail();
        }
    }


    @Test
    public void subscribe() {
        final int [] counter  = {0};
        try {
            callback c = () -> counter[0] ++;

            tst1.subscribe(c);
            assertEquals(0,counter[0]);

            tst1.resolve(10);
            assertEquals(1,counter[0]);

            tst1.subscribe(c);
            assertEquals(2,counter[0]);

        } catch (Exception exc) {
            Assert.fail();
        }
    }
}