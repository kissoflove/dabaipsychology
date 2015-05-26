package consult.psychological.dabai.lib;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataUtils {

    private static final DateFormat FORMATOR_TIME = new SimpleDateFormat(
            "HH:mm:ss");

    public static String getStringTime(Date date) {
        return FORMATOR_TIME.format(date);
    }

    /**
     * 0 表示周末 ，1-6表示周一到周六
     * 
     * @return
     */
    public static final int getCurrentDayOfWeek() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
    }
}