package apps.savvisingh.colorpalletteselection;

import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SavviSingh on 27/03/17.
 */

public class adapter extends RecyclerView.Adapter<adapter.ViewHolder> {

    private List<Palette.Swatch> swatches;

    private HashMap<String, Integer> closestColors;

    private List<Integer> list;


    public adapter(List<Integer>  list){
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.swatch_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

//        Palette.Swatch swatch = swatches.get(position);



        holder.itemView.setBackgroundColor(list.get(position));

        //holder.population.setText(swatch.getPopulation() + " ");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView population;

        public ViewHolder(View itemView) {
            super(itemView);

            population = (TextView) itemView.findViewById(R.id.population);
        }
    }
}
