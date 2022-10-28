package ua.pkk.wetravel.fragments.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.io.File;

import ua.pkk.wetravel.R;
import ua.pkk.wetravel.fragments.main.MainFragmentNewDesignDirections;
import ua.pkk.wetravel.utils.Keys;
import ua.pkk.wetravel.utils.User;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SettingsViewModel viewModel;
    private static SettingsFragment fragment;

    public static SettingsFragment getInstance(){
        if (fragment == null){
            fragment = new SettingsFragment();
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        Preference exit = findPreference("exit");
        exit.setOnPreferenceClickListener(preference -> {
            showExitDialog();
            return true;
        });

        Preference changePassword = findPreference("change_password");
        changePassword.setOnPreferenceClickListener(preference -> {
            showChangePasswordDialog();
            return true;
        });
    }

    private void showChangePasswordDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText password = dialogView.findViewById(R.id.password);
        EditText newPassword = dialogView.findViewById(R.id.new_password);
        EditText repeatNewPassword = dialogView.findViewById(R.id.repeat_new_password);

        Button submit = dialogView.findViewById(R.id.submit_btn);
        Button cancel = dialogView.findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(v -> dialog.dismiss());
        submit.setOnClickListener(v -> {
            viewModel.checkPassword(password.getText().toString());
            viewModel.isPasswordCorrect.observe(getViewLifecycleOwner(), isCorrect -> {
                if (isCorrect) {
                    if (newPassword.getText().toString().equals(repeatNewPassword.getText().toString())) {
                        viewModel.changePassword(newPassword.getText().toString());
                        dialog.dismiss();
                    } else {
                        newPassword.setError("passwords are not equal");
                    }
                } else {
                    password.setError("password isn`t correct");
                }
            });

        });
        dialog.show();
    }

    private void showExitDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme)
                .setTitle("Are you sure?")
                .setNegativeButton("No", (dialog1, which) -> dialog1.dismiss())
                .setPositiveButton("Yes", (dialog1, which) -> onLogOut()).create();

        dialog.show();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    private void onLogOut() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userID", "");
        editor.apply();
        File user_img = new File(getContext().getFilesDir(), "profile_img");
        user_img.delete();
        User.getInstance().cleanData();
        if(!Keys.isNewDesign()){
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(SettingsFragmentDirections.actionSettingsFragmentToLoginPageFragment());
        } else {
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(MainFragmentNewDesignDirections.actionMainFragment2ToLoginPageFragment());
        }
    }
}