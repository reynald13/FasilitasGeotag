package android.com.fasilitas;

import android.com.fasilitas.models.Facility;

/**
 * Created by User on 5/14/2018.
 */

public interface IMainActivity {

    void createNewFacility(String title, String content);

    void updateFacility(Facility facility);

    void onFacilitySelected(Facility facility);

    void deleteFacility(Facility facility);
}
