package android.com.fasilitas;

import android.com.fasilitas.models.Facility;
import android.com.fasilitas.models.User;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class ViewFacilityDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "ViewFacilityDialog";

    //widgets
    private TextView mTitle, mContent;
    private TextView mSave, mDelete;

    //vars
    private IMainActivity mIMainActivity;
    private Facility mFacility;

    public static ViewFacilityDialog newInstance(Facility facility) {
        ViewFacilityDialog dialog = new ViewFacilityDialog();

        Bundle args = new Bundle();
        args.putParcelable("facility", facility);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(style, theme);

        mFacility = getArguments().getParcelable("facility");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_view_facility, container, false);
        mTitle = view.findViewById(R.id.view_title);
        mContent = view.findViewById(R.id.view_content);
        mSave = view.findViewById(R.id.save);
        mDelete = view.findViewById(R.id.delete);

        mSave.setOnClickListener(this);
        mDelete.setOnClickListener(this);

        getDialog().setTitle("Ubah Fasilitas");

        setInitialProperties();

        return view;
    }

    private void setInitialProperties() {
        mTitle.setText(mFacility.getTitle());
        mContent.setText(mFacility.getContent());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.save: {

                String title = mTitle.getText().toString();
                String content = mContent.getText().toString();

                if (!title.equals("")) {

                    mFacility.setTitle(title);
                    mFacility.setContent(content);

                    mIMainActivity.updateFacility(mFacility);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "Masukkan nama pos", Toast.LENGTH_SHORT).show();
                }
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





















