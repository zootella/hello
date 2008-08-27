package org.limewire.hello.bittorrent;


import java.util.List;

import org.limewire.hello.bittorrent.meta.Meta;

// a torrent the program is sharing online
public class Torrent {
	
	
	/** The .torrent file that has the list of files, sizes, and piece hashes. */
	public Meta meta;
	
	
	public List<TorrentFile> files;
	
	public List<Peer> peers;
	
	
	
	
	
	
	// have it keep and return state like we're sharing, we're paused, and so on
	// do this so you can list one and see it working in the list
	
	
	
	
	
	// for the gui
	
	public boolean canShare() { return true; }
	public boolean canPause() { return true; }
	public void share() {
		if (!canShare()) return;
	}
	public void pause() {
		if (!canPause()) return;
	}
	public void close() {
		
	}
	
	public String describeStatus() { return ""; }
	public int describeStatusNumber() { return 0; }
	public String describeName() { return ""; }
	public String describeSize() { return ""; }
	public long size() { return 0; }
	public String describeTorrent() { return ""; }
	public String describeSavedTo() { return ""; }

}
