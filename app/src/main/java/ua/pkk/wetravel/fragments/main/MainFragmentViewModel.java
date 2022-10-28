package ua.pkk.wetravel.fragments.main;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import ua.pkk.wetravel.utils.TaskExecutor;

public class MainFragmentViewModel extends ViewModel {

    public void load_user_img(Context context) {
        InitUserDataTask initUserDataTask = new InitUserDataTask(context);
        TaskExecutor.execute(initUserDataTask);
    }
}
