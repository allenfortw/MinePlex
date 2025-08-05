package nautilus.game.arcade.game.games.hideseek.forms;

import org.bukkit.block.Block;

public class InfestedData
{
	public Block Block;
	public org.bukkit.Material Material;
	public byte Data;
	
	@SuppressWarnings("deprecation")
	public InfestedData(Block block)
	{
		Block = block;
		Material = block.getType();
		Data = block.getData();
		
		block.setType(org.bukkit.Material.AIR);
	}
	
	@SuppressWarnings("deprecation")
	public void restore()
	{
		Block.setTypeIdAndData(Material.getId(), Data, true);
	}
}
