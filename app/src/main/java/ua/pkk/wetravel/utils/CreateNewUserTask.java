package ua.pkk.wetravel.utils;

import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.pkk.wetravel.retrofit.UserAPI;
import ua.pkk.wetravel.retrofit.UserProperty;

public class CreateNewUserTask implements Runnable {
    private MutableLiveData<Boolean> _isSuccessRegister;
    private final UserProperty userProperty;
    private final String id;

    public CreateNewUserTask(UserProperty userProperty, String id) {
        this.userProperty = userProperty;
        this.id = id;
    }

    public CreateNewUserTask(MutableLiveData<Boolean> _isSuccessRegister, UserProperty userProperty, String id) {
        this._isSuccessRegister = _isSuccessRegister;
        this.userProperty = userProperty;
        this.id = id;
    }

    @Override
    public void run() {
        UserAPI.INSTANCE.getRETROFIT_SERVICE().createNewUser(id,userProperty).enqueue(new Callback<UserProperty>() {
            @Override
            public void onResponse(Call<UserProperty> call, Response<UserProperty> response) {
                if (_isSuccessRegister != null){
                    _isSuccessRegister.setValue(response.isSuccessful());
                }
            }

            @Override
            public void onFailure(Call<UserProperty> call, Throwable t) {
                if (_isSuccessRegister != null){
                    _isSuccessRegister.setValue(false);
                }
            }
        });
    }

}
