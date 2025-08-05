package me.chiss.Core.Shop.page;

import java.util.List;

import org.bukkit.entity.HumanEntity;

import mineplex.core.account.CoreClient;

public interface IPage<PageType extends IPage<PageType>>
{
    String GetTitle();
    boolean HasNextPage();
    boolean HasPreviousPage();
    void SetPreviousPage(PageType previousPage);
    void SetNextPage(PageType nextPage);
    PageType GetPreviousPage();
    PageType GetNextPage();
    
    void OpenForPlayer(CoreClient clicker);
    void CloseForPlayer(CoreClient clicker);
    
    void SetPageNumber(int pageNumber);
    int GetPageNumber();
    
    List<HumanEntity> getViewers();
}
