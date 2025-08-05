package mineplex.enjinTranslator;

import java.util.List;

public class EnjinPurchase
{
	public User user;
	public List<Item> items;
	public long purchase_date;
	public String currency;
	public String character;
	public List<String> commands;
	
	public String toString()
	{
		String itemStr = " items: {";
		
		if (items != null)
		{
			for (Item item : items)
			{
				itemStr += " item_id:" + item.item_id + " item_name:" + item.item_name + " item_price:" + item.item_price;
			}
		}
		
		itemStr += "}";
		
		String userStr = " user : ";
		
		if (user != null)
		{
			userStr += "user_id:" + user.user_id + " username:" + user.username;
		}
		
		return "EnjinePurchase - " + userStr + " " + itemStr + " purchase_date:" + purchase_date + " currency:" + currency + " character:" + character;
	}
}
