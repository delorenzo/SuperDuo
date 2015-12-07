package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class FootballScoresAdapter extends CursorAdapter
{
    public double detail_match_id = 0;
    public FootballScoresAdapter(Context context, Cursor cursor, int flags)
    {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor)
    {
        final ViewHolder mHolder = (ViewHolder) view.getTag();
        String homeTeamName = cursor.getString(MainScreenFragment.INDEX_HOME_TEAM);
        String awayTeamName = cursor.getString(MainScreenFragment.INDEX_AWAY_TEAM);
        mHolder.homeName.setText(homeTeamName);
        mHolder.awayName.setText(awayTeamName);
        mHolder.date.setText(cursor.getString(MainScreenFragment.INDEX_MATCH_TIME));
        mHolder.score.setText(Utilities.getScores(
                context,
                cursor.getInt(MainScreenFragment.INDEX_HOME_GOALS),
                cursor.getInt(MainScreenFragment.INDEX_AWAY_GOALS)));
        mHolder.matchId = cursor.getDouble(MainScreenFragment.INDEX_MATCH_ID);
        mHolder.homeCrest.setImageResource(Utilities.getTeamCrestByTeamName(homeTeamName));
        mHolder.homeCrest.setContentDescription(
                String.format(context.getString(R.string.home_crest), homeTeamName));
        mHolder.awayCrest.setImageResource(Utilities.getTeamCrestByTeamName(awayTeamName));
        mHolder.awayCrest.setContentDescription(
                String.format(context.getString(R.string.away_crest), awayTeamName));
        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        if(mHolder.matchId == detail_match_id)
        {
            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
            TextView matchDay = (TextView) v.findViewById(R.id.matchday_textview);
            int leagueId = cursor.getInt(MainScreenFragment.INDEX_LEAGUE);
            matchDay.setText(Utilities.getMatchDay(
                    context,
                    cursor.getInt(MainScreenFragment.INDEX_MATCH_DAY),
                    leagueId));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilities.getLeague(context, leagueId));
            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(context,
                            mHolder.homeName.getText()+" "
                    +mHolder.score.getText()+" "+mHolder.awayName.getText() + " "));
                }
            });
        }
        else
        {
            container.removeAllViews();
        }

    }

    public Intent createShareForecastIntent(Context context, String shareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.football_scores_sharetext, shareText));
        return shareIntent;
    }
}
