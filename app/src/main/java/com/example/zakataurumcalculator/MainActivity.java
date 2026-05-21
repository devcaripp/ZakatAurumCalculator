package com.example.zakataurumcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    Toolbar myToolbar;
    EditText editTextWeight, editTextPrice;
    Spinner spinnerGoldType;
    Button buttonCalculate, buttonReset;
    TextView textViewTotalValue, textViewZakatValue, textViewTotalZakat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Toolbar setup
        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        // Link views
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextPrice = findViewById(R.id.editTextPrice);
        spinnerGoldType = findViewById(R.id.spinnerGoldType);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        textViewTotalValue = findViewById(R.id.textViewTotalValue);
        textViewZakatValue = findViewById(R.id.textViewZakatValue);
        textViewTotalZakat = findViewById(R.id.textViewTotalZakat);

        // Spinner setup
        String[] goldTypes = {"Keep", "Wear"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, goldTypes) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextColor(android.graphics.Color.BLACK);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setTextColor(android.graphics.Color.BLACK);
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoldType.setAdapter(adapter);

        // Button click
        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateZakat();
            }
        });

        buttonReset = findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextWeight.setText("");
                editTextPrice.setText("");
                spinnerGoldType.setSelection(0);
                textViewTotalValue.setText("Total Value: ");
                textViewZakatValue.setText("Zakat Payable: ");
                textViewTotalZakat.setText("Total Zakat: ");
            }
        });
    }

    private void calculateZakat() {
        String weightStr = editTextWeight.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String selectedType = spinnerGoldType.getSelectedItem().toString();

        // Validation
        if (weightStr.isEmpty()) {
            Toast.makeText(this, "Please enter gold weight.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedType.equals("Select Gold Type")) {
            Toast.makeText(this, "Please select gold type.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (priceStr.isEmpty()) {
            Toast.makeText(this, "Please enter gold price per gram.", Toast.LENGTH_SHORT).show();
            return;
        }

        double weight = Double.parseDouble(weightStr);
        double price = Double.parseDouble(priceStr);

        // Nisab based on type
        double nisab = selectedType.contains("Keep") ? 85 : 200;

        // Calculations
        double totalValue = weight * price;
        double zakatableWeight = Math.max(0, weight - nisab);
        double zakatPayableValue = zakatableWeight * price;
        double totalZakat = zakatPayableValue * 0.025;

        // Display results
        textViewTotalValue.setText(String.format("Total Value: RM %.2f", totalValue));
        textViewZakatValue.setText(String.format("Zakat Payable: RM %.2f", zakatPayableValue));
        textViewTotalZakat.setText(String.format("Total Zakat: RM %.2f", totalZakat));

        if (zakatableWeight == 0) {
            Toast.makeText(this, "Gold does not reach nisab. No zakat required.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "My GitHub link - https://github.com/devcaripp/ZakatAurumCalculator");
            startActivity(Intent.createChooser(shareIntent, null));
            return true;
        } else if (item.getItemId() == R.id.item_about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
            return true;
        }
        return false;
    }
}