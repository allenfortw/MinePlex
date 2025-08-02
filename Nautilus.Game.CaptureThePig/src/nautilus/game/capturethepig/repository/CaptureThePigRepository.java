package nautilus.game.capturethepig.repository;

import java.util.List;

import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;

import mineplex.core.server.RemoteRepository;
import mineplex.core.server.remotecall.JsonWebCall;
import mineplex.core.server.util.Callback;
import mineplex.minecraft.donation.repository.token.PlayerUpdateToken;
import nautilus.game.capturethepig.stats.CaptureThePigGameStatsToken;

public class CaptureThePigRepository extends RemoteRepository implements ICaptureThePigRepository 
{
	public CaptureThePigRepository(String webServerAddress) 
	{
		super(webServerAddress);
	}

	@Override
    public void SaveGameStats(final Callback<List<PlayerUpdateToken>> callback, final CaptureThePigGameStatsToken captureThePigGameStats)
    {
        Thread asyncThread = new Thread(new Runnable()
        {
            public void run()
            {
                List<PlayerUpdateToken> tokenList = new JsonWebCall(WebServerAddress + "CaptureThePig/UploadStats").Execute(new TypeToken<List<PlayerUpdateToken>>(){}.getType(), captureThePigGameStats);
                callback.run(tokenList);
            }
        });
        
        asyncThread.start();
    }
}
