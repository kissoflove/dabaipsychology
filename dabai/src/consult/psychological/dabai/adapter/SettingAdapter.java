package consult.psychological.dabai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import consult.psychological.dabai.R;

public class SettingAdapter extends BaseAdapter {
	
	String[] list;
	Context context;

	public SettingAdapter(String[] list, Context context) {
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_setting_adapter, null);
			viewHolder = new ViewHolder();
			viewHolder.toolbox_title = (TextView) convertView
					.findViewById(R.id.toolbox_title);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.toolbox_title.setText(list[position]);

		return convertView;
	}

	private class ViewHolder {
		TextView toolbox_title;

	}
}
