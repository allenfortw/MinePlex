package nautilus.game.arcade.game.minigames.escape;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.minigames.escape.DragonEscapeData;
import nautilus.game.arcade.game.minigames.quiver.kits.KitBrawler;
import nautilus.game.arcade.kit.Kit;

public class DragonEscape extends SoloGame
{
	private ArrayList<DragonScore> _ranks = new ArrayList<DragonScore>();
	private ArrayList<String> _lastScoreboard = new ArrayList<String>();

	private Location _dragon;
	private ArrayList<Location> _waypoints;

	private DragonEscapeData _dragonData;

	public DragonEscape(ArcadeManager manager) 
	{
		super(manager, GameType.DragonEscape,

				new Kit[]
						{
				new KitBrawler(manager)
						},

						new String[]
								{
				"Douglas the Dragon is after you!",
				"RUN!!!!!!!!!!",
				"Last player alive wins!"
								});

		this.DamagePvP = false;
		this.HungerSet = 20;
	}

	@Override
	public void ParseData() 
	{
		_dragon = WorldData.GetDataLocs("RED").get(0);
		_waypoints = new ArrayList<Location>();

		//Order Waypoints
		Location last = _dragon;
		
		while (!WorldData.GetDataLocs("BLACK").isEmpty())
		{	
			Location best = null;
			double bestDist = 0;

			//Get Best
			for (Location loc : WorldData.GetDataLocs("BLACK"))
			{
				double dist = UtilMath.offset(loc, last);

				if (best == null || dist < bestDist)
				{
					best = loc;
					bestDist = dist;
				}
			}

			_waypoints.add(best);
			WorldData.GetDataLocs("BLACK").remove(best);
			best.subtract(new Vector(0,1,0));
			
			last = best;
		}
	}

	@EventHandler
	public void SpawnDragon(GameStateChangeEvent event)
	{
		if (event.GetState() != GameState.Prepare)
			return;

		this.CreatureAllowOverride = true;
		EnderDragon dragon = _dragon.getWorld().spawn(_dragon, EnderDragon.class);
		this.CreatureAllowOverride = false;

		dragon.setCustomName(ChatColor.YELLOW + C.Bold + "Douglas the Dragon");

		_dragonData = new DragonEscapeData(this, dragon, _waypoints.get(0));
	}

	@EventHandler 
	public void MoveDragon(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		if (_dragonData == null)
			return;

		_dragonData.Target = _waypoints.get(Math.min(_waypoints.size()-1, (GetWaypointIndex(_dragonData.Location) + 1)));

		_dragonData.Move();
			
		Manager.GetExplosion().BlockExplosion(UtilBlock.getInRadius(_dragonData.Location, 10d).keySet(), _dragonData.Location);
	}

	@EventHandler
	public void UpdateScores(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		if (_dragonData == null)
			return;

		double dragonScore = GetScore(_dragonData.Dragon);

		for (Player player : GetPlayers(true))
		{
			double playerScore = GetScore(player);
			
			if (player.getLocation().getY() < 50)
				player.damage(50);

			if (dragonScore > playerScore)
			{
				player.damage(50);
			}
			else
			{ 
			
				SetScore(player, playerScore);
			}
		}
	}
	
	public void SetScore(Player player, double playerScore)
	{
		//Rank
		for (DragonScore score : _ranks)
		{
			if (score.Player.equals(player))
			{
				score.Score = playerScore;
				return;
			}
		}

		_ranks.add(new DragonScore(player, playerScore));
	}

	public double GetScore(Entity ent)
	{
		int index = GetWaypointIndex(ent.getLocation());

		double score =  10000 * index;

		score -= UtilMath.offset(ent.getLocation(), _waypoints.get(Math.min(_waypoints.size()-1, index+1)));

		return score;
	}



	public int GetWaypointIndex(Location loc)
	{
		int best = -1;
		double bestDist = 0;

		for (int i=0 ; i<_waypoints.size() ; i++) 
		{
			Location waypoint = _waypoints.get(i);

			double dist = UtilMath.offset(waypoint, loc);

			if (best == -1 || dist < bestDist)
			{
				best = i;
				bestDist = dist;
			}
		}

		return best;
	}

	private void SortScores() 
	{
		for (int i=0 ; i<_ranks.size() ; i++)
		{
			for (int j=_ranks.size()-1 ; j>0 ; j--)
			{
				if (_ranks.get(j).Score > _ranks.get(j-1).Score)
				{
					DragonScore temp = _ranks.get(j);
					_ranks.set(j, _ranks.get(j-1));
					_ranks.set(j-1, temp);
				}
			}
		}
	}
	
	

	@Override
	@EventHandler
	public void ScoreboardUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		//Wipe Last
		for (String string : _lastScoreboard)
			GetScoreboard().resetScores(Bukkit.getOfflinePlayer(string));
		_lastScoreboard.clear();

		SortScores();

		//Write New
		for (int i=0 ; i<_ranks.size() ; i++)
		{
			DragonScore score = _ranks.get(i);

			ChatColor col = ChatColor.GREEN;
			if (!IsAlive(score.Player))
				col = ChatColor.RED;

			String out = i+1 + " " + col + score.Player.getName();

			if (out.length() >= 16)
				out = out.substring(0, 15);

			_lastScoreboard.add(out);

			GetObjectiveSide().getScore(Bukkit.getOfflinePlayer(out)).setScore(16-i);
		}
	}

	@Override
	public Location GetSpectatorLocation()
	{
		if (SpectatorSpawn == null)
		{
			SpectatorSpawn = new Location(this.WorldData.World, 0,0,0);
		}

		Vector vec = new Vector(0,0,0);
		double count = 0;

		for (Player player : GetPlayers(true))
		{				
			count++;
			vec.add(player.getLocation().toVector());
		}
		
		if (count == 0)
			count++;

		vec.multiply(1d/count);

		SpectatorSpawn.setX(vec.getX());
		SpectatorSpawn.setY(vec.getY() + 10);
		SpectatorSpawn.setZ(vec.getZ());

		return SpectatorSpawn;
	}
	
	@Override
	public void EndCheck()
	{
		if (!IsLive())
			return;
		
		if (GetPlayers(true).size() <= 0)
		{	
			//Announce
			AnnounceEnd(_places);
			
			//Gems
			if (_places.size() >= 1)
				AddGems(_places.get(0), 20, "1st Place", false);
			
			if (_places.size() >= 2)
				AddGems(_places.get(1), 15, "2nd Place", false);
			
			if (_places.size() >= 3)
				AddGems(_places.get(2), 10, "3rd Place", false);
						
			for (Player player : GetPlayers(false))
				if (player.isOnline())
					AddGems(player, 10, "Participation", false);
			
			//End
			SetState(GameState.End);
			
		}
	}
}
