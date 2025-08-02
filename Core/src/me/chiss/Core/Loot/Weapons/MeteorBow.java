package me.chiss.Core.Loot.Weapons;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import me.chiss.Core.Loot.LootBase;
import me.chiss.Core.Loot.LootFactory;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilEvent.ActionType;

public class MeteorBow extends LootBase
{
	private HashSet<Entity> _arrows = new HashSet<Entity>();

	public MeteorBow(LootFactory factory) 
	{
		super(factory, "Meteor Bow", new String[] 
				{
				"",
				C.cGray + "Damage: " + C.cYellow + "10 (AoE)",
				C.cGray + "Passive: " + C.cYellow + "Explosive Arrows",
				"",
				C.cWhite + "The mythical bow that reigned down",
				C.cWhite + "hell from the heavens. Each shot",
				C.cWhite + "is as deadly as a meteor.",
				""
				},
				Material.BOW, ActionType.L, 2);
	}

	@Override
	@EventHandler
	public void DamageTrigger(CustomDamageEvent event) 
	{
		if (event.IsCancelled())
			return;

		Projectile proj = event.GetProjectile();
		if (proj == null)	return;

		Player damager = event.GetDamagerPlayer(true);
		if (damager == null)	return;

		if (!GetUsers().contains(damager))
			return;

		event.SetCancelled("Meteor Shot");
	}

	@Override
	public void Damage(CustomDamageEvent event) 
	{

	}

	@EventHandler
	public void ProjectileShoot(EntityShootBowEvent event) 
	{
		if (!GetUsers().contains(event.getEntity()))
			return;
		
		_arrows.add(event.getProjectile());
	}

	@EventHandler
	public void ProjectileHit(ProjectileHitEvent event)
	{
		Projectile proj = event.getEntity();

		if (proj.getShooter() == null)
			return;

		if (!(proj.getShooter() instanceof Player))
			return;

		Player damager = (Player)proj.getShooter();

		if (!_arrows.contains(proj) && !GetUsers().contains(damager))
			return;

		//Condition
		for (Player cur : UtilPlayer.getNearby(proj.getLocation(), 6))
			Factory.Condition().Factory().Explosion(C.mLoot + "Meteor Arrow", cur, damager, 10, 1, false, true);

		proj.getWorld().createExplosion(proj.getLocation(), 1.6f);
		proj.remove();
	}

	@Override
	public void Ability(PlayerInteractEvent event) 
	{

	}
	
	@EventHandler
	public void Clean(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;
		
		HashSet<Entity> remove = new HashSet<Entity>();

		for (Entity cur : _arrows)
			if (cur.isDead() || !cur.isValid())
				remove.add(cur);

		for (Entity cur : remove)
			_arrows.remove(cur);
	}

	@Override
	public void ResetCustom(Player player)
	{

	}
}
