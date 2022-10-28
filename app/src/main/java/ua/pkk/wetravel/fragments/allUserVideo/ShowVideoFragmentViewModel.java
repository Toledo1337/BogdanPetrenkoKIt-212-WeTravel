package ua.pkk.wetravel.fragments.allUserVideo;

import android.net.Uri;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.pkk.wetravel.retrofit.UserAPI;
import ua.pkk.wetravel.utils.User;
import ua.pkk.wetravel.utils.Video;

public class ShowVideoFragmentViewModel extends ViewModel {
    private MutableLiveData<Video> _videos = new MutableLiveData<>();
    public LiveData<Video> videos = _videos;

    public void loadVideo() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference(User.getInstance().getId());
        reference.listAll().addOnSuccessListener(listResult -> {
            addVideo(listResult.getItems());
        });
    }

    private void addVideo(List<StorageReference> items) {
        for (StorageReference reference : items) {
            if (reference.getName().equals("profile_img")) continue;
            reference.getMetadata().addOnSuccessListener(storageMetadata ->
                    //TODO constructors
                    reference.getDownloadUrl().addOnCompleteListener(task -> {
                        String id = storageMetadata.getCustomMetadata("user_id");
                        String uploadingTime = storageMetadata.getCustomMetadata("uploadingTime");
                        String name = reference.getName();
                        getVideoData(id, name, uploadingTime, task.getResult());
                    }));
        }
    }

    private void getVideoData(String id, String name, String uploadingTime, Uri reference) {
        new Thread(() ->
                UserAPI.INSTANCE.getRETROFIT_SERVICE().getVideoData(id, name).enqueue(new Callback<Video>() {
                    @Override
                    public void onResponse(Call<Video> call, Response<Video> response) {
                        Video video = new Video(reference,
                                name,
                                uploadingTime,
                                id,
                                response.body().description,
                                response.body().tags);
                        video.setUri(reference.toString());
                        _videos.postValue(video);
                    }

                    @Override
                    public void onFailure(Call<Video> call, Throwable t) {

                    }
                })).start();
    }

    public void cleanVideo() {
        _videos.setValue(null);
    }
}
