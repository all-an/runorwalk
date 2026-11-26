package com.runorwalk.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.runorwalk.R;
import com.runorwalk.calc.PaceFormat;
import com.runorwalk.calc.ZoneTwo;

/** Screen where the user types the Zone 2 pace range (m:ss per km). */
public final class SettingsActivity extends Activity {
    private EditText fastField;
    private EditText slowField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        fastField = findViewById(R.id.fastPace);
        slowField = findViewById(R.id.slowPace);
        ZoneTwo zone = ZoneSettings.load(this);
        fastField.setText(PaceFormat.formatPace(zone.fastestSecPerKm()));
        slowField.setText(PaceFormat.formatPace(zone.slowestSecPerKm()));
        findViewById(R.id.saveButton).setOnClickListener(v -> save());
    }

    private void save() {
        try {
            ZoneTwo zone = ZoneTwo.parse(fastField.getText().toString(), slowField.getText().toString());
            ZoneSettings.save(this, zone);
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            finish();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
