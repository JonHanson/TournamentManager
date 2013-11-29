package tournamentmanager;

import java.util.*;

public final class Tournament
{
    private TournamentType tournamentType;
    private Flight flight[];
    private int numberOfTeams, numberOfFlights;
    private Course course;
    private boolean mixed, skins;
    private static int DOUBLE_BOGEY = 2, HOLES = 18, CALLAWAY_HOLES = 16;

    private void determineSkinsResults()
    {

    }

    /* This function will do the actual comparison for the different handicap holes
     * and returns the number of ties that were broken.
     */
    private int compareHoles(List<Team> tiedTeams, int holeIndex, int currentPlace)
    {
        int tiesBroken = 0;
        boolean done = false, firstTime;
        int lowestScore;

        while (!done)
        {
            // Go through and find the lowest score for the given hole.
            firstTime = true;
            lowestScore = 10;
            for (Team team : tiedTeams)
            {
                // If a team has already been placed skip it.
                if (team.getFlightPlace() != 0)
                    continue;

                if (firstTime)
                {
                    lowestScore = team.getScorePerHole()[holeIndex];
                    firstTime = false;
                    continue;
                }

                if (team.getScorePerHole()[holeIndex] < lowestScore)
                    lowestScore = team.getScorePerHole()[holeIndex];
            }

            // Now see which teams have the lowest score.
            for (Team team : tiedTeams)
            {
                // If a team has already been placed skip it.
                if (team.getFlightPlace() != 0)
                    continue;

                
            }
        }

        return (tiesBroken);
    }

    private boolean breakTies(List<Team> tiedTeams, int currentPlace)
    {
        /* Ties are broken by going from the #1 handicap hole and comparing each tied
         * particiant's score on that hole until the tie is broken. How do we handle
         * a tie between a man and a woman (they could have different holes for the
         * #1 handicap)? Right now we'll just return false from here to indicate
         * a problem of some kind.
         */
        boolean finished = false;
        int handicapHole = 1, tiesBroken;
        Sexes tiedTeamsSex = tiedTeams.get(0).getPlayer()[0].getSex();

         // Determine if we have mixed sexes in the tied teams
        for (int index = 0; index < tiedTeams.size(); index++)
            if (tiedTeams.get(index).getPlayer()[0].getSex() != tiedTeamsSex)
                return (false);

        do
        {
            int holeIndex;

            // Find the handicap hole we are looking for
            for (holeIndex = 0; holeIndex < HOLES; holeIndex++)
            {
                if (tiedTeamsSex == Sexes.male)
                {
                     if (course.getMensHoleHandicap()[holeIndex] == handicapHole)
                        break;
                }
                else
                {
                    if (course.getWomensHoleHandicap()[holeIndex] == handicapHole)
                        break;
                }
            }

            tiesBroken = compareHoles(tiedTeams, holeIndex, currentPlace);
            if (tiesBroken != tiedTeams.size())
            {
                // Still tied, go to the next handicap hole.
                handicapHole++;
                
                // Something's wrong because we couldn't break the tie after 18 holes.
                if (handicapHole == 19)
                    return (false);

                currentPlace += tiesBroken;
            }
            else
            {
                currentPlace += tiedTeams.size();
                finished = true;
            }
        } while (!finished);

        return (true);
    }

    // Check for ties and adjust the flight places accordingly (the flight has already been sorted).
    private boolean assignPlaces(Flight flightToAssign)
    {
        boolean status, placesAssigned = false;
        List<Team> tiedTeams = new ArrayList<Team>();

        if (tournamentType == TournamentType.individualStroke)
        {
            int previousScore, currentScore, currentPlace = 1;
            boolean tieFound = false;

            previousScore = (int)flightToAssign.getTeamList().get(0).getNetScore();
            for (int teamIndex = 1; teamIndex < flightToAssign.getTeamList().size(); teamIndex++)
            {
                currentScore = (int)flightToAssign.getTeamList().get(teamIndex).getNetScore();
                if (currentScore != previousScore)
                {
                    // The scores do not match (no tie)
                    if (tieFound == false)
                    {
                        // We are not at the end of a series of tied scores
                        previousScore = currentScore;
                        if (teamIndex == 1)
                            // If this is the first iteration, assign the first place.
                            flightToAssign.getTeamList().get(0).setFlightPlace(currentPlace++);

                        flightToAssign.getTeamList().get(teamIndex).setFlightPlace(currentPlace++);
                        continue;
                    }
                    else
                    {
                        // This is the first score that wasn't tied in the series.
                        tieFound = false;
                        status = breakTies(tiedTeams, currentPlace);
                        if (status == false)
                            placesAssigned = false;

                        currentPlace += tiedTeams.size();
                        tiedTeams.clear();
                    }
                }
                else
                {
                    // A tie was found.
                    if (tieFound == false)
                        // If this was the first tie found we have to save the previous team as well.
                        tiedTeams.add(flightToAssign.getTeamList().get(teamIndex - 1));

                    tiedTeams.add(flightToAssign.getTeamList().get(teamIndex));
                    tieFound = true;
                }

                previousScore = currentScore;
            }
        }
        else if (tournamentType == TournamentType.fourPersonScramble || tournamentType == TournamentType.twoPersonBestBall || tournamentType == TournamentType.twoPersonScramble)
            /* We shouldn't get ties for these kinds of tournaments because they have
             * decimal final scores so just assign the places here.
             */
            for (int teamIndex = 0; teamIndex < flightToAssign.getTeamList().size(); teamIndex++)
                flightToAssign.getTeamList().get(teamIndex).setFlightPlace(teamIndex + 1);

        // Nothing is done here for match play (no places in match play).
        
        return (placesAssigned);
    }

    // Figure out the winners of the flights.
    private void determineFlightWinners()
    {
        for (int flightCounter = 0; flightCounter < numberOfFlights; flightCounter++)
        {
            if (flight[flightCounter].getCallawayFlight() == true)
            {
                Collection<Team> teamCollection = flight[flightCounter].getTeamList();

                // Callaway flight (http://www.usga.org/handicapping/articles_resources/Handicapping-The-Unhandicapped/)
                for (Iterator<Team> teamIterator = teamCollection.iterator(); teamIterator.hasNext(); )
                    for (int playerCounter = 0; playerCounter < teamIterator.next().getPlayersPerTeam(); playerCounter++)
                    {
                        int[] coursePar, playerScore, callawayHoles;
                        int totalScore, callawayHandicap = 0, originalTotalScore;

                        callawayHoles = new int[CALLAWAY_HOLES];
                        if (teamIterator.next().getPlayer()[playerCounter].getSex() == Sexes.male)
                            coursePar = course.getMensPar().clone();
                        else
                            coursePar = course.getWomensPar().clone();

                        playerScore = teamIterator.next().getScorePerHole().clone();
                        originalTotalScore = calculateStrokeTotal(playerScore);
                        for (int hole = 0; hole < coursePar.length; hole++)
                        {
                            // No hole can be more than double par.
                            if (playerScore[hole] > 2 * coursePar[hole])
                                playerScore[hole] = 2 * coursePar[hole];
                            
                            // Callaway scoring uses the first 16 holes only.
                            if (hole < CALLAWAY_HOLES)
                                callawayHoles[hole] = playerScore[hole];
                        }

                        totalScore = calculateStrokeTotal(playerScore);
                        Arrays.sort(callawayHoles);
                        switch (totalScore)
                        {
                            case 73:
                            case 74:
                            case 75:
                                // 1/2 worst hole adjustment
                                callawayHandicap = Math.round((float)0.5 * callawayHoles[15]);
                                break;

                            case 76:
                            case 77:
                            case 78:
                            case 79:
                            case 80:
                                // 1 worst hole adjustment
                                callawayHandicap = callawayHoles[15];
                                break;

                            case 81:
                            case 82:
                            case 83:
                            case 84:
                            case 85:
                                // 1-1/2 worst holes adjustment
                                callawayHandicap = callawayHoles[15] + Math.round((float)0.5 * callawayHoles[14]);
                                break;

                            case 86:
                            case 87:
                            case 88:
                            case 89:
                            case 90:
                                // 2 worst holes adjustment
                                callawayHandicap = callawayHoles[15] + callawayHoles[14];
                                break;

                            case 91:
                            case 92:
                            case 93:
                            case 94:
                            case 95:
                                // 2-1/2 worst holes adjustment
                                callawayHandicap = callawayHoles[15] + callawayHoles[14] + Math.round((float)0.5 * callawayHoles[13]);
                                break;

                            case 96:
                            case 97:
                            case 98:
                            case 99:
                            case 100:
                                // 3 worst holes adjustment
                                callawayHandicap = callawayHoles[15] + callawayHoles[14] + callawayHoles[13];
                                break;

                            case 101:
                            case 102:
                            case 103:
                            case 104:
                            case 105:
                                // 3-1/2 worst holes adjustment
                                callawayHandicap = callawayHoles[15] + callawayHoles[14] + callawayHoles[13] + Math.round((float)0.5 * callawayHoles[12]);
                                break;

                            case 106:
                            case 107:
                            case 108:
                            case 109:
                            case 110:
                                // 4 worst holes adjustment
                                callawayHandicap = callawayHoles[15] + callawayHoles[14] + callawayHoles[13] + callawayHoles[12];
                                break;

                            case 111:
                            case 112:
                            case 113:
                            case 114:
                            case 115:
                                // 4-1/2 worst holes adjustment
                                callawayHandicap = callawayHoles[15] + callawayHoles[14] + callawayHoles[13] + callawayHoles[12] + Math.round((float)0.5 * callawayHoles[11]);
                                break;

                            case 116:
                            case 117:
                            case 118:
                            case 119:
                            case 120:
                                // 5 worst holes adjustment
                                callawayHandicap = callawayHoles[15] + callawayHoles[14] + callawayHoles[13] + callawayHoles[12] + callawayHoles[11];
                                break;

                            case 121:
                            case 122:
                            case 123:
                            case 124:
                            case 125:
                                // 5-1/2 worst holes adjustment
                                callawayHandicap = callawayHoles[15] + callawayHoles[14] + callawayHoles[13] + callawayHoles[12] + callawayHoles[11] + Math.round((float)0.5 * callawayHoles[10]);;
                                break;

                            case 126:
                            case 127:
                            case 128:
                            case 129:
                            case 130:
                                // 6 worst holes adjustment
                                callawayHandicap = callawayHoles[15] + callawayHoles[14] + callawayHoles[13] + callawayHoles[12] + callawayHoles[11] + callawayHoles[10];
                                break;
                        }

                        switch (totalScore)
                        {
                            case 73:
                            case 76:
                            case 81:
                            case 86:
                            case 91:
                            case 96:
                            case 101:
                            case 106:
                            case 111:
                            case 116:
                            case 121:
                            case 126:
                                callawayHandicap -= 2;
                                break;

                            case 74:
                            case 77:
                            case 82:
                            case 87:
                            case 92:
                            case 97:
                            case 102:
                            case 107:
                            case 112:
                            case 117:
                            case 122:
                            case 127:
                                callawayHandicap -= 1;
                                break;

                            case 71:
                            case 79:
                            case 84:
                            case 89:
                            case 94:
                            case 99:
                            case 104:
                            case 109:
                            case 114:
                            case 119:
                            case 124:
                            case 129:
                                callawayHandicap += 1;
                                break;

                            case 72:
                            case 80:
                            case 85:
                            case 90:
                            case 95:
                            case 100:
                            case 105:
                            case 110:
                            case 115:
                            case 120:
                            case 125:
                            case 130:
                                callawayHandicap += 2;
                                break;
                        }

                        // 50 is the highest callaway handicap allowed.
                        if (callawayHandicap > 50)
                            callawayHandicap = 50;

                        teamIterator.next().setNetScore(originalTotalScore - callawayHandicap);
                        teamIterator.next().setGrossScore(originalTotalScore);
                    }

                flight[flightCounter].sortFlight();
                assignPlaces(flight[flightCounter]);
            }
            else if (tournamentType != TournamentType.matchPlay)
            {
                // Non-callaway flight
                flight[flightCounter].sortFlight();
                assignPlaces(flight[flightCounter]);
            }

            // TODO: Figure out match play.
        }
    }

    // Calculate the strokes per hole based on a player's Course Handicap.
    private void calculateStrokeDots()
    {
        if (tournamentType != TournamentType.twoPersonBestBall || getTournamentType() != TournamentType.matchPlay || getTournamentType() != TournamentType.individualStroke)
            // Stokes per hole not used for this kind of tournament
            return;

        for (int flightCounter = 0; flightCounter < numberOfFlights; flightCounter++)
        {
            Collection<Team> teamCollection = flight[flightCounter].getTeamList();

            for (Iterator<Team> teamIterator = teamCollection.iterator(); teamIterator.hasNext();)
                for (int playerCounter = 0; playerCounter < teamIterator.next().getPlayersPerTeam(); playerCounter++)
                {
                    Sexes playerSex;
                    float playerHandicapIndex;
                    int[] courseHoleHandicap, playerHoleDots;
                    int courseHandicap, baselineDots, remainingDots;

                    playerSex = teamIterator.next().getPlayer()[playerCounter].getSex();
                    playerHandicapIndex = teamIterator.next().getPlayer()[playerCounter].getHandicapIndex();
                    courseHandicap = course.calculateCourseHandicap(playerSex, playerHandicapIndex, mixed);
                    playerHoleDots = new int[HOLES];
                    if (playerSex == Sexes.male)
                        courseHoleHandicap = course.getMensHoleHandicap().clone();
                    else
                        courseHoleHandicap = course.getWomensHoleHandicap().clone();

                    /* The following variable is used to hold the dots that would be
                     * applied to every hole. For example, with a Course Handicap
                     * of 19 the player would get a stroke on every hole and then
                     * an additional stroke on the hardest hole. */
                    baselineDots = courseHandicap / HOLES;

                    remainingDots = courseHandicap % HOLES;
                    for (int holeIndex = 0; holeIndex < courseHoleHandicap.length; holeIndex++)
                    {
                        playerHoleDots[holeIndex] = baselineDots;
                        if (courseHoleHandicap[holeIndex] <= remainingDots)
                            playerHoleDots[holeIndex]++;
                    }

                    teamIterator.next().getPlayer()[playerCounter].setStrokesPerHole(playerHoleDots);
                }
        }
    }

    // Calculates the ESC-adjusted score for all players.
    private void equitableStrokeControlAdjustment()
    {
        /* Equitable Stroke Control
         * Course Handicap  Maximum Score
         * 0-9              Double bogey
         * 10-19            7
         * 20-29            8
         * 30-39            9
         * 40 or more       10
        */

        if (tournamentType != TournamentType.individualStroke || tournamentType != TournamentType.twoPersonBestBall || tournamentType != TournamentType.matchPlay)
            // ESC not used for this type of tournament.
            return;

        for (int flightCounter = 0; flightCounter < numberOfFlights; flightCounter++)
        {
            Collection<Team> teamCollection = flight[flightCounter].getTeamList();

            for (Iterator<Team> teamIterator = teamCollection.iterator(); teamIterator.hasNext();)
                for (int playerCounter = 0; playerCounter < teamIterator.next().getPlayersPerTeam(); playerCounter++)
                {
                    Sexes playerSex;
                    float playerHandicapIndex;
                    int[] playerESCScore, courseHolePar;
                    int courseHandicap, hole;

                    playerSex = teamIterator.next().getPlayer()[playerCounter].getSex();
                    playerHandicapIndex = teamIterator.next().getPlayer()[playerCounter].getHandicapIndex();
                    playerESCScore = teamIterator.next().getScorePerHole().clone();
                    courseHandicap = course.calculateCourseHandicap(playerSex, playerHandicapIndex, mixed);
                    if (playerSex == Sexes.male)
                        courseHolePar = course.getMensPar().clone();
                    else
                        courseHolePar = course.getWomensPar().clone();

                    if (courseHandicap <= 9)
                        // Maximum allowed score is double bogey on any hole.
                        for (hole = 0; hole < courseHolePar.length; hole++)
                            if (playerESCScore[hole] > courseHolePar[hole] + DOUBLE_BOGEY)
                                playerESCScore[hole] = courseHolePar[hole] + DOUBLE_BOGEY;
                    else if (courseHandicap >= 10 && courseHandicap <= 19)
                        // Maximum allowed score is 7 on any hole for 10-19.
                        for (hole = 0; hole < courseHolePar.length; hole++)
                            if (playerESCScore[hole] > 7)
                                playerESCScore[hole] = 7;
                    else if (courseHandicap >= 20 && courseHandicap <= 29)
                        // Maximum allowed score is 8 on any hole for 20-29.
                        for (hole = 0; hole < courseHolePar.length; hole++)
                            if (playerESCScore[hole] > 8)
                                playerESCScore[hole] = 8;
                    else if (courseHandicap >= 30 && courseHandicap <= 39)
                        // Maximum allowed score is 9 on any hole for 30-39.
                        for (hole = 0; hole < courseHolePar.length; hole++)
                            if (playerESCScore[hole] > 9)
                                playerESCScore[hole] = 9;
                    else if (courseHandicap >= 40)
                        // Maximum allowed score is 10 on any hole for 40 or more.
                        for (hole = 0; hole < courseHolePar.length; hole++)
                            if (playerESCScore[hole] > 10)
                                playerESCScore[hole] = 10;

                    teamIterator.next().getPlayer()[playerCounter].setESCScore(calculateStrokeTotal(playerESCScore));
                }
        }
    }

    private int calculateStrokeTotal(int[] holes)
    {
        int total = 0;

        for (int i = 0; i < holes.length; i++)
            total += holes[i];

        return (total);
    }

    public TournamentType getTournamentType()
    {
        return (tournamentType);
    }

    public void setTournamentType(TournamentType newTournamentType)
    {
        tournamentType = newTournamentType;
    }

    public void setNumberOfFlights(int newNumberOfFlights)
    {
        numberOfFlights = newNumberOfFlights;
    }

    public int getNumberOfFlights()
    {
        return (numberOfFlights);
    }

    public void setMixed(boolean isMixed)
    {
        mixed = isMixed;
    }

    public boolean getMixed()
    {
        return (mixed);
    }

    public boolean getSkins()
    {
        return (skins);
    }

    public void setSkins(boolean isSkins)
    {
        skins = isSkins;
    }

    Tournament(TournamentType newTournamentType, boolean isMixed, boolean isSkins)
    {
        tournamentType = newTournamentType;
        mixed = isMixed;
        skins = isSkins;
    }
}
