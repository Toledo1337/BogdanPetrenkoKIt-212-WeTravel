package ua.pkk.wetravel.fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.File;

import ua.pkk.wetravel.R;
import ua.pkk.wetravel.databinding.FragmentMainBinding;
import ua.pkk.wetravel.utils.Keys;
import ua.pkk.wetravel.utils.User;

public class MainFragment extends Fragment {
    private FragmentMainBinding binding;
    private NavController navController;
    private MainFragmentViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        viewModel = new ViewModelProvider(this).get(MainFragmentViewModel.class);
        viewModel.load_user_img(getContext());

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        binding.loadVideo.setOnClickListener(v -> goToLoadVideoMap());
        binding.showMyVideo.setOnClickListener(v -> goToShowVideo());
        binding.settings.setOnClickListener(v -> goToSettings());
        binding.showMap.setOnClickListener(v -> goToShowMap());
        binding.myAccountBtn.setOnClickListener(v -> goToMyAccount());
        binding.button.setOnClickListener(v -> navController.navigate(MainFragmentDirections.actionMainFragmentToMainFragment2()));

        return binding.getRoot();
    }

    private void goToSettings() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToSettingsFragment());
    }

    private void goToMyAccount() {
        File user_img = new File(getContext().getFilesDir(), "profile_img");
        String path = null;
        if (user_img.length() != 0) {
            path = user_img.getAbsolutePath();
        }
        navController.navigate(MainFragmentDirections
                .actionMainFragmentToUserAccountFragment(path,
                        User.getInstance().getName(), User.getInstance().getInfo(), User.getInstance().getStatus(), Keys.OWNER_ACCOUNT.getIntValue()));
    }

    private void goToShowMap() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToVideoMapsFragment());
    }

    private void goToLoadVideoMap() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToLoadVideomapsFragment());
    }

    private void goToShowVideo() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToShowVideoFragment());
    }
}
