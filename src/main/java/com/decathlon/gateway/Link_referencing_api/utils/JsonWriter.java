package com.decathlon.gateway.Link_referencing_api.utils;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;


public class JsonWriter {
	
	//ArrayList<JsonObject> array = new ArrayList<JsonObject>();
	
	public JsonWriter() {}
	
	public void addObjectToArray(/*JsonObject obj*/) {
		//array.add(obj);
	}
	
	/*public String createArrayWithData() {
		String to = "{\"links\":[";
		int lastElement;
		if (array.size() > 0) {
			lastElement = array.size() - 1;
			for (int i = 0; i < lastElement; i++)
				to += array.get(i).toString() + ",";
			to += array.get(lastElement);
			to += "]}";
		} else {
			to = "null";
		}
		return to;
	}*/
	
	public static String createArray(ArrayList<String> content) {
		String to = "{\"links\":[";
		int lastElement;
		if (content.size() > 0) {
			lastElement = content.size() - 1;
			for (int i = 0; i < lastElement; i++)
				to += content.get(i) + ",";
			to += content.get(lastElement);
			to += "]}";
		} else {
			to = "null";
		}
		return to;
	}
	
	public static void createArrayInFile(File destination, ArrayList<String/*JsonObject*/> content) throws Exception {
		if (destination.exists())
			destination.delete();
		destination.createNewFile();
		
		PrintWriter pw = new PrintWriter(destination);
		
		pw.print("{\"links\":[");
		int lastElement;
		if (content.size() > 0) {
			lastElement = content.size() - 1;
			for (int i = 0; i < lastElement; i++)
				pw.print(content.get(i).toString() + ",");
			pw.print(content.get(lastElement));
			pw.print("]}");
		} else
			pw.println("null");
		pw.close();
	}

	public void/*int*/ getAmountOfObjectsInData() {
		//return array.size();
	}
	
}
