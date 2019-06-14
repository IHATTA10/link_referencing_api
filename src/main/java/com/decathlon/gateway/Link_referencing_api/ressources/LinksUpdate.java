package com.decathlon.gateway.Link_referencing_api.ressources;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.ws.rs.core.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.decathlon.gateway.Link_referencing_api.LinkReferencingApiiApplication;
import com.decathlon.gateway.Link_referencing_api.utils.sqlUtils;

@RestController
public class LinksUpdate {
	
	/*
	 * 	Fonction appellé lors d'une tentative de mise à jours des données, en POST.
	 */
	@RequestMapping(value="/" + LinkReferencingApiiApplication.version + "/links/{id}", 
			method=RequestMethod.PUT, 
			produces=MediaType.TEXT_PLAIN)
    public ResponseEntity<String> update(@RequestParam(value="title", defaultValue="-1") String title, 
    		@RequestParam(value="language", defaultValue="-1") String language,
    		@RequestParam(value="img", defaultValue="-1") String img,
    		@RequestParam(value="url", defaultValue="-1") String url,
    		@RequestParam(value="responsible", defaultValue="-1") String responsible,
    		@RequestParam(value="tags", defaultValue="-1") String tags,
    		@RequestParam(value="department", defaultValue="-1") String department,
    		@RequestParam(value="description", defaultValue="-1") String description,
    		@RequestParam(value="type", defaultValue="-1") String type,
    		@RequestHeader(value="Authorization", required=true, defaultValue="-1") String token,
			@PathVariable("id") String id) {
				
		String o = LinkReferencingApiiApplication.verifyToken(token);	//	Contenant de retour, contient les objets Json
		String uid = "-1";							//	uid du l'utilisateur ou du responsable de l'application		
		String content = MediaType.TEXT_PLAIN;			//	Content type
		if (!o.contains("ERROR:") && !o.equals("-1"))
			uid = o;								//	Définition uid si token bon
		
		if (!uid.equals("-1")) {			//	Si token ok
			if (!id.equals("-1")) {			//	Si ID spécifié
				//	Récupération données
				String[] tagss = tags.split("_;_"),
					responsibles = responsible.split("_;_"),
					departments = department.split("_;_");
				sqlUtils su = new sqlUtils(uid);		//	Objet bdd
				
				try {
					
					//	Si on demande une update de :
					//	titre ou de description
					if (!title.equals("-1") || !description.equals("-1")) {
						if (!description.equals("-1") && !title.equals("-1")) {
							if (!language.equals("-1")) {
								ArrayList<String> langs = su.getLanguages();
								boolean continu = true;
								if (!langs.contains(language))
									continu = false;
								if (continu) {
									su.updateTitleAndDescription(title, description, id, language);
									o += "TITLE AND DESCRIPTION UPDATED;";
								} else
									o = "TITLE AND DESCRIPTION NOT UPDATED: LANGUAGE IS NOT RECOGNIZED;";
							} else
								o = "TITLE AND DESCRIPTION NOT UPDATED: YOU HAVE TO SPECIFY A LANGUAGE;";
						} else
							o = "TITLE AND DESCRIPTION NOT UPDATED: I NEED TITLE AND DESCRIPTION;";
					}
					//	D'image
					if (!img.equals("-1")) {
						try {
						    @SuppressWarnings("unused")
							URL myURL = new URL(img);
						} catch (MalformedURLException e) {		//	-4 -->	URL DIFF FORM
						    o = "IMG_URL NOT UPDATED: FORM IS NOT URL FORM;";
						}
						su.updateLinkImg(img, id);
						o += "IMG_URL UPDATED;";
					}
					//	D'url
					if (!url.equals("-1")) {
						try {
						    @SuppressWarnings("unused")
							URL myURL = new URL(img);
						} catch (MalformedURLException e) {		//	-4 -->	URL DIFF FORM
						    o = "URL NOT UPDATED: FORM IS NOT URL FORM;";
						}
						su.updateLinkImg(img, id);
						o += "URL UPDATED;";
					}
					//	De type
					if (!type.equals("-1")) {
						ArrayList<String> types = su.getTypes();
						boolean ok = false;
						for (String str : types) {
							if (str.equalsIgnoreCase(type))
								ok = true;
						}
						if (ok) {
							su.updateLinkType(type, id);
							o += "TYPE UPDATED;";
						} else {
							o = "TYPE NOT UPDATED: UNKNOW TYPE. POSSIBLE VALUES : ";
							int max = su.getTypes().size() -1;
							for (int x = 0; x < max; x++)
								o += types.get(x) + ", ";
							o += types.get(max) + ";";	
						}
					}
					// De département
					if (!department.equals("-1")) {
						ArrayList<String> departs = su.getDepartments();
						boolean continu = true;
						for (String str : departments) {
							if (!departs.contains(str))
								continu = false;
						}
						if (continu) {
							su.updateMultiValues(departments, id, sqlUtils.DEPARTMENTS, sqlUtils.INFO_DEPARTMENT_LINK_ID);
							o += "DEPARTMENTS UPDATED;";
						} else
							o = "DEPARTMENTS NOT UPDATED: ONE OF THE SPECIFIED DEPARTMENT IS NOT RECOGNIZED;";
					}
					// De tags
					if (!tags.equals("-1")) {
						su.updateMultiValues(tagss, id, sqlUtils.TAGS, sqlUtils.INFO_TAG_LINK_ID);
						o += "TAGS UPDATED;";
					}
					//	De responsable
					if (!responsible.equals("-1")) {
						su.updateMultiValues(responsibles, id, sqlUtils.RESPONSIBLE, sqlUtils.INFO_RESPONSIBLE_LINK_ID);
						o += "RESPONSIBLES UPDATED;";
					}
					
					su.updateLastModificationDate(id);	//	Update modification date
					su.close();			//	Fermeture bdd object
					
				} catch (Exception e) {
					e.printStackTrace();
					o = "null";
				}
			} else {
				o = "ERROR: NO ID SPECIFIED;";
			}
		}
			
		return ResponseEntity.ok()
		        .header("link-amount", 0 + "")
		        .header("page", 1 + "")
		        .header("page-amount", 1 + "")
		        .header("Content-Type", content)
		        .header("Content-Range", 0 + "-" + 0 + "/" + 0)
		        .header("Accept-Ranges", "links")
		        .body(o);
	}
	
}
