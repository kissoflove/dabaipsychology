package consult.psychological.dabai;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class ZhiduApplication extends Application {
	 private List<Activity> mList = new LinkedList<Activity>();
	    private static ZhiduApplication instance;

	    private ZhiduApplication() {
	    }

	    public synchronized static ZhiduApplication getInstance() {
	        if (null == instance) {
	            instance = new ZhiduApplication();
	        }
	        return instance;
	    }

	    // add Activity
	    public void addActivity(Activity activity) {
	        mList.add(activity);
	    }

	    public void exit() {
	        try {
	            for (Activity activity : mList) {
	                if (activity != null)
	                    activity.finish();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            System.exit(0);
	        }
	    }

	    @Override
	    public void onLowMemory() {
	        super.onLowMemory();
	        System.gc();
	    }
}
