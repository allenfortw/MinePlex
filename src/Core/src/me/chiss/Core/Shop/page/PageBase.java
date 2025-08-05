package me.chiss.Core.Shop.page;

import mineplex.core.account.CoreClient;
import net.minecraft.server.v1_6_R2.Item;

import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftItemStack;

public class PageBase<PageType extends IPage<PageType>> extends CraftInventoryCustom implements IPage<PageType>
{
    private int _pageNumber;
    
    protected PageType PreviousPage;
    protected PageType NextPage;
    
    public PageBase(String title)
    {
        super(null, 54, title);
        setMaxStackSize(128);
    }
    
    public void SetPageNumber(int pageNumber)
    {
        _pageNumber = pageNumber;
    }
    
    public int GetPageNumber()
    {
        return _pageNumber;
    }
    
    public void SetPreviousPage(PageType previousPage)
    {
    	if (previousPage != null)
    	{
	        getInventory().setItem(0, CraftItemStack.asNewCraftStack(Item.PAPER, previousPage.GetPageNumber()).getHandle());
	        PreviousPage = previousPage;
    	}
    }
    
    public PageType GetPreviousPage()
    {
        return PreviousPage;
    }

    public boolean HasPreviousPage()
    {
        return (PreviousPage != null);
    }
    
    public void SetNextPage(PageType nextPage)
    {
    	if (nextPage != null)
    	{
	        getInventory().setItem(8, CraftItemStack.asNewCraftStack(Item.PAPER, nextPage.GetPageNumber()).getHandle());
	        NextPage = nextPage;
    	}
    }
    
    public PageType GetNextPage()
    {
        return NextPage;
    }
    
    public boolean HasNextPage()
    {
        return (NextPage != null);
    }
    
    public String GetTitle()
    {
        return getTitle();
    }
    
    public void OpenForPlayer(CoreClient playerClient)
    {
    	playerClient.GetPlayer().openInventory(this);
    }
    
    public void CloseForPlayer(CoreClient playerClient)
    {
    	playerClient.GetPlayer().closeInventory();
    	this.inventory.onClose((CraftPlayer)playerClient.GetPlayer());
    }
}
