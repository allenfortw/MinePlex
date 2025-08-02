package nautilus.game.core.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;

import mineplex.core.server.ServerTalker;
import mineplex.core.server.event.PlayerGameAssignmentEvent;
import mineplex.core.server.packet.GameReadyPacket;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.creature.event.CreatureSpawnCustomEvent;
import mineplex.core.donation.DonationManager;
import mineplex.core.energy.*;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.core.combat.CombatComponent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.classcombat.shop.ClassCombatCustomBuildShop;
import mineplex.minecraft.game.classcombat.shop.ClassCombatPurchaseShop;
import mineplex.minecraft.game.classcombat.shop.ClassCombatShop;
import mineplex.minecraft.game.core.IRelation;
import mineplex.minecraft.game.core.combat.event.ClearCombatEvent;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.condition.*;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.core.shop.item.ShopItem;
import mineplex.core.updater.event.RestartServerEvent;
import me.chiss.Core.Events.ServerSaveEvent;
import nautilus.game.core.arena.ArenaManager;
import nautilus.game.core.arena.IArena;
import nautilus.game.core.events.GamePlayerAfkEvent;
import nautilus.game.core.events.GamePlayerAttackedPlayerEvent;
import nautilus.game.core.events.GamePlayerDeathEvent;
import nautilus.game.core.events.GamePlayerQuitEvent;
import nautilus.game.core.game.IGame;
import nautilus.game.core.player.IGamePlayer;
import nautilus.game.core.scoreboard.IScoreHandler;
import nautilus.minecraft.core.event.AfkEvent;
import nautilus.minecraft.core.player.AfkMonitor;
import net.minecraft.server.v1_6_R2.Item;
import net.minecraft.server.v1_6_R2.NBTTagCompound;
import net.minecraft.server.v1_6_R2.NBTTagList;
import net.minecraft.server.v1_6_R2.NBTTagString;

public abstract class GameEngine<GameType extends IGame<ArenaType, PlayerType>, ScoreHandlerType extends IScoreHandler<PlayerType>, ArenaType extends IArena, PlayerType extends IGamePlayer> implements IGameEngine<GameType, ArenaType, PlayerType>, Listener, Runnable, IRelation 
{
	protected int MaxGames = -1;
	
    protected JavaPlugin Plugin;    
    protected World World;
    protected Location SpawnLocation;
    protected CoreClientManager ClientManager;
    protected ClassManager ClassManager;
    protected ConditionManager ConditionManager;
    protected Energy Energy;
    protected ArenaManager<ArenaType> ArenaManager;
    protected GameScheduler<GameType> Scheduler;
    protected ScoreHandlerType ScoreHandler;
    protected HashMap<String, Integer> PlayerTaskIdMap;

    protected HashMap<String, Long> PendingPlayerRemoveMap;
    protected HashMap<String, GameType> PlayerGameMap;
    protected HashMap<String, GameType> SpectatorGameMap;
    protected List<GameType> ActiveGames;
    protected List<GameType> GamesInSetup;
    protected AfkMonitor AfkMonitor;
    
    protected CraftItemStack StartBook;
    protected ShopItem LobbyGem;
    
    protected ClassCombatShop GameShop;
    protected ClassCombatPurchaseShop DonationShop;
    protected ClassCombatCustomBuildShop CustomBuildShop;
    
    protected int LogoutPeriod = 30;
    protected boolean AddToActiveGame;
    
    protected mineplex.core.portal.Portal Portal;
    
    protected HashSet<String> ConnectingPlayersToAddToGame;
    
    protected ServerTalker HubConnection;
    
    public GameEngine(JavaPlugin plugin, ServerTalker hubConnection, CoreClientManager clientManager, DonationManager donationManager, ClassManager classFactory, 
    		ConditionManager conditionManager, Energy energy, ArenaManager<ArenaType> arenaManager, ScoreHandlerType scoreHandler, World world, Location spawnLocation)
    {
        Plugin = plugin;
        HubConnection = hubConnection;
        ClientManager = clientManager;
        ClassManager = classFactory;
        ConditionManager = conditionManager;
        Energy = energy;
        
        World = world;
        SpawnLocation = spawnLocation;
        ArenaManager = arenaManager;
        Scheduler = new GameScheduler<GameType>();
        ScoreHandler = scoreHandler;
        PlayerTaskIdMap = new HashMap<String, Integer>();
        
        PendingPlayerRemoveMap = new HashMap<String, Long>();
        PlayerGameMap = new HashMap<String, GameType>();
        SpectatorGameMap = new HashMap<String, GameType>();
        ActiveGames = new ArrayList<GameType>();
        GamesInSetup = new ArrayList<GameType>();
        AfkMonitor = new AfkMonitor(plugin);
        
        ConnectingPlayersToAddToGame = new HashSet<String>();
        
        Portal = new mineplex.core.portal.Portal(plugin);
        
        Plugin.getServer().getPluginManager().registerEvents(this, Plugin);
        Plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin, this, 0L, 1200L);
        
        StartBook = CraftItemStack.asNewCraftStack(Item.WRITTEN_BOOK);
        NBTTagCompound bookData = StartBook.getHandle().tag;
                
        if (bookData == null)
        	bookData = new NBTTagCompound("tag");
        
        bookData.setString("title", ChatColor.GREEN + "" + ChatColor.BOLD + "Instructions");
        
        NBTTagList nPages = new NBTTagList();
        nPages.add(new NBTTagString("1", ChatColor.DARK_GREEN + "         DOMINATE" +
        								 ChatColor.DARK_GRAY + "\n\n\n   Table of Contents" +
        								 ChatColor.DARK_GRAY + "\n\n1. " + ChatColor.BLUE + "Signup to play" +
        								 ChatColor.DARK_GRAY + "\n2. " + ChatColor.BLUE + "Setting up a class" +
        								 ChatColor.DARK_GRAY + "\n3. " + ChatColor.BLUE + "Choosing class" +
        								 ChatColor.DARK_GRAY + "\n4. " + ChatColor.BLUE + "Skills" + 
        								 ChatColor.DARK_GRAY + "\n5. " + ChatColor.BLUE + "How to Play" +
        								 ChatColor.DARK_GRAY + "\n6. " + ChatColor.BLUE + "Tips"));
        nPages.add(new NBTTagString("a", ChatColor.DARK_GREEN + "          SIGNUP" +
        								 ChatColor.BLUE + "\n\nClick with dye in your hand. It will turn green." +
    									 				  "\n\nGame will start when 10 players sign up." +
    									 				  "\n\nIf you wish to leave queue, click with dye in your hand again. It will go back to gray"));
        nPages.add(new NBTTagString("2", ChatColor.DARK_GREEN + "       CLASS SETUP" +
        								 ChatColor.BLUE + "\n\nRight-click on the Enchantment Table." +										 				  
        												  "\n\nLeft-click class and Anvil to setup a custom build." +
        												  "\n\nChoose skills, weapons and items."));
        nPages.add(new NBTTagString("3", ChatColor.DARK_GREEN + "      CLASS IN-GAME" +
        								 ChatColor.BLUE + "\n\nRight-click on the Enchantment Table." +
				        								  "\n\nLeft-click class." + 
				        								  "\n\nLeft-click dye to select a custom build." +
				        								  "\n\nLeft-click anvil to edit build or workbench modify without saving build."));
        nPages.add(new NBTTagString("4", ChatColor.DARK_GREEN + "          SKILLS" +
        		 					 	 ChatColor.BLUE + "\n\nOpen inventory and mouse over the 'Skill Hotbar' to see how each skill works." +
        		 					 	 				  "\n\nActive skills are attached to weapons." +
        		 					 	 				  "\n\nPassive skills are active all the time.")); 
        nPages.add(new NBTTagString("5", ChatColor.DARK_GREEN + "      HOW TO PLAY" +
        								 ChatColor.BLUE + "\n\nEach control point is marked by a beacon." + 
        								 				  "\n\nTo capture, stand on the glass flooring of the point until the wool floor underneath the glass is your team's color."));
        nPages.add(new NBTTagString("6", ChatColor.DARK_GREEN + "          TIPS" +
        								 ChatColor.BLUE + "\n\n1. Gather emeralds for an additional " + ChatColor.DARK_GREEN + "+100" + ChatColor.BLUE + " to your score." +
        								 				  "\n\n2. Defend control points for " + ChatColor.DARK_GREEN + "+2" + ChatColor.BLUE + " score per second" +
        								 				  "\n\n3. Watch scoreboard by holding TAB to see what points the enemy team controls."));
        									 
        bookData.set("pages", nPages);
        
        StartBook.getHandle().tag = bookData;
        
        LobbyGem = new ShopItem(Material.EYE_OF_ENDER, "Lobby Teleport", new String [] { "§rClick with this in hand to teleport back to lobby." }, 1, false, true);
    }
    
    protected abstract void ActivateGame(GameType game, ArenaType arena);
    
    public abstract GameType ScheduleNewGame();
    
    public abstract String GetGameType();
    
    public void StopGame(GameType game)
    {
    	for (PlayerType player : game.GetPlayers())
        {
            RemovePlayerFromGame(game, player, !player.isOnline());
                        
            player.setFireTicks(0);
            player.setHealth(20);
            player.setFoodLevel(20);    
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            
            player.SetDead(false);
            player.SetSpectating(false);
            
            player.GetPlayer().eject();

            if (player.GetPlayer().isInsideVehicle())
            	player.GetPlayer().leaveVehicle();
            
            player.teleport(SpawnLocation);
            
            ClearCombatEvent clearCombatEvent = new ClearCombatEvent(player.GetPlayer());
            Plugin.getServer().getPluginManager().callEvent(clearCombatEvent);
        }
    	
    	for (PlayerType player : game.GetSpectators())
        {
            RemoveSpectatorFromGame(player.GetPlayer(), !player.isOnline(), false);
                        
            player.setFireTicks(0);
            player.setHealth(20);
            player.setFoodLevel(20);    
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            
            player.SetDead(false);
            player.SetSpectating(false);
            
            player.GetPlayer().eject();

            if (player.GetPlayer().isInsideVehicle())
            	player.GetPlayer().leaveVehicle();
            
            player.teleport(SpawnLocation);
            
            ClearCombatEvent clearCombatEvent = new ClearCombatEvent(player.GetPlayer());
            Plugin.getServer().getPluginManager().callEvent(clearCombatEvent);
        }
        
        ActiveGames.remove(game);
        ArenaType arena = game.GetArena();
        
        // Deactivate sets arena to null for memory purposes
        game.Deactivate();

        ArenaManager.RemoveArena(arena);
        TryToActivateGames();
        
        Plugin.getServer().shutdown();
    }

    public Boolean CanScheduleNewGame()
    {
        if (Scheduler.GetGames().size() > 0 && Scheduler.GetGames().get(Scheduler.GetGames().size()-1).GetPlayers().size() < 10)
        {
            return false;
        }
        
        if (!ArenaManager.HasAvailableArena())
        {
            return false;
        }
        
        return true;
    }

    protected abstract void TryToActivateGames();
    
    protected boolean AddPlayerToGame(GameType game, Player player, Boolean notify)
    {                   
        PlayerType gamePlayer = game.AddPlayerToGame(player);
        PlayerGameMap.put(gamePlayer.getName(), game);
        
        if (notify)
        {
            // TODO Notifier.NotifyPlayerJoinGame(game, gamePlayer);
        }
        
        TryToActivateGames();
            
        // TODO UpdateScoreboard();
        
        return true;
    }
    
    public void AddSpectatorToGame(GameType game, Player player, Location location)
    {
    	if (IsPlayerInActiveGame(player))
    		return;
    	
		if (IsSpectatorInGame(player))
		{
			if (SpectatorGameMap.get(player.getName()) != game)
				RemoveSpectatorFromGame(player, false);
			else
			{
				player.teleport(location);
				return;
			}
		}
	    
	    PlayerType spectator = game.AddSpectatorToGame(player, location);
	    SpectatorGameMap.put(spectator.getName(), game);
    }
    
    @Override
    public boolean AddPlayerToGame(Player player, boolean notify)
    {
        GameType game = GetNextOpenGame();
        
        if (game == null)
        {
            return false;
        }
        
        return AddPlayerToGame(game, player, notify);
    }
    
    @Override
    public List<GameType> GetGames()
    {
        return Scheduler.GetGames();
    }
    
    @Override
    public List<GameType> GetActiveGames()
    {
        return ActiveGames;
    }

    @Override
    public void RemovePlayerFromGame(Player player)
    {
    	RemovePlayerFromGame(player, false);
    }
    
    @Override
    public void RemovePlayerFromGame(Player player, boolean quit)
    {
        GameType game = GetGameForPlayer(player);
        PlayerType gamePlayer = game.GetPlayer(player);
        
        RemovePlayerFromGame(game, gamePlayer, quit);
        
        player.playSound(player.getLocation(), Sound.BLAZE_DEATH, .5F, .5F);
    }
    
    protected void RemovePlayerFromGame(GameType game, PlayerType player, boolean quit)
    {   
        PlayerGameMap.remove(player.getName());
        
        if (!ActiveGames.contains(game))
        {
            game.RemovePlayer(player);
            Scheduler.CleanEmptyGames();
        }
    }

    public void RemoveSpectatorFromGame(Player player, boolean quit)
    {
    	RemoveSpectatorFromGame(player, quit, true);
    }
    
    public void RemoveSpectatorFromGame(Player player, boolean quit, boolean removeAndTeleport)
    {
    	GameType game = SpectatorGameMap.get(player.getName());
        SpectatorGameMap.remove(player.getName());
        
        if (removeAndTeleport)
        {
	        game.RemoveSpectator(game.GetSpectator(player));
	        
	        player.eject();
	        player.leaveVehicle();
	        player.teleport(SpawnLocation);
        }
    }

    public GameType GetNextOpenGame()
    {
    	if (ActiveGames.size() > 0 && AddToActiveGame && !IsGameFull(ActiveGames.get(0)))
    	{
    		return ActiveGames.get(0);
    	}
    	else if (Scheduler.GetGames().size() > 0)
        {
            return Scheduler.GetGames().get(Scheduler.GetGames().size() - 1);
        }
        
        return ScheduleNewGame();
    }
    
    protected boolean IsGameFull(GameType gameType)
	{
		return false;
	}

	@Override
    public boolean IsPlayerInGame(Player player)
    {
        return PlayerGameMap.containsKey(player.getName());
    }
    
    public boolean IsPlayerInGame(String playerName)
    {
        return PlayerGameMap.containsKey(playerName);
    }
   
	@EventHandler
	public void restartServerCheck(RestartServerEvent event)
	{
		if (ActiveGames.size() > 0 || GamesInSetup.size() > 0)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void MessageMOTD(ServerListPingEvent event)
	{
		if (ActiveGames.size() > 0 || GamesInSetup.size() > 0)
		{
			event.setMotd(ChatColor.YELLOW + "In Progress");
		}
		else
		{
			event.setMotd(ChatColor.GREEN + "Recruiting");
		}
		
		event.setMaxPlayers(10);
	}

    @EventHandler
    public void PlayerGameRequest(PlayerGameAssignmentEvent event)
    {
    	ConnectingPlayersToAddToGame.add(event.GetPlayerName());
    	
    	if (ConnectingPlayersToAddToGame.size() >= 10)
    	{
    		HubConnection.QueuePacket(new GameReadyPacket(new ArrayList<String>(ConnectingPlayersToAddToGame)));	
    	}
    }
    
    @Override
    public boolean IsSpectatorInActiveGame(Player player)
    {
        return IsSpectatorInGame(player) && ActiveGames.contains(GetGameForSpectator(player));
    }

    @Override
    public boolean IsPlayerInActiveGame(String playerName)
    {
        return IsPlayerInGame(playerName) && ActiveGames.contains(GetGameForPlayer(playerName));
    }
    
    @Override
    public GameType GetGameForPlayer(Player player)
    {
        return GetGameForPlayer(player.getName());
    }
    
    @Override
    public GameType GetGameForPlayer(String playerName)
    {
    	if (PlayerGameMap.containsKey(playerName))
    		return PlayerGameMap.get(playerName);
    	else
    		return SpectatorGameMap.get(playerName);
    }
    
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnCustomEvent event)
    {
    	event.setCancelled(true);
    }
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void OnWeatherChange(WeatherChangeEvent event) 
    {
        if (event.toWeatherState())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            if (IsPlayerInActiveGame((Player)event.getEntity()))
            {
                GameType game = GetGameForPlayer((Player)event.getEntity());
                
                if (game.HasStarted() && (event.getEntity().getLastDamageCause() == null || event.getEntity().getLastDamageCause().getEntity() instanceof Player))
                {
                    PlayerType victim = game.GetPlayer((Player) event.getEntity());
                    PlayerType attacker = null;
                    
                    if (victim.IsDead() || victim.IsSpectating())
                        event.setCancelled(true);
                    
                    if (event instanceof EntityDamageByEntityEvent)
                    {
                        EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent)event;
                        Entity damager = entityEvent.getDamager();
                        
                        if(entityEvent != null)
                        {
                            if (damager instanceof Projectile)
                            {
                                damager = ((Projectile)entityEvent.getDamager()).getShooter();
                            }
                            
                            if (damager instanceof Player)
                            {
                            	if (IsPlayerInActiveGame((Player)damager))
                            		attacker = game.GetPlayer((Player)damager);
                            	else if (IsSpectatorInGame((Player)damager))
                            		attacker = game.GetSpectator((Player)damager);
                            }
                        }
                    }

                    if (attacker != null)
                    {
                    	if (attacker.IsDead() || attacker.IsSpectating())
                    	{
	                        event.setCancelled(true);
                    	}
                    	else
                    	{
                    		GamePlayerAttackedPlayerEvent<GameType, PlayerType> customEvent = new GamePlayerAttackedPlayerEvent<GameType, PlayerType>(game, attacker, victim);
	                        Plugin.getServer().getPluginManager().callEvent(customEvent);
	                        
	                        if (customEvent.isCancelled())
	                        {
	                            event.setCancelled(true);
	                        }
                    	}
                    }
                }
                else
                {
                    event.setCancelled(true);
                }
            }
            else
            {
            	Player victim = (Player) event.getEntity();            	
                Player attacker = null;
                
                if (event instanceof EntityDamageByEntityEvent)
                {
                    EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent)event;
                    Entity damager = entityEvent.getDamager();
                    
                    if(entityEvent != null)
                    {
                        if (damager instanceof Projectile)
                        {
                            damager = ((Projectile)entityEvent.getDamager()).getShooter();
                        }
                        
                        if (damager instanceof Player)
                        {
                            attacker = (Player) damager;                                                                                        
                        }
                    }
                }
                
                if (attacker != null && victim.isOp() && attacker.isOp())
                {
                    return;
                }
                
                event.setCancelled(true);
            }
        }
        else	
        {
        	event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event)
    {
    	event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDeath(CombatDeathEvent event)   
    {
        if (event.GetEvent().getEntity() instanceof Player)
        {
            Player victim = (Player) event.GetEvent().getEntity();                 
            
            if (!IsPlayerInGame(victim))
            {
                return;
            }
            
            GameType game = GetGameForPlayer(victim);            
            
            World.playSound(victim.getLocation(), Sound.BREATH, .5F, .3F);
            
            PlayerType gamekiller = null;
            List<PlayerType> assistants = new ArrayList<PlayerType>();
            
            if (!event.GetClientCombat().GetDeaths().isEmpty())
            {
	            CombatLog victimCombatLog = event.GetClientCombat().GetDeaths().getFirst();
	            List<CombatComponent> attackers = victimCombatLog.GetAttackers();
	            
	            for (CombatComponent component : attackers)
	            {
	                if (component == victimCombatLog.GetKiller())
	                    continue;
	                
	                if (!component.IsPlayer())
	                    continue;
	                
	                Player player = Bukkit.getPlayer(component.GetName());
	                
	                if (player == null)
	                	continue;
	                
	                PlayerType assistant = game.GetPlayer(player);
	                
	                if (assistant != null)
	                {
	                	assistant.AddAssists(1);
	                	assistants.add(assistant);
	                }
	            }
	            
	            if (victimCombatLog.GetKiller() != null && Bukkit.getPlayer(victimCombatLog.GetKiller().GetName()) != null)
	            {
	                gamekiller = game.GetPlayer(Bukkit.getPlayer(victimCombatLog.GetKiller().GetName()));
	                
	                if (gamekiller != null)
	                	gamekiller.AddKills(1);
	                else
	                	System.out.println("Null gameplayer for " + victimCombatLog.GetKiller().GetName());
	            }
            }
            
            PlayerType gameVictim = game.GetPlayer(victim);
            gameVictim.AddDeaths(1);
            
            GamePlayerDeathEvent<GameType, PlayerType> customEvent = new GamePlayerDeathEvent<GameType, PlayerType>(game, gameVictim, gamekiller, assistants, event.GetEvent().getDrops(), event);
            Plugin.getServer().getPluginManager().callEvent(customEvent);
            event.GetEvent().getDrops().clear();
        }
        
        if (event.GetEvent() instanceof PlayerDeathEvent)
        {
            PlayerDeathEvent pDeathEvent = (PlayerDeathEvent)event.GetEvent(); 
            
            pDeathEvent.setDeathMessage("");
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event)
    {
        if (IsPlayerInActiveGame(event.getPlayer()) || IsSpectatorInActiveGame(event.getPlayer()))
        {
            event.setCancelled(true);
        }
        else if (!event.getPlayer().isOp())
        {
        	event.getPlayer().teleport(SpawnLocation);
        	event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event)
    {
        if (IsPlayerInActiveGame(event.getPlayer()))
        {
            event.setCancelled(true);
        }
        else if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
    {
        if (IsPlayerInActiveGame(event.getPlayer()))
        {
            event.setCancelled(true);
        }
        else if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event)
    {
        if (IsPlayerInActiveGame(event.getPlayer()))
        {
            event.setCancelled(true);
        }
        else if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }
    
    @EventHandler
    public void BurnCancel(BlockBurnEvent event)
    {
    	event.setCancelled(true);
    }
    
    @EventHandler
    public void SpreadCancel(BlockFromToEvent event)
    {
    	event.setCancelled(true);
    }

    @EventHandler
    public void GrowCancel(BlockGrowEvent event)
    {
    	event.setCancelled(true);
    }
    
    @EventHandler
    public void DecayCancel(LeavesDecayEvent event)
    {
    	event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerFoodBarChange(FoodLevelChangeEvent event)
    {
        if (!IsPlayerInActiveGame((Player)event.getEntity()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        if (event.getResult() != Result.KICK_BANNED && IsPlayerInGame(event.getPlayer()))
        {            
            event.allow();
            return;
        }
        
        if (ActiveGames.size() > 0 || GamesInSetup.size() > 0)
        {
        	event.disallow(Result.KICK_FULL, "Game in progress");
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        
        if (IsPlayerInGame(player))
        {
        	GameType game = GetGameForPlayer(player);
        	
            event.getPlayer().setNoDamageTicks(0);
            game.UpdateReconnectedPlayer(event.getPlayer());
            
            PlayerType gamePlayer = game.GetPlayer(player);
            
            if (ActiveGames.contains(game) && gamePlayer.GetPlayer().getWorld() != game.GetArena().GetWorld())
            {
                gamePlayer.teleport(gamePlayer.GetLastInArenaPosition());
            }
            
            if (ActiveGames.contains(game))
            	gamePlayer.StartTimePlay();
            
            onGamePlayerJoin(game, gamePlayer);
        }
        else if (AddToActiveGame)
		{
    		player.getInventory().setItem(1, ItemStackFactory.Instance.CreateStack(Material.WATCH, (byte)0, 1, (short)0, C.cGreen + "Return to Hub", 
    				new String[] {"", ChatColor.RESET + "Click while holding this", ChatColor.RESET + "to return to the Hub."}));
    		
        	AddPlayerToGame(event.getPlayer(), false);
        	
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
         
            player.teleport(SpawnLocation);
		}
        else
        {
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
         
            player.teleport(SpawnLocation);
            
            player.sendMessage(ChatColor.AQUA + "*************************************");
            player.sendMessage(C.cDGreen + "           Welcome to " + GetGameType() + "!");
            player.sendMessage(C.cDGreen + "  Right click book for instructions ");
            player.sendMessage(ChatColor.AQUA + "*************************************");
        }
    }
    
    @EventHandler
    public void onPlayerAfk(AfkEvent event)
    {
    	if (IsPlayerInActiveGame(event.GetPlayerName()))
    	{
    		GameType game = GetGameForPlayer(event.GetPlayerName());
    		PlayerType gamePlayer = game.GetPlayer(event.GetPlayerName());
    		
            Plugin.getServer().getPluginManager().callEvent(new GamePlayerAfkEvent<GameType, PlayerType>(game, gamePlayer));
            
            /*
            RemovePlayerFromGame(game, gamePlayer, !gamePlayer.isOnline());
            
            if (gamePlayer.isOnline())
            	gamePlayer.teleport(Plugin.GetSpawnLocation());
           */
    	}
    	else
    	{    		
    		if (IsPlayerInGame(event.GetPlayerName()))
    		{
    			Player player = Bukkit.getPlayerExact(event.GetPlayerName());
    			
    			if (player != null)
    			{
    				RemovePlayerFromGame(Bukkit.getPlayerExact(event.GetPlayerName()));
    				player.sendMessage(ChatColor.RED + "You were removed from the Play Queue for being afk.");
    				Portal.SendPlayerToServer(player, "Lobby");    				
    			}
    		}
    	}
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {   
        if (event.isCancelled())
            return;

        event.setCancelled(true);
    }
    
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		if (event.getMessage().startsWith("/spectate") || event.getMessage().startsWith("/spec"))
		{
			if (IsPlayerInActiveGame(event.getPlayer()))
			{
				event.getPlayer().sendMessage(F.main("Dominate", ChatColor.RED + "You are already in a game, doh."));
			}
			else
			{
				String messageSplit[] = event.getMessage().split(" ");
				
				if (messageSplit.length == 2)
				{
					Player player = UtilPlayer.searchOnline(event.getPlayer(), messageSplit[1], true);
					
					if (player != null)
					{
						if (IsPlayerInActiveGame(player))
						{
							AddSpectatorToGame(GetGameForPlayer(player), event.getPlayer(), player.getLocation());
						}
						else
						{
							event.getPlayer().sendMessage(F.main("Dominate", ChatColor.RED + player.getName() + " isn't in a game."));
						}
					}
				}
				else if (this.IsSpectatorInGame(event.getPlayer()))
				{
					this.RemoveSpectatorFromGame(event.getPlayer(), false);
				}
				else
				{
					event.getPlayer().sendMessage(F.main("Dominate", ChatColor.RED + "Incorrect syntax.  Example : '/spectate defek7' or '/spectate' to leave"));
				}
			}

			event.setCancelled(true);
		}
		else if (event.getMessage().trim().equalsIgnoreCase("/stopgame"))
		{
			if (ClientManager.Get(event.getPlayer()).GetRank().Has(event.getPlayer(), Rank.MODERATOR, true))
			{
				GameType game = null;
				
				if (IsSpectatorInGame(event.getPlayer()))
				{
					game = GetGameForSpectator(event.getPlayer());					
				}
				else if (IsPlayerInActiveGame(event.getPlayer()))
				{
					game = GetGameForPlayer(event.getPlayer());
				}
				
				if (game != null)
				{
					for (PlayerType player : game.GetPlayers())
					{
						player.sendMessage(ChatColor.RED + event.getPlayer().getName() + " stopped the game.");
					}
					
					StopGame(game);
				}
				else
					event.getPlayer().sendMessage(ChatColor.RED + "You must be in or spectating an active game to issue '/stopgame'.");
				
				event.setCancelled(true);
			}
		}
		else if (event.getMessage().startsWith("/spawn"))
		{
			if (!IsPlayerInActiveGame(event.getPlayer()) && !IsSpectatorInActiveGame(event.getPlayer()))
			{
				event.getPlayer().teleport(SpawnLocation);
			}
			
			event.setCancelled(true);
		}
	}
	
    @SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPickupItem(PlayerPickupItemEvent event)
    {   
        if (event.isCancelled())
            return;

        if (!IsPlayerInActiveGame(event.getPlayer()) || IsSpectatorInActiveGame(event.getPlayer()) || event.getItem().getItemStack().getType() == Material.BONE)
        {
            event.setCancelled(true);
        }
        else
        {
            PlayerType gamePlayer = GetGameForPlayer(event.getPlayer()).GetPlayer(event.getPlayer());
            
            if (gamePlayer.IsDead() || gamePlayer.IsSpectating())
            {
                event.setCancelled(true);
                return;
            }
            
            if (event.getItem().getItemStack().getType() != Material.ARROW)
            {
                event.setCancelled(true);
                event.getItem().remove();
                return;
            }

            PlayerInventory inventory = event.getPlayer().getInventory();
            int typeSlot = inventory.first(event.getItem().getItemStack().getType());
            ItemStack itemStack = event.getItem().getItemStack();
            
            if (typeSlot != -1)
            {
            	ItemStack firstFoundItemStack = inventory.getItem(typeSlot);
            	int pickupAmount = itemStack.getAmount();
            	int maxAmount = (firstFoundItemStack.getType() == Material.NETHER_STAR ? 4 : (firstFoundItemStack.getType() == Material.ARROW ? 64 : 1));
            	
            	while (typeSlot < 9)
            	{
	            	ItemStack existingItemStack = inventory.getItem(typeSlot);
	            	
	            	if (existingItemStack != null && existingItemStack.getType() == itemStack.getType())
	            	{
		            	int existingAmount = existingItemStack.getAmount() + pickupAmount;
		            	
	            		if (existingAmount > maxAmount)
	            		{
	            			pickupAmount = existingAmount - maxAmount;
	            			existingAmount = maxAmount;
	            		}
	            		else
	            		{
	            			pickupAmount = 0;
	            		}
	
	            		inventory.getItem(typeSlot).setAmount(existingAmount);
	            		existingAmount = 0;
	            		
	            		if (pickupAmount == 0)
	            			break;
	            	}
	            	
	            	typeSlot++;
            	}
            	
        		if (pickupAmount > 0)
        		{
        			ItemStack newItemStack = firstFoundItemStack.clone();
        			int existingAmount = pickupAmount;
        			maxAmount = (newItemStack.getType() == Material.NETHER_STAR ? 4 : (newItemStack.getType() == Material.ARROW ? 64 : 1));
        			
        			while (inventory.firstEmpty() < 9)
        			{
	            		if (existingAmount > maxAmount)
	            		{
	            			pickupAmount = existingAmount - maxAmount;
	            			existingAmount = maxAmount;
	            		}
	            		else
	            		{
	            			pickupAmount = 0;
	            		}
	            		
	            		newItemStack.setAmount(existingAmount);
        				inventory.setItem(inventory.firstEmpty(), newItemStack);
        				existingAmount = pickupAmount;
        				
        				if (pickupAmount == 0)
        					break;
        				
        				newItemStack = firstFoundItemStack.clone();
        			}
        		}
            	
            	event.setCancelled(true);
            	
            	if (pickupAmount == 0)
            		event.getItem().remove();
            }
            
            if (inventory.firstEmpty() > 8)
            {
            	event.setCancelled(true);
            }
            
            event.getPlayer().updateInventory();
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        
        if (IsSpectatorInGame(player))
        {
            RemoveSpectatorFromGame(player, true);
        }
        
        if (IsPlayerInGame(player))
        {
            GameType game = GetGameForPlayer(player);
            PlayerType gamePlayer = game.GetPlayer(player);
            
            if (ActiveGames.contains(game) || GamesInSetup.contains(game))
            {
                GamePlayerQuitEvent<GameType, PlayerType> customEvent = new GamePlayerQuitEvent<GameType, PlayerType>(game, gamePlayer);
                Plugin.getServer().getPluginManager().callEvent(customEvent);
                
                gamePlayer.StopTimePlay();
                
                //PendingPlayerRemoveMap.put(gamePlayer.getName(), System.currentTimeMillis() + LogoutPeriod * 1000);
            }
            else
            {
                RemovePlayerFromGame(player, true);
            }
        }
    }
    
    private GameType GetGameForSpectator(Player player) 
    {
		return SpectatorGameMap.get(player.getName());
	}

	private boolean IsSpectatorInGame(Player player) 
    {
		return SpectatorGameMap.containsKey(player.getName());
	}
	
    @Override
    public boolean IsPlayerInActiveGame(Player player)
    {
        return IsPlayerInGame(player) && ActiveGames.contains(GetGameForPlayer(player));
    }

	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInventoryClick(InventoryClickEvent event)
    {    	
    	if (IsPlayerInActiveGame((Player)event.getWhoClicked()) || IsSpectatorInActiveGame((Player)event.getWhoClicked()))
    	{
	    	if (event.getSlotType() != SlotType.QUICKBAR || event.isShiftClick() || event.getCursor().getType() == event.getCurrentItem().getType())
	    	{
	    		event.setCancelled(true);
	    	}
    	}
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        
        if (IsSpectatorInActiveGame(player))
        {
        	event.setCancelled(true);
        	return;
        }
        
        if (event.getAction() == Action.PHYSICAL)
        {
            if (event.getClickedBlock().getType() == Material.SOIL)
            {
                event.setCancelled(true);
                return;
            }
        }
        
        if (IsPlayerInActiveGame(player))
        {
            if (player.getItemInHand() != null)
            {
            	if (player.getItemInHand().getType() == Material.BUCKET)
            	{
                	event.setCancelled(true);
                }
            	else if (player.getItemInHand().getType() == Material.FIREBALL || player.getItemInHand().getType() == Material.FIRE)
            	{
                	event.setCancelled(true);
                }		
            }
        }
        else
        {
        	if (player.getItemInHand().getType() == Material.WATCH)
            {
        		Portal.SendPlayerToServer(player, "Lobby");
            }
        	
        	if (player.isOp())
        	{
	        	if (player.getItemInHand().getType() == Material.GHAST_TEAR)
	        	{
	        		/*
	                final Packet20NamedEntitySpawn packet = new Packet20NamedEntitySpawn(((CraftPlayer)player).getHandle());
	                packet.a = 9919;
	                packet.b = player.getName();
	                packet.c = MathHelper.floor(player.getLocation().getX() * 32.0D);
	                packet.d = MathHelper.floor(player.getLocation().getY() * 32.0D);
	                packet.e = MathHelper.floor(player.getLocation().getZ() * 32.0D);
	                packet.f = (byte) ((int) (0 * 256.0F / 360.0F));
	                packet.g = (byte) ((int) (0 * 256.0F / 360.0F));
	        		
	                DataWatcher dataWatcher = new DataWatcher();
	                
	        		dataWatcher.a(0, Byte.valueOf((byte) 32));
	        		dataWatcher.watch(0, Byte.valueOf((byte) 32));
	        		dataWatcher.a(1, Short.valueOf((short)300));
	        		dataWatcher.watch(1, Short.valueOf((short)300));
	        		dataWatcher.a(8, 8356754);
	        		dataWatcher.watch(8, 8356754);
	        		dataWatcher.a(9, (byte)0);
	        		dataWatcher.watch(9, (byte)0);
	        		dataWatcher.a(10, (byte)0);
	        		dataWatcher.watch(10, (byte)0);               
	
	        		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	        		((CraftPlayer)player).getHandle().playerConnection.sendPacket(new Packet5EntityEquipment(9919, 0, null));
	                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new Packet40EntityMetadata(9919, dataWatcher, false));
	                
	                Packet23VehicleSpawn itemPacket = new Packet23VehicleSpawn(); 
	                itemPacket.a = 9919 + 2 * new Random().nextInt(30);
	                itemPacket.b = (byte)0;
	                itemPacket.c = (byte)0;
	                itemPacket.d = (byte)0;
	                itemPacket.e = 0;
	                itemPacket.f = 0;
	                itemPacket.g = 0;
	                itemPacket.h = 0;
	                itemPacket.i = 0;
	                itemPacket.j = 2;
	                itemPacket.k = 1;
	                
	                dataWatcher = new DataWatcher();
	                
	        		dataWatcher.a(0, Byte.valueOf((byte) 0));
	        		dataWatcher.watch(0, Byte.valueOf((byte) 0));
	        		dataWatcher.a(1, Short.valueOf((short)300));
	        		dataWatcher.watch(1, Short.valueOf((short)300));
	        		dataWatcher.a(10, new net.minecraft.server.v1_4_6.ItemStack(net.minecraft.server.v1_4_6.Item.STONE_SWORD, 1));
	        		dataWatcher.watch(10, new net.minecraft.server.v1_4_6.ItemStack(net.minecraft.server.v1_4_6.Item.STONE_SWORD, 1));   
	                
	                Packet39AttachEntity vehiclePacket = new Packet39AttachEntity();
	                vehiclePacket.a = itemPacket.a;
	                vehiclePacket.b = 9919;
	                
	                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(itemPacket);
	                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(vehiclePacket);
	                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new Packet40EntityMetadata(itemPacket.a, dataWatcher, false));
	                
	                vehiclePacket = new Packet39AttachEntity();
	                vehiclePacket.a = 9919;
	                vehiclePacket.b = Bukkit.getPlayer("jRayx").getEntityId();
	                
	                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(vehiclePacket);
	                */
	        	}
	            if (player.getItemInHand().getType() == Material.BLAZE_ROD)
	            {
	                if (event.getAction() == Action.LEFT_CLICK_AIR)
	                {
	                    player.setItemInHand(new ItemStack(Material.BLAZE_ROD, player.getItemInHand().getAmount() + 1));
	                    int index = player.getItemInHand().getAmount() - 1;
	                    player.sendMessage("Sound." + Sound.values()[index]);
	                }
	                else if (event.getAction() == Action.RIGHT_CLICK_AIR)
	                {
	                    player.setItemInHand(new ItemStack(Material.BLAZE_ROD, player.getItemInHand().getAmount() - 1));
	                    
	                    int index = player.getItemInHand().getAmount() - 1;
	                    player.sendMessage("Sound." + Sound.values()[index]);
	                }
	            }
	            
	            if (player.getItemInHand().getType() == Material.STICK)
	            {
	                if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR)
	                {
	                    player.damage(1d, Bukkit.getPlayer("Chiss"));
	                    return;
	                }
	            }
	            
	            if (player.getItemInHand().getType() == Material.STRING)
	            {
	                if (event.getAction() == Action.LEFT_CLICK_AIR)
	                {
	                    player.setItemInHand(new ItemStack(Material.STRING, player.getItemInHand().getAmount() + 1));
	                }
	                else if (event.getAction() == Action.RIGHT_CLICK_AIR)
	                {
	                    player.setItemInHand(new ItemStack(Material.STRING, player.getItemInHand().getAmount() - 1));
	                }
	            }
	            
	            if (player.getItemInHand().getType() == Material.JUKEBOX)
	            {
	                if (player.getInventory().getItem(0).getAmount() - 1 > Sound.values().length)
	                {
	                    player.getInventory().setItem(0, new ItemStack(Material.BLAZE_ROD, 0));
	                }
	                else if (player.getInventory().getItem(0).getAmount() - 1 < 0)
	                {
	                    player.getInventory().setItem(0, new ItemStack(Material.BLAZE_ROD, Sound.values().length));
	                }
	                
	                if (player.getInventory().getItem(1).getAmount() - 1 > 30)
	                {
	                    player.getInventory().setItem(1, new ItemStack(Material.STRING, 0));
	                }
	                else if (player.getInventory().getItem(1).getAmount() - 1 < 0)
	                {
	                    player.getInventory().setItem(1, new ItemStack(Material.STRING, 10));
	                }
	                
	                int index = player.getInventory().getItem(0).getAmount() - 1;
	                float pitch = (player.getInventory().getItem(1).getAmount() - 1) * .1F;
	
	                System.out.println("index: " + index + " pitch: " + pitch);
	                
	                player.getWorld().playSound(player.getLocation(), Sound.values()[index] , .3F, pitch);
	                player.sendMessage("Sound." + Sound.values()[index]);
	            }
        	}
        	
            if (player.getItemInHand().getType() == Material.EYE_OF_ENDER)
            {
            	Portal.SendPlayerToServer(player, "lobby");
	            event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        
        if (IsPlayerInActiveGame(player) || IsSpectatorInActiveGame(player))
        {
            GameType game = GetGameForPlayer(player);
            
            if (game == null)
            	game = GetGameForSpectator(player);
            
            PlayerType gamePlayer = game.GetPlayer(player);
            
            if (gamePlayer == null)
            	gamePlayer = game.GetSpectator(player);
                    
            if (!game.CanMove(gamePlayer, event.getFrom(), event.getTo()))
            {
                if (game.IsInArena(event.getFrom()))
                {
                    gamePlayer.SetLastInArenaPosition(event.getFrom().getWorld(), event.getFrom().getX(), event.getFrom().getY(), event.getFrom().getZ());
                }
                
            	player.eject();
            	player.leaveVehicle();
                
                gamePlayer.teleport(gamePlayer.GetLastInArenaPosition());
                
                if (game.HasStarted() && !gamePlayer.IsDead())
                	gamePlayer.GetPlayer().damage(4d);
            }
            else
                gamePlayer.SetLastInArenaPosition(event.getTo().getWorld(), event.getTo().getX(), event.getTo().getY(), event.getTo().getZ());;
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        Player player = event.getPlayer();
        
        if (IsPlayerInActiveGame(player) || IsSpectatorInActiveGame(player))
        {
            GameType game = GetGameForPlayer(player);
            
            if (game == null)
            	game = GetGameForSpectator(player);
            
            PlayerType gamePlayer = game.GetPlayer(player);
            
            if (gamePlayer == null)
            	gamePlayer = game.GetSpectator(player);

            if (!game.CanMove(gamePlayer, event.getFrom(), event.getTo()))
            {
                if (game.IsInArena(event.getFrom()))
                {
                    gamePlayer.SetLastInArenaPosition(event.getFrom().getWorld(), event.getFrom().getX(), event.getFrom().getY(), event.getFrom().getZ());
                }
                
                event.setCancelled(true);
                gamePlayer.teleport(gamePlayer.GetLastInArenaPosition());
            }
            else
            {
                gamePlayer.SetLastInArenaPosition(event.getTo().getWorld(), event.getTo().getX(), event.getTo().getY(), event.getTo().getZ());
            }
        }
        else if (event.getTo() == SpawnLocation)
        {
            GivePlayerLobbyItems(player);
            //ShowArmorSetsToPlayer(player);
        }
    }
    
    /*
    @EventHandler
    public void onPlayerVote(PlayerVoteEvent event)
    {
    	System.out.println("Received player vote event : " + event.GetPlayerName());
    	
    	CoreClient client = Plugin.GetClients().GetNull(event.GetPlayerName());
    	
		if (client != null)
		{
			client.Donor().AddPoints(event.GetPointsReceived());
		
    		client.GetPlayer().sendMessage(ChatColor.AQUA + "*************************************");
    		client.GetPlayer().sendMessage(C.cDGreen + "           Thanks for voting!");
    		client.GetPlayer().sendMessage(C.cDGreen + "       You received " + ChatColor.YELLOW + event.GetPointsReceived() + C.cDGreen + " points! ");
    		client.GetPlayer().sendMessage(ChatColor.AQUA + "*************************************");
    		client.GetPlayer().playSound(client.GetPlayer().getLocation(), Sound.LEVEL_UP, .3f, 1f);
    		
    		
    		UpdatePlayerLobbyItemBalances(client);
    		
    		for (Player player : Plugin.GetPlugin().getServer().getOnlinePlayers())
    		{
    			if (player == client.GetPlayer())
    				continue;
    			
    			if (!IsPlayerInActiveGame(player))
    			{
    				player.sendMessage(F.main("Vote", ChatColor.YELLOW + event.GetPlayerName() + ChatColor.GRAY + " voted at nautmc.com/Vote for " + ChatColor.YELLOW + event.GetPointsReceived() + C.cGray + " points! "));
    			}
    		}
		}
    }
    */
    
    @EventHandler
    public void onServerSaveEvent(ServerSaveEvent event)
    {
    	event.setCancelled(true);
    }
    
	public void onGamePlayerJoin(GameType game, PlayerType gamePlayer)
	{
		if (game.HasStarted())
		{
			ScoreHandler.RewardForDeath(gamePlayer);

			if (!PlayerTaskIdMap.containsKey(gamePlayer.getName()))
			{
				game.RespawnPlayer(gamePlayer);
				game.ResetPlayer(gamePlayer);

				gamePlayer.SetSpectating(false);
				gamePlayer.SetDead(false);
				
				ConditionManager.EndCondition(gamePlayer.GetPlayer(), ConditionType.CLOAK, null);
				ConditionManager.Factory().Regen("Respawn", gamePlayer.GetPlayer(), gamePlayer.GetPlayer(), 7, 1, false, true, true);
			}
		}
	}
    
    protected void GivePlayerLobbyItems(Player player)
    {
    	player.getInventory().clear();
    	player.getInventory().setArmorContents(new ItemStack[4]);

		player.getInventory().setItem(1, ItemStackFactory.Instance.CreateStack(Material.WATCH, (byte)0, 1, (short)0, C.cGreen + "Return to Hub", 
				new String[] {"", ChatColor.RESET + "Click while holding this", ChatColor.RESET + "to return to the Hub."}));
        /*
        if (IsPlayerInGame(player))
        	player.getInventory().addItem(QueuedItem.clone());
        else
        	player.getInventory().addItem(NotQueuedItem.clone());
        
        
        
        UpdatePlayerLobbyItemBalances(client);
        
        for (Entry<EntityType, String> petToken : client.Donor().GetPets().entrySet())
        {
        	ItemStack petEgg = new ItemStack(Material.MONSTER_EGG, 1, (byte)petToken.getKey().getTypeId()); 
        	ItemMeta meta = petEgg.getItemMeta();
        	meta.setDisplayName(ChatColor.GREEN + petToken.getValue());
        	
        	petEgg.setItemMeta(meta);
        	player.getInventory().addItem(petEgg);
        }
        
        int nameTagCount = client.Donor().GetPetNameTagCount();
        
        if (nameTagCount > 0) 
        {
	        ItemStack nameTags = new ItemStack(Material.SIGN, client.Donor().GetPetNameTagCount());
	        ItemMeta meta = nameTags.getItemMeta();
	        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Name Tag");	
	        nameTags.setItemMeta(meta);
	        player.getInventory().addItem(nameTags);
        }
        */
    }
    
    public boolean IsInLobby(Player player)
    {
    	return !IsPlayerInActiveGame(player) && !IsSpectatorInActiveGame(player);
    }
    
    public boolean CanHurt(Player a, Player b)
    {
        if (IsPlayerInActiveGame(b) && IsPlayerInActiveGame(a))
        {
            GameType game = GetGameForPlayer(b);
            
            if (game.HasStarted())
            {
                PlayerType victim = game.GetPlayer(b);
                
                if (victim.IsDead() || victim.IsSpectating())
                    return false;
            }
        }
        
        return true;
    }
    
    public ChatColor GetColorOfFor(String other, Player player)
    {
    	ChatColor prefixColor = null;
    	
        if (IsPlayerInGame(other))
        {
        	prefixColor = ChatColor.GREEN;            
        }
        else if (ClientManager.Get(other) != null && ClientManager.Get(other).GetRank().Has(player, Rank.ADMIN, false))
        {
        	prefixColor = ChatColor.DARK_RED; 
        }
        else if (ClientManager.Get(other) != null && ClientManager.Get(other).GetRank().Has(player, Rank.MODERATOR, false))
        {
        	prefixColor = ChatColor.RED; 
        }
        else
        {
        	prefixColor = ChatColor.YELLOW; 
        }
        
        return prefixColor;
    }
    
    public boolean CanHurt(String a, String b)
    {
        return CanHurt(Bukkit.getPlayerExact(a), Bukkit.getPlayerExact(b));
    }
    
    @Override
    public boolean IsSafe(Player player)
    {
        return false;
    }
}
