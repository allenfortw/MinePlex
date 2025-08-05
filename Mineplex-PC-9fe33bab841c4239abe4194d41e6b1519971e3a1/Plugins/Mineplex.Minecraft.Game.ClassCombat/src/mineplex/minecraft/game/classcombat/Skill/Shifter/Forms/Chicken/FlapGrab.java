package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Chicken;

import java.util.HashMap;

import org.bukkit.EntityEffect;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.F;

public class FlapGrab 
{
	public Flap Host;
	
	private HashMap<Player, LivingEntity> _clutch = new HashMap<Player, LivingEntity>();

	public FlapGrab(Flap host)
	{
		Host = host;
	}
	
	public void Grab(Player player, LivingEntity ent)
	{
		if (_clutch.containsKey(player))
			return;
		
		if (ent == null)
			return;

		//Ent already has LE passenger
		if (ent.getPassenger() != null)
			if (ent.getPassenger() instanceof LivingEntity)
				return;

		//Player on something
		if (player.getVehicle() != null)
			return;

		//Condition Indicators
		Host.Factory.Condition().SetIndicatorVisibility((LivingEntity)ent, false);

		//Action
		player.leaveVehicle();
		ent.eject();
		ent.setPassenger(player);
		_clutch.put(player, (LivingEntity)ent);

		//Inform
		UtilPlayer.message(player, F.main(Host.GetClassType().name(), "You picked up " + F.name(UtilEnt.getName(ent)) + "."));
		UtilPlayer.message(ent, F.main(Host.GetClassType().name(), F.name(player.getName()) + " picked you up."));

		//Effect
		ent.playEffect(EntityEffect.HURT);
		
		//Sound
		player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_HURT, 2f, 1.5f);
	}

	public void Release(Player player) 
	{
		LivingEntity ent = _clutch.remove(player);
		if (ent == null)		return;

		player.leaveVehicle();
		ent.eject();

		//Condition Indicators
		Host.Factory.Condition().SetIndicatorVisibility(ent, true);

		//Inform
		UtilPlayer.message(player, F.main(Host.GetClassType().name(), "You released " + F.name(UtilEnt.getName(ent)) + "."));
		UtilPlayer.message(ent, F.main(Host.GetClassType().name(), F.name(player.getName()) + " released you."));

		//Effect
		ent.playEffect(EntityEffect.HURT);
	}
	
	public void DamageRelease(CustomDamageEvent event) 
	{
		Player damagee = event.GetDamageePlayer();
		if (damagee == null)	return;

		Release(damagee);
	}

	public void Reset(Player player) 
	{
		LivingEntity ent = _clutch.remove(player);
		if (ent != null)
		{
			ent.eject();
			Host.Factory.Condition().SetIndicatorVisibility(ent, true);
		}
	}
}
