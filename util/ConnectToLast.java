package util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;


import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class ConnectToLast{

  public static final String API_KEY = "YOUR_API_KEY";
  public static final String SECRET = "YOUR_SECRET";

  //
  // STATIC METHODS
  //
  
  /**
   *  Begin the authorization process at last.fm, ask for browser user interaction.
   *  @return a last.fm valid token
   */
	public static String authReq() {
		String token = "";
		try{
		
		//fase 1 - token request
		String s1 = "ws.audioscrobbler.com/2.0/?method=auth.getToken&api_key=" + API_KEY;
		s1 += "&api_sig" + StringToMD5.MD5(URLEncoder.encode("api_key" + API_KEY + "methodauth.getToken" + SECRET, "UTF-8"));

		try{
		token = readPage(s1)[2].split("<token>")[1].split("</token>")[0];
		} catch (Exception e){ System.out.println("ERROR RETRIVING DATA. try again later."); return null;}

		//fase 2 - user in-browser auth
		s1 = "www.last.fm/api/auth/?api_key=" + API_KEY + "&token=" + token;
		Desktop.getDesktop().browse(new URI("http", s1 , "", ""));
		
		} catch (Exception e){e.printStackTrace();}
		return token;
	}
	
	/**
	 *  Get a valid last.fm token and return a session key
	 *  @param token a valid last.fm token
	 *  @return the string "noSuchKey" if something goes wrong, a last.fm session key otherwise 
	 */
	public static String getSesKey(String token){
		try{
		String s1 = "ws.audioscrobbler.com/2.0/?method=auth.getSession" + "&token=" + token;
		s1 += "&api_key=10aa5186376e8d146cac21e6f8da82eb" + "&api_sig="; 
		s1 += StringToMD5.MD5(URLEncoder.encode("api_key" + API_KEY + "methodauth.getSession" + "token" + token + SECRET, "UTF-8"));

		return readPage(s1)[4].split("<key>")[1].split("</key>")[0];
		} catch (Exception e){return "noSuchKey";}
	}


	public static void sendFavs (Song[] fav, String user, String key){

		for (int i = 0; i < fav.length; i++){
			try{
			String artist = URLEncoder.encode(fav[i].getArtist(), "UTF-8");
			String track = URLEncoder.encode(fav[i].getName(), "UTF-8");
			key = URLEncoder.encode(key, "UTF-8");

			String api_sig = 	"api_key" + API_KEY +
			"artist" + fav[i].getArtist() + 
			"method" + "track.love" +
			"sk" + key +
			"track" + fav[i].getName() +
			SECRET;

			String s1 = "method=track.love";
			s1 += "&track=" + track + "&artist=" + artist; 
			s1 += "&api_key=" + API_KEY;
			s1 += "&api_sig=" + StringToMD5.MD5(api_sig);
			s1 += "&sk=" + key;


			// Send data
			URL url = new URL("http://ws.audioscrobbler.com/2.0/");
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", "Rumpus (1.4beta)");
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(s1);
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = rd.readLine();
			line = rd.readLine();
			if (line.equals("<lfm status=\"ok\">"))
				System.out.println(fav[i].getName() + " - " + fav[i].getArtist());
			else System.out.println("*! error submitting " + fav[i].getName() + " - " + fav[i].getArtist());
			wr.close();
			rd.close();
			} catch (Exception e){e.printStackTrace();}
		}
	}
	
	// the same as the method above but for a single Song object
	public static void sendFavs (Song fav, String user, String key){
		Song[] song = new Song[1];
		song[0] = fav;
		sendFavs(song, user, key);
	}
  //utility method to get a web page. return an array with line x at index x
	public static String[] readPage(String s1) throws Exception{

		URI uri = new URI("http", s1 , "", "");
		URL readURL = uri.toURL();

		// Open connection
		HttpURLConnection httpConnection =
			(HttpURLConnection) readURL.openConnection();

		// Set User-Agent request header
		httpConnection.setRequestProperty("User-Agent", scrobbler.Rumpus.VERSION);

		// HTTP response code (200 means success)
		if (httpConnection.getResponseCode() != 200) { System.out.println("ERROR: could not connect. Server returned "+ httpConnection.getResponseCode() ); return null; }

		// Open input stream on the search result page
		BufferedReader in = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));

		ArrayList<String> a = new ArrayList<String>();
		String line = in.readLine();

		while (line != null){
			a.add(line);
			line= in.readLine();
		}

		String[] lines = new String[a.size()];
		lines = a.toArray(lines);
		return lines;
	}

  //
  //NON-STATIC METHODS
  //
  
  // useful variables for a scrobbling (or fav synch) session
	private String sessionID;
	private String subLink;
	private String status;
	
	public String sessionRequest(String user, String md5pass){
		
		String s1 = "post.audioscrobbler.com/?hs=true&p=1.2.1&c=rms&v=1.0&u=" + user;
		long timestamp = System.currentTimeMillis()/1000;

		String md5 = StringToMD5.MD5(md5pass + timestamp);

		String s2 = "&t=" + timestamp + "&a=" + md5;

		try{
		String[] utils = readPage(s1 + s2);
		
		status = utils[0];
		sessionID = utils[1];
		subLink = utils[3];
		
		} catch (Exception e){ return status; }
		
		return status;
	}

	public int sendSongs (Song[] songs) throws Exception{

		int i = 0;
		if (status.equals("OK")){

			String req = "s=" + sessionID;

			// Construct data
			for (int s=(songs.length - 1); s >= 0; s--){
				req += songs[s].getLastLink(i);
				i++;
			}
			//System.out.println(req);

			// Send data
			URL url = new URL(subLink);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(req);
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = rd.readLine();
			if (line.equals("OK")) return i;

			wr.close();
			rd.close();


		}
		return i;
	}
	// the same as the method above but for a single Song object
	public int sendSongs (Song song) throws Exception
	{
		Song[] songs = new Song[1];
		songs[0] = song;
		return sendSongs(songs);
	}
	
}
