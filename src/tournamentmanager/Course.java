package tournamentmanager;

public final class Course
{
    private int mensSlope, womensSlope;
    private float mensRating, womensRating;
    private String mensTees, womensTees, courseName;
    private int[] mensHoleHandicap, womensHoleHandicap, mensPar, womensPar;
    private int[] mensYardage, womensYardage;
    Course(String newCourseName, int newMensSlope, int newWomensSlope, float newMensRating, float newWomensRating, String newMensTees, String newWomensTees, int[] newMensHoleHandicap, int[] newWomensHoleHandicap, int[] newMensPar, int[] newWomensPar, int[] newMensYardage, int[] newWomensYardage)
    {
        setCourseName(newCourseName);
        setMensTees(newMensTees);
        setWomensTees(newWomensTees);
        setMensHoleHandicap(newMensHoleHandicap);
        setWomensHoleHandicap(newWomensHoleHandicap);
        setMensPar(newMensPar);
        setWomensPar(newWomensPar);
        setMensYardage(newMensYardage);
        setWomensYardage(newWomensYardage);
    }

    public int[] getMensPar()
    {
        return (mensPar);
    }

    public void setMensPar(int[] newMensPar)
    {
        mensPar = newMensPar.clone();
    }

    public int[] getWomensPar()
    {
        return (womensPar);
    }

    public void setWomensPar(int[] newWomensPar)
    {
        womensPar = newWomensPar.clone();
    }

    public int[] getMensYardage()
    {
        return (mensYardage);
    }

    public void setMensYardage(int[] newMensYardage)
    {
        mensYardage = newMensYardage.clone();
    }

    public int[] getWomensYardage()
    {
        return (womensYardage);
    }

    public void setWomensYardage(int[] newWomensYardage)
    {
        womensYardage = newWomensYardage.clone();
    }

    public int[] getWomensHoleHandicap()
    {
        return (womensHoleHandicap);
    }

    public void setWomensHoleHandicap(int[] newWomensHoleHandicap)
    {
        womensHoleHandicap = newWomensHoleHandicap.clone();
    }

    public int[] getMensHoleHandicap()
    {
        return (mensHoleHandicap);
    }

    public void setMensHoleHandicap(int[] newMensHoleHandicap)
    {
        mensHoleHandicap = newMensHoleHandicap.clone();
    }

    public int calculateCourseHandicap(Sexes sex, float handicapIndex, boolean mixed)
    {
        int courseHandicap = 0;

        /* Limit the maximum allowable handicap index for men. */
        if (sex == Sexes.male)
            if (handicapIndex > 36.4)
                handicapIndex = (float)36.4;

        /* Limit the maximmum allowable handicap index for women. */
        if (sex == Sexes.female)
            if (handicapIndex > 40.4)
                handicapIndex = (float)40.4;

        if (mixed == false)
        {
            if (sex == Sexes.male)
                courseHandicap = Math.round(handicapIndex * mensSlope / 113);

            if (sex == Sexes.female)
                courseHandicap = Math.round(handicapIndex * womensSlope / 113);
        }
        else /* Men and women playing together */
        {
            if (mensRating > womensRating)
            {
                if (sex == Sexes.male)
                    courseHandicap = Math.round(handicapIndex * mensSlope / 113) + Math.round(mensRating - womensRating);

                if (sex == Sexes.female)
                    courseHandicap = Math.round(handicapIndex * womensSlope / 113);
            }
            else if (womensRating > mensRating)
            {
                if (sex == Sexes.male)
                    courseHandicap = Math.round(handicapIndex * mensSlope / 113);

                if (sex == Sexes.female)
                    courseHandicap = Math.round(handicapIndex * womensSlope / 113) + Math.round(womensRating - mensRating);
            }
            else
            {
                if (sex == Sexes.male)
                    courseHandicap = Math.round(handicapIndex * mensSlope / 113);

                if (sex == Sexes.female)
                    courseHandicap = Math.round(handicapIndex * womensSlope / 113);
            }
        }

        return (courseHandicap);
    }

    public int mensTotalPar()
    {
        int totalPar = 0;

        for (int holeIndex = 0; holeIndex < mensPar.length; holeIndex++)
            totalPar += mensPar[holeIndex];

        return (totalPar);
    }

    public int womensTotalPar()
    {
        int totalPar = 0;

        for (int holeIndex = 0; holeIndex < womensPar.length; holeIndex++)
            totalPar += womensPar[holeIndex];

        return (totalPar);

    }

    public String getCourseName()
    {
        return (courseName);
    }

    public int getMensSlope()
    {
        return (mensSlope);
    }

    public int getWomensSlope()
    {
        return (womensSlope);
    }

    public float getMensRating()
    {
        return (mensRating);
    }

    public float getWomensRating()
    {
        return (womensRating);
    }

    public String getMensTees()
    {
        return (mensTees);
    }

    public String getWomensTees()
    {
        return (womensTees);
    }

    public void setMensSlope(int slope)
    {
        mensSlope = slope;
    }

    public void setWomensSlope(int slope)
    {
        womensSlope = slope;
    }

    public void setMensRating(float rating)
    {
        mensRating = rating;
    }

    public void setWomensRating(float rating)
    {
        womensRating = rating;
    }

    public void setMensTees(String tees)
    {
        mensTees = tees;
    }

    public void setWomensTees(String tees)
    {
        womensTees = tees;
    }

    public void setCourseName(String name)
    {
        courseName = name;
    }
}
