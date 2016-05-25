package thesis.jager.indoorpositioning.view.main_activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import thesis.jager.indoorpositioning.R;

public class ImageAdapter extends BaseAdapter
{
       public ImageAdapter(Context activityContext, List<MenuIcon> menuitems)
       {
              this.context = activityContext;
              this.menuitems = menuitems;
       }

       private Context context;
       private List<MenuIcon> menuitems;

       public int getCount()
       {
              return menuitems.size();
       }

       public Object getItem(int position)
       {
              return menuitems.get(position);
       }

       public long getItemId(int position)
       {
              return position;
       }

       public View getView(int position, View convertView, ViewGroup parent)
       {
              View view;
              if (convertView == null)
              {
                     view = new View(context);
                     LayoutInflater inf = (LayoutInflater) context
                             .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                     view = inf.inflate(R.layout.menuicon, null);
              }
              else
              {
                     view = convertView;
              }
              MenuIcon item = menuitems.get(position);
              ImageView iv = (ImageView) view.findViewById(R.id.img_menuicon);
              TextView tv = (TextView) view.findViewById(R.id.txt_menutitle);
              iv.setImageDrawable(context.getResources().getDrawable(item.getResourceID()));
              tv.setText(item.getTitle());

              return view;
       }

}
