package nautilus.game.arcade.kit.perks.data;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class WoolBombData
{
	public Block Block;
	public long Time;
	public Material Material;
	public byte Data;
	
	@SuppressWarnings("deprecation")
	public WoolBombData(Block block)
	{
		Block = block;
		Material = block.getType();
		Data = block.getData();
		
		Time = System.currentTimeMillis();
	}

	@SuppressWarnings("deprecation")
	public void restore()
	{
		Block.setTypeIdAndData(Material.getId(), Data, true);
	}
}
