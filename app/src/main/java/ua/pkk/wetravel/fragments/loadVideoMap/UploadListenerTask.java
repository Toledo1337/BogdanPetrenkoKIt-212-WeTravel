package ua.pkk.wetravel.fragments.loadVideoMap;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ua.pkk.wetravel.R;
import ua.pkk.wetravel.utils.User;

public class UploadListenerTask implements Runnable {
    private final Context context;
    private final NotificationCompat.Builder builder;
    private final NotificationManagerCompat notificationManager;
    private final LatLng marker;
    private final Intent data;
    private final int NOTIFICATION_ID;
    private final String name;

    public UploadListenerTask(Context context, NotificationCompat.Builder builder, NotificationManagerCompat notificationManager, LatLng marker, Intent data, int NOTIFICATION_ID, String name) {
        this.context = context;
        this.builder = builder;
        this.notificationManager = notificationManager;
        this.marker = marker;
        this.data = data;
        this.NOTIFICATION_ID = NOTIFICATION_ID;
        this.name = name;
    }

    @Override
    public void run() {
        builder.setProgress(100, 0, false);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(User.getInstance().getId()).child(name);
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        StorageMetadata.Builder metadata = new StorageMetadata.Builder()
                .setCustomMetadata("position", marker.latitude + "/" + marker.longitude);
        metadata.setCustomMetadata("uploadingTime", formatter.format(date));
        metadata.setCustomMetadata("user_id", User.getInstance().getId());

        UploadTask uploadTask = storageReference.putFile(data.getData(), metadata.build());
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                if (snapshot.getBytesTransferred() == snapshot.getTotalByteCount()) {
                    builder.setProgress(0, 0, false);
                    builder.setContentText(context.getString(R.string.upload_complete));
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                    return;
                }
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                builder.setProgress(100, (int) progress, false);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        });
    }
}
