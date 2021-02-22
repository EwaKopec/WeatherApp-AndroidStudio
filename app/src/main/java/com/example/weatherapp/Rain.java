package com.example.weatherapp;

public class Rain {
    private String onehour;

    public String get1h ()
    {
        return onehour;
    }

    public void set1h (String onehour)
    {
        this.onehour = onehour;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [1h = "+onehour+"]";
    }
}
