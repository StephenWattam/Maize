package maize.ui;

import javax.swing.*;

public class TabPanel extends JPanel{
    MazeTest mazeTest = null;
    JTabbedPane tabContainer = null;
    String name = null;
    JFrame frame = null;

    public TabPanel(MazeTest mazeTest, JTabbedPane tabContainer, String name){
        this.mazeTest       = mazeTest;
        this.tabContainer   = tabContainer;
        this.name           = name;
       
        attach();
    }

    public String getName(){
        return this.name;
    }

    public void attach(){
        Log.log("Attaching tab '" + name + "'");

        if(frame != null){
            frame.dispose();
            frame = null;
        }

        tabContainer.add(this, name);

        // TODO: make this less of a hack
        for(int i=0;i<tabContainer.getTabCount();i++)
            tabContainer.setTabComponentAt(i, new TabButtonComponent(tabContainer) );
    }

    public void detach(){
        if(frame == null){
            // TODO
            tabContainer.remove(this);
            frame = new TabFrame(this);
        }else{
            Log.log("Cannot detach tab '" + name + "', is already detached!");
        }
    }

    // Is the tab currently in a tab, or in a window?
    public boolean isAttached(){
        return frame == null;
    }

    // Updates the info and rendering on a tab
    public void update(){
        System.out.println("STUB update() in TabPanel");
    }

    // Makes a tab panel responsible for cleaning up its descendents.
    public void dispose(){
        System.out.println("STUB dispose() in TabPanel");
    }
}
