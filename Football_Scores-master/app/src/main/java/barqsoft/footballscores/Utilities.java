package barqsoft.footballscores;

import android.content.Context;

public class Utilities
{
    public static final int BUNDESLIGA1 = 394;
    public static final int BUNDESLIGA2 = 395;
    public static final int LIGUE1 = 396;
    public static final int LIGUE2 = 397;
    public static final int PREMIER_LEAGUE = 398;
    public static final int PRIMERA_DIVISION = 399;
    public static final int SEGUNDA_DIVISION = 400;
    public static final int SERIE_A = 401;
    public static final int PRIMERA_LIGA = 402;
    public static final int BUNDESLIGA3 = 403;
    public static final int EREDIVISIE = 404;
    public static final int CHAMPIONS_LEAGUE = 405;

    public static String getLeague(Context context, int league_num)
    {
        switch (league_num)
        {
            case LIGUE1:
            case LIGUE2:
                return context.getString(R.string.ligue);
            case SERIE_A : return context.getString(R.string.seria_a);
            case PREMIER_LEAGUE: return context.getString(R.string.premier_league);
            case CHAMPIONS_LEAGUE : return context.getString(R.string.uefa_champions_league);
            case PRIMERA_DIVISION : return context.getString(R.string.primera_division);
            case SEGUNDA_DIVISION: return context.getString(R.string.segunda_division);
            case PRIMERA_LIGA: return context.getString(R.string.primera_liga);
            case EREDIVISIE:  return context.getString(R.string.eredivisie);
            case BUNDESLIGA1:
            case BUNDESLIGA2:
            case BUNDESLIGA3:
                return context.getString(R.string.bundesliga);
            default: return context.getString(R.string.unknown_league);
        }
    }

    public static String getMatchDay(Context context, int match_day, int league_num)
    {
        if(league_num == CHAMPIONS_LEAGUE)
        {
            if (match_day <= 6)
            {
                return context.getString(R.string.group_stages);
            }
            else if(match_day == 7 || match_day == 8)
            {
                return context.getString(R.string.first_knockout_round);
            }
            else if(match_day == 9 || match_day == 10)
            {
                return context.getString(R.string.quarter_final);
            }
            else if(match_day == 11 || match_day == 12)
            {
                return context.getString(R.string.semi_final);
            }
            else
            {
                return context.getString(R.string.final_text);
            }
        }
        else
        {
            return String.format(context.getString(R.string.matchday), match_day);
        }
    }

    public static String getScores(Context context, int homeGoals,int awayGoals)
    {
        if(homeGoals < 0 || awayGoals < 0)
        {
            return context.getString(R.string.no_score);
        }
        else
        {
            return String.format(context.getString(R.string.score), homeGoals, awayGoals);
        }
    }

    public static int getTeamCrestByTeamName (String teamName)
    {
        if (teamName == null){return R.drawable.no_icon;}
        switch (teamName)
        {   //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            case "SD Eibar" : return R.drawable.eibar;
            case "Real Sociedad de Fútbol" : return R.drawable.real_sociedad;
            case "Bologna FC":  return R.drawable.bologna;
            case "SSC Napoli" : return R.drawable.napoli;
            case "CA Osasuna" : return R.drawable.osasuna;
            case "SD Ponferradina" : return R.drawable.sd_ponferradina;
            case "Fortuna Düsseldorf": return R.drawable.fortuna;
            case "Eintracht Braunschweig": return R.drawable.eintracht;
            case "RCD Espanyol": return R.drawable.rcd_espanyol;
            case "Levante UD": return R.drawable.levante;
            case "Athletic Bilbao B": return R.drawable.athletic_bilbao;
            case "Real Zaragoza": return R.drawable.real_zaragoza;
            case "Crystal Palace FC": return R.drawable.crystal_palace;
            default: return R.drawable.no_icon;
        }
    }
}
