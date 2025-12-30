package th.nguyenviethoang.expensemanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private TextView tvStatExpense, tvStatIncome, tvStatBalance;
    private PieChart pieChart;
    private BarChart barChart;
    private TabLayout tabLayout;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        tvStatExpense = findViewById(R.id.tvStatExpense);
        tvStatIncome  = findViewById(R.id.tvStatIncome);
        tvStatBalance = findViewById(R.id.tvStatBalance);
        pieChart      = findViewById(R.id.pieChart);
        barChart      = findViewById(R.id.barChart);
        tabLayout     = findViewById(R.id.tabLayoutStats);

        dbHelper = DatabaseHelper.getInstance(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        setupTabs();
        loadStatistics();
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Tổng quan"));
        tabLayout.addTab(tabLayout.newTab().setText("Theo tháng"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // Tổng quan
                    pieChart.setVisibility(android.view.View.VISIBLE);
                    barChart.setVisibility(android.view.View.GONE);
                    loadStatistics();
                } else {
                    // Theo tháng
                    pieChart.setVisibility(android.view.View.GONE);
                    barChart.setVisibility(android.view.View.VISIBLE);
                    loadMonthlyStatistics();
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadStatistics() {
        double totalExpense = dbHelper.getTotalByType("Chi tiêu");
        double totalIncome  = dbHelper.getTotalByType("Thu nhập");
        double balance = totalIncome - totalExpense;

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        tvStatExpense.setText("Chi tiêu: " + nf.format(totalExpense) + " đ");
        tvStatIncome.setText("Thu nhập: " + nf.format(totalIncome) + " đ");
        tvStatBalance.setText("Số dư: " + nf.format(balance) + " đ");

        if (balance < 0) {
            tvStatBalance.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvStatBalance.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }

        setupPieChart(totalExpense, totalIncome);
    }

    private void setupPieChart(double expense, double income) {
        List<PieEntry> entries = new ArrayList<>();

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

    private void loadMonthlyStatistics() {
        List<Transaction> allTransactions = dbHelper.getAllTransactions();

        // Lấy 6 tháng gần nhất
        Calendar calendar = Calendar.getInstance();
        Map<String, Double> monthlyIncome = new HashMap<>();
        Map<String, Double> monthlyExpense = new HashMap<>();
        List<String> months = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            calendar.add(Calendar.MONTH, i == 5 ? 0 : -1);
            String monthKey = String.format("%02d/%d",
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR));
            months.add(monthKey);
            monthlyIncome.put(monthKey, 0.0);
            monthlyExpense.put(monthKey, 0.0);
        }


        for (Transaction t : allTransactions) {
            String transactionDate = t.getDate(); // dd/MM/yyyy
            if (transactionDate.length() >= 10) {
                String monthKey = transactionDate.substring(3, 10); // MM/yyyy

                if (months.contains(monthKey)) {
                    if (t.getType().equals("Thu nhập")) {
                        monthlyIncome.put(monthKey, monthlyIncome.get(monthKey) + t.getAmount());
                    } else {
                        monthlyExpense.put(monthKey, monthlyExpense.get(monthKey) + t.getAmount());
                    }
                }
            }
        }

        setupBarChart(months, monthlyIncome, monthlyExpense);


        double totalIncome6Months = 0;
        double totalExpense6Months = 0;

        for (String month : months) {
            totalIncome6Months += monthlyIncome.get(month);
            totalExpense6Months += monthlyExpense.get(month);
        }

        double balance6Months = totalIncome6Months - totalExpense6Months;
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        tvStatExpense.setText("Chi tiêu (6 tháng): " + nf.format(totalExpense6Months) + " đ");
        tvStatIncome.setText("Thu nhập (6 tháng): " + nf.format(totalIncome6Months) + " đ");
        tvStatBalance.setText("Số dư (6 tháng): " + nf.format(balance6Months) + " đ");

        if (balance6Months < 0) {
            tvStatBalance.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvStatBalance.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    private void setupBarChart(List<String> months, Map<String, Double> income, Map<String, Double> expense) {
        ArrayList<BarEntry> incomeEntries = new ArrayList<>();
        ArrayList<BarEntry> expenseEntries = new ArrayList<>();

        for (int i = 0; i < months.size(); i++) {
            String month = months.get(i);
            incomeEntries.add(new BarEntry(i, income.get(month).floatValue()));
            expenseEntries.add(new BarEntry(i, expense.get(month).floatValue()));
        }

        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Thu nhập");
        incomeDataSet.setColor(getResources().getColor(android.R.color.holo_green_dark));

        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Chi tiêu");
        expenseDataSet.setColor(getResources().getColor(android.R.color.holo_red_dark));

        BarData data = new BarData(incomeDataSet, expenseDataSet);
        data.setBarWidth(0.35f);

        barChart.setData(data);
        barChart.groupBars(0, 0.3f, 0.05f);


        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelRotationAngle(-45);

        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.animateY(1000);
        barChart.invalidate();
    }
}