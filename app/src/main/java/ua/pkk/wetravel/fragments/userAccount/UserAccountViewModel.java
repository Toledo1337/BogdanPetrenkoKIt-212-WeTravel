package ua.pkk.wetravel.fragments.userAccount;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.storage.FirebaseStorage;

import java.io.File;

import ua.pkk.wetravel.utils.User;

public class UserAccountViewModel extends ViewModel {
    private final MutableLiveData<String> _imgLoadComplete = new MutableLiveData<>();
    public LiveData<String> imgLoadComplete = _imgLoadComplete;

    public void initUserImg(Context context){
        File userImg = new File(context.getFilesDir(), "profile_img");
        if (userImg.length() == 0){
            FirebaseStorage.getInstance().getReference().child(User.getInstance().getId()).child("profile_img").getFile(userImg).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    _imgLoadComplete.setValue(userImg.getAbsolutePath());
                }
            });
        } else {
            _imgLoadComplete.setValue(userImg.getAbsolutePath());
        }
    }

}
