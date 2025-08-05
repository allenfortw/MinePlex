package mineplex.enjinTranslator;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.reflect.TypeToken;

import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.donation.DonationManager;
import mineplex.core.punish.Category;
import mineplex.core.punish.Punish;
import mineplex.core.server.remotecall.JsonWebCall;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;

public class Enjin extends MiniPlugin implements CommandExecutor
{
	private CoreClientManager _clientManager;
	private DonationManager _donationManager;
	private Punish _punish;
	
	public long _lastPoll = System.currentTimeMillis() - 120000;
	
	public Enjin(JavaPlugin plugin, CoreClientManager clientManager, DonationManager donationManager, Punish punish)
	{
		super("Enjin", plugin);
		
		_clientManager = clientManager;
		_donationManager = donationManager;
		_punish = punish;
		
		plugin.getCommand("enjin_mineplex").setExecutor(this);
		plugin.getCommand("pull").setExecutor(this);
	}
	
	@EventHandler
	public void pollLastPurchases(UpdateEvent event)
	{
		if (event.getType() != UpdateType.MIN_01)
			return;
		
		//@SuppressWarnings("serial")
		//List<EnjinPurchase> purchases = new JsonWebCall("http://www.mineplex.com/api/m-shopping-purchases/m/14702725").Execute(new TypeToken<List<EnjinPurchase>>(){}.getType(), null);
		//_lastPoll = System.currentTimeMillis();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (sender instanceof Player)
			((Player)sender).kickPlayer("Like bananas? I don't.  Here take these and go have fun.");

		if (label.equalsIgnoreCase("enjin_mineplex"))
		{
			System.out.println("enjin command");
			
			if (args.length == 3 && args[0].equalsIgnoreCase("gem"))
			{			
				String name = args[1];
				int amount = Integer.parseInt(args[2]);
				
				_donationManager.RewardGems(name, amount);
				System.out.println("enjin gem");
			}		
			else if (args.length == 4 && args[0].equalsIgnoreCase("rank"))
			{
				String name = args[1];
				String rank = args[2];
				boolean perm = Boolean.parseBoolean(args[3]);
				
				_clientManager.SaveRank(name, mineplex.core.common.Rank.valueOf(rank), perm);
				System.out.println("enjin rank");
			}
			else if (args.length >= 3 && args[0].equalsIgnoreCase("purchase"))
			{
				String name = args[1];
				
				String packageName = args[2];
				
				for (int i = 3; i < args.length; i++)
				{
					packageName += " " + args[i];
				}
				
				_donationManager.PurchaseUnknownSalesPackage(null, name, packageName, 0, false);
				System.out.println("enjin purchase");
			}
			else if (args.length >= 3 && args[0].equalsIgnoreCase("unban"))
			{
				String name = args[1];				
				String reason = args[2];
				
				for (int i = 3; i < args.length; i++)
				{
					reason += " " + args[i];
				}
				
				_punish.RemoveBan(name, reason);
				System.out.println("enjin unban");
			}
			else if (args.length >= 3 && args[0].equalsIgnoreCase("ban"))
			{
				String name = args[1];				
				String reason = args[2];
				
				for (int i = 3; i < args.length; i++)
				{
					reason += " " + args[i];
				}
				
				_punish.AddPunishment(name, Category.Other, reason, null, 3, true, -1);
				System.out.println("enjin ban");
			}
		}
		
		return true;
	}
}
