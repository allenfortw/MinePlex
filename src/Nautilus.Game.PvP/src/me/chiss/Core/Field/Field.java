package me.chiss.Core.Field;


import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.chiss.Core.Module.AModule;
import mineplex.core.server.IRepository;

public class Field extends AModule
{
	private FieldBlock _block;
	private FieldOre _ore;
	private FieldMonster _mob;
	
	public Field(JavaPlugin plugin, IRepository repository, String serverName) 
	{
		super("Field Factory", plugin, repository);
		
		_block = new FieldBlock(plugin, repository, serverName);
		_ore = new FieldOre(plugin, repository, serverName);
		_mob = new FieldMonster(plugin, repository, serverName);
	}

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
	
	public FieldBlock GetBlock()
	{
		return _block;
	}
	
	public FieldOre GetOre()
	{
		return _ore;
	}
	
	public FieldMonster GetMonster()
	{
		return _mob;
	}	
}
