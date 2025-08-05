package nautilus.game.arcade.game.minigames.quiver;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.minigames.quiver.kits.*;
import nautilus.game.arcade.kit.Kit;

public class Quiver extends SoloGame
{
	private ArrayList<QuiverScore> _ranks = new ArrayList<QuiverScore>();
	private ArrayList<String> _lastScoreboard = new ArrayList<String>();
	private HashMap<Player, Integer> _combo = new HashMap<Player, Integer>();
	private HashMap<Player, Integer> _bestCombo = new HashMap<Player, Integer>();
	private HashMap<Player, Long> _deathTime = new HashMap<Player, Long>();
	
	private Objective _scoreObj;

	public Quiver(ArcadeManager manager) 
	{
		super(manager, GameType.Quiver,

				new Kit[]
						{
				new KitLeaper(manager),
				new KitBrawler(manager),
				new KitElementalist(manager),
						},

						new String[]
								{
				"Bow and Arrow insta-kills.",
				"You receive 1 Arrow per kill.",
				"Glass blocks are breakable",
				"First player to 20 kills wins."
								});

		this.HungerSet = 20;
		this.DeathDropItems = false;
		this.DeathOut = false;
		this.DamageSelf = false;
		this.DamageTeamSelf = true;
		this.PrepareFreeze = false;
		this.SpawnDistanceRequirement = 16;
		this.BlockBreakAllow.add(102);
		this.BlockBreakAllow.add(20);
		
		_scoreObj = GetScoreboard().registerNewObjective("Kills", "dummy");
		_scoreObj.setDisplaySlot(DisplaySlot.BELOW_NAME);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void GameStateChange(GameStateChangeEvent event)
	{
		if (event.GetState() != GameState.Live)
			return;
		
		for (Player player : GetPlayers(true))
		{
			player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(262, (byte)1, 1, F.item("Super Arrow")));
			player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 3f, 2f);
		}

		GetObjectiveSide().setDisplayName(C.cWhite + C.Bold + "First to " + C.cGold + C.Bold + "20 Kills");
	}
	
	@EventHandler
	public void BowShoot(EntityShootBowEvent event)
	{
		if (!(event.getProjectile() instanceof Arrow))
			return;
		
		Arrow arrow = (Arrow)event.getProjectile();
		
		if (arrow.getShooter() == null)
			return;
		
		if (!(arrow.getShooter() instanceof Player))
			return;
		
		if (!_deathTime.containsKey(arrow.getShooter()))
			return;
		
		if (UtilTime.elapsed(_deathTime.get(arrow.getShooter()), 1000))
			return;
		
		event.getProjectile().remove();
		
		final Player player = (Player)arrow.getShooter();
		
		Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(Manager.GetPlugin(), new Runnable()
		{
			public void run()
			{
				if (!player.getInventory().contains(Material.ARROW))
					player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(262, (byte)1, 1, F.item("Super Arrow")));
			}
		}, 10);
		
	}

	@EventHandler
	public void Death(CombatDeathEvent event)
	{
		if (event.GetEvent().getEntity() instanceof Player)
		{
			_deathTime.put((Player)event.GetEvent().getEntity(), System.currentTimeMillis());
		}
		
		if (event.GetLog().GetKiller() == null)
			return;

		if (!event.GetLog().GetKiller().IsPlayer())
			return;

		Player player = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
		if (player == null)	return;

		//New Arrow
		player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(262, (byte)1, 1, F.item("Super Arrow")));
		player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 3f, 2f);

		//Score
		AddKill(player);
	}

	@EventHandler
	public void ComboReset(CombatDeathEvent event)
	{
		if (!(event.GetEvent().getEntity() instanceof Player))
			return;
		
		Player player = (Player)event.GetEvent().getEntity();
		
		if (!_combo.containsKey(player))
			return;

		int combo = _combo.remove(player);

		int best = 0;
		if (_bestCombo.containsKey(player))
			best = _bestCombo.get(player);

		if (combo > best)
			_bestCombo.put(player, combo);
	}

	public void AddKill(Player player)
	{
		//Combo
		int combo = 1;
		if (_combo.containsKey(player))
			combo += _combo.get(player);

		_combo.put(player, combo);

		AnnounceCombo(player, combo);

		//Rank
		for (QuiverScore score : _ranks)
		{
			if (score.Player.equals(player))
			{
				score.Kills += 1;
				_scoreObj.getScore(player).setScore(score.Kills);
				EndCheck();
				return;
			}
		}

		_ranks.add(new QuiverScore(player, 1));
		_scoreObj.getScore(player).setScore(1);
	}

	private void AnnounceCombo(Player player, int combo) 
	{
		String killType = null;
		if (combo == 20)		killType = "PERFECT RUN";
		else if (combo == 13)	killType = "GODLIKE";
		else if (combo == 11)	killType = "UNSTOPPABLE";
		else if (combo == 9)	killType = "ULTRA KILL";
		else if (combo == 7)	killType = "MONSTER KILL";
		else if (combo == 5)	killType = "MEGA KILL";
		else if (combo == 3)	killType = "TRIPLE KILL";

		if (killType == null)
			return;

		//Announce
		for (Player other : UtilServer.getPlayers())
		{
			UtilPlayer.message(other, F.main("Game", C.cGreen + C.Bold + player.getName() + ChatColor.RESET + " got " + 
					F.elem(C.cAqua + C.Bold + killType +" (" + combo + " Kills)!")));
			other.playSound(other.getLocation(), Sound.ENDERDRAGON_GROWL, 1f + (combo/10f), 1f + (combo/10f));
		}
	}

	private void SortScores() 
	{
		for (int i=0 ; i<_ranks.size() ; i++)
		{
			for (int j=_ranks.size()-1 ; j>0 ; j--)
			{
				if (_ranks.get(j).Kills > _ranks.get(j-1).Kills)
				{
					QuiverScore temp = _ranks.get(j);
					_ranks.set(j, _ranks.get(j-1));
					_ranks.set(j-1, temp);
				}
			}
		}
	}

	@EventHandler
	public void ArrowDamage(CustomDamageEvent event)
	{
		if (event.GetProjectile() == null)
			return;

		event.AddMod("Projectile", "Instagib", 9001, false);
		event.SetKnockback(false);
		
		event.GetProjectile().remove();
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

		SortScores();

		//Write New
		for (QuiverScore score : _ranks)
		{
			String out = score.Kills + " " + C.cGreen + score.Player.getName();

			if (out.length() >= 16)
				out = out.substring(0, 15);

			_lastScoreboard.add(out);

			GetObjectiveSide().getScore(Bukkit.getOfflinePlayer(out)).setScore(score.Kills);
		}
	}

	@EventHandler
	public void PickupCancel(PlayerPickupItemEvent event)
	{
		event.setCancelled(true);
	}

	@Override
	public void EndCheck()
	{
		if (!IsLive())
			return;

		SortScores();

		if ((!_ranks.isEmpty() && _ranks.get(0).Kills >= 20) || GetPlayers(true).size() <= 1)
		{
			//Set Places
			_places.clear();
			for (int i=0 ; i<_ranks.size() ; i++)
				_places.add(i, _ranks.get(i).Player);

			//Award Gems
			if (_ranks.size() >= 1)
				AddGems(_ranks.get(0).Player, 20, "1st Place", false);

			if (_ranks.size() >= 2)
				AddGems(_ranks.get(1).Player, 15, "2nd Place", false);

			if (_ranks.size() >= 3)
				AddGems(_ranks.get(2).Player, 10, "3rd Place", false);

			//Combo Gems
			for (Player player : _bestCombo.keySet())
			{
				int combo = _bestCombo.get(player);
				
				if (combo >= 20)		AddGems(player, 40, "PERFECT - 20 Kill Combo", false);
				else if (combo >= 13)	AddGems(player, 24, "GODLIKE - 13 Kill Combo", false);
				else if (combo >= 11)	AddGems(player, 20, "UNSTOPPABLE - 11 Kill Combo", false);
				else if (combo >= 9)	AddGems(player, 16, "ULTRA KILL - 9 Kill Combo", false);
				else if (combo >= 7)	AddGems(player, 12, "MONSTER KILL - 7 Kill Combo", false);
				else if (combo >= 5)	AddGems(player, 8, "MEGA KILL - 5 Kill Combo", false);
				else if (combo >= 3)	AddGems(player, 4, "TRIPLE KILL - 3 Kill Combo", false);
			}
			
			//Participation
			for (Player player : GetPlayers(false))
				if (player.isOnline())
					AddGems(player, 10, "Participation", false);

			SetState(GameState.End);
			AnnounceEnd(_places);
		}
	}
}
