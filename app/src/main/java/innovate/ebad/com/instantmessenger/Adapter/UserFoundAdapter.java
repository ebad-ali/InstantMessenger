package innovate.ebad.com.instantmessenger.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.andexert.library.RippleView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import innovate.ebad.com.instantmessenger.Model.SearchProfile;
import innovate.ebad.com.instantmessenger.R;
import innovate.ebad.com.instantmessenger.UsernameResultProfile;


public class UserFoundAdapter extends ArrayAdapter<SearchProfile> {

    ArrayList<SearchProfile> list;
    Context context;
    RippleView rippleView;

    public UserFoundAdapter(Context context, ArrayList<SearchProfile> list) {
        super(context, R.layout.custom_username_found_result_layout, list);
        this.context = context;
        this.list = list;

        Log.e("WTF LISTVIEW", "Its running");
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        Log.e("WTF LISTVIEW", "Its running");

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View rowView = inflater.inflate(R.layout.custom_username_found_result_layout, parent, false);

        final CircleImageView imageview = (CircleImageView) rowView.findViewById(R.id.senderUserProfilePictureImageView);
        final TextView fullName = (TextView) rowView.findViewById(R.id.senderUserFullName);
        final TextView userName = (TextView) rowView.findViewById(R.id.senderUsername);
        rippleView = (RippleView)rowView.findViewById(R.id.rippleView);

        imageview.setImageURI(list.get(position).getPicUri());
        fullName.setText(list.get(position).getFullName());
        userName.setText(list.get(position).getUserName());

        rippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(context,fullName.getText().toString(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, UsernameResultProfile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("fullName", fullName.getText().toString());
                intent.putExtra("picuri",list.get(position).getPicUri().toString());
                intent.putExtra("username", userName.getText().toString());
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });

        return rowView;
    }



}
