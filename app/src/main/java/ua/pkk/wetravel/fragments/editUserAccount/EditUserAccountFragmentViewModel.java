package ua.pkk.wetravel.fragments.editUserAccount;

import androidx.lifecycle.ViewModel;

import ua.pkk.wetravel.utils.CreateNewUserDataTask;
import ua.pkk.wetravel.retrofit.UserData;
import ua.pkk.wetravel.utils.TaskExecutor;
import ua.pkk.wetravel.utils.User;

public class EditUserAccountFragmentViewModel extends ViewModel {
    public void uploadUserData(String name, String info, String status) {
        UserData userData = new UserData(name, info, status);
        CreateNewUserDataTask createNewUserDataTask = new CreateNewUserDataTask(User.getInstance().getId(), userData);
        TaskExecutor.execute(createNewUserDataTask);
    }
}
