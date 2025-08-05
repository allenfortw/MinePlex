package nautilus.game.capturethepig.engine;

import mineplex.minecraft.game.classcombat.Class.ClassManager;

import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import me.chiss.Core.Plugin.IPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.donation.DonationManager;
import mineplex.core.energy.Energy;
import mineplex.core.npc.NpcManager;
import mineplex.core.recharge.Recharge;
import mineplex.core.server.ServerTalker;

import nautilus.game.capturethepig.arena.ICaptureThePigArena;
import nautilus.game.capturethepig.event.*;
import nautilus.game.capturethepig.game.CaptureThePigGame;
import nautilus.game.capturethepig.game.ICaptureThePigGame;
import nautilus.game.capturethepig.game.ICaptureThePigTeam;
import nautilus.game.capturethepig.player.ICaptureThePigPlayer;
import nautilus.game.capturethepig.repository.ICaptureThePigRepository;
import nautilus.game.capturethepig.scoreboard.CaptureThePigScoreHandler;
import nautilus.game.capturethepig.scoreboard.ICaptureThePigScoreHandler;
import nautilus.game.capturethepig.stats.CaptureThePigStatsReporter;
import nautilus.game.core.arena.ArenaManager;
import nautilus.game.core.engine.TeamGameEngine;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CaptureThePigGameEngine extends TeamGameEngine<ICaptureThePigGame, ICaptureThePigScoreHandler, ICaptureThePigArena, ICaptureThePigTeam, ICaptureThePigPlayer> 
{
	public CaptureThePigGameEngine(JavaPlugin plugin, ServerTalker hubConnection, CoreClientManager clientManager, DonationManager donationManager, ClassManager classManager, 
			ConditionManager conditionManager, Energy energy, NpcManager npcManager, CaptureThePigNotifier notifier, ArenaManager<ICaptureThePigArena> arenaManager, World world, Location spawnLocation, String webServerAddress)
	{
		super(plugin, hubConnection, clientManager, donationManager, classManager, conditionManager, energy, npcManager, arenaManager, new CaptureThePigScoreHandler(plugin, notifier), world, spawnLocation);
		
        new CaptureThePigStatsReporter(plugin, this, webServerAddress);
        
        TeamSize = 50;
        MinQueuePlayersToStart = 1;
        TimeToStart = 0;
        AddToActiveGame = true;
        BroadcastQueueJoinMessage = false;
	}

    @Override
    public void ActivateGame(ICaptureThePigGame game, ICaptureThePigArena arena)
    {
        super.ActivateGame(game, arena);
        
        for (ICaptureThePigPlayer dominatePlayer : game.GetPlayers())
        {
            GameShop.ResetShopFor(dominatePlayer.GetPlayer());
        }
    }
	
	@Override
	public ICaptureThePigGame ScheduleNewGame() 
	{
		return Scheduler.ScheduleNewGame(new CaptureThePigGame(Plugin, PlayerNamer.PacketHandler));
	}

	@Override
	public String GetGameType() 
	{
		return "Capture The Pig";
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void TransferDamageToOwner(EntityDamageByEntityEvent event)
	{
		if (event.getEntity() instanceof Pig && event.getEntity().isInsideVehicle())
		{
			EntityDamageByEntityEvent newEvent = new EntityDamageByEntityEvent(event.getDamager(), event.getEntity().getVehicle(), event.getCause(), event.getDamage());
			Plugin.getServer().getPluginManager().callEvent(newEvent);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void DisableDamageToPig(CustomDamageEvent event)
	{
		if (event.GetDamageeEntity() != null && event.GetDamageeEntity() instanceof Pig)
		{
			event.SetCancelled("Don't harm dat pig!");
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void PlayerDeath(CombatDeathEvent event)
	{
		if (event.GetEvent().getEntity() == null || !(event.GetEvent().getEntity() instanceof Player))
			return;
		
		Player player = (Player)event.GetEvent().getEntity();
		
		if (!IsPlayerInActiveGame(player))
			return;
		
		ICaptureThePigGame game = GetGameForPlayer(player);
		
		if (!game.HasStarted())
			return;
		
		ICaptureThePigPlayer gamePlayer = game.GetPlayer(player);
		ICaptureThePigTeam playerTeam = gamePlayer.GetTeam();
		
		if (player.getPassenger() != null)
		{
			if (player.getPassenger() instanceof Pig)
			{
				RemovePigShuttle(gamePlayer);
				
				Plugin.getServer().getPluginManager().callEvent(new PigDroppedEvent(game, playerTeam));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCTPPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		if (event.isCancelled() || !(event.getRightClicked() instanceof Pig))
			return;
		
		if (!IsPlayerInActiveGame(event.getPlayer()))
			return;
		
		ICaptureThePigGame game = GetGameForPlayer(event.getPlayer());
		
		if (!game.HasStarted())
			return;
		
		if (event.getRightClicked().isInsideVehicle() || event.getPlayer().getPassenger() != null && event.getPlayer().getPassenger() instanceof Pig)
			return;
		
		ICaptureThePigPlayer player = game.GetPlayer(event.getPlayer());
		ICaptureThePigTeam playerTeam = player.GetTeam();
		ICaptureThePigTeam otherTeam = player.GetTeam() == game.GetRedTeam() ? game.GetBlueTeam() : game.GetRedTeam();

		if (player.IsDead() || player.IsSpectating())
			return;
		
		if (otherTeam.GetPigPen().Contains(event.getRightClicked().getLocation().toVector()))
		{
			if (otherTeam.HasPig() && Recharge.Instance.use(player.GetPlayer(), "Pig Steal", 10000, true))
			{
				CreatePigShuttle(player, otherTeam.RemovePig());
				
				Plugin.getServer().getPluginManager().callEvent(new PigStolenEvent(game, player, otherTeam));
			}
		}
		else if (!playerTeam.HasPig())
		{
			if (playerTeam.GetPig() == event.getRightClicked())
				playerTeam.ReturnPig();
			else
			{
				CreatePigShuttle(player, event.getRightClicked());
				Plugin.getServer().getPluginManager().callEvent(new PigPickedUpEvent(game, playerTeam));
			}
		}	
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void DropPig(PlayerCommandPreprocessEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (!IsPlayerInActiveGame(event.getPlayer()))
			return;
		
		if (event.getMessage().startsWith("/drop"))
		{
			ICaptureThePigGame game = GetGameForPlayer(event.getPlayer());
			
			if (!game.HasStarted())
				return;
			
			ICaptureThePigPlayer player = game.GetPlayer(event.getPlayer());
			ICaptureThePigTeam playerTeam = player.GetTeam();
			
			if (player.GetPlayer().getPassenger() != null)
			{
				if (player.GetPlayer().getPassenger() instanceof Pig)
				{
					RemovePigShuttle(player);
					
					Plugin.getServer().getPluginManager().callEvent(new PigDroppedEvent(game, playerTeam));
					
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCTPPlayerQuit(PlayerQuitEvent event)
	{
		if (!IsPlayerInActiveGame(event.getPlayer()))
			return;
		
		ICaptureThePigGame game = GetGameForPlayer(event.getPlayer());
		
		if (!game.HasStarted())
			return;
		
		ICaptureThePigPlayer player = game.GetPlayer(event.getPlayer());
		ICaptureThePigTeam playerTeam = player.GetTeam();
		
		if (player.GetPlayer().getPassenger() != null)
		{
			if (player.GetPlayer().getPassenger() instanceof Pig)
			{
				RemovePigShuttle(player);
				
				Plugin.getServer().getPluginManager().callEvent(new PigDroppedEvent(game, playerTeam));
			}
		}
	}

	private void CreatePigShuttle(ICaptureThePigPlayer player, Entity removePig) 
	{
		ConditionManager.Factory().Slow("Pig", player.GetPlayer(), player.GetPlayer(), 7200, 0, false, false, false, false);
		
		ConditionManager.SetIndicatorVisibility(player.GetPlayer(), false);
		
		player.GetPlayer().eject();
		player.GetPlayer().setPassenger(removePig);
	}

	private Pig RemovePigShuttle(ICaptureThePigPlayer player) 
	{
		Entity pig = player.GetPlayer().getPassenger();
		player.GetPlayer().eject();
		
		ConditionManager.EndCondition(player.GetPlayer(), ConditionType.SLOW, "Pig");
		ConditionManager.Factory().Vulnerable("Pig", player.GetPlayer(), player.GetPlayer(), 2, 0, false, false, false);

		ConditionManager.SetIndicatorVisibility(player.GetPlayer(), true);
		
		return (Pig)pig;
	}
}
