package th.nguyenviethoang.expensemanager;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    private TabLayout tabLayout;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        rvCategories = findViewById(R.id.rvCategories);
        tabLayout = findViewById(R.id.tabLayout);
        btnBack = findViewById(R.id.btnBack);

        rvCategories.setLayoutManager(new GridLayoutManager(this, 3));

        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(this, categoryList);
        rvCategories.setAdapter(adapter);

        // Tabs
        tabLayout.addTab(tabLayout.newTab().setText("Chi tiêu"));
        tabLayout.addTab(tabLayout.newTab().setText("Thu nhập"));

        loadExpenseCategories();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    loadExpenseCategories();
                } else {
                    loadIncomeCategories();
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        btnBack.setOnClickListener(v -> finish());
    }


    private void loadExpenseCategories() {
        categoryList.clear();
        categoryList.add(new Category("Ăn uống", "🍔"));
        categoryList.add(new Category("Mua sắm", "🛍"));
        categoryList.add(new Category("Di chuyển", "🚌"));
        categoryList.add(new Category("Giải trí", "🎮"));
        categoryList.add(new Category("Hóa đơn", "💡"));
        categoryList.add(new Category("Khác", "📦"));
        adapter.notifyDataSetChanged();
    }

    private void loadIncomeCategories() {
        categoryList.clear();
        categoryList.add(new Category("Lương", "💼"));
        categoryList.add(new Category("Thưởng", "🎁"));
        categoryList.add(new Category("Đầu tư", "📈"));
        categoryList.add(new Category("Khác", "💰"));
        adapter.notifyDataSetChanged();
    }
}
