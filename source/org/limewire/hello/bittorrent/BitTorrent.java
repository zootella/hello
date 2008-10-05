package org.limewire.hello.bittorrent;

import java.util.List;

import org.limewire.hello.base.file.Here;
import org.limewire.hello.base.internet.old.OldInternet;
import org.limewire.hello.base.setting.BooleanSetting;
import org.limewire.hello.base.setting.PathSetting;
import org.limewire.hello.base.setting.Store;
import org.limewire.hello.base.web.Web;

// the program's bittorrent object keeps the list of torrents we're sharing online
public class BitTorrent {

	// -------- Settings --------

	/** true if the Pause button is pressed. */
	public BooleanSetting pauseSetting;
	/** The Path to the folder where the BitTorrent tab saves files. */
	public PathSetting folderSetting;
	
	// -------- The torrent's we're sharing --------
	
	public List<Torrent> list;
	
	public BitTorrent(OldInternet internet, Web web, Store store) {
		

		// Make the BitTorrent settings, specifying default values
		pauseSetting = store.make("bittorrent.pause", false); // By default, not pressed
		folderSetting = store.make("bittorrent.folder", Here.folder().add("Shared")); // A folder named "Shared" next to where we're running
		
	}
	
	

}
