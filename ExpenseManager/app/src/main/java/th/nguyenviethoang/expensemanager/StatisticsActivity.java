package th.nguyenviethoang.expensemanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    private TextView tvStatExpense, tvStatIncome, tvStatBalance;
    private PieChart pieChart;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        tvStatExpense = findViewById(R.id.tvStatExpense);
        tvStatIncome  = findViewById(R.id.tvStatIncome);
        tvStatBalance = findViewById(R.id.tvStatBalance);
        pieChart      = findViewById(R.id.pieChart);

        // ✅ SỬA: Dùng getInstance() thay vì new DatabaseHelper()
        dbHelper = DatabaseHelper.getInstance(this);

        // Nút Back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadStatistics();
    }

    private void loadStatistics() {
        double totalExpense = dbHelper.getTotalByType("Chi tiêu");
        double totalIncome  = dbHelper.getTotalByType("Thu nhập");
        double balance = totalIncome - totalExpense;

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        tvStatExpense.setText("Chi tiêu: " + nf.format(totalExpense) + " đ");
        tvStatIncome.setText("Thu nhập: " + nf.format(totalIncome) + " đ");
        tvStatBalance.setText("Số dư: " + nf.format(balance) + " đ");

        // Set màu cho số dư
        if (balance < 0) {
            tvStatBalance.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvStatBalance.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }

        setupPieChart(totalExpense, totalIncome);
    }

    private void setupPieChart(double expense, double income) {
        List<PieEntry> entries = new ArrayList<>();

        // Chỉ thêm nếu có giá trị
        if (expense > 0) entries.add(new PieEntry((float) expense, "Chi tiêu"));
        if (income > 0) entries.add(new PieEntry((float) income, "Thu nhập"));

        if (entries.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("Chưa có dữ liệu thống kê");
            pieChart.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "Thống kê");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Thu / Chi");
        pieChart.setCenterTextSize(16f);
        pieChart.animateY(1000);

        pieChart.invalidate();
    }
}