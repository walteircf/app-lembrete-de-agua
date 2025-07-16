package com.example.lembretedeagua;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

/*
================================================================================
NOTA IMPORTANTE:
Este arquivo Java contém a lógica principal para uma versão Android do aplicativo.
Para um projeto funcional, você precisaria também dos seguintes arquivos:

1. `activity_main.xml`: O arquivo de layout para esta atividade.
   - Conteria elementos como `TextClock`, `TextView`, `ProgressBar` e `Button`.
   - A `ProgressBar` seria estilizada para ser vertical e representar o nível da água.

2. `activity_setup.xml`: O layout para a tela de configuração inicial.
   - Conteria `EditText` para idade e peso e um `Button` para calcular.

3. `SetupActivity.java`: A classe Java para a tela de configuração.
   - Leria os valores, calcularia a meta e a salvaria em `SharedPreferences`.

4. `AndroidManifest.xml`: O manifesto do aplicativo.
   - Declararia as atividades, o `BroadcastReceiver` e as permissões necessárias
     (ex: POST_NOTIFICATIONS, SCHEDULE_EXACT_ALARM).

O código abaixo está comentado para mostrar como esses elementos se conectam.
================================================================================
*/

public class MainActivity extends AppCompatActivity {

    // --- Constantes ---
    private static final String PREFS_NAME = "WaterReminderPrefs";
    private static final String KEY_GOAL = "glassesGoal";
    private static final int GLASS_SIZE_ML = 220;
    private static final int TOTAL_DOSES_PER_BOTTLE = 3;
    private static final long REMINDER_INTERVAL = AlarmManager.INTERVAL_HOUR;
    private static final int INTERVAL_REMINDER_ID = 100;
    private static final int CUSTOM_REMINDER_ID = 101;
    private static final String NOTIFICATION_CHANNEL_ID = "water_reminder_channel";

    // --- Elementos da UI (definidos em activity_main.xml) ---
    private TextView goalTotalText;
    private TextView goalText;
    private TextView bottleStatusText;
    private TextView messageText;
    private ProgressBar waterLevelProgress;
    private Button drinkButton;
    private Button drinkBottleButton;
    private Button resetButton;
    private Button setReminderButton;
    private TextClock textClock;

    // --- Estado do Aplicativo ---
    private int glassesGoal;
    private int glassesDrunk = 0;
    private int dosesInBottle = TOTAL_DOSES_PER_BOTTLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // O layout é inflado a partir do arquivo res/layout/activity_main.xml
        setContentView(R.layout.activity_main);

        // Carrega a meta salva ou uma padrão
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        glassesGoal = prefs.getInt(KEY_GOAL, 8); // Meta padrão de 8 copos

        // Inicializa os elementos da UI
        setupUI();

        // Configura os listeners de clique para os botões
        setupClickListeners();

        // Cria o canal de notificação (necessário para Android 8.0+)
        createNotificationChannel();

        // Solicita permissão para notificações (necessário para Android 13+)
        requestNotificationPermission();

        // Configura o estado inicial da UI e o primeiro lembrete
        resetState();
    }

    private void setupUI() {
        // Conecta as variáveis aos IDs definidos no XML
        goalTotalText = findViewById(R.id.goal_total_text); // Ex: <TextView android:id="@+id/goal_total_text" ... />
        goalText = findViewById(R.id.goal_text);
        bottleStatusText = findViewById(R.id.bottle_status_text);
        messageText = findViewById(R.id.message_text);
        waterLevelProgress = findViewById(R.id.water_level_progress); // Ex: <ProgressBar android:id="@+id/water_level_progress" style="?android:attr/progressBarStyleHorizontal" ... />
        drinkButton = findViewById(R.id.drink_button);
        drinkBottleButton = findViewById(R.id.drink_bottle_button);
        resetButton = findViewById(R.id.reset_button);
        setReminderButton = findViewById(R.id.set_reminder_button);
        textClock = findViewById(R.id.text_clock);

        // Configura a ProgressBar para agir como o nível da garrafa
        waterLevelProgress.setMax(TOTAL_DOSES_PER_BOTTLE);
    }

    private void setupClickListeners() {
        drinkButton.setOnClickListener(v -> handleDrink());
        drinkBottleButton.setOnClickListener(v -> handleDrinkBottle());
        resetButton.setOnClickListener(v -> resetState());
        setReminderButton.setOnClickListener(v -> openTimePicker());
    }

    private void updateUI() {
        // Atualiza todos os textos
        goalText.setText(String.valueOf(glassesDrunk));
        goalTotalText.setText(String.format("Meta diária: %d copos", glassesGoal));
        bottleStatusText.setText(String.format("%d / %d na garrafa", dosesInBottle, TOTAL_DOSES_PER_BOTTLE));
        waterLevelProgress.setProgress(dosesInBottle);

        // Lógica para mensagens e estado dos botões
        if (glassesDrunk >= glassesGoal) {
            messageText.setText("Parabéns! Você atingiu sua meta! 🎉");
            drinkButton.setEnabled(false);
            drinkBottleButton.setEnabled(false);
            cancelIntervalReminder();
        } else {
            drinkButton.setEnabled(true);
            drinkBottleButton.setEnabled(dosesInBottle > 0);
            messageText.setText(dosesInBottle == 0 ? "Garrafa vazia! Beba para encher." : "Ótimo! Continue assim.");
        }
    }

    private void handleDrink() {
        if (glassesDrunk >= glassesGoal) return;

        if (dosesInBottle == 0) {
            dosesInBottle = TOTAL_DOSES_PER_BOTTLE; // Enche a garrafa
        }

        glassesDrunk++;
        dosesInBottle--;
        updateUI();
    }

    private void handleDrinkBottle() {
        if (dosesInBottle == 0 || glassesDrunk >= glassesGoal) return;

        glassesDrunk += dosesInBottle;
        if (glassesDrunk > glassesGoal) {
            glassesDrunk = glassesGoal; // Evita ultrapassar a meta
        }
        dosesInBottle = 0;
        updateUI();
    }

    private void resetState() {
        glassesDrunk = 0;
        dosesInBottle = TOTAL_DOSES_PER_BOTTLE;
        messageText.setText("Clique no botão para registrar um copo.");
        updateUI();
        startIntervalReminder();
    }

    // --- Lógica de Notificação e Lembretes ---

    private void openTimePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            // Se o horário já passou hoje, agenda para o dia seguinte
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            setCustomReminder(calendar.getTimeInMillis());
            String time = String.format("%02d:%02d", hourOfDay, minute);
            Toast.makeText(this, "Lembrete definido para " + time, Toast.LENGTH_SHORT).show();

        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void setCustomReminder(long timeInMillis) {
        setAlarm(timeInMillis, CUSTOM_REMINDER_ID, false);
    }

    private void startIntervalReminder() {
        // Agenda um alarme que se repete a cada hora
        long firstTrigger = System.currentTimeMillis() + REMINDER_INTERVAL;
        setAlarm(firstTrigger, INTERVAL_REMINDER_ID, true);
    }

    private void cancelIntervalReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, INTERVAL_REMINDER_ID, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void setAlarm(long triggerTime, int requestCode, boolean isRepeating) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("requestCode", requestCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        if (isRepeating) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime, REMINDER_INTERVAL, pendingIntent);
        } else {
            // Usa setExact para garantir que o alarme toque na hora certa
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                // Lidar com a falta de permissão para alarmes exatos
                Toast.makeText(this, "Permissão para alarmes exatos não concedida.", Toast.LENGTH_SHORT).show();
                return;
            }
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Lembretes de Água";
            String description = "Canal para notificações de lembrete para beber água.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    /**
     * BroadcastReceiver para lidar com os alarmes e mostrar as notificações.
     * Este Receiver deve ser declarado no AndroidManifest.xml.
     * <receiver android:name=".MainActivity$NotificationReceiver" android:enabled="true" />
     */
    public static class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_water_drop) // Ícone deve existir em res/drawable
                    .setContentTitle("Hora de Beber Água!")
                    .setContentText("Lembre-se de se manter hidratado.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            // Usa um ID de notificação único para não sobrescrever outras
            int notificationId = intent.getIntExtra("requestCode", (int) System.currentTimeMillis());
            notificationManager.notify(notificationId, builder.build());
        }
    }
}
