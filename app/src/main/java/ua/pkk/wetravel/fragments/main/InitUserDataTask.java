package ua.pkk.wetravel.fragments.main;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.pkk.wetravel.retrofit.UserAPI;
import ua.pkk.wetravel.retrofit.UserData;
import ua.pkk.wetravel.utils.User;

public class InitUserDataTask implements Runnable{

    private final Context context;

    public InitUserDataTask(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        FirebaseStorage.getInstance().getReference().child(User.getInstance().getId()).child("profile_img").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    User.getInstance().setImgUri(task.getResult().toString());
                }
            }
        });

        UserAPI.INSTANCE.getRETROFIT_SERVICE().getUserData(User.getInstance().getId()).enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                User.getInstance().setName(response.body().getUserName());
                User.getInstance().setInfo(response.body().getUserInfo());
                User.getInstance().setStatus(response.body().getStatus());
                Log.d("TAG",response.body().getStatus());
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {

            }
        });
    }
}
