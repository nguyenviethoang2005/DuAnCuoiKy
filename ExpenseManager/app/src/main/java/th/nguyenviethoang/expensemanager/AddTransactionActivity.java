package th.nguyenviethoang.expensemanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText etAmount, etNote, etDate;
    private Spinner spinnerCategory;
    private RadioGroup rgType;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // ✅ SỬA: Dùng getInstance() thay vì new DatabaseHelper()
        dbHelper = DatabaseHelper.getInstance(this);
        calendar = Calendar.getInstance();

        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        etDate = findViewById(R.id.etDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        rgType = findViewById(R.id.rgType);
        btnSave = findViewById(R.id.btnSave);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        etDate.setText(sdf.format(calendar.getTime()));

        etDate.setOnClickListener(v -> showDatePicker());

        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbExpense) {
                loadCategories("Chi tiêu");
            } else {
                loadCategories("Thu nhập");
            }
        });

        loadCategories("Chi tiêu");

        btnSave.setOnClickListener(v -> saveTransaction());

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    calendar.set(year, month, day);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    etDate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void loadCategories(String type) {
        List<String> categoryNames = new ArrayList<>();

        if (type.equals("Chi tiêu")) {
            categoryNames.add("Ăn uống");
            categoryNames.add("Mua sắm");
            categoryNames.add("Di chuyển");
            categoryNames.add("Giải trí");
            categoryNames.add("Hóa đơn");
            categoryNames.add("Khác");
        } else {
            categoryNames.add("Lương");
            categoryNames.add("Thưởng");
            categoryNames.add("Đầu tư");
            categoryNames.add("Khác");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void saveTransaction() {
        String amountStr = etAmount.getText().toString().trim();
        String note = etNote.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        if (date.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        String type = rgType.getCheckedRadioButtonId() == R.id.rbExpense
                ? "Chi tiêu"
                : "Thu nhập";

        long result = dbHelper.addTransaction(amount, category, type, note, date);

        if (result > 0) {
            Toast.makeText(this, "Đã lưu giao dịch", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi lưu giao dịch", Toast.LENGTH_SHORT).show();
        }
    }
}