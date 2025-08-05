package me.chiss.Core.Config;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.chiss.Core.Module.AModule;

public class Config extends AModule
{
	public Config(JavaPlugin plugin)
	{
		super("Config", plugin);
	}

	//Configuation
	private HashMap<String, HashMap<String, String>> _moduleConfig = new HashMap<String, HashMap<String, String>>();

	@Override
	public void enable() 
	{
		_moduleConfig.clear();
		/*
		ResultSet rs = _sql.doQuery("SELECT module,variable,value FROM config", DB.LOCAL);

		try
		{
			while (rs.next())
			{
				String module = rs.getString(1);
				String variable = rs.getString(2);
				String value = rs.getString(3);

				//Ensure Module Exists
				if (!_moduleConfig.containsKey(module))
					_moduleConfig.put(module, new HashMap<String, String>());

				//Add Variable
				_moduleConfig.get(module).put(variable, value);
			}

			Log("Configuration Loaded from SQL");
		}
		catch (Exception e)
		{
			Log(DB.LOCAL + " SQL Error: " + e.getMessage());
			Log("SELECT module,variable,value FROM config");
		}*/
	}

	@Override
	public void disable() 
	{
		long epoch = System.currentTimeMillis();
		Log("Saving Config...");

		//writeVars();

		Log("Config Saved. Took " + (System.currentTimeMillis()-epoch) + " milliseconds.");
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

	public void writeVars()
	{
		/*
		//Clear
		_sql.doStatement("DELETE FROM config", DB.LOCAL, false);

		//Write
		for (String curModule : _moduleConfig.keySet())
		{
			for (String curVariable : _moduleConfig.get(curModule).keySet())
			{
				writeVar(curModule, curVariable, _moduleConfig.get(curModule).get(curVariable));
			}
		}*/
	}

	public void writeVar(String module, String variable, String value)
	{/*
		String statement = "REPLACE INTO config (module,variable,value) VALUES (" + 
				_sql.f(module) + ", " + 
				_sql.f(variable) + ", " + 
				_sql.f(value) + ")";

		//Insert
		_sql.doStatement(statement, DB.LOCAL, true);
	*/}

	public void addVar(String module, String variable, String value)
	{
		Log("Adding Variable [" + module + ": " + variable + " = " + value + "]");

		//Ensure Module Exists
		if (!_moduleConfig.containsKey(module))
			_moduleConfig.put(module, new HashMap<String, String>());

		//Add Variable
		_moduleConfig.get(module).put(variable, value);

		writeVar(module, variable, value);
	}

	public String getString(String module, String variable, String def)
	{
		if (!_moduleConfig.containsKey(module))
		{
			Log("Variable Not Found [" + module + ": " + variable + "]");
			addVar(module, variable, def);
			return def;
		}

		HashMap<String, String> varMap = _moduleConfig.get(module);

		if (!varMap.containsKey(variable))
		{
			Log("Variable Not Found [" + module + ": " + variable + "]");
			addVar(module, variable, def);
			return def;
		}

		return varMap.get(variable);
	}

	public int getInt(String module, String variable, int def)
	{
		if (!_moduleConfig.containsKey(module))
		{
			Log("Variable Not Found [" + module + ": " + variable + "]");
			addVar(module, variable, def+"");
			return def;
		}

		HashMap<String, String> varMap = _moduleConfig.get(module);

		if (!varMap.containsKey(variable))
		{
			Log("Variable Not Found [" + module + ": " + variable + "]");
			addVar(module, variable, def+"");
			return def;
		}

		try
		{
			return Integer.parseInt(varMap.get(variable));
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	public long getLong(String module, String variable, long def)
	{
		if (!_moduleConfig.containsKey(module))
		{
			Log("Variable Not Found [" + module + ": " + variable + "]");
			addVar(module, variable, def+"");
			return def;
		}

		HashMap<String, String> varMap = _moduleConfig.get(module);

		if (!varMap.containsKey(variable))
		{
			Log("Variable Not Found [" + module + ": " + variable + "]");
			addVar(module, variable, def+"");
			return def;
		}

		try
		{
			return Long.parseLong(varMap.get(variable));
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	public boolean getBool(String module, String variable, boolean def)
	{
		if (!_moduleConfig.containsKey(module))
		{
			Log("Variable Not Found [" + module + ": " + variable + "]");
			addVar(module, variable, def+"");
			return def;
		}

		HashMap<String, String> varMap = _moduleConfig.get(module);

		if (!varMap.containsKey(variable))
		{
			Log("Variable Not Found [" + module + ": " + variable + "]");
			addVar(module, variable, def+"");
			return def;
		}

		try
		{
			return Boolean.parseBoolean(varMap.get(variable));
		}
		catch (Exception e)
		{
			addVar(module, variable, def+"");
			return def;
		}
	}
}
