package graph;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import util.ConnectToLast;
import util.DataFile;
import util.RSSReader;
import util.Song;

public class TrayIconCreator {
	public TrayIconCreator(){
		TrayIcon trayIcon = null;
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage("rumpus.png");

			ActionListener listener = new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (e.getActionCommand().equals("quit")) System.exit(0);
					else if (e.getActionCommand().equals("favs")){

						String[] read;
						try { read = DataFile.getData(); }
						catch (java.io.FileNotFoundException exc) { 
							DataDialog d = new DataDialog();
							read = d.getData();
						}

						String key = read[4];
						if (key.equals("noSuchKey")) {
							graph.FavDialog dialog = new graph.FavDialog(); 
							key = read[4] = dialog.getKey();
							if (key != null) {
								read[4] = key;
								DataFile.saveData(read);
							}
							else{ System.out.println("ERROR: something gone wrong, please try again"); return; }
						}

						RSSReader reader = RSSReader.getInstance();
						Song[] fav = reader.readSongs(read[0], Long.parseLong(read[5]), "recent_favorite_songs");

						if (fav.length != 0){
							System.out.println("--- Start submitting new favorites.");
							ConnectToLast.sendFavs(fav, read[1], key);

							read[5] = Long.toString(fav[0].getTimePlayed());
							DataFile.saveData(read);
							System.out.println("--- Favorites submitted.");
						}
						else System.out.println("No new favorites");

					}
					else if (e.getActionCommand().equals("editSet")){
						DataDialog d = new DataDialog();
						d.saveNewData();
					}
					else if (e.getActionCommand().equals("WeAreHunted")){
						new HuntedList().makeUI("main", "1");
					}
				}
			};

			PopupMenu popup = new PopupMenu();
			MenuItem quit = new MenuItem("Quit Rumpus");
			MenuItem hunted = new MenuItem("WAH Scrobbling");
			MenuItem sendFav = new MenuItem("Synch Favorites");
			MenuItem manualScr = new MenuItem("Edit Settings");
			quit.setActionCommand("quit");
			sendFav.setActionCommand("favs");
			manualScr.setActionCommand("editSet");
			hunted.setActionCommand("WeAreHunted");
			quit.addActionListener(listener);
			sendFav.addActionListener(listener);
			manualScr.addActionListener(listener);
			hunted.addActionListener(listener);
			popup.add(hunted);
			popup.add(sendFav);
			popup.add(manualScr);
			popup.add(quit);

			trayIcon = new TrayIcon(image, "Rumpus Scrobbler", popup);
			trayIcon.addActionListener(listener);
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println(e);
			}
		} else {
			System.out.println("Tray icon errors!");
		}		
	}
}
