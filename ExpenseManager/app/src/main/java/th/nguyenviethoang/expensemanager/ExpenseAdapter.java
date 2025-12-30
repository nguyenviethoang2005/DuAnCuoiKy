package th.nguyenviethoang.expensemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private Context context;
    private List<Transaction> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
        void onDeleteClick(Transaction transaction);
    }

    public ExpenseAdapter(Context context, List<Transaction> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction t = list.get(position);

        holder.tvCategory.setText(t.getCategory());
        holder.tvDate.setText(t.getDate());

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvAmount.setText(nf.format(t.getAmount()) + " đ");


        holder.tvIcon.setText(getIconByCategory(t.getCategory()));


        if (t.getType().equals("Chi tiêu")) {
            holder.tvAmount.setTextColor(
                    context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvAmount.setTextColor(
                    context.getResources().getColor(android.R.color.holo_green_dark));
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(t));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(t));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private String getIconByCategory(String category) {
        if (category == null) return "💰";

        switch (category) {

            case "Ăn uống":
                return "🍔";
            case "Mua sắm":
                return "🛒";
            case "Di chuyển":
                return "🚗";
            case "Giải trí":
                return "🎮";
            case "Hóa đơn":
                return "💡";


            case "Lương":
                return "💵";
            case "Thưởng":
                return "🎁";
            case "Đầu tư":
                return "📈";


            case "Khác":
                return "📦";
            default:
                return "💰";
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvIcon, tvCategory, tvAmount, tvDate;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvIcon = itemView.findViewById(R.id.tvCategoryIcon);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}