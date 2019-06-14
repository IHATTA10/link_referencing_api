package com.decathlon.gateway.Link_referencing_api.ressources;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.ws.rs.core.MediaType;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.decathlon.gateway.Link_referencing_api.LinkReferencingApiiApplication;
import com.decathlon.gateway.Link_referencing_api.utils.JsonWriter;
import com.decathlon.gateway.Link_referencing_api.utils.sqlUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class LinksRegister {
	
	/*
	 * 	Fonction appellé lors d'une tentative d'enregistrement de liens.
	 */
	@RequestMapping(value="/" + LinkReferencingApiiApplication.version + "/links", 
			method=RequestMethod.POST, 
			produces=MediaType.TEXT_PLAIN, 
			consumes="application/json")
	public ResponseEntity<String> register(
					@RequestParam(value="fields", defaultValue="-1") String backk,
					@RequestHeader(value="Authorization", required=true, defaultValue="-1") String token,
					@RequestBody String value) {
		
		String o = LinkReferencingApiiApplication.verifyToken(token);	//	Contenant de retour, contient les objets Json
		String uid = "-1";							//	uid du l'utilisateur ou du responsable de l'application		
		String content = MediaType.APPLICATION_JSON;		//	Content type
		if (!o.contains("ERROR:") && !o.equals("-1"))		
			uid = o;								//	Définition de l'uid si le token est bon
		else
			content = MediaType.TEXT_PLAIN;			//	Sinon le retour est du text
		
		try {
			ObjectMapper mapper = new ObjectMapper();		//	Objet pour lire le json
		    JsonNode json = mapper.readTree(value);			//	Récupèration du json sous forme d'objet
			
			//	Récupération des paramètres
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
			
			//	Split des paramètres qui peuvent contenir plusieurs valeures
			String[] tagss = tags.split("_;_"),
				responsibles = responsible.split("_;_"),
				departments = department.split("_;_"),
				titles = title.split("_;_"),
				descriptions = description.split("_;_"),
				languages = language.split("_;_"),
				back = backk.split("_;_");
			sqlUtils su = new sqlUtils(uid);		//	Objet de gestion de bdd
			
			if (!uid.equals("-1")) {				//	Si token bon
				try {
					ArrayList<String> types = su.getTypes();		//	Récupération des types de la bdd
					//	Vérification des paramètres. 'i' contient un code : erreur ou ok
					int i = checkRegisterParameters(titles, descriptions, languages, type, img, url, 
							responsibles, departments, su.getDepartments(), su.getLanguages(), types);
					//	Selon le code
					switch (i) {
					case 1:		//	OK
						//	On tente d'enregistrer les informations de base du lien
						int id = su.registerLink(type, img, url);
						if (id == 0)	//	Si 0 alors le lien est déjà utilisé
							o = "ERROR: THE URL USED ALREADY EXISTS.";
						else {			//	Si autre, tout va bien !	
							su.registerDepartments(departments, id);	//	On enregistre les departements
							su.registerResponsibles(responsibles, id);	//	On enregistre les responsables
							su.registerTags(tagss, id);					//	On enregistre les tags
							su.registerTitleAndDescription(titles, descriptions, languages, id);	//	Puis les tires et les descriptions dans les langues ...
							o = "REGISTERED";		//	REGISTERED
							
							if (!back[0].equals("-1")) {		//	Si le param fields est spécifié
								ArrayList<Integer> ls = new ArrayList<Integer>();
								ls.add(id);
								o = JsonWriter.createArray(su.getInfosOf(ls, back));	//	Récupération des données demandés
							}
							su.updateLastModificationDate(id + "");			//	Mise à jours de la date de modification
						}
						break;
					case -1:
						o = "ERROR: WITHOUT LANGUAGES SPECIFIED YOU HAVE TO DEFINE TWO TITLES. LIKE: '[monTitle],[myTitle]'.";
						break;
					case -2:
						o = "ERROR: WITHOUT LANGUAGES SPECIFIED YOU HAVE TO DEFINE TWO DESCRIPTIONS. LIKE: '[maDesc],[myDesc]'.";
						break;
					case -3:
						o = "ERROR: UNKNOW TYPE. POSSIBLE VALUES : ";
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
					
					su.close();		//	Fermeture objet bdd
				} catch (Exception e) {e.printStackTrace();o = "null";}
			}
			
			//	Différentes réponses possibles
			return ResponseEntity.ok()
			        .header("link-amount", 0 + "")
			        .header("page", 1 + "")
			        .header("page-amount", 1 + "")
			        .header("Content-Type", content)
			        .header("Content-Range", 0 + "-" + 0 + "/" + 0)
			        .header("Accept-Ranges", "links")
			        .body(o);
	    } catch (Exception e) {o = "THERE IS A NUMBER OF PARAMETERS YOU HAVE TO ENTRY !";}
		return ResponseEntity.ok()
		        .header("link-amount", 0 + "")
		        .header("page", 1 + "")
		        .header("page-amount", 1 + "")
		        .header("Content-Type", content)
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
		//	On vérifie qu'il y a au moins un responsable
		if (!(responsible.length > 0))		//		-8	-->		NO RESP
			return -8;
		
		//	On vérifie qu'il y a au moins un département
		if (!(department.length > 0))		//		-9	-->		NO DEPARTMENT
			return -9;
		
		//	On vérifie que le type est enregistré dans la bdd
		boolean ok = false;
		for (String typ : types) {
			if (typ.equalsIgnoreCase(type))
				ok = true;
		}
		if (!ok)
			return -3;							//	-3 -->	TYPE DIFF
		
		//	On vérifie que les url sont bien des url
		try {
		    @SuppressWarnings("unused")
			URL myURL = new URL(url);
			URL myyURL = new URL(img);
		} catch (MalformedURLException e) {		//	-4 -->	URL DIFF FORM
		    return -4;
		}
		
		// On vérifie que les départements entrés sont bien enregistrés sur la bdd
		for (String str : department) {
			if (!departs.contains(str))
				return -10;					//	-10	-->		UNKNOW DEPARTMENT
		}
		
		//	si le param language n'est pas spécifié
		if (language[0].equals("-1")) {
			//	On vérifie qu'il y a bien au minimum deux titres (fr et en)
			if (title.length != 2)			//	-1 	-->		TITRES DIFF 2
				return -1;
			//	On vérifie qu'il y a bien au minimum deux descriptions (fr et en)
			if (description.length != 2)		//	-2	-->		DESCS DIFF 2
				return -2;
			return 1;
		} else { //	Si il l'est
			//	On vérifie que tous les langages existent dans la base de données
			for (String str : language) {
				if (!languages.contains(str))
					return -5;							//	-5	-->		LANGUAGE NOT FOUND
			}
			//	On vérifie qu'il y a le même nombre de descriptions, de titres, et de langues
			if (title.length != language.length)		//	-6 	-->		TITRES DIFF LANGUES
				return -6;
			if (description.length != language.length)	//	-7	-->		DESCS DIFF LANGUES
				return -7;
			
			return 1;
		}
	}
	
}
