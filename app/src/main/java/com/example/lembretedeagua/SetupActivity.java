package com.example.lembretedeagua;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SetupActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "WaterReminderPrefs";
    private static final String KEY_GOAL = "glassesGoal";
    private static final String KEY_SETUP_COMPLETE = "setupComplete";
    private static final int GLASS_SIZE_ML = 220;

    private EditText ageInput;
    private EditText weightInput;
    private Button calculateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verifica se a configuração já foi feita antes
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(KEY_SETUP_COMPLETE, false)) {
            startMainActivity();
            return; // Pula o resto do método
        }

        setContentView(R.layout.activity_setup);

        ageInput = findViewById(R.id.age_input);
        weightInput = findViewById(R.id.weight_input);
        calculateButton = findViewById(R.id.calculate_button);

        calculateButton.setOnClickListener(v -> handleCalculation());
    }

    private void handleCalculation() {
        String ageStr = ageInput.getText().toString();
        String weightStr = weightInput.getText().toString();

        if (ageStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            double weight = Double.parseDouble(weightStr);

            if (age <= 0 || weight <= 0) {
                Toast.makeText(this, "Por favor, insira valores válidos.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cálculo: 35ml por kg de peso
            double totalMl = weight * 35;
            int glassesGoal = (int) Math.round(totalMl / GLASS_SIZE_ML);

            // Salva a meta e marca a configuração como completa
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putInt(KEY_GOAL, glassesGoal);
            editor.putBoolean(KEY_SETUP_COMPLETE, true);
            editor.apply();

            // Inicia a tela principal
            startMainActivity();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Erro ao ler os valores. Verifique os números.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(SetupActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Fecha a tela de setup para que o usuário não possa voltar a ela
    }
}