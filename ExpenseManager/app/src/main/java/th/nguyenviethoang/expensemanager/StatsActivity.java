package th.nguyenviethoang.expensemanager;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private TextView tvStatExpense, tvStatIncome, tvStatBalance;
    private PieChart pieChart;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        tvStatExpense = findViewById(R.id.tvStatExpense);
        tvStatIncome = findViewById(R.id.tvStatIncome);
        tvStatBalance = findViewById(R.id.tvStatBalance);
        pieChart = findViewById(R.id.pieChart);

        dbHelper = new DatabaseHelper(this);

        updateStats();
    }

    private void updateStats() {
        double income = safeGetTotalByType("Thu");
        double expense = safeGetTotalByType("Chi");
        double balance = income - expense;

        tvStatIncome.setText("Tổng thu nhập: " + (int) income + " ₫");
        tvStatExpense.setText("Tổng chi tiêu: " + (int) expense + " ₫");
        tvStatBalance.setText("Số dư: " + (int) balance + " ₫");

        List<PieEntry> entries = new ArrayList<>();
        if (income > 0) entries.add(new PieEntry((float) income, "Thu"));
        if (expense > 0) entries.add(new PieEntry((float) expense, "Chi"));

        if (!entries.isEmpty()) {
            PieDataSet dataSet = new PieDataSet(entries, "Thống kê");
            dataSet.setColors(new int[]{0xFF43A047, 0xFFE53935});
            PieData data = new PieData(dataSet);
            pieChart.setData(data);
        } else {
            pieChart.clear();
        }
        pieChart.invalidate();
    }

    private double safeGetTotalByType(String type) {
        double total = 0;
        try {
            total = dbHelper.getTotalByType(type);
        } catch (Exception e) {
            Log.e("StatsActivity", "Lỗi đọc DB", e);
        }
        return total;
    }
}
