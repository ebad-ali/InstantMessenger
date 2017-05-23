package innovate.ebad.com.instantmessenger.Adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import innovate.ebad.com.instantmessenger.MainChatActivity;
import innovate.ebad.com.instantmessenger.Model.SearchProfile;
import innovate.ebad.com.instantmessenger.R;

public class FriendAdapter extends ArrayAdapter<SearchProfile> {

    ArrayList<SearchProfile> list;
    Context context;
    RippleView rippleView;

    public FriendAdapter(Context context, ArrayList<SearchProfile> list) {
        super(context, R.layout.custom_friend_list_layout, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = null;
        if (position == 0) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.custom_friend_header_layout, parent, false);

            final CircleImageView imageview = (CircleImageView) rowView.findViewById(R.id.friendProfilePictureImageView);
            final TextView fullName = (TextView) rowView.findViewById(R.id.friendUserFullName);
            final TextView userName = (TextView) rowView.findViewById(R.id.friendUsername);
            rippleView = (RippleView) rowView.findViewById(R.id.rippleView);


            String path = String.valueOf(list.get(position).getPicUri());
            File file = new File(path);

            if (file.exists() && !path.equals("")) {
                imageview.setImageURI(list.get(position).getPicUri());
            } else {
                imageview.setImageResource(R.drawable.user);
            }

            fullName.setText(list.get(position).getFullName());
            userName.setText(list.get(position).getUserName());

            rippleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(context, MainChatActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("senderusername", userName.getText().toString());
                            intent.putExtra("recieverusername", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                            intent.putExtra("chatroomname", userName.getText().toString() + "room");
                            intent.putExtra("senderfullname", fullName.getText().toString());
                            intent.putExtra("senderpicuri", String.valueOf(list.get(position).getPicUri()));
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }
                    }, 350);


                }
            });
        } else {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.custom_friend_list_layout, parent, false);

            final CircleImageView imageview = (CircleImageView) rowView.findViewById(R.id.friendProfilePictureImageView);
            final TextView fullName = (TextView) rowView.findViewById(R.id.friendUserFullName);
            final TextView userName = (TextView) rowView.findViewById(R.id.friendUsername);
            rippleView = (RippleView) rowView.findViewById(R.id.rippleView);


            String path = String.valueOf(list.get(position).getPicUri());
            File file = new File(path);

            if (file.exists() && !path.equals("")) {
                imageview.setImageURI(list.get(position).getPicUri());
            } else {
                imageview.setImageResource(R.drawable.user);
            }

            fullName.setText(list.get(position).getFullName());
            userName.setText(list.get(position).getUserName());

            rippleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(context, MainChatActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("senderusername", userName.getText().toString());
                            intent.putExtra("recieverusername", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                            intent.putExtra("chatroomname", userName.getText().toString() + "room");
                            intent.putExtra("senderfullname", fullName.getText().toString());
                            intent.putExtra("senderpicuri", String.valueOf(list.get(position).getPicUri()));
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }
                    }, 350);


                }
            });
        }

        return rowView;
    }

}
