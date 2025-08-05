package me.chiss.Core.Modules;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_6_R2.CraftChunk;

public class BlockRegenerateSet 
{
	private boolean _restore = false;
	private int _blocksPerTick = 10;
	private int _index = 0;

	private ArrayList<BlockRegenerateData> _blocks = new ArrayList<BlockRegenerateData>();
	private HashSet<Chunk> _chunks = new HashSet<Chunk>();

	public BlockRegenerateSet(int blocksPerTick)
	{
		_blocksPerTick = blocksPerTick;
	}

	public void AddBlock(Location loc, int id, byte data)
	{
		if (!_restore)
			_blocks.add(new BlockRegenerateData(loc, id, data));
	}

	public void Start()
	{
		_restore = true;
		_index = _blocks.size() - 1;
	}

	public int GetRate()
	{
		return _blocksPerTick;
	}

	public boolean IsRestoring()
	{
		return _restore;
	}

	public boolean RestoreNext() 
	{
		if (_index < 0)
		{
			LightChunks();
			return false;
		}

		_blocks.get(_index).Restore(_chunks);
		_index--;

		return true;
	}

	private void LightChunks()
	{
		for (Chunk chunk : _chunks)
		{
			net.minecraft.server.v1_6_R2.Chunk c = ((CraftChunk)chunk).getHandle();
			c.initLighting();
		}
	}

	public ArrayList<BlockRegenerateData> GetBlocks()
	{
		return _blocks;
	}
}
