package nautilus.game.arcade.managers;

import java.util.HashMap;

import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.minecraft.game.core.combat.CombatComponent;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import nautilus.game.arcade.ArcadeFormat;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.events.PlayerStateChangeEvent;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GemData;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam.PlayerState;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameGemManager implements Listener
{
	ArcadeManager Manager;

	public GameGemManager(ArcadeManager manager)
	{
		Manager = manager;
		
		Manager.GetPluginManager().registerEvents(this, Manager.GetPlugin());
	}

	@EventHandler
	public void PlayerKillAward(CombatDeathEvent event)
	{
		Game game = Manager.GetGame();
		if (game == null)	return;
		
		if (!(event.GetEvent().getEntity() instanceof Player))
			return;
		
		Player killed = (Player)event.GetEvent().getEntity();

		if (event.GetLog().GetKiller() != null)
		{
			Player killer = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());

			if (killer != null && !killer.equals(killed))
			{
				//Kill
				game.AddGems(killer, game.GetKillsGems(killer, killed, false), "Kills", true);

				//First Kill
				if (game.FirstKill)
				{
					game.AddGems(killer, 10, "First Blood", false);

					game.FirstKill = false;

					game.Announce(F.main("Game", Manager.GetColor(killer) + killer.getName() + " drew first blood!"));
				}
			}
		}

		for (CombatComponent log : event.GetLog().GetAttackers())
		{
			if (event.GetLog().GetKiller() != null && log.equals(event.GetLog().GetKiller()))
				continue;

			Player assist = UtilPlayer.searchExact(log.GetName());

			//Assist
			if (assist != null)
				game.AddGems(assist, game.GetKillsGems(assist, killed, true), "Kill Assists", true);
		}
	}
	
	@EventHandler
	public void PlayerQuit(PlayerQuitEvent event)
	{
		Game game = Manager.GetGame();
		if (game == null)	return;

		RewardGems(game, event.getPlayer(), true);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void PlayerStateChange(PlayerStateChangeEvent event)
	{
		if (event.GetState() != PlayerState.OUT)
			return;
		
		RewardGems(event.GetGame(), event.GetPlayer(), false);
	}
	
	@EventHandler
	public void GameStateChange(GameStateChangeEvent event)
	{
		if (event.GetState() != GameState.Dead)
			return;
		
		for (Player player : UtilServer.getPlayers())
			RewardGems(event.GetGame(), player, true);
	}

	public void RewardGems(Game game, Player player, boolean give)
	{
		//Inform Gems
		AnnounceGems(game, player, game.GetPlayerGems().get(player), give);
		
		//Give Gems
		if (give)
			GiveGems(player, game.GetPlayerGems().remove(player));
	}
	
	public void GiveGems(Player player, HashMap<String,GemData> gems)
	{
		if (gems == null)	
			return;
		
		int total = 0;

		for (GemData data : gems.values())
			total += (int)data.Gems;

		if (total <= 0)
			total = 1;

		if (Manager.GetClients().Get(player).GetRank().Has(Rank.ULTRA) || Manager.GetDonation().Get(player.getName()).OwnsUnknownPackage(Manager.GetServerConfig().ServerType + " ULTRA"))
			total = total * 2;

		Manager.GetDonation().RewardGems(player.getName(), total);
	}

	public void AnnounceGems(Game game, Player player, HashMap<String,GemData> gems, boolean give)
	{
		if (gems == null)	
			return;
		
		player.playSound(player.getLocation(), Sound.LEVEL_UP, 2f, 1f);

		UtilPlayer.message(player, "");
		UtilPlayer.message(player, ArcadeFormat.Line);

		UtilPlayer.message(player, "§aGame - §f§l" + game.GetName());
		UtilPlayer.message(player, "");

		int earnedGems = 0;

		for (String type : gems.keySet())
		{
			int gemCount = (int)gems.get(type).Gems;
			if (gemCount <= 0)
				gemCount = 1;

			earnedGems += gemCount;

			int amount = gems.get(type).Amount;
			String amountStr = "";
			if (amount > 0)
				amountStr = amount + " ";

			UtilPlayer.message(player, F.elem(C.cGreen + "+" + gemCount + " Gems") + " for " + F.elem(amountStr + type));
		}

		if (Manager.GetClients().Get(player).GetRank().Has(Rank.ULTRA) || Manager.GetDonation().Get(player.getName()).OwnsUnknownPackage(Manager.GetServerConfig().ServerType + " ULTRA"))
		{
			UtilPlayer.message(player, F.elem(C.cGreen + "+" + earnedGems + " Gems") + " for " + F.elem(C.cAqua + "Ultra Rank 2x Gems"));
			earnedGems = earnedGems * 2;
		}
			
		UtilPlayer.message(player, "");
		if (give)
		{
			UtilPlayer.message(player, F.elem(C.cWhite + "§lYou now have " + 
					C.cGreen + C.Bold + (Manager.GetDonation().Get(player.getName()).GetGems() + earnedGems) + " Gems"));
		}
		else
		{
			UtilPlayer.message(player, F.elem(C.cWhite + "§lGame is still in progress..."));
			UtilPlayer.message(player, F.elem(C.cWhite + "§lYou may earn more " + C.cGreen + C.Bold + "Gems" + C.cWhite + C.Bold + " when its completed."));
		}
		
		
		UtilPlayer.message(player, ArcadeFormat.Line);	
	}
	
	public double GetScale(Game game)
	{
		return 0.25 + (0.75 * ((double)game.GetPlayerCountAtStart() / (double)Manager.GetPlayerFull()));
	}
}
