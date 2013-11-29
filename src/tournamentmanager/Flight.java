package tournamentmanager;

import java.util.*;

public final class Flight
{
    private List<Team> teamList;
    private int flightIdentifier;
    private boolean isCallaway;

    public void sortFlight()
    {
        Collections.sort(teamList);
    }

    public void addTeam(Team newTeam)
    {
        teamList.add(newTeam);
    }

    public List<Team> getTeamList()
    {
        return (teamList);
    }

    public void setFlightIdentifier(int newFlightIdentifier)
    {
        flightIdentifier = newFlightIdentifier;
    }

    public int getFlightIdentifier()
    {
        return (flightIdentifier);
    }

    public boolean getCallawayFlight()
    {
        return (isCallaway);
    }

    public void setCallawayFlight(boolean callawayFlight)
    {
        isCallaway = callawayFlight;
    }

    Flight(int newFlightIdentifier, boolean callawayFlight)
    {
        flightIdentifier = newFlightIdentifier;
        isCallaway = callawayFlight;
        teamList = new ArrayList<Team>();
    }
}
