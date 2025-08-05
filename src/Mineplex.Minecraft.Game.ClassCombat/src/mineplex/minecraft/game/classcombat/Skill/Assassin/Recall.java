package mineplex.minecraft.game.classcombat.Skill.Assassin;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class Recall extends Skill
{
	private HashMap<Player, Item> _items = new HashMap<Player, Item>();
	private HashMap<Player, Long> _time = new HashMap<Player, Long>();
	private HashMap<Player, Long> _informed = new HashMap<Player, Long>();
	
	public Recall(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Place a recall marker on the ground.",
				"Use recall again within 20 seconds",
				"to return to original location."
				});
	}

	@Override
	public String GetEnergyString()
	{
		return "Energy: 80";
	}
	
	@Override
	public String GetRechargeString()
	{
		return "Recharge: 1 Minute";
	}
	
	@EventHandler
	public void Crouch(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();
		
		int level = GetLevel(player);
		if (level == 0)		return;
		
		if (!UtilGear.isWeapon(event.getItemDrop().getItemStack()))
			return;

		event.setCancelled(true);

		//Mark
		if (!_items.containsKey(player))
		{
			//Check Energy - DO NOT USE YET
			if (!Factory.Energy().Use(player, GetName(level), 80, false, true))
				return;

			//Use Recharge
			if (!Recharge.Instance.use(player, GetName(), GetName(level), 60000, true))
				return;

			//Use Energy
			Factory.Energy().Use(player, GetName(level), 80, true, true);
			
			//Item
			Item item = player.getWorld().dropItem(player.getEyeLocation(), ItemStackFactory.Instance.CreateStack(2261));
			item.setVelocity(new Vector(0, -1, 0));
			
			//Save
			_items.put(player, item);
			_time.put(player, System.currentTimeMillis() + 20000);
			_informed.put(player, 20000L);
			
			//Inform
			UtilPlayer.message(player, F.main(GetClassType().name(), "You prepared " + F.skill(GetName(level)) + "."));
			
			//Effect
			player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 133);
			
		}
		//Return
		else
		{
			//Effect
			player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 133);
			player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 2f, 2f);
			
			//Return
			Item item = _items.remove(player);
			Factory.Teleport().TP(player, item.getLocation());
			item.remove();
			
			
			//Inform
			UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
			
			//Effect
			player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 133);
			player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 2f, 2f);
			
			Reset(player);
			
		}
	}
	
	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;
		
		HashSet<Player> expired = new HashSet<Player>();
		
		for (Player cur : _time.keySet())
		{
			if (System.currentTimeMillis() > _time.get(cur))
				expired.add(cur);
			else
			{
				long time = (_time.get(cur) - System.currentTimeMillis());
				if (time < _informed.get(cur))
				{
					UtilPlayer.message(cur, F.main(GetClassType().name(), 
							"Recall Time: " + F.time(UtilTime.convertString(_informed.get(cur), 0, TimeUnit.FIT))));
					_informed.put(cur, _informed.get(cur) - 5000);
				}		
			}
		}
			
		
		for (Player cur : expired)
		{
			_time.remove(cur);
			_informed.remove(cur);
			
			Item item = _items.remove(cur);
			if (item != null)	
			{
				item.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, 133);
				item.remove();
				
				//Inform
				UtilPlayer.message(cur, F.main(GetClassType().name(), 
						"Recall Time: " + C.cRed + "Expired"));
			}	
		}
	}
	
	@EventHandler
	public void ItemPickup(PlayerPickupItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (_items.containsValue(event.getItem()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void HopperPickup(InventoryPickupItemEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (_items.containsValue(event.getItem()))
			event.setCancelled(true);
	}
	
	@Override
	public void Reset(Player player) 
	{
		if (_items.containsKey(player))
			_items.get(player).remove();
		
		_items.remove(player);
		_time.remove(player);
		_informed.remove(player);
	}
}
