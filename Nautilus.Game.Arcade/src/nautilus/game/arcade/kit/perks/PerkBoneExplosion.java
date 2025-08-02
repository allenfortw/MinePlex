package nautilus.game.arcade.kit.perks;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import nautilus.game.arcade.kit.Perk;

public class PerkBoneExplosion extends Perk
{	
	public PerkBoneExplosion() 
	{
		super("Bone Explosion", new String[]  
				{
				C.cYellow + "Right-Click" + C.cGray + " with Axe to " + C.cGreen + "Bone Explosion"
				});
	}

	@EventHandler
	public void Leap(PlayerInteractEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		if (UtilBlock.usable(event.getClickedBlock()))
			return;
		
		if (event.getPlayer().getItemInHand() == null)
			return;
		
		if (!event.getPlayer().getItemInHand().getType().toString().contains("_AXE"))
			return;
		
		Player player = event.getPlayer();
		
		if (!Kit.HasKit(player))
			return;
		
		if (!Recharge.Instance.use(player, GetName(), 10000, true))
			return;
		
		HashMap<Player, Double> nearby = UtilPlayer.getInRadius(player.getLocation(), 8);
		for (Player other : nearby.keySet())
		{
			if (player.equals(other))
				continue;
			
			//Inform
			UtilPlayer.message(player, F.main("Game", F.elem(Manager.GetColor(player) + player.getName()) + " used " + F.skill(GetName()) + "."));
			
			//Damage Event
			Manager.GetDamage().NewDamageEvent(other, player, null, 
					DamageCause.CUSTOM, 4, false, true, false,
					player.getName(), GetName());
			
			//Velocity
			UtilAction.velocity(other, UtilAlg.getTrajectory(player, other), 1.2 + 0.8 * nearby.get(other), false, 0, 0.8 + 0.6 * nearby.get(other), 1.2, true);
		}
		
		//Inform
		UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
		
		//Effect
		Manager.GetBlood().Effects(player.getLocation().add(0, 0.5, 0), 48, 0.8, Sound.SKELETON_HURT, 2f, 1.2f, Material.BONE, (byte)0, 40, false);
	}
}
