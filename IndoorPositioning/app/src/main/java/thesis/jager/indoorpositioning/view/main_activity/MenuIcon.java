package thesis.jager.indoorpositioning.view.main_activity;

/**
 * Created by Jager on 2016.04.11..
 */
public class MenuIcon
{
       public MenuIcon(String title, int resourceID, MenuIconID menuIconId)
       {
              this.title = title;
              this.resourceID = resourceID;
              this.id = menuIconId;
       }

       private String title;
       private int resourceID;
       private MenuIconID id;

       public String getTitle()
       {
              return title;
       }

       public void setTitle(String title)
       {
              this.title = title;
       }

       public int getResourceID()
       {
              return resourceID;
       }

       public void setResourceID(int resourceID)
       {
              this.resourceID = resourceID;
       }

       public MenuIconID getId()
       {
              return id;
       }

       public void setId(MenuIconID id)
       {
              this.id = id;
       }
}
