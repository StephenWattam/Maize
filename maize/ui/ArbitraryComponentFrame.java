package maize.ui;

import javax.swing.*; 
import javax.swing.JPanel;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.Vector;
import java.awt.font.*;
public class ArbitraryComponentFrame extends JFrame{
    private Component c;

    /**Creates a new visualisation with the given net and dimension.

	    @param p_width The width of the visualisation
	    @param p_height The height of the visualisation
	    @param p_title The height of the visualisation
	    @param c Component
     */
    public ArbitraryComponentFrame(int p_width, int p_height, String p_title, Component c){
	    super(p_title);
	    setSize(new Dimension(p_width,p_height));

	    setComponent(c);

	    repaint();
	    setVisible(true);
    }

    /** Returns the component as currently attached 
     *
     * @return The component that is being rendered
     */
    private Component getComponent(){
	    return this.c;
    }

    /** Sets the component that is being rendered 
      * 
      * @param c The component to attach
    */
    private void setComponent(Component c){
	    this.c = c;
	    
	    c.setSize(this.getWidth(),this.getHeight());
	    this.add(c);
    }

    /** Repaint, swing */
    public void repaint(){
	    c.repaint();
    }
}
