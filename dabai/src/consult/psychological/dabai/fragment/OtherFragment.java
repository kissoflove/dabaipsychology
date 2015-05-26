package consult.psychological.dabai.fragment;

import java.lang.reflect.Type;
import java.util.List;

import net.tsz.afinal.FinalDb;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.umeng.analytics.MobclickAgent;

import consult.psychological.dabai.R;
import consult.psychological.dabai.activity.ArticleContentActivity;
import consult.psychological.dabai.adapter.ArticleAdapter;
import consult.psychological.dabai.bean.Article;
import consult.psychological.dabai.commont.APIURL;
import consult.psychological.dabai.lib.NetWorkUtil;

public class OtherFragment extends Fragment implements
		android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener {
	ListView listView;
	ArticleAdapter adapter;
	private CountDownTimer timer;
	// private ArrayList<Article> listArticle = new ArrayList<Article>();
	ProgressBar progressbar;
	SwipeRefreshLayout swipe;
	private List<Article> sections;
	FinalDb db;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_other, container, false);
		initView(view);

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// MobclickAgent.onPageStart("MainScreen"); //统计页面
		MobclickAgent.onResume(getActivity()); // 统计时长
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(getActivity());
	}

	private void initView(View view) {
		db = FinalDb.create(getActivity(), false);

		// if (sections == null || sections.size() == 0) {
		// getDataA(APIURL.SERVER_URL + "item_b.json");
		// adapter = new ArticleAdapter(getActivity(), listView);
		// listView.setAdapter(adapter);
		// adapter.resetData(sections);
		// } else {
		// adapter = new ArticleAdapter(getActivity(), listView);
		// listView.setAdapter(adapter);
		// adapter.resetData(sections);
		// }

		listView = (ListView) view.findViewById(R.id.listView);
		swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
		swipe.setOnRefreshListener(this);
		// 顶部刷新的样式
		swipe.setColorSchemeResources(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		timer = new CountDownTimer(9 * 100, 100) {

			@Override
			public void onTick(long millisUntilFinished) {
				long a = millisUntilFinished / 100;

				if (a == 1) {
					getDataA(APIURL.SERVER_URL + "item_b.json");
					Log.e("APIURL:", APIURL.SERVER_URL + "item_b.json");
					adapter = new ArticleAdapter(getActivity(), listView);
					listView.setAdapter(adapter);

					// progressbar.setVisibility(View.GONE);
				} else {
					progressbar.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onFinish() {

			}
		};

		progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
		sections = db.findAllByWhere(Article.class, "1=1", "id");
		if (NetWorkUtil.networkCanUse(getActivity())) {
			timer.start();
		} else {
			progressbar.setVisibility(View.GONE);
			if (sections != null || sections.size() != 0) {
				adapter = new ArticleAdapter(getActivity(), listView);
				listView.setAdapter(adapter);
				adapter.resetData(sections);
			}
		}
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				String a = sections.get(arg2).pcount;
				String author = sections.get(arg2).created;
				String title = sections.get(arg2).title;
				Intent it = new Intent(getActivity(),
						ArticleContentActivity.class);
				it.putExtra("CONTENT", a);
				it.putExtra("AUTHOR", author);
				it.putExtra("TITLE", title);
				startActivity(it);
			}
		});

	}

	private void getDataA(String jsonurl) {
		if (isAdded() == true) {
			Ion.with(getActivity(), jsonurl).asJsonObject()
					.setCallback(new FutureCallback<JsonObject>() {

						@Override
						public void onCompleted(Exception e, JsonObject result) {
							Log.e("------------->	", "" + result);
							if (e != null) {
								return;
							}
							String code = result.get("code").getAsString();

							if ("200".equals(code)) {

								Log.e("", "200");
								Type type = new TypeToken<List<Article>>() {
								}.getType();
								Gson gson = new Gson();
								sections = gson.fromJson(result.get("msg"),
										type);
								adapter.resetData(sections);
								add(sections);
								progressbar.setVisibility(View.GONE);
							} else if ("401".equals(code)) {
							} else {
								Toast.makeText(getActivity(),
										result.get("msg").getAsString(), 1)
										.show();
							}
						}
					});
		}
	}

	@Override
	public void onRefresh() {

		new Handler().postDelayed(new Runnable() {
			public void run() {

				if (NetWorkUtil.networkCanUse(getActivity())) {
					getDataA(APIURL.SERVER_URL + "item_b.json");
					adapter = new ArticleAdapter(getActivity(), listView);
					listView.setAdapter(adapter);
				} else {
					Toast.makeText(getActivity(), "网络连接失败..", 1).show();
					progressbar.setVisibility(View.GONE);
					if (sections != null || sections.size() != 0) {
						adapter = new ArticleAdapter(getActivity(), listView);
						listView.setAdapter(adapter);
						adapter.resetData(sections);
					}
				}

				swipe.setRefreshing(false);

			}
		}, 1500);
	}

	private void add(List<Article> a) {
		if (sections != null && sections.size() > 0) {
			for (Article tmpSection : a) {
				Article oldSection = db.findById(tmpSection.id, Article.class);
				if (oldSection == null) {
					db.save(tmpSection);
				} else {
					db.update(tmpSection);
				}
			}
		}

	}
}