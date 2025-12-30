package th.nguyenviethoang.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryTransactionsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private TextView tvCategoryTitle, tvCategoryTotal;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_transactions);

        dbHelper = DatabaseHelper.getInstance(this);

        categoryName = getIntent().getStringExtra("category_name");

        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        tvCategoryTotal = findViewById(R.id.tvCategoryTotal);
        recyclerView = findViewById(R.id.recyclerView);

        tvCategoryTitle.setText(categoryName);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadData();
    }

    private void loadData() {
        List<Transaction> allTransactions = dbHelper.getAllTransactions();
        List<Transaction> filteredTransactions = new ArrayList<>();
        double total = 0;

        // Lọc giao dịch theo danh mục
        for (Transaction t : allTransactions) {
            if (t.getCategory().equals(categoryName)) {
                filteredTransactions.add(t);
                if (t.getType().equals("Chi tiêu")) {
                    total -= t.getAmount();
                } else {
                    total += t.getAmount();
                }
            }
        }

        adapter = new ExpenseAdapter(this, filteredTransactions, new ExpenseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Transaction transaction) {
                Intent intent = new Intent(CategoryTransactionsActivity.this, TransactionDetailActivity.class);
                intent.putExtra("transaction_id", transaction.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Transaction transaction) {
                new AlertDialog.Builder(CategoryTransactionsActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc muốn xóa giao dịch này?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            dbHelper.deleteTransaction(transaction.getId());
                            loadData();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvCategoryTotal.setText("Tổng: " + nf.format(Math.abs(total)) + " đ");

        if (total < 0) {
            tvCategoryTotal.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvCategoryTotal.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}