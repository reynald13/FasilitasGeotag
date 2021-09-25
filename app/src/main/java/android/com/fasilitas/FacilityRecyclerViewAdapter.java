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

public class FacilityRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "FacilityRecyclerViewAdapter";

    private ArrayList<Facility> mFacilities = new ArrayList<>();
    private IMainActivity mIMainActivity;
    private Context mContext;
    private int mSelectedFacilityIndex;

    public FacilityRecyclerViewAdapter(Context context, ArrayList<Facility> facilities) {
        mFacilities = facilities;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_facility_list_item, parent, false);

        holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).title.setText(mFacilities.get(position).getTitle());

            SimpleDateFormat spf = new SimpleDateFormat("MMM dd, yyyy");
            String date = spf.format(mFacilities.get(position).getTimestamp());
            ((ViewHolder) holder).timestamp.setText(date);
        }
    }

    @Override
    public int getItemCount() {
        return mFacilities.size();
    }


    public void updateFacility(Facility facility) {
        mFacilities.get(mSelectedFacilityIndex).setTitle(facility.getTitle());
        mFacilities.get(mSelectedFacilityIndex).setContent(facility.getContent());
        notifyDataSetChanged();
    }

    public void removeFacility(Facility facility) {
        mFacilities.remove(facility);
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mIMainActivity = (IMainActivity) mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, timestamp;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            timestamp = itemView.findViewById(R.id.timestamp);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mSelectedFacilityIndex = getAdapterPosition();
            mIMainActivity.onFacilitySelected(mFacilities.get(mSelectedFacilityIndex));
        }
    }
}


















