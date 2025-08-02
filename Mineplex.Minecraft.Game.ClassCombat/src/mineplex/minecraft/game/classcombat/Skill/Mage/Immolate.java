package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.util.Vector;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.F;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;

public class Immolate extends Skill
{
	private HashSet<Entity> _active = new HashSet<Entity>();

	public Immolate(SkillFactory skills, String name, ClassType classType, SkillType skillType, int cost, int levels) 
	{
		super(skills, name, classType, skillType, cost, levels);

		SetDesc(new String[] 
				{
				"Drop Axe/Sword to Toggle.",
				"",
				"Ignite yourself in flaming fury.",
				"You receive Strength 4, Speed 1,",
				"Fire Resistance and Vulnerability 2.",
				"",
				"You leave a trail of fire, which",
				"burns players that go near it."
				});
	}

	@Override
	public String GetEnergyString()
	{
		return "Energy: 12 per Second";
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
		Factory.Condition().Factory().FireResist(GetName(), player, player, 0.9, 0, false, true, true);
	}

	@EventHandler
	public void Aura(UpdateEvent event)
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
			if (!Factory.Energy().Use(cur, GetName(), 0.6, true, true))
			{
				Remove(cur);
				continue;
			}	

			//Put out Fire
			cur.setFireTicks(0);
		}
	}

	@EventHandler
	public void Combust(EntityCombustEvent event)
	{
		if (_active.contains(event.getEntity()))
			event.setCancelled(true);
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() == UpdateType.FAST)
			Conditions();

		if (event.getType() == UpdateType.TICK)
			Flames();
	}

	public void Conditions()
	{
		for (Player cur : GetUsers())
		{	
			if (!_active.contains(cur))
				continue;

			//Speed + Strength
			Factory.Condition().Factory().Speed(GetName(), cur, cur, 1.9, 0, false, true, true);
			Factory.Condition().Factory().Strength(GetName(), cur, cur, 1.9, 3, false, true, true);
			Factory.Condition().Factory().FireResist(GetName(), cur, cur, 1.9, 0, false, true, true);
			Factory.Condition().Factory().Vulnerable(GetName(), cur, cur, 1.9, 1, false, true, true);
		}
	}

	public void Flames()
	{
		for (Player cur : GetUsers())
		{	
			if (!_active.contains(cur))
				continue;

			//Fire
			Item fire = cur.getWorld().dropItem(cur.getLocation().add(0, 0.5, 0), ItemStackFactory.Instance.CreateStack(Material.FIRE));
			fire.setVelocity(new Vector((Math.random() - 0.5)/3,Math.random()/3,(Math.random() - 0.5)/3));
			Factory.Fire().Add(fire, cur, 1, 0, 1.2, 0, GetName());

			//Sound
			cur.getWorld().playSound(cur.getLocation(), Sound.FIZZ, 0.2f, 1f);
		}
	}

	@Override
	public void Reset(Player player) 
	{
		_active.remove(player);
	}
}
