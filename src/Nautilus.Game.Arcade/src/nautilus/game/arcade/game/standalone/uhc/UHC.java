package nautilus.game.arcade.game.standalone.uhc;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.MapUtil;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.disguise.disguises.DisguiseChicken;
import mineplex.core.disguise.disguises.DisguiseCreeper;
import mineplex.core.disguise.disguises.DisguisePig;
import mineplex.core.disguise.disguises.DisguiseSkeleton;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.map.Map;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeFormat;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.game.GameTeam.PlayerState;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.managers.GameLobbyManager;

public class UHC extends TeamGame
{
	//private Objective _listHealth; 
	private Map _map;

	private NautHashMap<String, Long> _deathTime = new NautHashMap<String, Long>();

	private NautHashMap<Player, Long> _rejoinTime = new NautHashMap<Player, Long>();

	private NautHashMap<Player, Long> _combatTime = new NautHashMap<Player, Long>();

	private int _gameMinutes = 0;
	private long _lastMinute = System.currentTimeMillis();

	public UHC(ArcadeManager manager)
	{ 
		super(manager, GameType.UHC,

				new Kit[] 
						{ 
				new KitUHC(manager)
						},

						new String[] { 
				"20 minutes of no PvP",
				"Borders at -1000 and 1000",
				"No default health regeneration",
				"Last team alive wins!" 
		});

		// Flags
		this.DamagePvP = false;
		this.DamageSelf = true;
		this.DamageTeamSelf = true;

		this.ItemDrop = true;
		this.ItemPickup = true;

		this.BlockBreak = true;
		this.BlockPlace = true;

		this.InventoryOpen = true;

		this.DeathOut = true; 
		this.QuitOut = false;

		this.CreatureAllow = true;

		this.AnnounceStay = false;
		this.AnnounceJoinQuit = false;
		this.AnnounceSilence = false;

		this.DisplayLobbySide = false;

		this.DeathMessages = false; 

		this.IdleKick = false;

		this.AutoStart = false;

		CraftRecipes();

		///_listHealth = this.GetScoreboard().registerNewObjective("Health", "health");
		//_listHealth.setDisplaySlot(DisplaySlot.PLAYER_LIST); 

		_map = new Map(manager.GetPlugin());
		_map.SetDefaultUrl("http://chivebox.com/img/mc/uhc.png");	

		//Disable Custom Mob Drops (and EXP Disable)
		Manager.GetCreature().SetDisableCustomDrops(true);

		//Disable Weapon Damage Mods
		Manager.GetDamage().DisableDamageChanges = true;
	}


	@EventHandler
	public void TimeUpdate(UpdateEvent event)
	{
		if (!IsLive())
			return;

		if (event.getType() != UpdateType.FAST)
			return;

		if (!UtilTime.elapsed(_lastMinute, 60000))
			return;

		_gameMinutes++;
		_lastMinute = System.currentTimeMillis();

		if (_gameMinutes == 5)
		{
			Announce(ChatColor.WHITE + C.Bold + "PvP enabled in 15 minutes.");
		}
		else if (_gameMinutes == 10)
		{
			Announce(ChatColor.WHITE + C.Bold + "PvP enabled in 10 minutes.");
		}
		else if (_gameMinutes == 15)
		{
			Announce(ChatColor.WHITE + C.Bold + "PvP enabled in 5 minutes.");
		}
		else if (_gameMinutes == 16)
		{
			Announce(ChatColor.WHITE + C.Bold + "PvP enabled in 4 minutes.");
		}
		else if (_gameMinutes == 17)
		{
			Announce(ChatColor.WHITE + C.Bold + "PvP enabled in 3 minutes.");
		}
		else if (_gameMinutes == 18)
		{
			Announce(ChatColor.WHITE + C.Bold + "PvP enabled in 2 minutes.");
		}
		else if (_gameMinutes == 19)
		{
			Announce(ChatColor.WHITE + C.Bold + "PvP enabled in 1 minutes.");
		}
		else if (_gameMinutes == 20)
		{
			Announce(ChatColor.WHITE + C.Bold + "PvP enabled!");
			for (Player player : UtilServer.getPlayers())
				player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 2f, 1f);

			this.DamagePvP = true;
		}
		else if (_gameMinutes % 20 == 20)
		{
			Announce(ChatColor.WHITE + C.Bold + _gameMinutes +  " minutes have passed.");
		}
	}

	@EventHandler
	public void GameStart(GameStateChangeEvent event)
	{
		if (event.GetState() != GameState.Prepare)
			return;

		//Kill Mobs
		for (Entity ent : WorldData.World.getEntities())
		{
			if (!(ent instanceof LivingEntity))
				continue;

			if (ent instanceof Player)
				continue;

			ent.remove();
		}

		//Blindness
		for (Player player : GetPlayers(true))
		{
			Manager.GetCondition().Factory().Blind("Start Blind", player, player, 8, 1, false, false, false);
			Manager.GetCondition().Factory().Slow("Start Slow", player, player, 8, 4, false, false, false, false);
		}
	}

	@EventHandler
	public void PlayerJoinMap(PlayerJoinEvent event)
	{
		//event.getPlayer().getInventory().remove(Material.MAP);
		//event.getPlayer().getInventory().addItem(_map.GetMap());
	}

	@EventHandler
	public void WorldBoundaryCheck(PlayerMoveEvent event)
	{
		if (!IsLive())
			return;

		//Allowed
		if (
				event.getTo().getX() <= 1000 && 
				event.getTo().getX() >= -1000 && 
				event.getTo().getZ() <= 1000 && 
				event.getTo().getZ() >= -1000)
			return;

		Location from = event.getFrom();
		if (from.getX() > 1000)			from.setX(1000);
		if (from.getX() < -1000)		from.setX(-1000);
		if (from.getZ() > 1000)			from.setZ(1000);
		if (from.getZ() < -1000)		from.setZ(-1000);

		event.setTo(event.getFrom());

		event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_BASS, 0.5f, 1f);
	}

	@EventHandler
	public void WorldBoundarySet(GameStateChangeEvent event)
	{
		if (event.GetState() != GameState.Recruit)
			return;

		this.WorldData.MinX = -1050;
		this.WorldData.MaxX = 1050;
		this.WorldData.MinZ = -1050;
		this.WorldData.MaxZ = 1050;

		for (int x=-63 ; x<=62 ; x++)
		{
			WorldBoundaryRed(WorldData.World.getChunkAt(x, 62));
			WorldBoundaryRed(WorldData.World.getChunkAt(x, -63));
		}

		for (int z=-63 ; z<=62 ; z++)
		{
			WorldBoundaryRed(WorldData.World.getChunkAt(62, z));
			WorldBoundaryRed(WorldData.World.getChunkAt(-63, z));
		}
	}

	public void WorldBoundaryRed(Chunk chunk)
	{
		Block block;
		for (int x=0 ; x<16 ; x++)
			for (int z=0 ; z<16 ; z++)
				for (int y=0 ; y<256 ; y++)
				{
					block = chunk.getBlock(x, y, z);

					if (
							block.getX() <= 999 && 
							block.getX() >= -1000 && 
							block.getZ() <= 999 && 
							block.getZ() >= -1000)
						continue;

					if (block.getTypeId() != 0)
						MapUtil.QuickChangeBlockAt(block.getLocation(), 159, (byte)4);
				}
	}

	@EventHandler
	public void WorldBoundaryBlockBreak(BlockBreakEvent event)
	{
		Block block = event.getBlock();

		if (
				block.getX() <= 1000 && 
				block.getX() >= -1000 && 
				block.getZ() <= 1000 && 
				block.getZ() >= -1000)
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void GenerateTeamNames(GameStateChangeEvent event)
	{
		if (event.GetState() != GameState.Recruit)
			return;

		for (GameTeam team : GetTeamList())
		{
			if (team.GetColor() == ChatColor.RED)				team.SetName("Fire");
			else if (team.GetColor() == ChatColor.GOLD)			team.SetName("Orange");
			else if (team.GetColor() == ChatColor.YELLOW)		team.SetName("Banana");
			else if (team.GetColor() == ChatColor.GREEN)		team.SetName("Forest");
			else if (team.GetColor() == ChatColor.DARK_BLUE)	team.SetName("Ocean");
			else if (team.GetColor() == ChatColor.AQUA)			team.SetName("Sky");
			else if (team.GetColor() == ChatColor.LIGHT_PURPLE)	team.SetName("Violet");
			else if (team.GetColor() == ChatColor.WHITE)		team.SetName("Ghost");
		}
	}

	@EventHandler
	public void GenerateSpawns(GameStateChangeEvent event)
	{
		if (event.GetState() != GameState.Recruit)
			return;

		for (GameTeam team : GetTeamList())
		{
			team.GetSpawns().clear();
		}

		for (GameTeam team : GetTeamList())
		{
			Location loc = WorldData.World.getHighestBlockAt(-1000 + UtilMath.r(2000), -1000 + UtilMath.r(2000)).getLocation();

			//Ensure 250 Blocks between Teams
			while (true)
			{ 
				boolean clash = false;

				for (GameTeam otherTeam : GetTeamList())
				{
					if (otherTeam.GetSpawns().isEmpty())
						continue;

					if (UtilMath.offset(loc, otherTeam.GetSpawn()) < 250)
					{
						clash = true;
						break;
					}				
				}

				if (!clash)
					break;

				loc = WorldData.World.getHighestBlockAt(-1000 + UtilMath.r(2000), -1000 + UtilMath.r(2000)).getLocation();
			}

			team.GetSpawns().add(loc);

			while (team.GetSpawns().size() < 5)
			{
				Location other = WorldData.World.getHighestBlockAt(loc.getBlockX() - 5 + UtilMath.r(10), loc.getBlockZ() - 5 + UtilMath.r(10)).getLocation();

				team.GetSpawns().add(other);
			}
		}
	}

	@EventHandler
	public void GhastDrops(EntityDeathEvent event)
	{
		if (event.getEntity() instanceof Ghast)
		{
			event.getDrops().clear();
			event.getDrops().add(ItemStackFactory.Instance.CreateStack(Material.GOLD_INGOT, 1 + UtilMath.r(6)));
		}
	}

	@EventHandler
	public void PlayerDeath(PlayerDeathEvent event)
	{
		Player player = event.getEntity();

		GameTeam team = GetTeam(player);
		if (team == null)	return;

		//Skull Drop
		event.getDrops().add(ItemStackFactory.Instance.CreateStack(Material.SKULL, (byte)3, 1, team.GetColor() + player.getName() + "'s Head"));

		//Lightning
		player.getWorld().strikeLightningEffect(player.getLocation());

		//Time
		_deathTime.put(player.getName(), System.currentTimeMillis());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void PlayerDeathMessage(CombatDeathEvent event)
	{
		if (!(event.GetEvent().getEntity() instanceof Player))
			return;

		Player dead = (Player)event.GetEvent().getEntity();

		CombatLog log = event.GetLog();

		Player killer = null;
		if (log.GetKiller() != null)
			killer = UtilPlayer.searchExact(log.GetKiller().GetName());

		//Simple 
		if (killer != null)
		{
			Announce(Manager.GetColor(dead) + C.Bold + dead.getName() + 
					C.cWhite + C.Bold + " was killed by " + 
					Manager.GetColor(killer) + C.Bold + killer.getName() + 
					C.cWhite + C.Bold + ".");
		}
		else
		{
			if (log.GetAttackers().isEmpty())
			{
				Announce(Manager.GetColor(dead) + C.Bold + dead.getName() + 
						C.cWhite + C.Bold + " has died by unknown causes.");
			}

			else
			{
				Announce(Manager.GetColor(dead) + C.Bold + dead.getName() + 
						C.cWhite + C.Bold + " was killed by " + log.GetAttackers().getFirst().GetName() + ".");
			}
		}
	}

	@EventHandler
	public void PlayerDeathTimeKick(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;

		for (Player player : UtilServer.getPlayers())
		{
			if (!_deathTime.containsKey(player.getName()))
				continue;

			if (!UtilTime.elapsed(_deathTime.get(player.getName()), 60000))
				continue;

			player.kickPlayer(C.cYellow + "60 Seconds have passed since you died.\nYou have been removed.");
		}
	}



	@EventHandler(priority = EventPriority.LOWEST)
	public void PlayerKick(PlayerKickEvent event)
	{
		event.setLeaveMessage(null);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();

		if (player.isDead())
			return;

		GameTeam team = GetTeam(player);
		if (team == null)		return;

		if (team.GetColor() == ChatColor.DARK_GRAY)
		{
			team.RemovePlayer(player);
			return;
		}

		if (_combatTime.containsKey(player) && !UtilTime.elapsed(_combatTime.get(player), 15000))
		{
			//Announcement
			Announce(team.GetColor() + C.Bold + player.getName() + " was killed for disconnecting during combat.");

			player.damage(5000);

			//team.SetPlayerState(player, PlayerState.OUT);
		}

		if (!team.IsAlive(player))
			return;

		_rejoinTime.put(player, System.currentTimeMillis());
		GetLocationStore().put(player.getName(), player.getLocation());

		//Announcement
		Announce(team.GetColor() + C.Bold + player.getName() + " has disconnected! 10 minutes to reconnect.");
	}

	@EventHandler
	public void DamageRecord(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		Player damagee = event.GetDamageePlayer();
		if (damagee == null)	return;

		Player damager = event.GetDamagerPlayer(true);
		if (damager == null)	return;

		_combatTime.put(damagee, System.currentTimeMillis());
		_combatTime.put(damager, System.currentTimeMillis());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void DamageToLevelDisable(CustomDamageEvent event)
	{
		event.SetDamageToLevel(false);
	}

	@EventHandler
	public void PlayerRejoinExpire(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;

		Iterator<Player> rejoinIterator = _rejoinTime.keySet().iterator();

		while (rejoinIterator.hasNext())
		{
			Player oldPlayer = rejoinIterator.next();

			if (!UtilTime.elapsed(_rejoinTime.get(oldPlayer), 600000))
				continue;

			rejoinIterator.remove();

			//Get Team (By Name)
			GameTeam team = GetTeam(oldPlayer);
			if (team != null)
			{
				Announce(team.GetColor() + C.Bold + oldPlayer.getName() + " did not reconnent in time!");
				team.SetPlayerState(oldPlayer, PlayerState.OUT);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void PlayerLoginAllow(PlayerLoginEvent event)
	{		
		if (!InProgress())
			return;

		//Get Team and OldPlayer
		GameTeam team = null;
		Player oldPlayer = null;
		for (GameTeam curTeam : GetTeamList())
		{
			for (Player curPlayer : curTeam.GetPlayers(true))
			{
				if (curPlayer.getName().equals(event.getPlayer().getName()))
				{
					oldPlayer = curPlayer;
					team = curTeam;
					break;
				}
			}

			if (oldPlayer != null)
				break;
		}

		if (team != null && oldPlayer != null)	
		{
			team.ReplaceReference(event.getPlayer());

			if (_rejoinTime.remove(oldPlayer) != null)
			{
				//Announcement
				Announce(team.GetColor() + C.Bold + event.getPlayer().getName() + " has reconnected!");
				return;
			}
		}

		if (Manager.GetClients().Get(event.getPlayer()).GetRank().Has(Rank.OWNER))
			return;

		event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
		event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "You cannot join this UHC.");
	}

	private void CraftRecipes() 
	{
		ShapelessRecipe goldMelon = new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON, 1));
		goldMelon.addIngredient(1, Material.MELON);
		goldMelon.addIngredient(1, Material.GOLD_BLOCK);
		UtilServer.getServer().addRecipe(goldMelon);

		ShapedRecipe headApple = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE, 1));
		headApple.shape("GGG","GHG","GGG");
		headApple.setIngredient('G', Material.GOLD_INGOT);
		headApple.setIngredient('H', Material.SKULL);
		UtilServer.getServer().addRecipe(headApple);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void CraftGoldenAppleDeny(PrepareItemCraftEvent event)
	{
		if (event.getRecipe().getResult() == null)
			return;

		Material type = event.getRecipe().getResult().getType();

		if (type != Material.GOLDEN_APPLE)
			return;

		if (!(event.getInventory() instanceof CraftingInventory))
			return;

		CraftingInventory inv = (CraftingInventory)event.getInventory();

		//Allow Normal Gold Apples
		for (ItemStack item : inv.getMatrix())
			if (item != null && item.getType() != Material.AIR)
				if (item.getType() == Material.GOLD_INGOT)
					return;

		inv.setResult(null);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void CraftGoldenAppleHead(PrepareItemCraftEvent event)
	{
		if (event.getRecipe().getResult() == null)
			return;

		Material type = event.getRecipe().getResult().getType();

		if (type != Material.GOLDEN_APPLE)
			return;

		if (!(event.getInventory() instanceof CraftingInventory))
			return;

		CraftingInventory inv = (CraftingInventory)event.getInventory();

		//Allow Normal Gold Apples
		for (ItemStack item : inv.getMatrix())
			if (item != null && item.getType() != Material.AIR)
				if (item.getType() == Material.SKULL)
				{
					if (item.getItemMeta() == null)
						continue;

					if (item.getItemMeta().getDisplayName() == null)
						continue;

					ItemStack apple = ItemStackFactory.Instance.CreateStack(Material.GOLDEN_APPLE, (byte)0, 1, item.getItemMeta().getDisplayName() + ChatColor.AQUA + " Golden Apple");
					apple.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);

					inv.setResult(apple);
					return;
				}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void CraftGlisteringMelon(PrepareItemCraftEvent event)
	{
		if (event.getRecipe().getResult() == null)
			return;

		Material type = event.getRecipe().getResult().getType();

		if (type != Material.SPECKLED_MELON)
			return;

		if (!(event.getInventory() instanceof CraftingInventory))
			return;

		CraftingInventory inv = (CraftingInventory)event.getInventory();

		//Allow FULL BLOCK Gold Melon
		for (ItemStack item : inv.getMatrix())
			if (item != null && item.getType() != Material.AIR)
				if (item.getType() == Material.GOLD_BLOCK)
					return;

		inv.setResult(null);
	}

	@EventHandler
	public void HealthChange(EntityRegainHealthEvent event)
	{
		if (event.getRegainReason() == RegainReason.SATIATED)
			event.setCancelled(true);
	}

	@EventHandler
	public void ConsumeHeadApple(PlayerItemConsumeEvent event)
	{
		if (event.getItem().getItemMeta().getDisplayName() == null)
			return;

		if (!event.getItem().getItemMeta().getDisplayName().contains("Head"))
			return;

		UtilPlayer.message(event.getPlayer(), "You ate " + event.getItem().getItemMeta().getDisplayName());

		(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 1)).apply(event.getPlayer());
		(new PotionEffect(PotionEffectType.REGENERATION, 200, 1)).apply(event.getPlayer());
	}

	@EventHandler
	public void NetherObsidianCancel(BlockPlaceEvent event)
	{
		if (event.getBlock().getWorld().getEnvironment() == Environment.NETHER)
		{
			if (event.getBlock().getType() == Material.OBSIDIAN)
			{
				UtilPlayer.message(event.getPlayer(), F.main("Game", "You cannot place " + F.elem("Obsidian") + " in the " + F.elem("Nether") + "."));
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void KillCancel(PlayerCommandPreprocessEvent event)
	{

	}

	@EventHandler
	public void Commands(PlayerCommandPreprocessEvent event)
	{
		if (event.getMessage().startsWith("/kill"))
			event.setCancelled(true);

		if (event.getMessage().startsWith("/uhc game start"))
		{
			this.SetCountdownForce(true);
			this.SetCountdown(11);
			event.setCancelled(true);

			Announce(event.getPlayer().getName() + " started the game!");
		}

		if (event.getMessage().startsWith("/uhc game stop"))
		{
			this.SetState(GameState.End);
			event.setCancelled(true);

			Announce(event.getPlayer().getName() + " stopped the game!");
		}

		if (event.getMessage().startsWith("/uhc time day"))
		{
			this.WorldTimeSet = 4000; 
			event.setCancelled(true);

			Announce(event.getPlayer().getName() + " set time to Always Day!");
		}

		if (event.getMessage().startsWith("/uhc time night"))
		{
			this.WorldTimeSet = 16000; 
			event.setCancelled(true);

			Announce(event.getPlayer().getName() + " set time to Always Night!");
		}

		if (event.getMessage().startsWith("/uhc time cycle"))
		{
			this.WorldTimeSet = -1; 
			event.setCancelled(true);

			Announce(event.getPlayer().getName() + " set time to Day and Night!");
		}


	}

	@EventHandler
	public void SpecialCommand(PlayerCommandPreprocessEvent event)
	{
		if (event.getMessage().startsWith("/superchissburger"))
		{
			Player player = event.getPlayer();

			if (GetTeam(player) != null)
			{
				if (GetTeam(player).GetColor() == ChatColor.DARK_GRAY)
				{
					GetTeam(player).RemovePlayer(player);
				}
				else
				{
					return;
				}
			}

			GameTeam special = null;
			for (GameTeam team : this.GetTeamList())
			{
				if (team.GetColor() == ChatColor.DARK_GRAY)
				{
					special = team;
					break;
				}
			}

			if (special == null)
			{
				special = new GameTeam("Herobrine", ChatColor.DARK_GRAY, GetTeamList().get(0).GetSpawns());
				AddTeam(special);
			}

			special.AddPlayer(player);

			((CraftPlayer)player).getHandle().spectating = false;
			((CraftPlayer)player).getHandle().m = true;

			Manager.GetCondition().EndCondition(player, ConditionType.CLOAK, null);

			if (event.getMessage().contains("chicken"))
			{
				DisguiseChicken disguise = new DisguiseChicken(player);
				Manager.GetDisguise().disguise(disguise);	
				player.sendMessage("You are a Chicken!");
			}
			else if (event.getMessage().contains("creeper"))
			{
				DisguiseCreeper disguise = new DisguiseCreeper(player);
				Manager.GetDisguise().disguise(disguise);	
				player.sendMessage("You are a Creeper!");
			}
			else if (event.getMessage().contains("pig"))
			{
				DisguisePig disguise = new DisguisePig(player);
				Manager.GetDisguise().disguise(disguise);
				player.sendMessage("You are a Pig!");
			}
			else
			{
				DisguiseSkeleton disguise = new DisguiseSkeleton(player);
				disguise.SetSkeletonType(SkeletonType.WITHER);
				Manager.GetDisguise().disguise(disguise);	
				player.sendMessage("You are a Wither Skeleton!");
			}
			

			UtilInv.Clear(player);
			UtilInv.insert(player, new ItemStack(Material.IRON_SWORD));

			event.setCancelled(true);
		}
	}

	@EventHandler
	public void SpecialDamage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		Player player = event.GetDamageePlayer();
		if (player != null)
		{
			GameTeam team = GetTeam(player);
			if (team != null)
			{
				if (team.GetColor() == ChatColor.DARK_GRAY)
				{
					event.SetCancelled("Special Cancel");
				}
			}
		}

		player = event.GetDamagerPlayer(true);
		if (player != null)
		{
			GameTeam team = GetTeam(player);
			if (team != null)
			{
				if (team.GetColor() == ChatColor.DARK_GRAY)
				{
					event.SetCancelled("Special Cancel");
				}
			}
		}
	}

	@EventHandler
	public void SpecialCloak(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		GameTeam team = GetTeam(ChatColor.DARK_GRAY);
		if (team == null)	return;

		for (Player player : team.GetPlayers(true))
		{
			if (!UtilGear.isMat(player.getItemInHand(), Material.IRON_SWORD))
				Manager.GetCondition().Factory().Cloak("Special Cloak", player, player, 1.9, false, false);
			else
				Manager.GetCondition().EndCondition(player, ConditionType.CLOAK, null);
		}
	}

	/*
	@EventHandler
	public void TabHealth(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		if (!InProgress())
			return;

		for (Player player : GetPlayers(true))
		{
			_listHealth.getScore(player).setScore((int)player.getHealth());
		}
	}
	 */

	@EventHandler
	public void TabHealth(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		if (!InProgress())
			return;

		for (Player player : GetPlayers(true))
		{
			GameTeam team = GetTeam(player);

			if (team.GetColor() == ChatColor.DARK_GRAY)
			{
				if (player.getName().equalsIgnoreCase("chiss"))
					player.setPlayerListName(" ");
				else if (player.getName().equalsIgnoreCase("defek7"))
					player.setPlayerListName("  ");
				else if (player.getName().equalsIgnoreCase("spu_"))
					player.setPlayerListName("   ");
				else if (player.getName().equalsIgnoreCase("sterling_"))
					player.setPlayerListName("    ");
				else
					player.setPlayerListName("     ");
				
				continue;
			}

			ChatColor col = ChatColor.GREEN;
			if (player.getHealth() <= 12)	col = ChatColor.YELLOW;
			if (player.getHealth() <= 6) 	col = ChatColor.RED;

			String health = " - " + col;

			if (((int)player.getHealth()) % 2 == 0)
				health += (int)(player.getHealth()/2);
			else
				health += player.getHealth()/2d;

			String name = team.GetColor() + player.getName();

			int length = 16 - (name.length() + health.length());
			if (length < 0)
			{
				name = name.substring(0, name.length() + length);
			}

			player.setPlayerListName(name + health);
		}
	}

	@Override
	@EventHandler
	public void ScoreboardUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;
		/*
		for (GameTeam team : this.GetTeamList())
		{			
			String name = team.GetColor() + team.GetName();
			if (name.length() > 16)
				name = name.substring(0, 16);

			int health = 0;
			for (Player player : team.GetPlayers(true))
			{
				health += (int)player.getHealth();
			}

			if (health <= 0)
			{
				GetScoreboard().resetScores(Bukkit.getOfflinePlayer(name));
				continue;
			}

			Score score = GetObjectiveSide().getScore(Bukkit.getOfflinePlayer(name));
			score.setScore(health);
		}
		 */
	}


	@Override
	public boolean CanJoinTeam(GameTeam team)
	{
		return (team.GetPlayers(true).size() < 3);
	}

	@Override
	public void AnnounceGame()
	{
		for (Player player : UtilServer.getPlayers())
		{
			player.playSound(player.getLocation(), Sound.LEVEL_UP, 2f, 1f);

			for (int i=0 ; i<6-GetDesc().length ; i++)
				UtilPlayer.message(player, "");

			UtilPlayer.message(player, ArcadeFormat.Line);

			UtilPlayer.message(player, C.cYellow+ C.Bold + this.GetName());
			UtilPlayer.message(player, "");

			for (String line : this.GetDesc())
			{
				UtilPlayer.message(player, C.cWhite + "- " + line);
			}

			UtilPlayer.message(player, "");
			UtilPlayer.message(player, C.cWhite + "Created and Hosted by " + C.cYellow + C.Bold + "Mineplex.com");

			UtilPlayer.message(player, ArcadeFormat.Line);
		}
	}

	@Override
	public boolean AdvertiseText(GameLobbyManager gameLobbyManager, int _advertiseStage) 
	{
		if (_advertiseStage == 0)
		{
			gameLobbyManager.WriteAdvertiseLine("         ", 0, 159, (byte)15);
			gameLobbyManager.WriteAdvertiseLine("ULTRA HARDCORE", 1, 159, (byte)15);
			gameLobbyManager.WriteAdvertiseLine("CODED AND HOSTED BY", 2, 159, (byte)15);
			gameLobbyManager.WriteAdvertiseLine("MINEPLEX.COM", 3, 159, (byte)4);
			gameLobbyManager.WriteAdvertiseLine("             ", 4, 159, (byte)15);
		}
		else if (_advertiseStage == 1)
		{
			gameLobbyManager.WriteAdvertiseLine("         ", 0, 159, (byte)15);
			gameLobbyManager.WriteAdvertiseLine("JOIN", 1, 159, (byte)15);
			gameLobbyManager.WriteAdvertiseLine("MINEPLEX.COM", 2, 159, (byte)4);
			gameLobbyManager.WriteAdvertiseLine("TO PLAY", 3, 159, (byte)15);
			gameLobbyManager.WriteAdvertiseLine("         ", 4, 159, (byte)15);
		}

		return true;
	}

	@Override
	public void EndCheck()
	{
		if (!IsLive())
			return;

		ArrayList<GameTeam> teamsAlive = new ArrayList<GameTeam>();

		for (GameTeam team : this.GetTeamList())
			if (team.GetColor() != ChatColor.DARK_GRAY)
				if (team.GetPlayers(true).size() > 0)
					teamsAlive.add(team);

		if (teamsAlive.size() <= 1)
		{
			//Announce
			if (teamsAlive.size() > 0)
				AnnounceEnd(teamsAlive.get(0));

			for (GameTeam team : GetTeamList())
			{
				if (WinnerTeam != null && team.equals(WinnerTeam))
				{
					for (Player player : team.GetPlayers(false))
						AddGems(player, 10, "Winning Team", false);
				}

				for (Player player : team.GetPlayers(false))
					if (player.isOnline())
						AddGems(player, 10, "Participation", false);
			}

			//End
			SetState(GameState.End);	
		}
	}

	/*
	@EventHandler
	public void PlayersDisplay(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SLOW)
			return;

		for (GameTeam team : GetTeamList())
		{
			for (Player player : team.GetPlayers(false))
			{
				Announce(team.GetColor() + player.getName() + ": " + (IsAlive(player)));
			}
		}
	}

	private NautHashMap<Entity, Long> _portalTime = new NautHashMap<Entity, Long>();
	private NautHashMap<Entity, Integer> _portalTick = new NautHashMap<Entity, Integer>();

	@EventHandler
	public void NetherPortal(EntityPortalEnterEvent event)
	{
		_portalTime.put(event.getEntity(), System.currentTimeMillis());

		int ticks = 1;
		if (_portalTick.containsKey(event.getEntity()))
			ticks += _portalTick.get(event.getEntity());

		_portalTick.put(event.getEntity(), ticks);
	}

	@EventHandler
	public void NetherUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FASTER)
			return;

		Iterator<Entity> netherIterator = _portalTime.keySet().iterator();

		while (netherIterator.hasNext())
		{
			Entity ent = netherIterator.next();

			//Left Portal
			if (UtilTime.elapsed(_portalTime.get(ent), 1000))
			{
				netherIterator.remove();
				_portalTick.remove(ent);
				continue;
			}

			int ticks = _portalTick.get(ent);

			if (ticks < 80)
				continue;

			netherIterator.remove();
			_portalTick.remove(ent);

			Vector vec = ent.getLocation().toVector().multiply(1d/16d);
			Block nether = vec.toLocation(UtilWorld.getWorldType(Environment.NETHER)).getBlock();


			//ent.teleport(nether, TeleportCause.NETHER_PORTAL);
		}
	}
	 */
}