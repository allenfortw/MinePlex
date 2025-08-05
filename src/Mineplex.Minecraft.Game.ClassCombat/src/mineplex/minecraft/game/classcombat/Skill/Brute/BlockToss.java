package mineplex.minecraft.game.classcombat.Skill.Brute;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.F;
import mineplex.core.projectile.IThrown;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillEvent;

public class BlockToss extends mineplex.minecraft.game.classcombat.Skill.Skill implements IThrown
{
	private HashMap<Player, FallingBlock> _holding = new HashMap<Player, FallingBlock>();
	private HashMap<Player, Long> _charge = new HashMap<Player, Long>();
	private HashSet<Player> _charged = new HashSet<Player>();
	private HashMap<FallingBlock, Player> _falling = new HashMap<FallingBlock, Player>();

	public BlockToss(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Hold Block to pick up a block,",
				"Release Block to throw it, dealing",
				"up to 12 damage.",
				"",
				"You must hold the block for",
				"1 second for full throw power.",
				"",
				"You can only pick up Stone, Dirt,",
				"Cobblestone, Sand, Gravel or Snow."
				});
	}

	@Override
	public String GetEnergyString()
	{
		return "Energy: 40";
	}

	@EventHandler
	public void Grab(PlayerInteractEvent event)
	{	 	
		Player player = event.getPlayer();

		if (!UtilEvent.isAction(event, ActionType.R_BLOCK))
			return;

		if (!UtilGear.isSword(player.getItemInHand()))
			return;

		if (_holding.containsKey(player))
			return;

		//Level
		int level = GetLevel(player);
		if (level == 0)		return;

		Block grab = event.getClickedBlock();

		int id = event.getClickedBlock().getTypeId();

		if (
				id != 1 &&
				id != 2 &&
				id != 3 &&
				id != 12 &&
				id != 13 &&
				id != 80) 
			return;

		//Door
		if (grab.getRelative(BlockFace.UP).getTypeId() == 64 || grab.getRelative(BlockFace.UP).getTypeId() == 71)
		{
			UtilPlayer.message(player, F.main(GetName(), "You cannot grab this block."));
			return;
		}

		//TrapDoor
		if (	grab.getRelative(BlockFace.NORTH).getType() == Material.TRAP_DOOR ||
				grab.getRelative(BlockFace.SOUTH).getType() == Material.TRAP_DOOR ||
				grab.getRelative(BlockFace.EAST).getType() 	== Material.TRAP_DOOR ||
				grab.getRelative(BlockFace.WEST).getType() 	== Material.TRAP_DOOR)
		{
			UtilPlayer.message(player, F.main(GetName(), "You cannot grab this block."));
			return;
		}

		//Energy
		if (!Factory.Energy().Use(player, GetName(level), 40, true, true))
			return;

		//Block to Item
		FallingBlock block  = player.getWorld().spawnFallingBlock(player.getEyeLocation(), event.getClickedBlock().getType(), (byte)0);
		Factory.BlockRestore().Add(event.getClickedBlock(), 0, (byte)0, 10000);

		//Condition Indicators
		Factory.Condition().SetIndicatorVisibility(player, false);

		//Action
		player.eject();
		player.setPassenger(block);
		_holding.put(player, block);
		_falling.put(block, player);
		_charge.put(player, System.currentTimeMillis());

		//Effect
		player.getWorld().playEffect(event.getClickedBlock().getLocation(), Effect.STEP_SOUND, block.getMaterial().getId());
	}

	@EventHandler
	public void Throw(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		HashSet<Player> voidSet = new HashSet<Player>();
		HashSet<Player> throwSet = new HashSet<Player>();

		for (Player cur : _holding.keySet())
		{
			if (cur.getPassenger() == null)
			{
				voidSet.add(cur);
				continue;	
			}

			if (_holding.get(cur).getVehicle() == null)
			{
				voidSet.add(cur);
				continue;	
			}

			if (!_holding.get(cur).getVehicle().equals(cur))
			{
				voidSet.add(cur);
				continue;	
			}

			//Throw
			if (!cur.isBlocking())
				throwSet.add(cur);

			//Charged Tick
			if (!_charged.contains(cur))
				if (System.currentTimeMillis() - _charge.get(cur) > 1000)
				{
					_charged.add(cur);
					cur.playEffect(cur.getLocation(), Effect.CLICK1, 0);
				}
		}

		for (Player cur : voidSet)
		{
			FallingBlock block = _holding.remove(cur);
			_charge.remove(cur);
			_charged.remove(cur);
			block.remove();
		}

		for (Player cur : throwSet)
		{
			FallingBlock block = _holding.remove(cur);
			_charged.remove(cur);
			long charge = System.currentTimeMillis() - _charge.remove(cur);

			//Throw 
			cur.eject();
			double mult = 1;
			if (charge < 1000)
				mult = mult * (charge/1000d);

			//Condition Indicators
			Factory.Condition().SetIndicatorVisibility(cur, true);

			//Action
			UtilAction.velocity(block, cur.getLocation().getDirection(), mult, false, 0, 0, 1, true);
			Factory.Projectile().AddThrow(block, cur, this, -1, true, true, true, 
					null, 0, 0, null, 0, UpdateType.FASTEST, 2d);

			//Boost
			UtilAction.velocity(cur, cur.getLocation().getDirection().multiply(-1), 0.4, false, 0, 0, 1, false);
			
			//Event
			UtilServer.getServer().getPluginManager().callEvent(new SkillEvent(cur, GetName(), ClassType.Brute));
		}
	}

	@Override
	public void Collide(LivingEntity target, Block block, ProjectileUser data) 
	{
		if (target == null)
			return;

		//Damage Event
		Factory.Damage().NewDamageEvent(target, data.GetThrower(), null, 
				DamageCause.CUSTOM, 2 + (data.GetThrown().getVelocity().length() * 10), true, true, false,
				UtilEnt.getName(data.GetThrower()), GetName());

		//Block to Item
		if (data.GetThrown() instanceof FallingBlock)
		{
			FallingBlock thrown = (FallingBlock) data.GetThrown();

			FallingBlock newThrown  = data.GetThrown().getWorld().spawnFallingBlock(data.GetThrown().getLocation(), thrown.getMaterial(), (byte)0);

			//Remove Old
			_falling.remove(thrown);
			thrown.remove();

			//Add New
			if (data.GetThrower() instanceof Player)
				_falling.put(newThrown, (Player)data.GetThrower());
		}
	}

	@Override
	public void Idle(ProjectileUser data) 
	{

	}

	@Override
	public void Expire(ProjectileUser data) 
	{

	}	

	@EventHandler
	public void CreateBlock(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		HashSet<FallingBlock> fallen = new HashSet<FallingBlock>();

		for (FallingBlock cur : _falling.keySet())
		{
			if (cur.isDead() || !cur.isValid())
				fallen.add(cur);
		}

		for (FallingBlock cur : fallen)
		{
			_falling.remove(cur);
			Block block = cur.getLocation().getBlock();
			
			int id = block.getTypeId();
			if (
					id != 1 &&
					id != 2 &&
					id != 3 &&
					id != 4 &&
					id != 12 &&
					id != 13 &&
					id != 80) 
				continue;

			block.setTypeIdAndData(0, (byte)0, false);
			
			//Block Replace
			Factory.BlockRestore().Add(block, cur.getBlockId(), (byte)0, 10000);

			//Effect
			block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
		}
	}

	@EventHandler
	public void ItemSpawn(ItemSpawnEvent event)
	{
		int id = event.getEntity().getItemStack().getTypeId();

		if (
				id != 1 &&
				id != 2 &&
				id != 3 &&
				id != 4 &&
				id != 12 &&
				id != 13 &&
				id != 80) 
			return;

		for (FallingBlock block : _falling.keySet())
			if (UtilMath.offset(event.getEntity().getLocation(), block.getLocation()) < 1)
				event.setCancelled(true);	
	}

	@Override
	public void Reset(Player player) 
	{
		if (_holding.containsKey(player))
		{
			player.eject();
		}
		
		_holding.remove(player);
		_charge.remove(player);
		_charged.remove(player);
	}
}
