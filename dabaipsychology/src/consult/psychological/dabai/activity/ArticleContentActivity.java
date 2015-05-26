package consult.psychological.dabai.activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import consult.psychological.dabai.Conf;
import consult.psychological.dabai.R;

public class ArticleContentActivity extends Activity implements OnClickListener {
	TextView textView, textView_author, textView_title;
	String strContent;
	String strAuthor, strTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_article_content);
		textView = (TextView) findViewById(R.id.textView);
		textView_title = (TextView) findViewById(R.id.textView_title);
		textView_author = (TextView) findViewById(R.id.textView_author);
	
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			setTranslucentStatus(true);
//		}
//
//		SystemBarTintManager tintManager = new SystemBarTintManager(this);
//		tintManager.setStatusBarTintEnabled(true);
//		tintManager.setStatusBarTintResource(R.color.actionbar_color);
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		
		strContent = getIntent().getStringExtra("CONTENT");
		strAuthor = getIntent().getStringExtra("AUTHOR");
		strTitle = getIntent().getStringExtra("TITLE");
		ab.setTitle(strTitle+"  "+strAuthor);
//		textView_title.setText(strTitle);
		textView.setText(strContent);
//		textView_author.setText(strAuthor);
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

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this); // 统计时长

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);

	}

	public void onClickFinish(View v) {
		finish();
	}

	public void onClickShare(View v) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		// 分享的数据类型
		intent.setType("text/plain");
		// 分享的主题
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		// 分享的内容
		intent.putExtra(
				Intent.EXTRA_TEXT,
				"小伙伴们快来下载吧"
						+ "\n"
						+ Conf.DOWNLOAD_APK);
		// 允许启动新的Activity
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 目标应用寻找对话框的标题
		startActivity(Intent.createChooser(intent, getTitle()));
	}

	@Override
	public void onClick(View arg0) {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) { 
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}