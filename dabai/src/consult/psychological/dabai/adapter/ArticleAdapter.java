package consult.psychological.dabai.adapter;

import java.util.ArrayList;
import java.util.List;

import consult.psychological.dabai.R;
import consult.psychological.dabai.bean.Article;
import consult.psychological.dabai.bean.HomeBean;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ArticleAdapter extends BaseAdapter {
	private List<Article> list = new ArrayList<Article>();
	private Context context;
	private Article article;
	ListView listView;

	public ArticleAdapter(Context context, ListView listView) {
		this.context = context;
		this.listView = listView;
	}
	public void resetData(List<Article> list) {
		this.list.clear();
		this.list.addAll(list);
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_text_article,
					null);
			viewHolder = new ViewHolder();
			viewHolder.adapter_article_title_tv = (TextView) convertView
					.findViewById(R.id.adapter_article_title_tv);
			viewHolder.adapter_article_content_tv = (TextView) convertView
					.findViewById(R.id.adapter_article_content_tv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		article = list.get(position);

		viewHolder.adapter_article_title_tv.setText(article.title);
		viewHolder.adapter_article_content_tv.setText(article.content);
		return convertView;
	}

	public class ViewHolder {
		TextView adapter_article_title_tv;
		TextView adapter_article_content_tv;

	}
}
