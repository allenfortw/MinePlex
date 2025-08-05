package nautilus.game.core.player;

import mineplex.minecraft.game.classcombat.Class.ClientClass;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class GamePlayer implements IGamePlayer
{
	private Player _player;
	
    private int _lives;
    private int _kills;
    private int _assists;
    private int _deaths;
    private int _points;
    private int _skillTokens;
    private int _itemTokens;
    private long _logoutTime;
    private Location _lastInArenaPosition;
    private boolean _isDead;
    private boolean _isSpectating;
    private long _timePlayed;
    private long _lastTimePlayStart;
    
    private ClientClass _clientClass;

    public GamePlayer(JavaPlugin plugin, Player player)
    {
        _player = player;
    }

    @Override
    public int GetRemainingLives()
    {
        return _lives;
    }

    @Override
    public void SetLives(int getRemainingLives)
    {
        _lives = getRemainingLives;
    }

    @Override
    public int GetKills() 
    {
        return _kills;
    }
    
    @Override
    public void AddKills(int kills)
    {
        _kills += kills;
    }
    
    @Override
    public int GetAssists() 
    {
        return _assists;
    }
    
    @Override
    public void AddAssists(int assists)
    {
        _assists += assists;
    }
    
    @Override
    public int GetDeaths() 
    {
        return _deaths;
    }
    
    @Override
    public void AddDeaths(int deaths)
    {
        _deaths += deaths;
    }
    
    @Override
    public int GetSkillTokens() 
    {
        return _skillTokens;
    }
    
    @Override
    public void SetSkillTokens(int tokens)
    {
        _skillTokens = tokens;
    }
    
    @Override
    public int GetItemTokens() 
    {
        return _itemTokens;
    }
    
    @Override
    public void SetItemTokens(int tokens)
    {
        _itemTokens = tokens;
    }
    
    @Override 
    public int GetPoints()
    {
        return _points;
    }
    
    @Override
    public void AddPoints(int points)
    {
        _points += points;
    }

    @Override
    public void SetLogoutTime(long logoutTime)
    {
        _logoutTime = logoutTime;
    }
    
    @Override
    public long GetLogoutTime()
    {
        return _logoutTime;
    }

    @Override
    public boolean IsOut()
    {
        return _lives <= 0;
    }

    @Override
    public void SetLastInArenaPosition(World world, double x, double y, double z)
    {
        _lastInArenaPosition = new Location(world, x, y, z);
    }

    @Override
    public Location GetLastInArenaPosition()
    {
        return _lastInArenaPosition;
    }

    @Override
    public boolean IsDead()
    {
        return _isDead;
    }

    @Override
    public void SetDead(boolean dead)
    {
        _isDead = dead;
    }

    @Override
    public org.bukkit.entity.Player GetPlayer()
    {
        return _player;
    }
    
    public void RemoveArrows()
    {
    	// WHERE DID IT GO?????? ((CraftPlayer)_player).getHandle().r(0);
    }

    @Override
    public boolean IsSpectating()
    {
        return _isSpectating;
    }

    @Override
    public void SetSpectating(boolean spectating)
    {
        _isSpectating = spectating;
        
        // Fix for arrows hitting dead players.
        ((CraftPlayer)_player).getHandle().spectating = spectating;
        
        if (spectating)
        {
        	_player.setAllowFlight(true);
        	_player.setFlying(true);
        }
        else
        {
        	_player.setAllowFlight(false);
        	_player.setFlying(false);
        }
    }

    @Override
    public ClientClass GetClass()
    {
        return _clientClass;
    }

    @Override
    public void SetClass(ClientClass clientClass)
    {
        _clientClass = clientClass;
    }
    
	@Override
	public long GetTimePlayed()
	{
		return _timePlayed;
	}

	@Override
	public void StartTimePlay()
	{
		_lastTimePlayStart = System.currentTimeMillis();
	}

	@Override
	public void StopTimePlay()
	{
		_timePlayed += System.currentTimeMillis() - _lastTimePlayStart;
	}

	@Override
	public String getName()
	{
		return _player.getName();
	}

	@Override
	public void sendMessage(String string)
	{
		_player.sendMessage(string);
	}

	@Override
	public Location getLocation()
	{
		return _player.getLocation();
	}

	@Override
	public boolean teleport(Location location)
	{
		return _player.teleport(location);
	}

	@Override
	public boolean isOnline()
	{
		return _player.isOnline();
	}

	@Override
	public void setFireTicks(int i)
	{
		_player.setFireTicks(i);
	}

	@Override
	public void setHealth(double i)
	{
		_player.setHealth(i);
	}

	@Override
	public void setFoodLevel(int i)
	{
		_player.setFoodLevel(i);
	}

	@Override
	public void setVelocity(Vector vector)
	{
		_player.setVelocity(vector);
	}

	@Override
	public PlayerInventory getInventory()
	{
		return _player.getInventory();
	}

	@Override
	public void playSound(Location location, Sound zombieMetal, float f, float g)
	{
		_player.playSound(location, zombieMetal, f, g);
	}
}
