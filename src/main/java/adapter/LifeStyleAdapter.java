package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.coolweather.R;

import java.util.List;

import model.Lifestyleitem;

public class LifeStyleAdapter extends RecyclerView.Adapter<LifeStyleAdapter.ViewHolder> {

    private static final String TAG = "MainActivity";

    private List<Lifestyleitem> list;
    private Context mContext;

    public LifeStyleAdapter(List<Lifestyleitem> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.liftstyle_item, parent, false);
        ViewHolder  viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Lifestyleitem item = list.get(position);
        holder.typeTextView.setText(item.getType());
        holder.brfTextView.setText(item.getBrf());
        holder.txtTextView.setText(item.getTxt());
        Log.d(TAG, "onBindViewHolder: " + holder.txtTextView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        private TextView txtTextView;
        private TextView brfTextView;
        private TextView typeTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            txtTextView = itemView.findViewById(R.id.liftstyle_txt);
            brfTextView = itemView.findViewById(R.id.liftstyle_brf);
            typeTextView = itemView.findViewById(R.id.liftstyle_type);
        }
    }
}
