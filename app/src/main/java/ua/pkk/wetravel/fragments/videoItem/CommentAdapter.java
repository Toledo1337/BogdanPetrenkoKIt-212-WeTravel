package ua.pkk.wetravel.fragments.videoItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import ua.pkk.wetravel.R;
import ua.pkk.wetravel.databinding.ItemCommentBinding;
import ua.pkk.wetravel.retrofit.COMMENT_DIFF_CALLBACK;
import ua.pkk.wetravel.retrofit.Comment;

public class CommentAdapter extends ListAdapter<Comment, CommentAdapter.CommentHolder> {
    private final Context context;

    protected CommentAdapter(Context context) {
        super(COMMENT_DIFF_CALLBACK.INSTANCE);
        this.context = context;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCommentBinding itemView = ItemCommentBinding.inflate(inflater, parent, false);
        return new CommentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        Comment comment = getItem(position);
        holder.bind(comment);
    }

    class CommentHolder extends RecyclerView.ViewHolder {
        private final ItemCommentBinding binding;

        public CommentHolder(@NonNull ItemCommentBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
            //TODO OnClick
        }

        public void bind(Comment comment) {
            binding.setComment(comment);
            Glide.with(context).load(comment.getUimg())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.progress_bar_animation)
                            .error(R.drawable.video_editor))
                    .into(binding.commentUserImg);
            binding.executePendingBindings();
        }
    }
}
