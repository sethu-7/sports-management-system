package base.player;


public class Player_v {
    protected int id;
    protected String name;
    protected int year;
    protected int salary;
    protected String role;

    public void setName(String name)
    {
        this.name=name;
    }
    public String getName()
    {
        return this.name;
    }


    public void setRole(String role){this.role=role;}
    public String getRole()
    {
        return this.role;
    }

    public void setYear(int year){
        this.year=year;
    }
    public int getYear(){
        return this.year;
    }

    public void setSalary(int salary){
        this.salary=salary;
    }
    public int getSalary(){
        return this.salary;
    }

    public void setId(int id){
        this.id=id;
    }
    public int getId(){
        return this.id;
    }
}
