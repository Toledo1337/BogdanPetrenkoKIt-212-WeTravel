package ua.pkk.wetravel.fragments.loadVideoMap;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.pkk.wetravel.R;
import ua.pkk.wetravel.databinding.FragmentLoadVideoMapsBinding;
import ua.pkk.wetravel.retrofit.UserAPI;
import ua.pkk.wetravel.utils.Keys;
import ua.pkk.wetravel.utils.TaskExecutor;
import ua.pkk.wetravel.utils.User;
import ua.pkk.wetravel.utils.Video;

//TODO Set focus on marker if cancel  Priority -> LOW
public class LoadVideoMapFragment extends Fragment {
    private FragmentLoadVideoMapsBinding binding;
    private LatLng marker;
    public int VIDEO_FILE_REQUEST_CODE = 1;
    private LoadVideoMapViewModel viewModel;
    private static LoadVideoMapFragment fragment;

    public static LoadVideoMapFragment getInstance(){
        if (fragment == null){
            fragment = new LoadVideoMapFragment();
        }
        return fragment;
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        private GoogleMap map;

        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(50.33, 30.53)));
            googleMap.setOnMapLongClickListener(this::onMapLongClick);
            map = googleMap;
        }

        public void onMapLongClick(LatLng latLng) {
            marker = latLng;
            map.clear();
            map.addMarker(new MarkerOptions().position(latLng));
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    };

    private void checkMapPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
    }

    private void checkVideoPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_load_video_maps, container, false);
        binding.addVideoFab.setOnClickListener(this::onAddVideoFab);
        binding.selectVideoBtn.setOnClickListener(this::onSelectVideo);
        viewModel = new ViewModelProvider(this).get(LoadVideoMapViewModel.class);

        checkMapPermissions();
        checkVideoPermissions();
        initAddVideoLayout();
        checkIsVideoLayoutWasShow();

        return binding.getRoot();
    }

    private void checkIsVideoLayoutWasShow() {
        if (viewModel.is_add_video_show) {
            ViewGroup.LayoutParams layoutParams = binding.addVideoLayout.getLayoutParams();
            layoutParams.height = binding.loadVideoMapLayout.getLayoutParams().height;
            binding.addVideoLayout.setLayoutParams(layoutParams);
            binding.TEST.animate().scaleX(30).scaleY(30);
            binding.addVideoFab.animate().scaleX(0).scaleY(0);
            binding.addVideoLayout.animate().y(0);
        }
    }

    private void showNotification(Intent data, LatLng marker, String name, String description, String tags) {
        String CHANNEL_ID = "SuccessUpload";
        int NOTIFICATION_ID = new Random().nextInt(256);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID);
        builder.setContentTitle(getString(R.string.video_upload))
                .setContentText(getString(R.string.upload_in_process))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setOnlyAlertOnce(true);

        createNotificationChannel(CHANNEL_ID);

        UploadListenerTask uploadListenerTask = new UploadListenerTask(getContext(),builder,notificationManager,marker,data,NOTIFICATION_ID,name);
        UploadVideoDataTask uploadVideoDataTask = new UploadVideoDataTask(description,tags,name);
        TaskExecutor.execute(uploadListenerTask);
        TaskExecutor.execute(uploadVideoDataTask);

        //Navigate to MainFragment
        if (!Keys.isNewDesign()){
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(LoadVideoMapFragmentDirections.actionLoadVideoMapsFragmentToMainFragment());
        } else {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_fragments_container, LoadVideoMapFragment.getInstance()).commit();
            binding.cancelBtn.performClick();
        }
    }

    private void createNotificationChannel(String CHANNEL_ID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_chanel_name);
            String description = getString(R.string.notification_chanel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initAddVideoLayout() {
        binding.cancelBtn.setOnClickListener(v -> {
            hideKeyboardFrom(getContext(), binding.videoName);
            viewModel.is_add_video_show = false;
            binding.TEST.animate().scaleX(0).scaleY(0).setDuration(800);
            binding.addVideoLayout.animate().y(binding.loadVideoMapLayout.getHeight() * 2).setDuration(1300);
            binding.addVideoFab.setVisibility(View.VISIBLE);
            binding.addVideoFab.animate().scaleX(1).scaleY(1).setDuration(800);
            binding.videoName.setText("");
            binding.videoTags.setText("");
            binding.videoDescription.setText("");
        });
        binding.addVideoFab.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (viewModel.is_add_video_show) binding.addVideoFab.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        binding.videoDescription.setOnClickListener(v -> {
            Boolean.toString(v.requestFocus());
        });
        binding.videoTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > before && s.charAt(before) == '#')
                    binding.videoTags.setSelection(s.length());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0 && s.charAt(s.length() - 1) == ' ')
                    binding.videoTags.setText(binding.videoTags.getText().toString() + "#");
            }
        });

        KeyboardVisibilityEvent.setEventListener(getActivity(), b -> {
            if (b) {
                binding.cancelBtn.setVisibility(View.GONE);
                binding.selectVideoBtn.setVisibility(View.GONE);
            } else {
                binding.cancelBtn.setVisibility(View.VISIBLE);
                binding.selectVideoBtn.setVisibility(View.VISIBLE);
            }
        });

    }

    private void onAddVideoFab(View view) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), getContext().getString(R.string.storage_permission), Toast.LENGTH_LONG).show();
            checkVideoPermissions();
            return;
        }
        if (marker == null) {
            Toast.makeText(getContext(), getContext().getText(R.string.addMarker), Toast.LENGTH_LONG).show();
            return;
        }
        if (!viewModel.is_add_video_show) {
            ViewGroup.LayoutParams layoutParams = binding.addVideoLayout.getLayoutParams();
            layoutParams.height = binding.loadVideoMapLayout.getLayoutParams().height;
            binding.addVideoLayout.setLayoutParams(layoutParams);
            viewModel.is_add_video_show = true;
            binding.TEST.animate().scaleX(30).scaleY(30).setDuration(800);
            binding.addVideoFab.animate().scaleX(0).scaleY(0).setDuration(800);
            binding.addVideoLayout.animate().y(0).setDuration(700);
        }
    }

    public void onSelectVideo(View v) {
        viewModel.checkNames(binding.videoName.getText().toString());
        viewModel.hasSameNames.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.videoName.setError(getContext().getString(R.string.enter_another_name));
                } else {
                    startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT).setType("video/*"), "Choose Video"), VIDEO_FILE_REQUEST_CODE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != 1 || marker == null) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        showNotification(data, marker, binding.videoName.getText().toString(), binding.videoDescription.getText().toString(), binding.videoTags.getText().toString());
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(callback);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
