package mineplex.minecraft.game.core.damage;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.npc.NpcManager;
import mineplex.minecraft.game.core.combat.CombatManager;
import mineplex.minecraft.game.core.damage.compatibility.NpcProtectListener;
import net.minecraft.server.v1_6_R2.DamageSource;
import net.minecraft.server.v1_6_R2.EntityHuman;
import net.minecraft.server.v1_6_R2.EntityLiving;

import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftLivingEntity;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class DamageManager extends MiniPlugin
{
	private CombatManager _combatManager;
	private DisguiseManager _disguiseManager;
	protected Field _lastDamageByPlayerTime;
	protected Method _k;

	public boolean UseSimpleWeaponDamage = false;
	public boolean DisableDamageChanges = false;
	
	public DamageManager(JavaPlugin plugin, CombatManager combatManager, NpcManager npcManager, DisguiseManager disguiseManager) 
	{
		super("Damage Manager", plugin);

		_combatManager = combatManager;
		_disguiseManager = disguiseManager;

		try
		{
			_lastDamageByPlayerTime = EntityLiving.class.getDeclaredField("lastDamageByPlayerTime");
			_lastDamageByPlayerTime.setAccessible(true);
			_k = EntityLiving.class.getDeclaredMethod("h", float.class);
			_k.setAccessible(true);
		} 
		catch (final Exception e)
		{
			System.out.println("Problem getting access to EntityLiving: " + e.getMessage());
		}

		RegisterEvents(new NpcProtectListener(npcManager));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void StartDamageEvent(EntityDamageEvent event)
	{
		boolean preCancel = false;
		if (event.isCancelled())
			preCancel = true;

		if (!(event.getEntity() instanceof LivingEntity))
			return;

		//Get Data
		LivingEntity damagee = GetDamageeEntity(event);
		LivingEntity damager = GetDamagerEntity(event, true);
		Projectile projectile = GetProjectile(event);

		if (projectile instanceof Fish)
			return;

		//Pre-Event Modifications
		if (!DisableDamageChanges)
			WeaponDamage(event, damager);

		//New Event
		NewDamageEvent(damagee, damager, projectile, event.getCause(), event.getDamage(), true, false, false, null, null, preCancel);

		//event.setDamage(0);
		//if (GoldPower(damager))
		event.setCancelled(true);
	}

	/*
	private boolean GoldPower(LivingEntity damager) 
	{
		try
		{
			Player player = (Player)damager;
			if (!Util().Gear().isGold(player.getItemInHand()))
				return false;

			if (!player.getInventory().contains(Material.GOLD_NUGGET))
				return false;

			UtilInv.remove(player, Material.GOLD_NUGGET, (byte)0, 1);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	 */

	public void NewDamageEvent(LivingEntity damagee, LivingEntity damager, Projectile proj, 
			DamageCause cause, double damage, boolean knockback, boolean ignoreRate, boolean ignoreArmor,
			String source, String reason)
	{
		NewDamageEvent(damagee, damager, proj, 
				cause, damage, knockback, ignoreRate, ignoreArmor,
				source, reason, false);
	}

	public void NewDamageEvent(LivingEntity damagee, LivingEntity damager, Projectile proj, 
			DamageCause cause, double damage, boolean knockback, boolean ignoreRate, boolean ignoreArmor,
			String source, String reason, boolean cancelled)
	{
		_plugin.getServer().getPluginManager().callEvent(
				new CustomDamageEvent(damagee, damager, proj, cause, damage, 
						knockback, ignoreRate, ignoreArmor, 
						source, reason, cancelled));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void CancelDamageEvent(CustomDamageEvent event)
	{
		if (event.GetDamageeEntity().getHealth() <= 0)
		{
			event.SetCancelled("0 Health");
			return;
		}

		if (event.GetDamageePlayer() != null)
		{
			Player damagee = event.GetDamageePlayer();

			//Not Survival
			if (damagee.getGameMode() != GameMode.SURVIVAL)
			{
				event.SetCancelled("Damagee in Creative");
				return;
			}

			//Limit World Damage Rate
			if (!event.IgnoreRate())
			{
				if (!_combatManager.Get(damagee.getName()).CanBeHurtBy(event.GetDamagerEntity(true)))
				{
					event.SetCancelled("Damage Rate");
					return;
				}
			}
		}

		if (event.GetDamagerPlayer(true) != null)
		{
			Player damager = event.GetDamagerPlayer(true);

			//Not Survival
			if (damager.getGameMode() != GameMode.SURVIVAL)
			{
				event.SetCancelled("Damager in Creative");
				return;
			}

			//Damage Rate
			if (!event.IgnoreRate())
				if (!_combatManager.Get(damager.getName()).CanHurt(event.GetDamageeEntity()))
				{
					event.SetCancelled("Damage Rate");
					return;
				}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void EndDamageEvent(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		if (event.GetDamage() < 1)
			return;

		Damage(event);
	}

	private void Damage(CustomDamageEvent event) 
	{
		if (event.GetDamageeEntity() == null)
			return;
		
		if (event.GetDamageeEntity().getHealth() <= 0)
			return;

		//Player Conditions
		if (event.GetDamageePlayer() != null)
		{
			//Register Damage (must happen before damage)
			_combatManager.AddAttack(event);
		}

		if (event.GetDamagerPlayer(true) != null && event.DisplayDamageToLevel())
		{
			//Display Damage to Damager
			if (event.GetCause() != DamageCause.THORNS)
				event.GetDamagerPlayer(true).setLevel((int)event.GetDamage());
		}

		try
		{	
			int bruteBonus = 0;
			if (event.IsBrute() && 
					(
							event.GetCause() == DamageCause.ENTITY_ATTACK || 
							event.GetCause() == DamageCause.PROJECTILE || 
							event.GetCause() == DamageCause.CUSTOM
							) && event.GetDamage() > 2)
				bruteBonus = 10;

			//Do Damage
			HandleDamage(event.GetDamageeEntity(), event.GetDamagerEntity(true), event.GetCause(), (int)event.GetDamage() + bruteBonus, event.IgnoreArmor());

			//Effect
			event.GetDamageeEntity().playEffect(EntityEffect.HURT);

			//Knockback
			double knockback = event.GetDamage();
			if (knockback < 1)		knockback = 1;
			knockback = Math.log10(knockback);

			for (double cur : event.GetKnockback().values())
				knockback = knockback * cur;

			if (event.IsKnockback())
				if (event.GetDamagerEntity(true) != null)
				{
					Vector trajectory = UtilAlg.getTrajectory2d(event.GetDamagerEntity(true), event.GetDamageeEntity());
					trajectory.multiply(0.6 * knockback);
					trajectory.setY(Math.abs(trajectory.getY()));

					UtilAction.velocity(event.GetDamageeEntity(), 
							trajectory, 0.2 + trajectory.length() * 0.8, false, 0, Math.abs(0.2 * knockback), 0.4 + (0.04 * knockback), true);
				}

			DisplayDamage(event);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} 
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

	private void DisplayDamage(CustomDamageEvent event) 
	{
		for (Player player : UtilServer.getPlayers())
		{
			if (!UtilGear.isMat(player.getItemInHand(), Material.BOOK))
				continue;

			UtilPlayer.message(player, " ");
			UtilPlayer.message(player, "=====================================");
			UtilPlayer.message(player, F.elem("Reason ") + event.GetReason());
			UtilPlayer.message(player, F.elem("Cause ") + event.GetCause());
			UtilPlayer.message(player, F.elem("Damager ") + UtilEnt.getName(event.GetDamagerEntity(true)));
			UtilPlayer.message(player, F.elem("Damagee ") + UtilEnt.getName(event.GetDamageeEntity()));
			UtilPlayer.message(player, F.elem("Projectile ") + UtilEnt.getName(event.GetProjectile()));
			UtilPlayer.message(player, F.elem("Damage ") + event.GetDamage());
			UtilPlayer.message(player, F.elem("Damage Initial ") + event.GetDamageInitial());
			for (DamageChange cur : event.GetDamageMod())
				UtilPlayer.message(player, F.elem("Mod ") + cur.GetDamage() + " - " + cur.GetReason() + " by " + cur.GetSource());

			for (DamageChange cur : event.GetDamageMult())
				UtilPlayer.message(player, F.elem("Mult ") + cur.GetDamage() + " - " + cur.GetReason() + " by " + cur.GetSource());

			for (String cur : event.GetKnockback().keySet())
				UtilPlayer.message(player, F.elem("Knockback ") + cur + " = " + event.GetKnockback().get(cur));

			for (String cur : event.GetCancellers())
				UtilPlayer.message(player, F.elem("Cancel ") + cur);

			
			
		
		}
	}

	private void HandleDamage(LivingEntity damagee, LivingEntity damager, DamageCause cause, float damage, boolean ignoreArmor) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		EntityLiving entityDamagee = ((CraftLivingEntity)damagee).getHandle();
		EntityLiving entityDamager = null;
		if (damager != null)
			entityDamager= ((CraftLivingEntity)damager).getHandle();

		entityDamagee.aG = 1.5F;

		if ((float) entityDamagee.noDamageTicks > (float) entityDamagee.maxNoDamageTicks / 2.0F) 
		{
			if (damage <= entityDamagee.lastDamage)
			{
				return;
			}

			ApplyDamage(entityDamagee, damage - entityDamagee.lastDamage, ignoreArmor);
			entityDamagee.lastDamage = damage;
		}        
		else
		{
			entityDamagee.lastDamage = damage;
			entityDamagee.ax = entityDamagee.getHealth();
			//entityDamagee.noDamageTicks = entityDamagee.maxNoDamageTicks;
			ApplyDamage(entityDamagee, damage, ignoreArmor);
			//entityDamagee.hurtTicks = entityDamagee.aW = 10;
		}

		if (entityDamager != null)
			entityDamagee.b(entityDamager);

		_lastDamageByPlayerTime.setInt(entityDamagee, 60);

		if (entityDamager != null)
			if (entityDamager instanceof EntityHuman)
				entityDamagee.killer = (EntityHuman)entityDamager;

		if (entityDamagee.getHealth() <= 0) 
		{
			if (entityDamager != null)
			{
				if (entityDamager instanceof EntityHuman)			entityDamagee.die(DamageSource.playerAttack((EntityHuman)entityDamager));
				else if (entityDamager instanceof EntityLiving)		entityDamagee.die(DamageSource.mobAttack((EntityLiving)entityDamager));
				else												entityDamagee.die(DamageSource.GENERIC);
			}
			else
				entityDamagee.die(DamageSource.GENERIC);
		}
	}

	@EventHandler
	public void DamageSound(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		if (event.GetCause() != DamageCause.ENTITY_ATTACK && event.GetCause() != DamageCause.PROJECTILE)
			return;

		//Damagee
		LivingEntity damagee = event.GetDamageeEntity();
		if (damagee == null)    return;

		
		if (_disguiseManager.isDisguised(damagee))
		{
			_disguiseManager.getDisguise(damagee).playHurtSound();
			return;
		}

		//Sound
		Sound sound = Sound.HURT_FLESH;
		float vol = 1f;
		float pitch = 1f;

		//Armor Sound
		if (damagee instanceof Player)
		{
			Player player = (Player)damagee;

			double r = Math.random();

			ItemStack stack = null;

			if (r > 0.50)		stack = player.getInventory().getChestplate();
			else if (r > 0.25)	stack = player.getInventory().getLeggings();
			else if (r > 0.10)	stack = player.getInventory().getHelmet();
			else 				stack = player.getInventory().getBoots();

			if (stack != null)
			{
				if (stack.getType().toString().contains("LEATHER_"))	
				{
					sound = Sound.SHOOT_ARROW;
					pitch = 2f;
				}
				else if (stack.getType().toString().contains("CHAINMAIL_"))	
				{
					sound = Sound.ITEM_BREAK;
					pitch = 1.4f;
				}
				else if (stack.getType().toString().contains("GOLD_"))	
				{
					sound = Sound.ITEM_BREAK;
					pitch = 1.8f;
				}
				else if (stack.getType().toString().contains("IRON_"))	
				{
					sound = Sound.BLAZE_HIT;
					pitch = 0.7f;
				}
				else if (stack.getType().toString().contains("DIAMOND_"))	
				{
					sound = Sound.BLAZE_HIT;
					pitch = 0.9f;
				}	
			}
		}
		//Animal Sound
		else 
		{
			UtilEnt.PlayDamageSound(damagee);	
		}

		damagee.getWorld().playSound(damagee.getLocation(), sound, vol, pitch);
	}

	private void ApplyDamage(EntityLiving entityLiving, float damage, boolean ignoreArmor) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		if (!ignoreArmor)
		{
			int j = 25 - entityLiving.aP();
			float k = damage * (float)j;

			_k.invoke(entityLiving, damage);
			damage = k / 25.0f;
		}

		/**
		if (entityLiving.hasEffect(MobEffectList.RESISTANCE)) 
		{
			int j = (entityLiving.getEffect(MobEffectList.RESISTANCE).getAmplifier() + 1) * 5;
			int k = 25 - j;
			int l = damage * k + _aS.getInt(entityLiving);

			damage = l / 25;
			_aS.setInt(entityLiving, l % 25);
		}
		 **/

		entityLiving.setHealth(entityLiving.getHealth() - damage);
	}

	private void WeaponDamage(EntityDamageEvent event, LivingEntity ent)
	{
		if (!(ent instanceof Player))
			return;

		if (event.getCause() != DamageCause.ENTITY_ATTACK)
			return;

		Player damager = (Player)ent;

		if (UseSimpleWeaponDamage)
		{
			if (event.getDamage() > 1)
				event.setDamage(event.getDamage() - 1);

			if (damager.getItemInHand().getType().name().contains("GOLD_")) 
				event.setDamage(event.getDamage() + 2);

			return;
		}

		if (damager.getItemInHand() == null || !UtilGear.isWeapon(damager.getItemInHand()))
		{
			event.setDamage(1);
			return;
		}

		Material mat = damager.getItemInHand().getType();

		int damage = 6;

		if (mat.name().contains("WOOD")) damage -= 3;
		else if (mat.name().contains("STONE")) damage -= 2;
		else if (mat.name().contains("DIAMOND")) damage += 0;
		else if (mat.name().contains("GOLD")) damage += 1;

		event.setDamage(damage);
	}

	private LivingEntity GetDamagerEntity(EntityDamageEvent event, boolean ranged)
	{
		if (!(event instanceof EntityDamageByEntityEvent))
			return null;

		EntityDamageByEntityEvent eventEE = (EntityDamageByEntityEvent)event;

		//Get Damager
		if (eventEE.getDamager() instanceof LivingEntity)
			return (LivingEntity)eventEE.getDamager();

		if (!ranged)
			return null;

		if (!(eventEE.getDamager() instanceof Projectile))
			return null;

		Projectile projectile = (Projectile)eventEE.getDamager();

		if (projectile.getShooter() == null)
			return null;

		if (!(projectile.getShooter() instanceof LivingEntity))
			return null;

		return (LivingEntity)projectile.getShooter();
	}

	private LivingEntity GetDamageeEntity(EntityDamageEvent event)
	{
		if (event.getEntity() instanceof LivingEntity)
			return (LivingEntity)event.getEntity();

		return null;
	}

	private Projectile GetProjectile(EntityDamageEvent event)
	{
		if (!(event instanceof EntityDamageByEntityEvent))
			return null;

		EntityDamageByEntityEvent eventEE = (EntityDamageByEntityEvent)event;

		if (eventEE.getDamager() instanceof Projectile)
			return (Projectile)eventEE.getDamager();

		return null;
	}
}
