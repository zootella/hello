package org.limewire.hello.base.internet.web;

import java.net.InetAddress;

import org.jdesktop.swingworker.SwingWorker;
import org.limewire.hello.base.internet.name.Ip;
import org.limewire.hello.base.state.Later;
import org.limewire.hello.base.state.Update;

public class DomainLater extends Later {
	
	// Make

	/** Use DNS to resolve site like "www.site.com" to an IP address. */
	public DomainLater(Update above, String site) {
		this.above = above; // We'll tell above when we're done
		
		// Save the input
		this.site = site;

		work = new MySwingWorker();
		work.execute(); // Have a worker thread call doInBackground() now
	}

	/** The site domain name like "www.site.com". */
	public final String site;

	// Result
	
	/** The IP address our DNS lookup found, or throws the exception that made this give up. */
	public Ip result() throws Exception { return (Ip)check(ip); }
	private Ip ip;
	
	// Inside

	/** Our SwingWorker with a worker thread that runs our code that blocks. */
	private class MySwingWorker extends SwingWorker<Void, Void> {
		private Exception workException; // References the worker thread can safely set
		private Ip workIp;

		// A worker thread will call this method
		public Void doInBackground() {
			try {

				// Look up the domain name in DNS to get its IP address
				workIp = new Ip(InetAddress.getByName(site));

			} catch (Exception e) { workException = e; } // Catch the exception our code threw
			return null;
		}

		// Once doInBackground() returns, the normal event thread calls this done() method
		public void done() {
			if (closed()) return; // Don't let anything change if we're already closed
			try { get(); } catch (Exception e) { exception = e; } // Get the exception worker threw
			if (workException != null) exception = workException; // Get the exception our code threw
			if (exception == null) { // No exception, save what worker did
				
				ip = workIp;
			}
			close(); // We're done
			above.send(); // Tell update we've changed
		}
	}
}
