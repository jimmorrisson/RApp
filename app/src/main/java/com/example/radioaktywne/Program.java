package com.example.radioaktywne;

public class Program {
    private String name;
    private String dateTime;
    private String host;

    public Program(String name, String dateTime, String host)
    {
        this.name = name;
        this.dateTime = dateTime;
        this.host = host;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String getDateTime()
    {
        return dateTime;
    }

    public String getHost()
    {
        return host;
    }
}
