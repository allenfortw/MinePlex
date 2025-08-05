package mineplex.core.pet.ui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.donation.DonationManager;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.pet.Pet;
import mineplex.core.pet.PetExtra;
import mineplex.core.pet.PetManager;
import mineplex.core.pet.PetShop;
import mineplex.core.pet.repository.token.PetChangeToken;
import mineplex.core.pet.repository.token.PetToken;
import mineplex.core.shop.item.ShopItem;
import mineplex.core.shop.page.ConfirmationPage;
import mineplex.core.shop.page.ShopPageBase;

public class PetPage extends ShopPageBase<PetManager, PetShop>
{	
    public PetPage(PetManager plugin, PetShop shop, CoreClientManager clientManager, DonationManager donationManager, String name, Player player)
    {
        super(plugin, shop, clientManager, donationManager, name, player);
        
        BuildPage();
    }
    
    protected void BuildPage()
    {
        int slot = 19;
        for (Pet pet : Plugin.GetFactory().GetPets())
        {
        	List<String> itemLore = new ArrayList<String>();
        	
        	itemLore.add(C.cYellow + pet.GetCost(CurrencyType.Gems) + " Gems");
        	itemLore.add(C.cBlack);
        	
        	if (DonationManager.Get(Player.getName()).OwnsUnknownPackage("Pet " + pet.GetPetName()))
        	{
        		itemLore.add(C.cRed + "You already own this pet!");
                getInventory().setItem(slot, new ShopItem(Material.MONSTER_EGG, (byte)pet.GetPetType().getTypeId(), pet.GetPetName(), itemLore.toArray(new String[itemLore.size()]), 1, true, false).getHandle());
        	}
        	else
        	{
        		AddButton(slot, new ShopItem(Material.MONSTER_EGG, (byte)pet.GetPetType().getTypeId(), pet.GetPetName(), itemLore.toArray(new String[itemLore.size()]), 1, true, false), new PetButton(pet, this));
        	}            
        	
        	slot++;
        }
        
        slot = 40;
        for (PetExtra petExtra : Plugin.GetFactory().GetPetExtras())
        {
        	List<String> itemLore = new ArrayList<String>();
        	
        	itemLore.add(C.cYellow + petExtra.GetCost(CurrencyType.Gems) + " Gems");
        	itemLore.add(C.cBlack);
        	
        	if (Plugin.Get(Player.getName()).GetPets().size() == 0)
        	{
                itemLore.add(C.cRed + "You don't own a pet!");
                getInventory().setItem(slot, new ShopItem(petExtra.GetMaterial(), (byte)0, petExtra.GetName(), itemLore.toArray(new String[itemLore.size()]), 1, true, false).getHandle());                
        	}
        	else
        	{
        		AddButton(slot, new ShopItem(petExtra.GetMaterial(), (byte)0, petExtra.GetName(), itemLore.toArray(new String[itemLore.size()]), 1, false, false), new PetExtraButton(petExtra, this));
        	}
            
        	slot++;
        }
    }

	public void PurchasePet(final Player player, final Pet pet)
	{
		Shop.OpenPageForPlayer(player, new ConfirmationPage<PetManager, PetShop>(Plugin, Shop, ClientManager, DonationManager, new Runnable()
		{
			public void run()
			{
				PetChangeToken token = new PetChangeToken();
				token.Name = player.getName();
				token.PetType = pet.GetPetType().toString();
				token.PetName = "";
				
				PetToken petToken = new PetToken();
				petToken.PetType = token.PetType;
				
				Plugin.GetRepository().AddPet(token);
				Plugin.Get(player).GetPets().put(pet.GetPetType(), token.PetName);
				
				Plugin.GetRepository().AddPetNameTag(player.getName());
				Plugin.Get(player).SetPetNameTagCount(Plugin.Get(player).GetPetNameTagCount() + 1);
				player.getInventory().addItem(ItemStackFactory.Instance.CreateStack(Material.NAME_TAG, (byte)0, 1, "Name Tag", new String[] { ChatColor.RESET + "" + ChatColor.GRAY + "Right-click pet to apply nametag" }));
    			player.sendMessage(F.main("Pet Shop", "Right click pet to enter name and apply tag."));
			}
		}, this, pet, CurrencyType.Gems, player));
	}

	public void PurchasePetExtra(final Player player, PetExtra petExtra)
	{
		Shop.OpenPageForPlayer(player, new ConfirmationPage<PetManager, PetShop>(Plugin, Shop, ClientManager, DonationManager, new Runnable()
		{
			public void run()
			{
				Plugin.GetRepository().AddPetNameTag(player.getName());
				Plugin.Get(player.getName()).SetPetNameTagCount(Plugin.Get(player.getName()).GetPetNameTagCount() + 1);
    			player.sendMessage(F.main("Pet Shop", "Right click pet to enter name and apply tag."));
			}
		}, this, petExtra, CurrencyType.Gems, player));
	}
}