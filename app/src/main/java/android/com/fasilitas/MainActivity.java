package android.com.fasilitas;

import android.Manifest;
import android.com.fasilitas.models.Facility;
import android.com.fasilitas.models.User;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        IMainActivity,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MainActivity";

    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Facility facility;
    private User user;

    //widgets
    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextView;

    //vars
    private View mParentLayout;
    private ArrayList<Facility> mFacilities = new ArrayList<>();
    private FacilityRecyclerViewAdapter mFacilityRecyclerViewAdapter;
    private DocumentSnapshot mLastQueriedDocument;

    private FirebaseFirestore mDb;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFab = findViewById(R.id.fab);
        mParentLayout = findViewById(android.R.id.content);
        mRecyclerView = findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mTextView = (TextView) findViewById(R.id.text_bidang);

        facility = new Facility();
        user = new User();

        mDb = FirebaseFirestore.getInstance();

        mFab.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupFirebaseAuth();
        initRecyclerView();
        getFacilities();
        getLastKnownLocation();
        getUserDetails();
    }

    private void getUserDetails() {
        DocumentReference userRef = mDb
                .collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: successfully get the user details");

                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot != null) {
                        User user = task.getResult().toObject(User.class);
                        assert user != null;
                        mTextView.setText(user.getBidang());
                        facility.setUser(user);
                    } else {
                        Log.d(TAG, "Dokumen tidak tersedia");
                    }
                } else {
                    Log.d(TAG, "Gagal", task.getException());
                }
            }
        });
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude());
                    Log.d(TAG, "onComplete: longitude " + geoPoint.getLongitude());

                    facility.setGeo_point(geoPoint);
                }
            }
        });
    }

    @Override
    public void deleteFacility(final Facility facility) {

        DocumentReference facilityRef = mDb
                .collection("facilities")
                .document(facility.getFacility_id());

        facilityRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    makeSnackBarMessage("Pos dihapus");
                    mFacilityRecyclerViewAdapter.removeFacility(facility);
                } else {
                    makeSnackBarMessage("Gagal.");
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        getFacilities();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void getFacilities() {
        CollectionReference facilitiesCollectionRef = mDb
                .collection("facilities");

        Query facilitiesQuery = null;
        if (mLastQueriedDocument != null) {
            facilitiesQuery = facilitiesCollectionRef
                    .whereEqualTo("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .startAfter(mLastQueriedDocument);
        } else {
            facilitiesQuery = facilitiesCollectionRef
                    .whereEqualTo("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .orderBy("timestamp", Query.Direction.ASCENDING);
        }

        facilitiesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) { //berisi list dari dokumen
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) { //loop, dan tambah documen ke array list(mengambil list dokumen secara general)
                        Facility facility = document.toObject(Facility.class);
                        mFacilities.add(facility); //fasilitas ditambah di facilitas arraylist
//                        Log.d(TAG, "onComplete: got a new facility. Position: " + (mFacilities.size() - 1));
                    }

                    if (task.getResult().size() != 0) {
                        mLastQueriedDocument = task.getResult().getDocuments()
                                .get(task.getResult().size() - 1);
                    }

                    mFacilityRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    makeSnackBarMessage("Query gagal.");
                }
            }
        });
    }

    private void initRecyclerView() {
        if (mFacilityRecyclerViewAdapter == null) {
            mFacilityRecyclerViewAdapter = new FacilityRecyclerViewAdapter(this, mFacilities);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFacilityRecyclerViewAdapter);
    }

    @Override
    public void updateFacility(final Facility facility) {
        DocumentReference facilityRef = mDb
                .collection("facilities")
                .document(facility.getFacility_id());

        facilityRef.update(
                "title", facility.getTitle(),
                "content", facility.getContent()
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    makeSnackBarMessage("Pos berhasil diupdate");
                    mFacilityRecyclerViewAdapter.updateFacility(facility);
                } else {
                    makeSnackBarMessage("Gagal.");
                }
            }
        });
    }

    @Override
    public void onFacilitySelected(Facility facility) {
        ViewFacilityDialog dialog = ViewFacilityDialog.newInstance(facility);
        dialog.show(getSupportFragmentManager(), getString(R.string.dialog_view_facility));
    }

    @Override
    public void createNewFacility(String title, String content) {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference newFacilityRef = mDb
                .collection("facilities")
                .document();

        facility.setTitle(title);
        facility.setContent(content);
        facility.setFacility_id(newFacilityRef.getId());
        facility.setUser_id(userId);

        newFacilityRef.set(facility).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    makeSnackBarMessage("Pos berhasil ditambahkan");
                    getFacilities();
                } else {
                    makeSnackBarMessage("Gagal.");
                }
            }
        });
    }

    private void makeSnackBarMessage(String message) {
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fab: {
                //create a new facility
                NewFacilityDialog dialog = new NewFacilityDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.dialog_new_facility));
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.optionSignOut:
                signOut();
                return true;

            case R.id.optionList:
                Intent intentList = new Intent(this, ListActivity.class);
                startActivity(intentList);
                return true;

            case R.id.optionMap:
                Intent intetenMap = new Intent(this, MapKoordinatorActivity.class);
                startActivity(intetenMap);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        Log.d(TAG, "signOut: signing out");
        FirebaseAuth.getInstance().signOut();
    }

    /*
            ----------------------------- Firebase setup ---------------------------------
         */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }
}





