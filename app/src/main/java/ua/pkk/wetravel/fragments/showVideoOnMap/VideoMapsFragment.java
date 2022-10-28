package ua.pkk.wetravel.fragments.showVideoOnMap;

import android.animation.Animator;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ua.pkk.wetravel.R;
import ua.pkk.wetravel.databinding.FragmentVideoMapsBinding;
import ua.pkk.wetravel.fragments.editUserAccount.EditUserAccountFragment;
import ua.pkk.wetravel.fragments.videoItem.VideoFragment;
import ua.pkk.wetravel.utils.Keys;
import ua.pkk.wetravel.utils.Video;

public class VideoMapsFragment extends Fragment {
    private VideoMapsViewModel viewModel;
    private GoogleMap map;
    private FragmentVideoMapsBinding binding;
    private ArrayList<Pair<MarkerOptions, Video>> videos;
    private static VideoMapsFragment fragment;

    public static VideoMapsFragment getInstance(){
        if (fragment == null){
            fragment = new VideoMapsFragment();
        }
        return fragment;
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            map.setOnInfoWindowClickListener(this::onInfoWindowClick);
            LatLng ukraine = new LatLng(33, 32);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(ukraine));
        }

        public void onInfoWindowClick(@NotNull Marker marker) {
            if (!Keys.isNewDesign()){
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(VideoMapsFragmentDirections.actionVideoMapsFragmentToVideoFragment((Video) marker.getTag(), Keys.VIDEO_FROM_MAP.getIntValue()));
            } else {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main_fragments_container, new VideoFragment((Video) marker.getTag(), Keys.VIDEO_FROM_MAP.getIntValue())).commit();
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(VideoMapsViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video_maps, container, false);
        videos = new ArrayList<>();

        viewModel.markers.observe(getViewLifecycleOwner(), markerOptionsVideoPair -> {
            if (map == null) return;
            binding.mapLoadPb.setVisibility(View.GONE);
            binding.mapLoadTv.setVisibility(View.GONE);
            setMarkers(markerOptionsVideoPair);
            videos.add(markerOptionsVideoPair);
        });
        viewModel.getMarkers();
        initSearchLayout();
        return binding.getRoot();
    }

    private TextWatcher tagListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > before && s.charAt(before) == '#')
                binding.filterValue.setSelection(s.length());
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() != 0 && s.charAt(s.length() - 1) == ' ')
                binding.filterValue.setText(binding.filterValue.getText().toString() + "#");
        }
    };

    //TODO is not work
    private void checkIsFilterLayoutWasShown() {
        if (viewModel.isFilterLayoutWasShown) {
            binding.marker.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                binding.filterLayout.animate().y(binding.marker.getY()).setDuration(0);
                binding.searchFab.animate().scaleX(0).scaleY(0).setDuration(0);
                viewModel.isFilterLayoutWasShown = true;
            });
        }
    }

    private void initSearchLayout() {
        checkIsFilterLayoutWasShown();
        binding.searchFab.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (viewModel.isFilterLayoutWasShown) binding.searchFab.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        binding.searchFab.setOnClickListener(v -> {
            binding.filterLayout.animate().translationY(binding.filterLayout.getHeight() * -1).setDuration(500);
            binding.searchFab.animate().scaleX(0).scaleY(0).setDuration(500);
            viewModel.isFilterLayoutWasShown = true;
        });
        binding.closeFilterBtn.setOnClickListener(v -> {
            binding.searchFab.setVisibility(View.VISIBLE);
            binding.filterLayout.animate().translationY(binding.filterLayout.getHeight()).setDuration(500);
            binding.searchFab.animate().scaleY(1).scaleX(1).setDuration(500);
            viewModel.isFilterLayoutWasShown = false;
        });

        binding.filterList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch ((String) parent.getSelectedItem()) {
                    case "Find by video name":
                        binding.filterValueLayout.setHint("Enter video name");
                        binding.filterValueLayout.setPrefixText("");
                        binding.filterValue.removeTextChangedListener(tagListener);
                        break;
                    case "Find by Tags":
                        binding.filterValueLayout.setHint("Enter tags");
                        binding.filterValueLayout.setPrefixText("#");
                        binding.filterValueLayout.setPrefixTextColor(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
                        binding.filterValue.addTextChangedListener(tagListener);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        binding.findBtn.setOnClickListener(v -> {
            switch ((String) binding.filterList.getSelectedItem()) {
                case "Find by video name":
                    focusOnVideoName(binding.filterValue.getText().toString());
                    break;
                case "Find by Tags":
                    showVideosWithTags("#" + binding.filterValue.getText().toString());
                    break;
            }
        });

        binding.showAllBtn.setOnClickListener(v -> {
            map.clear();
            for (Pair<MarkerOptions, Video> i : videos) {
                setMarkers(i);
            }
        });
    }

    private void showVideosWithTags(String tags) {
        String[] inputTags = tags.split(" ++");
        for (Pair<MarkerOptions, Video> i : videos) {
            String[] videoTags = i.second.tags.split(" ++");
            for (String inputTag : inputTags) {
                for (String videoTag : videoTags) {
                    if (inputTag.equals(videoTag)) {
                        map.clear();
                        setMarkers(i);
                    }
                }
            }
        }
    }

    private void focusOnVideoName(String name) {
        for (Pair<MarkerOptions, Video> i : videos) {
            if (i.second.getName().equals(name)) {
                map.moveCamera(CameraUpdateFactory.newLatLng(i.first.getPosition()));
            }
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void setMarkers(Pair<MarkerOptions, Video> markerVideoHashMap) {
        Marker marker = map.addMarker(markerVideoHashMap.first);
        marker.setTag(markerVideoHashMap.second);
    }

}