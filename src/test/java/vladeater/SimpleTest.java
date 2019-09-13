package vladeater;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vlados Guskov
 */
public class SimpleTest {
    @Test
    public void test(){
        int x = 2;
        int y = 10;
        Assert.assertEquals(12,x+y);
    }

    @Test(expected = ArithmeticException.class)
    public void error(){
        int x = 0;
        int y = 1/0;
    }
}
