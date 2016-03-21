package business;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test example.
 */
public class TimeTrackingFacadeTest {


    @Before
    public void SetUp(){
        // ToDo: Create via dependency injection
    }


    @Test
    public void TestAdd_insertTwoValues_ShouldAddThem(){
        int a = 2;
        int b = 3;
        int expected = 5;

        int actual = a + b;

        Assert.assertEquals(expected, actual);
    }
}
