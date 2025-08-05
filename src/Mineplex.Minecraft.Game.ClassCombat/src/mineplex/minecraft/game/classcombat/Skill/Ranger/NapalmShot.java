package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.core.common.util.F;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

public class NapalmShot extends SkillActive
{
	private HashSet<Entity> _arrows = new HashSet<Entity>();
	private HashSet<Player> _napalm = new HashSet<Player>();

	public NapalmShot(SkillFactory skills, String name, ClassType classType, SkillType skillType,
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
				"Prepare a napalm shot;",
				"Your next arrow will explode on",
				"impact, spewing out a rain of",
				"flames, which ignite players."
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

		return true;
	}

	@Override
	public void Skill(Player player, int level) 
	{
		//Action
		_napalm.add(player);

		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You prepared " + F.skill(GetName(level)) + "."));

		//Effect
		player.getWorld().playSound(player.getLocation(), Sound.BREATH, 2.5f, 2.0f);
	}

	@EventHandler
	public void BowShoot(EntityShootBowEvent event)
	{
		if (!(event.getEntity() instanceof Player))
			return;

		if (!(event.getProjectile() instanceof Arrow))
			return;

		Player player = (Player)event.getEntity();

		if (!_napalm.remove(player))
			return;

		//Inform
		UtilPlayer.message(player, F.main(GetClassType().name(), "You fired " + F.skill(GetName(GetLevel(player))) + "."));

		_arrows.add(event.getProjectile());
		event.getProjectile().setFireTicks(120);
	}

	@EventHandler
	public void ProjectileHit(ProjectileHitEvent event)
	{
		Projectile proj = event.getEntity();

		if (!_arrows.remove(proj))
			return;

		if (proj.getShooter() == null)
			return;

		if (!(proj.getShooter() instanceof Player))
			return;

		Player damager = (Player)proj.getShooter();
		int level = GetLevel(damager);
		if (level == 0)		return;

		proj.getWorld().playSound(proj.getLocation(), Sound.EXPLODE, 0.4f, 2f);

		for (int i = 0 ; i < 32 ; i++)
		{
			Item fire = proj.getWorld().dropItemNaturally(proj.getLocation(), ItemStackFactory.Instance.CreateStack(Material.FIRE, 1));
			Factory.Fire().Add(fire, damager, 16, 0.25, 2, 0, GetName());
			fire.setVelocity(fire.getVelocity().multiply(1.6));
		}
	}
	
	@EventHandler
	public void Clean(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;
		
		for (Iterator<Entity> arrowIterator = _arrows.iterator(); arrowIterator.hasNext();) 
		{
			Entity arrow = arrowIterator.next();
			
			if (arrow.isDead() || !arrow.isValid())
				arrowIterator.remove();
		}
	}

	@Override
	public void Reset(Player player) 
	{
		_napalm.remove(player);
	}
}
