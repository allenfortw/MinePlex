package nautilus.game.core.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import mineplex.core.energy.Energy;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import nautilus.game.core.arena.IArena;
import nautilus.game.core.notifier.PlayerNotifier;
import nautilus.game.core.player.IGamePlayer;
import nautilus.game.core.util.BroadcastSecondTimer;
import nautilus.minecraft.core.utils.GenericRunnable;

public abstract class Game<PlayerType extends IGamePlayer, ArenaType extends IArena> implements IGame<ArenaType, PlayerType>, Listener, Runnable
{
    protected JavaPlugin Plugin;
    protected ClassManager ClassManager;
    protected ConditionManager ConditionManager;
    protected Energy Energy;
    protected ArenaType Arena;
    protected Map<String, PlayerType> Players;
    protected Map<String, PlayerType> Spectators;
    protected int PlayerLives = 1;
    protected int ScoreLimit;
    protected long StartTime;
    protected boolean HasStarted;
    protected boolean CountdownRunning;
    protected BroadcastSecondTimer StartTimer;
    
    protected HashMap<String, Integer> PlayerTaskIdMap;
    
    protected int UpdaterTaskId;
    
    public Game(JavaPlugin plugin, ClassManager classManager, ConditionManager conditionManager, Energy energy)
    {
        Plugin = plugin;
        ClassManager = classManager;
        ConditionManager = conditionManager;
        Energy = energy;
        Players = new HashMap<String, PlayerType>();
        Spectators = new HashMap<String, PlayerType>();
               
        PlayerTaskIdMap = new HashMap<String, Integer>();
        
        Plugin.getServer().getPluginManager().registerEvents(this, Plugin);
    }
    
    public void run()
    {
        Update();
    }
    
    protected void Update() { }
    
    protected abstract PlayerType CreateGamePlayer(Player player, int playerLives);
    
    @Override
    public void RemovePlayer(PlayerType player)
    {
        if (Players.containsKey(player.getName()))
        {
            Players.remove(player.getName());
            ClearPlayerSettings(player);
        }
    }
    
    @Override
    public void RemoveSpectator(PlayerType player)
    {
        if (Spectators.containsKey(player.getName()))
        {
        	Spectators.remove(player.getName());
        	ClearSpectatorSettings(player);
        }
    }
    
    @Override
    public boolean IsPlayerInGame(Player player)
    {
        return Players.containsKey(player.getName());
    }
    
    @Override
    public boolean IsSpectatorInGame(Player player)
    {
        return Spectators.containsKey(player.getName());
    }
    
    @Override
    public PlayerType AddPlayerToGame(Player player)
    {
        PlayerType gamePlayer = CreateGamePlayer(player, PlayerLives);
        Players.put(gamePlayer.getName(), gamePlayer);
        gamePlayer.SetClass(ClassManager.Get(player));
        
        return gamePlayer;
    }
    
    @Override
    public PlayerType AddSpectatorToGame(Player player, Location to)
    {
        PlayerType gamePlayer = CreateGamePlayer(player, PlayerLives);
        Spectators.put(gamePlayer.getName(), gamePlayer);
        
        gamePlayer.SetSpectating(true);
        gamePlayer.teleport(to);
        gamePlayer.getInventory().addItem(new ItemStack(Material.SHEARS, 1));
        
        ConditionManager.Factory().Cloak("Spectator", player, player, 7200, false, true);
        
        return gamePlayer;
    }

    @Override
    public PlayerType GetPlayer(Player player)
    {
        return Players.get(player.getName());
    }
    
    @Override
    public PlayerType GetPlayer(String playerName)
    {
        return Players.get(playerName);
    }
    
    @Override
    public PlayerType GetSpectator(Player player)
    {
        return Spectators.get(player.getName());
    }

    @Override
    public Collection<PlayerType> GetPlayers()
    {
        return Players.values();
    }
    
    @Override
    public Collection<PlayerType> GetSpectators()
    {
        return Spectators.values();
    }
    
    @Override
    public void UpdateReconnectedPlayer(Player player)
    {
        Entry<String, PlayerType> oldPlayerEntry = GetGamePlayer(player.getName());
        
        if (oldPlayerEntry != null)
        {
            PlayerType oldPlayer = oldPlayerEntry.getValue();
            PlayerType newPlayer = CreateGamePlayer(player, oldPlayer.GetRemainingLives());
         
            UpdateNewPlayerWithOldPlayer(newPlayer, oldPlayer);
            
            ClientClass client = ClassManager.Get(player);
            
            if (oldPlayer.GetClass().GetDefaultItems() != null)
            {
	            for (Entry<Integer, ItemStack> item : oldPlayer.GetClass().GetDefaultItems().entrySet()) 
	            {
	                client.PutDefaultItem(item.getValue(), item.getKey());
	            }
            }
            
            if (oldPlayer.GetClass().GetDefaultArmor() != null)
            {
	            client.SetDefaultHead(oldPlayer.GetClass().GetDefaultArmor()[3]);
	            client.SetDefaultChest(oldPlayer.GetClass().GetDefaultArmor()[2]);
	            client.SetDefaultLegs(oldPlayer.GetClass().GetDefaultArmor()[1]);
	            client.SetDefaultFeet(oldPlayer.GetClass().GetDefaultArmor()[0]);
            }
            
            client.SetGameClass(oldPlayer.GetClass().GetGameClass());
            
            if (oldPlayer.GetClass().GetDefaultSkills() != null)
            {
            	client.ClearDefaultSkills();
            	
            	for (ISkill skill : oldPlayer.GetClass().GetDefaultSkills())
            	{
            		client.AddSkill(skill);
            	}
            }
            
            Players.put(player.getName(), newPlayer);
        }
    }

    @Override
    public List<PlayerType> GetAssailants(Player player)
    {
        // TODO
        return null;
    }

    @Override
    public boolean HasStarted()
    {
        return HasStarted;
    }

    @Override
    public boolean IsInArena(Location location)
    {
        return Arena.IsInArena(location.toVector());
    }
    
    @Override
    public boolean CanMove(PlayerType player, Location from, Location to)
    {
    	return !CountdownRunning || Arena.CanMove(player.getName(), from != null ? from.toVector() : null, to != null ? to.toVector() : null);
    }
    
    @Override
    public boolean CanInteract(PlayerType player, Block block)
    {       
        return !CountdownRunning || Arena.CanInteract(player.getName(), block);
    }
    
	public void StartRespawnFor(PlayerType player)
	{       
		ResetPlayer(player);

		player.SetSpectating(true);
		player.SetDead(true);

		ConditionManager.Factory().Cloak("Death", player.GetPlayer(), player.GetPlayer(), 10, false, true);

		PlayerTaskIdMap.put(player.getName(), Plugin.getServer().getScheduler().scheduleSyncDelayedTask(Plugin, new GenericRunnable<String>(player.getName())
		{
			public void run()
			{
				PlayerType player = GetPlayer(t);

				if (player != null && player.isOnline() && HasStarted)
				{   
					ResetPlayer(player);
					RespawnPlayer(player);

					player.SetSpectating(false);
					player.SetDead(false);
				}
				
				ConditionManager.EndCondition(player.GetPlayer(), ConditionType.CLOAK, null);
				ConditionManager.Factory().Regen("Respawn", player.GetPlayer(), player.GetPlayer(), 7, 3, true, true, true);

				PlayerTaskIdMap.remove(player.getName());
			}
		}, 200L));
	}
    
    public void ResetPlayer(PlayerType player)
    {
        player.setFireTicks(0);
        player.setHealth(20);
        player.setVelocity(new Vector(0,0,0));
        player.setFoodLevel(20);
        player.GetPlayer().setFallDistance(0);
        player.RemoveArrows();
        player.GetPlayer().eject();
        
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        
        for (PotionEffect potionEffect : player.GetPlayer().getActivePotionEffects())
        {
            player.GetPlayer().addPotionEffect(new PotionEffect(potionEffect.getType(), 0, 0), true);
        }
        
        if (player.GetPlayer().isOnline())
        {
        	Energy.Get(player.GetPlayer()).Energy = Energy.GetMax(player.GetPlayer());
        	ClassManager.Get(player.GetPlayer()).ResetSkills(player.GetPlayer());
        }
    }
    
    public void Activate(ArenaType arena)
    {
        Arena = arena;
        StartTimer = new BroadcastSecondTimer(new PlayerNotifier<IGame<ArenaType, PlayerType>, ArenaType, PlayerType>(Plugin, "Dominate"), this, 30, "Game starting", "Game starting...", new ActionListener() 
        {
            public void actionPerformed(ActionEvent evt) 
            {
                try 
                {
                    ReallyStartGame();
                } 
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
            }   
        });
        
        StartTimer.Start();
        
        CountdownRunning = true;
    }
    
    public boolean IsActive()
    {
    	return CountdownRunning || HasStarted;
    }
    
    protected void ReallyStartGame()
    {
        StartTime = System.currentTimeMillis();
        HasStarted = true;

        for (Entity entity : Arena.GetWorld().getEntitiesByClasses(Item.class, Arrow.class))
        {
            if (Arena.IsInArena(entity.getLocation().toVector()))
            {
                entity.remove();
            }
        }
        
        UpdaterTaskId = Plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin, this, 0L, 10L);
    }
    
    public void Deactivate()
    {
        for (PlayerType player : Players.values())
        {            
            ClearPlayerSettings(player);
        }
        
        for (PlayerType spectator : Spectators.values())
        {
            ClearSpectatorSettings(spectator);
        }
        
        Players.clear();
        Spectators.clear();
        
        if (StartTimer != null)
        	StartTimer.Deactivate();
        
        Plugin.getServer().getScheduler().cancelTask(UpdaterTaskId);
        HandlerList.unregisterAll(this);
        
        StartTimer = null;
        Arena = null;
        Plugin = null;
        Players = null;
        Spectators = null;
        HasStarted = false;
    }
    
    protected void ClearSpectatorSettings(PlayerType spectator) 
    {
    	ConditionManager.EndCondition(spectator.GetPlayer(), ConditionType.CLOAK, null);
    	spectator.SetSpectating(false);
	}

    protected void ClearPlayerSettings(PlayerType player) {	}

	protected Entry<String, PlayerType> GetGamePlayer(String playerName)
    {
        for (Entry<String, PlayerType> player : Players.entrySet())
        {
            if (player.getKey().equalsIgnoreCase(playerName))
            {
                return player;
            }
        }
        
        return null;
    }
    
    @Override
    public ArenaType GetArena()
    {
        return Arena;
    }
    
    @Override
    public long GetStartTime()
    {
        return StartTime;
    }

    @Override
    public int GetWinLimit()
    {
        return ScoreLimit;
    }
    
    protected void UpdateNewPlayerWithOldPlayer(PlayerType newPlayer, PlayerType oldPlayer)
    {
        newPlayer.SetLives(oldPlayer.GetRemainingLives());
        newPlayer.AddKills(oldPlayer.GetKills());
        newPlayer.AddAssists(oldPlayer.GetAssists());
        newPlayer.AddDeaths(oldPlayer.GetDeaths());
        newPlayer.AddPoints(oldPlayer.GetPoints());
        newPlayer.SetLogoutTime(oldPlayer.GetLogoutTime());
        newPlayer.SetDead(oldPlayer.IsDead());
        newPlayer.SetSpectating(oldPlayer.IsSpectating());
        newPlayer.SetClass(oldPlayer.GetClass());
        Location lastInArenaPosition = oldPlayer.GetLastInArenaPosition();
        
        if (lastInArenaPosition != null)
        {
            newPlayer.SetLastInArenaPosition(lastInArenaPosition.getWorld(), lastInArenaPosition.getX(), lastInArenaPosition.getY(), lastInArenaPosition.getZ());
        }
    }
}
