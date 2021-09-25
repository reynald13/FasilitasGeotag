package android.com.fasilitas;

import android.com.fasilitas.models.Facility;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements
        IMainActivity,
        SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener {

    private static final String TAG = "MainActivity";

    //Firebase
    private Facility facility;

    //widgets
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //vars
    private View mParentLayout;
    private ArrayList<Facility> mFacilities = new ArrayList<>();

    private ListRecyclerViewAdapter mListRecyclerViewAdapter;

    private FirebaseFirestore mDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mParentLayout = findViewById(android.R.id.content);
        mRecyclerView = findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        facility = new Facility();

        mDb = FirebaseFirestore.getInstance();

        mSwipeRefreshLayout.setOnRefreshListener(this);

        initRecyclerView();
        getFacilities();
    }

    @Override
    public void deleteFacility(final Facility facility) {

    }

    @Override
    public void onRefresh() {
        getFacilities();
        mSwipeRefreshLayout.setRefreshing(false);
        overridePendingTransition(0, 0);
        this.recreate();
    }

    private void getFacilities() {
        final CollectionReference facilitiesCollectionRef = mDb
                .collection("facilities");
        facilitiesCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Facility facility = documentSnapshot.toObject(Facility.class);
                        mFacilities.add(facility);
                        mListRecyclerViewAdapter.notifyDataSetChanged();
                    }
                } else {
                    makeSnackBarMessage("Query Failed. Check Logs.");
                }
            }
        });
    }

    private void initRecyclerView() {
        if (mListRecyclerViewAdapter == null) {
            mListRecyclerViewAdapter = new ListRecyclerViewAdapter(this, mFacilities);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mListRecyclerViewAdapter);
    }

    @Override
    public void updateFacility(final Facility facility) {

    }

    @Override
    public void onFacilitySelected(Facility facility) {
        ViewDialogUser dialogUser = ViewDialogUser.newInstance(facility);
        dialogUser.show(getSupportFragmentManager(), "View Facility Dialog User");
    }

    @Override
    public void createNewFacility(String title, String content) {

    }

    private void makeSnackBarMessage(String message) {
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_seach);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        String userInput = s.toLowerCase();
        List<Facility> newList = new ArrayList<>();

        for (Facility fac : mFacilities) {
            if (fac.getTitle().toLowerCase().contains(userInput)) {
                newList.add(fac);
            }
        }

        mListRecyclerViewAdapter.updateList(newList);
        return true;
    }
}