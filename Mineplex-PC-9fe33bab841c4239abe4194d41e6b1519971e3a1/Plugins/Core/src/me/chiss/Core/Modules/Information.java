package me.chiss.Core.Modules;

import java.util.LinkedList;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.chiss.Core.Module.AModule;
import mineplex.core.account.CoreClient;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;

public class Information extends AModule 
{
	public Information(JavaPlugin plugin) 
	{
		super("Client Information", plugin);

		AddCommand("x");
		AddCommand("info");
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		if (args.length == 0 || args[0].equals("help"))
		{
			help(caller);
			return;
		}

		Handle(caller, args);
	}

	private void help(Player caller) 
	{

	}

	public void Handle(Player caller, String[] args)
	{
		//Get Client
		CoreClient target = null;
		if (args.length >= 2)
		{
			Player other = UtilPlayer.searchOnline(caller, args[1], true);
			if (other == null)		
			{
				UtilPlayer.message(caller, C.cDGray + "-- Offline Player Data Coming Soon --");
				return;
			}

			target = Clients().Get(other);
		}
		else
		{
			target = Clients().Get(caller);
		}

		if (target == null)
			return;

		//Element
		if (args[0].equals("acc") || args[0].equals("account"))			
			Account(caller, target);

		else if (args[0].equals("acc"))			
			Account(caller, target);

		else if (args[0].equals("nac"))			
			NAC(caller, target);

		else if (args[0].equals("ban"))			
			Ban(caller, target);

		else if (args[0].equals("mute"))			
			Mute(caller, target);

		else if (args[0].equals("alias"))			
			Alias(caller, target);

		else if (args[0].equals("clan"))			
			Clan(caller, target);

		else if (args[0].equals("ignore"))			
			Ignore(caller, target);
	}

	public void Account(Player caller, CoreClient target)
	{
		CoreClient self = Clients().Get(caller);
		LinkedList<String> out = new LinkedList<String>();

		//Header
		out.add(F.main(GetName(), "Account Information - " + target.GetPlayerName()));

		//Public
		out.add(F.value("Play Time", UtilTime.convertString(target.Acc().GetPlayTime(), 1, TimeUnit.FIT)));
		out.add(F.value("Play Count", target.Acc().GetLoginCount()+""));
		out.add(F.value("Last Login", UtilTime.convertString(System.currentTimeMillis() - target.Acc().GetLoginLast(), 1, TimeUnit.FIT) + " Ago"));

		//IP Alias > Mod
		if (self.Rank().Has(Rank.MODERATOR, false))
		{
			out.add(F.value("IP Count", target.Acc().GetListIP().size() + ""));

			String alias = "";

			for (String cur :target.Acc().GetAliasIP())
				alias += cur + " ";

			if (alias.length() == 0)
				alias = "None";

			out.add(F.value("IP Alias", alias));
		}

		//MAC Alias > Admin
		if (self.Rank().Has(Rank.ADMIN, false))
		{
			out.add(F.value("MAC Count", target.Acc().GetListMAC().size() + ""));

			String alias = "";

			for (String cur : target.Acc().GetAliasMAC())
				alias += cur + " ";

			if (alias.length() == 0)
				alias = "None";

			out.add(F.value("MAC Alias", alias));
		}

		//Send
		UtilPlayer.message(caller, out);
	}

	public void NAC(Player caller, CoreClient target)
	{
		CoreClient self = Clients().Get(caller);
		LinkedList<String> out = new LinkedList<String>();

		//Header
		out.add(F.main(GetName(), "NAC Information - " + target.GetPlayerName()));

		//Requirement
		if (target.NAC().IsRequired())
		{
			if (target.NAC().GetRequired())
			{
				out.add(F.value("Required", "True", true));
				out.add(F.value(1, "Date", target.NAC().GetDate()));
				out.add(F.value(1, "Admin", target.NAC().GetAdmin()));
				out.add(F.value(1, "Reason", target.NAC().GetReason()));
			}
			else
			{
				out.add(F.value("Required", "True", true));
				out.add(F.value(1, "Time Played", UtilTime.convertString(target.Acc().GetPlayTime(), 1, TimeUnit.FIT)));
			}
		}
		else
		{
			out.add(F.value("Required", "False", false));	
			out.add(F.value(1, "Time Played", UtilTime.convertString(target.Acc().GetPlayTime(), 1, TimeUnit.FIT)));
			out.add(F.value(1, "Required In", UtilTime.convertString(target.NAC().GetRequireTime() - target.Acc().GetPlayTime(), 1, TimeUnit.FIT)));
		}

		if (!UtilPlayer.isOnline(target.GetPlayerName()))
			return;

		//Usage
		if (target.NAC().IsUsing())
		{
			out.add(F.value("Using CAH", "True", true));
			out.add(F.value(1, "Build", target.NAC().GetPack()));

			if (self.Rank().Has(Rank.ADMIN, false))
				out.add(F.value(1, "Token", target.NAC().GetToken()));
		}
		else
		{
			out.add(F.value("Using CAH", "False", false));
			if (self.Rank().Has(Rank.ADMIN, false))
			{		
				out.add(F.value(1, "Build", target.NAC().GetPack()));
				out.add(F.value(1, "Token", target.NAC().GetToken()));
			}
		}

		//Send
		UtilPlayer.message(caller, out);
	}

	public void Ban(Player caller, CoreClient target)
	{
		LinkedList<String> out = new LinkedList<String>();

		//Header
		out.add(F.main(GetName(), "Ban Information - " + target.GetPlayerName()));

		if (target.Ban().GetBan() != null)
		{
			out.add(F.value(1, "Banned", F.tf(true)));
			
			out.add(F.value(1, "Date", UtilTime.when(target.Ban().GetBan().GetBanTime())));
			out.add(F.value(1, "Duration", UtilTime.convertString(target.Ban().GetBan().GetBanDuration(), 1, TimeUnit.FIT)));

			if (target.Ban().GetBan().GetBanDuration() > 0)		
				out.add(F.value(1, "Remaining", target.Ban().GetBan().RemainingString()));

			out.add(F.value(1, "Admin", target.Ban().GetBan().GetAdmin()));
			out.add(F.value(1, "Rank", target.Ban().GetBan().GetRank().toString()));
			out.add(F.value(1, "Reason", target.Ban().GetBan().GetReason()));	

			if (target.Ban().GetBan().IsAliasBan())		out.add(F.value(1, "Alias Ban", "True", true));	
			else										out.add(F.value(1, "Alias Ban", "False", false));	

			if (!target.Ban().GetBan().Active())		out.add(F.value(1, "Unbanned", "True", true));	
			else										out.add(F.value(1, "Unbanned", "False", false));	
		}
		else
		{
			out.add(F.value(1, "Banned", F.tf(false)));
		}
		
		//Send
		UtilPlayer.message(caller, out);
	}

	public void Mute(Player caller, CoreClient target)
	{
		LinkedList<String> out = new LinkedList<String>();

		//Header
		out.add(F.main(GetName(), "Mute Information - " + target.GetPlayerName()));

		//Send
		UtilPlayer.message(caller, out);
	}

	public void Alias(Player caller, CoreClient target)
	{
		LinkedList<String> out = new LinkedList<String>();

		//Header
		out.add(F.main(GetName(), "Alias Information - " + target.GetPlayerName()));

		//Send
		UtilPlayer.message(caller, out);
	}

	public void Clan(Player caller, CoreClient target)
	{
		LinkedList<String> out = new LinkedList<String>();

		//Header
		out.add(F.main(GetName(), "Clan Information - " + target.GetPlayerName()));

		if (target.Clan().GetClanName().equals(""))
		{
			out.add(F.value("Clan", "No Clan", false));
		}
		else
		{
			out.add(F.value("Clan", target.Clan().GetClanName()));
			out.add(F.value("Invited By", target.Clan().GetInviter()));
		}

		if (target.Clan().CanJoin())
			out.add(F.value("Leave Timer", "No Timer", true));
		else
			out.add(F.value("Leave Timer", UtilTime.convertString(System.currentTimeMillis() - target.Clan().GetDelay(), 1, TimeUnit.FIT), false));

		//Send
		UtilPlayer.message(caller, out);
	}

	public void Ignore(Player caller, CoreClient target)
	{
		LinkedList<String> out = new LinkedList<String>();

		//Header
		out.add(F.main(GetName(), "Ignore Information - " + target.GetPlayerName()));

		String ignoreString = "";

		for (String cur : UtilAlg.sortKey(target.Ignore().GetIgnored()))
			ignoreString += cur + " ";

		if (ignoreString.length() == 0)
			ignoreString = "Empty";

		out.add(F.value("Ignore List", ignoreString));

		//Send
		UtilPlayer.message(caller, out);
	}
}
