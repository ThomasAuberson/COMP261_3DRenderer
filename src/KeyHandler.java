import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*
 * 3D Renderer
 * 
 * Author: Thomas Auberson
 * Version: 0.12
 * 
 * This class controls the key action listener.  Extracted from Template Library v0.1
 */

public class KeyHandler implements KeyListener {

	// FIELDS
	Display display;

	// CONSTRUCTOR
	public KeyHandler(Display d){
		display = d;
		//System.out.println("Key Listener Active");
	}
	
	
	@Override
	public void keyPressed(KeyEvent k) {
		display.keyPressed(k.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent k) {
		display.keyReleased(k.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent k) {
		// TODO Auto-generated method stub
		// display.keyTyped(k.getKeyChar());
	}

}
