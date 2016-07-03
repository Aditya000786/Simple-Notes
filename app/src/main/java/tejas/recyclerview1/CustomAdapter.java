package tejas.recyclerview1;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    public Context context;
    public ArrayList<MyData> local_ArrayList;
    String id;
    MyData data;

    public CustomAdapter(ArrayList<MyData> arrayList,Context c)
    {
        local_ArrayList=arrayList;
        context = c;
    }


    //defining ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Title_textview,Content_textview,Date_textview;
        public View custom_layout;
        public CardView card_view;


        public ViewHolder(View itemView){
            super(itemView);
            //itemView.setOnClickListener(this);

            Title_textview=(TextView)itemView.findViewById(R.id.row_title);
            Content_textview=(TextView)itemView.findViewById(R.id.row_content);
            Date_textview=(TextView)itemView.findViewById(R.id.row_date);
            custom_layout=(View)itemView.findViewById(R.id.custom_layout);
            card_view=(CardView)itemView.findViewById(R.id.custom_card);

        }

    }

    //defining primary Override Methods
    @Override//involves inflating a layout from XML and returning the holder
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context c = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(c);

        View customView = inflater.inflate(R.layout.custom_row,parent,false);

        ViewHolder viewHolder = new ViewHolder(customView);
        return viewHolder;
    }


    @Override//Involves populating data into the item through holder
    public void onBindViewHolder(ViewHolder holder, int position) {
        data = local_ArrayList.get(position);
        id = data.getData_id();

        holder.Title_textview.setText(data.getData_Title());
        holder.Content_textview.setText(data.getData_Content());
        holder.Date_textview.setText(data.getData_date());
        //holder.card_view.setBackgroundColor(Integer.parseInt(data.getData_color()));
        holder.card_view.setCardBackgroundColor(Integer.parseInt(data.getData_color()));
    }

    @Override
    public int getItemCount() {
        return local_ArrayList.size();
    }

}
