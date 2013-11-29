package tournamentmanager;

public final class Player
{
    private String lastName, firstName;
    private float handicapIndex, skinsWinnings;
    private boolean noHandicap, skinsParticipant;
    private Sexes sex;
    private int ESCScore;
    private int[] strokesPerHole;

    public void setSkinsWinnings(float newSkinsWinnings)
    {
        skinsWinnings = newSkinsWinnings;
    }

    public float getSkinsWinnings()
    {
        return (skinsWinnings);
    }

    public void setStrokesPerHole(int[] newStrokesPerHole)
    {
        strokesPerHole = newStrokesPerHole.clone();
    }

    public int[] getStrokesPerHole()
    {
        return (strokesPerHole);
    }

    public boolean getSkinsParticipant()
    {
        return (skinsParticipant);
    }

    public void setSkinsParticipant(boolean newSkinsParticipant)
    {
        skinsParticipant = newSkinsParticipant;
    }

    public boolean getNoHandicap()
    {
        return (noHandicap);
    }

    public void setNoHandicap(boolean isNoHandicap)
    {
        noHandicap = isNoHandicap;
    }

    public String getLastName()
    {
        return (lastName);
    }

    public String getFirstName()
    {
        return (firstName);
    }

    public float getHandicapIndex()
    {
        return (handicapIndex);
    }

    public Sexes getSex()
    {
        return (sex);
    }

    public void setName(String newLastName, String newFirstName)
    {
        lastName = newLastName;
        firstName = newFirstName;
    }

    public void setSex(Sexes newSex)
    {
        sex = newSex;
    }

    public void setHandicapIndex(float newHandicapIndex)
    {
        handicapIndex = newHandicapIndex;
    }

    public void setESCScore(int newESCScore)
    {
        ESCScore = newESCScore;
    }

    public int getESCScore()
    {
        return (ESCScore);
    }

    Player(String newLastName, String newFirstName, Sexes newSex, float newHandicapIndex, boolean isNoHandicap, boolean isSkinsParticipant)
    {
        setName(newLastName, newFirstName);
        setSex(newSex);
        setHandicapIndex(newHandicapIndex);
        setNoHandicap(isNoHandicap);
        setSkinsParticipant(isSkinsParticipant);
    }
}
