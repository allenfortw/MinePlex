package nautilus.game.tutorial.action.types;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import nautilus.game.tutorial.action.Action;
import nautilus.game.tutorial.part.Part;

public class SoundEffect extends Action
{
	private Sound _sound;
	private float _volume;
	private float _pitch;
	
	public SoundEffect(Part part, Sound sound, float volume, float pitch) 
	{
		super(part, 0);
		
		_sound = sound;
		_volume = volume;
		_pitch = pitch;
	}
	
	public SoundEffect(Part part, Sound sound, float volume, float pitch, long delay) 
	{
		super(part, delay);
		
		_sound = sound;
		_volume = volume;
		_pitch = pitch;
	}

	@Override
	public void CustomAction(Player player)
	{
		player.playSound(player.getLocation(), _sound, _volume, _pitch);
	}
}
