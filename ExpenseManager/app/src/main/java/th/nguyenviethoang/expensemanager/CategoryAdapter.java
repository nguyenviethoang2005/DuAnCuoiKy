package th.nguyenviethoang.expensemanager;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> list;
    private Activity activity;

    public CategoryAdapter(Activity activity, List<Category> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category c = list.get(position);

        holder.tvName.setText(c.getName());
        holder.tvIcon.setText(c.getIcon());

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(activity, CategoryTransactionsActivity.class);
            intent.putExtra("category_name", c.getName());
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvName;

        ViewHolder(View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tvCategoryIcon);
            tvName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}