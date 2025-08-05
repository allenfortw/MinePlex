package nautilus.game.arcade.game.standalone.zombiesurvival;

import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftCreature;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.standalone.zombiesurvival.kits.*;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.NullKit;
import net.minecraft.server.v1_6_R2.EntityCreature;
import net.minecraft.server.v1_6_R2.Navigation;

public class ZombieSurvival extends SoloGame
{
	private GameTeam _survivors;
	private GameTeam _undead;

	private HashMap<Creature, ZombieData> _mobs = new HashMap<Creature, ZombieData>();
	
	public ZombieSurvival(ArcadeManager manager) 
	{
		super(manager, GameType.ZombieSurvival,

				new Kit[]
						{
				new KitSurvivorKnight(manager),
				new KitSurvivorRogue(manager),
				new KitSurvivorArcher(manager),
				new NullKit(manager),
				new KitUndeadAlpha(manager),
				new KitUndeadZombie(manager),
						},

						new String[]
								{
				"The Undead are attacking!",
				"Run, fight or hide to survive!",
				"When you die, you become Undead",
				"The last Survivor alive wins!"
								});
		
		this.DeathOut = false;
		this.DeathDropItems = false;
		this.HungerSet = 20;
	}

	@Override
	public void ParseData()
	{
		if (!WorldData.GetDataLocs("WHITE").isEmpty())
			WorldHeightLimit = WorldData.GetDataLocs("WHITE").get(0).getBlockY();
	}
	
	@Override
	public void RestrictKits()
	{
		for (Kit kit : GetKits())
		{
			for (GameTeam team : GetTeamList())
			{
				if (team.GetColor() == ChatColor.RED)
				{
					if (kit.GetName().contains("Survivor"))
						team.GetRestrictedKits().add(kit);
				}
				else
				{
					if (kit.GetName().contains("Undead"))
						team.GetRestrictedKits().add(kit);
				}
			}
		}
	}

	@Override
	@EventHandler
	public void CustomTeamGeneration(GameStateChangeEvent event) 
	{
		if (event.GetState() != GameState.Recruit)
			return;

		_survivors = this.GetTeamList().get(0);
		_survivors.SetName("Survivors");

		//Undead Team
		_undead = new GameTeam("Undead", ChatColor.RED, WorldData.GetDataLocs("RED"));	
		GetTeamList().add(_undead);
		
		RestrictKits();
	}

	@Override
	public GameTeam ChooseTeam(Player player) 
	{
		return _survivors;
	}

	@EventHandler
	public void UpdateChasers(UpdateEvent event)
	{
		if (!IsLive())
			return;

		if (event.getType() != UpdateType.FAST)
			return;

		int req = 1 + _survivors.GetPlayers(true).size()/20;
			
		while (_undead.GetPlayers(true).size() < req && _survivors.GetPlayers(true).size() > 0)
		{
			Player player = _survivors.GetPlayers(true).get(UtilMath.r(_survivors.GetPlayers(true).size()));
			SetChaser(player, true);
		}
	}

	@EventHandler
	public void PlayerDeath(PlayerDeathEvent event) 
	{
		if (_survivors.HasPlayer(event.getEntity()))
			SetChaser(event.getEntity(), false);
	}

	public void SetChaser(Player player, boolean forced)
	{
		if (!GetPlaces().contains(player))
			GetPlaces().add(0, player);

		SetPlayerTeam(player, _undead);

		//Kit
		Kit newKit = this.GetKits()[4];
		if (forced)
			newKit = this.GetKits()[5];
		SetKit(player, newKit, true);
		newKit.ApplyKit(player);

		//Refresh
		for (Player other : UtilServer.getPlayers())
		{
			other.hidePlayer(player);
			other.showPlayer(player);
		}

		if (forced)
		{
			player.eject();
			player.teleport(_undead.GetSpawn());

			AddGems(player, 10, "Forced Undead", false);

			Announce(F.main("Game", F.elem(_survivors.GetColor() + player.getName()) + " has become an " + 
					F.elem(_undead.GetColor() + "Alpha Zombie") + "."));

			player.getWorld().strikeLightningEffect(player.getLocation());
		}
		
		UtilPlayer.message(player, C.cRed + C.Bold + "You have been Zombified! Braaaaaiiiinnnssss!");
	}

	@Override
	public void RespawnPlayer(final Player player)
	{
		Manager.Clear(player);

		if (_undead.HasPlayer(player))
		{
			player.eject();
			player.teleport(_undead.GetSpawn());
		}

		//Re-Give Kit
		Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(Manager.GetPlugin(), new Runnable()
		{
			public void run()
			{
				GetKit(player).ApplyKit(player);

				//Refresh on Spawn
				for (Player other : UtilServer.getPlayers())
				{
					other.hidePlayer(player);
					other.showPlayer(player);
				}
			} 
		}, 0);
	}

	@EventHandler
	public void UndeadUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			return;

		if (!InProgress())
			return;

		Iterator<Creature> mobIterator = _mobs.keySet().iterator();

		while (mobIterator.hasNext())
		{	
			Creature mob = mobIterator.next();

			if (!mob.isValid())
			{
				mob.remove();
				mobIterator.remove();
			}
		}

		if (_mobs.size() < 50)
		{
			this.CreatureAllowOverride = true;
			Zombie zombie = _undead.GetSpawn().getWorld().spawn(_undead.GetSpawn(), Zombie.class);
			_mobs.put(zombie, new ZombieData(GetTargetLocation()));
			this.CreatureAllowOverride = false;
		}

		mobIterator = _mobs.keySet().iterator();
		while (mobIterator.hasNext())
		{	
			Creature mob = mobIterator.next();
			Manager.GetCondition().Factory().Speed("Zombie Speed", mob, mob, 1.9, 1, false, false, true);

			ZombieData data = _mobs.get(mob);

			//New Target via Distance
			if (UtilMath.offset(mob.getLocation(), data.Target) < 10 || 
				UtilMath.offset2d(mob.getLocation(), data.Target) < 6 ||
				UtilTime.elapsed(data.Time, 30000))
			{
				data.SetTarget(GetTargetLocation());
				continue;
			}

			//Untarget
			if (mob.getTarget() != null)
			{
				if (UtilMath.offset2d(mob, mob.getTarget()) > 10)
				{
					mob.setTarget(null);
				}
				else
				{
					if (mob.getTarget() instanceof Player)
						if (_undead.HasPlayer((Player)mob.getTarget()))
							mob.setTarget(null);
				}
			}
			//Move
			else
			{
				//Move
				EntityCreature ec = ((CraftCreature)mob).getHandle();
				Navigation nav = ec.getNavigation();
				
				if (UtilMath.offset(mob.getLocation(), data.Target) > 20)
				{
					Location target = mob.getLocation();
					
					target.add(UtilAlg.getTrajectory(mob.getLocation(), data.Target).multiply(20));
					
					nav.a(target.getX(), target.getY(), target.getZ(), 1.2f);
				}
				else
				{
					nav.a(data.Target.getX(), data.Target.getY(), data.Target.getZ(), 1.2f);
				}
				
			}
		}
	}
	
	public Location GetTargetLocation()
	{
		if (_survivors.GetPlayers(true).size() == 0)
		{
			return _survivors.GetSpawn();
		}
		else
		{
			return _survivors.GetPlayers(true).get(UtilMath.r(_survivors.GetPlayers(true).size())).getLocation();
		}
	}

	@EventHandler
	public void UndeadTarget(EntityTargetEvent event)
	{
		if (event.getTarget() instanceof Player)
			if (_undead.HasPlayer((Player)event.getTarget()))
				event.setCancelled(true);
	}

	@EventHandler
	public void UndeadCombust(EntityCombustEvent event)
	{
		event.setCancelled(true);
	}
	
	@Override
	public void EndCheck()
	{
		if (!IsLive())
			return;

		if (_survivors.GetPlayers(true).size() <= 1)
		{
			if (_survivors.GetPlayers(true).size() == 1)
				GetPlaces().add(0, GetPlayers(true).get(0));

			if (GetPlaces().size() >= 1)
				AddGems(GetPlaces().get(0), 15, "1st Place", false);

			if (GetPlaces().size() >= 2)
				AddGems(GetPlaces().get(1), 10, "2nd Place", false);

			if (GetPlaces().size() >= 3)
				AddGems(GetPlaces().get(2), 5, "3rd Place", false);
			/*
			int sections = GetPlaces().size()/10;

			for (int i=0 ; i<5 ; i++)
			{
				for (int j=i*sections ; j < j*sections + sections ; j++)
				{
					AddGems(GetPlaces().get(j), 5-i, "Top " + ((i+1)*10) + "%", false);
				}
			}
			*/

			for (Player player : GetPlayers(false))
				if (player.isOnline())
					AddGems(player, 10, "Participation", false);

			SetState(GameState.End);
			AnnounceEnd(GetPlaces());
		}
	}

	@Override
	@EventHandler
	public void ScoreboardUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		if (_survivors == null || _undead == null)
			return;
		
		GetObjectiveSide().getScore(Bukkit.getOfflinePlayer(_survivors.GetColor() + _survivors.GetName())).setScore(_survivors.GetPlayers(true).size());
		GetObjectiveSide().getScore(Bukkit.getOfflinePlayer(_undead.GetColor() + _undead.GetName())).setScore(_undead.GetPlayers(true).size());
	}

	@Override
	public boolean CanJoinTeam(GameTeam team)
	{
		if (team.GetColor() == ChatColor.RED)
		{
			return team.GetSize() < 1 + UtilServer.getPlayers().length/25;
		}
		
		return true;
	}
}
