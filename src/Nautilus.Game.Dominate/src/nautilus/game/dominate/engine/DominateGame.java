package nautilus.game.dominate.engine;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import mineplex.core.account.CoreClientManager;
import mineplex.core.energy.*;
import mineplex.core.packethandler.PacketHandler;
import mineplex.minecraft.game.core.condition.*;
import mineplex.minecraft.game.classcombat.Class.*;
import nautilus.game.core.arena.Region;
import nautilus.game.core.engine.TeamType;
import nautilus.game.core.events.team.TeamGameFinishedEvent;
import nautilus.game.core.game.TeamGame;
import nautilus.game.dominate.arena.IDominateArena;
import nautilus.game.dominate.player.DominatePlayer;
import nautilus.game.dominate.player.IDominatePlayer;
import nautilus.game.dominate.scoreboard.DominateTabScoreboard;

public class DominateGame extends TeamGame<IDominatePlayer, IDominateTeam, IDominateArena> implements IDominateGame
{
	private DominateNotifier _notifier;
	private DominateTabScoreboard _scoreboard;
	private List<IControlPoint> _controlPoints;
	private List<IPowerUp> _powerUps;

	private int _lastUpdate;

	public DominateGame(JavaPlugin plugin, CoreClientManager clientManager, ClassManager classManager, ConditionManager conditionmanager, Energy energy, DominateNotifier notifier, PacketHandler packetHandler) 
	{
		super(plugin, classManager, conditionmanager, energy);

		_notifier = notifier;
		ScoreLimit = 15000;
		_controlPoints = new ArrayList<IControlPoint>();
		_powerUps = new ArrayList<IPowerUp>();
		_scoreboard = new DominateTabScoreboard(plugin, clientManager, classManager, packetHandler, this);
	}

	@Override
	public IDominatePlayer AddSpectatorToGame(Player player, Location to)
	{
		IDominatePlayer spectator = super.AddSpectatorToGame(player, to);

		_scoreboard.AddSpectator(spectator);

		return spectator;
	}

	@Override 
	public void ClearPlayerSettings(IDominatePlayer player)
	{
		super.ClearPlayerSettings(player);

		if (_scoreboard != null)
			_scoreboard.ClearScoreboardForSpectator(player);
	}
	
	@Override
	public void RemoveSpectator(IDominatePlayer player)
	{
		super.RemoveSpectator(player);

		_scoreboard.ClearScoreboardForSpectator(player);
	}

	@Override
	public void Activate(IDominateArena arena)
	{
		super.Activate(arena);

		for (Region controlPointRegion : arena.GetControlPointAreas())
		{
			IControlPoint controlPoint = new ControlPoint(Plugin, this, _notifier, Arena.GetWorld(), controlPointRegion, controlPointRegion.GetName());
			_controlPoints.add(controlPoint);
		}

		for (Vector pointPowerUpPoint : arena.GetPointPowerUpPoints())
		{
			IPowerUp pointPowerUp = new PointPowerUp(Plugin, this, _notifier, pointPowerUpPoint.toLocation(Arena.GetWorld()), (long)180000, 100);
			_powerUps.add(pointPowerUp);
		}

		for (Vector resupplyPowerUpPoint : arena.GetResupplyPowerUpPoints())
		{
			IPowerUp resupplyPowerUp = new ResupplyPowerUp(Plugin, this, ClassManager, _notifier, resupplyPowerUpPoint.toLocation(Arena.GetWorld()), (long)60000);
			_powerUps.add(resupplyPowerUp);
		}

		_scoreboard.Update();
	}

	public void Update()
	{
		for (IControlPoint controlPoint : _controlPoints)
		{
			controlPoint.UpdateLogic();

			if (controlPoint.Captured())
			{
				controlPoint.GetOwnerTeam().AddPoints(controlPoint.GetPoints());

				for (IDominatePlayer defender : controlPoint.GetCapturers())
				{
					if (defender.GetTeam() == controlPoint.GetOwnerTeam())
					{
						defender.AddPoints(1);
					}
				}

				Arena.GetWorld().playSound(controlPoint.GetMiddlePoint(), Sound.CHICKEN_EGG_POP, 0.1F, 0.9F);
			}
		}

		for (IPowerUp powerUp : _powerUps)
		{
			powerUp.Update();
		}

		if (RedTeam.GetScore() >= ScoreLimit)
		{
			RedTeam.SetScore(ScoreLimit);
			StopGame();
			Plugin.getServer().getPluginManager().callEvent(new TeamGameFinishedEvent<IDominateGame, IDominateTeam, IDominatePlayer>(this, RedTeam));   
			_scoreboard.Update();
		}
		else if (BlueTeam.GetScore() >= ScoreLimit)
		{
			BlueTeam.SetScore(ScoreLimit);
			StopGame();
			Plugin.getServer().getPluginManager().callEvent(new TeamGameFinishedEvent<IDominateGame, IDominateTeam, IDominatePlayer>(this, BlueTeam));
			_scoreboard.Update();
		}

		if (_lastUpdate % 10 == 0)
		{            
			_scoreboard.Update();
		}
		
		_lastUpdate++;        
	}

	@Override
	public void Deactivate()
	{
		_scoreboard.Stop();

		for (IControlPoint controlPoint : _controlPoints)
		{
			controlPoint.Deactivate();
		}

		for (IPowerUp powerUp : _powerUps)
		{
			powerUp.Deactivate();
		}

		_controlPoints.clear();
		_powerUps.clear();
		
		_scoreboard = null;
		
		_controlPoints = null;
		_powerUps = null;
		
		super.Deactivate();
	}

	@Override
	protected IDominateTeam CreateTeam(TeamType teamType)
	{
		return new DominateTeam(teamType);
	}

	@Override
	protected IDominatePlayer CreateGamePlayer(Player player, int playerLives)
	{
		return new DominatePlayer(Plugin, player);        
	}

	@Override
	public List<IControlPoint> GetControlPoints()
	{
		return _controlPoints;
	}
}
