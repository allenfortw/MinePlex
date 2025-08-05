package me.chiss.Core.Modules;

import java.util.HashSet;

import me.chiss.Core.Module.AModule;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockRegenerate extends AModule
{
	public HashSet<BlockRegenerateSet> _blockSets = new HashSet<BlockRegenerateSet>();
	
	public BlockRegenerate(JavaPlugin plugin) 
	{
		super("Block Regenerate", plugin);
	}

	@Override
	public void enable() 
	{

	}

	@Override
	public void disable() 
	{
		for (BlockRegenerateSet sets : _blockSets)
			for (int i=sets.GetBlocks().size() - 1 ; i>=0 ; i--)
				sets.GetBlocks().get(i).RestoreSlow();
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
	
	public BlockRegenerateSet CreateSet(int blocksPerTick)
	{
		BlockRegenerateSet newSet = new BlockRegenerateSet(blocksPerTick);
		
		_blockSets.add(newSet);
		
		return newSet;
	}
	
	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;
		
		HashSet<BlockRegenerateSet> remove = new HashSet<BlockRegenerateSet>();
		
		for (BlockRegenerateSet set : _blockSets)
			if (set.IsRestoring())
				for (int i=0 ; i<set.GetRate() ; i++)
				{
					if (!set.RestoreNext())
					{
						remove.add(set);
						break;
					}
				}
	}
}
