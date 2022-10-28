package ua.pkk.wetravel.fragments.videoItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.exoplayer2.SimpleExoPlayer;

import ua.pkk.wetravel.utils.Video;

public class VideoFragmentViewModelFactory implements ViewModelProvider.Factory {
    private Video video;
    private SimpleExoPlayer player;

    public VideoFragmentViewModelFactory(Video video, SimpleExoPlayer player) {
        this.video = video;
        this.player = player;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(VideoFragmentViewModel.class)) {
            return (T) new VideoFragmentViewModel(video, player);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
