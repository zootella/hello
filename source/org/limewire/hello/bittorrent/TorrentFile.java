package org.limewire.hello.bittorrent;

import org.limewire.hello.base.file.Save;
import org.limewire.hello.bittorrent.meta.MetaFile;


// a file that is part of a Torrent the program is sharing online
public class TorrentFile {
	
	

	// information about us in the .torrent file
	public MetaFile metaFile;
	
	// our temporary file we're using, or where we're saved on disk for uploading
	public Save save;

}
