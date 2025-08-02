package nautilus.game.arcade.managers;

import java.util.ArrayList;

import mineplex.core.account.CoreClient;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.donation.Donor;
import mineplex.core.shop.page.ConfirmationPage;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.shop.ArcadeShop;
import nautilus.game.arcade.shop.KitPackage;
import net.minecraft.server.v1_6_R2.Packet40EntityMetadata;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GamePlayerManager implements Listener
{
	ArcadeManager Manager;
	
	public GamePlayerManager(ArcadeManager manager)
	{
		Manager = manager;
		
		Manager.GetPluginManager().registerEvents(this, Manager.GetPlugin());
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void PlayerDeath(CombatDeathEvent event)
	{
		//Don't actually die
		event.GetEvent().getEntity().setHealth(20);

		//Dont display message
		if (Manager.GetGame() != null)
			event.SetBroadcastType(Manager.GetGame().GetDeathMessageType());
	
		//Colors
		if (event.GetLog().GetKiller() != null)
		{
			Player player = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
			if (player != null)
				event.GetLog().SetKillerColor(Manager.GetColor(player)+"");
		}


		if (event.GetEvent().getEntity() instanceof Player)
		{
			Player player = (Player)event.GetEvent().getEntity();
			if (player != null)
				event.GetLog().SetKilledColor(Manager.GetColor(player)+"");
		}
	}

	@EventHandler
	public void PlayerJoin(PlayerJoinEvent event)
	{
		final Player player = event.getPlayer();

		//Lobby Name
		Manager.GetLobby().AddPlayerToScoreboards(player, null);

		//Lobby Spawn
		if (Manager.GetGame() == null || !Manager.GetGame().InProgress())
		{
			Manager.Clear(player);
			player.teleport(Manager.GetLobby().GetSpawn());
			return;
		}

		//Game Spawn
		if (Manager.GetGame().IsAlive(player))
		{
			Location loc = Manager.GetGame().GetLocationStore().remove(player.getName());
			if (loc != null && !loc.getWorld().getName().equalsIgnoreCase("world"))
			{
				player.teleport(loc);
			}			
			else
			{
				Manager.Clear(player);
				player.teleport(Manager.GetGame().GetTeam(player).GetSpawn());
			}
		} 
		else
		{
			Manager.Clear(player);
			Manager.GetGame().SetSpectator(player);
			UtilPlayer.message(player, F.main("Game", Manager.GetGame().GetName() + " is in progress, please wait for next game!"));
		}

		player.setScoreboard(Manager.GetGame().GetScoreboard());
	}

	@EventHandler
	public void PlayerRespawn(PlayerRespawnEvent event)
	{
		if (Manager.GetGame() == null || !Manager.GetGame().InProgress())
		{
			event.setRespawnLocation(Manager.GetLobby().GetSpawn());
			return;
		}

		Player player = event.getPlayer();

		if (Manager.GetGame().IsAlive(player))
		{
			event.setRespawnLocation(Manager.GetGame().GetTeam(player).GetSpawn());
		}
		else
		{
			Manager.GetGame().SetSpectator(player);

			event.setRespawnLocation(Manager.GetGame().GetSpectatorLocation());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void TeamInteract(PlayerInteractEntityEvent event)
	{
		if (event.getRightClicked() == null)
			return;

		Player player = event.getPlayer();

		GameTeam team = Manager.GetLobby().GetClickedTeam(event.getRightClicked());

		if (team == null)
			return;

		TeamClick(player, team);
	}

	@EventHandler
	public void TeamDamage(CustomDamageEvent event)
	{
		Player player = event.GetDamagerPlayer(false);
		if (player == null)		return;

		LivingEntity target = event.GetDamageeEntity();

		GameTeam team = Manager.GetLobby().GetClickedTeam(target);

		if (team == null)
			return;

		TeamClick(player, team);
	}

	public void TeamClick(final Player player, final GameTeam team)
	{
		if (Manager.GetGame() == null)
			return;

		if (Manager.GetGame().GetState() != GameState.Recruit)
			return;

		if (!Manager.GetGame().HasTeam(team))
			return;

		AddTeamPreference(Manager.GetGame(), player, team);
	}
	
	public void AddTeamPreference(Game game, Player player, GameTeam team)
	{
		GameTeam past = game.GetTeamPreference(player);

		GameTeam current = game.GetTeam(player);
		if (current != null && current.equals(team))
		{
			game.RemoveTeamPreference(player);
			UtilPlayer.message(player, F.main("Team", "You are already on " + F.elem(team.GetFormattedName()) + "."));
			return;
		}

		if (past == null || !past.equals(team))
		{
			if (past != null)
				game.RemoveTeamPreference(player);

			if (!game.GetTeamPreferences().containsKey(team))
				game.GetTeamPreferences().put(team, new ArrayList<Player>());

			game.GetTeamPreferences().get(team).add(player);
		}

		UtilPlayer.message(player, F.main("Team", "You are " + F.elem(game.GetTeamQueuePosition(player)) + " in queue for " + F.elem(team.GetFormattedName() + " Team") + "."));
	}
	

	@EventHandler(priority = EventPriority.HIGH)
	public void KitInteract(PlayerInteractEntityEvent event)
	{
		if (event.getRightClicked() == null)
			return;

		Player player = event.getPlayer();

		Kit kit = Manager.GetLobby().GetClickedKit(event.getRightClicked());

		if (kit == null)
			return;

		KitClick(player, kit, event.getRightClicked());
	}

	@EventHandler
	public void KitDamage(CustomDamageEvent event)
	{
		Player player = event.GetDamagerPlayer(false);
		if (player == null)		return;

		LivingEntity target = event.GetDamageeEntity();

		Kit kit = Manager.GetLobby().GetClickedKit(target);

		if (kit == null)
			return;

		KitClick(player, kit, target);
	}

	public void KitClick(final Player player, final Kit kit, final Entity entity)
	{
		kit.DisplayDesc(player);

		if (Manager.GetGame() == null)
			return;

		if (!Manager.GetGame().HasKit(kit))
			return;


		CoreClient client = Manager.GetClients().Get(player);
		Donor donor = Manager.GetDonation().Get(player.getName());

		if (kit.GetAvailability() == KitAvailability.Free || client.GetRank().Has(Rank.ULTRA) || donor.OwnsUnknownPackage(Manager.GetServerConfig().ServerType + " ULTRA") || donor.OwnsUnknownPackage(Manager.GetGame().GetName() + " " + kit.GetName()))
		{
			Manager.GetGame().SetKit(player, kit, true);
		}
		else if (kit.GetAvailability() == KitAvailability.Green && donor.GetBalance(CurrencyType.Gems) > kit.GetCost())
		{
			Manager.GetShop().OpenPageForPlayer(player, new ConfirmationPage<ArcadeManager, ArcadeShop>(
					Manager, Manager.GetShop(), Manager.GetClients(), Manager.GetDonation(), new Runnable()
			{
				public void run()
				{
					if (player.isOnline())
					{
						Manager.GetGame().SetKit(player, kit, true);
						((CraftPlayer)player).getHandle().playerConnection.sendPacket(new Packet40EntityMetadata(entity.getEntityId(), ((CraftEntity)entity).getHandle().getDataWatcher(), true));
					}
				}
			}, null, new KitPackage(Manager.GetGame().GetName(), kit), CurrencyType.Gems, player));
		}
		else
		{
			player.playSound(player.getLocation(), Sound.NOTE_BASS, 2f, 0.5f);

			if (kit.GetAvailability() == KitAvailability.Blue)
				UtilPlayer.message(player, F.main("Kit", "You must purchase " + F.elem(C.cAqua + "Ultra") + " to use " + F.elem(kit.GetFormattedName() + " Kit") + "."));
			else
				UtilPlayer.message(player, F.main("Kit", "You need more " + F.elem(C.cGreen + "Gems") + " for " + F.elem(kit.GetFormattedName() + " Kit") + "."));
		}
	}
}
