package me.chiss.Core.Weapon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import me.chiss.Core.Weapon.Commands.WeaponCommand;
import mineplex.core.MiniPlugin;
import mineplex.core.donation.repository.GameSalesPackageToken;
import mineplex.core.server.remotecall.JsonWebCall;
import nautilus.minecraft.core.webserver.token.Server.WeaponToken;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.plugin.java.JavaPlugin;

public class WeaponFactory extends MiniPlugin implements IWeaponFactory
{
	public static WeaponFactory Instance;
	
	private HashMap<String, IWeapon> _weapons;
	private String _webServerAddress;
	
	protected WeaponFactory(JavaPlugin plugin, String webServerAddress) 
	{
		super("Weapon Factory", plugin);

		_weapons = new HashMap<String, IWeapon>();
        _webServerAddress = webServerAddress;
        
        PopulateWeapons();
	}
	
	public static void Initialize(JavaPlugin plugin, String webServerAddress) 
	{
		Instance = new WeaponFactory(plugin, webServerAddress);
	}

	@Override
	public void AddCommands() 
	{
		AddCommand(new WeaponCommand(this));
	}

	private void PopulateWeapons()
	{
		_weapons.clear();
		
        AddWeapon(new Weapon("Standard Bow", new String[] { }, Material.BOW, 1, 800));
        AddWeapon(new Weapon("Power Bow", new String[] { "+1 Damage" }, Material.BOW, 1, 800));
        AddWeapon(new Weapon("Booster Bow", new String[] { "+1 Skill Level", "-1 Damage" }, Material.BOW, 1, 800));
        
        AddWeapon(new Weapon("Standard Axe", new String[] { }, Material.IRON_AXE, 1, 600));
        AddWeapon(new Weapon("Power Axe", new String[] { "+1 Damage", "Low Durability" }, Material.GOLD_AXE, 1, 600));
        AddWeapon(new Weapon("Booster Axe", new String[] { "+1 Skill Level", "-1 Damage" }, Material.DIAMOND_AXE, 1, 600));
        
        AddWeapon(new Weapon("Standard Sword", new String[] { }, Material.IRON_SWORD, 1, 600));
        AddWeapon(new Weapon("Power Sword", new String[] { "+1 Damage", "Low Durability" }, Material.GOLD_SWORD, 1, 600));
        AddWeapon(new Weapon("Booster Sword", new String[] { "+1 Skill Level", "-1 Damage" }, Material.DIAMOND_SWORD, 1, 600));
                
		List<WeaponToken> weaponTokens = new ArrayList<WeaponToken>();
		
		for (IWeapon weapon : _weapons.values())
		{
			WeaponToken weaponToken = new WeaponToken();
			weaponToken.Name = weapon.GetName();
			weaponToken.SalesPackage = new GameSalesPackageToken();
			weaponToken.SalesPackage.Gems = weapon.GetGemCost();
			
			weaponTokens.add(weaponToken);
		}
		
		List<WeaponToken> weapons = new JsonWebCall(_webServerAddress + "Dominate/GetWeapons").Execute(new TypeToken<List<WeaponToken>>(){}.getType(), weaponTokens);
		
		for (WeaponToken weaponToken : weapons)
		{
			if (_weapons.containsKey(weaponToken.Name))
			{
				_weapons.get(weaponToken.Name).Update(weaponToken);
			}
		}
	}

	public IWeapon GetWeapon(String weaponName)
	{
		return _weapons.get(weaponName);
	}

	@Override
	public Collection<IWeapon> GetWeapons()
	{
		return _weapons.values();
	}

	public void AddWeapon(IWeapon newWeapon)
	{
	    _weapons.put(newWeapon.GetName(), newWeapon);
	}
}
