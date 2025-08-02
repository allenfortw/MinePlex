package nautilus.game.arcade.managers;

import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.PlayerDeathOutEvent;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam.PlayerState;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GameFlagManager implements Listener
{
	ArcadeManager Manager;

	public GameFlagManager(ArcadeManager manager)
	{
		Manager = manager;

		Manager.GetPluginManager().registerEvents(this, Manager.GetPlugin());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void DamageEvent(CustomDamageEvent event)
	{ 
		Game game = Manager.GetGame();
		if (game == null)	
		{
			event.SetCancelled("Game Null");
			return;
		}

		LivingEntity damagee = event.GetDamageeEntity();
		LivingEntity damager = event.GetDamagerEntity(true);

		if (damagee != null && damagee.getWorld().getName().equals("world"))
		{
			event.SetCancelled("In Lobby");

			if (event.GetCause() == DamageCause.VOID)
				damagee.teleport(Manager.GetLobby().GetSpawn());

			return;
		}

		if (!game.Damage)
		{
			event.SetCancelled("Damage Disabled");
			return;
		}

		if (game.GetState() != GameState.Live)
		{
			event.SetCancelled("Game not Live");
			return; 
		}

		if (damagee != null && damagee instanceof Player && !game.IsAlive((Player)damagee))
		{
			event.SetCancelled("Damagee Not Playing");
			return;
		}

		if (damager != null && damager instanceof Player && !game.IsAlive((Player)damager))
		{
			event.SetCancelled("Damager Not Playing");
			return;
		}

		//Entity vs Entity
		if (damagee != null && damager != null)	
		{
			//PvP
			if (damagee instanceof Player && damager instanceof Player)
			{
				if (!Manager.CanHurt((Player)damagee, (Player)damager))
				{
					event.SetCancelled("Damage Rules");
					return;
				}
			}
			//PvE
			else if (damager instanceof Player)
			{
				if (!game.DamagePvE)
				{
					event.SetCancelled("PvE Disabled");
					return;
				}
			}
			//EvP
			else if (damagee instanceof Player)
			{
				if (!game.DamageEvP)
				{
					event.SetCancelled("EvP Disabled");
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void DamageExplosion(CustomDamageEvent event)
	{ 
		if (event.IsCancelled())
			return;

		if (event.GetCause() != DamageCause.ENTITY_EXPLOSION && event.GetCause() != DamageCause.BLOCK_EXPLOSION)
			return;

		Player damagee = event.GetDamageePlayer();
		if (damagee == null)	return;

		Player damager = event.GetDamagerPlayer(true);
		if (damager == null)	return;

		if (Manager.CanHurt(damagee, damager))
			return;

		event.SetCancelled("Allied Explosion");
	}

	

	@EventHandler(priority = EventPriority.LOWEST)
	public void ItemPickupEvent(PlayerPickupItemEvent event)
	{
		Player player = event.getPlayer();

		Game game = Manager.GetGame();

		if (game == null || !game.IsAlive(player) || game.GetState() != GameState.Live)
		{
			if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE)	
			{
				event.setCancelled(true);
			}

			return;
		}


		if (game.ItemPickup)
		{
			if (game.ItemPickupDeny.contains(event.getItem().getItemStack().getTypeId()))
			{
				event.setCancelled(true);
			}
		}
		else
		{					
			if (!game.ItemPickupAllow.contains(event.getItem().getItemStack().getTypeId()))
			{
				event.setCancelled(true);
			}
		}

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void ItemDropEvent(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();

		Game game = Manager.GetGame();
		if (game == null || !game.IsAlive(player) || game.GetState() != GameState.Live)
		{
			if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE)	
			{
				event.setCancelled(true);
			}

			return;
		}

		if (game.ItemDrop)
		{
			if (game.ItemDropDeny.contains(event.getItemDrop().getItemStack().getTypeId()))
			{
				event.setCancelled(true);
			}
		}
		else
		{					
			if (!game.ItemDropAllow.contains(event.getItemDrop().getItemStack().getTypeId()))
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void ItemDropEvent(InventoryOpenEvent event)
	{
		HumanEntity player = event.getPlayer();

		Game game = Manager.GetGame();
		if (game == null)
			return;
		
		if (!game.InProgress())
			return;

		if (game.InventoryOpen)
			return;

		player.closeInventory();
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockPlaceEvent(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();

		Game game = Manager.GetGame();
		if (game == null)
		{
			if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE)	
				event.setCancelled(true);
		}
		else
		{
			if (!game.IsAlive(player))
			{
				if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE)	
					event.setCancelled(true);
			}
			else
			{
				if (game.BlockPlace)
				{
					if (game.BlockPlaceDeny.contains(event.getBlock().getTypeId()))
					{
						event.setCancelled(true);
					}
				}
				else
				{					
					if (!game.BlockPlaceAllow.contains(event.getBlock().getTypeId()))
					{
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockBreakEvent(org.bukkit.event.block.BlockBreakEvent event)
	{
		Player player = event.getPlayer();

		Game game = Manager.GetGame();
		if (game == null)
		{
			if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE)	
				event.setCancelled(true);
		}
		else if (game.GetState() == GameState.Live)
		{
			if (!game.IsAlive(player))
			{
				event.setCancelled(true);
			}
			else
			{
				if (game.BlockBreak)
				{
					if (game.BlockBreakDeny.contains(event.getBlock().getTypeId()))
					{
						event.setCancelled(true);
					}

				}
				else
				{
					if (!game.BlockBreakAllow.contains(event.getBlock().getTypeId()))
					{
						event.setCancelled(true);
					}
				}
			}
		}
		else
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void PrivateBlockPlace(BlockPlaceEvent event)
	{
		Game game = Manager.GetGame();
		if (game == null)	return;

		if (!game.PrivateBlocks)
			return;

		if (event.isCancelled())
			return;

		if (!UtilBlock.usable(event.getBlockPlaced()))
			return;

		if (event.getBlockPlaced().getType() != Material.CHEST &&
				event.getBlockPlaced().getType() != Material.FURNACE &&
				event.getBlockPlaced().getType() != Material.BURNING_FURNACE &&
				event.getBlockPlaced().getType() != Material.WORKBENCH)
			return;

		String privateKey = event.getPlayer().getName();

		//Add Empty
		if (!game.PrivateBlockCount.containsKey(privateKey))
			game.PrivateBlockCount.put(privateKey, 0);

		if (game.PrivateBlockCount.get(privateKey) == 4)
			return;

		if (game.PrivateBlockCount.get(privateKey) > 1)
		{
			if (!Manager.GetDonation().Get(privateKey).OwnsUnknownPackage(
					Manager.GetServerConfig().ServerType + " ULTRA") && 
					!Manager.GetClients().Get(privateKey).GetRank().Has(Rank.ULTRA))
				return;
		}

		game.PrivateBlockMap.put(event.getBlockPlaced().getLocation(), event.getPlayer());
		game.PrivateBlockCount.put(event.getPlayer().getName(), game.PrivateBlockCount.get(event.getPlayer().getName()) + 1);
		event.getPlayer().sendMessage(F.main(game.GetName(), "Can't touch this. Na na nana!"));

		if (game.PrivateBlockCount.get(privateKey) == 4)
		{
			event.getPlayer().sendMessage(F.main(game.GetName(), "Protected block limit reached.  Stay classy Ultra ranker ;)"));
		}		
		else if (game.PrivateBlockCount.get(privateKey) == 2)
		{
			if (!Manager.GetDonation().Get(privateKey).OwnsUnknownPackage(
					Manager.GetServerConfig().ServerType + " ULTRA") && 
					!Manager.GetClients().Get(privateKey).GetRank().Has(Rank.ULTRA))
			{
				event.getPlayer().sendMessage(F.main(game.GetName(), "Protected block limit reached. Thieves are scary, get Ultra for 2 extra protected blocks!"));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void PrivateBlockPlaceCancel(BlockPlaceEvent event)
	{
		Game game = Manager.GetGame();
		if (game == null)	return;

		if (!game.PrivateBlocks)
			return;

		if (event.isCancelled())
			return;

		Block block = event.getBlockPlaced();

		if (block.getType() != Material.CHEST)
			return;

		Player player = event.getPlayer();

		BlockFace[] faces = new BlockFace[] {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

		for (BlockFace face : faces)
		{
			Block other = block.getRelative(face);

			if (other.getType() != Material.CHEST)
				continue;

			if (!game.PrivateBlockMap.containsKey(other.getLocation()))
				continue;

			Player owner = game.PrivateBlockMap.get(other.getLocation());

			if (player.equals(owner))
				continue;

			//Allow Enemy Raiding
			GameTeam ownerTeam = game.GetTeam(owner);
			GameTeam playerTeam = game.GetTeam(player);

			if (ownerTeam != null && playerTeam != null && !ownerTeam.equals(playerTeam))
				continue;

			//Disallow
			UtilPlayer.message(event.getPlayer(), F.main("Game", 
					"You cannot combine " + 
							F.elem(C.cPurple + ItemStackFactory.Instance.GetName(event.getBlock(), false)) + 
							" with " + F.elem(Manager.GetColor(owner) + owner.getName() + ".")));

			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void PrivateBlockBreak(org.bukkit.event.block.BlockBreakEvent event)
	{
		Game game = Manager.GetGame();
		if (game == null)	return;

		if (!game.PrivateBlocks)
			return;

		if (event.isCancelled())
			return;

		if (!game.PrivateBlockMap.containsKey(event.getBlock().getLocation()))
			return;

		Player owner = game.PrivateBlockMap.get(event.getBlock().getLocation());
		Player player = event.getPlayer();

		//Same Team (or no team)
		if (owner.equals(player))
		{
			game.PrivateBlockMap.remove(event.getBlock().getLocation());
		}
		else
		{
			//Allow Enemy Raiding
			GameTeam ownerTeam = game.GetTeam(owner);
			GameTeam playerTeam = game.GetTeam(player);

			if (ownerTeam != null && playerTeam != null && !ownerTeam.equals(playerTeam))
				return;

			//Disallow
			UtilPlayer.message(event.getPlayer(), F.main("Game", 
					F.elem(C.cPurple + ItemStackFactory.Instance.GetName(event.getBlock(), false)) + 
					" belongs to " + F.elem(Manager.GetColor(owner) + owner.getName() + ".")));

			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void PrivateBlockUse(PlayerInteractEvent event)
	{
		Game game = Manager.GetGame();
		if (game == null)	return;

		if (!game.PrivateBlocks)
			return;

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (!UtilBlock.usable(event.getClickedBlock()))
			return;

		if (event.getClickedBlock().getType() != Material.CHEST &&
				event.getClickedBlock().getType() != Material.FURNACE &&
				event.getClickedBlock().getType() != Material.BURNING_FURNACE)
			return;

		if (!game.PrivateBlockMap.containsKey(event.getClickedBlock().getLocation()))
			return;

		Player owner = game.PrivateBlockMap.get(event.getClickedBlock().getLocation());
		Player player = event.getPlayer();

		if (owner.equals(player))
		{
			return;
		}
		else
		{
			//Allow Enemy Raiding
			GameTeam ownerTeam = game.GetTeam(owner);
			GameTeam playerTeam = game.GetTeam(player);

			if (ownerTeam != null && playerTeam != null && !ownerTeam.equals(playerTeam))
				return;

			//Disallow
			UtilPlayer.message(event.getPlayer(), F.main("Game", 
					F.elem(C.cPurple + ItemStackFactory.Instance.GetName(event.getClickedBlock(), false)) + 
					" belongs to " + F.elem(Manager.GetColor(owner) + owner.getName() + ".")));

			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void PlayerDeath(PlayerDeathEvent event)
	{
		Game game = Manager.GetGame();
		if (game == null)	return;

		final Player player = event.getEntity();

		player.setFireTicks(0);
		player.setFallDistance(0);

		//Drop Items
		if (game.DeathDropItems)
			for (ItemStack stack : event.getDrops())
				player.getWorld().dropItem(player.getLocation(), stack);
		event.getDrops().clear();

		//Player State
		if (game.GetState() == GameState.Live && game.DeathOut)
		{
			//Event
			PlayerDeathOutEvent outEvent = new PlayerDeathOutEvent(game, player);
			UtilServer.getServer().getPluginManager().callEvent(outEvent);

			if (!outEvent.isCancelled())
			{
				game.SetPlayerState(player, PlayerState.OUT);
			}
		}

		//Teleport
		if (game.IsAlive(player))
		{
			game.RespawnPlayer(player);
		} 
		else
		{
			game.SetSpectator(player);
		}

		Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(Manager.GetPlugin(), new Runnable()
		{
			public void run()
			{
				player.setFireTicks(0);
				player.setVelocity(new Vector(0,0,0));
			}
		}, 0);
	}
	
	@EventHandler
	public void PlayerQuit(PlayerQuitEvent event)
	{
		Game game = Manager.GetGame();
		if (game == null)	return;

		//Remove Kit
		game.RemoveTeamPreference(event.getPlayer());
		game.GetPlayerKits().remove(event.getPlayer());
		game.GetPlayerGems().remove(event.getPlayer());
		
		if (!game.QuitOut)
			return;
		
		GameTeam team = game.GetTeam(event.getPlayer());

		team.SetPlayerState(event.getPlayer(), PlayerState.OUT);
	}

	@EventHandler
	public void PlayerMoveCancel(PlayerMoveEvent event)
	{
		Game game = Manager.GetGame();
		if (game == null || game.GetState() != GameState.Prepare)
			return;

		if (!game.PrepareFreeze)
			return;

		if (UtilMath.offset(event.getFrom(), event.getTo()) <= 0)
			return;

		event.getFrom().setPitch(event.getTo().getPitch());
		event.getFrom().setYaw(event.getTo().getYaw());

		event.setTo(event.getFrom());
	}

	@EventHandler
	public void PlayerHealthFoodUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		Game game = Manager.GetGame();

		//Not Playing
		for (Player player : UtilServer.getPlayers())
		{
			if (game == null || game.GetState() != GameState.Live || !game.IsAlive(player))
			{
				player.setHealth(20);
				player.setFoodLevel(20);
			}
		}

		if (game == null)
			return;

		if (game.HungerSet != -1)
			for (Player player : game.GetPlayers(true))
				player.setFoodLevel(game.HungerSet);

		if (game.HealthSet != -1)
			for (Player player : game.GetPlayers(true))
				player.setHealth(game.HealthSet);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PlayerBoundaryCheck(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		Game game = Manager.GetGame();
		if (game == null || game.GetState() != GameState.Live)
			return;
		
		for (Player player : UtilServer.getPlayers())
		{
			if (player.getLocation().getX() > game.WorldData.MaxX ||
					player.getLocation().getX() < game.WorldData.MinX ||
					player.getLocation().getZ() > game.WorldData.MaxZ ||
					player.getLocation().getZ() < game.WorldData.MinZ ||
					(game.WorldHeightLimit > 0 && player.getLocation().getY() > game.WorldHeightLimit && game.IsAlive(player)))
			{
				if (!Manager.IsAlive(player)) 
				{
					player.teleport(game.GetSpectatorLocation());
				}
				else
				{
					UtilPlayer.message(player, C.cRed + C.Bold + "WARNING: " + C.cWhite + C.Bold +"RETURN TO PLAYABLE AREA!");

					Manager.GetDamage().NewDamageEvent(player, null, null, 
							DamageCause.VOID, 4, true, false, false,
							"Void", "Void Damage");

					player.getWorld().playSound(player.getLocation(), Sound.NOTE_BASS, 2f, 1f);
					player.getWorld().playSound(player.getLocation(), Sound.NOTE_BASS, 2f, 1f);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void WorldCreature(CreatureSpawnEvent event)
	{	
		Game game = Manager.GetGame();
		if (game == null)	return;

		if (!game.CreatureAllow && !game.CreatureAllowOverride)
		{
			if (game.WorldData != null)
			{
				if (game.WorldData.World != null)
				{
					if (event.getLocation().getWorld().equals(game.WorldData.World))
					{
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void WorldTime(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		Game game = Manager.GetGame();
		if (game == null)	return;

		if (game.WorldTimeSet != -1)
		{
			if (game.WorldData != null)
			{
				if (game.WorldData.World != null)
				{
					game.WorldData.World.setTime(game.WorldTimeSet);
				}
			}
		}
	}
	
	@EventHandler
	public void WorldWeather(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;

		Game game = Manager.GetGame();
		if (game == null)	return;

		if (!game.WorldWeatherEnabled)
		{
			if (game.WorldData != null)
			{
				if (game.WorldData.World != null)
				{
					game.WorldData.World.setStorm(false);
					game.WorldData.World.setThundering(false);
				}
			}
		}
	}

	@EventHandler
	public void WorldWaterDamage(UpdateEvent event)
	{
		Game game = Manager.GetGame();
		if (game == null)	return;

		if (game.WorldWaterDamage <= 0)
			return;

		if (!game.IsLive())
			return;		

		if (event.getType() != UpdateType.FAST)
			return;

		for (GameTeam team : game.GetTeamList())
			for (Player player : team.GetPlayers(true))
				if (player.getLocation().getBlock().getTypeId() == 8 || player.getLocation().getBlock().getTypeId() == 9)
				{
					//Damage Event
					Manager.GetDamage().NewDamageEvent(player, null, null, 
							DamageCause.DROWNING, 4, true, false, false,
							"Water", "Water Damage");

					player.getWorld().playSound(player.getLocation(),
							Sound.SPLASH, 0.8f,
							1f + (float) Math.random() / 2);
				}
	}
	
	@EventHandler
	public void SpectatorMessage(UpdateEvent event)
	{
		if (Manager.GetGame() == null)
			return;
				 
		if (!Manager.GetGame().AnnounceStay)
			return;
		
		if (!Manager.GetGame().IsLive())
			return;		
		
		if (event.getType() != UpdateType.SLOWER)
			return;
	
		for (Player player : UtilServer.getPlayers())
		{
			if (Manager.IsAlive(player))
				continue;
			
			UtilPlayer.message(player, " ");
			UtilPlayer.message(player, C.cWhite + C.Bold + "You are out of the game, but " + C.cGold + C.Bold + "DON'T QUIT" + C.cWhite + C.Bold + "!");
			UtilPlayer.message(player, C.cWhite + C.Bold + "The next game will be starting soon...");
		}
	}
	
	
}
