package mineplex.core.energy;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import mineplex.core.MiniClientPlugin;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.energy.event.EnergyEvent;
import mineplex.core.energy.event.EnergyEvent.EnergyChangeReason;

public class Energy extends MiniClientPlugin<ClientEnergy>
{
	private double _baseEnergy = 180;

	public Energy(JavaPlugin plugin)
	{
		super("Energy", plugin);
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK) 
			return;

		for (Player cur : UtilServer.getPlayers())
			UpdateEnergy(cur);
	}

	public void UpdateEnergy(Player cur) 
	{
		if (cur.isDead())
			return;

		//Get Exp Attribs
		double energy = 0.4;	

		//Sprint Reduce
		if (cur.isSprinting())
		{
			Get(cur).LastEnergy = System.currentTimeMillis();
			energy -= 0.2;
		}

		//Modify Energy
		EnergyEvent energyEvent = new EnergyEvent(cur, energy, EnergyChangeReason.Recharge);
		_plugin.getServer().getPluginManager().callEvent(energyEvent);

		if (energyEvent.isCancelled())
		{
			System.out.println("Cancelled recharge event.");
			return;
		}

		//Update Players Exp
		ModifyEnergy(cur, energyEvent.GetTotalAmount());
	}

	public void ModifyEnergy(Player player, double energy) 
	{
		ClientEnergy client = Get(player);

		if (energy > 0)
		{
			client.Energy = Math.min(GetMax(player), client.Energy + energy);
		}
		else
		{
			client.Energy = Math.max(0, client.Energy + energy);
		}

		//Record Drain
		if (energy < 0)
		{
			client.LastEnergy = System.currentTimeMillis();
		}

		player.setExp(Math.min(0.999f, ((float)client.Energy/(float)GetMax(player))));		
	}

	public double GetMax(Player player)
	{
		return _baseEnergy + Get(player).EnergyBonus();
	}

	public double GetCurrent(Player player)
	{
		return Get(player).Energy;
	}

	@EventHandler
	public void HandleRespawn(PlayerRespawnEvent event)
	{
		Get(event.getPlayer()).Energy = 0;
	}

	@EventHandler
	public void HandleJoin(PlayerJoinEvent event)
	{
		Get(event.getPlayer()).Energy = 0;
	}

	public boolean Use(Player player, String ability, double amount, boolean use, boolean inform)
	{
		ClientEnergy client = Get(player);

		if (client.Energy < amount)
		{
			if (inform)
				UtilPlayer.message(player, F.main(_moduleName, "You are too exhausted to use " + F.skill(ability) + "."));

			return false;
		}
		else
		{
			if (!use)
				return true;

			ModifyEnergy(player, -amount);

			return true;
		}
	}

	@EventHandler
	public void handleExp(PlayerExpChangeEvent event)
	{
		event.setAmount(0);
	}

	@Override
	protected ClientEnergy AddPlayer(String player)
	{
		return new ClientEnergy();
	}
	
	public void AddEnergyMaxMod(Player player, String reason, int amount)
	{
		Get(player).MaxEnergyMods.put(reason, amount);
	}
	
	public void RemoveEnergyMaxMod(Player player, String reason)
	{
		Get(player).MaxEnergyMods.remove(reason);
	}
	
	public void AddEnergySwingMod(Player player, String reason, int amount)
	{
		Get(player).SwingEnergyMods.put(reason, amount);
	}
	
	public void RemoveEnergySwingMod(Player player, String reason)
	{
		Get(player).SwingEnergyMods.remove(reason);
	}
	
	@EventHandler
	public void WeaponSwing(PlayerInteractEvent event)
	{
		if (!UtilEvent.isAction(event, ActionType.L))
			return;

		Player player = event.getPlayer();
		
		if (!UtilGear.isWeapon(player.getItemInHand()))
			return;

		if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING))
			return;
				
		ModifyEnergy(player, -Get(player).SwingEnergy());
	}
	
	@EventHandler
	public void ShootBow(EntityShootBowEvent event)
	{
		if (event.getEntity() instanceof Player)
			ModifyEnergy((Player)event.getEntity(), -10);
	}
}
