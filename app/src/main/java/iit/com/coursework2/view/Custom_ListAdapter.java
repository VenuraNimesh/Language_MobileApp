package iit.com.coursework2.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.TextView;

import iit.com.coursework2.R;


public class Custom_ListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> translatedMap;
    public static final String FIRST_COLUMN="First";
    public static final String SECOND_COLUMN="Second";

    public Custom_ListAdapter(Context context, ArrayList<HashMap<String, String>> translatedMap) {
        super();
        this.context = context;
        this.translatedMap = translatedMap;
    }

    @Override
    public int getCount() {
        return translatedMap.size();
    }

    @Override
    public Object getItem(int i) {
        return translatedMap.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    private class ViewHolder{
        TextView txtFirst;
        TextView txtSecond;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder holder;

        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null){

            view = inflater.inflate(R.layout.list_adapter_dictionary, null);
            holder = new ViewHolder();

            holder.txtFirst = (TextView) view.findViewById(R.id.phraseColumn);
            holder.txtSecond = (TextView) view.findViewById(R.id.translateColumn);

            view.setTag(holder);
        }else{

            holder = (ViewHolder) view.getTag();
        }

        HashMap<String, String> map = translatedMap.get(position);
        holder.txtFirst.setText(map.get(FIRST_COLUMN));
        holder.txtSecond.setText(map.get(SECOND_COLUMN));

        return view;

    }
}
