package base;
import base.player.*;

public class Bowlers_v extends Role{
    protected int overs;
    protected int wickets;
    protected int fivewickets;

    @Override
    public void setMatches(int matches){this.matches = matches;}
    @Override
    public int getMatches(){return this.matches;}

    public void setOvers(int overs){this.overs=overs;}
    public int getOvers(){return this.overs;}

    public void setWickets(int wickets){this.wickets=wickets;}
    public int getWickets(){return this.wickets;}

    public void setFivewickets(int fivewickets){this.fivewickets=fivewickets;}

    public int getFivewickets(){return this.fivewickets;}
}
