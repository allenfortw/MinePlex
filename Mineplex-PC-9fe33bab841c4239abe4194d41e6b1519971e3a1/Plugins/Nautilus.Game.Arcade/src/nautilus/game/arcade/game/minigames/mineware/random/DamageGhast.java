package nautilus.game.arcade.game.minigames.mineware.random;

import org.bukkit.Material;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.game.minigames.mineware.MineWare;
import nautilus.game.arcade.game.minigames.mineware.order.Order;

public class DamageGhast extends Order 
{
	public DamageGhast(MineWare host) 
	{
		super(host, "shoot the ghast");
	}

	@Override
	public void Initialize() 
	{
		for (Player player : Host.GetPlayers(true))
		{
			if (!player.getInventory().contains(Material.BOW))
				player.getInventory().addItem(new ItemStack(Material.BOW));	
		}
	}
	
	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;
		
		for (Player player : Host.GetPlayers(true))
			if (!player.getInventory().contains(Material.ARROW))
				player.getInventory().addItem(new ItemStack(Material.ARROW));	
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
		if (event.GetCause() != DamageCause.PROJECTILE)
			return;
		
		Player player = event.GetDamagerPlayer(true);
		if (player == null)	return;
		
		LivingEntity ent = event.GetDamageeEntity();
		if (ent == null)	return;
		
		if (!(ent instanceof Ghast))
			return;
		
		SetCompleted(player);
	}
}
