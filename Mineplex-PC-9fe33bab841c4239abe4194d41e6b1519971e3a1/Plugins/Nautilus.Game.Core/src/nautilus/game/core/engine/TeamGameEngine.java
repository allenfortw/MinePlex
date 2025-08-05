package nautilus.game.core.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;

import mineplex.core.packethandler.PacketHandler;
import mineplex.core.server.ServerTalker;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilServer;
import mineplex.core.donation.DonationManager;
import mineplex.core.energy.*;
import mineplex.core.npc.*;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.core.combat.CombatComponent;
import mineplex.minecraft.game.core.condition.*;
import nautilus.game.core.arena.ITeamArena;
import nautilus.game.core.arena.ArenaManager;
import nautilus.game.core.events.GamePlayerAttackedPlayerEvent;
import nautilus.game.core.events.GamePlayerDeathEvent;
import nautilus.game.core.events.team.TeamGameFinishedEvent;
import nautilus.game.core.game.ITeamGame;
import nautilus.game.core.player.ITeamGamePlayer;
import nautilus.game.core.scoreboard.ITeamScoreHandler;
import nautilus.minecraft.core.utils.GenericRunnable;
import net.minecraft.server.v1_6_R2.Packet;
import net.minecraft.server.v1_6_R2.Packet201PlayerInfo;

public abstract class TeamGameEngine<GameType extends ITeamGame<ArenaType, PlayerType, PlayerTeamType>, ScoreHandlerType extends ITeamScoreHandler<PlayerType, PlayerTeamType>, ArenaType extends ITeamArena, PlayerTeamType extends ITeam<PlayerType>, PlayerType extends ITeamGamePlayer<PlayerTeamType>> extends GameEngine<GameType, ScoreHandlerType, ArenaType, PlayerType> implements ITeamGameEngine<GameType, ArenaType, PlayerTeamType, PlayerType>
{
	private int _gameQueueTickValue;
	
    protected int TeamSize = 1;
    protected PacketHandler PacketHandler;

	protected boolean BroadcastQueueJoinMessage = true;
	protected int MinQueuePlayersToStart = 4;
	protected int TimeToStart = 2;
	
	protected NpcManager NpcManager;
	
	private List<Entity> _shopEntities = new ArrayList<Entity>();
	private NautHashMap<Player, Scoreboard> _scoreboardMap = new NautHashMap<Player, Scoreboard>();
    
    public TeamGameEngine(JavaPlugin plugin, ServerTalker hubConnection, CoreClientManager clientManager, DonationManager donationManager, ClassManager classManager, 
    		ConditionManager conditionManager, Energy energy, NpcManager npcManager, PacketHandler packetHandler, ArenaManager<ArenaType> arenaManager, ScoreHandlerType scoreHandler, World world, Location spawnLocation)
    {
        super(plugin, hubConnection, clientManager, donationManager, classManager, conditionManager, energy, arenaManager, scoreHandler, world, spawnLocation);
        
        NpcManager = npcManager;
        PacketHandler = packetHandler;
    }

    protected void TryToActivateGames()
    {
        Iterator<GameType> gameIterator = Scheduler.GetGames().iterator();
        
        while (gameIterator.hasNext())
        {
            GameType game = gameIterator.next();
            
            if (game.GetPlayers().size() == TeamSize * 2 && ArenaManager.HasAvailableArena() && (MaxGames == -1 || ActiveGames.size() < MaxGames))
            {
            	SetupGame(game);
                gameIterator.remove();
                _gameQueueTickValue = 0;
            }
        }
    }
    
    protected boolean IsGameFull(GameType gameType)
    {
    	return gameType.GetPlayers().size() >= TeamSize * 2;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerJoin(PlayerJoinEvent event)
    {
    	CreateScoreboard(event.getPlayer());
    }
    
	private void CreateScoreboard(Player player) 
	{
		_scoreboardMap.put(player, Bukkit.getScoreboardManager().getNewScoreboard());

		Scoreboard scoreboard = _scoreboardMap.get(player);
		Objective objective = scoreboard.registerNewObjective("§l" + "Score", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		for (Rank rank : Rank.values())
		{
			if (rank == Rank.ALL)
			{
				scoreboard.registerNewTeam(rank.Name).setPrefix("");
			}
			else
			{
				scoreboard.registerNewTeam(rank.Name).setPrefix(rank.Color + C.Bold + rank.Name.toUpperCase() + ChatColor.RESET + " " + ChatColor.WHITE);
			}

			if (rank == Rank.ALL)
			{
				scoreboard.registerNewTeam(rank.Name + "red".toUpperCase()).setPrefix(ChatColor.RED + "");
				scoreboard.registerNewTeam(rank.Name + "blue".toUpperCase()).setPrefix(ChatColor.BLUE + "");
			}
			else
			{
				scoreboard.registerNewTeam(rank.Name + "red".toUpperCase()).setPrefix(rank.Color + C.Bold + rank.Name.toUpperCase() + ChatColor.RESET + " " + ChatColor.RED);
				scoreboard.registerNewTeam(rank.Name + "blue".toUpperCase()).setPrefix(rank.Color + C.Bold + rank.Name.toUpperCase() + ChatColor.RESET + " " + ChatColor.BLUE);
			}
		}

		player.setScoreboard(scoreboard);
		
		for (Player otherPlayer : UtilServer.getPlayers())
		{
			AddPlayerToScoreboards(otherPlayer, GetGameForPlayer(otherPlayer).IsActive() ? GetGameForPlayer(otherPlayer).GetPlayer(otherPlayer).GetTeam().GetTeamType().name() : null);
		}
	}

	public Collection<Scoreboard> GetScoreboards()
	{
		return _scoreboardMap.values();
	}
	
	public void AddPlayerToScoreboards(Player player, String teamName) 
	{
		for (Scoreboard scoreboard : GetScoreboards())
		{
			for (Team team : scoreboard.getTeams())
				team.removePlayer(player);
		}

		if (teamName == null)
			teamName = "";

		for (Scoreboard scoreboard : GetScoreboards())
		{
			scoreboard.getTeam(ClientManager.Get(player).GetRank().Name + teamName).addPlayer(player);
		}
	}
    
	@EventHandler
	public void UpdateGameScoreboards(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
		{
			for (GameType activeGame : ActiveGames)
			{
				for (PlayerType player : activeGame.GetPlayers())
				{
					Scoreboard scoreboard = _scoreboardMap.get(player.GetPlayer());
					
					Objective objective = scoreboard.getObjective("§l" + "Score");
					objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Blue")).setScore(activeGame.GetBlueTeam().GetScore());
					objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "Red")).setScore(activeGame.GetRedTeam().GetScore());
					
				}
			}
		}
	}
	
    @Override
    public void run()
    {
        if (Scheduler.GetGames().size() > 0)
        {
            GameType game = Scheduler.GetGames().get(Scheduler.GetGames().size() - 1);
            int players = game.GetPlayers().size();
            
            if (players < TeamSize * 2 && players >= MinQueuePlayersToStart)
            {
    	        if (game.GetPlayers().size() % 2 != 0)
    	        {
    	        	if (BroadcastQueueJoinMessage )
    	        	{
	        	        for (Player gameplayer : game.GetBlueTeam().GetPlayers().get(0).GetPlayer().getWorld().getPlayers())
	        	        {
	        	        	gameplayer.sendMessage(F.main("Play Queue", "Waiting for teams to be even before start timer resumes."));
	        	        }
    	        	}
    	        }
    	        else
    	        {
	            	_gameQueueTickValue++;
	            	
	            	if (_gameQueueTickValue > TimeToStart)
	            	{
	            		TeamSize = players/2;
	            		TryToActivateGames();
	            	}
	            	else if (BroadcastQueueJoinMessage)
	            	{
	        	        for (Player gameplayer : game.GetBlueTeam().GetPlayers().get(0).GetPlayer().getWorld().getPlayers())
	        	        {
	        	        	String minuteMessage = (3 - _gameQueueTickValue) == 1 ? "minute" : "minutes";
	        	        	gameplayer.sendMessage(F.main("Play Queue", "Game will start in " + (3 - _gameQueueTickValue) + " " + minuteMessage + "!"));
	        	        }
	            	}
    	        }
            }
        }
    }

	@EventHandler
	public void onGamePlayerDeath(GamePlayerDeathEvent<GameType, PlayerType> event)
	{
		GameType game = event.GetGame();

		//Color Names
		try
		{
			if (event.GetPlayer().GetTeam().GetTeamType() == TeamType.RED)
				event.GetLog().SetKilledColor(C.cRed + "");
			else
				event.GetLog().SetKilledColor(C.cBlue + "");
		}
		catch (Exception e)
		{
			System.out.println("[SEVERE] TeamGameEngine.onGamePlayerDeath : Exception Setting Killed Color : " + e.getMessage());
			
			for (StackTraceElement trace : e.getStackTrace())
			{
				System.out.println(trace);
			}
		}
		
		try
		{
			if (event.GetLog().GetKiller() != null)
			{
				PlayerType killer = game.GetPlayer(event.GetLog().GetKiller().GetName());
				
				if (killer.GetTeam().GetTeamType() == TeamType.RED)
					event.GetLog().SetKillerColor(C.cRed + "");
				else
					event.GetLog().SetKillerColor(C.cBlue + "");
			}
		}
		catch (Exception e)
		{
			System.out.println("[SEVERE] TeamGameEngine.onGamePlayerDeath : Exception Setting Killed Color  : " + e.getMessage());
			
			for (StackTraceElement trace : e.getStackTrace())
			{
				System.out.println(trace);
			}
		}

		ScoreHandler.RewardForDeath(event.GetPlayer());

		boolean first = true;
		
		try
		{
			for (CombatComponent source : event.GetLog().GetAttackers())
			{		
				if (source == null || !source.IsPlayer())
					continue;
				
				PlayerType attacker = game.GetPlayer(source.GetName());
				
				if (attacker == null)
					continue;
				
				if (first)
				{
					if (attacker.GetTeam() == event.GetPlayer().GetTeam())
					{
						ScoreHandler.RewardForTeamKill(attacker, event.GetPlayer());
					}
					else
					{
						ScoreHandler.RewardForKill(attacker, event.GetPlayer(), event.GetLog().GetAssists());
					}
					
					first = false;
				}
				else
				{
					ScoreHandler.RewardForAssist(attacker, event.GetPlayer());
				}
			}
		}
		catch (Exception ex)
		{
			System.out.println("[SEVERE] TeamGameEngine.onGamePlayerDeath : Exception Handleing team kill/kill/assist  : " + ex.getMessage());
			
			for (StackTraceElement trace : ex.getStackTrace())
			{
				System.out.println(trace);
			}
		}

		game.StartRespawnFor(event.GetPlayer());
	}
	
    private void AddPlayerToTeam(GameType game, Player player, PlayerTeamType team, Boolean notify)
    {           
        PlayerType gamePlayer = game.AddPlayerToGame(player);
        PlayerGameMap.put(gamePlayer.getName(), game);

        team.AddPlayer(gamePlayer);
        
        if (ActiveGames.contains(game))
        {
            player.eject();
            
            if (player.isInsideVehicle())
            	player.leaveVehicle();
            
            for (Player otherPlayer : Plugin.getServer().getOnlinePlayers())
            {
                if (player != otherPlayer)
                {
                    player.showPlayer(otherPlayer);
                }
            }
            
            game.ActivatePlayer(gamePlayer);
        }
        
        TryToActivateGames();
    }
    
    @Override
    public boolean AddPlayerToGame(Player player, boolean notify)
    {       
        GameType game = GetNextOpenGame();
        
        if (game == null)
        {
            return false;
        }
        
        int redTeamSize = game.GetRedTeam().GetPlayers().size();
        int blueTeamSize = game.GetBlueTeam().GetPlayers().size();
        
        if (blueTeamSize < redTeamSize)
        {
            AddPlayerToBlueTeam(game, player);
            player.playSound(player.getLocation(), Sound.ZOMBIE_METAL, .5F, .5F);
        }
        else if (redTeamSize < blueTeamSize)
        {
            AddPlayerToRedTeam(game, player);
            player.playSound(player.getLocation(), Sound.ZOMBIE_METAL, .5F, .5F);
        }
        else if (blueTeamSize < TeamSize)
        {
            AddPlayerToBlueTeam(game, player);
            player.playSound(player.getLocation(), Sound.ZOMBIE_METAL, .5F, .5F);
        }
        else
        {
            System.out.println("Error neither blue nor red have < " + TeamSize + " players and " + player.getName() + " shouldn't be assigned to this game.");
            return false;
        }
        
        if (game.GetPlayers().size() < TeamSize*2 && BroadcastQueueJoinMessage)
        {
	        for (Player gameplayer : player.getWorld().getPlayers())
	        {
	        	gameplayer.sendMessage(ChatColor.BLUE + "Play Queue> " + ChatColor.GRAY + "Only " + (TeamSize*2 - game.GetPlayers().size()) + " more players needed to start game!");
	        }
        }
        
        return true;
    }

    public void AddPlayerToRedTeam(GameType game, Player player)
    {               
        AddPlayerToRedTeam(game, player, true);
    }
    
    public void AddPlayerToBlueTeam(GameType game, Player player)
    {       
        AddPlayerToBlueTeam(game, player, true);
    }
    
    public void AddPlayerToRedTeam(GameType game, Player player, Boolean notify)
    {               
        AddPlayerToTeam(game, player, game.GetRedTeam(), notify);
    }
    
    public void AddPlayerToBlueTeam(GameType game, Player player, Boolean notify)
    {       
        AddPlayerToTeam(game, player, game.GetBlueTeam(), notify);
    }
    
    public void SetupGame(final GameType game)
    {
    	GamesInSetup.add(game);
    	
    	for (PlayerType gamePlayer : game.GetPlayers())
    	{
    		if (IsSpectatorInActiveGame(gamePlayer.GetPlayer()))
    			RemoveSpectatorFromGame(gamePlayer.GetPlayer(), true);
    		
    		gamePlayer.sendMessage(ChatColor.BLUE + "Dominate>" + ChatColor.GRAY + " Preparing map...");
    	}
    	
    	ArenaManager.GetNextArena(new Callback<ArenaType>()
    			{
    				public void run(ArenaType arena)
    				{
    					ActivateGame(game, arena);
    				}
    			});
    }
    
    @Override
    protected void RemovePlayerFromGame(GameType game, PlayerType player, boolean quit)
    {
        super.RemovePlayerFromGame(game, player, quit);
        
        if (!ActiveGames.contains(game) && game != null && game.GetRedTeam() != null && game.GetBlueTeam() != null)
        {
            int redTeamSize = game.GetRedTeam().GetPlayers().size();
            int blueTeamSize = game.GetBlueTeam().GetPlayers().size();
            
            if (redTeamSize - blueTeamSize > 1)
            {
            	PlayerType movingPlayer = game.GetRedTeam().GetPlayers().get(0);
            	
            	game.GetRedTeam().RemovePlayer(movingPlayer);
            	game.GetBlueTeam().AddPlayer(movingPlayer);
            }
            else if (blueTeamSize - redTeamSize > 1)
            {
            	PlayerType movingPlayer = game.GetBlueTeam().GetPlayers().get(0);
            	
            	game.GetBlueTeam().RemovePlayer(movingPlayer);
            	game.GetRedTeam().AddPlayer(movingPlayer);
            }
        }
    }
    
    @Override
    public void ActivateGame(GameType game, ArenaType arena)
    {        
        GamesInSetup.remove(game);
        ActiveGames.add(game);
        
        List<Packet> removeListPacket = new ArrayList<Packet>(); 
        
        for (PlayerType player : game.GetPlayers())
        {
        	if (player.isOnline())
        	{
	            player.GetPlayer().eject();
	            
	            if (player.GetPlayer().isInsideVehicle())
	            	player.GetPlayer().leaveVehicle();
	            
	            removeListPacket.add(new Packet201PlayerInfo(player.getName(), false, -9999));
        	}
        }
        
        for (PlayerType player : game.GetPlayers())
        {
        	if (player.isOnline())
        	{
        		for (Packet packet : removeListPacket)
        		{
        			((CraftPlayer)player.GetPlayer()).getHandle().playerConnection.sendPacket(packet);
        		}
        		
        		AddPlayerToScoreboards(player.GetPlayer(), player.GetTeam().GetTeamType().name());
        	}
        }

        removeListPacket.clear();
        
        game.Activate(arena);
        
        for (Location location : arena.GetBlueShopPoints())
        {
        	_shopEntities.add(NpcManager.AddNpc(EntityType.ZOMBIE, 0, ChatColor.BLUE + "Select Class Here", location));
        }
        
        for (Location location : arena.GetRedShopPoints())
        {
        	_shopEntities.add(NpcManager.AddNpc(EntityType.ZOMBIE, 0, ChatColor.RED + "Select Class Here", location));
        }
        
        for (PlayerType player : game.GetPlayers())
        {
            for (Player otherPlayer : Plugin.getServer().getOnlinePlayers())
            {
                if (player.GetPlayer() != otherPlayer)
                {
                    player.GetPlayer().showPlayer(otherPlayer);
                }
            }
        }
    }
    
    @Override
    public void StopGame(GameType game)
    {
        for (Entity entity : _shopEntities)
        {
        	NpcManager.DeleteNpc(entity);
        }
        
        super.StopGame(game);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGameFinished(TeamGameFinishedEvent<GameType, PlayerTeamType, PlayerType> event)
    {
        for (PlayerType player : event.GetGame().GetPlayers())
        {
        	Portal.SendPlayerToServer(player.GetPlayer(), "Lobby");
        }
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin, new GenericRunnable<GameType>(event.GetGame())
        {
            public void run()
            {
                StopGame(t);
            }
        }, 300L);
    }
    
    @EventHandler
    public void onGamePlayerAttackedPlayerEvent(GamePlayerAttackedPlayerEvent<GameType, PlayerType> event)
    {
        PlayerType attacker = event.GetAttacker();
        PlayerType victim = event.GetVictim();
        
        if (attacker.GetTeam() == victim.GetTeam())
        {
            event.setCancelled(true);
        }
        else if (victim.IsDead() || attacker.IsDead())
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void HandleChat(final AsyncPlayerChatEvent event) 
    {
		if (event.isCancelled())
			return;
		
        final Player sender = event.getPlayer();
        String message = event.getMessage();

        if (message.length() < 1)
            return;

        if (!IsPlayerInActiveGame(sender))
        {
            Iterator<Player> recipientIterator = event.getRecipients().iterator();
            
            while(recipientIterator.hasNext())
            {
            	Player otherPlayer = recipientIterator.next();
            	
                if (IsPlayerInActiveGame(otherPlayer))
                	recipientIterator.remove();
            }
            
            StringBuilder playerNameBuilder = new StringBuilder();
            
            if (ClientManager.Get(sender.getName()) != null)
            {
            	CoreClient client = ClientManager.Get(sender.getName());
            	
            	if (client.GetRank().Has(Rank.OWNER))
            	{
            		playerNameBuilder.append(ChatColor.DARK_RED);
            	}
            	else if (client.GetRank().Has(Rank.MODERATOR))
            	{
            		playerNameBuilder.append(ChatColor.RED);
            	}
            	else if (client.GetRank().Has(Rank.ULTRA))
            	{
            		playerNameBuilder.append(ChatColor.GOLD);
            	}
            	else
            	{
            		playerNameBuilder.append(ChatColor.YELLOW);
            	}
            }

            event.setFormat(playerNameBuilder.toString() + "%1$s " + C.cWhite + "%2$s");
        }
        else
        {            
            GameType senderGame = GetGameForPlayer(sender);
            TeamType senderTeamType = senderGame.GetPlayer(sender).GetTeam().GetTeamType();
            
            String teamColor = senderTeamType == TeamType.RED ? C.cRed : C.cBlue;

            boolean globalMessage = false;
            if (message.charAt(0) == '!')
            {
                globalMessage = true;
                event.setMessage(message.substring(1, message.length()));
                event.setFormat(C.cDGray + "@" + C.cDAqua + "All" + " " + teamColor + "%1$s " + C.cWhite + "%2$s");
            }
            else
            {
            	event.setFormat(C.cDGray + "@" + C.cDAqua + "Team" + " " + teamColor + "%1$s " + C.cWhite + "%2$s");
            }
            
            Iterator<Player> recipientIterator = event.getRecipients().iterator();
            
            while (recipientIterator.hasNext())
            {
            	Player receiver = recipientIterator.next();
				
            	if (!IsPlayerInActiveGame(receiver) || GetGameForPlayer(receiver) != senderGame)
            	{
            		recipientIterator.remove();
            		continue;
            	}
 
                if (!globalMessage && senderTeamType != senderGame.GetPlayer(receiver).GetTeam().GetTeamType())
                	recipientIterator.remove();
            }
        }
    }
    
    @Override
    public boolean CanHurt(Player a, Player b)
    {    	
        if (IsPlayerInActiveGame(b) && IsPlayerInActiveGame(a))
        {
            GameType game = GetGameForPlayer(b);
            
            if (game.HasStarted())
            {
                PlayerType victim = game.GetPlayer(b);
                PlayerType attacker = game.GetPlayer(a);
                
                if (victim.IsDead() || victim.IsSpectating())
                    return false;
                
                if (attacker == null || attacker.GetTeam() == victim.GetTeam())
                    return false;
            }
        }
        
        return true;
    }
    
    @Override
    public ChatColor GetColorOfFor(String other, Player player)
    {
    	ChatColor prefixColor = super.GetColorOfFor(other, player);
    	
        if (IsPlayerInActiveGame(other))
        {
            PlayerTeamType playerTeam = GetGameForPlayer(other).GetPlayer(other).GetTeam();
            
            prefixColor = (playerTeam.GetTeamType() == TeamType.RED ? ChatColor.RED : ChatColor.BLUE);
        }
        
        return prefixColor;
    }
}
