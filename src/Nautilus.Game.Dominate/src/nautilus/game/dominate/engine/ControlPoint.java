package nautilus.game.dominate.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mineplex.core.common.util.MapUtil;
import nautilus.game.core.arena.Region;
import nautilus.game.core.engine.TeamType;
import nautilus.game.core.events.GamePlayerDeathEvent;
import nautilus.game.core.events.GamePlayerJoinedEvent;
import nautilus.game.core.events.GamePlayerQuitEvent;
import nautilus.game.dominate.events.ControlPointCapturedEvent;
import nautilus.game.dominate.events.ControlPointEnemyCapturingEvent;
import nautilus.game.dominate.events.ControlPointLostEvent;
import nautilus.game.dominate.player.IDominatePlayer;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class ControlPoint implements IControlPoint, Listener
{
    private JavaPlugin _plugin;
    private IDominateGame _game;
    private DominateNotifier _notifier;
    private World _world;
	private Region _captureRegion;
	private IDominateTeam _capturingTeam = null;
	private int _captureValue;
	private int _captureThreshold;
	private int _incrementValue;
	private List<IDominatePlayer> _capturers;
	private Boolean _captured;
	private IDominateTeam _ownerTeam = null;
	private int _pointValue;
	private boolean _captureVisualChange;
	private List<Vector> _ownedLandVisualRegionBlocks;
	private List<Vector> _neutralLandVisualRegionBlocks;
	private Random _random;
	private Location _middlePoint;
	private List<Location> _topCornerPoints;
	
	private boolean _captureTeamChanged;
	private boolean _alternateColor;
	boolean Locked;	
	String Message;
	
	private int _defaultPointValue = 3;
	
	public ControlPoint(JavaPlugin plugin, IDominateGame game, DominateNotifier notifier, World world, Region captureSquare, String message)
	{
	    _plugin = plugin;
	    _game = game;
	    _notifier = notifier;
	    _world = world;
		_captureRegion = captureSquare;
		_capturers = new ArrayList<IDominatePlayer>();
		Message = ChatColor.GREEN + message + ChatColor.GRAY;
		_captured = false;
		_incrementValue = 0;
		_captureValue = 0;
		_captureThreshold = 20;
		_pointValue = _defaultPointValue;
		_neutralLandVisualRegionBlocks = new ArrayList<Vector>();
		_ownedLandVisualRegionBlocks = new ArrayList<Vector>();
		_random = new Random();
		_topCornerPoints = new ArrayList<Location>();
		
		SetupVisuals();
		
		_plugin.getServer().getPluginManager().registerEvents(this, _plugin);
	}
	
	protected void SetupVisuals()
	{
        Vector minPoint = _captureRegion.GetMinimumPoint();
        Vector maxPoint = _captureRegion.GetMaximumPoint();
        
        Vector middleVector = minPoint.getMidpoint(maxPoint.clone().setY(minPoint.getBlockY()));
        _middlePoint = new Location(_world, middleVector.getBlockX(), middleVector.getBlockY(), middleVector.getBlockZ());
                
        _topCornerPoints.add(new Location(_world, minPoint.getBlockX(), maxPoint.getBlockY(), minPoint.getBlockZ()));
        _topCornerPoints.add(new Location(_world, maxPoint.getBlockX(), maxPoint.getBlockY(), minPoint.getBlockZ()));
        _topCornerPoints.add(new Location(_world, minPoint.getBlockX(), maxPoint.getBlockY(), maxPoint.getBlockZ()));
        _topCornerPoints.add(new Location(_world, maxPoint.getBlockX(), maxPoint.getBlockY(), maxPoint.getBlockZ()));
        
        for (Location cornerPoint : _topCornerPoints)
        {
            MapUtil.QuickChangeBlockAt(_world, cornerPoint.getBlockX(), cornerPoint.getBlockY(), cornerPoint.getBlockZ(), Material.WOOL, 0);
        }
        
        int minX = minPoint.getBlockX() + 1;
        int minY = minPoint.getBlockY();
        int minZ = minPoint.getBlockZ() + 1;
        
        int maxX = maxPoint.getBlockX() - 1;
        int maxZ = maxPoint.getBlockZ() - 1;
        
        for (int x=minX; x <= maxX; x++)
        {
        	if (Math.abs(x - middleVector.getBlockX()) >= 3)
        		continue;
        	
            for (int z=minZ; z <= maxZ; z++)
            {
            	if (Math.abs(z - middleVector.getBlockZ()) >= 3)
            		continue;
            	
            	if (_middlePoint.getBlockX() == x && _middlePoint.getBlockZ() == z)
            	{
            		MapUtil.QuickChangeBlockAt(_world, x, minY, z, Material.BEACON, 0);
            		continue;
            	}
            	
                _neutralLandVisualRegionBlocks.add(new Vector(x, minY, z));
                MapUtil.QuickChangeBlockAt(_world, x, minY, z, Material.WOOL, 0);
            }
        }
        
        for (int x=_middlePoint.getBlockX()-1; x <= _middlePoint.getBlockX()+1; x++)
        {
            for (int z=_middlePoint.getBlockZ()-1; z <= _middlePoint.getBlockZ()+1; z++)
            {
            	MapUtil.QuickChangeBlockAt(_world, x, minY - 1, z, Material.IRON_BLOCK, 0);
            }
        }
	}
	
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (event.isCancelled())
            return;
        
        Player player = event.getPlayer();
        
        if (_game.IsPlayerInGame(player))
        {
            IDominatePlayer gamePlayer = _game.GetPlayer(player);
            
            if (_game.HasStarted() && !gamePlayer.IsSpectating()) 
            {
                if (!IsLocationInControlPoint(event.getFrom()) && IsLocationInControlPoint(event.getTo()))
                {
                	if (_capturers.contains(gamePlayer))
                	{
                		System.out.println("Move tried to add existing player " + player.getName());
                	}
                	else
                	{
                		AddCapturer(gamePlayer);
                	}
                }
                else if (IsLocationInControlPoint(event.getFrom()) && !IsLocationInControlPoint(event.getTo()))
                {
                    RemoveCapturer(gamePlayer);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        if (event.isCancelled())
            return;
        
        Player player = event.getPlayer();
        
        if (_game.IsPlayerInGame(player))
        {
            IDominatePlayer gamePlayer = _game.GetPlayer(player);
            
            if (_game.HasStarted() && !gamePlayer.IsSpectating()) 
            {
                if (!IsLocationInControlPoint(event.getFrom()) && IsLocationInControlPoint(event.getTo()))
                {
                	if (_capturers.contains(gamePlayer))
                	{
                		System.out.println("Teleport tried to add existing player " + player.getName());
                	}
                	else
                	{
                		AddCapturer(gamePlayer);
                	}
                }
                else if (IsLocationInControlPoint(event.getFrom()) && !IsLocationInControlPoint(event.getTo()))
                {
                    RemoveCapturer(gamePlayer);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onGamePlayerDeath(GamePlayerDeathEvent<IDominateGame, IDominatePlayer> event)
    {
        if (event.GetGame() == _game)
        {
            if (_game.HasStarted()) 
            {
                if (IsLocationInControlPoint(event.GetPlayer().getLocation()))
                {
                    RemoveCapturer(event.GetPlayer());
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(GamePlayerJoinedEvent<IDominateGame, IDominatePlayer> event)
    {
        if (event.GetGame() == _game)
        {            
            if (_game.HasStarted() && !event.GetPlayer().IsDead() && !event.GetPlayer().IsSpectating()) 
            {
                if (IsLocationInControlPoint(event.GetPlayer().getLocation()))
                {
                	if (_capturers.contains(event.GetPlayer()))
                	{
                		System.out.println("Join tried to add existing player " + event.GetPlayer().getName());
                	}
                	else
                	{
                		AddCapturer(event.GetPlayer());
                	}
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerLeave(GamePlayerQuitEvent<IDominateGame, IDominatePlayer> event)
    {
        if (event.GetGame() == _game)
        {            
            if (_game.HasStarted()) 
            {
                if (IsLocationInControlPoint(event.GetPlayer().getLocation()))
                {
                    RemoveCapturer(event.GetPlayer());
                }
            }
        }
    }
        
    protected boolean IsLocationInControlPoint(Location location)
    {
        return _captureRegion.Contains(location.toVector());
    }

	public boolean IsPlayerInControlPoint(IDominatePlayer player)
	{
		return IsLocationInControlPoint(player.getLocation());
	}
	
	public void AddCapturer(IDominatePlayer player)
	{
		_capturers.add(player);
		
		if (_ownerTeam == null)
		{
    		if (_capturingTeam == null)
    		{
    			_capturingTeam = player.GetTeam();
    			_captureValue = 0;
    			_captureTeamChanged = true;
    		}
    		
    		if (_capturingTeam == player.GetTeam())
    		{
    			_incrementValue++;
    			_notifier.BroadcastMessageToPlayer("You are taking control of " + Message + "!", player.GetPlayer());
    		}
    		else
    		{
    		    Locked = true;
    		}
		}
		else if (_ownerTeam != player.GetTeam())
		{
			_capturingTeam = player.GetTeam();
			_incrementValue++;
			
		    if (_pointValue == _defaultPointValue)
		    {
                _notifier.BroadcastMessageToPlayer("You are taking control of " + Message + "!", player.GetPlayer());
                _plugin.getServer().getPluginManager().callEvent(new ControlPointEnemyCapturingEvent(_game, this, _ownerTeam, player.GetTeam()));
		    }
		    else
		    {
		        Locked = true;
		    }
		}
		else
		{
		    if (_pointValue <= 5)
		    {
		        _pointValue++;
		        _notifier.BroadcastMessageToPlayer("You are defending " + Message + ", " + ChatColor.YELLOW + "+1" + ChatColor.GRAY +" to capture score!", player.GetPlayer());
		    }
		    
		    if (_capturingTeam != null)
		    {
		    	Locked = true;
		    }
		}
	}
	
	public void UpdateLogic()
	{
		if (!Locked)
		{
			if (_ownerTeam == null)
			{
				if (_capturingTeam != null)
				{
					if (_captureValue <= _captureThreshold && _incrementValue > 0)
					{
						_captureValue += _incrementValue;
					}
					
					if (_captureValue >= _captureThreshold)
					{
						_captured = true;
						_captureValue = _captureThreshold;
						_ownerTeam = _capturingTeam;
						_ownerTeam.AddControlPoint(this);
						_captureVisualChange = true;
						
						_capturingTeam = null;
						_incrementValue = 0;
						
						_plugin.getServer().getPluginManager().callEvent(new ControlPointCapturedEvent(_game, this, _ownerTeam, _capturers));						
						
						for (IDominatePlayer player : _capturers)
						{
				            if (_pointValue <= 5)
				            {
				                _pointValue++;
				                _notifier.BroadcastMessageToPlayer("You are defending " + Message + ", " + ChatColor.YELLOW + "+1" + ChatColor.GRAY + " to capture score!", player.GetPlayer());
				            }						    
						}
					}
					
					UpdateVisual();
				}
			}
			else if (_capturingTeam != null)
			{			    
				if (_captureValue > 0)
				{
					_captureValue -= _incrementValue;
				}
				
				if (_captureValue <= 0)
				{
				    ControlPointLost();
				}
				
				UpdateVisual();
			}
		}
	}
	
	public void RemoveCapturer(IDominatePlayer player)
	{
		_capturers.remove(player);		

		if (_capturers.size() == 0)
		{
			_capturingTeam = null;
			_captureTeamChanged = true;
			_incrementValue = 0;
			_pointValue = _defaultPointValue;
			
			if (_ownerTeam == null)
			{
				_captureValue = 0;
			}
			
			if (player.GetTeam() == _ownerTeam)
			{
				_notifier.BroadcastMessageToPlayer("You are no longer defending " + Message + "!", player.GetPlayer());
			}
			
			UpdateVisual();
		}
		else
		{
    		if (!Locked)
    		{
    			if (player.GetTeam() == _ownerTeam)
                {
    				_notifier.BroadcastMessageToPlayer("You are no longer defending " + Message + "!", player.GetPlayer());
    				
    				_pointValue = _defaultPointValue + _capturers.size();
    				
                    if (_pointValue > 5)
                    {
                    	_pointValue = 5;
                    }
                }
    			else if (player.GetTeam() == _capturingTeam)
    		    {
    		        _incrementValue = _capturers.size();
    		    }
    		}
    		else
    		{
    			int capturingTeam = 0;
    			int teamB = 0;
    			
    			for (IDominatePlayer gamePlayer : _capturers)
    			{
					if (gamePlayer.GetTeam() == _capturingTeam)
					{
						capturingTeam++;
					}
					else
					{
						teamB++;
					}
    			}
    			
				if (capturingTeam == 0 || teamB == 0)
				{
					Locked = false;
				}
    			
				_incrementValue = capturingTeam;
				
				if (capturingTeam == 0)
				{
					_capturingTeam = null;
				}
				
				if (_ownerTeam != null)
				{
					_pointValue = _defaultPointValue + teamB;
				
	                if (_pointValue > 5)
	                {
	                	_pointValue = 5;
	                }
				}
				else
				{
    				if (capturingTeam == 0)
    				{
    					_capturingTeam = _capturers.get(0).GetTeam();
    					_captureTeamChanged = true;
    					_incrementValue = _capturers.size();
    					_captureValue = 0;
    					
    					_notifier.BroadcastMessageToPlayers("You are taking control of " + Message + "!", _capturers);
    				}
				}
    		}
		}
	}
	
	private void ControlPointLost()
	{
	    if (_ownerTeam != null)
	    {
	        _plugin.getServer().getPluginManager().callEvent(new ControlPointLostEvent(_game, this, _ownerTeam, _capturers));
	        _ownerTeam.RemoveControlPoint(this);
	    }
	    
	    _captureValue = 0;
        _captured = false;
        _ownerTeam = null;
        _captureVisualChange = true;
	}
	
	public void UpdateVisual()
	{
	    try
	    {
	    	_alternateColor = !_alternateColor;
	    	
            byte data = 0;
    
            if (_ownerTeam != null)
            {
                if (_ownerTeam.GetTeamType() == TeamType.RED)
                {
                    data = 14;
                }
                else if (_ownerTeam.GetTeamType() == TeamType.BLUE)
                {
                    data = 11;
                }
            }
            else if (_capturingTeam != null)
            {
                if (_capturingTeam.GetTeamType() == TeamType.RED)
                {
                    data = 14;
                }
                else if (_capturingTeam.GetTeamType() == TeamType.BLUE)
                {
                    data = 11;
                }
            }
    
            if (_captureVisualChange)
    	    {
                byte topPointColor = data;
                
                if (_ownerTeam == null)
                {
                    topPointColor = 0;
                }
                
                for (Location cornerPoint : _topCornerPoints)
                {
                    MapUtil.QuickChangeBlockAt(_world, cornerPoint.getBlockX(), cornerPoint.getBlockY(), cornerPoint.getBlockZ(), Material.WOOL, topPointColor);
                }
                
                _captureVisualChange = false;
    	    }
            
            if (_captureTeamChanged)
            {
                for (Vector blockVector : _neutralLandVisualRegionBlocks)
                {
                	if (_middlePoint.getBlockX() == blockVector.getBlockX() && _middlePoint.getBlockZ() == blockVector.getBlockZ())
                		continue;

                    MapUtil.QuickChangeBlockAt(_world, blockVector.getBlockX(), blockVector.getBlockY(), blockVector.getBlockZ(), Material.WOOL, 0);
                }
                
                for (Vector blockVector : _ownedLandVisualRegionBlocks)
                {
                	if (_middlePoint.getBlockX() == blockVector.getBlockX() && _middlePoint.getBlockZ() == blockVector.getBlockZ())
                		continue;
                	
                    MapUtil.QuickChangeBlockAt(_world, blockVector.getBlockX(), blockVector.getBlockY(), blockVector.getBlockZ(), Material.WOOL, data);
                }
                
                _captureTeamChanged = false;
            }
            
            float capturePercentage = (float)_captureValue / (float)_captureThreshold;
     
            byte captureProgressColorData = data;
            int neutralLandBlockCount = _neutralLandVisualRegionBlocks.size();
            int ownedLandBlockCount = _ownedLandVisualRegionBlocks.size();
            
            int captureBlocksToColorCount = (int)(capturePercentage * (neutralLandBlockCount + ownedLandBlockCount));
            
            if (captureBlocksToColorCount > ownedLandBlockCount)
            {
                captureBlocksToColorCount -= ownedLandBlockCount;
                
                for (int i=0; i < captureBlocksToColorCount; i++)
                {
                    Vector blockVector = _neutralLandVisualRegionBlocks.get(_random.nextInt(_neutralLandVisualRegionBlocks.size()));
                    _ownedLandVisualRegionBlocks.add(blockVector);
                    _neutralLandVisualRegionBlocks.remove(blockVector);
                    
                	if (_middlePoint.getBlockX() == blockVector.getBlockX() && _middlePoint.getBlockZ() == blockVector.getBlockZ())
                		continue;
                	
                    MapUtil.QuickChangeBlockAt(_world, blockVector.getBlockX(), blockVector.getBlockY(), blockVector.getBlockZ(), Material.WOOL, captureProgressColorData);
                }
                
                for (Location cornerPoint : _topCornerPoints)
                {
                    _world.playEffect(cornerPoint, Effect.STEP_SOUND, captureProgressColorData == 14 ? 55 : 8);
                }
            }
            else if (captureBlocksToColorCount < _ownedLandVisualRegionBlocks.size())
            {
                captureBlocksToColorCount = _ownedLandVisualRegionBlocks.size() - captureBlocksToColorCount;
                captureProgressColorData = 0;
                
                for (int i=0; i < captureBlocksToColorCount; i++)
                {
                    Vector blockVector = _ownedLandVisualRegionBlocks.get(_random.nextInt(_ownedLandVisualRegionBlocks.size()));
                    _neutralLandVisualRegionBlocks.add(blockVector);
                    _ownedLandVisualRegionBlocks.remove(blockVector);
                    
                	if (_middlePoint.getBlockX() == blockVector.getBlockX() && _middlePoint.getBlockZ() == blockVector.getBlockZ())
                		continue;
                	
                    MapUtil.QuickChangeBlockAt(_world, blockVector.getBlockX(), blockVector.getBlockY(), blockVector.getBlockZ(), Material.WOOL, captureProgressColorData);
                }
                
                for (Location cornerPoint : _topCornerPoints)
                {
                    _world.playEffect(cornerPoint, Effect.STEP_SOUND, 35);
                }
            }
	    }
	    catch(Exception ex)
	    {
	        System.out.println("Exception in ControlPoint.UpdateVisual(): " + ex.getMessage());
	        System.out.println("Increment value = " + _incrementValue);
	        System.out.println("Capturing team = " + (_capturingTeam != null ? _capturingTeam.GetTeamType() : "None"));
	        System.out.println("Owner team = " + (_ownerTeam != null ? _ownerTeam.GetTeamType() : "None"));
	        System.out.println("Capturers size = " + _capturers.size());
	        System.out.println("capturePercentage = " + ((float)_captureValue / (float)_captureThreshold));            
	        System.out.println("neutralLandBlockCount = " + _neutralLandVisualRegionBlocks.size());
	        System.out.println("ownedLandBlockCount = " + _ownedLandVisualRegionBlocks.size());            
	        System.out.println("captureBlocksToColorCount = " + ((int)(((float)_captureValue / (float)_captureThreshold) * (_neutralLandVisualRegionBlocks.size() + _ownedLandVisualRegionBlocks.size()))));
	    }
	    
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void OnPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		
		if (player.isOp() && event.getMessage().startsWith("!test cpinfo"))
		{
			player.sendMessage("Control Point Name: " + Message);
			player.sendMessage("Increment value = " + _incrementValue);
			player.sendMessage("Capturing team = " + (_capturingTeam != null ? _capturingTeam.GetTeamType() : "None"));
			player.sendMessage("Owner team = " + (_ownerTeam != null ? _ownerTeam.GetTeamType() : "None"));
			player.sendMessage("Capturers size = " + _capturers.size());
			player.sendMessage("capturePercentage = " + ((float)_captureValue / (float)_captureThreshold));            
			player.sendMessage("neutralLandBlockCount = " + _neutralLandVisualRegionBlocks.size());
			player.sendMessage("ownedLandBlockCount = " + _ownedLandVisualRegionBlocks.size());            
			player.sendMessage("captureBlocksToColorCount = " + ((int)(((float)_captureValue / (float)_captureThreshold) * (_neutralLandVisualRegionBlocks.size() + _ownedLandVisualRegionBlocks.size()))));
	        
			player.sendMessage("Capturer list:");
	        for (IDominatePlayer capturer : _capturers)
	        {
	        	player.sendMessage(capturer.getName());
	        }
		}
	}
	
	/*
	private void UpdateVisualBlocks(Vector middle, IDominateTeam team, int blocks, int max)
	{
		Block block = _world.getBlockAt(middle.getBlockX(), middle.getBlockY(), middle.getBlockZ());
		byte data = 0;
		
		if (team == null)
		{
			data = 0;
		}
		else if (team.GetTeamType() == TeamType.RED)
		{
			data = 14;
		}
		else if (team.GetTeamType() == TeamType.BLUE)
		{
			data = 11;
		}
		
		UpdateWoolBlock(block, data);
		
		for (int i = 1; i < max; i++)
		{					
			if (i < max)
			{
				UpdateWoolBlock(_world.getBlockAt(middle.getBlockX() + i, middle.getBlockY(), middle.getBlockZ()), data);
				UpdateWoolBlock(_world.getBlockAt(middle.getBlockX(), middle.getBlockY(), middle.getBlockZ() + i), data);
				UpdateWoolBlock(_world.getBlockAt(middle.getBlockX() - i, middle.getBlockY(), middle.getBlockZ()), data);
				UpdateWoolBlock(_world.getBlockAt(middle.getBlockX(), middle.getBlockY(), middle.getBlockZ() - i), data);
			}
			else
			{
				_world.getBlockAt(middle.getBlockX() + i, middle.getBlockY(), middle.getBlockZ()).setData((byte)0);
				_world.getBlockAt(middle.getBlockX(), middle.getBlockY(), middle.getBlockZ() + i).setData((byte)0);
				_world.getBlockAt(middle.getBlockX() - i, middle.getBlockY(), middle.getBlockZ()).setData((byte)0);
				_world.getBlockAt(middle.getBlockX(), middle.getBlockY(), middle.getBlockZ() - i).setData((byte)0);
			}
		}
	}
	*/
	
    @Override
    public String GetName()
    {
        return Message;
    }

    @Override
    public boolean Captured()
    {
        return _captured;
    }

    @Override
    public IDominateTeam GetOwnerTeam()
    {
        return _ownerTeam;
    }

    @Override
    public void Deactivate()
    {
        _plugin = null;
        _game = null;
        _notifier = null;
        _world = null;
    	_captureRegion = null;
    	_capturingTeam = null;
    	_capturers = null;
    	_captured = null;
    	_ownerTeam = null;
    	_ownedLandVisualRegionBlocks = null;
    	_neutralLandVisualRegionBlocks = null;
    	_random = null;
    	_middlePoint = null;
    	_topCornerPoints = null;
    	
        HandlerList.unregisterAll(this);
    }

    @Override
    public int GetPoints()
    {
        return _pointValue;
    }

    @Override
    public Location GetMiddlePoint()
    {
        return _middlePoint;
    }

    @Override
    public List<IDominatePlayer> GetCapturers()
    {
        return _capturers;
    }
}
