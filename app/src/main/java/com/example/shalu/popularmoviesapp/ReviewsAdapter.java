package com.example.shalu.popularmoviesapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by sarum on 10/6/2017.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    String[] author;
    String[] content;
    private ReviewsAdapter.OnReviewsItemClickListener mClickHandler;

    ReviewsAdapter(ReviewsAdapter.OnReviewsItemClickListener clickListener) {
        mClickHandler = clickListener;
    }
    public interface OnReviewsItemClickListener {
        void onClickReviews(int position);
    }
    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews,parent,false);
        ReviewsAdapter.ReviewViewHolder reviewViewHolder = new ReviewViewHolder(view);
        return reviewViewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.mAuthor.setText(author[position]);
        holder.mContent.setText(content[position]);
    }

    @Override
    public int getItemCount() {
        if(author == null)
        return 0;
        else
            return author.length;
    }

    public void setReviews(String[] auth, String[] cont) {
        author = auth;
        content = cont;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mAuthor;
        TextView mContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mAuthor = (TextView) itemView.findViewById(R.id.tv_author);
            mContent = (TextView) itemView.findViewById(R.id.tv_content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickHandler.onClickReviews(position);
        }
    }
}
