package th.nguyenviethoang.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private TextView tvTotalIncome, tvTotalExpense, tvBalance;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ SỬA: Dùng getInstance() thay vì new DatabaseHelper()
        dbHelper = DatabaseHelper.getInstance(this);

        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvBalance = findViewById(R.id.tvBalance);
        fabAdd = findViewById(R.id.fabAdd);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddTransactionActivity.class));
        });

        findViewById(R.id.btnCategories).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CategoriesActivity.class));
        });

        findViewById(R.id.btnStatistics).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<Transaction> transactions = dbHelper.getAllTransactions();

        adapter = new ExpenseAdapter(this, transactions, new ExpenseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Transaction transaction) {
                Intent intent = new Intent(MainActivity.this, TransactionDetailActivity.class);
                intent.putExtra("transaction_id", transaction.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Transaction transaction) {
                new AlertDialog.Builder(MainActivity.this)
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

        double income = dbHelper.getTotalByType("Thu nhập");
        double expense = dbHelper.getTotalByType("Chi tiêu");
        double balance = income - expense;

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        tvTotalIncome.setText(nf.format(income) + " đ");
        tvTotalExpense.setText(nf.format(expense) + " đ");
        tvBalance.setText(nf.format(balance) + " đ");

        if (balance < 0) {
            tvBalance.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvBalance.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }
}