package android.com.fasilitas;

import android.com.fasilitas.models.Facility;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.GeoPoint;

public class ViewDialogUser extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "ViewDialogUser";

    //widgets
    private TextView mTitle, mBidang, mName, mPhone, mDesc;
    private Button mMap;

    //vars
    private IMainActivity mIMainActivity;
    private Facility mFacility;

    public static ViewDialogUser newInstance(Facility facility) {
        ViewDialogUser dialog = new ViewDialogUser();

        Bundle args = new Bundle();
        args.putParcelable("facility", facility);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen;

        setStyle(style, theme);

        mFacility = getArguments().getParcelable("facility");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_view_facility_user, container, false);
        mTitle = view.findViewById(R.id.fac_title);
        mBidang = view.findViewById(R.id.fac_bidang);
        mName = view.findViewById(R.id.det_nama);
        mPhone = view.findViewById(R.id.det_nomor);
        mDesc = view.findViewById(R.id.deskripsi);
        mMap = view.findViewById(R.id.map);

        mMap.setOnClickListener(this);
        mPhone.setOnClickListener(this);

        getDialog().setTitle("Ubah Fasilitas");

        setInitialProperties();

        return view;
    }

    private void setInitialProperties() {
        mTitle.setText(mFacility.getTitle());
        mBidang.setText(mFacility.getUser().getBidang());
        mName.setText(mFacility.getUser().getNama());
        mPhone.setText(mFacility.getUser().getPhone());
        mDesc.setText(mFacility.getContent());
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.map: {

                Double lat = mFacility.getGeo_point().getLatitude();
                Double log = mFacility.getGeo_point().getLongitude();
                String lab = "Jorgesys @ Bucharest";

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("geo:<" + lat  + ">,<" + log + ">?q=<" + lat  + ">,<" + log + ">(" + lab + ")"));
                intent.setComponent(new ComponentName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity"));
                startActivity(intent);
                break;
                /*Intent intetenMap = new Intent(getActivity(), MapKoordinatorActivity.class);
                startActivity(intetenMap);
                break;*/
            }

            case R.id.det_nomor: {

                String phone = mFacility.getUser().getPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
                break;
            }

            case R.id.delete: {
                mIMainActivity.deleteFacility(mFacility);
                getDialog().dismiss();
                break;
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mIMainActivity = (IMainActivity) getActivity();
    }
}
