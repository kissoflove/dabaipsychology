package consult.psychological.dabai.lib;

import android.os.CountDownTimer;

public class TimerUtils {

	public	static CountDownTimer timer;
	public	static long time;

	public static long createTimer(int allTime) {

		timer = new CountDownTimer(allTime * 100, 100) {

			@Override
			public void onTick(long millisUntilFinished) {
				time = millisUntilFinished / 100;

			}

			@Override
			public void onFinish() {

			}
		};
		return time;
	}

}
