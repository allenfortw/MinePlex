package mineplex.minecraft.game.classcombat.Class;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.MiniPlugin;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ClassRestore extends MiniPlugin
{
  private ClassManager _classManager;
  private SkillFactory _skillFactory;
  private boolean _active = false;
  private String _folder = "data/classbuilds/";
  
  private HashMap<String, HashMap<IPvpClass, Collection<ISkill>>> _skills = new HashMap();
  
  public ClassRestore(JavaPlugin plugin, ClassManager classManager, SkillFactory skillFactory)
  {
    super("Class Restore", plugin);
    
    this._classManager = classManager;
    this._skillFactory = skillFactory;
    
    File file = new File(this._folder);
    file.mkdirs();
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void Join(AsyncPlayerPreLoginEvent event)
  {
    if (!this._active) {
      return;
    }
    if (event.getLoginResult() != org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.ALLOWED) {
      return;
    }
    ReadBuilds(event.getName());
  }
  
  @EventHandler
  public void Quit(PlayerQuitEvent event)
  {
    if (!this._active) {
      return;
    }
    WriteBuilds(event.getPlayer().getName());
    this._skills.remove(event.getPlayer().getName());
  }
  
  public Collection<ISkill> GetBuild(String name, IPvpClass gameClass)
  {
    if (!this._active) {
      return null;
    }
    if (!this._skills.containsKey(name)) {
      return null;
    }
    if (!((HashMap)this._skills.get(name)).containsKey(gameClass)) {
      return null;
    }
    return (Collection)((HashMap)this._skills.get(name)).get(gameClass);
  }
  
  public void SaveBuild(String name, IPvpClass gameClass, Collection<ISkill> skills)
  {
    if (!this._active) {
      return;
    }
    if (!this._skills.containsKey(name)) {
      this._skills.put(name, new HashMap());
    }
    
    ((HashMap)this._skills.get(name)).put(gameClass, new ArrayList());
    

    for (ISkill skill : skills) {
      ((Collection)((HashMap)this._skills.get(name)).get(gameClass)).add(skill);
    }
  }
  
  public boolean IsActive() {
    return this._active;
  }
  
  public void Activate()
  {
    this._active = true;
  }
  






  public void WriteBuilds(String player)
  {
    if (!this._skills.containsKey(player)) {
      return;
    }
    System.out.println("Writing Build: " + player);
    
    FileWriter fstream = null;
    BufferedWriter out = null;
    
    try
    {
      fstream = new FileWriter(this._folder + player + ".dat");
      out = new BufferedWriter(fstream);
      
      HashMap<IPvpClass, Collection<ISkill>> builds = (HashMap)this._skills.get(player);
      Iterator localIterator2;
      for (Iterator localIterator1 = builds.keySet().iterator(); localIterator1.hasNext(); 
          localIterator2.hasNext())
      {
        IPvpClass gameClass = (IPvpClass)localIterator1.next();
        localIterator2 = ((Collection)builds.get(gameClass)).iterator(); continue;ISkill skill = (ISkill)localIterator2.next();
        out.write(gameClass.GetName() + " " + skill.GetName() + "\n");
      }
    }
    catch (Exception e) {
      System.err.println("Build Write Error: " + e.getMessage());
      


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
      File f = new File(this._folder + player + ".dat");
      
      if (!f.exists()) {
        return;
      }
      if (!this._skills.containsKey(player)) {
        this._skills.put(player, new HashMap());
      }
      fstream = new FileInputStream(f);
      in = new DataInputStream(fstream);
      br = new BufferedReader(new InputStreamReader(in));
      
      String strLine;
      while ((strLine = br.readLine()) != null) {
        String strLine;
        String[] tokens = strLine.split(" ");
        
        if (tokens.length >= 3)
        {

          try
          {

            IPvpClass gameClass = this._classManager.GetClass(tokens[0]);
            
            if (!((HashMap)this._skills.get(player)).containsKey(gameClass)) {
              ((HashMap)this._skills.get(player)).put(gameClass, new ArrayList());
            }
            ISkill skill = this._skillFactory.GetSkill(mineplex.core.common.util.F.combine(tokens, 2, null, false));
            
            ((Collection)((HashMap)this._skills.get(player)).get(gameClass)).add(skill);
          }
          catch (Exception e)
          {
            System.err.println("Build Parse Error: " + e.getMessage());
          }
        }
      }
    }
    catch (Exception e) {
      System.err.println("Build Read Error: " + e.getMessage());
      


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
