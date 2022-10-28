package ua.pkk.wetravel.fragments.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

import ua.pkk.wetravel.R;
import ua.pkk.wetravel.databinding.FragmentMain2Binding;
import ua.pkk.wetravel.fragments.allUserVideo.ShowVideoFragment;
import ua.pkk.wetravel.fragments.loadVideoMap.LoadVideoMapFragment;
import ua.pkk.wetravel.fragments.settings.SettingsFragment;
import ua.pkk.wetravel.fragments.showVideoOnMap.VideoMapsFragment;
import ua.pkk.wetravel.fragments.userAccount.UserAccountFragment;
import ua.pkk.wetravel.utils.Keys;
import ua.pkk.wetravel.utils.User;

public class MainFragmentNewDesign extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentMain2Binding binding;
    private MainFragmentViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main2, container, false);
        binding.bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        viewModel = new ViewModelProvider(this).get(MainFragmentViewModel.class);
        viewModel.load_user_img(getContext());

        switchToAccountTab();
        return binding.getRoot();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.accountTab:
                   switchToAccountTab();
                return true;
            case R.id.videoUploadTab:
                getParentFragmentManager().beginTransaction()
                        .replace(binding.mainFragmentsContainer.getId(), LoadVideoMapFragment.getInstance()).commit();
                return true;
            case  R.id.myVideoTab:
                getParentFragmentManager().beginTransaction()
                        .replace(binding.mainFragmentsContainer.getId(), ShowVideoFragment.getInstance()).commit();
                return true;
            case R.id.allVideoTab:
                getParentFragmentManager().beginTransaction()
                        .replace(binding.mainFragmentsContainer.getId(), VideoMapsFragment.getInstance()).commit();
                return true;
            case R.id.settingsTab:
                getParentFragmentManager().beginTransaction()
                        .replace(binding.mainFragmentsContainer.getId(), SettingsFragment.getInstance()).commit();
                return true;

        }
        return false;
    }

    private void switchToAccountTab() {
        String userName = User.getInstance().getName();
        String userInfo = User.getInstance().getInfo();
        String userStatus = User.getInstance().getStatus();
        File user_img = new File(getContext().getFilesDir(), "profile_img");
        String path = null;
        if (user_img.length() != 0) {
            path = user_img.getAbsolutePath();
        }
        getParentFragmentManager().beginTransaction()
                .replace(binding.mainFragmentsContainer.getId(), UserAccountFragment.getInstance(userName, userInfo, path ,userStatus,Keys.OWNER_ACCOUNT.getIntValue())).commit();

    }


}