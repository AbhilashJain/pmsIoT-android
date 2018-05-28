package com.hcl.pmsiot.data;


public class BuildingBoundary
{
    private Boudary[] boudary;

    private String _id;

    private String name;

    private String capacity;

    public Boudary[] getBoudary ()
    {
        return boudary;
    }

    public void setBoudary (Boudary[] boudary)
    {
        this.boudary = boudary;
    }

    public String get_id ()
    {
        return _id;
    }

    public void set_id (String _id)
    {
        this._id = _id;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getCapacity ()
    {
        return capacity;
    }

    public void setCapacity (String capacity)
    {
        this.capacity = capacity;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [boudary = "+boudary+", _id = "+_id+", name = "+name+", capacity = "+capacity+"]";
    }
}