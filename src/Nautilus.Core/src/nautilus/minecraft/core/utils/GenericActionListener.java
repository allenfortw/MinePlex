package nautilus.minecraft.core.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GenericActionListener<T> implements ActionListener 
{
	protected T Object;
	
	public GenericActionListener(T t)
	{
		Object = t;
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		// TODO Auto-generated method stub
	}
}
