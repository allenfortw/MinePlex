package nautilus.game.tutorial.part.a_welcome;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import nautilus.game.tutorial.TutorialData;
import nautilus.game.tutorial.TutorialManager;
import nautilus.game.tutorial.action.types.Dialogue;
import nautilus.game.tutorial.action.types.ForceLook;
import nautilus.game.tutorial.action.types.Index;
import nautilus.game.tutorial.action.types.IndexJump;
import nautilus.game.tutorial.action.types.Pause;
import nautilus.game.tutorial.action.types.SoundEffect;
import nautilus.game.tutorial.action.types.Teleport;
import nautilus.game.tutorial.part.Part;

public class A01_Welcome extends Part
{	
	public A01_Welcome(TutorialManager manager, TutorialData data, Player player) 
	{
		super(manager, data, player);
	}

	@Override
	public void CreateActions() 
	{
		Add(new SoundEffect(this, Sound.ZOMBIE_UNFECT, 2f, 2f));		
		Add(new Teleport(this, Manager.spawnA));
		Add(new Pause(this, 6000));
		
		//Intro
		Add(new Dialogue(this, "Voice", "Hello?"));
		Add(new Pause(this, 2000));
		Add(new Dialogue(this, "Voice", "Ah, there you are."));
		Add(new Dialogue(this, "Voice", "Welcome to the " + F.te("Better MineCraft Tutorial") + "."));
		Add(new Dialogue(this, "Voice", "Here you will learn about Classes, Skills and Games."));
		
		Add(new Dialogue(this, "Voice", "My name is " + F.te("Catherine") + ", but you can call me " + F.te("Cat") + "."));
		Add(new SoundEffect(this, Sound.CAT_MEOW, 1f, 1f, 1000));
		Add(new Dialogue(this, "I promise to keep this short and fun!"));
		
		Add(new Dialogue(this, "For each tutorial you complete, you will be rewarded..."));
		Add(new Dialogue(this, F.te(C.cAqua + "400 Credits") + " to unlock " + F.te("Skills") + " and " + F.te("Items") + "!"));
		Add(new Dialogue(this, "I know, I am very generous... no need to say thank you."));
		
		//Wool Punch 
		Add(new Index(this, "Wool Punch"));
		
		Add(new ForceLook(this, Manager.redWool, 0));
		Add(new Dialogue(this, "Punch the " + F.te(C.cRed + "Red Wool") + " if you don't need tutoring."));
		
		Add(new ForceLook(this, Manager.greenWool, 0));
		Add(new Dialogue(this, "Punch the " + F.te(C.cGreen + "Green Wool") + " to get started!"));
		
		Add(new Pause(this, 20000));
		
		Add(new Dialogue(this, Dialogue.restartMessages));
		Add(new IndexJump(this, "Wool Punch"));
	}
	
	@EventHandler
	public void BlockClick(PlayerInteractEvent event)
	{
		if (event.getClickedBlock() == null)
			return;
		
		if (event.getClickedBlock().getType() != Material.WOOL)
			return;
		
		if (!event.getPlayer().getName().equals(GetPlayerName()))
			return;
		
		if (event.getClickedBlock().getData() == 5)
		{
			SetCompleted(true);
			event.setCancelled(true);
		}
			
		else if (event.getClickedBlock().getData() == 14)
		{
			UtilPlayer.kick(event.getPlayer(), "Cat", "Thanks for nothing...");
			event.setCancelled(true);
		}			
	}

	@Override
	public Part GetNext() 
	{
		return new A02_Food(Manager, Data, GetPlayer());
	}
}
