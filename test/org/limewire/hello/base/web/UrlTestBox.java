package org.limewire.hello.base.web;

import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.limewire.hello.base.exception.MessageException;
import org.limewire.hello.base.user.Cell;
import org.limewire.hello.base.user.Dialog;
import org.limewire.hello.base.user.Panel;
import org.limewire.hello.base.user.SelectTextArea;
import org.limewire.hello.base.user.TextMenu;

public class UrlTestBox {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new UrlTestBox();
            }
        });
    }

	private JDialog dialog;
	private JTextField in;
	private SelectTextArea address, get, protocol, user, pass, site, port, path;
	private SelectTextArea uriToString;
	private SelectTextArea uriScheme, uriHost, uriPort;
	private SelectTextArea uriSchemeSpecificPart, uriAuthority, uriUserInfo, uriPath, uriQuery, uriFragment;
	private SelectTextArea rawSchemeSpecificPart, rawAuthority, rawUserInfo, rawPath, rawQuery, rawFragment;

	public UrlTestBox() {

		// Make controls
		in = new JTextField("https://user:pass@www.site.com:99/folder/folder/file.ext?parameters#bookmark");
		in.getDocument().addDocumentListener(new MyDocumentListener());
		new TextMenu(in);
		
		address  = new SelectTextArea();
		get      = new SelectTextArea();
		protocol = new SelectTextArea();
		user     = new SelectTextArea();
		pass     = new SelectTextArea();
		site     = new SelectTextArea();
		port     = new SelectTextArea();
		path     = new SelectTextArea();
		
		uriToString = new SelectTextArea();
		
		uriScheme = new SelectTextArea();
		uriHost   = new SelectTextArea();
		uriPort   = new SelectTextArea();
		
		uriSchemeSpecificPart = new SelectTextArea();
		uriAuthority          = new SelectTextArea();
		uriUserInfo           = new SelectTextArea();
		uriPath               = new SelectTextArea();
		uriQuery              = new SelectTextArea();
		uriFragment           = new SelectTextArea();
		
		rawSchemeSpecificPart = new SelectTextArea();
		rawAuthority          = new SelectTextArea();
		rawUserInfo           = new SelectTextArea();
		rawPath               = new SelectTextArea();
		rawQuery              = new SelectTextArea();
		rawFragment           = new SelectTextArea();

		// Lay out controls
		Panel panel = new Panel();
		panel.border();
		panel.place(1,  0, 1, 1, 0, 1, 0, 0, Cell.wrap(in).fillWide());
		
		panel.place(0,  1, 1, 1, 2, 0, 0, 0, Cell.wrap(new JLabel("address")));
		panel.place(0,  2, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("get")));
		panel.place(0,  3, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("protocol")));
		panel.place(0,  4, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("user")));
		panel.place(0,  5, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("pass")));
		panel.place(0,  6, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("site")));
		panel.place(0,  7, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("port")));
		panel.place(0,  8, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("path")));
		
		panel.place(0,  9, 1, 1, 2, 0, 0, 0, Cell.wrap(new JLabel("uri to string")));
		
		panel.place(0, 10, 1, 1, 2, 0, 0, 0, Cell.wrap(new JLabel("uri scheme")));
		panel.place(0, 11, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("uri host")));
		panel.place(0, 12, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("uri port")));

		panel.place(0, 13, 1, 1, 2, 0, 0, 0, Cell.wrap(new JLabel("uri scheme specific part")));
		panel.place(0, 14, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("uri authority")));
		panel.place(0, 15, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("uri user info")));
		panel.place(0, 16, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("uri path")));
		panel.place(0, 17, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("uri query")));
		panel.place(0, 18, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("uri fragment")));

		panel.place(0, 19, 1, 1, 2, 0, 0, 0, Cell.wrap(new JLabel("raw scheme specific part")));
		panel.place(0, 20, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("raw authority")));
		panel.place(0, 21, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("raw user info")));
		panel.place(0, 22, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("raw path")));
		panel.place(0, 23, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("raw query")));
		panel.place(0, 24, 1, 1, 0, 0, 0, 0, Cell.wrap(new JLabel("raw fragment")));

		panel.place(1,  1, 1, 1, 2, 1, 0, 0, Cell.wrap(address).fillWide());
		panel.place(1,  2, 1, 1, 0, 1, 0, 0, Cell.wrap(get).fillWide());
		panel.place(1,  3, 1, 1, 0, 1, 0, 0, Cell.wrap(protocol).fillWide());
		panel.place(1,  4, 1, 1, 0, 1, 0, 0, Cell.wrap(user).fillWide());
		panel.place(1,  5, 1, 1, 0, 1, 0, 0, Cell.wrap(pass).fillWide());
		panel.place(1,  6, 1, 1, 0, 1, 0, 0, Cell.wrap(site).fillWide());
		panel.place(1,  7, 1, 1, 0, 1, 0, 0, Cell.wrap(port).fillWide());
		panel.place(1,  8, 1, 1, 0, 1, 0, 0, Cell.wrap(path).fillWide());
		
		panel.place(1,  9, 1, 1, 2, 1, 0, 0, Cell.wrap(uriToString).fillWide());
		
		panel.place(1, 10, 1, 1, 2, 1, 0, 0, Cell.wrap(uriScheme).fillWide());
		panel.place(1, 11, 1, 1, 0, 1, 0, 0, Cell.wrap(uriHost).fillWide());
		panel.place(1, 12, 1, 1, 0, 1, 0, 0, Cell.wrap(uriPort).fillWide());

		panel.place(1, 13, 1, 1, 2, 1, 0, 0, Cell.wrap(uriSchemeSpecificPart).fillWide());
		panel.place(1, 14, 1, 1, 0, 1, 0, 0, Cell.wrap(uriAuthority).fillWide());
		panel.place(1, 15, 1, 1, 0, 1, 0, 0, Cell.wrap(uriUserInfo).fillWide());
		panel.place(1, 16, 1, 1, 0, 1, 0, 0, Cell.wrap(uriPath).fillWide());
		panel.place(1, 17, 1, 1, 0, 1, 0, 0, Cell.wrap(uriQuery).fillWide());
		panel.place(1, 18, 1, 1, 0, 1, 0, 0, Cell.wrap(uriFragment).fillWide());
		
		panel.place(1, 19, 1, 1, 2, 1, 0, 0, Cell.wrap(rawSchemeSpecificPart).fillWide());
		panel.place(1, 20, 1, 1, 0, 1, 0, 0, Cell.wrap(rawAuthority).fillWide());
		panel.place(1, 21, 1, 1, 0, 1, 0, 0, Cell.wrap(rawUserInfo).fillWide());
		panel.place(1, 22, 1, 1, 0, 1, 0, 0, Cell.wrap(rawPath).fillWide());
		panel.place(1, 23, 1, 1, 0, 1, 0, 0, Cell.wrap(rawQuery).fillWide());
		panel.place(1, 24, 1, 1, 0, 1, 0, 0, Cell.wrap(rawFragment).fillWide());

		// Make the dialog box and show it on the screen
		dialog = Dialog.make("Url Test");
		dialog.setContentPane(panel.jpanel); // Put everything we layed out in the dialog box
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Make closing the dialog close the program
		Dialog.show(dialog, 800, 600); // Control sticks here while the dialog is open
		update(); // Parse and show output for the default starting text
	}

	private class MyDocumentListener implements DocumentListener {
		public void insertUpdate(DocumentEvent e) { update(); }
		public void removeUpdate(DocumentEvent e) { update(); }
		public void changedUpdate(DocumentEvent e) {}
	}
	
	private void update() {

		String s = in.getText();

		Url url;
		try {
			url = new Url(s);
			
			address.setText(url.address);
			get.setText(url.get);
			protocol.setText(url.protocol);
			user.setText(url.user);
			pass.setText(url.pass);
			site.setText(url.site);
			port.setText(url.port + "");
			path.setText(url.path.toString());
			
		} catch (MessageException e) {

			address.setText("(message exception)");
			get.setText("");
			protocol.setText("");
			user.setText("");
			pass.setText("");
			site.setText("");
			port.setText("");
			path.setText("");
		}
		
		URI uri;
		try {
			uri = new URI(s);
			
			uriToString.setText(say(uri.toString()));
			
			uriScheme.setText(say(uri.getScheme()));
			uriHost.setText(say(uri.getHost()));
			uriPort.setText(uri.getPort() + "");
			
			uriSchemeSpecificPart.setText(say(uri.getSchemeSpecificPart()));
			uriAuthority.setText(say(uri.getAuthority()));
			uriUserInfo.setText(say(uri.getUserInfo()));
			uriPath.setText(say(uri.getPath()));
			uriQuery.setText(say(uri.getQuery()));
			uriFragment.setText(say(uri.getFragment()));
			
			rawSchemeSpecificPart.setText(say(uri.getRawSchemeSpecificPart()));
			rawAuthority.setText(say(uri.getRawAuthority()));
			rawUserInfo.setText(say(uri.getRawUserInfo()));
			rawPath.setText(say(uri.getRawPath()));
			rawQuery.setText(say(uri.getRawQuery()));
			rawFragment.setText(say(uri.getRawFragment()));

		} catch (URISyntaxException e) {
			
			uriToString.setText("(uri syntax exception)");
			
			uriScheme.setText("");
			uriHost.setText("");
			uriPort.setText("");
			
			uriSchemeSpecificPart.setText("");
			uriAuthority.setText("");
			uriUserInfo.setText("");
			uriPath.setText("");
			uriQuery.setText("");
			uriFragment.setText("");
			
			rawSchemeSpecificPart.setText("");
			rawAuthority.setText("");
			rawUserInfo.setText("");
			rawPath.setText("");
			rawQuery.setText("");
			rawFragment.setText("");
		}
	}

	private String say(String s) {
		if (s == null) return "(null)";
		else           return s;
	}
}
