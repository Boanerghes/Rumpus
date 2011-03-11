package graph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import util.DataFile;
import util.StringToMD5;


public class DataDialog
{
	JButton submit = new JButton("Confirm");

	JTextField grooveUser;
	JTextField lastUser;
	private JPasswordField lastPass;
	JTextField wahUser;

	ButtonGroup group;
	JCheckBox wahOnStart;

	JLabel gu = new JLabel("Grooveshark username: ");
	JLabel lu = new JLabel("Last.fm username: ");
	JLabel lp = new JLabel("Last.fm password: ");
	JLabel wah = new JLabel("We Are Hunted username: ");

	JPanel con;
	JDialog dialog;

	String[] dati = new String[util.DataFile.DATA_ELEMENTS];

	public DataDialog()
	{	
		try{ dati = DataFile.getData();
		} catch (java.io.FileNotFoundException e) {
			for (int i = 0; i < 3; i++)
				dati[i] = "";
			dati[3] = "200912010000";
			dati = DataFile.correct(dati);
		}

		grooveUser = new JTextField(dati[0]);
		lastUser = new JTextField(dati[1]);
		lastPass = new JPasswordField(dati[2]);
		wahUser = new JTextField(dati[7]);

		con = new JPanel();
		con.setLayout(new BoxLayout(con, BoxLayout.Y_AXIS));

		grooveUser.setColumns(9);
		lastUser.setColumns(9);
		lastPass.setColumns(9);
		wahUser.setColumns(9);

		JPanel fp = new JPanel();
		fp.add(new JLabel("Set your preferences!"));
		con.add(fp);

		fp = new JPanel();
		fp.add(gu); fp.add (grooveUser); fp.add(wah); fp.add(wahUser);
		con.add(fp);

		fp = new JPanel();
		fp.add(lu); fp.add (lastUser);	fp.add(lp);	fp.add (lastPass);
		con.add(fp);

		fp = new JPanel();
		JRadioButton[] radioButtons = new JRadioButton[3];
		group = new ButtonGroup();
		CheckButton buttonList = new CheckButton();
	
		radioButtons[0] = new JRadioButton("scheduled GS scrobbling");
		radioButtons[0].setActionCommand("schedule"); radioButtons[0].addActionListener(buttonList);

		radioButtons[1] = new JRadioButton("oneTime scrob");
		radioButtons[1].setActionCommand("oneTime"); radioButtons[1].addActionListener(buttonList);

		radioButtons[2] = new JRadioButton("never scrobble");
		radioButtons[2].setActionCommand("never"); radioButtons[2].addActionListener(buttonList);


		for (int i = 0; i < 3; i++) {
			group.add(radioButtons[i]);
			fp.add(radioButtons[i]);
		}
		
		wahOnStart = new JCheckBox("open WAH scrobbling on start", true);
		fp.add(wahOnStart);
		
		if (dati[6].equals("0") || dati[6].equals("1")) {
			radioButtons[0].setSelected(true);
			wahOnStart.setSelected(false);
			if (dati[6].equals("1")) wahOnStart.setSelected(true);
		}
		else if (dati[6].equals("2") || dati[6].equals("3")){
			radioButtons[1].setSelected(true);
			wahOnStart.setSelected(false);
			if (dati[6].equals("3")) wahOnStart.setSelected(true);
		}
		else if (dati[6].equals("4")) {
			radioButtons[2].setSelected(true); 
			wahOnStart.setSelected(true);
			wahOnStart.setEnabled(false);
		}
		
		con.add(fp);

		fp = new JPanel();
		submit.addActionListener(new Ascoltatore());
		submit.setActionCommand("send");
		fp.add(submit);
		con.add(fp);

		dialog = new JDialog((JFrame) null, "Rumpus Preferences");
		dialog.setLocation(100, 100);
		dialog.setSize(700, 175);
		dialog.add(con);
		dialog.setModal(true);
		dialog.setVisible(false);

	}

	public String[] getData()
	{ 	
		dialog.setVisible(true);
		return dati; 
	}
	
	public String[] saveNewData(){
		String[] freshData = this.getData();
		DataFile.saveData(freshData);
		return freshData;
	}

	public class CheckButton implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String command = group.getSelection().getActionCommand();
			if (command.equals("never")){ wahOnStart.setSelected(true);  wahOnStart.setEnabled(false); }
			else wahOnStart.setEnabled(true);
		}
	}

	public class Ascoltatore implements ActionListener{

		public void actionPerformed (ActionEvent e)
		{
			String grooveSchedule = group.getSelection().getActionCommand();
			Boolean wahStart = wahOnStart.isSelected();
			String Comando=e.getActionCommand();
			
			if (Comando.equals("send")){

				if (!dati[1].equals(lastUser.getText())){
					dati[4] = "noSuchKey";
					dati[5] = "200912010000";
				}
				
				dati[0] = grooveUser.getText();
				dati[1] = lastUser.getText();
				
				char[] pass = lastPass.getPassword();
				String p = "";
				for (int i = 0; i < pass.length; i++) p += pass[i];
				
				if (!StringToMD5.MD5(p).equals(dati[2]) && !p.equals(dati[2]))
					dati[2] = StringToMD5.MD5(p);
				dati[7] = wahUser.getText();
	
				
				if (grooveSchedule.equals("schedule")){
					if (wahStart == false) dati[6] = "0";
					else dati[6] = "1";
				} else if (grooveSchedule.equals("oneTime")){
					if (wahStart == false) dati[6] = "2";
					else dati[6] = "3";
				} else if (grooveSchedule.equals("never")) dati[6] = "4";
				
				dialog.setVisible(false);
			}
			
		}
	}


}