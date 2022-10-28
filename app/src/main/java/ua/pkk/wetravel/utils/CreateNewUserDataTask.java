package ua.pkk.wetravel.utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.pkk.wetravel.retrofit.UserAPI;
import ua.pkk.wetravel.retrofit.UserData;

public class CreateNewUserDataTask implements Runnable{
    private final String id;
    private final UserData userData;

    public CreateNewUserDataTask(String id, UserData userData) {
        this.id = id;
        this.userData = userData;
    }

    @Override
    public void run() {
        UserAPI.INSTANCE.getRETROFIT_SERVICE().createNewUserData(id, userData).enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
            }
        });
    }
}
