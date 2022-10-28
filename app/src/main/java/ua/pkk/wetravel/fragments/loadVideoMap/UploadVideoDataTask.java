package ua.pkk.wetravel.fragments.loadVideoMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.pkk.wetravel.retrofit.UserAPI;
import ua.pkk.wetravel.utils.User;
import ua.pkk.wetravel.utils.Video;

public class UploadVideoDataTask implements Runnable{
    private final String description;
    private final String tags;
    private final String name;

    public UploadVideoDataTask(String description, String tags, String name) {
        this.description = description;
        this.tags = tags;
        this.name = name;
    }

    @Override
    public void run() {
        Video video = new Video(description, "#" + tags);
        UserAPI.INSTANCE.getRETROFIT_SERVICE().uploadVideoData(User.getInstance().getId(), name, video).enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                //TODO
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
            }
        });
    }
}
