package me.chiss.Core.Modules;

import java.util.ArrayList;
import java.util.HashSet;

import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.itemstack.ItemStackFactory;
import me.chiss.Core.Module.AModule;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Firework extends AModule
{
	private HashSet<Color> _colors;

	public Firework(JavaPlugin plugin) 
	{
		super("Firework", plugin);
	}

	//Module Functions
	@Override
	public void enable() 
	{
		_colors = new HashSet<Color>();
		
		_colors.add(Color.AQUA);
		_colors.add(Color.BLACK);
		_colors.add(Color.BLUE);
		_colors.add(Color.FUCHSIA);
		_colors.add(Color.GRAY);
		_colors.add(Color.GREEN);
		_colors.add(Color.LIME);
		_colors.add(Color.MAROON);
		_colors.add(Color.NAVY);
		_colors.add(Color.OLIVE);
		_colors.add(Color.ORANGE);
		_colors.add(Color.PURPLE);
		_colors.add(Color.RED);
		_colors.add(Color.SILVER);
		_colors.add(Color.TEAL);
		_colors.add(Color.WHITE);
		_colors.add(Color.YELLOW);
	}

	@Override
	public void disable() 
	{

	}

	@Override
	public void config() 
	{

	}

	@Override
	public void commands() 
	{
		AddCommand("fw");
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		if (!Clients().Get(caller).Rank().Has(Rank.ADMIN, true))
			return;
		
		if (args.length >= 1 && args[0].equalsIgnoreCase("type"))
		{
			for (Type type : Type.values())
				caller.sendMessage(type.toString());
			return;
		}
		
		if (args.length >= 1 && args[0].equalsIgnoreCase("color"))
		{
			for (Color color : _colors)
				caller.sendMessage(color.toString());
			return;
		}

		if (args.length < 4)
		{
			caller.sendMessage(F.link("/fw <Type> <Color,Color..> <Power> (Flicker,Trail)"));
			caller.sendMessage(F.link("/fw type") + " for Type List.");
			caller.sendMessage(F.link("/fw color") + " for Color List.");
			return;
		}

		FireworkEffect fe = BuildFirework(caller, args[0], args[1], args[3]);
		
		//Power
		int power = 2;
		try
		{
			power = Integer.parseInt(args[2]);
			caller.sendMessage(F.value("Power", power+""));
		}
		catch (Exception e)
		{
			caller.sendMessage(F.value("Invalid Power", args[2]));
		}
		
		//Make Firework
		ItemStack firework = ItemStackFactory.Instance.CreateStack(401, 64);
		FireworkMeta fm = (FireworkMeta)firework.getItemMeta();
		fm.addEffect(fe);
		fm.setPower(power);
		firework.setItemMeta(fm);
		caller.setItemInHand(firework);
	}

	public FireworkEffect BuildFirework(Player caller, String typeString, String colorString, String specialString)
	{	
		//Type
		Type type = Type.BALL;
		for (Type cur : Type.values())
			if (typeString.equalsIgnoreCase(cur.toString()))
			{
				caller.sendMessage(F.value("Type", type.toString()));
				type = cur;
				break;
			}

		//Colors
		ArrayList<Color> colors = new ArrayList<Color>();
		colorString = colorString.toLowerCase();
		for (String colorToken : colorString.split(","))
		{
			ArrayList<Color> matchList = new ArrayList<Color>();
			Color match = null;
			
			for (Color cur : _colors)
			{
				if (cur.toString().toLowerCase().equals(colorToken))
				{
					match = cur;
					break;
				}
				
				else if (cur.toString().toLowerCase().contains(colorToken))
				{
					matchList.add(cur);
				}
			}		
			
			if (match != null)
			{
				caller.sendMessage(F.value("Added Color", match.toString()));
				colors.add(match);
			}
			else if (!matchList.isEmpty())
			{
				if (matchList.size() == 1)
				{
					caller.sendMessage(F.value("Added Color", matchList.get(0).toString()));
					colors.add(matchList.get(0));
				}
				else
				{
					caller.sendMessage(F.value("Multiple Color Matches", colorToken));
				}
			}
			else
			{
				caller.sendMessage(F.value("No Color Matches", colorToken));
			}
		}
		if (colors.isEmpty())
			colors.add(Color.WHITE);
		
		boolean flicker = false;
		boolean trail = false;
		
		if (specialString.toLowerCase().contains("flicker") ||
				specialString.toLowerCase().contains("flick") ||
				specialString.toLowerCase().contains("f"))
			flicker = true;
		
		if (specialString.toLowerCase().contains("trail") ||
				specialString.toLowerCase().contains("t"))
			flicker = true;
			
		return FireworkEffect.builder().flicker(flicker).trail(trail)
				.withColor(colors).withFade(colors).with(type).build();
	}

}
