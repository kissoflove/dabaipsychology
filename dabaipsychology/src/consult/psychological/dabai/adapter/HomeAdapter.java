package consult.psychological.dabai.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.e;

import consult.psychological.dabai.Conf;
import consult.psychological.dabai.R;
import consult.psychological.dabai.bean.HomeBean;
import consult.psychological.dabai.lib.FlipLayout;

public class HomeAdapter extends BaseAdapter {
	private List<HomeBean> homeList = new ArrayList<HomeBean>();
	private Context context;
	private HomeBean homeBean;
	ListView listView;

	public HomeAdapter(Context context, ListView listView) {
		this.context = context;
		this.listView = listView;
	}

	public void resetData(List<HomeBean> list) {
		this.homeList.clear();
		this.homeList.addAll(list);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return homeList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return homeList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_one, null);
			viewHolder = new ViewHolder();
			viewHolder.item_vickeytalk_tv = (TextView) convertView
					.findViewById(R.id.item_vickeytalk_tv);
			viewHolder.item_vickeytalk_iv = (ImageView) convertView
					.findViewById(R.id.item_vickeytalk_iv);
			
//			viewHolder.flipLayout= (FlipLayout) convertView.findViewById(R.id.flipLayout);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		homeBean = homeList.get(position);
		viewHolder.item_vickeytalk_tv.setText(homeBean.content);
		viewHolder.item_vickeytalk_tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				viewHolder.item_vickeytalk_iv.setVisibility(View.VISIBLE);
				viewHolder.item_vickeytalk_tv.setVisibility(View.GONE);
				
			}
		});
		viewHolder.item_vickeytalk_iv.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				int a = homeList.get(position).imgid;
				Log.e("",""+a);
				downImg(a);
				return false;
			}
		});
		Ion.with(context, homeBean.img).withBitmap()
				.intoImageView(viewHolder.item_vickeytalk_iv);

 
		return convertView;
	}

	private class ViewHolder {
		TextView item_vickeytalk_tv;
		ImageView item_vickeytalk_iv;
	    FlipLayout flipLayout;
	}
	private void downImg(final int id) {
		HttpUtils http = new HttpUtils();
		HttpHandler handler = http.download(Conf.APP_IMG+id+".jpg",
		    "/sdcard/zhidu/+"+id+".jpg",
		    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
		    true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
		    new RequestCallBack<File>() {

		        @Override
		        public void onStart() {
		        	Log.e("onStart","........start......");
		        }

		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        	Log.e("onLoading",total+"|"+current);
		        }

		        @Override
		        public void onSuccess(ResponseInfo<File> responseInfo) {
		        	Log.e("onSuccess", responseInfo.toString());
		        	Toast.makeText(context, "图片已保存到 /sdcard/zhidu/"+id+".jpg", 1).show();
		        	
		        }


		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	Log.e("onFailure","........msg......"+msg);
		        }
		});

	}
}