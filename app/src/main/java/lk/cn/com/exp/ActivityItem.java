package lk.cn.com.exp;

import java.io.Serializable;

/**
 * Created by lk on 2017/11/8.
 */

public class ActivityItem implements Serializable {
    public Class cls;

    public ActivityItem(String name, Class cls) {
        this.cls = cls;
        this.name = name;
    }

    public String name;
}
