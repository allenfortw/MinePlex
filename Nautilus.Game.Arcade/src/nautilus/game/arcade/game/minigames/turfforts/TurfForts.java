package nautilus.game.arcade.game.minigames.turfforts;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scoreboard.Score;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.MapUtil;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.game.minigames.turfforts.kits.*;
import nautilus.game.arcade.kit.Kit;
import net.minecraft.server.v1_6_R2.EntityArrow;
import net.minecraft.server.v1_6_R2.Item;

public class TurfForts extends TeamGame
{
	private ArrayList<Location> _turf;

	private Location _red;
	private Location _redBase;

	private Location _blue;
	private Location _blueBase;

	private int xRed = 0;
	private int zRed = 0;

	private long _phaseTime = 0;
	private long _buildTime = 20000;
	private long _fightTime = 90000;
	private boolean _fight = false;
	private int _lines = 0;
	
	private HashMap<Player, Long> _enemyTurf = new HashMap<Player, Long>();

	public TurfForts(ArcadeManager manager)
	{
		super(manager, GameType.TurfForts,

				new Kit[] 
						{ 

				new KitMarksman(manager),
				new KitInfiltrator(manager),
				new KitShredder(manager),
						},

						new String[]
								{
				"You have 30 seconds to build your Fort!",
				"",
				"Each kill advances your turf forwards.",
				"Take over all the turf to win!"

								});

		this.HungerSet = 20;
		this.DeathOut = false;
		this.DeathDropItems = false;
		this.BlockPlaceAllow.add(35); 
		this.BlockBreakAllow.add(35);
		this.ItemDrop = false;
		this.ItemPickup = false;
		this.DamageSelf = false;
	}

	@Override
	public void ParseData()
	{
		_turf = WorldData.GetDataLocs("YELLOW");

		_red = WorldData.GetDataLocs("RED").get(0);
		_redBase = WorldData.GetDataLocs("PINK").get(0);

		_blue = WorldData.GetDataLocs("BLUE").get(0);
		_blueBase = WorldData.GetDataLocs("LIGHT_BLUE").get(0);

		if (_red.getBlockX() > _blue.getBlockX())			xRed = 1;
		else if (_red.getBlockX() < _blue.getBlockX())		xRed = -1;

		if (_red.getBlockZ() > _blue.getBlockZ())			zRed = 1;
		else if (_red.getBlockZ() < _blue.getBlockZ())		zRed = -1;

		//Color Turf
		for (Location loc : _turf)
		{
			if (UtilMath.offset(loc, _red) < UtilMath.offset(loc, _blue))
				MapUtil.QuickChangeBlockAt(loc, 159, (byte)14);
			else
				MapUtil.QuickChangeBlockAt(loc, 159, (byte)3);
		}
	}

	@EventHandler
	public void PlayerKillAward(CombatDeathEvent event)
	{
		if (!(event.GetEvent().getEntity() instanceof Player))
			return;

		if (event.GetLog().GetKiller() == null)
			return;
		
		Player killed = (Player)event.GetEvent().getEntity();

		Player killer = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
		if (killer == null)
			return;

		if (GetTeam(killer) == null)
			return;
		
		if (GetTeam(killed) == null)
			return;
		
		if (GetTeam(killer).equals(GetTeam(killed)))
			return;

		if (GetTeam(killer).GetColor() == ChatColor.RED)
		{
			TurfMove(true);
		}
		else
		{
			TurfMove(false);
		}
	}

	private void TurfMove(boolean red) 
	{
		for (int line=0 ; line<GetLinesPerKill() ; line++)
		{
			if (red)
			{
				if (xRed != 0)
					for (Location loc : _turf)
						if (loc.getBlockX() == _blue.getBlockX())
						{
							MapUtil.QuickChangeBlockAt(loc, 159, (byte)14);

							for (int i=1 ; i<6 ; i++)
								MapUtil.QuickChangeBlockAt(loc.clone().add(0, i, 0), 0, (byte)0);
						}


				if (zRed != 0)
					for (Location loc : _turf)
						if (loc.getBlockZ() == _blue.getBlockZ())
						{
							MapUtil.QuickChangeBlockAt(loc, 159, (byte)14);

							for (int i=1 ; i<6 ; i++)
								MapUtil.QuickChangeBlockAt(loc.clone().add(0, i, 0), 0, (byte)0);
						}

				_red.subtract(xRed, 0, zRed);
				_blue.subtract(xRed, 0, zRed);
			}
			else 
			{
				if (xRed != 0)
					for (Location loc : _turf)
						if (loc.getBlockX() == _red.getBlockX())
						{
							MapUtil.QuickChangeBlockAt(loc, 159, (byte)3);

							for (int i=1 ; i<6 ; i++)
								MapUtil.QuickChangeBlockAt(loc.clone().add(0, i, 0), 0, (byte)0);
						}

				if (zRed != 0)
					for (Location loc : _turf)
						if (loc.getBlockZ() == _red.getBlockZ())
						{
							MapUtil.QuickChangeBlockAt(loc, 159, (byte)3);

							for (int i=1 ; i<6 ; i++)
								MapUtil.QuickChangeBlockAt(loc.clone().add(0, i, 0), 0, (byte)0);
						}

				_red.add(xRed, 0, zRed);
				_blue.add(xRed, 0, zRed);
			}

			EndCheck();
		}
	}

	@EventHandler
	public void BowCancel(PlayerInteractEvent event)
	{
		if (!_fight)
		{
			if (UtilGear.isMat(event.getPlayer().getItemInHand(), Material.BOW))
			{
				event.setCancelled(true);
				UtilPlayer.message(event.getPlayer(), F.main("Game", "You cannot attack during Build Time!"));
			}

		}	
	}

	@EventHandler
	public void BlockPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled())
			return;

		GameTeam team = GetTeam(event.getPlayer());
		if (team == null)	
		{
			event.setCancelled(true);
			return;
		}

		if (event.getBlock().getLocation().getY() > _turf.get(0).getBlockY() + 5)
		{
			UtilPlayer.message(event.getPlayer(), F.main("Game", "You cannot build this high up."));
			event.setCancelled(true);
			return;
		}

		Block block = event.getBlock().getRelative(BlockFace.DOWN);
		while (block.getTypeId() == 0)
			block = block.getRelative(BlockFace.DOWN);

		if (block.getData() != team.GetColorData())
		{
			UtilPlayer.message(event.getPlayer(), F.main("Game", "You can only build above " + F.elem(team.GetColor() + team.GetName()) + "."));
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void BlockDamage(ProjectileHitEvent event)
	{
		if (event.getEntity().getShooter() == null)
			return;

		if (!(event.getEntity() instanceof Arrow))
			return;

		if (!(event.getEntity().getShooter() instanceof Player))
			return;

		Player shooter = (Player)event.getEntity().getShooter();
		final GameTeam team = GetTeam(shooter);
		if (team == null)
			return;

		final Arrow arrow = (Arrow)event.getEntity();

		Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(Manager.GetPlugin(), new Runnable()
		{
			public void run()
			{
				try
				{
					EntityArrow entityArrow = ((CraftArrow)arrow).getHandle();

					Field fieldX = EntityArrow.class.getDeclaredField("d");
					Field fieldY = EntityArrow.class.getDeclaredField("e");
					Field fieldZ = EntityArrow.class.getDeclaredField("f");

					fieldX.setAccessible(true);
					fieldY.setAccessible(true);
					fieldZ.setAccessible(true);

					int x = fieldX.getInt(entityArrow);
					int y = fieldY.getInt(entityArrow);
					int z = fieldZ.getInt(entityArrow);

					Block block = arrow.getWorld().getBlockAt(x, y, z);

					if (block.getTypeId() == 35)
					{
						if (block.getData() == 14 && team.GetColor() != ChatColor.RED)
						{
							block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK.getId());
						}
						else if (block.getData() == 3 && team.GetColor() != ChatColor.AQUA)
						{
							block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.LAPIS_BLOCK.getId());
						}

						block.breakNaturally();
					}
					
					arrow.remove();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}  
			}
		}, 0);
	}

	@EventHandler
	public void Damage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;
		
		if (!_fight)
		{
			event.SetCancelled("Build Time");
		}
		
		Player damager = event.GetDamagerPlayer(true);
		if (damager == null)	return;

		event.AddMod("Turf Forts", "Nullify", -event.GetDamageInitial(), false);

		if (event.GetCause() == DamageCause.PROJECTILE)
		{
			if (GetKit(damager).GetName().contains("Shredder"))
			{
				event.AddMod("Turf Forts", "One Hit Kill", 15, false);
			}
			else
			{
				event.AddMod("Turf Forts", "One Hit Kill", 30, false);
			}
		}
		else if (event.GetCause() == DamageCause.ENTITY_ATTACK)
		{
			if (UtilGear.isMat(damager.getItemInHand(), Material.IRON_SWORD))
			{
				event.AddMod("Turf Forts", "One Hit Kill", 12, false);
			}
			else
			{
				event.AddMod("Turf Forts", "One Hit Kill", 6, false);
			}
		}
	}

	@EventHandler
	public void ScoreboardTitle(UpdateEvent event) 
	{
		if (GetState() != GameState.Live)
			return;
		
		//2x Initial Build
		if (_phaseTime == 0)
			_phaseTime = System.currentTimeMillis() + _buildTime;

		if (event.getType() != UpdateType.TICK)
			return;

		long time;
		if (!_fight)
		{
			time = _buildTime - (System.currentTimeMillis() - _phaseTime);
			
			long displayTime = Math.max(0, time);
			GetObjectiveSide().setDisplayName(ChatColor.WHITE + C.Bold + "Build Time " + C.cGreen + C.Bold+ UtilTime.MakeStr(displayTime));
			
			if (time <= 0)
			{
				_fight = true;
				_lines++;
				
				Announce(" ");
				Announce(C.cWhite + C.Bold + "1 Kill" + C.cWhite + C.Bold + " = " + C.cWhite + C.Bold + GetLinesPerKill() + " Turf Lines");
				Announce(C.cWhite + C.Bold + "90 Seconds of " + C.cYellow + C.Bold + "Combat Time" + C.cWhite + C.Bold + " has begun!");
				Announce(" ");
				
				_phaseTime = System.currentTimeMillis();
			}
		}
			
		else
		{
			time = _fightTime - (System.currentTimeMillis() - _phaseTime);
			
			long displayTime = Math.max(0, time);
			GetObjectiveSide().setDisplayName(ChatColor.WHITE + C.Bold + "Combat Time " + C.cGreen + C.Bold+ UtilTime.MakeStr(displayTime));
			
			if (time <= 0)
			{
				_fight = false;
				
				Announce(" ");
				Announce(C.cWhite + C.Bold + "20 Seconds of " + C.cGreen + C.Bold + "Build Time" + C.cWhite + C.Bold + " has begun!");
				Announce(" ");
				
				_phaseTime = System.currentTimeMillis();
				
				for (GameTeam team : GetTeamList())
				{
					for (Player player : team.GetPlayers(true))
					{
						player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.WOOL, team.GetColorData(), 16));
					}
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

		for (GameTeam team : this.GetTeamList())
		{			
			String name = team.GetColor() + team.GetName();
			if (name.length() > 16)
				name = name.substring(0, 16);

			int lines = 0;
			if (team.GetColor() == ChatColor.RED)	lines = GetRedLines();
			else									lines = GetBlueLines();

			Score score = GetObjectiveSide().getScore(Bukkit.getOfflinePlayer(name));
			score.setScore(lines);
		}
	}

	public int GetRedLines()
	{
		if (!InProgress())
			return 0;

		if (xRed != 0)
		{
			return Math.abs(_redBase.getBlockX() - _red.getBlockX());
		}

		return Math.abs(_redBase.getBlockZ() - _red.getBlockZ());
	}

	public int GetBlueLines()
	{
		if (!InProgress())
			return 0;

		if (xRed != 0)
		{
			return Math.abs(_blueBase.getBlockX() - _blue.getBlockX());
		}

		return Math.abs(_blueBase.getBlockZ() - _blue.getBlockZ());
	}

	public int GetLinesPerKill()
	{
		return _lines;
		//return Math.min(5, 1 + (int)((System.currentTimeMillis() - GetStateTime() - 30000) / 60000));
	}

	@EventHandler
	public void Territory(UpdateEvent event)
	{
		if (!IsLive())
			return;

		if (event.getType() != UpdateType.FASTER)
			return;

		for (GameTeam team : this.GetTeamList())
		{	
			for (Player player : team.GetPlayers(true))
			{				
				Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
				while (block.getTypeId() == 0)
					block = block.getRelative(BlockFace.DOWN);

				byte data = block.getData();

				//Slow
				if (_enemyTurf.containsKey(player))
				{
					int time = (int) ((System.currentTimeMillis() - _enemyTurf.get(player))/2500);
					
					if (time > 0)
						Manager.GetCondition().Factory().Slow("Infiltrator Slow", player, player, 0.9, time-1, false, false, false, false);
				}
				
				//On Enemy Turf
				if ((team.GetColor() == ChatColor.RED && data == 3) || (team.GetColor() == ChatColor.AQUA && data == 14))
				{
					
					//Infiltrate
					if (_fight && GetKit(player) != null && GetKit(player).GetName().contains("Infil"))
					{
						
						if (!_enemyTurf.containsKey(player))
							_enemyTurf.put(player, System.currentTimeMillis());

						continue;
					}
					
					if (Recharge.Instance.use(player, "Territory Knockback", 2000, false))
					{
						UtilAction.velocity(player, UtilAlg.getTrajectory2d(player.getLocation(), team.GetSpawn()), 2, false, 0, 0.8, 1, true);

						player.damage(10);

						player.playSound(player.getLocation(), Sound.NOTE_BASS, 2f, 1f);
						UtilPlayer.message(player, F.main("Game", "You cannot walk on the enemies turf!"));
					}

					return;
				}
				//On Own Turf
				else if ((team.GetColor() == ChatColor.RED && data == 14) || (team.GetColor() == ChatColor.AQUA && data == 3))
				{
					_enemyTurf.remove(player);
				} 
			}
		}
	}

	@EventHandler
	public void ItemRemoval(UpdateEvent event)
	{
		if (!IsLive())
			return;

		if (event.getType() != UpdateType.FAST)
			return;

		for (Entity ent : _red.getWorld().getEntities())
		{
			if (!(ent instanceof Item))
				return;

			if (ent.getTicksLived() > 40)
				ent.remove();
		}
	}

	@Override
	public void EndCheck()
	{
		if (!IsLive())
			return;

		if (GetRedLines() == 0)
		{
			AnnounceEnd(GetTeam(ChatColor.AQUA));
		}
		else if (GetBlueLines() == 0)
		{
			AnnounceEnd(GetTeam(ChatColor.RED));
		}
		else
			return;

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
