package nautilus.game.tutorial;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import nautilus.game.tutorial.TutorialManager.TutorialType;
import nautilus.game.tutorial.part.Part;

public class TutorialData 
{
	private String _playerName;
	
	private Part _part = null;

	private HashMap<TutorialType, Boolean> _tutorials;
	
	private TutorialType _current = null;
	
	public TutorialData(String playerName)
	{
		_playerName = playerName;
		
		_tutorials = new HashMap<TutorialType, Boolean>();
		
		for (TutorialType cur : TutorialType.values())
			_tutorials.put(cur, false);

		Read();
	}

	public Part GetPart() 
	{
		return _part;
	}

	public void SetPart(Part part) 
	{
		_part = part;
	}
	
	public void SetCompleted(TutorialType tute)
	{
		_tutorials.put(tute, true);
		
		Write();
	}
	
	public boolean HasCompleted(TutorialType tute)
	{
		return _tutorials.get(tute);
	}

	public TutorialType GetCurrent()
	{
		return _current;
	}
	
	public void SetCurrent(TutorialType type) 
	{
		_current = type;
	}

	public boolean AllowClassShop() 
	{
		if (_part == null)
			return false;
		
		return _part.AllowClassShop();
	}
	
	public boolean AllowClassSetup() 
	{
		if (_part == null)
			return false;
		
		return _part.AllowClassSetup();
	}
	
	public boolean AllowAction() 
	{
		if (_part == null)
			return false;
		
		return _part.AllowAction();
	}
	
	public void Read()
	{
		FileInputStream fstream = null;
		BufferedReader br = null;
		
		try
		{
			//Make Dir
			File folder = new File("data/tutorial");
			folder.mkdirs();
			
			File file = new File("data/tutorial/" + _playerName + ".dat");
			
			if (file.exists())
			{
				fstream = new FileInputStream(file);
				br = new BufferedReader(new InputStreamReader(fstream));
				
				String line = br.readLine();
				
				while (line != null)
				{
					TutorialType type = TutorialType.valueOf(line.charAt(0)+"");
					boolean complete = line.charAt(1) == '1';
					
					_tutorials.put(type, complete);
					
					line = br.readLine();
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Tutorial Data Read Error.");
			e.printStackTrace();
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
	
	public void Write()
	{
		FileWriter fstream = null;
		BufferedWriter out = null;
		
		try
		{
			//Make Dir
			File folder = new File("data/tutorial");
			folder.mkdirs();
			
			File file = new File("data/tutorial/" + _playerName + ".dat");
			
			fstream = new FileWriter(file);
			out = new BufferedWriter(fstream);
			
			for (TutorialType type : _tutorials.keySet())
			{
				if (_tutorials.get(type))
					out.write(type + "1");
				else
					out.write(type + "0");
				
				out.newLine();
			}

			out.close();
		}
		catch (Exception e)
		{
			System.out.println("Tutorial Data Write Error.");
			e.printStackTrace();
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
}
