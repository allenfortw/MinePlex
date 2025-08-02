package me.chiss.Core.Modules;

import me.chiss.Core.Module.AModule;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Logger extends AModule
{
	public Logger(JavaPlugin plugin) 
	{
		super("Logger", plugin);
	}

	//Module Functions
	@Override
	public void enable() 
	{

	}

	@Override
	public void disable() 
	{

	}

	@Override
	public void config() 
	{

	}
	
	@Override
	public void commands() 
	{
		
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		
	}

	@EventHandler
	public void handleCommand(PlayerCommandPreprocessEvent event) 
	{
	    /*
		String statement = "INSERT INTO log_command (client, command, location, date, systime) VALUES (?, ?, ?, ?, ?)";	

		try 
		{
			PreparedStatement stmt = SQL().prepareStatement(statement, DB.LOCAL);

			stmt.setString(1, event.getPlayer().getName());
			stmt.setString(2, event.getMessage());
			stmt.setString(3, UtilWorld.locToStr(event.getPlayer().getLocation()));
			stmt.setString(4, UtilTime.now());
			stmt.setLong(5, System.currentTimeMillis());
			
			SQL().doStatement(stmt, DB.LOCAL, true);
		} 
		catch (SQLException e) 
		{
			Log("Statement: " + statement);
			Log(DB.LOCAL + " Error: " + e.getMessage());	
		}
		*/
	}

	public void logChat(String type, Player from, String to, String message) 
	{
	    /*
		String statement = "INSERT INTO log_chat (date, type, m_from, m_to, message, location, systime) VALUES (?, ?, ?, ?, ?, ?, ?)";	

		try 
		{
			PreparedStatement stmt = SQL().prepareStatement(statement, DB.LOCAL);

			stmt.setString(1, UtilTime.now());
			stmt.setString(2, type);
			stmt.setString(3, from.getName());
			stmt.setString(4, to);
			stmt.setString(5, message);
			stmt.setString(6, UtilWorld.locToStrClean(from.getLocation()));
			stmt.setLong(7, System.currentTimeMillis());
			
			SQL().doStatement(stmt, DB.LOCAL, true);
		} 
		catch (SQLException e) 
		{
			Log("Statement: " + statement);
			Log(DB.LOCAL + " Error: " + e.getMessage());	
		}
		*/
	}
}
