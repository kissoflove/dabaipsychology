package consult.psychological.dabai.fragment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import consult.psychological.dabai.R;
import consult.psychological.dabai.cache.ACache;
import consult.psychological.dabai.dialog.SweetAlertDialog;
import consult.psychological.dabai.lib.NetWorkUtil;

public class EveryDayEnglishFragment extends Fragment implements
		OnClickListener,
		android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener {
	TextView tv_english, tv_china;
	ImageView iv, iv_play;
	private MediaPlayer mMediaPlayer = new MediaPlayer();// 播放音频。
	int play_state;
	private boolean mPlayState; // 播放状态
	String dateline;
	String tts;
	private ProgressBar mDisplayVoiceProgressBar;
	private String strDate = "";
	private int year;
	private int month;
	private int day;
	SwipeRefreshLayout swipe;
	TextView voice_display_voice_time;
	LinearLayout voice_display_voice_layout;
	ACache mCache;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);// 为了在Fragment中显示右上角的menu
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_everydayenglish,
				container, false);
		initView(view);
		
		return view;
	}

	private void initView(View view) {
		mCache = ACache.get(getActivity());
		try {
			ViewConfiguration mconfig = ViewConfiguration.get(getActivity());
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);

				menuKeyField.setBoolean(mconfig, false);
			}
		} catch (Exception ex) {
		}
		// numbercircleprogress_bar = (RoundProgressBar) view.
		// findViewById(R.id.numbercircleprogress_bar);
		swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
		swipe.setOnRefreshListener(this);
		// 顶部刷新的样式
		swipe.setColorSchemeResources(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		mDisplayVoiceProgressBar = (ProgressBar) view
				.findViewById(R.id.voice_display_voice_progressbar);
		voice_display_voice_time = (TextView) view
				.findViewById(R.id.voice_display_voice_time);
		voice_display_voice_layout = (LinearLayout) view
				.findViewById(R.id.voice_display_voice_layout);
		voice_display_voice_layout.setOnClickListener(this);
		tv_english = (TextView) view.findViewById(R.id.tv_english);
		tv_china = (TextView) view.findViewById(R.id.tv_china);
		iv = (ImageView) view.findViewById(R.id.iv);
		iv.setOnClickListener(this);
		iv_play = (ImageView) view.findViewById(R.id.voice_display_voice_play);
		iv_play.setImageResource(R.drawable.globle_player_btn_play);
		// iv_play.setOnClickListener(this);

		// http://open.iciba.com/dsapi/?date=

		if (NetWorkUtil.networkCanUse(getActivity())) {
			getData("http://open.iciba.com/dsapi/?date=" + strDate);
		} else {
			String content = mCache.getAsString("content");
			tv_english.setText(content);
			String note = mCache.getAsString("note");
			tv_china.setText(note);
			String picture2 = mCache.getAsString("picture2");
			String picture = mCache.getAsString("picture");
			if (picture2.equals("http://cdn.iciba.com/news/word/")) {
				Ion.with(getActivity(), picture).withBitmap().intoImageView(iv);
			} else {
				Ion.with(getActivity(), picture2).withBitmap()
						.intoImageView(iv);
			}
		}

	}

	private void getData(String jsonurl) {
		if (isAdded() == true) {

			Ion.with(getActivity(), jsonurl).asJsonObject()
					.setCallback(new FutureCallback<JsonObject>() {

						@Override
						public void onCompleted(Exception e, JsonObject result) {
							if (e != null) {
								return;
							}

							String content = result.get("content")
									.getAsString();
							mCache.put("content", content);//
							tv_english.setText(content);
							String note = result.get("note").getAsString();
							mCache.put("note", note);//
							tv_china.setText(note);
							String picture2 = result.get("picture2")
									.getAsString();
							mCache.put("picture2", picture2);//
							String picture = result.get("picture")
									.getAsString();
							mCache.put("picture", picture);//
							if (picture2
									.equals("http://cdn.iciba.com/news/word/")) {
								Ion.with(getActivity(), picture).withBitmap()
										.intoImageView(iv);
								Log.e("picture", picture);
							} else {
								Ion.with(getActivity(), picture2).withBitmap()
										.intoImageView(iv);
							}

							dateline = result.get("dateline").getAsString();
							tts = result.get("tts").getAsString();

						}
					});
		}
	}

	@SuppressLint("SdCardPath")
	private void aa() {
		if (!NetWorkUtil.networkCanUse(getActivity())) {
			new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
					.setTitleText("网络连接失败...").setContentText("请检查您的网络连接是否正常")
					.show();
			return;
		}
		HttpUtils http = new HttpUtils();

		try {
			if (tts.equals("") || tts == null) {
				getData("http://open.iciba.com/dsapi/?date=" + strDate);
			}
			@SuppressWarnings({ "rawtypes", "unused" })
			HttpHandler handler = http.download(tts, "/sdcard/zhidu/" + strDate
					+ ".mp3", true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
					false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
					new RequestCallBack<File>() {

						@Override
						public void onStart() {
							Log.e("onStart", "........start......");
						}

						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
							Log.e("onLoading", total + "=|=" + current);
							// pb.setMax((int)total);
							// pb.setProgress((int)current);
							Log.e("(int)current------------>", (int) current
									+ "");
						}

						@Override
						public void onSuccess(ResponseInfo<File> responseInfo) {
							Log.e("onSuccess", responseInfo.toString());
							Toast.makeText(getActivity(), "正在获取网络发音..", 1).show();

							play();
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.e("onFailure", "........msg......" + msg);
						}
					});
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public String getStandardTime(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("ss", Locale.getDefault());
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
		Date date = new Date(timestamp * 1000);
		sdf.format(date);
		return sdf.format(date);
	}

	/**
	 * 毫秒转换 mm：ss格式方法
	 * 
	 * @param max
	 * @return
	 */
	public String converLongTimeToStr(long time) {
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;

		long hour = (time) / hh;
		long minute = (time - hour * hh) / mi;
		long second = (time - hour * hh - minute * mi) / ss;

		String strHour = hour < 10 ? "0" + hour : "" + hour;
		String strMinute = minute < 10 ? "0" + minute : "" + minute;
		String strSecond = second < 10 ? "0" + second : "" + second;
		if (hour > 0) {
			return strHour + ":" + strMinute + ":" + strSecond;
		} else {
			return strMinute + ":" + strSecond;
		}
	}

	@SuppressLint("SdCardPath")
	private void play() {
		Log.e("dateline:", "" + dateline);
		// 播放录音
		if (!mPlayState) {
			mMediaPlayer = new MediaPlayer();
			try {
				// 添加录音的路径
				mMediaPlayer.setDataSource("/sdcard/zhidu/" + strDate + ".mp3");

				// 准备
				mMediaPlayer.prepare();
				// 播放
				mMediaPlayer.start();
				voice_display_voice_time
						.setText(converLongTimeToStr(mMediaPlayer.getDuration())
								+ "″");
				// 根据时间修改界面
				new Thread(new Runnable() {

					public void run() {
						mDisplayVoiceProgressBar.setMax(mMediaPlayer
								.getDuration());

						while (mMediaPlayer.isPlaying()) {

							mDisplayVoiceProgressBar.setProgress(mMediaPlayer
									.getCurrentPosition());
						}
					}
				}).start();
				// 修改播放状态
				mPlayState = true;
				// 修改播放图标
				// mDisplayVoicePlay
				// .setImageResource(R.drawable.globle_player_btn_stop);

				iv_play.setImageResource(R.drawable.globle_player_btn_stop);
				mMediaPlayer
						.setOnCompletionListener(new OnCompletionListener() {
							// 播放结束后调用
							public void onCompletion(MediaPlayer mp) {
								// 停止播放
								mMediaPlayer.stop();
								// 修改播放状态
								mPlayState = false;
								// 修改播放图标
								iv_play.setImageResource(R.drawable.globle_player_btn_play);
								mDisplayVoiceProgressBar.setProgress(0);

							}
						});

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (mMediaPlayer != null) {
				// 根据播放状态修改显示内容
				if (mMediaPlayer.isPlaying()) {
					mPlayState = false;
					mMediaPlayer.stop();
					mDisplayVoiceProgressBar.setProgress(0);
					iv_play.setImageResource(R.drawable.globle_player_btn_play);
				} else {
					mPlayState = false;
					iv_play.setImageResource(R.drawable.globle_player_btn_play);
					mDisplayVoiceProgressBar.setProgress(0);
				}
			}
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		getData("http://open.iciba.com/dsapi/?date=" + strDate);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if( mMediaPlayer != null ) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	@SuppressLint("SdCardPath")
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.voice_display_voice_layout:
			File file = new File("/sdcard/zhidu/" + strDate + ".mp3");
			if (file.exists()) {
				play();
			} else {
				aa();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {

			return true;
		} else {
			
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
			day = cal.get(Calendar.DAY_OF_MONTH);
			DatePickerDialog dpd = new DatePickerDialog(getActivity(),
					Datelistener, year, month, day);

			dpd.show();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.time, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {
		/**
		 * params：view：该事件关联的组件 params：myyear：当前选择的年 params：monthOfYear：当前选择的月
		 * params：dayOfMonth：当前选择的日
		 */
		@Override
		public void onDateSet(DatePicker view, int myyear, int monthOfYear,
				int dayOfMonth) {

			// 修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
			year = myyear;
			month = monthOfYear;
			day = dayOfMonth;
			// 更新日期
			updateDate();

		}

		// 当DatePickerDialog关闭时，更新日期显示
		private void updateDate() {
			// 在TextView上显示日期
			int mm = month + 1;
			// showdate.setText("当前日期："+year+"-"+(month+1)+"-"+day);
			strDate = year + "-" + mm + "-" + day;
			Calendar cal = Calendar.getInstance();
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH)+1;
			int d = cal.get(Calendar.DAY_OF_MONTH);
			String nowTime = y+"-"+m+"-"+d;
			if (year > y||year<=2013){
				new SweetAlertDialog(getActivity()).setTitleText("查询范围:2014-1-1至"+nowTime)
				.show();
				return;
			}

			String da = "http://open.iciba.com/dsapi/?date=" + strDate;
			getData(da);
			voice_display_voice_time.setText("");
		}
	};

	@Override
	public void onRefresh() {

		new Handler().postDelayed(new Runnable() {
			public void run() {

				if (NetWorkUtil.networkCanUse(getActivity())) {
					Calendar cal = Calendar.getInstance();
					year = cal.get(Calendar.YEAR);
					month = cal.get(Calendar.MONTH);
					day = cal.get(Calendar.DAY_OF_MONTH);
					strDate = year + "-" + (month + 1) + "-" + day;
					getData("http://open.iciba.com/dsapi/?date=" + strDate);
					voice_display_voice_time.setText("");


				} else {
					Toast.makeText(getActivity(), "网络连接失败..", 1).show();

				}

				swipe.setRefreshing(false);

			}
		}, 1500);
	}
}