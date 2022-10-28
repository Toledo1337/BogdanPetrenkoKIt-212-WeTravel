package ua.pkk.wetravel.fragments.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.pkk.wetravel.retrofit.UserAPI;
import ua.pkk.wetravel.retrofit.UserProperty;
import ua.pkk.wetravel.utils.User;

public class LoginFragmentViewModel extends ViewModel {
    private final MutableLiveData<Boolean> _eventIsLogin = new MutableLiveData<>(false);
    public LiveData<Boolean> eventIsLogin = _eventIsLogin;

    private final MutableLiveData<Boolean> _wrongPasswordKey = new MutableLiveData<>(false);
    public LiveData<Boolean> wrongPasswordKey = _wrongPasswordKey;

    private final MutableLiveData<Boolean> _wrongMailKey = new MutableLiveData<>(false);
    public LiveData<Boolean> wrongMailKey = _wrongMailKey;

    public void sign_in(final String email, final String password) {
        UserAPI.INSTANCE.getRETROFIT_SERVICE().getAllUsers().enqueue(new Callback<Map<String, UserProperty>>() {
            @Override
            public void onResponse(Call<Map<String, UserProperty>> call, Response<Map<String, UserProperty>> response) {
                if (response.isSuccessful()) {
                    UserProperty buf = new UserProperty(email, password);
                    if (response.body().containsValue(buf)) {
                        String id = userId(response.body(), buf);
                        if (!response.body().get(id).getPassword().equals(password)) {
                            _wrongMailKey.setValue(true);
                            return;
                        }
                        User.getInstance().setId(id);
                        _eventIsLogin.setValue(true);
                    } else _wrongPasswordKey.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<Map<String, UserProperty>> call, Throwable t) {
                //TODO maybe Toast or something else || write log
            }
        });
    }


    private String userId(Map<String, UserProperty> map, UserProperty value) {
        for (Map.Entry<String, UserProperty> i : map.entrySet()) {
            if (value.equals(i.getValue())) {
                return i.getKey();
            }
        }
        return null;
    }

    public void renewKey() {
        _wrongPasswordKey.setValue(false);
        _wrongMailKey.setValue(false);
    }

    public boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
