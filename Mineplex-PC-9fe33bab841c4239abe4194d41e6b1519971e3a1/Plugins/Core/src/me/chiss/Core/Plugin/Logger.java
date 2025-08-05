package me.chiss.Core.Plugin;

public class Logger implements ILogger
{
    public void Log(String source, String message)
    {
        System.out.println("[" + source + "]" + " " + message);
    }
}
