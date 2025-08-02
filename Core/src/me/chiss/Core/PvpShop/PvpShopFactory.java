package me.chiss.Core.PvpShop;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;

import mineplex.core.server.IRepository;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import me.chiss.Core.Module.AModule;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PvpShopFactory extends AModule implements IPvpShopFactory
{
	private String _loadFile;
	private HashMap<String, IShopItem> _items; 

	public PvpShopFactory(JavaPlugin plugin, IRepository repository, String loadFile) 
	{
		super("Shop Factory", plugin, repository);

		_items = new HashMap<String, IShopItem>();
		_loadFile = loadFile;

		PopulateItems();
	}

	@Override
	public void enable() 
	{

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
		AddCommand("pvpshop");
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		if (!Clients().Get(caller).Rank().Has(Rank.ADMIN, true))
			return;

		if (args.length == 0)
		{			
			UtilPlayer.message(caller, F.main(GetName(), "Listing Items;"));

			for (IShopItem cur : GetItems())
			{
				UtilPlayer.message(caller, cur.GetName());
			}

			UtilPlayer.message(caller, "Type " + F.elem("/pvpshop <Item>") + " to receive Item.");
			return;
		}

		for (IShopItem cur : GetItems())
		{
			if (cur.GetName().toLowerCase().contains(args[0].toLowerCase()))
			{				
				caller.getInventory().addItem(ItemStackFactory.Instance.CreateStack(cur.GetType(), cur.GetAmount()));

				UtilPlayer.message(caller, F.main(GetName(), "You received " + F.elem(cur.GetName()) + "."));
			}
		}
	}

	private void PopulateItems()
	{
		_items.clear();

		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader br = null;
		
		try
		{
			fstream = new FileInputStream(_loadFile);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			int line = 1;
			int col = 0;
			while ((strLine = br.readLine()) != null)  
			{
				String[] tokens = strLine.split(" ");

				if (tokens.length == 1)
				{
					if (tokens[0].equals("-"))
					{
						col++;
						continue;
					}

					try
					{
						line = Integer.parseInt(tokens[0]);
						col = 0;
						continue;
					}
					catch (Exception e)
					{
						System.out.println("Shop Load - Invalid Line: " + tokens[0]);
						continue;
					}		
				}
				
				if (tokens.length != 8)
				{
					System.out.println("Shop Load - Invalid Token Count: (" + tokens.length + ") " + strLine);
					continue;
				}
				
				try
				{
					//Name
					String name = null;
					if (!tokens[5].equals("null")) name = tokens[5].replaceAll("_", " ");
					
					//Delivery Name
					String deliveryName = null;
					if (!tokens[6].equals("null")) deliveryName = tokens[6].replaceAll("_", " ");
					
					//Desc
					String[] desc = null;
					if (!tokens[7].equals("null"))
					{
						desc = tokens[7].split(",");
						
						for (int i=0 ; i<desc.length ; i++)
							desc[i] = desc[i].replaceAll("_", " ");
					}
					
					//Material
					Material mat = Material.getMaterial(tokens[0]);
					if (mat == null)
					{
						System.out.println("Shop Load - Invalid Material: " + strLine);
						continue;
					}
					
					//Data
					byte data = Byte.parseByte(tokens[1]);
					
					//Amount
					int amount = Integer.parseInt(tokens[2]);
					
					//Price 
					int price = Integer.parseInt(tokens[3]);
					
					//Sale
					float percent = Float.parseFloat(tokens[4]);
					
					AddItem(new ShopItem(this, name, deliveryName, desc, mat, data, amount, price, percent, (line*9) + col));
					col++;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					System.out.println("Shop Load - Invalid Line: " + strLine);
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Shop Load - File Missing: " + _loadFile);
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

	public IShopItem GetItem(String ItemName)
	{
		return _items.get(ItemName);
	}

	@Override
	public Collection<IShopItem> GetItems()
	{
		return _items.values();
	}

	public void AddItem(IShopItem newItem)
	{
		_items.put(newItem.GetName(), newItem);
	}
}
