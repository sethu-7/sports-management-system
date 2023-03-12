package base;
import base.player.*;

public class Wicketkeepers_v extends Role{
    protected int catches;
    protected int stumpings;

    @Override
    public void setMatches(int matches){this.matches = matches;}
    @Override
    public int getMatches(){return this.matches;}

    public void setCatches(int catches){this.catches=catches;}
    public int getCatches(){return this.catches;}

    public void setStumpings(int stumpings){this.stumpings=stumpings;}
    public int getStumpings(){return this.stumpings;}
}
