package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.F;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;

public class ArcticArmor extends Skill
{
	private HashSet<Player> _active = new HashSet<Player>();

	public ArcticArmor(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Drop Axe/Sword to Toggle.",
				"",
				"Create a freezing area around you",
				"in a 5 Block radius. Allies inside",
				"this area receive Protection II.",
				"",
				"You are permanently immune to the",
				"Slowing effect of snow."
				});
	}

	@Override
	public String GetEnergyString()
	{
		return "Energy: 3 per Second";
	}

	@EventHandler
	public void Toggle(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();

		if (GetLevel(player) == 0)				
			return;

		if (!UtilGear.isWeapon(event.getItemDrop().getItemStack()))
			return;

		event.setCancelled(true);

		//Check Allowed
		SkillTriggerEvent trigger = new SkillTriggerEvent(player, GetName(), GetClassType());
		UtilServer.getServer().getPluginManager().callEvent(trigger);
		if (trigger.IsCancelled())
			return;

		if (_active.contains(player))
		{
			Remove(player);	
		}
		else
		{
			if (!Factory.Energy().Use(player, "Enable " + GetName(), 10, true, true))
				return;

			Add(player);
		}
	}

	public void Add(Player player)
	{
		_active.add(player);
		UtilPlayer.message(player, F.main(GetClassType().name(), GetName() + ": " + F.oo("Enabled", true)));
	}

	public void Remove(Player player)
	{
		_active.remove(player);
		UtilPlayer.message(player, F.main(GetClassType().name(), GetName() + ": " + F.oo("Disabled", false)));
		Factory.Condition().EndCondition(player, null, GetName());
	}

	@EventHandler
	public void Audio(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;

		for (Player cur : _active)	
			cur.getWorld().playSound(cur.getLocation(), Sound.AMBIENCE_RAIN, 0.3f, 0f);
	}

	@EventHandler
	public void SnowAura(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player cur : GetUsers())
		{	
			if (!_active.contains(cur))
				continue;

			//Level
			if (GetLevel(cur) == 0)
			{
				Remove(cur);	
				continue;
			}

			
			//Check Allowed
			SkillTriggerEvent trigger = new SkillTriggerEvent(cur, GetName(), GetClassType());
			UtilServer.getServer().getPluginManager().callEvent(trigger);
			if (trigger.IsCancelled())
			{
				Remove(cur);
				continue;
			}
			
			//Energy
			if (!Factory.Energy().Use(cur, GetName(), 0.15, true, true))
			{
				Remove(cur);
				continue;
			}

			//Blocks
			double duration = 2000;
			HashMap<Block, Double> blocks = UtilBlock.getInRadius(cur.getLocation().getBlock().getLocation(), 5d);
			for (Block block : blocks.keySet())
			{			
				//Freeze
				if (!block.getRelative(BlockFace.UP).isLiquid())
					if (block.getLocation().getY() <= cur.getLocation().getY())
						if (block.getTypeId() == 8 || block.getTypeId() == 9 || block.getTypeId() == 79)
							Factory.BlockRestore().Add(block, 79, (byte)0, (long)(duration * (1 + blocks.get(block))));

				//Snow
				Factory.BlockRestore().Snow(block, (byte)0, (byte)0, (long)(duration * (1 + blocks.get(block))), 250, 0);
			}
		}
	}

	@EventHandler
	public void ProtectionAura(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		for (Player cur : _active)
		{				
			//Protection
			for (Player other : UtilPlayer.getNearby(cur.getLocation(), 5))
				if (!Factory.Relation().CanHurt(cur, other) || other.equals(cur))
					Factory.Condition().Factory().Protection(GetName(), other, cur, 1.9, 1, false, true, true);
		}
	}

	@EventHandler
	public void Slow(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player cur : UtilServer.getPlayers())
		{
			if (cur.getLocation().getChunk() == null)
				continue;

			Block block = cur.getLocation().getBlock();

			if (block.getTypeId() != 78)
				continue;

			if (block.getData() == 0)
				continue;

			if (GetLevel(cur) > 0)
				continue;

			int level = 0;
			if (block.getData() == 2 || block.getData() == 3)
				level = 1;
			else if (block.getData() == 4 || block.getData() == 5)
				level = 2;
			else if (block.getData() == 6 || block.getData() == 7)
				level = 3;

			//Slow
			Factory.Condition().Factory().Custom("Thick Snow", cur, cur, 
					ConditionType.SLOW, 1.9, level, false, 
					Material.SNOW_BALL, (byte)0, true);
		}
	}

	@Override
	public void Reset(Player player) 
	{
		_active.remove(player);
	}
}
