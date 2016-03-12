package backend;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by david on 12.03.16.
 */
public class TestExampleImpl {

    private ExampleInterface _testee;

    @Before
    public void SetUp(){
        _testee = new ExampleImpl();
    }

    @Test
    public void TestAdd_insertTwoValues_ShouldAddThem(){
        int a = 2;
        int b = 3;
        int expected = 5;

        int actual = _testee.add(a, b);

        Assert.assertEquals(expected, actual);
    }
}
