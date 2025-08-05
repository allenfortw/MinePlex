package me.chiss.Core.Module;

import java.util.HashSet;

public class ModuleManager 
{
	private HashSet<AModule> _modules;
	
	public ModuleManager()
	{
		_modules = new HashSet<AModule>();
	}

	public void Register(AModule module) 
	{
		_modules.add(module);
	}
	
	public HashSet<AModule> GetAll()
	{
		return _modules;
	}

	public void onDisable() 
	{
		for (AModule module : _modules)
			module.onDisable();
	}
}
