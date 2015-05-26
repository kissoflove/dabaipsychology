package consult.psychological.dabai.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import consult.psychological.dabai.R;

public class TuiAdapter extends BaseAdapter{
	String[] list;
	Context context;
	public TuiAdapter(String[] list,Context context) {
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
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context)
					.inflate(R.layout.item_tui, null);
			holder = new ViewHolder();
			holder.app_info = (TextView) convertView
					.findViewById(R.id.app_info);
			holder.app_name = (TextView) convertView
					.findViewById(R.id.app_name);
			holder.logo_id = (ImageView) convertView
					.findViewById(R.id.logo_id);

			if (position == 0) {
				holder.logo_id
						.setImageResource(R.drawable.logo_beautyidea);
				holder.app_name.setText("最美创意");
			} else if (position == 1) {
				holder.app_name.setTextColor(Color.RED);
				holder.app_name.setText("随口记语音记账");
				holder.logo_id
						.setImageResource(R.drawable.logo_shangpuyun);

			} 
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.app_info.setText(list[position]);

		 
	 
		return convertView;
	}

	class ViewHolder {
		TextView app_info;
		ImageView logo_id;
		TextView app_name;
	}

}