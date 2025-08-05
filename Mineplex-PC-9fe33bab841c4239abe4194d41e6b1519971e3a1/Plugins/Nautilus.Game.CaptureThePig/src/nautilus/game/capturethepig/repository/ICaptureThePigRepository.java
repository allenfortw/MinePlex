package nautilus.game.capturethepig.repository;

import java.util.List;

import mineplex.core.server.IRepository;
import mineplex.core.server.util.Callback;
import mineplex.minecraft.donation.repository.token.PlayerUpdateToken;
import nautilus.game.capturethepig.stats.CaptureThePigGameStatsToken;


public interface ICaptureThePigRepository extends IRepository 
{
	void SaveGameStats(Callback<List<PlayerUpdateToken>> callback, CaptureThePigGameStatsToken captureThePigGameStats);
}
