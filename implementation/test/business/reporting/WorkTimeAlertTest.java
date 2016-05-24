package business.reporting;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Stefan on 24.05.2016.
 */
public class WorkTimeAlertTest {
    private List<WorkTimeAlert> alertList;

    @Before
    public void setUp() {
        alertList = new ArrayList<>();
        alertList.add(new WorkTimeAlert("name1", WorkTimeAlert.Type.INFORMATION));
        alertList.add(new WorkTimeAlert("name2", WorkTimeAlert.Type.WARNING));
        alertList.add(new WorkTimeAlert("name3", WorkTimeAlert.Type.INFORMATION));
        alertList.add(new WorkTimeAlert("name4", WorkTimeAlert.Type.URGENT));
        alertList.add(new WorkTimeAlert("name5", WorkTimeAlert.Type.INFORMATION));
        alertList.add(new WorkTimeAlert("name6", WorkTimeAlert.Type.WARNING));
        alertList.add(new WorkTimeAlert("name7", WorkTimeAlert.Type.WARNING));
        alertList.add(new WorkTimeAlert("name8", WorkTimeAlert.Type.URGENT));
    }

    @Test
    public void sortingWorkTimeAlerts_WithComparableInterface_ShouldSucceed() {
        Collections.sort(alertList);

        Assert.assertEquals(alertList.get(0).getType(), WorkTimeAlert.Type.URGENT);
        Assert.assertEquals(alertList.get(1).getType(), WorkTimeAlert.Type.URGENT);
        Assert.assertEquals(alertList.get(2).getType(), WorkTimeAlert.Type.WARNING);
        Assert.assertEquals(alertList.get(3).getType(), WorkTimeAlert.Type.WARNING);
        Assert.assertEquals(alertList.get(4).getType(), WorkTimeAlert.Type.WARNING);
        Assert.assertEquals(alertList.get(5).getType(), WorkTimeAlert.Type.INFORMATION);
        Assert.assertEquals(alertList.get(6).getType(), WorkTimeAlert.Type.INFORMATION);
        Assert.assertEquals(alertList.get(7).getType(), WorkTimeAlert.Type.INFORMATION);
    }


}
