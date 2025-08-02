package mineplex.minecraft.game.classcombat.Class;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.F;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;

public class ClassRestore extends MiniPlugin
{
	private ClassManager _classManager;
	private SkillFactory _skillFactory;
	private boolean _active = false;
	private String _folder = "data/classbuilds/";
	
	private HashMap<String, HashMap<IPvpClass, Collection<ISkill>>> _skills = new HashMap<String, HashMap<IPvpClass, Collection<ISkill>>>();
	
	public ClassRestore(JavaPlugin plugin, ClassManager classManager, SkillFactory skillFactory) 
	{
		super("Class Restore", plugin);
		
		_classManager = classManager;
		_skillFactory = skillFactory;
		
		File file = new File(_folder);
		file.mkdirs();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void Join(AsyncPlayerPreLoginEvent event)
	{
		if (!_active)
			return;
		
		if (event.getLoginResult() != Result.ALLOWED)
			return;
		
		ReadBuilds(event.getName());
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent event)
	{
		if (!_active)
			return;
		
		WriteBuilds(event.getPlayer().getName());
		_skills.remove(event.getPlayer().getName());
	}

	public Collection<ISkill> GetBuild(String name, IPvpClass gameClass) 
	{
		if (!_active)
			return null;
		
		if (!_skills.containsKey(name))
			return null;
		
		if (!_skills.get(name).containsKey(gameClass))
			return null;
		
		return _skills.get(name).get(gameClass);
	}
	
	public void SaveBuild(String name, IPvpClass gameClass, Collection<ISkill> skills)
	{
		if (!_active)
			return;
		
		if (!_skills.containsKey(name))
			_skills.put(name, new HashMap<IPvpClass, Collection<ISkill>>());
		
		//Replace Old
		_skills.get(name).put(gameClass, new ArrayList<ISkill>());

		//Insert Skills
		for (ISkill skill : skills)
			_skills.get(name).get(gameClass).add(skill);
	}
	
	public boolean IsActive()
	{
		return _active;
	}
	
	public void Activate()
	{
		_active = true;
	}
	
	/*  FORMAT
	 * 	
	 *	Assassin 5 Bulls Charge
	 * 	
	 */
	
	public void WriteBuilds(String player)
	{
		if (!_skills.containsKey(player))
			return;

		System.out.println("Writing Build: " + player);
		
		FileWriter fstream = null;
		BufferedWriter out = null;
		
		try
		{
			fstream = new FileWriter(_folder + player + ".dat");
			out = new BufferedWriter(fstream);

			HashMap<IPvpClass, Collection<ISkill>> builds = _skills.get(player);
			
			for (IPvpClass gameClass : builds.keySet())
				for (ISkill skill : builds.get(gameClass))
					out.write(gameClass.GetName() + " " + skill.GetName() + "\n");
		}
		catch (Exception e)
		{
			System.err.println("Build Write Error: " + e.getMessage());
		}
		finally
		{
			if (out != null)
			{
				try
				{
					out.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			if (fstream != null)
			{
				try
				{
					fstream.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void ReadBuilds(String player)
	{
		System.out.println("Reading Build: " + player);
		
		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader br = null;
		
		try
		{
			File f = new File(_folder + player + ".dat");

			if (!f.exists())
				return;
			
			if (!_skills.containsKey(player))
				_skills.put(player, new HashMap<IPvpClass, Collection<ISkill>>());
			
			fstream = new FileInputStream(f);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			while ((strLine = br.readLine()) != null)  
			{
				String[] tokens = strLine.split(" ");
				
				if (tokens.length < 3)
					continue;

				try
				{
					//Class
					IPvpClass gameClass = _classManager.GetClass(tokens[0]);
				
					if (!_skills.get(player).containsKey(gameClass))
						_skills.get(player).put(gameClass, new ArrayList<ISkill>());
					
					ISkill skill = _skillFactory.GetSkill(F.combine(tokens, 2, null, false));
					
					_skills.get(player).get(gameClass).add(skill);
				}
				catch (Exception e)
				{
					System.err.println("Build Parse Error: " + e.getMessage());
				}
			}
		}
		catch (Exception e)
		{
			System.err.println("Build Read Error: " + e.getMessage());
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			if (in != null)
			{
				try
				{
					in.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			if (fstream != null)
			{
				try
				{
					fstream.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
