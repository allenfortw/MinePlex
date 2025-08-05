package mineplex.enjinTranslator;

import mineplex.core.account.CoreClientManager;
import mineplex.core.command.CommandCenter;
import mineplex.core.donation.DonationManager;
import mineplex.core.punish.Punish;
import mineplex.core.updater.Updater;

import org.bukkit.plugin.java.JavaPlugin;

public class EnjinTranslator extends JavaPlugin
{
	private String WEB_CONFIG = "webServer";

	@Override
	public void onEnable()
	{
		getConfig().addDefault(WEB_CONFIG, "http://api.mineplex.com/");
		getConfig().set(WEB_CONFIG, getConfig().getString(WEB_CONFIG));
		saveConfig();

		//Core Modules
		CoreClientManager clientManager = CoreClientManager.Initialize(this, GetWebServerAddress());
		DonationManager donationManager = new DonationManager(this, GetWebServerAddress());
		
		//Static Modules
		CommandCenter.Initialize(this, clientManager);
		
		//Other Modules
		Punish punish = new Punish(this, GetWebServerAddress());
		
		//Main Modules
		new Enjin(this, clientManager, donationManager, punish);
		
		new Updater(this);
	}
	
	public String GetWebServerAddress()
	{
		String webServerAddress = getConfig().getString(WEB_CONFIG);

		return webServerAddress;
	}
}
