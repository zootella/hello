package org.limewire.hello.base.internet.web;

import java.net.InetAddress;

import org.limewire.hello.base.internet.name.Ip;
import org.limewire.hello.base.state.Task;
import org.limewire.hello.base.state.TaskBody;
import org.limewire.hello.base.state.TaskClose;
import org.limewire.hello.base.state.Update;

public class DomainTask extends TaskClose {
	
	// Make

	/** Use DNS to resolve site like "www.site.com" to an IP address. */
	public DomainTask(Update update, String site) {
		this.update = update; // We'll tell above when we're done
		
		// Save the input
		this.site = site;

		task = new Task(new MyTask()); // Make a separate thread call thread() below now
	}

	/** The site domain name like "www.site.com". */
	public final String site;

	// Result
	
	/** The IP address our DNS lookup found, or throws the exception that made this give up. */
	public Ip result() throws Exception { return (Ip)check(ip); }
	private Ip ip;
	
	// Task

	/** Our Task with a thread that runs our code that blocks. */
	private class MyTask implements TaskBody {
		private Ip taskIp; // References thread() can safely set

		// A separate thread will call this method
		public void thread() throws Exception {

			// Look up the domain name in DNS to get its IP address
			taskIp = new Ip(InetAddress.getByName(site));
		}

		// Once thread() above returns, the normal event thread calls this done() method
		public void done(Exception e) {
			if (closed()) return; // Don't let anything change if we're already closed
			exception = e;        // Get the exception our code above threw
			if (e == null) {      // No exception, save what thread() did
				
				ip = taskIp;
			}
			close();       // We're done
			update.send(); // Tell update we've changed
		}
	}
}
