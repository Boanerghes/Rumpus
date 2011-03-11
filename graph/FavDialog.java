package graph;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


@SuppressWarnings("serial")
public class FavDialog extends Dialog{

	Button submit = new Button("Open my Web Browser");
	Label toDo = new Label("Please authorise Rumpus to acces your last.fm account first.");

	String token;
	String sessionKey;
	
	public FavDialog () {

		super((Frame)null, "auth request");

		submit.addActionListener(new Ascoltatore());
		submit.setActionCommand("browser");

		this.addWindowListener(new AscoltaWindow());

		BorderLayout b = new BorderLayout();
		setLayout(b);
		setModal(true);
		add (toDo, BorderLayout.NORTH);
		add(submit, BorderLayout.CENTER);

		pack();

		setLocation(100,100);
		setModal(true);
		setVisible(false);
		
	}

	public String getKey(){
		setVisible(true);
		return sessionKey;
	}
	
	public class AscoltaWindow implements WindowListener{

		public void windowActivated(WindowEvent arg0) { }
		public void windowClosed(WindowEvent arg0) { }
		public void windowClosing(WindowEvent arg0) { 
			setVisible(false);
		}
		public void windowDeactivated(WindowEvent arg0) { }
		public void windowDeiconified(WindowEvent arg0) { }
		public void windowIconified(WindowEvent arg0) {	}
		public void windowOpened(WindowEvent arg0) { }
	}

	public class Ascoltatore implements ActionListener{

		public void actionPerformed (ActionEvent e)
		{ 	
			String Comando=e.getActionCommand();
			if (Comando.equals("browser")) {
				token = util.ConnectToLast.authReq();
				submit.setLabel("OK, I'm done");
				submit.setActionCommand("close");

			}
			else if (Comando.equals("close")){
				sessionKey = util.ConnectToLast.getSesKey(token);
				if (sessionKey.equals("noSuchKey")) {
					submit.setLabel("Open my Web Browser");
					submit.setActionCommand("browser");
					toDo.setText("Please follow the in-browser instructions"); }
				else setVisible(false);
			}

		}

	}
}
