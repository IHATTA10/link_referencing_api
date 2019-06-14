package com.decathlon.gateway.Link_referencing_api.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Dump {
	
	private File f;
	private ArrayList<String> data = new ArrayList<String>();
	
	public Dump(File f) {
		this.f = f;
	}
	
	public int getDataAmount() throws Exception {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		while (br.ready())
			data.add(br.readLine());
		br.close();
		
		ArrayList<String> Data = new ArrayList<String>();
		for (String str : data) {
			Data.add(str.replaceAll(",NULL", ",'NULL"));
		}
		data = Data;
		
		return data.size();
	}
	
	public void prepareData() {
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		ArrayList<String> resu = new ArrayList<String>(), bad = new ArrayList<String>();
		for (String str : data) {
			String id = "id";
			String infos[] = new String[18];
			String cache[] = str.split(",'");
			id = cache[0];
			infos[0] = getStructData(cache[1]);
			infos[1] = getStructData(cache[3]);
			int ii = 2;
			for (int i = 7; i < 23; i++) {
				infos[ii] = getStructData(cache[i]);
				ii++;
			}
			ArrayList<String> json = new ArrayList<String>();
			json.add("{");
			json.add("\"department\": \"decath\",");
			if (infos[3].equals("NULL") && infos[4].equals("NULL"))
				json.add("\"responsible\": \"NULL\",");
			else if (infos[3].equals("NULL") && !infos[4].equals("NULL"))
				json.add("\"responsible\": \"" + infos[4] + "\",");
			else if (!infos[3].equals("NULL") && infos[4].equals("NULL"))
				json.add("\"responsible\": \"" + infos[3] + "\",");
			else if (!infos[3].equals("NULL") && !infos[4].equals("NULL"))
				json.add("\"responsible\": \"" + infos[3] + "_;_" + infos[4] + "\",");
			
			if (infos[6].isEmpty())
				json.add("\"img\": \"http://rien.fr\",");
			else
				json.add("\"img\": \"" + infos[6] + "\",");
			if (infos[1].isEmpty())
				json.add("\"url\": \"http://rien.fr\",");
			else
				json.add("\"url\": \"" + infos[1] + "\",");
			if (infos[16].isEmpty())
				json.add("\"type\": \"null\",");
			else if (infos[16].equals("tool"))
				json.add("\"type\": \"application\",");
			else
				json.add("\"type\": \"" + infos[16] + "\",");
			json.add("\"tags\": \"" + infos[5].replace(", ", "_;_") + "\",");	
			
			boolean fr = false;
			if (!infos[7].isEmpty() && !infos[7].equals(null))
				fr = true;
			boolean en = false;
			if (!infos[2].isEmpty() && !infos[2].equals("NULL"))
				en = true;
			boolean it = false;
			if (!infos[8].isEmpty() && !infos[8].equals("NULL"))
				it = true;
			boolean ru = false;
			if (!infos[9].isEmpty() && !infos[9].equals("NULL"))
				ru = true;
			boolean es = false;
			if (!infos[10].isEmpty() && !infos[10].equals("NULL"))
				es = true;
			boolean port = false;
			if (!infos[11].isEmpty() && !infos[11].equals("NULL"))
				port = true;
			boolean ger = false;
			if (!infos[12].isEmpty() && !infos[12].equals("NULL"))
				ger = true;
			boolean cn = false;
			if (!infos[13].isEmpty() && !infos[13].equals("NULL"))
				cn = true;
			boolean zh = false;
			if (!infos[14].isEmpty() && !infos[14].equals("NULL"))
				zh = true;
			boolean tr = false;
			if (!infos[15].isEmpty() && !infos[15].equals("NULL"))
				tr = true;
			boolean nl = false;
			if (!infos[17].isEmpty() && !infos[17].equals("NULL"))
				nl = true;
			
			String languages = "";
			String titles = "";
			String descriptions = "";
			
			if (fr || en) {
				languages += "fr_;_en";
				titles += infos[0] + "_;_" + infos[0];
				if (fr && en)
					descriptions += infos[7] + "_;_" + infos[2];
				else if (fr)
					descriptions += infos[7] + "_;_" + infos[7];
				else if (en)
					descriptions += infos[2] + "_;_" + infos[2];
				
				if (it) {
					languages += "_;_it";
					titles += "_;_" + infos[0];
					descriptions += "_;_" + infos[8];
				}
				if (ru) {
					languages += "_;_ru";
					titles += "_;_" + infos[0];
					descriptions += "_;_" + infos[9];
				}
				if (es) {
					languages += "_;_es";
					titles += "_;_" + infos[0];
					descriptions += "_;_" + infos[10];
				}
				if (port) {
					languages += "_;_pl";
					titles += "_;_" + infos[0];
					descriptions += "_;_" + infos[11];
				}
				if (ger) {
					languages += "_;_de";
					titles += "_;_" + infos[0];
					descriptions += "_;_" + infos[12];
				}
				if (cn) {
					languages += "_;_cn";
					titles += "_;_" + infos[0];
					descriptions += "_;_" + infos[13];
				}
				if (zh) {
					languages += "_;_zh";
					titles += "_;_" + infos[0];
					descriptions += "_;_" + infos[14];
				}
				if (tr) {
					languages += "_;_tr";
					titles += "_;_" + infos[0];
					descriptions += "_;_" + infos[15];
				}
				if (nl) {
					languages += "_;_nl";
					titles += "_;_" + infos[0];
					descriptions += "_;_" + infos[17];
				}
				
			}
			
			json.add("\"title\": \"" + titles + "\",");
			json.add("\"description\": \"" + descriptions + "\",");
			json.add("\"languages\": \"" + languages + "\"");
			json.add("}");
			
			String to = "";
			for (String j : json)
				to += j;
						
			ResponseEntity<String> res = register("-1", "ihatta10", to);
			
			if (!res.getBody().contains("REGISTERED"))
				bad.add(id);
			
			if (!result.containsKey(res.getBody())) {
				resu.add(res.getBody());
				result.put(res.getBody(), 1);
			} else
				result.replace(res.getBody(), result.get(res.getBody()) + 1);
		}
		int invalid = 0;
		for (String str : resu) {
			System.out.println(str + ": " + result.get(str));
			if (!str.equals("REGISTERED"))
				invalid += result.get(str);
		}
		for (String str : bad)
			System.out.println(str);
		System.out.println("valid: " + result.get("REGISTERED"));
		System.out.println("invalid: " + invalid);
	}
	
	@Override
	public String toString() {
		String to = "Error";
		try {
			to = "Informations retenus :\n\n"
					+ "[Fichier] \"" + f.getName() + "\"\n"
					+ "\tChemin : " + f.getAbsolutePath() + "\n"
					+ "\tTaille : " + f.length() + " octets\n"
					+ "\tTotal de donn�es � transf�rer : " + getDataAmount() + " liens\n\n";
			prepareData();
		} catch (Exception e) {e.printStackTrace();}
		return to;
	}
	
	private String getStructData(String strr) {
		if (strr.lastIndexOf("'") == strr.length() -1) {
			String tmp = "";
			for (int i = 0; i < strr.length()-1; i++)
				tmp += strr.toCharArray()[i];
			strr = tmp;
		}
		return strr;
	}
	
	public ResponseEntity<String> register(
			String backk,
			String uid,
			String value) {
String o = "";
try {
	ObjectMapper mapper = new ObjectMapper();
	mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true); 
    JsonNode json = mapper.readTree(value);
	
	String tags = json.get("tags").toString().replace("\"", ""),
			responsible = json.get("responsible").toString().replace("\"", ""),
			department = json.get("department").toString().replace("\"", ""),
			title = json.get("title").toString().replace("\"", ""),
			description = json.get("description").toString().replace("\"", ""),
			language = "fr_;_en",
			type = json.get("type").toString().replace("\"", ""),
			img = json.get("img").toString().replace("\"", ""),
			url = json.get("url").toString().replace("\"", "");
	if (json.has("languages"))
		language = json.get("languages").toString().replace("\"", "");

	String[] tagss = tags.split("_;_"),
		responsibles = responsible.split("_;_"),
		departments = department.split("_;_"),
		titles = title.split("_;_"),
		descriptions = description.split("_;_"),
		languages = language.split("_;_"),
		back = backk.split("_;_");
	sqlUtils su = new sqlUtils(uid);
	
	if (o.equals("")) {
		if (!uid.equals("-1")) {
			try {
				ArrayList<String> types = su.getTypes();
				int i = checkRegisterParameters(titles, descriptions, languages, type, img, url, 
						responsibles, departments, su.getDepartments(), su.getLanguages(), types);
				switch (i) {
				case 1:
					int id = su.registerLink(type, img, url);
					if (id == 0)
						o = "ERROR: THE URL USED ALREADY EXISTS.";
					else {
						su.registerDepartments(departments, id);
						su.registerResponsibles(responsibles, id);
						su.registerTags(tagss, id);
						su.registerTitleAndDescription(titles, descriptions, languages, id);
						o = "REGISTERED";
						
						if (!back[0].equals("-1")) {
							ArrayList<Integer> ls = new ArrayList<Integer>();
							ls.add(id);
							o = JsonWriter.createArray(su.getInfosOf(ls, back));
						}
						su.updateLastModificationDate(id + "");
					}
					break;
				case -1:
					o = "ERROR: WITHOUT LANGUAGES SPECIFIED YOU HAVE TO DEFINE TWO TITLES. LIKE: '[monTitle],[myTitle]'.";
					break;
				case -2:
					o = "ERROR: WITHOUT LANGUAGES SPECIFIED YOU HAVE TO DEFINE TWO DESCRIPTIONS. LIKE: '[maDesc],[myDesc]'.";
					break;
				case -3:
					o = "ERROR: UNKNOW TYPE \"" + type + "\". POSSIBLE VALUES : ";
					int max = su.getTypes().size() -1;
					for (int x = 0; x < max; x++)
						o += types.get(x) + ", ";
					o += types.get(max) + ".";
					break;
				case -4:
					o = "ERROR: THE URL IS WRONG. VERIFY IT.";
					break;
				case -5:
					o = "ERROR : LANGUAGES GOT ARE WRONG. VERIFY THEM.";
					break;
				case -6:
					o = "ERROR: THE AMOUNT OF TITLES ARE DIFFERENT THAT THE AMOUNT OF LANGUAGES.";
					break;
				case -7:
					o = "ERROR: THE AMOUNT OF DESCRIPTIONS ARE DIFFERENT THAT THE AMOUNT OF LANGUAGES.";
					break;
				case -8:
					o = "ERROR: THERE IS NO RESPONSIBLE.";
					break;
				case -9:
					o = "ERROR: THERE IS NO DEPARTMENT.";
					break;
				case -10:
					o = "ERROR: DEPARTMENT(S) IS/ARE NOT REGISTERED.";
					break;
				case -11:
					o = "ERROR: THE LANGUAGES HAVE TO CONTAIN 'FR' AND 'EN'.";
					break;
				default:
					break;
				}
				
				su.close();
			} catch (Exception e) {e.printStackTrace();o = "null";}
		} else
			o = "ERROR: YOU HAVE TO SPECIFY A UID.";
		
		return ResponseEntity.ok()
		        .header("link-amount", 0 + "")
		        .header("page", 1 + "")
		        .header("page-amount", 1 + "")
		        .header("Content-Type", MediaType.APPLICATION_JSON)
		        .header("Content-Range", 0 + "-" + 0 + "/" + 0)
		        .header("Accept-Ranges", "links")
		        .body(o);
	}
} catch (Exception e) {e.printStackTrace();o = "THERE IS A NUMBER OF PARAMETERS YOU HAVE TO ENTRY !";}
return ResponseEntity.ok()
        .header("link-amount", 0 + "")
        .header("page", 1 + "")
        .header("page-amount", 1 + "")
        .header("Content-Type", MediaType.APPLICATION_JSON)
        .header("Content-Range", 0 + "-" + 0 + "/" + 0)
        .header("Accept-Ranges", "links")
        .body(o);
}

/*
* 	Récupère plusieurs variables (listes et chaines), toutes issue des paramètres
* 		de la fonction 'register'.
* 	Cette fonction vérifie que les paramètres sont valides, et detecte l'erreur
* 		si il y en a une.
*/
private int checkRegisterParameters(String[] title, String[] description, String[] language, String type, String img,
	String url, String[] responsible, String[] department, ArrayList<String> departs,
	ArrayList<String> languages, ArrayList<String> types) throws Exception {
if (!(responsible.length > 0))		//		-8	-->		NO RESP
	return -8;

if (!(department.length > 0))		//		-9	-->		NO DEPARTMENT
	return -9;

boolean ok = false;
for (String typ : types) {
	if (typ.equalsIgnoreCase(type))
		ok = true;
}
if (!ok)
	return -3;							//	-3 -->	TYPE DIFF

try {
    @SuppressWarnings("unused")
	URL myURL = new URL(url);
} catch (MalformedURLException e) {		//	-4 -->	URL DIFF FORM
    return -4;
}
for (String str : department) {
	if (!departs.contains(str))
		return -10;					//	-10	-->		UNKNOW DEPARTMENT
}
if (language[0].equals("-1")) {
	if (title.length != 2)			//	-1 	-->		TITRES DIFF 2
		return -1;
	if (description.length != 2)		//	-2	-->		DESCS DIFF 2
		return -2;
	return 1;
} else {
	for (String str : language) {
		if (!languages.contains(str))
			return -5;							//	-5	-->		LANGUAGE NOT FOUND
	}
	if (title.length != language.length)		//	-6 	-->		TITRES DIFF LANGUES
		return -6;
	if (description.length != language.length)	//	-7	-->		DESCS DIFF LANGUES
		return -7;
	
	return 1;
}
}
	
}
