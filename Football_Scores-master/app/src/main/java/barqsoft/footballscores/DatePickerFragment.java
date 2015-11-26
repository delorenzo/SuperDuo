package barqsoft.footballscores;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;


/**
 * A simple {@link android.app.DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link android.app.DatePickerDialog.OnDateSetListener} interface
 * to handle interaction events.
 */
public class DatePickerFragment extends DialogFragment {
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //get the current date
        final Calendar c = Calendar.getInstance();

        return new DatePickerDialog(
                getActivity(),
                (DatePickerDialog.OnDateSetListener)getActivity(),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    }
}
