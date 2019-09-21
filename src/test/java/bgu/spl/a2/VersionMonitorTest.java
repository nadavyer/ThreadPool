package bgu.spl.a2;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

public class VersionMonitorTest {

    private VersionMonitor tst;

    @Before
    public void setUp() throws Exception{
        this.tst = new VersionMonitor();
    }
    @After
    public void tearDown() throws Exception{
    }


    @Test
    public void getVersion() {
        try {
            assertEquals(0, tst.getVersion());
        }
        catch (Exception exc){
            Assert.fail();
        }
    }

    @Test
    public void inc() {
        try {
            int num = tst.getVersion();
            tst.inc();
            assertEquals(num + 1, tst.getVersion());
        }
        catch (Exception exc) {
            Assert.fail();
        }
    }

    @Test
    public void await() {

        Thread t1 = new Thread(() -> {
            try {
                tst.await(0);
            } catch (InterruptedException exc) {
                    assertEquals(1, tst.getVersion());
                    tst.inc();
            }
        });

        try {
            t1.start();
            Thread.sleep(1000);  // that's so t1 could run.
            tst.inc();
            Thread.sleep(1000);
            assertEquals(2, tst.getVersion());
        }
        catch(Exception exc){
            Assert.fail();}

    }
}