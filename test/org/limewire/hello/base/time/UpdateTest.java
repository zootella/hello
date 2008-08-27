package org.limewire.hello.base.time;


import java.awt.Frame;

import javax.swing.JDialog;

import org.junit.Test;
import org.limewire.hello.base.time.Update;
import org.limewire.hello.base.time.UpdateReceive;
import org.limewire.hello.base.user.Dialog;

public class UpdateTest {
	
	@Test
	public void spinTest() {
		
		// this will produce a SpinException right away
		// then, when you close the box, the test will succeed
		new TestBox();
	}

	private class TestBox {
		
		public TestBox() {
			
			spin();
			
			JDialog dialog = Dialog.modal("Test Box");
			dialog.setVisible(true); // control sticks here while the dialog is open
		}
	}
	
	private void spin() {

		Parent parent = new Parent();
		parent.child.finished();
	}

	private class Parent {
		
		public Parent() {
			
			update = new Update(new MyUpdateReceive());
			child = new Child(update);
		}
		
		public Update update;
		public Child child;
		
		private class MyUpdateReceive implements UpdateReceive {
			public void receive() {
				
				child.finished();
			}
		}
		
		public void close() {
			update.close();
		}
	}
	
	private class Child {
		
		public Child(Update update) {
			this.update = update;
		}
		
		private Update update;
		
		public void finished() {
			
			update.send();
		}
	}
}