package scrobbler;

import graph.DataDialog;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.util.TimerTask;

import util.DataFile;
import util.RSSReader;
import util.Song;
import util.ConnectToLast;

public class Task extends TimerTask {

	public void run() {

		RSSReader reader = RSSReader.getInstance();
		String[] dati = getData();

		//retrieve the list of new songs as an array of Song objects and send them to last.fm
		Song[] s = reader.readSongs(dati[0], Long.parseLong(dati[3]), "recent_listens");
		ConnectToLast lastFmBridge = new ConnectToLast();
		String status = lastFmBridge.sessionRequest(dati[1], dati[2]);
		if (status.equals("OK")){
			String report = "";

			if (s.length != 0) {

				int scrobbled = 0;
				try {
					scrobbled = lastFmBridge.sendSongs(s);
				} catch (Exception e1) {
					// not-so-elegant, please remove in some way
					e1.printStackTrace();
				}

				// reset informations about last scrobbled song
				dati[3] = String.valueOf(s[0].getTimePlayed());
				this.setTime(dati[3]);
				saveData();

				// print useful infos on screen and generate a status string
				if (scrobbled != 0) {
					System.out.println("*");
					for (int q = 0; q < s.length; q++) { System.out.println(s[q].getName() + " - " + s[q].getArtist()); }
					System.out.println("*");
					report = "Last Scrobbled: " + s[0].getName() + " - " + s[0].getArtist();
					setLastScrob(s[0].getName() + " - " + s[0].getArtist());
				}
				else if (scrobbled == 0) { report = "Problems submitting tracks!!"; }
			}
			else {
				if (getLastScrob().equals("")) report = "No tracks to submit";
				else report = "No new tracks. Last: " + getLastScrob() ;
			}

			// print the status string and set it as the tooltip for the tray icon
			System.out.println(report);

			TrayIcon[] icons = SystemTray.getSystemTray().getTrayIcons();
			if (icons.length != 0) icons[0].setToolTip(report);
		}
		else{
			System.out.println("ERROR: Maybe last.fm is experiencing some problems or you set a wrong username/pass!");
			TrayIcon[] icons = SystemTray.getSystemTray().getTrayIcons();
			if (icons.length != 0) icons[0].setToolTip("Wrong user/pass, probably!");
		}
	}


	// useful data and methods for the class
	// all the infos have to be provided when setting up this class
	private String gshark;
	private String last;
	private String pass;
	private String date;
	private String lastScrob = "";

	public void setData(String g, String l, String p, String d){
		gshark = g;
		last = l;
		pass = p;
		date = d;
	}

	private String[] getData(){
		String[] ret = new String[4];
		ret[0] = gshark;
		ret[1] = last;
		ret[2] = pass;
		ret[3] = date;

		return ret;
	}

	private void setTime(String t){
		date = t;
	}

	private void saveData(){
		String[] data;
		try { data = DataFile.getData(); }
		catch (java.io.FileNotFoundException exc) { 
			DataDialog d = new DataDialog();
			data = d.getData();
		}

		data[3] = date;
		DataFile.saveData(data);

		gshark = data[0];
		last = data[1];
		pass = data[2];
	}

	private String getLastScrob()
	{ return lastScrob; }

	private void setLastScrob(String l)
	{ lastScrob = l; }


}
