package innovate.ebad.com.instantmessenger.Adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.ArrayList;

import innovate.ebad.com.instantmessenger.R;


public class UserSearchingAdapater extends ArrayAdapter<String> {

    ArrayList<String> list;
    Context context;
    String texts;

    public UserSearchingAdapater(Context context,ArrayList list) {
        super(context, R.layout.custom_username_searching_layout,list);
        this.context=context;
        this.list  = list;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.e("WTF LISTVIEW","Its running");

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.custom_username_searching_layout, parent, false);

        CircularProgressView progressView = (CircularProgressView) rowView.findViewById(R.id.progressbar);
        TextView textview = (TextView) rowView.findViewById(R.id.SearchingUsername);

        progressView.setIndeterminate(true);
        textview.setText(list.get(position));

        return rowView;

    }



}


