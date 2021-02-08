package site.qifen.android_samples;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class SimplePagingAdapter extends PagedListAdapter<User,SimplePagingAdapter.MyViewHolder> {


    protected SimplePagingAdapter() {
        super(new DiffUtil.ItemCallback<User>() {
            @Override
            public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                return oldItem.getUserId() == newItem.getUserId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                return oldItem.getUserName().equals(newItem.getUserName());
            }
        });
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setTag("tv");
        return new MyViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = getItem(position);
        TextView textView = (TextView) holder.itemView;
        if (user != null) {
            textView.setText("name: "+user.getUserName() + "   say: "+user.getUserSay());
        } else {
            textView.setText("Loading...");
        }

    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull TextView itemView) {
            super(itemView);
        }
    }
}
