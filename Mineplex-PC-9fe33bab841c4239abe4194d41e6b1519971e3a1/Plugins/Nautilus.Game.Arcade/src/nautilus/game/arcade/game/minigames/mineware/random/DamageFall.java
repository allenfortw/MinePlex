package nautilus.game.arcade.game.minigames.mineware.random;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.game.minigames.mineware.MineWare;
import nautilus.game.arcade.game.minigames.mineware.order.Order;

public class DamageFall extends Order 
{
	public DamageFall(MineWare host) 
	{
		super(host, "Take fall damage");
	}

	@Override
	public void Initialize() 
	{
		
	}

	@Override
	public void Uninitialize()
	{
		
	}

	@Override
	public void FailItems(Player player) 
	{
		
	}
	
	@EventHandler
	public void Damage(CustomDamageEvent event)
	{
		if (event.GetCause() != DamageCause.FALL)
			return;
		
		Player player = event.GetDamageePlayer();
		if (player == null)	return;
		
		SetCompleted(player);
	}
}
