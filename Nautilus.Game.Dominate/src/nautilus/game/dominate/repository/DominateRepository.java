package nautilus.game.dominate.repository;

import java.util.List;

import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;

import mineplex.core.common.util.Callback;
import mineplex.core.donation.repository.token.PlayerUpdateToken;
import mineplex.core.server.remotecall.JsonWebCall;
import nautilus.game.dominate.stats.DominateGameStatsToken;

public class DominateRepository
{
	private String _webServerAdddress;
	
    public DominateRepository(String webServerAddress)
    {
		_webServerAdddress = webServerAddress;
    }
    
    public void SaveGameStats(final Callback<List<PlayerUpdateToken>> callback, final DominateGameStatsToken dominationGameStats)
    {
        Thread asyncThread = new Thread(new Runnable()
        {
            public void run()
            {
                List<PlayerUpdateToken> tokenList = new JsonWebCall(_webServerAdddress + "Dominate/UploadStats").Execute(new TypeToken<List<PlayerUpdateToken>>(){}.getType(), dominationGameStats);
                callback.run(tokenList);
            }
        });
        
        asyncThread.start();
    }
}