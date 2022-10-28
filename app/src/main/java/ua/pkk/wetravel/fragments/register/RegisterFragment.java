package ua.pkk.wetravel.fragments.register;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import ua.pkk.wetravel.R;
import ua.pkk.wetravel.databinding.FragmentRegistrationBinding;
import ua.pkk.wetravel.fragments.loginPage.LoginPageFragmentDirections;

public class RegisterFragment extends Fragment {
    private FragmentRegistrationBinding binding;
    private RegisterFragmentViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registration, container, false);
        viewModel = new ViewModelProvider(this).get(RegisterFragmentViewModel.class);
        binding.acceptBtnReg.setOnClickListener(v -> onAccept());

        viewModel.isSuccessRegister.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                successRegistration();
            else
                Toast.makeText(getContext(), getString(R.string.error_try_again),Toast.LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }

    private void onAccept() {
        if (!binding.passwEdReg.getText().toString().equals(binding.passwAgainEdReg.getText().toString())) {
            binding.passwEdReg.setError(getContext().getResources().getString(R.string.password_dont_match));
            binding.passwAgainEdReg.setError(getContext().getResources().getString(R.string.password_dont_match));
            return;
        }
        viewModel.create_account(binding.mailEdReg.getText().toString(),
                binding.passwEdReg.getText().toString());

    }

    private void successRegistration() {
        Toast.makeText(this.getContext(), getString(R.string.success_registration), Toast.LENGTH_SHORT).show();
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(LoginPageFragmentDirections.actionLoginPageFragmentSelf());
        for (Fragment fragment :  getParentFragmentManager().getFragments()) {
            getParentFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

}
