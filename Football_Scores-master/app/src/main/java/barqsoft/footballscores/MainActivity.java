package barqsoft.footballscores;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{
    public static int selectedMatchId;
    public static int currentFragment = 2;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String CURRENT_FRAGMENT = "current_fragment";
    private static final String SELECTED_MATCH = "selected_match";
    private static final String PAGER_FRAGMENT_TAG = "pager_fragment";
    private static final String DATE_PICKER_FRAGMENT_TAG = "date_picker_fragment";
    private PagerFragment mPagerFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            mPagerFragment = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mPagerFragment)
                    .commit();
        }
    }

    // update the pager fragment when the user selects a date from the date picker.
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mPagerFragment.selectDate(year, monthOfYear, dayOfMonth);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about)
        {
            Intent start_about = new Intent(this,AboutActivity.class);
            startActivity(start_about);
            return true;
        }
        else if (id == R.id.menu_item_date_picker) {
            DialogFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.show(getSupportFragmentManager(), DATE_PICKER_FRAGMENT_TAG);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(CURRENT_FRAGMENT, mPagerFragment.mPagerHandler.getCurrentItem());
        outState.putInt(SELECTED_MATCH, selectedMatchId);
        getSupportFragmentManager().putFragment(outState,PAGER_FRAGMENT_TAG, mPagerFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        currentFragment = savedInstanceState.getInt(CURRENT_FRAGMENT);
        selectedMatchId = savedInstanceState.getInt(SELECTED_MATCH);
        mPagerFragment = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState,PAGER_FRAGMENT_TAG);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
