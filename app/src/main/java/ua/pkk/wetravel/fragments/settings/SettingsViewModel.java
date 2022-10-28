package ua.pkk.wetravel.fragments.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.pkk.wetravel.utils.CreateNewUserTask;
import ua.pkk.wetravel.retrofit.UserAPI;
import ua.pkk.wetravel.retrofit.UserProperty;
import ua.pkk.wetravel.utils.User;

public class SettingsViewModel extends ViewModel {
    private MutableLiveData<Boolean> _isPasswordCorrect = new MutableLiveData<>();
    public LiveData<Boolean> isPasswordCorrect = _isPasswordCorrect;

    private UserProperty userProperty;

    public boolean checkPassword(String password) {
        new Thread(() -> {
            UserAPI.INSTANCE.getRETROFIT_SERVICE().getUser(User.getInstance().getId()).enqueue(new Callback<UserProperty>() {
                @Override
                public void onResponse(Call<UserProperty> call, Response<UserProperty> response) {
                    if (response.isSuccessful()) {
                        _isPasswordCorrect.setValue(password.equals(response.body().getPassword()));
                        userProperty = response.body();
                    }
                }

                @Override
                public void onFailure(Call<UserProperty> call, Throwable t) {

                }
            });
        }).start();

        return false;
    }

    public void changePassword(String password) {
        userProperty.setPassword(password);
        CreateNewUserTask createNewUserTask = new CreateNewUserTask(userProperty,User.getInstance().getId());
        new Thread(createNewUserTask).start();
    }
}
