package nautilus.game.tutorial.action.types;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import nautilus.game.tutorial.action.Action;
import nautilus.game.tutorial.part.Part;

public class Dialogue extends Action
{
	public static int dialogueBase = 1200;
	public static int dialogueChar = 60;
	
	private String _sender = "Cat";
	private String _message = null;
	private String[] _messages = null;
	
	public static String[] restartMessages = new String[]
			{
		"Confused? Let's try again...",
		"Come on, you can do it!",
		"It's really not that difficult...",
		"Did you fall asleep..?"
			};
	
	public static String[] fishMessage = new String[]
			{
		"Eat the fish, so juicy sweeeeet!",
		"We're not going anywhere until you eat it.",
		"Look how deliciously fresh, and raw, it is.",
		"I can wait all day.",
		"You'll starve if you don't eat it..."
			};

	public Dialogue(Part part, String sender, String message) 
	{
		super(part, 0);
		_sender = sender;
		_message = message;
	}
	
	public Dialogue(Part part, String message) 
	{
		super(part, 0);
		_message = message;
	}
	
	public Dialogue(Part part, String[] messages) 
	{
		super(part, 0);
		_messages = messages;
	}

	@Override
	public void CustomAction(Player player)
	{
		//Set Message & Delay
		if (_messages != null || _message == null)
		{
			String previous = _message;
			
			_message = SelectMessage();
			
			while (_messages.length > 1 && _message.equals(previous))
				_message = SelectMessage();
		}
			

		//Set Delay
		SetDelay(dialogueBase + (dialogueChar * _message.length()));

		UtilPlayer.message(player, F.tute(_sender, _message));
		
		//Effect
		if (_sender.equals("System"))
			player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 2f);
		else
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
	}

	private String SelectMessage() 
	{
		if (_messages != null)
		{
			String message = _messages[UtilMath.r(_messages.length)];
			
			if (message != null)
				return message;
		}

		return "<Message Not Found>";
	}
}
