package util;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Song {
	private String name;
	private String artist;
	private long timePlayed;
	//private Boolean fav;
	
	/**
	 * Creates a new Song object
	 * @param n the song's name
	 * @param a the song's artist
	 * @param t last time the song was played
	 */
	public Song(String n, String a, long t){
		name = n;
		artist = a;
		timePlayed = t;
		//fav = false;
	}
	
	public String getName(){
		return name;
	}
	public String getArtist(){
		return artist;
	}
	public long getTimePlayed(){
		return timePlayed;	
	}
	
	/**
	 * produce the last.fm scrobbling representation for the Song
	 * @param pos the index of the song in the array or list
	 * @return a string representing the Song object
	 * @throws UnsupportedEncodingException if a problem occurred while encoding the string
	 */
	public String getLastLink(int pos) throws UnsupportedEncodingException{
		String req = "";
		req += "&a[" + pos + "]=" + URLEncoder.encode(getArtist(), "UTF-8");
		req += "&t[" + pos + "]=" + URLEncoder.encode(getName(), "UTF-8");
		req += "&i[" + pos + "]=" + URLEncoder.encode(String.valueOf((new TimeConv()).getUnixTime(timePlayed)), "UTF-8");
		req += "&o[" + pos + "]=E";
		req += "&r[" + pos + "]="; //+ URLEncoder.encode(getFav(), "UTF-8");
		req += "&l[" + pos + "]=&b[" + pos + "]=&n[" + pos + "]=&m[" + pos + "]=";
		
		return req;
	}
	
	public String toString(){
		return getName() + " - " + getArtist();
	}
	
	public void setDate(Long date){
	timePlayed = date;
	}
}
