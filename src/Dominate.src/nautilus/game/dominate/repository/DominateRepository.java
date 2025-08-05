package nautilus.game.dominate.repository;

import java.util.List;
import mineplex.core.common.util.Callback;
import mineplex.core.donation.repository.token.GemRewardToken;
import mineplex.core.server.remotecall.JsonWebCall;
import nautilus.game.dominate.stats.DominateGameStatsToken;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;



public class DominateRepository
{
  private String _webServerAdddress;
  
  public DominateRepository(String webServerAddress)
  {
    this._webServerAdddress = webServerAddress;
  }
  
  public void SaveGameStats(final Callback<List<GemRewardToken>> callback, final DominateGameStatsToken dominationGameStats)
  {
    Thread asyncThread = new Thread(new Runnable()
    {
      public void run()
      {
        List<GemRewardToken> tokenList = (List)new JsonWebCall(DominateRepository.this._webServerAdddress + "Dominate/UploadStats").Execute(new TypeToken() {}.getType(), dominationGameStats);
        callback.run(tokenList);
      }
      
    });
    asyncThread.start();
  }
}
