package tournamentmanager;

import java.util.Arrays;

public final class Team implements Comparable<Team>
{
    public static final int MAX_PLAYERS_PER_TEAM = 4;
    private Player[] player;
    private float teamHandicap, grossScore, netScore;
    private int[] scorePerHole, playerCourseHandicap;
    private int playersPerTeam; // A team can be 1 to 4 people.
    private int flightPlace;

    public float getGrossScore()
    {
        return (grossScore);
    }

    public int compareTo(Team comparisonTeam)
    {
        int comparisonResult = 0;
        final int LOWER = -1, TIE = 0, HIGHER = 1;

        if (comparisonTeam.getNetScore() > this.netScore)
            comparisonResult = LOWER;
        else if (comparisonTeam.getNetScore() == this.netScore)
            comparisonResult = TIE;
        else if (comparisonTeam.getNetScore() < this.netScore)
            comparisonResult = HIGHER;

        return (comparisonResult);
    }
    
    public void setGrossScore(float newGrossScore)
    {
        grossScore = newGrossScore;
    }

    public void setFlightPlace(int newFlightPlace)
    {
        flightPlace = newFlightPlace;
    }

    public int getFlightPlace()
    {
        return (flightPlace);
    }
    
    public float getNetScore()
    {
        return (netScore);
    }

    public void setNetScore(float newNetScore)
    {
        netScore = newNetScore;
    }

    public Player[] getPlayer()
    {
        return (player);
    }

    public void setPlayer(Player[] newPlayer)
    {
        player = newPlayer.clone();
        playersPerTeam = player.length;
    }

    public float getTeamHandicap(TournamentType tournamentType)
    {
        int[] localCourseHandicap = playerCourseHandicap.clone();

        switch (tournamentType)
        {
            case individualStroke:
                // Individual stroke doesn't use team handicap.
                teamHandicap = 0;
                break;

            case fourPersonScramble:
                /* Sort the individual handicaps lowest to highest. Then take
                 * 40% of the lowest, 30% of the next, 20% of the next and 10%
                 * of the highest and add them together and multiply by 0.5. */
                if (playersPerTeam != 4)
                {
                    teamHandicap = 0;
                    break;
                }

                Arrays.sort(localCourseHandicap);
                teamHandicap = (float)(0.5 * (0.4 * localCourseHandicap[0] + 0.3 * localCourseHandicap[1] + 0.2 * localCourseHandicap[2] + 0.1 * localCourseHandicap[3]));
                break;

            case twoPersonScramble:
                /* Sort the individual handicaps lowest to highest. Then take
                 * 70% of the lowest and 30% of the highest and add them
                 * together and multiply by 0.35. */
                if (playersPerTeam != 2)
                {
                    teamHandicap = 0;
                    break;
                }

                Arrays.sort(localCourseHandicap);
                teamHandicap = (float)(0.35 * (0.7 * localCourseHandicap[0] + 0.3 * localCourseHandicap[1]));
                break;

            case matchPlay:
                // Match play doesn't use team handicap.
                teamHandicap = 0;
                break;

            default:
                // Error case
                teamHandicap = 0;
                break;
        }

        return (teamHandicap);
    }

    public void setTeamHandicap(float newTeamHandicap)
    {
        teamHandicap = newTeamHandicap;
    }

    public int[] getScorePerHole()
    {
        return (scorePerHole);
    }

    public void setScorePerHole(int[] newScorePerHole)
    {
        scorePerHole = newScorePerHole.clone();
    }

    public int getPlayersPerTeam()
    {
        return (playersPerTeam);
    }

    Team(Player[] newPlayers, float newTeamHandicap)
    {
        teamHandicap = newTeamHandicap;
        setPlayer(newPlayers);
        flightPlace = 0;
    }
}
