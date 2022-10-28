package ua.pkk.wetravel.fragments.videoItem;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.pkk.wetravel.retrofit.Comment;
import ua.pkk.wetravel.retrofit.UserAPI;
import ua.pkk.wetravel.utils.User;
import ua.pkk.wetravel.utils.Video;

public class VideoFragmentViewModel extends ViewModel {
    private final Video video;

    private MutableLiveData<Boolean> _successDelete = new MutableLiveData<>();
    public LiveData<Boolean> successDelete = _successDelete;

    private MutableLiveData<Comment> _comments = new MutableLiveData<>();
    public LiveData<Comment> comments = _comments;

    private SimpleExoPlayer player;

    public long previousDuration;
    private Uri videoUri;


    public VideoFragmentViewModel(Video video, SimpleExoPlayer player) {
        this.video = video;
        this.player = player;
    }

    public void playVideoFromUri() {
        this.videoUri = video.getReference();
        MediaItem mediaItem = MediaItem.fromUri(video.getReference());
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    public void reCreatePlayerAndPlay(SimpleExoPlayer player) {
        this.player = player;
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.seekTo(previousDuration);
        player.prepare();
        player.setPlayWhenReady(true);
        player.play();
    }

    public void deleteVideo() {
        FirebaseStorage.getInstance().getReference().child(User.getInstance().getId()).child(video.getName()).delete();
        new Thread(() ->
                UserAPI.INSTANCE.getRETROFIT_SERVICE().deleteAllVideoComments(User.getInstance().getId(), video.getName()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //TODO
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        })).start();
        onDelete();
    }

    public void renameVideo() {
        //TODO Rename
    }

    private void onDelete() {
        _successDelete.setValue(true);
    }

    public void loadBitmapToTempFile(String src, File filesDir) {
        new Thread(() -> {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                input.close();

                File temp_img = new File(filesDir, "temp_img");
                FileOutputStream stream = new FileOutputStream(temp_img);
                myBitmap.compress(Bitmap.CompressFormat.PNG, 40, stream);
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void loadComments(String id, String videoName) {
        new Thread(
                () -> UserAPI.INSTANCE.getRETROFIT_SERVICE().getAllVideoComments(id, videoName).enqueue(new Callback<Map<String, Comment>>() {
            @Override
            public void onResponse(Call<Map<String, Comment>> call, Response<Map<String, Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Map.Entry<String, Comment> i : response.body().entrySet()) {
                        _comments.setValue(i.getValue());
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Comment>> call, Throwable t) {
                Log.d("TAG", t.getMessage());
            }
        })).start();

    }
}
