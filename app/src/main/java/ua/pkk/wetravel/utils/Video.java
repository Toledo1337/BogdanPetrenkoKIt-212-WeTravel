package ua.pkk.wetravel.utils;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.squareup.moshi.Json;

import java.util.Objects;

public class Video implements Parcelable {
    private transient Uri reference;
    private transient String name;
    private transient String uploadingTime;
    private transient String upload_user_id;
    private transient String uri;
    private transient Drawable thumbNail;

    @Json(name = "description")
    public String description;
    @Json(name = "tags")
    public String tags;

    public Video(Uri reference, String name, String uploadingTime, String upload_user_id, String description, String tags) {
        this.reference = reference;
        this.name = name;
        this.uploadingTime = uploadingTime;
        this.upload_user_id = upload_user_id;
        if (description != null)
        this.description = description; else this.description = "";
        if (tags != null)
        this.tags = tags; else this.tags = "";
    }

    public Video(String description, String tags) {
        this.description = description;
        this.tags = tags;
    }

    public Video(Uri reference, String name, String uploadingTime, String upload_user_id) {
        this.reference = reference;
        this.name = name;
        this.uploadingTime = uploadingTime;
        this.upload_user_id = upload_user_id;
    }

    protected Video(Parcel in) {
        name = in.readString();
    }

    public Uri getReference() {
        return reference;
    }

    public String getName() {
        return name;
    }

    public String getUploadingTime() {
        return uploadingTime;
    }

    public String getUpload_user_id() {
        return upload_user_id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Drawable getThumbNail() {
        return thumbNail;
    }

    public void setThumbNail(Drawable thumbNail) {
        this.thumbNail = thumbNail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Video buf = (Video) obj;
        return Objects.equals(name, buf.name)
                && Objects.equals(reference, buf.reference)
                && Objects.equals(uploadingTime, buf.uploadingTime)
                && Objects.equals(upload_user_id, buf.upload_user_id)
                && Objects.equals(uri, buf.uri);  //TODO more fields
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeValue(reference);
        dest.writeString(upload_user_id);
        dest.writeString(uploadingTime);
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public static final DiffUtil.ItemCallback<Video> DIFF_CALLBACK = new DiffUtil.ItemCallback<Video>() {
        @Override
        public boolean areItemsTheSame(@NonNull Video oldItem, @NonNull Video newItem) {
            return oldItem.getName().equals(newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Video oldItem, @NonNull Video newItem) {
            return oldItem.equals(newItem);
        }
    };
}
