package org.limewire.hello.bittorrent.meta;

import java.util.List;

import org.junit.Test;
import org.limewire.hello.all.Main;
import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.file.File;
import org.limewire.hello.base.file.Path;
import org.limewire.hello.base.size.Stripe;

public class MetaTest {

	@Test
	public void testStripe() throws Exception {
		
		Path path = new Path("C:\\Documents\\document\\projects\\torrent\\test.torrent");
		Meta meta = new Meta(File.data(path));
		
		testStripe(meta, new Stripe(0, 50));
		testStripe(meta, new Stripe(50, 50));
		testStripe(meta, new Stripe(0, 8311893));
		testStripe(meta, new Stripe(0, 8311894));
		testStripe(meta, new Stripe(8311883, 30));
		testStripe(meta, new Stripe(8311883, 6391814));
		
		Main.report(print(meta));
	}
	
	public void testStripe(Meta meta, Stripe stripe) {
		Main.report("");
		Main.report(stripe.toString());
		List<FileStripe> list = meta.files(stripe);
		for (FileStripe f : list)
			Main.report(f.stripeInFile.toString() + " " + f.file.path.toString());
	}
	
	// have a print method here that prints out all the information about this .torrent file
	// and also demonstrates how easy it is to read one, for instance
	// part of it is printing the bencoded data
	
	public String print(Meta meta) {
		StringBuffer b = new StringBuffer();
		
		b.append("tracker:           " + meta.tracker.address                                + "\n");
		b.append("size:              " + meta.stripe.size                                    + "\n");
		b.append("infohash:          " + meta.hash.base16()                                  + "\n");
		b.append("number of pieces:  " + meta.pieces.size()                                  + "\n");
		b.append("first piece size:  " + meta.pieces.get(0).stripeInTorrent.size                      + "\n");
		b.append("second piece size: " + meta.pieces.get(1).stripeInTorrent.size                      + "\n");
		b.append("last piece size:   " + meta.pieces.get(meta.pieces.size() - 1).stripeInTorrent.size + "\n");
		b.append("files:\n");
		
		for (MetaFile f : meta.files) {
			b.append("\n");
			b.append("file name: " + f.path.toString() + "\n");
			b.append("file size: " + f.stripeInTorrent.size    + "\n");
		}
		
		b.append("\n");
		meta.be.toString(b, "");
		return b.toString();
	}

	public Data makeDotTorrentFile() {

		return null;
	}
}
