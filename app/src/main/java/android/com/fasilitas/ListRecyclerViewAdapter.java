package android.com.fasilitas;

import android.com.fasilitas.models.Facility;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ListRecyclerViewAdapter";

    private ArrayList<Facility> mFacilitiesUser = new ArrayList<>();

    private IMainActivity mIMainActivityUser;
    private Context mContextUser;
    private int mSelectedFacilityUserIndex;

    public ListRecyclerViewAdapter(Context context, ArrayList<Facility> facilities) {
        mFacilitiesUser = facilities;
        mContextUser = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder holder;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.content_list, viewGroup, false);

        holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof ViewHolder) {
            ((ViewHolder) viewHolder).title.setText(mFacilitiesUser.get(i).getTitle());
            ((ViewHolder) viewHolder).bidang.setText(mFacilitiesUser.get(i).getUser().getBidang());
        }
    }

    @Override
    public int getItemCount() {
        return mFacilitiesUser.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mIMainActivityUser = (IMainActivity) mContextUser;
    }

    public void updateList(List<Facility> newList) {
        mFacilitiesUser = new ArrayList<>();
        mFacilitiesUser.addAll(newList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, bidang;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            bidang = itemView.findViewById(R.id.bidang);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mSelectedFacilityUserIndex = getAdapterPosition();
            mIMainActivityUser.onFacilitySelected(mFacilitiesUser.get(mSelectedFacilityUserIndex));
        }
    }
}
