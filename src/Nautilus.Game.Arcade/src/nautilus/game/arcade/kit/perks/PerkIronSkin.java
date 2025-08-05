package nautilus.game.arcade.kit.perks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import mineplex.core.common.util.C;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.kit.Perk;

public class PerkIronSkin extends Perk
{
	private int _reduction;
	
	public PerkIronSkin(int reduction) 
	{
		super("Iron Skin", new String[] 
				{ 
				C.cGray + "You take " + reduction + " less damage from attacks",
				});
		
		_reduction = reduction;
	}
		
	@EventHandler(priority = EventPriority.HIGH)
	public void DamageDecrease(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;
		
		if (event.GetCause() == DamageCause.FIRE_TICK)
			return;
		
		Player damagee = event.GetDamageePlayer();
		if (damagee == null)	return;
		
		if (!Kit.HasKit(damagee))
			return;
		
		event.AddMod(damagee.getName(), GetName(), -_reduction, false);
	}
}
