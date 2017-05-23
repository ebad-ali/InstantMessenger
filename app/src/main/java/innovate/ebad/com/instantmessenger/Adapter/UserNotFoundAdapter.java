package innovate.ebad.com.instantmessenger.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import innovate.ebad.com.instantmessenger.R;


public class UserNotFoundAdapter extends ArrayAdapter<String>{

    ArrayList<String> list;
    Context context;
    String texts;

    public UserNotFoundAdapter(Context context,ArrayList list) {
        super(context, R.layout.custom_user_not_found_layout,list);
        this.context=context;
        this.list  = list;

        Log.e("WTF LISTVIEW","Its running");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        Log.e("WTF LISTVIEW","Its running");

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.custom_user_not_found_layout, parent, false);

        ImageView imageview = (ImageView) rowView.findViewById(R.id.notfoundInfoImageView);
        TextView textview = (TextView) rowView.findViewById(R.id.notfoundsenderUserFullName);

        imageview.setImageResource(R.drawable.ic_info_icon);
        textview.setText(list.get(position));

        return rowView;

    }


}



