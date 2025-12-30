package th.nguyenviethoang.expensemanager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.util.Locale;

public class TransactionDetailActivity extends AppCompatActivity {

    private TextView tvAmount, tvCategory, tvDate, tvNote;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        tvAmount = findViewById(R.id.tvAmount);
        tvCategory = findViewById(R.id.tvCategory);
        tvDate = findViewById(R.id.tvDate);
        tvNote = findViewById(R.id.tvNote);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // ✅ SỬA: Dùng getInstance() thay vì new DatabaseHelper()
        dbHelper = DatabaseHelper.getInstance(this);

        int transactionId = getIntent().getIntExtra("transaction_id", -1);

        if (transactionId == -1) return;

        Transaction t = dbHelper.getTransactionById(transactionId);

        if (t != null) {
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

            tvAmount.setText(nf.format(t.getAmount()) + " đ");
            tvCategory.setText(t.getCategory());
            tvDate.setText(t.getDate());

            if (t.getNote() == null || t.getNote().isEmpty()) {
                tvNote.setText("Không có ghi chú");
            } else {
                tvNote.setText(t.getNote());
            }
        }
    }
}