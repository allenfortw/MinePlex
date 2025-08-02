package nautilus.game.tutorial.action;

import org.bukkit.entity.Player;

import nautilus.game.tutorial.part.Part;

public abstract class Action 
{
	protected Part Part;

	private long _delay;

	public Action(Part part, long delay)
	{
		Part = part;
		_delay = delay;
	}

	public void DoAction(Player player)
	{
		CustomAction(player);
		
		Part.SetNextAction(System.currentTimeMillis() + _delay);
		Part.IncrementIndex();
	}
	
	public abstract void CustomAction(Player player);
	
	public void SetDelay(long newDelay)
	{
		_delay = newDelay;
	}
	
	public long GetDelay()
	{
		return _delay;
	}
}
