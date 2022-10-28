package ua.pkk.wetravel.fragments.showVideoOnMap;

import android.net.Uri;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.pkk.wetravel.retrofit.UserAPI;
import ua.pkk.wetravel.utils.Video;

public class VideoMapsViewModel extends ViewModel {

    private MutableLiveData<Pair<MarkerOptions, Video>> _markers = new MutableLiveData<>();
    public LiveData<Pair<MarkerOptions, Video>> markers = _markers;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    public boolean isFilterLayoutWasShown = false;

    //TODO CALLBACK HELL. try to fix it or Kotlin
    public void getMarkers() {
        storage.getReference().listAll().addOnSuccessListener(listResult ->
                markers(listResult.getPrefixes())
        );
    }

    private void markers(List<StorageReference> id) {
        for (StorageReference i : id) {
            i.listAll().addOnSuccessListener(listResult -> {
                for (StorageReference reference : listResult.getItems()) {
                    getMetaData(reference);
                }
            });
        }
    }

    private void getMetaData(StorageReference reference) {
        reference.getMetadata().addOnSuccessListener(storageMetadata -> {
            if (reference.getName().equals("profile_img")) return;
            String[] meta = storageMetadata.getCustomMetadata("position").split("/");
            LatLng latLng = new LatLng(Double.parseDouble(meta[0]), Double.parseDouble(meta[1]));
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(reference.getName());
            getDownloadUrl(reference, storageMetadata, markerOptions);
        });
    }

    private void getDownloadUrl(StorageReference reference, StorageMetadata storageMetadata, MarkerOptions markerOptions) {
        reference.getDownloadUrl().addOnCompleteListener(task -> {
            String id = storageMetadata.getCustomMetadata("user_id");
            String uploadingTime = storageMetadata.getCustomMetadata("uploadingTime");
            String name = reference.getName();
            getVideoData(id, name, uploadingTime, task.getResult(), markerOptions);
        });
    }

    //last callback step //TODO Threads
    private void getVideoData(String id, String name, String uploadingTime, Uri reference, MarkerOptions markerOptions) {
        new Thread(() -> {
            UserAPI.INSTANCE.getRETROFIT_SERVICE().getVideoData(id, name).enqueue(new Callback<Video>() {
                @Override
                public void onResponse(Call<Video> call, Response<Video> response) {
                    _markers.postValue(new Pair<>(markerOptions,
                            new Video(reference,
                                    name,
                                    uploadingTime,
                                    id,
                                    response.body().description,
                                    response.body().tags)));
                }

                @Override
                public void onFailure(Call<Video> call, Throwable t) {

                }
            });
        }).start();
    }
}
