package ua.pkk.wetravel.fragments.loginPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ua.pkk.wetravel.R;
import ua.pkk.wetravel.databinding.FragmentLoginPageBinding;
import ua.pkk.wetravel.fragments.login.LoginFragment;
import ua.pkk.wetravel.fragments.register.RegisterFragment;


public class LoginPageFragment extends Fragment {
    private Fragment loginFragment;
    private Fragment registerFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction tr;
    private boolean check_fragment = true;
    private FragmentLoginPageBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login_page,container,false);

        binding.loginBtnLog.setOnClickListener(v -> onLogin());
        binding.registerBtnLog.setOnClickListener(v -> onRegister());



        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();
        getParentFragmentManager().beginTransaction().add(R.id.frame, loginFragment).commit();

        return binding.getRoot();
    }



    private void onLogin() {
        if (!check_fragment) {
            fragmentManager = getParentFragmentManager();
            tr = fragmentManager.beginTransaction();
            tr.setCustomAnimations(R.anim.go_from_left, R.anim.go_to_right);
            tr.replace(R.id.frame, loginFragment).commit();
            check_fragment = true;
        }
    }

    private void onRegister() {
        if (check_fragment) {
            fragmentManager = getParentFragmentManager();
            tr = fragmentManager.beginTransaction();
            tr.setCustomAnimations(R.anim.go_from_right, R.anim.go_to_left);
            tr.replace(R.id.frame, registerFragment).commit();
            check_fragment = false;
        }
    }
}