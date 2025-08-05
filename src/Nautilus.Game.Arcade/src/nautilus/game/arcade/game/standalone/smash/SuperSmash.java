package nautilus.game.arcade.game.standalone.smash;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeFormat;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.GameTeam.PlayerState;
import nautilus.game.arcade.game.standalone.smash.kits.*;
import nautilus.game.arcade.kit.Kit;

public class SuperSmash extends SoloGame
{
	private HashMap<Player, Integer> _lives = new HashMap<Player, Integer>();
	
	private ArrayList<String> _lastScoreboard = new ArrayList<String>();

	public SuperSmash(ArcadeManager manager) 
	{
		super(manager, GameType.Smash,

				new Kit[]
						{
				new KitSkeleton(manager),
				new KitGolem(manager),
				new KitSpider(manager),
				new KitSlime(manager),
				
				new KitCreeper(manager),
				new KitEnderman(manager),
				new KitSnowman(manager),
				new KitBlaze(manager),
				
				new KitChicken(manager),
				new KitKnight(manager),
				new KitSkySquid(manager),
				new KitWitherSkeleton(manager),
				
						},

						new String[]
								{
				"Each player has 3 respawns",
				"Last player alive wins!"

								});

		this.DeathOut = false;
		
		this.DeathDropItems = false;

		this.DamageTeamSelf = true;

		this.HungerSet = 20;
		
		this.CompassEnabled = true;
		
		this.SpawnDistanceRequirement = 16;
		
		this.InventoryOpen = false;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void GameStateChange(GameStateChangeEvent event)
	{
		if (event.GetState() != GameState.Prepare)
			return;

		for (Player player : GetPlayers(true))
			_lives.put(player, 4);
	}

	@EventHandler
	public void PlayerOut(PlayerDeathEvent event)
	{
		if (!LoseLife(event.getEntity()))
		{
			this.SetPlayerState(event.getEntity(), PlayerState.OUT);
		}
	}

	private int GetLives(Player player)
	{
		if (!_lives.containsKey(player))
			return 0;
		
		if (!IsAlive(player))
			return 0;
		
		return _lives.get(player);
	}
	
	private boolean LoseLife(Player player) 
	{
		int lives = GetLives(player) - 1;
		
		if (lives > 0)
		{
			UtilPlayer.message(player, C.cRed + C.Bold + "You have died!");
			UtilPlayer.message(player, C.cRed + C.Bold + "You have " + lives + " lives left!");
			player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 2f, 0.5f);
			
			_lives.put(player, lives);
			
			return true;
		}
		else
		{
			UtilPlayer.message(player, C.cRed + C.Bold + "You are out of the game!");
			player.playSound(player.getLocation(), Sound.EXPLODE, 2f, 1f);
			
			return false;
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
		{
			GetScoreboard().resetScores(Bukkit.getOfflinePlayer(string));
		}
		_lastScoreboard.clear();

		//Write New
		for (Player player : GetPlayers(true))
		{
			int lives = GetLives(player);
			
			String out;
			if (lives >= 4)			out = C.cGreen + player.getName();
			else if (lives == 3)	out = C.cYellow + player.getName();
			else if (lives == 2)	out = C.cGold + player.getName();
			else if (lives == 1)	out = C.cRed + player.getName();
			else if (lives == 0)	out = C.cRed + player.getName();
			else
				continue;

			if (out.length() >= 16)
				out = out.substring(0, 15);

			_lastScoreboard.add(out);

			GetObjectiveSide().getScore(Bukkit.getOfflinePlayer(out)).setScore(lives);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void FallDamage(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;
		
		if (event.GetCause() == DamageCause.FALL)
			event.SetCancelled("No Fall Damage");
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void Knockback(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;
		
		if (event.GetDamageePlayer() != null)
			event.AddKnockback("Smash Knockback", 1 + 0.1 * (20 - event.GetDamageePlayer().getHealth()));
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void ArenaWalls(CustomDamageEvent event)
	{
		if (event.IsCancelled())
			return;

		if (event.GetCause() == DamageCause.VOID || event.GetCause() == DamageCause.LAVA)
		{
			event.GetDamageeEntity().eject();
			event.GetDamageeEntity().leaveVehicle();
			
			if (event.GetDamageePlayer() != null)
				event.GetDamageeEntity().getWorld().strikeLightningEffect(event.GetDamageeEntity().getLocation());
			
			event.AddMod("Smash", "Super Smash Mobs", 5000, false);
		}	
	}
	
	@EventHandler
	public void HealthChange(EntityRegainHealthEvent event)
	{
		if (event.getRegainReason() == RegainReason.SATIATED)
			event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void EntityDeath(EntityDeathEvent event)
	{
		event.getDrops().clear();
	}
	
	@Override
	public void SetKit(Player player, Kit kit, boolean announce) 
	{
		GameTeam team = GetTeam(player);
		if (team != null)
		{
			if (!team.KitAllowed(kit))
			{
				player.playSound(player.getLocation(), Sound.NOTE_BASS, 2f, 0.5f);
				UtilPlayer.message(player, F.main("Kit", F.elem(team.GetFormattedName()) + " cannot use " + F.elem(kit.GetFormattedName() + " Kit") + "."));
				return;
			}
		}

		_playerKit.put(player, kit);

		if (announce)
		{
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 2f, 1f);
			UtilPlayer.message(player, F.main("Kit", "You equipped " + F.elem(kit.GetFormattedName() + " Kit") + "."));
			kit.ApplyKit(player);
			UtilInv.Update(player);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void AbilityDescription(PlayerInteractEvent event)
	{
		if (event.isCancelled())
			return;
		
		Player player = event.getPlayer();
		
		if (player.getItemInHand() == null)
			return;
		
		if (player.getItemInHand().getItemMeta() == null)
			return;
		
		if (player.getItemInHand().getItemMeta().getDisplayName() == null)
			return;
		
		if (player.getItemInHand().getItemMeta().getLore() == null)
			return;
		
		if (Manager.GetGame() == null || Manager.GetGame().GetState() != GameState.Recruit)
			return;
		
		for (int i=player.getItemInHand().getItemMeta().getLore().size() ; i<=7 ; i++)
			UtilPlayer.message(player, " ");
		
		UtilPlayer.message(player, ArcadeFormat.Line);

		UtilPlayer.message(player, "§aAbility - §f§l" + player.getItemInHand().getItemMeta().getDisplayName());
		
		//Perk Descs
		for (String line : player.getItemInHand().getItemMeta().getLore())
		{
			UtilPlayer.message(player, line);
		}
		
		UtilPlayer.message(player, ArcadeFormat.Line);
		
		player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 2f);
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void ExplosionDamageCancel(EntityDamageEvent event)
	{
		if (event.getCause() == DamageCause.ENTITY_EXPLOSION || event.getCause() == DamageCause.BLOCK_EXPLOSION)
		{
			event.setCancelled(true);
		}
	}
}
