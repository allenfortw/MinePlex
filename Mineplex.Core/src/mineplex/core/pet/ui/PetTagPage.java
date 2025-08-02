package mineplex.core.pet.ui;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.donation.DonationManager;
import mineplex.core.pet.PetClient;
import mineplex.core.pet.PetManager;
import mineplex.core.pet.PetShop;
import mineplex.core.pet.repository.token.PetChangeToken;
import mineplex.core.shop.page.ShopPageBase;
import net.minecraft.server.v1_6_R2.Item;
import net.minecraft.server.v1_6_R2.ItemStack;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;

public class PetTagPage extends ShopPageBase<PetManager, PetShop>
{
	private String _tagName = "Pet Tag";
	
    public PetTagPage(PetManager plugin, PetShop shop, CoreClientManager clientManager, DonationManager donationManager, String name, Player player)
    {
        super(plugin, shop, clientManager, donationManager, name, player, 3);
        
        BuildPage();
    }

	@Override
	protected void BuildPage()
	{
		inventory.setItem(0, new ItemStack(Item.NAME_TAG));
		
		ButtonMap.put(0, new CloseButton());
		ButtonMap.put(1, new CloseButton());
		ButtonMap.put(2, new SelectTagButton(this));
	}

	public void SelectTag()
	{
		if (ChatColor.stripColor(_tagName).length() > 16)
		{
			UtilPlayer.message(Player, F.main(Plugin.GetName(), ChatColor.RED + "Pet name cannot be longer than 16 characters."));
			PlayDenySound(Player);
			
			Player.closeInventory();
			Shop.ResetPlayer(Player);
			
			return;
		}
		
		UtilInv.remove(Player, Material.NAME_TAG, (byte)0, 1);
		
		Creature pet = Plugin.GetPet(Player);
		
		if (pet != null)
		{
			pet.setCustomNameVisible(true);
			pet.setCustomName(_tagName);
		}
		
		PetClient petClient = Plugin.Get(Player);
		
		petClient.GetPets().put(pet.getType(), _tagName);
		petClient.SetPetNameTagCount(petClient.GetPetNameTagCount() - 1);
		
		PetChangeToken token = new PetChangeToken();
		token.Name = Player.getName();
		token.PetName = _tagName;
		token.PetType = pet.getType().toString();
		
		Plugin.GetRepository().UpdatePet(token);
		Plugin.GetRepository().RemovePetNameTag(Player.getName());
		
		Player.closeInventory();
		Shop.ResetPlayer(Player);
	}

	public void SetTagName(String tagName)
	{
		_tagName = tagName;
	}
}
