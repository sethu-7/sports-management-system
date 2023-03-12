package base;
import base.player.*;

public class Batsmen_v extends Role{
    protected int runs;
    protected int halfcenturies;
    protected int centuries;

    @Override
    public void setMatches(int matches){this.matches = matches;}
    @Override
    public int getMatches(){return this.matches;}

    public void setRuns(int runs){this.runs = runs;}
    public int getRuns(){return this.runs;}

    public void setHalfcenturies(int halfcenturies){this.halfcenturies = halfcenturies;}
    public int getHalfcenturies(){return this.halfcenturies;}

    public void setCenturies(int centuries){this.centuries = centuries;}
    public int getCenturies(){return this.centuries;}

}
