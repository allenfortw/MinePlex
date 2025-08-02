package me.chiss.Core.Modules;

import java.util.HashSet;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_6_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;

public class BlockRegenerateData 
{
	private Location _loc;
	private int _id;
	private byte _data;
	
	public BlockRegenerateData(Location loc, int id, byte data)
	{
		_loc = loc;
		_id = id;
		_data = data;
	}
	
	public Location GetBlock()
	{
		return _loc;
	}
	
	public int GetId()
	{
		return _id;
	}
	
	public byte GetData()
	{
		return _data;
	}

	public void Restore(HashSet<Chunk> _chunks) 
	{
		_chunks.add(_loc.getChunk());
		QuickRestoreBlock();
	}
	
	public void QuickRestoreBlock()
    {
		//if (_loc.getBlock().getType() == Material.CHEST)
		//	return;
			
        net.minecraft.server.v1_6_R2.Chunk c = ((CraftChunk)_loc.getChunk()).getHandle();
   
        c.a(_loc.getBlockX() & 0xF, _loc.getBlockY(), _loc.getBlockZ() & 0xF, _id, _data);
        ((CraftWorld)_loc.getChunk().getWorld()).getHandle().notify(_loc.getBlockX(), _loc.getBlockY(), _loc.getBlockZ());
    }

	public void RestoreSlow() 
	{
		_loc.getBlock().setTypeIdAndData(_id, _data, true);
	}
}
