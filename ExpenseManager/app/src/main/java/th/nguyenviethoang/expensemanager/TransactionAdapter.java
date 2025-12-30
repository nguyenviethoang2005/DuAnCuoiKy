package th.nguyenviethoang.expensemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private Context context;
    private List<Transaction> transactionList;
    private OnDeleteClickListener deleteClickListener;

    // Interface để xử lý nút xóa
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view, deleteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.tvCategory.setText(transaction.getCategory());
        holder.tvDate.setText(transaction.getDate());
        holder.tvAmount.setText(transaction.getAmount() + " đ");

        // Hiển thị icon danh mục, bạn có thể tùy chỉnh theo category
        holder.tvCategoryIcon.setText(transaction.getCategoryIcon());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvDate, tvAmount, tvCategoryIcon;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView, OnDeleteClickListener listener) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCategoryIcon = itemView.findViewById(R.id.tvCategoryIcon);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
    }
}
