package org.limewire.hello.all;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.OceanTheme;

public class CustomTheme extends OceanTheme {
	
	public static void theme() {

		// uncomment to use nimbus
		/*
		try {            
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			Main.report("Exception getting Nimbus: " + e.toString());
        }
        */
		
		// uncomment to color old ocean metal
		/*
    	MetalLookAndFeel.setCurrentTheme(new CustomTheme());
    	UIManager.put("swing.boldMetal", Boolean.FALSE);
    	*/
	}
	
	// hot pink ff33cc
	
	private static final ColorUIResource secondary3 = new ColorUIResource(0xffffff); //0xEEEEEE, ffffff background
	private static final ColorUIResource black = new ColorUIResource(0x000000); //0x333333, 000000 text labels
	private static final ColorUIResource controlTextColor = new ColorUIResource(0x000000); //0x333333, 000000 button text

	
	private static final ColorUIResource primary1 = new ColorUIResource(0xff33cc); //0x6382BF, 808080 3d highlight
	private static final ColorUIResource secondary1 = new ColorUIResource(0xff33cc); //0x7A8A99, 808080 3d shadow
	
	private static final ColorUIResource primary2 = new ColorUIResource(0xff33cc); //0xA3B8CC button focus box
	private static final ColorUIResource primary3 = new ColorUIResource(0xc0c0c0); //0xB8CFE5 button highlight

	private static final ColorUIResource secondary2 = new ColorUIResource(0xff33cc); //0xB8CFE5, ffffff button gradient bottom

	private static final ColorUIResource inactiveControlTextColor = new ColorUIResource(0xff33cc); //0x999999 inactive button border and text
	private static final ColorUIResource menuDisabledForeground = new ColorUIResource(0xff33cc); //0x999999 something on the menu

	
//    protected ColorUIResource getPrimary1() { return primary1; }
//    protected ColorUIResource getPrimary2() { return primary2; }
//    protected ColorUIResource getPrimary3() { return primary3; }
    
//    protected ColorUIResource getSecondary1() { return secondary1; }
//    protected ColorUIResource getSecondary2() { return secondary2; }
    protected ColorUIResource getSecondary3() { return secondary3; }
    
    protected ColorUIResource getBlack() { return black; }
//    public ColorUIResource getInactiveControlTextColor() { return inactiveControlTextColor; }
    public ColorUIResource getControlTextColor() { return controlTextColor; }
//    public ColorUIResource getMenuDisabledForeground() { return menuDisabledForeground; }
    
    
    
    
    
    
    
    
    
    
}
