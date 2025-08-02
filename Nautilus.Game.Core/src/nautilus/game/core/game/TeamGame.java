package nautilus.game.core.game;

import java.util.List;
import java.util.Random;

import mineplex.core.energy.Energy;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import nautilus.game.core.arena.ITeamArena;
import nautilus.game.core.engine.ITeam;
import nautilus.game.core.engine.TeamType; 
import nautilus.game.core.player.ITeamGamePlayer;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class TeamGame<PlayerType extends ITeamGamePlayer<PlayerTeamType>, PlayerTeamType extends ITeam<PlayerType>, ArenaType extends ITeamArena> extends Game<PlayerType, ArenaType> implements ITeamGame<ArenaType, PlayerType, PlayerTeamType>
{
    private Random _random;        
    
    protected PlayerTeamType RedTeam;
    protected PlayerTeamType BlueTeam;
    
    public TeamGame(JavaPlugin plugin, ClassManager classManager, mineplex.minecraft.game.core.condition.ConditionManager conditionManager, Energy energy)
    {
        super(plugin, classManager, conditionManager, energy);
        
        _random = new Random();
        
        RedTeam = CreateTeam(TeamType.RED);
        BlueTeam = CreateTeam(TeamType.BLUE);
    }
    
    protected abstract PlayerTeamType CreateTeam(TeamType teamType);

    @Override
    public void Activate(ArenaType arena)
    {
        super.Activate(arena);
        
        RedTeam.SetSpawnRoom(arena.GetRedSpawnRoom());
        BlueTeam.SetSpawnRoom(arena.GetBlueSpawnRoom());
        
        for (PlayerType player : Players.values())
        {
        	ActivatePlayer(player);
        }
    }
    
    public void ActivatePlayer(PlayerType player)
    {
        if (player.GetTeam() == BlueTeam)
        {
        	Location spawnPoint = GetRandomSpawnPoint(Arena.GetBlueSpawnPoints());
        	player.SetLastInArenaPosition(spawnPoint.getWorld(), spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ());
            player.teleport(spawnPoint);
        }
        else if (player.GetTeam() == RedTeam)
        {
        	Location spawnPoint = GetRandomSpawnPoint(Arena.GetRedSpawnPoints());
        	player.SetLastInArenaPosition(spawnPoint.getWorld(), spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ());
            player.teleport(spawnPoint);
        }
    	
    	if (HasStarted)
    	{
            ResetPlayer(player);
            RespawnPlayer(player);
            
            player.SetSpectating(false);
            player.SetDead(false);
    	}
    	else
    	{
            player.SetSpectating(true);
            player.SetDead(true);
    	}
    }
    
    @Override 
    public void ClearPlayerSettings(PlayerType player)
    {
    	super.ClearPlayerSettings(player);
    	
    	player.GetTeam().RemovePlayer(player);
    }
    
    @Override
    public void ReallyStartGame()
    {
        super.ReallyStartGame();
        
        for (PlayerType player : Players.values())
        {
        	player.StartTimePlay();
        	
            ResetPlayer(player);
            RespawnPlayer(player);
            
            player.SetSpectating(false);
            player.SetDead(false);
        }
    }
    
	@Override
	public boolean CanInteract(PlayerType player, Block block)
	{
		if (super.CanInteract(player, block))
			return true;
		else if (player.GetTeam().IsInSpawnRoom(block.getLocation()) && player.GetTeam().IsInSpawnRoom(player.getLocation()))
			return true;

		return false;
	}
    
    @Override
    public void Deactivate()
    {       
        RedTeam.ClearPlayers();
        BlueTeam.ClearPlayers();
        
        RedTeam = null;
        BlueTeam = null;
        _random = null;
        
        super.Deactivate();
    }
    
    @Override
    public PlayerTeamType GetBlueTeam()
    {
        return BlueTeam;
    }

    @Override
    public PlayerTeamType GetRedTeam()
    {
        return RedTeam;
    }
    
    protected Location GetRandomSpawnPoint(List<Location> spawnPoints)
    {
        Location randomSpawnPoint = spawnPoints.get(_random.nextInt(spawnPoints.size()));
        return randomSpawnPoint;
    }
    
    protected void SpawnPlayer(PlayerType player)
    {
    	player.GetPlayer().eject();
    	player.GetPlayer().leaveVehicle();
    	
        if (player.GetTeam() == RedTeam)
        {
            player.teleport(GetRandomSpawnPoint(Arena.GetRedSpawnPoints()));
        }
        else if (player.GetTeam() == GetBlueTeam())
        {
            player.teleport(GetRandomSpawnPoint(Arena.GetBlueSpawnPoints()));
        }
        
        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, .5F, 0F);
    }
    
    public void RespawnPlayer(PlayerType player)
    {
        if (!player.isOnline())
            return;
        
        SpawnPlayer(player);
        
        ClassManager.Get(player.getName()).ResetToDefaults(true, true);
    }
    
	protected void StopGame()
	{
		for (PlayerType player : Players.values())
		{
			player.StopTimePlay();
			
			if (!player.isOnline())
				continue;

            player.GetPlayer().eject();

            if (player.GetPlayer().isInsideVehicle())
            	player.GetPlayer().leaveVehicle();
			
			if (player.GetTeam() == BlueTeam)
			{
				player.teleport(GetRandomSpawnPoint(Arena.GetBlueSpawnPoints()));
			}
			else if (player.GetTeam() == RedTeam)
			{
				player.teleport(GetRandomSpawnPoint(Arena.GetRedSpawnPoints()));
			}

			ResetPlayer(player);

			if (player.isOnline())
			{
				ClientClass playerClass = ClassManager.Get(player.getName());
				playerClass.SetGameClass(null);
				playerClass.ClearDefaults();
			}

			player.SetDead(true);
			player.SetSpectating(true);

			if (PlayerTaskIdMap.containsKey(player.getName()))
			{
				Plugin.getServer().getScheduler().cancelTask(PlayerTaskIdMap.get(player.getName()));
				ConditionManager.EndCondition(player.GetPlayer(), ConditionType.CLOAK, null);
				
				PlayerTaskIdMap.remove(player.getName());
			}

			player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1, .9f);
		}

		Plugin.getServer().getScheduler().cancelTask(UpdaterTaskId);
	}
	
    protected void UpdateNewPlayerWithOldPlayer(PlayerType newPlayer, PlayerType oldPlayer)
    {
        super.UpdateNewPlayerWithOldPlayer(newPlayer, oldPlayer);
        
        PlayerTeamType playerTeam = oldPlayer.GetTeam();
        
        playerTeam.RemovePlayer(oldPlayer);
        playerTeam.AddPlayer(newPlayer);
    }
}
