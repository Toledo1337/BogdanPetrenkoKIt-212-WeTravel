package ua.pkk.wetravel.fragments.editUserAccount;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ua.pkk.wetravel.R;
import ua.pkk.wetravel.databinding.FragmentEditUserAccountBinding;
import ua.pkk.wetravel.fragments.allUserVideo.ShowVideoFragment;
import ua.pkk.wetravel.fragments.userAccount.UserAccountFragment;
import ua.pkk.wetravel.utils.Keys;
import ua.pkk.wetravel.utils.User;

public class EditUserAccountFragment extends Fragment {
    private FragmentEditUserAccountBinding binding;
    private EditUserAccountFragmentViewModel viewModel;
    private final int IMAGE_FILE_REQUEST_CODE = 2;
    private String imgUri;

    private String userImg;
    private String userName;
    private String userInfo;
    private String userStatus;

    public EditUserAccountFragment(String userImg, String userName, String userInfo, String userStatus) {
        this.userImg = userImg;
        this.userName = userName;
        this.userInfo = userInfo;
        this.userStatus = userStatus;
    }

    public static EditUserAccountFragment getInstance(String userImg, String userName, String userInfo, String userStatus) {
        return new EditUserAccountFragment(userImg, userName, userInfo, userStatus);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_user_account, container, false);
        viewModel = new ViewModelProvider(this).get(EditUserAccountFragmentViewModel.class);

        if (getArguments() != null) {
            EditUserAccountFragmentArgs args = EditUserAccountFragmentArgs.fromBundle(getArguments());
            userImg = args.getUserImg();
            userName = args.getUserName();
            userInfo = args.getUserInfo();
            userStatus = args.getStatus();
        }
        initUI(userImg, userName, userInfo, userStatus);

        binding.saveBtn.setOnClickListener(this::onSave);
        binding.cancelBtn.setOnClickListener(this::onCancel);
        binding.changePhotoBtn.setOnClickListener(this::onImgChange);
        return binding.getRoot();
    }

    private void initUI(String userImg, String userName, String userInfo, String userStatus) {
        if (userImg != null) {
            binding.userImg.setImageBitmap(BitmapFactory.decodeFile(userImg));
            imgUri = userImg;
        } else {
            imgUri = null;
        }

        binding.editedUserAbout.setText(userInfo);
        binding.editedUserName.setText(userName);
        binding.editedUserStatus.setText(userStatus);

    }

    public void onImgChange(View v) {
        startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), "Choose image"), IMAGE_FILE_REQUEST_CODE);
    }

    public void onSave(View v) {
        String name = binding.editedUserName.getText().toString();
        String about_me = binding.editedUserAbout.getText().toString();
        String status = binding.editedUserStatus.getText().toString();

        viewModel.uploadUserData(name, about_me, status);

        if (Keys.isNewDesign())
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_fragments_container, UserAccountFragment.getInstance(name,about_me, imgUri, status, Keys.OWNER_ACCOUNT.getIntValue())).commit();
        else
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(EditUserAccountFragmentDirections.actionEditUserAccountFragmentToUserAccountFragment(imgUri, name, about_me, status, Keys.OWNER_ACCOUNT.getIntValue()));
    }

    public void onCancel(View v) {
        if (!Keys.isNewDesign()){
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                    .navigate(EditUserAccountFragmentDirections.actionEditUserAccountFragmentToUserAccountFragment(imgUri, userName, userInfo, userStatus, Keys.OWNER_ACCOUNT.getIntValue()));
        } else {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_fragments_container, UserAccountFragment.getInstance(userName,userInfo,null, userStatus, Keys.OWNER_ACCOUNT.getIntValue())).commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != 2 || data == null) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        binding.userImg.setImageURI(data.getData());
        File user_img = new File(getContext().getFilesDir(), "profile_img");
        imgUri = user_img.getAbsolutePath();
        StorageReference reference = FirebaseStorage.getInstance().getReference().child(User.getInstance().getId()).child("profile_img");

        new Thread(() -> {
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, stream);

                try (OutputStream outputStream = new FileOutputStream(user_img.getAbsolutePath())) {
                    stream.writeTo(outputStream);
                }

                byte[] output = stream.toByteArray();
                reference.putBytes(output); //TODO addOnSuccessListener
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
