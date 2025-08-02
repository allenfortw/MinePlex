package mineplex.minecraft.game.classcombat.Skill.Shifter;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.F;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class TreeShift extends SkillActive
{
	private HashMap<Location, Long> trees = new HashMap<Location, Long>();

	public TreeShift(SkillFactory skills, String name, ClassType classType, SkillType skillType, 
			int cost, int levels, 
			int energy, int energyMod, 
			long recharge, long rechargeMod, boolean rechargeInform, 
			Material[] itemArray, 
			Action[] actionArray) 
	{
		super(skills, name, classType, skillType,
				cost, levels,
				energy, energyMod, 
				recharge, rechargeMod, rechargeInform, 
				itemArray,
				actionArray);

		SetDesc(new String[] 
				{
				"Creates an illusionary tree at target location.",
				"Other players cannot attack or see through it.",
				"You view it as a sapling, and can attack over it.",
				"Lasts 2 + 2pL seconds."
				});
	}

	@Override
	public boolean CustomCheck(Player player, int level) 
	{
		if (player.getLocation().getBlock().getTypeId() == 8 || player.getLocation().getBlock().getTypeId() == 9)
		{
			UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
			return false;
		}

		Block block = player.getTargetBlock(null, 0);
		if (UtilMath.offset(block.getLocation().add(0.5, 0.5, 0.5), player.getLocation()) > 8 + level)
		{
			UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " so far away."));
			return false;
		}

		return true;
	}

	@Override
	public void Skill(Player player, int level) 
	{
		HashMap<Location, Material> tree = new HashMap<Location, Material>();

		Block block = player.getTargetBlock(null, 0);

		//Show to Player
		final Player fPlayer = player;
		final Location fLoc = block.getLocation().add(0, 1, 0);
		UtilServer.getServer().getScheduler().scheduleSyncDelayedTask(Factory.GetPlugin(), new Runnable()
		{
			public void run()
			{
				fPlayer.sendBlockChange(fLoc, 6, (byte)0);
			}
		}, 0);
		
		
		
		//Generate Tree
		for (int i=0 ; i<6 ; i++)
		{
			block = block.getRelative(BlockFace.UP);

			if (block.getTypeId() != 0)
			{
				block = block.getRelative(BlockFace.DOWN);
				break;
			}

			tree.put(block.getLocation(), Material.LOG);
		}

		if (tree.size() > 5)
		{
			for (Block leaf : UtilBlock.getInRadius(block.getLocation(), 2.5d).keySet())
			{
				if (!tree.containsKey(leaf.getLocation()) && leaf.getTypeId() == 0)
				{
					tree.put(leaf.getLocation(), Material.LEAVES);
				}
			}
		}
		
		
		//Show Tree
		for (Location loc : tree.keySet())
		{
			trees.put(loc, System.currentTimeMillis() + (2000 + (2000 * level)));
			
			for (Player other : player.getWorld().getPlayers())
			{
				if (other.equals(player))
					continue;

				other.sendBlockChange(loc, tree.get(loc), (byte)0);
				
				if (tree.get(loc) == Material.LOG)
					other.playEffect(loc, Effect.STEP_SOUND, 17);
			}
		}
			
		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
	}

	@EventHandler
	public void Detree(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;
		
		if (trees.isEmpty())
			return;
		
		HashSet<Location> remove = new HashSet<Location>();
		
		for (Location loc : trees.keySet())
		{
			if (System.currentTimeMillis() > trees.get(loc))
				remove.add(loc);
		}
		
		for (Location loc : remove)
		{
			for (Player player : loc.getWorld().getPlayers())
				player.sendBlockChange(loc, 0, (byte)0);
			
			trees.remove(loc);
		}
	}
	
	@Override
	public void Reset(Player player) 
	{

	}
}
