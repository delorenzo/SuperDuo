package barqsoft.footballscores;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.LocalDate;

import java.util.Locale;

public class PagerFragment extends Fragment
{
    public static final int MAX_PAGES = 20;
    public ViewPager mPagerHandler;
    private ScoresPagerAdapter mPagerAdapter;

    public void selectDate(int year, int month, int day) {
        //DatePicker expects 0 based month of year, but JodaTime expects 1 based.
        //Add one to the month of year to account for this.
        mPagerAdapter.startingDate = new LocalDate(year, month+1, day);
        mPagerHandler.setCurrentItem(MainActivity.currentFragment);
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new ScoresPagerAdapter(getChildFragmentManager(), new LocalDate());
        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setCurrentItem(MainActivity.currentFragment);
        return rootView;
    }


    private class ScoresPagerAdapter extends FragmentStatePagerAdapter
    {
        private LocalDate startingDate;

        @Override
        public Fragment getItem(int i)
        {
            return MainScreenFragment.newInstance(startingDate.plusDays(i-2), getContext());
        }

        @Override
        public int getCount()
        {
            return MAX_PAGES;
        }

        public ScoresPagerAdapter(FragmentManager fm, LocalDate date)
        {
            super(fm);
            startingDate = date;
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            LocalDate date = startingDate.plusDays(position-2);
            String dayName = getDayName(getActivity(),date);
            return String.format(
                    getString(R.string.day_title_format),
                    dayName,
                    date.toString(getString(R.string.month_day_format))
            );
        }

        //this is called when notifyDataSetChanged() is called
        @Override
        public int getItemPosition(Object object) {
            // refresh all fragments when data set changed
            return FragmentStatePagerAdapter.POSITION_NONE;
        }


        public String getDayName(Context context, LocalDate date) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.
            LocalDate today = new LocalDate();
            if (date.equals(today)) {
                return context.getString(R.string.today);
            } else if (date.equals(today.plusDays(1))) {
                return context.getString(R.string.tomorrow);
            } else if (date.equals(today.minusDays(1))) {
                return context.getString(R.string.yesterday);
            } else {
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                return date.toString(getString(R.string.day_of_week_format), Locale.getDefault());
            }
        }
    }
}
