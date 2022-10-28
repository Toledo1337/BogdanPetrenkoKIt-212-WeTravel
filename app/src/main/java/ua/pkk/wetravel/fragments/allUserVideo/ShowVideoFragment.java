package ua.pkk.wetravel.fragments.allUserVideo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import ua.pkk.wetravel.R;
import ua.pkk.wetravel.databinding.FragmentShowVideoBinding;
import ua.pkk.wetravel.utils.Video;

public class ShowVideoFragment extends Fragment {
    private FragmentShowVideoBinding binding;
    private ShowVideoFragmentViewModel viewModel;
    private ArrayList<Video> videos;
    private VideoAdapter adapter;

    public static ShowVideoFragment getInstance(){
        return new ShowVideoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_show_video, container, false);
        viewModel = new ViewModelProvider(this).get(ShowVideoFragmentViewModel.class);
        viewModel.cleanVideo();

        adapter = new VideoAdapter(getContext());

        RecyclerView recyclerView = binding.videos;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        videos = new ArrayList<>();
        adapter.submitList(videos);

        viewModel.videos.observe(getViewLifecycleOwner(), video -> {
            if (video == null) return;
            binding.showVideosPb.setVisibility(View.GONE);

            Glide.with(getContext()).load(video.getUri()).into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    video.setThumbNail(resource);
                    videos.add(video);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        });
        viewModel.loadVideo();
        return binding.getRoot();
    }
}