package nautilus.game.core.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nautilus.game.core.game.IGame;

public class GameScheduler<GameType extends IGame<?,?>>
{
	private List<GameType> _schedule;
	
	public GameScheduler()
	{
		_schedule = new ArrayList<GameType>();
	}
	
	public List<GameType> GetGames()
	{	
		return _schedule;
	}
	
	public boolean ContainsGame(GameType game)
	{
		return _schedule.contains(game);
	}
	
	public GameType ScheduleNewGame(GameType game)
	{		
		_schedule.add(game);
		
		return game;
	}
	
	public void RemoveGame(GameType game)
	{
		_schedule.remove(game);
	}
	
	public void CleanEmptyGames()
	{
	    Iterator<GameType> gameIterator = _schedule.iterator();
	    
	    while (gameIterator.hasNext())
	    {
	        GameType game = gameIterator.next();
			
			if (game.GetPlayers().isEmpty())
			{
				game.Deactivate();
			    gameIterator.remove();
			}
		}
	}
}
