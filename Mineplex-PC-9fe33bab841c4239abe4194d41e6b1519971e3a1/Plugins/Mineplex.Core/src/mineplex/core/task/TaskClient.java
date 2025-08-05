package mineplex.core.task;

import java.util.ArrayList;
import java.util.List;

public class TaskClient
{
	public String Name;
	public List<String> TasksCompleted;
	
	public TaskClient(String name)
	{
		Name = name;
		TasksCompleted = new ArrayList<String>();
	}
	
	public String toString()
	{
		return Name + " Tasks: {" + TasksCompleted.toString() + "}";
	}
}
