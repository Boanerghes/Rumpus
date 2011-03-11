package scrobbler;

import util.Song;

/**
 * WAHGet retrieves the list of WAH songs based on genre and timing infos using the official WAH API. 
 */
public class WAHGet {
	
	public static Song[] getChart(String genre, String time) throws Exception{
		String s1;
		if (!genre.equals("main")) s1 = "wearehunted.com/api/chart/" + genre + "/singles/" + time + "/?count=99";
		else s1 = "wearehunted.com/api/chart/singles/" + time + "/?count=99";
		
		// only the readPage method from the ConnectToLast class is used (no last.fm interaction at all)
		String[] read = util.ConnectToLast.readPage(s1);
		Song[] song = new Song[100];

	    
		String artist, name;
		int k, i; k = i = 0;
		for (i = 3; i < read.length-1; i = i + 5){
			name = read[i].substring(22, read[i].lastIndexOf(',')-1);
			artist = read[i+2].split(": ")[1]; artist = artist.substring(1, artist.length()-1);
			song[k] = new Song(name, artist, 0L);
			k++;
			//System.out.println(artist + " - " + name);
		}
		
		Song[] s = new Song[k];
		System.arraycopy(song, 0, s, 0, k);
		return s;
		
	}

	
	public static Song[] getUserChart(String user) throws Exception{
		String s1;
		s1 = "wearehunted.com/api/chart/by/" + user + "/?count=99";
		
		String[] read = util.ConnectToLast.readPage(s1);
		if (read == null) return null;
		Song[] song = new Song[100];

	    
		String artist, name;
		int k, i; k = i = 0;
		for (i = 3; i < read.length-1; i = i + 5){
			name = read[i].substring(22, read[i].lastIndexOf(',')-1);
			artist = read[i+2].split(": ")[1]; artist = artist.substring(1, artist.length()-1);
			song[k] = new Song(name, artist, 0L);
			k++;
			//System.out.println(artist + " - " + name);
		}
		
		Song[] s = new Song[k];
		System.arraycopy(song, 0, s, 0, k);
		return s;
		
	}
	
}
