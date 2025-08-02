package me.chiss.Core.Loot;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.chiss.Core.Loot.Weapons.AlligatorsTooth;
import me.chiss.Core.Loot.Weapons.GiantsSword;
import me.chiss.Core.Loot.Weapons.HyperAxe;
import me.chiss.Core.Loot.Weapons.LightningScythe;
import me.chiss.Core.Loot.Weapons.MagneticBlade;
import me.chiss.Core.Loot.Weapons.MeteorBow;
import me.chiss.Core.Loot.Weapons.WindBlade;
import me.chiss.Core.Module.AModule;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilWorld;
import mineplex.core.itemstack.ItemStackFactory;

public class LootFactory extends AModule
{
	private ArrayList<LootBase> _legendary;

	private HashMap<Item, LootBase> _loot = new HashMap<Item, LootBase>();

	public LootFactory(JavaPlugin plugin) 
	{
		super("Loot Factory", plugin);
	}

	@Override
	public void enable() 
	{
		_legendary = new ArrayList<LootBase>();

		AddLoot(new WindBlade(this));
		AddLoot(new LightningScythe(this));
		AddLoot(new HyperAxe(this)); 
		AddLoot(new GiantsSword(this));
		AddLoot(new MeteorBow(this));
		AddLoot(new AlligatorsTooth(this));
		AddLoot(new MagneticBlade(this));
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
		AddCommand("giveloot");
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		if (!Clients().Get(caller).Rank().Has(Rank.ADMIN, true))
			return;

		for (LootBase loot : _legendary)
			loot.GiveTo(caller);
	}

	@EventHandler
	public void Interact(PlayerInteractEvent event)
	{
		if (event.getPlayer().getItemInHand() == null)
			return;

		if (!Clients().Get(event.getPlayer()).Rank().Has(Rank.ADMIN, false))
			return;

		if (event.getPlayer().getItemInHand().getType() != Material.BLAZE_POWDER)
			return;

		DropLoot(event.getPlayer().getTargetBlock(null, 0).getRelative(BlockFace.UP).getLocation().add(0.5, 0.5, 0.5), 2, 3, 0.05f, 0.01f, 2d);

		event.setCancelled(true);
	}

	private void AddLoot(LootBase loot)
	{
		_legendary.add(loot);

		//Register Events
		UtilServer.getServer().getPluginManager().registerEvents(loot, Plugin());
	}

	@EventHandler
	public void Quit(PlayerQuitEvent event)
	{
		for (LootBase loot : _legendary)
			loot.Reset(event.getPlayer());
	}

	public void DropLoot(Location loc, int eMin, int eRan, float rareChance, float legendChance, double forceMult)
	{
		//Emerald
		DropEmerald(loc, eMin, eRan, forceMult);

		//Enchantment
		if (Math.random() < rareChance)
			DropRare(loc, forceMult);

		//Weapon
		if (Math.random() < legendChance)
			DropLegendary(loc, forceMult);
	}

	public void DropEmerald(Location loc, int eMin, int eRan, double forceMult) 
	{
		for (int i=0 ; i < eMin + UtilMath.r(eRan+1) ; i++)
		{
			Item e = loc.getWorld().dropItemNaturally(loc, ItemStackFactory.Instance.CreateStack(Material.EMERALD));
			e.setVelocity(e.getVelocity().multiply(forceMult));
		}
	}

	public void DropRare(Location loc, double forceMult) 
	{

	}

	public void DropLegendary(Location loc, double forceMult)
	{
		LootBase loot = _legendary.get(UtilMath.r(_legendary.size()));
		Item e = loc.getWorld().dropItemNaturally(loc, loot.Get());
		e.setVelocity(e.getVelocity().multiply(forceMult));

		_loot.put(e, loot);
	}

	@EventHandler
	public void Pickup(PlayerPickupItemEvent event) 
	{
		if (event.isCancelled())
			return;

		if (!_loot.containsKey(event.getItem()))
			return;

		LootBase loot = _loot.remove(event.getItem());

		UtilServer.broadcastSpecial("Legendary Loot",
						F.name(event.getPlayer().getName()) + " looted " + F.item(C.cRed + loot.GetName()) + " near " + 
						F.elem(UtilWorld.locToStrClean(event.getPlayer().getLocation())));	
	}
}
