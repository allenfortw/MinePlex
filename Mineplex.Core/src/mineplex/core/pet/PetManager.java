package mineplex.core.pet;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

import mineplex.core.MiniClientPlugin;
import mineplex.core.pet.event.PetSpawnEvent;
import mineplex.core.pet.repository.PetRepository;
import mineplex.core.pet.repository.token.ClientPetTokenWrapper;
import mineplex.core.pet.ui.PetTagPage;
import mineplex.core.shop.item.ShopItem;
import mineplex.core.shop.page.AnvilContainer;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.account.CoreClientManager;
import mineplex.core.account.event.ClientWebResponseEvent;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilInv;
import mineplex.core.donation.DonationManager;
import mineplex.core.itemstack.ItemStackFactory;
import net.minecraft.server.v1_6_R2.EntityCreature;
import net.minecraft.server.v1_6_R2.EntityHuman;
import net.minecraft.server.v1_6_R2.EntityInsentient;
import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.Item;
import net.minecraft.server.v1_6_R2.Navigation;
import net.minecraft.server.v1_6_R2.Packet100OpenWindow;
import net.minecraft.server.v1_6_R2.Packet103SetSlot;
import net.minecraft.server.v1_6_R2.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_6_R2.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_6_R2.PathfinderGoalSelector;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class PetManager extends MiniClientPlugin<PetClient>
{
	private CoreClientManager _clientManager;
	private DonationManager _donationManager;
	private mineplex.core.creature.Creature _creatureModule;
	private PetRepository _repository;
	private PetFactory _petFactory;
	
	private NautHashMap<String, Creature> _activePetOwners;
	private NautHashMap<String, Integer> _failedAttempts;

	private PetShop _petShop;
	
	private Field _goalSelector;
	private Field _targetSelector;
	
	public PetManager(JavaPlugin plugin, CoreClientManager clientManager, DonationManager donationManager, mineplex.core.creature.Creature creatureModule, String webAddress)
	{
		super("Pet Manager", plugin);
		
		_clientManager = clientManager;
		_donationManager = donationManager;
		_creatureModule = creatureModule;		
		_repository = new PetRepository(webAddress);
		_petFactory = new PetFactory(_repository);
		_petShop = new PetShop(this, clientManager, donationManager);
		
		_activePetOwners = new NautHashMap<String, Creature>();
		_failedAttempts = new NautHashMap<String, Integer>();
	}
	
	public void AddPetOwner(Player player, EntityType entityType, Location location)
	{
		if (_activePetOwners.containsKey(player.getName()))
		{
			if (_activePetOwners.get(player.getName()).getType() != entityType)
			{
				RemovePet(player, true, true);
			}
			else
				return;
		}
		
		Creature pet = (Creature)_creatureModule.SpawnEntity(location, entityType);
		pet.setCustomNameVisible(true);
		pet.setCustomName(Get(player).GetPets().get(entityType));
		
		_activePetOwners.put(player.getName(), pet);
		_failedAttempts.put(player.getName(), 0);
		
		if (pet instanceof Ageable)
		{
			((Ageable)pet).setBaby();
			((Ageable)pet).setAgeLock(true);
		}
		
		ClearPetGoals(pet);
		
		player.setItemInHand(null);
	}
	
	public Creature GetPet(Player player)
	{
		return _activePetOwners.get(player.getName());
	}
	
	public void RemovePet(final Player player, boolean returnPetEgg, boolean removeOwner)
	{
		if (_activePetOwners.containsKey(player.getName()))
		{
			final Creature pet = _activePetOwners.get(player.getName());
			pet.remove();
			
			if (returnPetEgg)
			{
				_plugin.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable()
				{
					public void run()
					{						
			        	ItemStack petEgg = new ItemStack(Material.MONSTER_EGG, 1, (byte)pet.getType().getTypeId()); 
			        	ItemMeta meta = petEgg.getItemMeta();
			        	meta.setDisplayName(pet.getCustomName());
			        	meta.setLore(Arrays.asList(ChatColor.WHITE + "Right-click block to place"));
			        	
			        	petEgg.setItemMeta(meta);
			        	player.getInventory().addItem(petEgg);					
					}
				});
			}
			
			if (removeOwner)
			{
				_activePetOwners.remove(player.getName());
			}
		}
	}
	
	@EventHandler
	public void TryToAddPetOwner(PlayerInteractEvent event)
	{
		if (event.hasItem() && event.getItem().getType() == Material.MONSTER_EGG && event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if (Get(event.getPlayer()).GetPets().containsKey(EntityType.fromId(event.getItem().getData().getData())))
			{
				PetSpawnEvent petSpawnEvent = new PetSpawnEvent(event.getPlayer(), EntityType.fromId(event.getItem().getData().getData()), event.getClickedBlock().getLocation().add(.5,  1,  .5));
				_plugin.getServer().getPluginManager().callEvent(petSpawnEvent);
				
				if (!petSpawnEvent.isCancelled())
					AddPetOwner(event.getPlayer(), EntityType.fromId(event.getItem().getData().getData()), event.getClickedBlock().getLocation().add(.5,  1,  .5));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{		
		if (_activePetOwners.containsKey(event.getPlayer().getName()) && _activePetOwners.get(event.getPlayer().getName()) == event.getRightClicked())
		{
			if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.NAME_TAG) 
			{
				PetTagPage petTagPage = new PetTagPage(this, _petShop, _clientManager, _donationManager, "Repairing", event.getPlayer());
		        EntityPlayer entityPlayer = ((CraftPlayer)event.getPlayer()).getHandle();
		        int containerCounter = entityPlayer.nextContainerCounter();
		        entityPlayer.playerConnection.sendPacket(new Packet100OpenWindow(containerCounter, 8, "Repairing", 9, true));
		        entityPlayer.activeContainer = new AnvilContainer(entityPlayer.inventory, petTagPage.getInventory());
		        entityPlayer.activeContainer.windowId = containerCounter;
		        entityPlayer.activeContainer.addSlotListener(entityPlayer);
		        entityPlayer.playerConnection.sendPacket(new Packet103SetSlot(containerCounter, 0, new net.minecraft.server.v1_6_R2.ItemStack(Item.NAME_TAG)));
		        
		        _petShop.SetCurrentPageForPlayer(event.getPlayer(), petTagPage);
		        event.setCancelled(true);
			}
			else
				RemovePet(event.getPlayer(), true, true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInventoryClick(InventoryClickEvent event)
	{
		for (Pet pet : _petFactory.GetPets())
		{
			UtilInv.DisallowMovementOf(event, "Pet Manager", Material.MONSTER_EGG, pet.GetDisplayData(), true);
		}
		
		UtilInv.DisallowMovementOf(event, "Pet Manager", Material.NAME_TAG, (byte)0, true);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		RemovePet(event.getPlayer(), false, true);
	}
	
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) 
    {
    	if (event.getEntity() instanceof Creature && _activePetOwners.containsValue((Creature)event.getEntity()))
    	{
    		event.setCancelled(true);
    	}
    }
	
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event)
    {
    	if (event.getEntity() instanceof Creature && _activePetOwners.containsValue((Creature)event.getEntity()))
    	{
    		if (event.getCause() == DamageCause.VOID)
    		{
    			String playerName = null;
    			
    			for (Entry<String,Creature> entry : _activePetOwners.entrySet())
    			{
    				if (entry.getValue() == event.getEntity())
    					playerName = entry.getKey();
    			}
    			
    			if (playerName != null)
    			{
    				Player player = Bukkit.getPlayerExact(playerName);
    				
    				if (player != null && player.isOnline())
    				{
    					RemovePet(player, true, true);
    				}
    			}
    		}
    		event.setCancelled(true);
    	}
    }
    
	@EventHandler
	public void onUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FAST)
			return;
		
		int xDiff;
		int yDiff;
		int zDiff;
		
		Iterator<String> ownerIterator = _activePetOwners.keySet().iterator(); 
		
		while (ownerIterator.hasNext())
		{
			String playerName = ownerIterator.next();
			Player owner = Bukkit.getPlayer(playerName);
			
			Creature pet = _activePetOwners.get(playerName);
			Location petSpot = pet.getLocation();
			Location ownerSpot = owner.getLocation();
			xDiff = Math.abs(petSpot.getBlockX() - ownerSpot.getBlockX());
			yDiff = Math.abs(petSpot.getBlockY() - ownerSpot.getBlockY());
			zDiff = Math.abs(petSpot.getBlockZ() - ownerSpot.getBlockZ());
		
			if ((xDiff + yDiff + zDiff) > 4)
			{
				EntityCreature ec = ((CraftCreature) pet).getHandle();
	            Navigation nav = ec.getNavigation();
	            
	            int xIndex = -1;
	            int zIndex = -1;
	            Block targetBlock = ownerSpot.getBlock().getRelative(xIndex, -1, zIndex);
	            while (targetBlock.isEmpty() || targetBlock.isLiquid())
	            {
	            	if (xIndex < 2)
	            		xIndex++;
	            	else if (zIndex < 2)
	            	{
	            		xIndex = -1;
	            		zIndex++;
	            	}
	            	else
	            		return;
	            	
	            	targetBlock = ownerSpot.getBlock().getRelative(xIndex, -1, zIndex);
	            }
	            
	            if (_failedAttempts.get(playerName) > 4)
	            {
	            	RemovePet(owner, true, false);
	            	ownerIterator.remove();
	            }
	            else if (!nav.a(targetBlock.getX(), targetBlock.getY() + 1, targetBlock.getZ(), 1.5f))
	            {
	            	if (pet.getFallDistance() == 0)
	            	{
	            		_failedAttempts.put(playerName, _failedAttempts.get(playerName) + 1);
	            	}
	            }
	            else
	            {
	            	_failedAttempts.put(playerName, 0);
	            }
			}
		}
	}
	
	private void ClearPetGoals(Creature pet)
	{
    	try
		{
			_goalSelector = EntityInsentient.class.getDeclaredField("goalSelector");
			_goalSelector.setAccessible(true);
			_targetSelector = EntityInsentient.class.getDeclaredField("targetSelector");
			_targetSelector.setAccessible(true);
			
			EntityCreature creature = ((CraftCreature)pet).getHandle();
	        	
	    	PathfinderGoalSelector goalSelector = new PathfinderGoalSelector(((CraftWorld)pet.getWorld()).getHandle().methodProfiler);
	    
	    	goalSelector.a(0, new PathfinderGoalLookAtPlayer(creature, EntityHuman.class, 6.0F));
	    	goalSelector.a(1, new PathfinderGoalRandomLookaround(creature));
	    	
	    	_goalSelector.set(creature, goalSelector);
	    	_targetSelector.set(creature, new PathfinderGoalSelector(((CraftWorld)pet.getWorld()).getHandle().methodProfiler));
		} 
    	catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} 
    	catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} 
    	catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		} 
    	catch (SecurityException e)
		{
			e.printStackTrace();
		}
	}

	@EventHandler
	public void OnClientWebResponse(ClientWebResponseEvent event)
	{		
		ClientPetTokenWrapper token = new Gson().fromJson(event.GetResponse(), ClientPetTokenWrapper.class);
	
		Get(token.Name).Load(token.DonorToken);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void GivePetItems(PlayerJoinEvent event)
	{
		for (Entry<EntityType, String> pet : Get(event.getPlayer()).GetPets().entrySet())
		{
			event.getPlayer().getInventory().addItem(new ShopItem(Material.MONSTER_EGG, (byte)pet.getKey().getTypeId(), pet.getValue(), new String[] { ChatColor.WHITE + "Right-click block to place" }, 1, false, false));
		}
		
		int playerTagCount = Get(event.getPlayer()).GetPetNameTagCount();
		
		if (playerTagCount > 0)
			event.getPlayer().getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.NAME_TAG, (byte)0, playerTagCount, "Name Tag", new String[] { ChatColor.RESET + "" + ChatColor.GRAY + "Right-click pet to apply nametag" }));
	}
	
	@Override
	protected PetClient AddPlayer(String player)
	{
		return new PetClient();
	}

	public PetFactory GetFactory()
	{
		return _petFactory;
	}

	public PetRepository GetRepository()
	{
		return _repository;
	}
}
