package util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class DataFile {
	
	/**
	 * number of elements inside the data file (rows number)
	 * the meaning of each line is as follows:
	 * 0 -> Grooveshark username
	 * 1 -> Last.fm username
	 * 2 -> Last.fm password as md5
	 * 3 -> last scrobbling date. default value 200912010000
	 * 4 -> Last.fm session key
	 * 5 -> last favorite synchronization date. default value 200912010000
	 * 6 -> scrobbling method
	 *      0: schedule scrobbling without starting WAH scrobbling panel
	 *      1: schedule scrobbling and open WAH scrobbling panel
	 *      2: scrobble new songs and exit
	 *      3: scrobble new songs and start WAH panel
	 *      4: only start WAH scrobbling panel
	 */
	public static final int DATA_ELEMENTS = 8;
	
	/**
	 * Reads the data file and returns an array of String storing all variables stored
	 * @return every "data" variable in a String array
	 */
	public static String[] getData(int d) throws java.io.FileNotFoundException{
		
		String[] dati = new String[d]; 
		Scanner data;
		
		int i = 0;
		try { 
			data = new Scanner(new FileReader("data"));
			for (i = 0; i < d; i++) dati[i] = data.nextLine();
			}
		// if the file is in an old version, corrects. if it doesn't exist, ask for data.
		catch (FileNotFoundException e) { throw new FileNotFoundException(); }
		catch (java.util.NoSuchElementException e){ saveData(dati = correct(dati)); System.out.println("HEY! You had an old data file. Now it's OK"); }

		return dati;
	}
	public static String[] getData() throws FileNotFoundException {
		return getData(DATA_ELEMENTS);
	}
	
	/**
	 * save the given string array in the data file. write an error if the string array is too much long.
	 * @param data the string the program have to save
	 */
	public static void saveData (String[] data){
		if (data.length != DATA_ELEMENTS) {System.out.println("INTERNAL ERROR WRITING DATA FILE"); }
		
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter("data"));
		} catch (IOException e) {
			System.out.println("ERROR WRITING DATA FILE"); e.printStackTrace(); System.exit(1);
		}
		for (int i = 0; i < DATA_ELEMENTS; i++){ out.println(data[i]); }
		out.close();
		}
	
	/**
	 * create a valid data file from an old-rumpus data file.
	 * @return the new array
	 */
	public static String[] correct(String[] prov){
		String[] tot = new String[DATA_ELEMENTS];
		for (int i = 0; i < 4; i++) {tot[i] = prov[i];}
		
		tot[4] = "noSuchKey";   //last.fm session key
		tot[5] = "200912010000"; //last fav synch date
		tot[6] = "1"; //automatic scrobbling
		tot[7] = "";
		return tot;
		
		
	}
}
