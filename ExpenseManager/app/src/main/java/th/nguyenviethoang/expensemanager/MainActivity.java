package th.nguyenviethoang.expensemanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private TextView tvTotalIncome, tvTotalExpense, tvBalance;
    private FloatingActionButton fabAdd;
    private Spinner spinnerFilter;

    private String currentFilter = "Tất cả";
    private String customStartDate = "";
    private String customEndDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = DatabaseHelper.getInstance(this);

        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvBalance = findViewById(R.id.tvBalance);
        fabAdd = findViewById(R.id.fabAdd);
        recyclerView = findViewById(R.id.recyclerView);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupFilterSpinner();

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

    private void setupFilterSpinner() {
        List<String> filters = new ArrayList<>();
        filters.add("Tất cả");
        filters.add("Hôm nay");
        filters.add("Tuần này");
        filters.add("Tháng này");
        filters.add("Tháng trước");
        filters.add("Năm này");
        filters.add("Tùy chọn...");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                filters
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentFilter = filters.get(position);

                if (currentFilter.equals("Tùy chọn...")) {
                    showCustomDatePicker();
                } else {
                    loadData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showCustomDatePicker() {
        Calendar calendar = Calendar.getInstance();

        // Chọn ngày bắt đầu
        new DatePickerDialog(this, (view1, year1, month1, day1) -> {
            customStartDate = String.format("%02d/%02d/%d", day1, month1 + 1, year1);

            // Chọn ngày kết thúc
            new DatePickerDialog(this, (view2, year2, month2, day2) -> {
                customEndDate = String.format("%02d/%02d/%d", day2, month2 + 1, year2);
                loadData();
            }, year1, month1, day1).show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<Transaction> allTransactions = dbHelper.getAllTransactions();
        List<Transaction> filteredTransactions = filterTransactions(allTransactions);

        adapter = new ExpenseAdapter(this, filteredTransactions, new ExpenseAdapter.OnItemClickListener() {
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

        // Tính tổng từ danh sách đã lọc
        double income = 0;
        double expense = 0;

        for (Transaction t : filteredTransactions) {
            if (t.getType().equals("Thu nhập")) {
                income += t.getAmount();
            } else {
                expense += t.getAmount();
            }
        }

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

    private List<Transaction> filterTransactions(List<Transaction> transactions) {
        List<Transaction> filtered = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String today = sdf.format(calendar.getTime());

        for (Transaction t : transactions) {
            String transactionDate = t.getDate();

            switch (currentFilter) {
                case "Tất cả":
                    filtered.add(t);
                    break;

                case "Hôm nay":
                    if (transactionDate.equals(today)) {
                        filtered.add(t);
                    }
                    break;

                case "Tuần này":
                    if (isThisWeek(transactionDate)) {
                        filtered.add(t);
                    }
                    break;

                case "Tháng này":
                    String currentMonth = transactionDate.substring(3, 10); // MM/yyyy
                    String thisMonth = today.substring(3, 10);
                    if (currentMonth.equals(thisMonth)) {
                        filtered.add(t);
                    }
                    break;

                case "Tháng trước":
                    calendar.add(Calendar.MONTH, -1);
                    String lastMonth = sdf.format(calendar.getTime()).substring(3, 10);
                    String transMonth = transactionDate.substring(3, 10);
                    if (transMonth.equals(lastMonth)) {
                        filtered.add(t);
                    }
                    calendar.add(Calendar.MONTH, 1); // Reset
                    break;

                case "Năm này":
                    String currentYear = transactionDate.substring(6, 10); // yyyy
                    String thisYear = today.substring(6, 10);
                    if (currentYear.equals(thisYear)) {
                        filtered.add(t);
                    }
                    break;

                case "Tùy chọn...":
                    if (!customStartDate.isEmpty() && !customEndDate.isEmpty()) {
                        if (isDateInRange(transactionDate, customStartDate, customEndDate)) {
                            filtered.add(t);
                        }
                    }
                    break;
            }
        }

        return filtered;
    }

    private boolean isThisWeek(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Calendar transactionCal = Calendar.getInstance();
            transactionCal.setTime(sdf.parse(dateStr));

            Calendar todayCal = Calendar.getInstance();

            int currentWeek = todayCal.get(Calendar.WEEK_OF_YEAR);
            int transactionWeek = transactionCal.get(Calendar.WEEK_OF_YEAR);
            int currentYear = todayCal.get(Calendar.YEAR);
            int transactionYear = transactionCal.get(Calendar.YEAR);

            return currentWeek == transactionWeek && currentYear == transactionYear;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isDateInRange(String dateStr, String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            long date = sdf.parse(dateStr).getTime();
            long start = sdf.parse(startDate).getTime();
            long end = sdf.parse(endDate).getTime();

            return date >= start && date <= end;
        } catch (Exception e) {
            return false;
        }
    }
}