package nautilus.game.tutorial.action.types;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import mineplex.core.common.util.UtilAlg;
import nautilus.game.tutorial.action.Action;
import nautilus.game.tutorial.part.Part;

public class ForceLook extends Action
{
	private Location _target;
	private int _ticks = 20;
	
	public ForceLook(Part part, Location target, long delay) 
	{
		super(part, delay);
		
		_target = target;
	}

	@Override
	public void DoAction(Player player)
	{
		CustomAction(player);
		
		_ticks--;
		
		if (_ticks > 0)
			return;
		
		Part.SetNextAction(System.currentTimeMillis() + GetDelay());
		Part.IncrementIndex();
		
		_ticks = 20;
	}
	
	@Override
	public void CustomAction(Player player)
	{
        Vector vector = UtilAlg.getTrajectory(player.getEyeLocation(), _target).normalize();

        double x = vector.getX();
        double y = vector.getY();
		double z = vector.getZ();
		double xz = Math.sqrt((x*x) + (z*z));
		
		double yaw = Math.toDegrees(Math.atan((-x)/z));
		if (z < 0)			yaw += 180;
		
		double pitch = Math.toDegrees(Math.atan(xz/y));
		if (y <= 0)			pitch += 90;
		else				pitch -= 90;
		
		Location loc = player.getLocation();
        loc.setYaw((float) yaw);
        loc.setPitch((float) pitch);
        
        player.teleport(loc);
	}
}
