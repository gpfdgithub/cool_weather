package util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Control the life cycle of app all activity
 */
public class ActivityCollrector {

    private static List<Activity> activitys = new ArrayList<>();

    public static void addActivity(Activity activity){
        activitys.add(activity);
    }

    public static  void deleteAllActivity(){
        for (Activity activity:activitys) {
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
