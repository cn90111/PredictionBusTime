package chara;

public class User
{
	private String UUID;
	private static User uniqueUser = null;
	
	private User()
	{
		setUUID("");
	}
	
	public static User getUniqueUser()
	{
		 if(uniqueUser == null)
		 {
			 uniqueUser = new User();
		 }
             
         return uniqueUser;
	}
	
	public void setUUID(String UUID)
	{
		this.UUID = UUID;
	}
	public String getUUID()
	{
		return UUID;
	}
}
