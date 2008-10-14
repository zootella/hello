package org.limewire.hello.base.download;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.limewire.hello.base.pattern.Range;
import org.limewire.hello.base.state.Close;
import org.limewire.hello.base.web.Url;

/** An open web request and download. */
public class Get extends Close {
	
	// Make
	
	/** Send a HTTP GET request to url and get the response. */
	public Get(Url url, Range range) throws IOException {
		
		// Save the given Url and Range
		this.url = url;
		this.range = range;
		
		//TODO put the range in the request

		// Create and send a HTTP GET request, and get the response
		client = new DefaultHttpClient();
		get = new HttpGet(this.url.uri);
		response = client.execute(get); // Blocks while we wait for the web server's response
		entity = response.getEntity();
		if (entity != null) // The response may not have an entity
			stream = entity.getContent();
		else
			stream = null;
	}

	/** The given Url we get. */
	public final Url url;
	/** The given Range we get. */
	public final Range range;

	/** The client object we use to send the request. */
	public final HttpClient client;
	/** The get object that represents our request to the web server. */
	public final HttpGet get;
	/** The response object that represents the web server's response. */
	public final HttpResponse response;
	/** The response entity, null if response doesn't have one. */
	public final HttpEntity entity;
	/** A stream to read the response entity, null if response doesn't have one. */
	public final InputStream stream;

	/** Stop all network communication. */
	public void close() {
		if (already()) return;
		try { if (stream != null) stream.close(); } catch (Exception e) {} // Close the InputStream to release the connection
		try { get.abort(); } catch (Exception e) {} // Give up our HTTP GET request
	}
	
	// Look

	/** The size of the file this Get can download. */
	public Range range() {
		
		long size = entity.getContentLength();
		System.out.println("entity.getContentLength() " + size);
		
		return new Range(0, size);
	}
}
