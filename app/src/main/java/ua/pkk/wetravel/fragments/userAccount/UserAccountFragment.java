package ua.pkk.wetravel.fragments.userAccount;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.firebase.storage.FirebaseStorage;

import java.io.File;

import ua.pkk.wetravel.R;
import ua.pkk.wetravel.databinding.FragmentUserAccountBinding;
import ua.pkk.wetravel.fragments.editUserAccount.EditUserAccountFragment;
import ua.pkk.wetravel.utils.Keys;
import ua.pkk.wetravel.utils.User;


public class UserAccountFragment extends Fragment {
    private FragmentUserAccountBinding binding;
    private UserAccountViewModel viewModel;
    private Handler userInfoHandler;
    private boolean is_data_ready;
    private String userName;
    private String userInfo;
    private String userImg;
    private String status;
    private int sourceKey;

    public UserAccountFragment(String userName, String userInfo, String userImg, String status, int sourceKey) {
        this.userImg = userImg;
        this.status = status;
        this.sourceKey = sourceKey;
        this.userName = userName;
        this.userInfo = userInfo;
    }

    public static UserAccountFragment getInstance(String userName, String userInfo, String userImg, String status, int sourceKey) {
        return new UserAccountFragment(userName, userInfo, userImg, status, sourceKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_account, container, false);
        viewModel = new ViewModelProvider(this).get(UserAccountViewModel.class);

        viewModel.imgLoadComplete.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.userImg.setImageBitmap(BitmapFactory.decodeFile(s));
            }
        });

        is_data_ready = false;
        userInfoHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                binding.userName.setText(User.getInstance().getName());
                binding.aboutUser.setText(User.getInstance().getInfo());
                binding.userStatusTv.setText(User.getInstance().getStatus());
                is_data_ready = true;
            }
        };
        if (getArguments() != null) {
            UserAccountFragmentArgs accountFragmentArgs = UserAccountFragmentArgs.fromBundle(getArguments());
            initUI(accountFragmentArgs.getUserName(), accountFragmentArgs.getUserInfo(), accountFragmentArgs.getUserImg(), accountFragmentArgs.getStatus(), accountFragmentArgs.getSourceKey());
        } else {
            initUI(userName, userInfo, userImg, status, sourceKey);
        }
        return binding.getRoot();
    }

    private void initUI(String userName, String userInfo, String userImg, String status, int sourceKey) {
        if (userImg != null) {
            binding.userImg.setImageBitmap(BitmapFactory.decodeFile(userImg));
        } else {
           viewModel.initUserImg(getContext());
        }
        if (status != null) {
            is_data_ready = true;
            binding.userName.setText(userName);
            binding.aboutUser.setText(userInfo);
            binding.userStatusTv.setText(status);
        } else {  //possibly only for owner account
            new Thread(() -> {
                //Wait for info
                while (User.getInstance().getStatus() == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                userInfoHandler.sendEmptyMessage(1);
            }).start();
        }
        if (sourceKey == Keys.OWNER_ACCOUNT.getIntValue()) {
            binding.editBtn.setOnClickListener(v -> {
                        //TODO Remove old pattern
                        if (is_data_ready && !Keys.isNewDesign()) {
                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(
                                    UserAccountFragmentDirections.actionUserAccountFragmentToEditUserAccountFragment(
                                            userImg,
                                            User.getInstance().getName(),
                                            User.getInstance().getInfo(),
                                            User.getInstance().getStatus()
                                    )
                            );
                        } else {
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.main_fragments_container, EditUserAccountFragment.getInstance(userImg, userName, userInfo, status)).commit();
                        }
                    }
            );
        } else {
            binding.editBtn.setVisibility(View.GONE);
        }
    }
}