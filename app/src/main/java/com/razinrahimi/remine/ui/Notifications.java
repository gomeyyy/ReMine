package com.razinrahimi.remine.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.razinrahimi.remine.R;

public class Notifications extends AppCompatActivity {
    Button button;
    private static final String CHANNEL_ID = "reminder_channel";
    private static final int NOTIFICATION_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Create notification channel (Required for Android 8.0+)
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }


        button = findViewById(R.id.click);
        button.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                            NOTIFICATION_PERMISSION_CODE);
                } else {
                    showNotification();
                }
            } else {
                showNotification();
            }
        });
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon) // Ensure 'icon.png' is in 'res/drawable'
                .setContentTitle("ALERT!")
                .setContentText("Your data is successfully cleared :)")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(0, builder.build());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showNotification();
            }
        }
    }
}
