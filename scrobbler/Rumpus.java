package scrobbler;

import graph.DataDialog;
import util.DataFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;

import javax.swing.JOptionPane;

public class Rumpus {

  // rumpus version string
  public static final String VERSION = "Rumpus / 1.4";

	public static void main(String[] args){

    // try to get the new current version and pop up an alert
		try {versionControl();} catch (IOException e) {System.out.println("ERROR: Could not connect to know if you have a recent Rumpus version!");}

    // load data saved in the "data" file. if it doesn't exists, ask for informations
    // info about the returned array can be found in the DataFile class
		String[] dati;
		try { dati = DataFile.getData(); }
		catch (java.io.FileNotFoundException exc) { 
			DataDialog d = new DataDialog();
			dati = d.saveNewData();
		}
		
    // prepare the scrobbling
		Task task = new Task();
		task.setData(dati[0], dati[1], dati[2], dati[3]);

    // start scrobbling and other services based on data file infos
    // create tray icon end schedule scrobbling. start wah scrobbling if requested
		if (dati[6].equals("0") || dati[6].equals("1")) {
			new graph.TrayIconCreator();
			Timer t = new Timer();
			t.schedule(task, 0, 200000L);		
			if (dati[6].equals("1")) new graph.HuntedList().makeUI("main", "1");
		}
		//only scrobble once. start wah scrobbling if requested
		else if (dati[6].equals("2") || dati[6].equals("3")){
			task.run();
			if (dati[6].equals("3")){ new graph.TrayIconCreator(); new graph.HuntedList().makeUI("main", "1"); }
		}
		else if (dati[6].equals("4")) { new graph.TrayIconCreator(); new graph.HuntedList().makeUI("main", "1"); }

	}	

	/**
	 * Check for new versions (in form of strings) looking at a specific URL.
	 * If a new version is out, pop up a small standard JOptionPane message dialog.
	 * @throws IOException in case problem raised retrieving data from the URL
	 */
	private static void versionControl() throws IOException {
		URL u = new URL("http://rumpus.altervista.org/version");
		BufferedReader version = new BufferedReader(new InputStreamReader(u.openStream()));
		Integer i = Integer.parseInt(version.readLine());
		
		if ((i - Double.parseDouble(VERSION.split(" / ")[1])*100) > 0.0) {
			JOptionPane.showMessageDialog(null, "A new, fantastic, version is out! Go on http://rumpus.altervista.org");
		}
	}

}
