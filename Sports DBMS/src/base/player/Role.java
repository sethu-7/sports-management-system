package base.player;

public abstract class Role{
    public Player_v player;
    protected int matches;

    public abstract void setMatches(int matches);

    public abstract int getMatches();
}
