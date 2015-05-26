package consult.psychological.dabai.activity;

import java.io.File;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import consult.psychological.dabai.Conf;
import consult.psychological.dabai.R;
import consult.psychological.dabai.adapter.SettingAdapter;
import consult.psychological.dabai.cache.ACache;
import consult.psychological.dabai.dialog.SweetAlertDialog;
import consult.psychological.dabai.lib.NetWorkUtil;
import consult.psychological.dabai.lib.toast.Crouton;
import consult.psychological.dabai.lib.toast.Style;
import consult.psychological.dabai.service.AppUpdateService;
//import consult.psychological.dabai.util.Setting;

@SuppressWarnings("deprecation")
public class SettingActivity extends Activity implements OnPreferenceChangeListener{
	ListView lv;
	SettingAdapter adapter;
	String[] items = { "意见反馈", "检查更新","赏个好评","QQ群：271436525", "清除缓存", "注销登录" };
	int vc;// 获取当前版本号
	File sdcardDir;
	String path;
	File f;
	File[] fl;
	ACache mCache;
	private boolean isExit = true;// true为登录状态
	private CountDownTimer timer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_setting);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			setTranslucentStatus(true);
//		}
//
//		SystemBarTintManager tintManager = new SystemBarTintManager(this);
//		tintManager.setStatusBarTintEnabled(true);
//		tintManager.setStatusBarTintResource(R.color.actionbar_color);
		mCache = ACache.get(this);
		//vc = Setting.getVersionCode(this); 
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		ab.setTitle("设置");
		lv = (ListView) findViewById(R.id.lv);
		adapter = new SettingAdapter(items, this);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent it = new Intent();
				switch (arg2) {
				case 0:
					it.setClass(getApplicationContext(), FeedBackActivity.class);
					startActivity(it);
					break;
				case 1:
					//chekedVersionCode();
					break;
				case 2:
					Intent i = getIntent(SettingActivity.this,"consult.psychological.dabai");
					boolean b = judge(SettingActivity.this, i);
					if (b == false) {
						startActivity(i);
					}
					break;
				case 3:
					ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
					clip.setText("271436525"); // 复制
					showCustomToast(getString(R.string.back_exit_qq),
							R.id.ll_setting_id);
					break;
				case 4:

					sdcardDir = Environment.getExternalStorageDirectory();
					path = sdcardDir.getPath() + "/dabai";
					f = new File(path);
					fl = f.listFiles();
					Log.e("fl.length==", fl.length + "");
					if (fl.length == 0) {
						showCustomToast(getString(R.string.back_exit_no),
								R.id.ll_setting_id);
					} else {
						new SweetAlertDialog(SettingActivity.this,
								SweetAlertDialog.CUSTOM_IMAGE_TYPE)
								.setTitleText("清除缓存")
								.setContentText("是否清除缓存？")
								.setCancelText("暂不清除")
								.setConfirmText("清除缓存")
								.showCancelButton(true)
								.setCancelClickListener(
										new SweetAlertDialog.OnSweetClickListener() {
											@Override
											public void onClick(
													SweetAlertDialog sDialog) {
												sDialog.dismiss();
											}
										})
								.setConfirmClickListener(
										new SweetAlertDialog.OnSweetClickListener() {
											@Override
											public void onClick(
													SweetAlertDialog sDialog) {

												//
												for (int i = 0; i < fl.length; i++) {
													if (fl[i].toString()
															.endsWith(".mp3")
															|| fl[i].toString()
																	.endsWith(
																			".MP3")||fl[i].toString()
																			.endsWith(".jpg")||fl[i].toString()
																			.endsWith(".JPG")) {
														fl[i].delete();
													}
												}
												showCustomToast(getString(R.string.back_exit_success),
														R.id.ll_setting_id);
												sDialog.dismiss();
											}
										}).show();
					}

					break;
				case 5:

					new SweetAlertDialog(SettingActivity.this,
							SweetAlertDialog.CUSTOM_IMAGE_TYPE)
							.setTitleText("注销登录")
							.setContentText("是否注销登录？")
							.setCancelText("暂不注销")
							.setConfirmText("注销登录")
							.showCancelButton(true)
							.setCancelClickListener(
									new SweetAlertDialog.OnSweetClickListener() {
										@Override
										public void onClick(
												SweetAlertDialog sDialog) {
											sDialog.dismiss();
										}
									})
							.setConfirmClickListener(
									new SweetAlertDialog.OnSweetClickListener() {
										@Override
										public void onClick(
												SweetAlertDialog sDialog) {

											mCache.clear();
											isExit = false;

											sDialog.dismiss();
										}
									}).show();

					break;
				case 6:
					 
					break;
				default:
					break;
				}
			}
		});
	}
	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}
	  /**
     * 显示ShortToast
     */
    public void showCustomToast(String pMsg, int view_position) {
	// ToastUtil.createCustomToast(this, pMsg, Toast.LENGTH_SHORT).show();
	 Crouton.makeText(this, pMsg, Style.CONFIRM, view_position).show();
//	Crouton.makeText(this, pMsg, Style.ALERT, view_position).show();
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (isExit == false) {
				// 数据是使用Intent返回
				Intent intent = new Intent();
				// 把返回数据存入Intent
				intent.putExtra("result", "exit");
				// 设置返回数据
				SettingActivity.this.setResult(RESULT_OK, intent);
				// 关闭Activity
				SettingActivity.this.finish();
//				overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else {
				// 数据是使用Intent返回
				Intent intent = new Intent();
				// 把返回数据存入Intent
				intent.putExtra("date", "用户没有退出---登录状态");
				// 设置返回数据
				SettingActivity.this.setResult(RESULT_OK, intent);
				// 关闭Activity
				SettingActivity.this.finish();
//				overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
			// finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

/*	public void chekedVersionCode() {
		if (!NetWorkUtil.networkCanUse(this)) {
			new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
					.setTitleText("网络连接失败...").setContentText("请检查您的网络连接是否正常")
					.show();
			return;
		}
		Ion.with(this, Conf.VERSION_CODE).asJsonObject()
				.setCallback(new FutureCallback<JsonObject>() {

					@Override
					public void onCompleted(Exception e, JsonObject result) {
						if (e != null) {
							return;
						}
						String code = result.get("code").getAsString();
						int jsonCode = Integer.parseInt(code);
						// 比较开源中国返回的code跟当前版本code是否一致
						if (jsonCode == vc) {
							Log.e("11111", "版本号相同，不更新" + "jsonCode:" + jsonCode);
							new SweetAlertDialog(SettingActivity.this,
									SweetAlertDialog.SUCCESS_TYPE)
									.setTitleText("当前版本已是最新")
									.setContentText("Version:" + getAppInfo())
									.show();

						} else if (jsonCode > vc) {
							new SweetAlertDialog(SettingActivity.this,
									SweetAlertDialog.WARNING_TYPE)
									.setTitleText("版本检测")
									.setContentText("发现新版本，是否更新？")
									.setCancelText("暂不更新")
									.setConfirmText("马上更新")
									.showCancelButton(true)
									.setCancelClickListener(
											new SweetAlertDialog.OnSweetClickListener() {
												@Override
												public void onClick(
														SweetAlertDialog sDialog) {
													sDialog.dismiss();
												}
											})
									.setConfirmClickListener(
											new SweetAlertDialog.OnSweetClickListener() {
												@Override
												public void onClick(
														SweetAlertDialog sDialog) {
													Intent updateIntent = new Intent(
															SettingActivity.this,
															AppUpdateService.class);
													updateIntent.putExtra(
															"titleId",
															R.string.app_name);
													startService(updateIntent);
													sDialog.dismiss();

												}
											}).show();
						}

					}
				});
	}

	private String getAppInfo() {
		try {
			String pkName = this.getPackageName();
			String versionName = this.getPackageManager().getPackageInfo(
					pkName, 0).versionName;
			return  versionName;
		} catch (Exception e) {
		}
		return null;
	}*/

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控/拦截/屏蔽返回键
			if (isExit == false) {
				// 数据是使用Intent返回
				Intent intent = new Intent();
				// 把返回数据存入Intent
				intent.putExtra("result", "exit");
				// 设置返回数据
				SettingActivity.this.setResult(RESULT_OK, intent);
				// 关闭Activity
				SettingActivity.this.finish();
//				overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else {
				// 数据是使用Intent返回
				Intent intent = new Intent();
				// 把返回数据存入Intent
				intent.putExtra("date", "用户没有退出---登录状态");
				// 设置返回数据
				SettingActivity.this.setResult(RESULT_OK, intent);
				// 关闭Activity
				SettingActivity.this.finish();
//				overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public static Intent getIntent(Context paramContext,String packageName) {
		StringBuilder localStringBuilder = new StringBuilder()
				.append("market://details?id=");
		String str = /* paramContext.getPackageName(); */packageName;
		localStringBuilder.append(str);
		Uri localUri = Uri.parse(localStringBuilder.toString());
		return new Intent("android.intent.action.VIEW", localUri);
	}

	// 直接跳转不判断是否存在市场应用
	public static void start(Context paramContext, String paramString) {
		Uri localUri = Uri.parse(paramString);
		Intent localIntent = new Intent("android.intent.action.VIEW", localUri);
		localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		paramContext.startActivity(localIntent);
	}

	public static boolean judge(Context paramContext, Intent paramIntent) {
		List<ResolveInfo> localList = paramContext.getPackageManager()
				.queryIntentActivities(paramIntent,
						PackageManager.GET_INTENT_FILTERS);
		if ((localList != null) && (localList.size() > 0)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (Boolean.parseBoolean(newValue.toString())) {
			
		 
		}
	 
	
		return true;
	}
}