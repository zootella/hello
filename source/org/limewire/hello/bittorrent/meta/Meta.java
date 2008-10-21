package org.limewire.hello.bittorrent.meta;


import java.util.ArrayList;
import java.util.List;

import org.limewire.hello.base.data.Data;
import org.limewire.hello.base.encode.Hash;
import org.limewire.hello.base.exception.ChopException;
import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.file.PathName;
import org.limewire.hello.base.size.Stripe;
import org.limewire.hello.base.web.Url;
import org.limewire.hello.bittorrent.bencode.Bencoded;

// a .torrent file the program has opened and can look at the contents of
public class Meta {
	
	// -------- Make a Meta object from the bencoded data inside a .torrent file --------

	/** Parse d, the bencoded data content of a .torrent file, into this new Meta object. */
	public Meta(Data d) throws ChopException, MessageException {

		// Parse the bencoded data into a clump of objects that extend Bencoded
		be = Bencoded.parse(d);

		// Get the address of the torrent's tracker on the Web
		tracker = new Url(be.d("announce").getString()); // Throws MessageException if the text isn't a URL

		// Make lists to hold a MetaFile for each file this .torrent file describes, and a MetaPiece about each piece
		files = new ArrayList<MetaFile>();
		pieces = new ArrayList<MetaPiece>();
		
		// Single-file torrent
		if (be.d("info").has("length")) { // The "info" dictionary has a key named "length"
			
			// Make a single MetaFile object, and put it in the files list
			MetaFile f = new MetaFile(                             // The list will only have one MetaFile object
				this,                                              // Link back up to this Meta object
				new Stripe(0, be.d("info").d("length").i()),       // The file's Stripe is all the torrent's data
				new PathName(be.d("info").d("name").getString())); // Name of the single file
			files.add(f);
			
			// The Stripe which is all the data of this torrent is the same as the stripe of the single file
			stripe = f.stripeInTorrent;
		
		// Multifile torrent
		} else {
			
			// Calculate the total size by adding the size of each file
			long size = 0;
			
			// Loop for each BencodedDictionary in the "files" list to make a MetaFile object about each one
			for (Bencoded b : be.d("info").d("files").l()) {

				// Get the relative path to this file
				PathName path = new PathName(be.d("info").d("name").getString()); // Name of the torrent's folder
				for (Bencoded b2 : b.d("path").l())  // Loop for each folder name in the "path" list
					path = path.add(b2.getString()); // Add it to the path, the last one is the file name
				
				// Make a MetaFile object with information this file in the torrent, and add it to the list
				MetaFile f = new MetaFile(
					this,                                // Link back up to this Meta object
					new Stripe(size, b.d("length").i()), // This file's stripe in the torrent's combined data
					path);                               // Folder and file name of this file
				files.add(f);
				
				// Add the size of this file to the total size
				size += f.stripeInTorrent.size;
			}
			
			// Make the Stripe which is all the data of this torrent, from 0 to the total size of all the files
			stripe = new Stripe(0, size);
		}

		// Loop for each 20-byte SHA1 hash in the .torrent file's "pieces" block of them all together
		long pieceSize = be.d("info").d("piece length").i(); // Find what piece size this torrent chose
		int number = 0;                                      // The first piece is number 0
		Data hashes = be.d("info").d("pieces").getData();    // All the hashes are together in a big block of data
		while (hashes.hasData()) {

			// Make a Stripe that shows where this piece is in the torrent's combined data
			Stripe s = (new Stripe(number * pieceSize, pieceSize)).and(stripe); // Shorten the last piece with and()
			if (s == null) throw new MessageException();                        // Make sure and() left us with a Stripe

			// Make a MetaPiece object with information about this piece, and add it to our list
			MetaPiece p = new MetaPiece(
				this,                   // Link back up to this Meta object
				number,                 // The piece number like 0, 1, 2
				s,                      // The Stripe where this piece is in the torrent's combined data
				hashes.cut(Hash.size)); // Cut the next 20-byte SHA1 hash from hashes for this MetaPiece object
			pieces.add(p);

			// Make number bigger for the next loop
			number++;
		}

		// Calculate the torrent's infohash, the SHA1 hash of the bencoded data of the "info" dictionary of the .torrent file
		hash = be.d("info").data().hash(); // Turn the "info" part of the .torrent file back into bencoded data, and hash it
	}

	// -------- Look at the information in this .torrent file --------

	/** The contents of the .torrent file, parsed into a BencodedDictionary of other objects that extend Bencoded. */
	public final Bencoded be;
	/** The address of this torrent's tracker on the Web, like "http://tracker.bittorrent.com:6969/announce". */
	public final Url tracker;
	/** A List of MetaFile objects, each MetaFile object has information about a file listed in this .torrent file. */
	public final List<MetaFile> files;
	/** A List of MetaPiece objects, each MetaPiece object has information about a piece in this .torrent file. */
	public final List<MetaPiece> pieces;
	/** The Stripe that is this torrent's combined data, from 0 to the total size of all the files placed together. */
	public final Stripe stripe;
	/** The torrent's infohash, the 20-byte SHA1 hash value of the "info" dictionary in this .torrent file. */
	public final Data hash;

	// -------- Find the files and pieces in a Stripe of the torrent's combined data --------
	
	/** See which parts of which files overlap the given Stripe in this torrent's combined data. */
	public List<FileStripe> files(Stripe stripe) {
		List<FileStripe> list = new ArrayList<FileStripe>();
		for (MetaFile f : files) { // Loop for each file f this .torrent file describes
			Stripe overlap = f.stripeInTorrent.and(stripe); // See if f's Stripe overlaps with the given one
			if (overlap != null) list.add(new FileStripe(f, overlap)); // It does, make a FileStripe that shows where
		}
		return list;
	}

	/** See which parts of which pieces overlap the given Stripe in this torrent's combined data. */
	public List<PieceStripe> pieces(Stripe stripe) {
		List<PieceStripe> list = new ArrayList<PieceStripe>();
		for (MetaPiece p : pieces) { // Loop for each piece p this .torrent file describes
			Stripe overlap = p.stripeInTorrent.and(stripe); // See if p's Stripe overlaps with the given one
			if (overlap != null) list.add(new PieceStripe(p, overlap)); // It does, make a PieceStripe that shows where
		}
		return list;
	}
}
