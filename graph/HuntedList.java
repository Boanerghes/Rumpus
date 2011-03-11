package graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;

import scrobbler.WAHGet;
import util.ConnectToLast;
import util.DataFile;
import util.Song;

public class HuntedList {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				new HuntedList().makeUI("main", "1");
			}
		});
	}

	Song[] songs;
	JLabel[] names;
	JLabel[] artists;
	JButton[] scrobButton;
	JButton[] loveButton;
	JPanel[] fp1 = new JPanel[99];
	JDialog dialog;
	JPanel con;

	ConnectToLast lastFmBridge;
	String currentGenre = "main";
	String currentTime = "1";
	String[] data;
	
	public void makeUI(String genre, String time) { makeUI(genre, time, ""); } 

	public void makeUI(String genre, String time, String user)
	{	

		con = new JPanel();
		con.setLayout(new BoxLayout(con, BoxLayout.Y_AXIS));

		WindowFresher buttonListener = new WindowFresher();
		JButton main = new JButton("main"); main.setActionCommand("genre:main"); main.addActionListener(buttonListener);
		JButton ro = new JButton("ro"); ro.setActionCommand("genre:rock"); ro.addActionListener(buttonListener);
		JButton po = new JButton("po"); po.setActionCommand("genre:pop"); po.addActionListener(buttonListener);
		JButton f = new JButton("f"); f.setActionCommand("genre:folk"); f.addActionListener(buttonListener);
		JButton m = new JButton("m"); m.setActionCommand("genre:metal"); m.addActionListener(buttonListener);
		JButton a = new JButton("a"); a.setActionCommand("genre:alternative"); a.addActionListener(buttonListener);
		JButton e = new JButton("e"); e.setActionCommand("genre:electronic"); e.addActionListener(buttonListener);
		JButton pu = new JButton("pu"); pu.setActionCommand("genre:punk"); pu.addActionListener(buttonListener);
		JButton ra = new JButton("ra"); ra.setActionCommand("genre:rap-hip-hop"); ra.addActionListener(buttonListener);

		JPanel fpP = new JPanel();
		fpP.add(main); fpP.add(ro); fpP.add(po); fpP.add(f); fpP.add(m); fpP.add(a); fpP.add(e); fpP.add(pu); fpP.add(ra);
		con.add(fpP);

		JButton mix = new JButton("mix"); mix.setActionCommand("genre:remix"); mix.addActionListener(buttonListener);
		JButton tw = new JButton("twitter"); tw.setActionCommand("genre:twitter"); tw.addActionListener(buttonListener);
		JButton pers = new JButton("personal"); pers.setActionCommand("personal"); pers.addActionListener(buttonListener);
		JButton day = new JButton("1"); day.setActionCommand("time:1"); day.addActionListener(buttonListener);
		JButton week = new JButton("7"); week.setActionCommand("time:7"); week.addActionListener(buttonListener);
		JButton month = new JButton("30"); month.setActionCommand("time:30"); month.addActionListener(buttonListener);

		fpP = new JPanel();
		fpP.add(mix); fpP.add(tw); fpP.add(pers); fpP.add(day); fpP.add(week); fpP.add(month);
		con.add(fpP);

		try {
			if (!user.equals("")) {	
				songs = WAHGet.getUserChart(user);
				if (songs == null){
					songs = WAHGet.getChart(currentGenre, currentTime); 
					dialog.setTitle(currentGenre + " - " + currentTime + "   We Are Scrobbled");
				}
			}
			else songs = WAHGet.getChart(genre, time);


			names = new JLabel[songs.length];
			artists = new JLabel[songs.length];
			scrobButton = new JButton[songs.length];
			loveButton = new JButton[songs.length];

			ScrobbleListener list = new ScrobbleListener();
			for (int i = 0; i < songs.length; i++) {
				fp1[i] = new JPanel();

				names[i] = new JLabel(songs[i].getName() + " - " + songs[i].getArtist());
				names[i].setPreferredSize(new Dimension(300, 20));
				fp1[i].add(names[i]);

				scrobButton[i] = new JButton("Scrobble");
				scrobButton[i].addActionListener(list);
				scrobButton[i].setActionCommand("scrobble:" + ((Integer) i).toString());

				fp1[i].add(scrobButton[i]);

				loveButton[i] = new JButton("Love");
				loveButton[i].addActionListener(list);
				loveButton[i].setActionCommand("love:" + ((Integer) i).toString());

				fp1[i].add(loveButton[i]);

				con.add(fp1[i]);
			}
		} catch (Exception excep) {excep.printStackTrace();}

		int x, y; x = y = -1;
		if (dialog != null){
			dialog.setVisible(false);
			x = dialog.getX();
			y = dialog.getY();
		}

		dialog = new JDialog((JFrame) null, "We Are Hunted Scrobbling");
		if (x == -1) dialog.setLocation(100, 100);
		else dialog.setLocation(x, y);
		dialog.setSize(600, 400);
		dialog.add(new JScrollPane(con));
		dialog.setVisible(true);
	}
	public class WindowFresher implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			String[] split = e.getActionCommand().split(":");

			if (split[0].equals("genre")){
				makeUI(split[1], "1"); currentGenre = split[1]; 
				dialog.setTitle(split[1] + " - " + "1    We Are Scrobbled");
			}
			else if (split[0].equals("time")){
				makeUI(currentGenre, split[1]);
				currentTime = split[1];
				dialog.setTitle(currentGenre + " - " + currentTime + "   We Are Scrobbled");
			}
			else if (split[0].equals("personal")){
				if (data == null) {
					try { data = DataFile.getData(); }
					catch (java.io.FileNotFoundException exc) { 
						DataDialog d = new DataDialog();
						data = d.getData();
					}
				}

				makeUI("", "", data[7]);
				dialog.setTitle("personal chart by" + data[7]  + "-   We Are Scrobbled");
			}
		}
	}
	public class ScrobbleListener implements ActionListener {

		public void actionPerformed (ActionEvent e)
		{
			if (data == null) {
				try { data = DataFile.getData(); }
				catch (java.io.FileNotFoundException exc) { 
					DataDialog d = new DataDialog();
					data = d.getData();
				}
			}

			if (lastFmBridge == null) {

				lastFmBridge = new ConnectToLast();
				String status = lastFmBridge.sessionRequest(data[1], data[2]);
				if (!status.equals("OK")) {
					lastFmBridge = null; 
					System.out.println("ERROR: could not connect to last.fm - " + status);
					return;
				}
			}

			String[] command = e.getActionCommand().split(":"); 
			Integer songIndex = Integer.parseInt(command[1]);

			if (command[0].equals("scrobble")){
				fp1[songIndex].setBackground(Color.ORANGE);

				DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
				Date date = new Date();

				songs[songIndex].setDate(Long.parseLong(dateFormat.format(date)) - 600);

				try{
					lastFmBridge.sendSongs(songs[songIndex]);
					fp1[songIndex].setOpaque(true);
					fp1[songIndex].setBackground(Color.GREEN);
				} catch (Exception exception) {System.out.println("ERROR connecting with last.fm"); }
			}
			else if (command[0].equals("love")){

				String key = data[4];
				if (key.equals("noSuchKey")) {
					graph.FavDialog dialog = new graph.FavDialog(); 
					key = data[4] = dialog.getKey();
					DataFile.saveData(data);
				}

				try {
					ConnectToLast.sendFavs(songs[songIndex], data[1], key);
					loveButton[songIndex].setOpaque(true);  loveButton[songIndex].setBackground(Color.RED);
				} catch (Exception error) { System.out.println("ERROR: problem submitting loved tracks"); }
			}
		}
	}
}