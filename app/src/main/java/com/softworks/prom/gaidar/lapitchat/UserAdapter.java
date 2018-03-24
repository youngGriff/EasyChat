package com.softworks.prom.gaidar.lapitchat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Andrew on 05.09.2017.
 */

public class UserAdapter extends FirebaseRecyclerAdapter<User, UserAdapter.UserHolder> {
    private Context mContext;

    public UserAdapter(Context context) {
        super(User.class, R.layout.users_single_layout, UserHolder.class, FirebaseDatabase.getInstance().getReference("Users"));
        mContext = context.getApplicationContext();
    }

    @Override
    protected void populateViewHolder(UserHolder viewHolder, final User model, final int position) {
        viewHolder.bind(mContext, model);

        viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = getRef(position).getKey();
                Intent intent = ProfileActivity.createIntent(mContext, uid);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            }
        });
    }

    public static class UserHolder extends RecyclerView.ViewHolder {
        CircleImageView mProfile;
        TextView mName;
        TextView mStatus;
        View rootView;

        public UserHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            mProfile = itemView.findViewById(R.id.user_single_image);
            mName = itemView.findViewById(R.id.user_single_name);
            mStatus = itemView.findViewById(R.id.user_single_status);
            mProfile.setImageResource(R.drawable.default_image);
        }

        void bind(Context context, User user) {
            mName.setText(user.getName());
            mStatus.setText(user.getStatus());
            Glide.with(context.getApplicationContext())
                    .load(user.getImage()).apply(new RequestOptions()
                    .placeholder(R.drawable.default_image))
                    .into(mProfile);
            //  Picasso.with(context).load(user.getImage()).into(mProfile);
        }

        void setOnClickListener(View.OnClickListener listener) {
            rootView.setOnClickListener(listener);
        }

    }
}
