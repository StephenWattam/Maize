package maize.ui;

import javax.swing.*;

public class TabPanel extends JPanel{
    MazeTest mazeTest           = null;
    JTabbedPane tabContainer    = null;
    String name                 = null;
    JFrame frame                = null;

    /** Constructs a new TabPanel, assigns it as a child of the given JTabbedPane tabContainer under the given name. */
    public TabPanel(MazeTest mazeTest, JTabbedPane tabContainer, String name){
        this.mazeTest       = mazeTest;
        this.tabContainer   = tabContainer;
        this.name           = name;
       
        attach();
    }

    /** Returns the name used as a tab header. */
    public String getName(){
        return this.name;
    }

    /** Attaches this panel to its parent tab container. */
    public void attach(){
        if(tabContainer.indexOfComponent(this) == -1){
            Log.log("Attaching tab '" + name + "'");
            if(frame != null){
                frame.dispose();
                frame = null;
            }

            tabContainer.add(this, name);

            // Set the tab to have a nice detach button on it.
            // FIXME: this is a bit of a hack
            for(int i=0;i<tabContainer.getTabCount();i++){
                if(tabContainer.getComponentAt(i) == this){
                    tabContainer.setTabComponentAt(i, new TabButtonComponent(tabContainer) );
                }
            }

        }else{
            Log.log("Cannot attach tab '" + name + "', is already attached.");
        }
    }

    /** Detaches the panel from its tab container, and gives it a TabFrame of its very own. */
    public void detach(){
        if(frame == null){
            // TODO
            Log.log("Detaching tab '" + name + "'");
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
        Log.log("Disposing of panel '" + name + "'");
        if(frame != null)
            frame.dispose();
    }
}
