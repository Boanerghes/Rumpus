package util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RSSReader {

	private static RSSReader instance = null;

	private RSSReader() {
	}

	public static RSSReader getInstance() {
		if(instance == null) {
			instance = new RSSReader();	
		}
		return instance;
	}
	/**
	 * Reads an RSS feed from grooveshark and return Song objects
	 * @param s		grooveshark username
	 * @param time	last scrobbling date as yyyyMMddhhmm
	 * @param page	the wanted grooveshark feed (recent_listen.rss, favorites.rss)
	 * @return		Song array ready for last.fm submission
	 */
	public Song[] readSongs(String user, long time, String page) {

		ArrayList<Song> a = new ArrayList<Song> ();

		try {

			String url = "http://api.grooveshark.com/feeds/1.0/users/" + user + "/" + page + ".rss";

			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			URL readURL = new URL(url);

	        // Open connection
	        HttpURLConnection httpConnection =
	            (HttpURLConnection) readURL.openConnection();

	        // Set User-Agent request header
	        httpConnection.setRequestProperty("User-Agent", scrobbler.Rumpus.VERSION);

	        // Open input stream on the search result page
			Document doc = builder.parse(httpConnection.getInputStream());

			NodeList nodes = doc.getElementsByTagName("item");

			for(int i=0;i<nodes.getLength();i++) {

				Element element = (Element)nodes.item(i);
				String t = getElementValue(element,"title");
				String[] ts = t.split(" - ");

				String date = getElementValue(element,"pubDate");
				String[] ds = date.split(" ");
				String[] oraEsatta = ds[4].split(":");

				String  e = ds[3];
				String month = "";
				if (ds[2].equals("Dec")) month = "12";
				else if (ds[2].equals("Jan")) month = "01";
				else if (ds[2].equals("Feb")) month = "02";
				else if (ds[2].equals("Mar")) month = "03";
				else if (ds[2].equals("Apr")) month = "04";
				else if (ds[2].equals("May")) month = "05";
				else if (ds[2].equals("Jun")) month = "06";
				else if (ds[2].equals("Jul")) month = "07";
				else if (ds[2].equals("Aug")) month = "08";
				else if (ds[2].equals("Sep")) month = "09";
				else if (ds[2].equals("Oct")) month = "10";
				else if (ds[2].equals("Nov")) month = "11";
				
				
				e += month + "" + ds[1] + "" + oraEsatta[0] + "" + oraEsatta[1];

				long dataComp = Long.parseLong(e);


				if (dataComp - time > 0) a.add(new Song(ts[0], ts[1], dataComp));

			}//for			
		}//try
		catch (java.io.FileNotFoundException fileNotFound){
			System.out.println("It seems you didn't set your GS username or it's not correct!");
		}
		catch(Exception ex) {
			ex.printStackTrace();	
		}
		
		Song[] songs = new Song[a.size()];
		songs = a.toArray(songs);
		
		return songs;


	}

	private String getCharacterDataFromElement(Element e) {
		try {
			Node child = e.getFirstChild();
			if(child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				return cd.getData();
			}
		}
		catch(Exception ex) {

		}
		return "";			
	} 

	protected float getFloat(String value) {
		if(value != null && !value.equals("")) {
			return Float.parseFloat(value);	
		}
		return 0;
	}

	protected String getElementValue(Element parent,String label) {
		return getCharacterDataFromElement((Element)parent.getElementsByTagName(label).item(0));	
	}


}
