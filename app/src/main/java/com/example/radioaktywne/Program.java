package com.example.radioaktywne;

public class Program {
    private String name;
    private String dateTime;
    private String host;
    private String description;

    public Program(String name, String dateTime, String host, String description)
    {
        this.name = name;
        this.dateTime = dateTime;
        this.host = host;
        this.description = description;
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

    public String getDescription() { return description; }
}
