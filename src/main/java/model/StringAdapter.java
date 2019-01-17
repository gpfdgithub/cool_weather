package model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.coolweather.R;

import java.util.List;

public class StringAdapter extends BaseAdapter {

    private Context mContext;
    private List<PerviewListItem> dataList;

    public StringAdapter(Context mContext, List<PerviewListItem> daraList) {
        this.mContext = mContext;
        this.dataList = daraList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.preview_list_item, parent, false);
        }
        ((TextView)convertView.findViewById(R.id.item1))
                .setText(dataList.get(position).getDay());

        ((TextView)convertView.findViewById(R.id.item2))
                .setText(dataList.get(position).getInfo());

        ((TextView)convertView.findViewById(R.id.item3))
                .setText(dataList.get(position).getMaxTmp());

        ((TextView)convertView.findViewById(R.id.item4))
                .setText(dataList.get(position).getMinTmp());
        return convertView;

    }
}
