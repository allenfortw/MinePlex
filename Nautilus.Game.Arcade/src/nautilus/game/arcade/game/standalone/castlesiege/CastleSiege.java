package nautilus.game.arcade.game.standalone.castlesiege;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftCreature;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.AsymTeamGame;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.standalone.castlesiege.kits.*;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.NullKit;
import net.minecraft.server.v1_6_R2.EntityCreature;
import net.minecraft.server.v1_6_R2.Navigation;

public class CastleSiege extends AsymTeamGame
{
	private long _tntSpawn = 0;
	private ArrayList<Location> _tntSpawns = new ArrayList<Location>();
	private ArrayList<Location> _tntWeakness = new ArrayList<Location>();
	private HashMap<Player, FallingBlock> _tntCarry = new HashMap<Player, FallingBlock>();
	
	private ArrayList<Location> _kingLocs;
	private Creature _king;
	private Location _kingLoc;
	private String _kingName;
	private Player _kingDamager = null;

	private ArrayList<Location> _peasantSpawns;

	public CastleSiege(ArcadeManager manager, HashMap<String, ChatColor> pastTeams)
	{
		super(manager, GameType.CastleSiege,

				new Kit[] 
						{ 

				new KitHumanKnight(manager),
				new KitHumanMarksman(manager),
				new KitHumanElementalist(manager),
				new KitHumanPeasant(manager),
				new NullKit(manager),
				new KitUndeadGhoul(manager),
				new KitUndeadZombie(manager),
				new KitUndeadArcher(manager),
						},

						new String[]
								{
				F.elem(C.cAqua + "Defenders") + C.cWhite + " must defend the King.",
				F.elem(C.cAqua + "Defenders") + C.cWhite + " win when the sun rises.",
				F.elem(C.cAqua + "Defenders") + C.cWhite + " respawn as peasants.",
				"",
				F.elem(C.cRed + "Undead") + C.cWhite + " must kill the King.",
				F.elem(C.cRed + "Undead") + C.cWhite + " lose when the sun rises.",

								}, pastTeams);

		this.HungerSet = 20;
		this.DeathOut = false;
		this.DeathDropItems = false;
		this.WorldTimeSet = 14000; //14000
		this.BlockPlaceAllow.add(85);
		
		_kingName = C.cYellow + C.Bold + "King Jonalon";

	}

	@Override
	public void ParseData()
	{
		_tntSpawns = WorldData.GetDataLocs("RED");
		_tntWeakness = WorldData.GetDataLocs("BLACK");

		_kingLocs =  WorldData.GetDataLocs("YELLOW");

		_peasantSpawns = WorldData.GetDataLocs("GREEN");
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
					if (kit.GetName().contains("Castle"))
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

	@EventHandler(priority = EventPriority.HIGH)
	public void GameStateChange(GameStateChangeEvent event)
	{
		if (event.GetState() != GameState.Prepare)
			return;
		
		//Spawn King
		this.CreatureAllowOverride = true;

		_kingLoc = _kingLocs.get(UtilMath.r(_kingLocs.size()));

		_king = (Creature) _kingLoc.getWorld().spawnEntity(_kingLoc, EntityType.ZOMBIE);

		_king.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		_king.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
		_king.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		_king.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));

		_king.setCustomName(_kingName);
		_king.setCustomNameVisible(true);
		
		_king.setRemoveWhenFarAway(false);
		
		this.CreatureAllowOverride = false;

		//Change to Peasant Spawns
		this.GetTeam(ChatColor.AQUA).SetSpawns(_peasantSpawns);
	}

	@EventHandler
	public void KingTarget(EntityTargetEvent event)
	{
		event.setCancelled(true);
	}

	@EventHandler
	public void KingDamage(CustomDamageEvent event)
	{
		if (_king == null || !_king.isValid())
			return;

		if (!event.GetDamageeEntity().equals(_king))
			return;

		Player damager = event.GetDamagerPlayer(true);
		if (damager == null)	return;

		GameTeam team = GetTeam(damager);

		if (team == null || team.GetColor() != ChatColor.RED)
		{
			event.SetCancelled("King Damage");
			return;
		}
		else
		{
			_kingDamager = damager;
		}
	}
	
	@EventHandler
	public void KingUpdate(UpdateEvent event)
	{
		if (GetState() != GameState.Live)
			return;
		
		if (event.getType() != UpdateType.SEC)
			return;
		
		if (_king == null)
			return;
		
		if (UtilMath.offset(_king.getLocation(), _kingLoc) > 6)
		{
			_king.teleport(_kingLoc);
		}
		else
		{
			EntityCreature ec = ((CraftCreature)_king).getHandle();
			Navigation nav = ec.getNavigation();
			nav.a(_kingLoc.getX(), _kingLoc.getY(), _kingLoc.getZ(), 0.8f);
		}
	}

	@EventHandler
	public void PlayerDeath(PlayerDeathEvent event) 
	{
		if (GetTeam(ChatColor.AQUA).HasPlayer(event.getEntity()))
			SetKit(event.getEntity(), GetKits()[3], true);
	}

	@Override
	@EventHandler
	public void ScoreboardUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;

		//King
		if (_king != null && _king.isValid())
		{
			GetObjectiveSide().getScore(Bukkit.getOfflinePlayer(C.cYellow + C.Bold + "Kings Health")).setScore((int) _king.getHealth());
		}
		
		//Teams
		HashMap<String, Integer> _scoreGroup = new HashMap<String, Integer>();
		_scoreGroup.put(C.cAqua + "Peasants", 0);
		_scoreGroup.put(C.cAqua + "Defenders", 0);
		_scoreGroup.put(C.cRed + "Undead", 0);
		
		for (Player player : UtilServer.getPlayers())
		{
			if (!IsAlive(player))
				continue;
			
			Kit kit = GetKit(player);
			if (kit == null)	continue;
			
			if (kit.GetName().contains("Castle"))
			{
				if (kit.GetName().contains("Peasant"))
				{
					_scoreGroup.put(C.cAqua + "Peasants", 1 + _scoreGroup.get(C.cAqua + "Peasants"));
				}
				else
				{
					_scoreGroup.put(C.cAqua + "Defenders", 1 + _scoreGroup.get(C.cAqua + "Defenders"));
				}
			}
			else if (kit.GetName().contains("Undead"))
			{
				_scoreGroup.put(C.cRed + "Undead", 1 + _scoreGroup.get(C.cRed + "Undead"));
			}
		}
		
		for (String group : _scoreGroup.keySet())
		{
			GetObjectiveSide().getScore(Bukkit.getOfflinePlayer(group)).setScore(_scoreGroup.get(group));
		}
	}

	@Override
	public void EndCheck()
	{
		if (!IsLive())
			return;
		
		if (this.WorldTimeSet > 24100)
		{
			SetCustomWinLine(_kingName + ChatColor.RESET + " has survived the seige!");
			
			SetState(GameState.End);
			AnnounceEnd(GetTeam(ChatColor.AQUA));

			for (GameTeam team : GetTeamList())
			{
				if (WinnerTeam != null && team.equals(WinnerTeam))
				{
					for (Player player : team.GetPlayers(false))
					{
						AddGems(player, 10, "Winning Team", false);
					}
				}

				for (Player player : team.GetPlayers(false))
					if (player.isOnline())
						AddGems(player, 10, "Participation", false);
			}
		}

		if (!_king.isValid())
		{
			if (_kingDamager != null)
			{
				SetCustomWinLine(C.cRed + _kingDamager.getName() + C.cWhite + " slaughtered " + _kingName + ChatColor.RESET + "!");
				AddGems(_kingDamager, 20, "King Slayer", false);
			}
			else
				SetCustomWinLine(_kingName + ChatColor.RESET + " has died!");
			
			SetState(GameState.End);
			AnnounceEnd(GetTeam(ChatColor.RED));

			for (GameTeam team : GetTeamList())
			{
				if (WinnerTeam != null && team.equals(WinnerTeam))
				{
					for (Player player : team.GetPlayers(false))
					{
						AddGems(player, 10, "Winning Team", false);
					}
				}

				for (Player player : team.GetPlayers(false))
					if (player.isOnline())
						AddGems(player, 10, "Participation", false);
			}
		}
	}

	@EventHandler
	public void TNTSpawn(UpdateEvent event)
	{
		if (GetState() != GameState.Live)
			return;
		
		if (event.getType() != UpdateType.SEC)
			return;

		if (!UtilTime.elapsed(_tntSpawn, 40000))
			return;
		
		if (_tntSpawns.isEmpty())
			return;

		Location loc = _tntSpawns.get(UtilMath.r(_tntSpawns.size()));

		if (loc.getBlock().getTypeId() == 46)
			return;

		loc.getBlock().setTypeId(46);
		_tntSpawn = System.currentTimeMillis();
	}

	@EventHandler(priority = EventPriority.LOW)
	public void TNTPickup(PlayerInteractEvent event)
	{
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;

		if (event.getClickedBlock().getTypeId() != 46)
			return;

		event.setCancelled(true);

		Player player = event.getPlayer();

		if (!IsAlive(player))
			return;
		
		if (!GetTeam(ChatColor.RED).HasPlayer(player))
			return;

		if (_tntCarry.containsKey(player))
			return;

		event.getClickedBlock().setTypeId(0);

		FallingBlock tnt = player.getWorld().spawnFallingBlock(player.getEyeLocation(), 46, (byte)0);

		player.eject();
		player.setPassenger(tnt);

		_tntCarry.put(player, tnt);

		UtilPlayer.message(player, F.main("Game", "You picked up " + F.skill("TNT") + "."));
		UtilPlayer.message(player, F.main("Game", F.elem("Right-Click") + " to detonate yourself."));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void TNTUse(PlayerInteractEvent event)
	{
		if (event.isCancelled())
			return;

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)
			return;	

		Player player = event.getPlayer();

		if (!_tntCarry.containsKey(player))
			return;

		event.setCancelled(true);
		
		for (Location loc : _tntSpawns)
		{
			if (UtilMath.offset(player.getLocation(), loc) < 16)
			{
				UtilPlayer.message(player, F.main("Game", "You cannot " + F.skill("Detonate") + " so far from the Castle."));
				return;
			}
		}

		_tntCarry.remove(player).remove();

		TNTPrimed tnt = player.getWorld().spawn(player.getEyeLocation(), TNTPrimed.class);	
		tnt.setFuseTicks(0);
		UtilPlayer.message(player, F.main("Game", "You used " + F.skill("Detonate") + "."));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void TNTDeath(PlayerDeathEvent event)
	{
		Player player = event.getEntity();

		if (!_tntCarry.containsKey(player))
			return;

		_tntCarry.remove(player).remove();

		TNTPrimed tnt = player.getWorld().spawn(player.getEyeLocation(), TNTPrimed.class);	
		tnt.setFuseTicks(0);
		UtilPlayer.message(player, F.main("Game", "You used " + F.skill("Detonate") + "."));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void TNTDamageDivert(ProjectileHitEvent event)
	{
		for (Player player : _tntCarry.keySet())
		{
			if (player.getPassenger() == null)
				continue;

			double dist = UtilMath.offset(player.getPassenger().getLocation(), event.getEntity().getLocation().add(event.getEntity().getVelocity()));

			if (dist < 2)
			{
				int damage = (int) (9 * (event.getEntity().getVelocity().length() / 3d));

				//Damage Event
				Manager.GetDamage().NewDamageEvent(player, event.getEntity().getShooter(), event.getEntity(), 
						DamageCause.CUSTOM, damage, true, false, false,
						null, GetName());

				event.getEntity().remove();
			}
		}	
	} 


	@EventHandler
	public void TNTExpire(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FASTER)
			return;

		Iterator<Player> tntIterator = _tntCarry.keySet().iterator();

		while (tntIterator.hasNext())
		{	
			Player player = tntIterator.next();
			FallingBlock block = _tntCarry.get(player);
			
			if (player.isDead() || !block.isValid() || block.getTicksLived() > 600)
			{
				player.eject();
				block.remove();

				TNTPrimed tnt = player.getWorld().spawn(player.getEyeLocation(), TNTPrimed.class);
				tnt.setFuseTicks(0);

				tntIterator.remove();
				continue;
			}

			FireworkEffect effect = FireworkEffect.builder().withColor(Color.RED).with(Type.BURST).build();

			try 
			{
				Manager.GetFirework().playFirework(player.getEyeLocation(), effect);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}	
	
	@EventHandler
	public void TNTWeakness(ExplosionPrimeEvent event)
	{
		Location weakness = null;
		for (Location loc : _tntWeakness)
		{
			if (UtilMath.offset(loc, event.getEntity().getLocation()) < 4)
			{
				weakness = loc;
				break;
			}
		}
		
		if (weakness == null)
			return;
		
		_tntWeakness.remove(weakness);
		
		final Location extra = weakness;
		
		for (int i=0 ; i<10 ; i++)
		{
			Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(Manager.GetPlugin(), new Runnable()
			{
				public void run()
				{
					TNTPrimed tnt = extra.getWorld().spawn(extra.clone().add(3 - UtilMath.r(6), 5 + UtilMath.r(2), 3 - UtilMath.r(6)), TNTPrimed.class);
					tnt.setFuseTicks(0);
					tnt.setIsIncendiary(true);
				}
			}, i*3);
		}
		
		weakness.getWorld().playSound(weakness, Sound.EXPLODE, 16f, 0.8f);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void AttackerBlockBreak(org.bukkit.event.block.BlockBreakEvent event)
	{
		GameTeam team = GetTeam(event.getPlayer());
		if (team == null)
			return;
		
		if (team.GetColor() != ChatColor.RED)
			return;
		
		if (event.getBlock().getTypeId() == 85)
			event.setCancelled(false);
	}
	
	@EventHandler
	public void DefenderBlockPlace(org.bukkit.event.block.BlockPlaceEvent event)
	{
		GameTeam team = GetTeam(event.getPlayer());
		if (team == null)
			return;
		
		if (team.GetColor() != ChatColor.AQUA)
			return;
		
		if (event.getBlock().getTypeId() != 85)
			return;
		
		for (Block block : UtilBlock.getSurrounding(event.getBlock()))
		{
			if (block.isLiquid())
			{
				event.setCancelled(true);
				UtilPlayer.message(event.getPlayer(), F.main("Game", "You cannot place " + F.elem("Barricade") + " in water."));
			}
		}

		if (event.getBlockAgainst().getTypeId() == 85)
		{
			event.setCancelled(true);
			UtilPlayer.message(event.getPlayer(), F.main("Game", "You cannot place " + F.elem("Barricade") + " on each other."));
		}
		
		if (_king != null && UtilMath.offset(_king.getLocation(), event.getBlock().getLocation().add(0.5, 0.5, 0.5)) < 4)
		{
			event.setCancelled(true);
			UtilPlayer.message(event.getPlayer(), F.main("Game", "You cannot place " + F.elem("Barricade") + " near " + F.elem(C.cAqua + _kingName) + "."));
		}
	}
	
	@EventHandler
	public void DayTimer(UpdateEvent event) 
	{
		if (GetState() != GameState.Live)
			return;

		if (event.getType() != UpdateType.TICK)
			return;
		
		WorldTimeSet = (WorldTimeSet+1);
		
		long timeLeft = 24000 - WorldTimeSet;
		timeLeft = timeLeft / 20 * 1000;
		
		
		if (timeLeft > 0)
			GetObjectiveSide().setDisplayName(
					ChatColor.WHITE + "§lSun Rise: " + C.cGreen + "§l"
							+ UtilTime.MakeStr(timeLeft));
		else
		{
			GetObjectiveSide().setDisplayName(
					ChatColor.WHITE + "§lSun has risen!");
			
			for (Player player : GetTeam(ChatColor.RED).GetPlayers(true))
				Manager.GetCondition().Factory().Ignite("Sun Damage", player, player, 5, false, false);
		}
			
	}
}
