package com.decathlon.gateway.Link_referencing_api.ressources;

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
import com.decathlon.gateway.Link_referencing_api.utils.JsonWriter;
import com.decathlon.gateway.Link_referencing_api.utils.sqlUtils;

@RestController
public class LinksSearch {
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/" + LinkReferencingApiiApplication.version + "/links/{id}", 
			method=RequestMethod.GET, 
			produces="application/json")
	public ResponseEntity<String> getSearchID(@RequestHeader(value="Authorization", required=true, defaultValue="-1") String token,
					@PathVariable("id") String ides,
					@RequestParam(value="fields", defaultValue="-1") String back) {		
		
		String o = LinkReferencingApiiApplication.verifyToken(token);	//	Contenant de retour, contient les objets Json
		String uid = "-1";							//	uid du l'utilisateur ou du responsable de l'application		
		String content = MediaType.APPLICATION_JSON;	//	Content type
		if (!o.contains("ERROR:") && !o.equals("-1"))
			uid = o;								//	Définition uid si token bon
		else
			content = MediaType.TEXT_PLAIN;			//	Sinon retour = text
		
		int total = 0;
		sqlUtils su = null;
		if (!uid.equals("-1")) {					//	Si token bon
			//if (!ides.equals("")) {
				try {
					ArrayList<String> ids = getIDs(ides);	//	Récupération des IDs contenues dans le lien
					ArrayList<Integer> Ids = new ArrayList<Integer>();
					su = new sqlUtils(uid);	//	Objet gestion bdd
					
					Ids = (ArrayList<Integer>)(su.getIdsByIds(ids));	//	Récupération des IDs existants
					
					total = Ids.size();		//	Total de liens
					//	Récupération des données
					o = JsonWriter.createArray(su.getInfosOf(Ids, back.split("_;_")));
					su.close();			//	Fermeture objet bdd
				} catch (Exception e) {
					o = "ERROR";
					e.printStackTrace();
				}
				//	Réponse
			    return ResponseEntity.ok()
				        .header("link-amount", total + "")
				        .header("page", 1 + "")
				        .header("page-amount", 1 + "")
				        .header("Content-Type", content)
				        .header("Content-Range", 1 + "-" + total + "/" + total)
				        .header("Accept-Ranges", "links")
				        .body(o);
			//} else
				//o = "ERROR: YOU HAVE TO SPECIFY AN ID.";
		}
		return ResponseEntity.ok()
		        .header("link-amount", total + "")
		        .header("page", 1 + "")
		        .header("page-amount", 1 + "")
		        .header("Content-Type", content)
		        .header("Content-Range", 0 + "-" + 0 + "/" + 0)
		        .header("Accept-Ranges", "links")
		        .body(o);
	}
	
	/*	
	 * 	.../links/search?title=Title_or_poart_of_title&language=<fr;en;zh;...>
	 *	Si language n'est pas définit (.../links/search?title=Title_or_poart_of_title), il 
	 *		prend 'fr' pour valeure par défaut.
	 *	Recherche en fonction du titre dans la langue selectionné si elle a été définit.
	 *	Renvoie "null" si invalide ou vide.
	*/
	@RequestMapping(value="/" + LinkReferencingApiiApplication.version + "/links/search", 
			method=RequestMethod.GET, 
			produces="application/json")
    public ResponseEntity<String> getSearch(@RequestParam(value="title", defaultValue="-1") String title, 
    					@RequestParam(value="amount", defaultValue="50") String amount,
    					@RequestParam(value="page", defaultValue="1") String page,
    					@RequestParam(value="responsible", defaultValue="-1") String responsibles,
    					@RequestParam(value="tags", defaultValue="-1") String tags,
    					@RequestParam(value="department", defaultValue="-1") String departments,
    					@RequestParam(value="description", defaultValue="-1") String description,
    					@RequestParam(value="type", defaultValue="-1") String type,
    					@RequestParam(value="fields", defaultValue="-1") String back,
						@RequestHeader(value="Authorization", required=true, defaultValue="-1") String token) {
		String o = LinkReferencingApiiApplication.verifyToken(token);	//	Contenant de retour, contient les objets Json
		String uid = "-1";							//	uid du l'utilisateur ou du responsable de l'application		
		String content = MediaType.APPLICATION_JSON;	//	Content type
		if (!o.contains("ERROR:") && !o.equals("-1"))
			uid = o;							//	définition uid si token ok
		else
			content = MediaType.TEXT_PLAIN;		//	sinon retour = text
		
		int total = 0;
		sqlUtils su = null;
		if (!uid.equals("-1")) {				//	Si token ok
			int nbr = 51, pag = 1;
			try {
				nbr = Integer.parseInt(amount);	//	Récupération params pagination
				pag = Integer.parseInt(page);
			} catch (Exception e) {
				e.fillInStackTrace();
				o = "null";
			}
			if (nbr <= 50 && nbr >= 0) {
				try {
					su = new sqlUtils(uid);		//	Objet gestion bdd
					
					//	Préparation des liste qui contiendront les données pour chaque recherche
					ArrayList<String> responsiblesLinksUIDs = new ArrayList<String>(),
							tagsLinksUIDs = new ArrayList<String>(),
							departmentsLinksUIDs = new ArrayList<String>(),
							titleLinksIDs = new ArrayList<String>(),
							descriptionLinkIDs = new ArrayList<String>(),
							typeLinkIDs = new ArrayList<String>(),
							tmp;
					
					/*
					 * 	Récupération des ID en rapport avec les paramètres
					 */
					
					if (!responsibles.equals("-1")) {		//	Responsable
						responsiblesLinksUIDs = su.getIdsByMultipleValues(sqlUtils.INFO_RESPONSIBLE_LINK_ID, 
																	sqlUtils.INFO_RESPONSIBLE_UID, 
																	sqlUtils.RESPONSIBLE, 
																	responsibles.split("_;_"));
					}
					
					if (!tags.equals("-1")) {				//	Tags
						tagsLinksUIDs = su.getIdsByLikeMultipleValues(sqlUtils.INFO_TAG_LINK_ID, 
																	sqlUtils.INFO_TAG, 
																	sqlUtils.TAGS, 
																	tags.split("_;_"));
					}
					
					if (!departments.equals("-1")) {		//	Departements
						departmentsLinksUIDs = su.getIdsByMultipleValues(sqlUtils.INFO_DEPARTMENT_LINK_ID, 
																	sqlUtils.INFO_DEPARTMENT, 
																	sqlUtils.DEPARTMENTS, 
																	departments.split("_;_"));
					}
					
					if (!title.equals("_title"))			//	Titre
						titleLinksIDs = su.getIdsByTypeLikeARRAY(sqlUtils.INFO_TITLE, title);
					
					if (!description.equals("_description"))	//	Description
						descriptionLinkIDs = su.getIdsByTypeLikeARRAY(sqlUtils.INFO_DESC, description);
					
					tmp = new ArrayList<String>();		//	Liste temporaire
					ArrayList<String> fina = new ArrayList<String>();	//	Liste finale
					
					/*
					 *  Combinaison des paramètres spécifiés
					 */
					
					fina = checkParam(title, fina, titleLinksIDs);
					fina = checkParam(description, fina, descriptionLinkIDs);
					fina = checkParam(tags, fina, tagsLinksUIDs);
					fina = checkParam(responsibles, fina, responsiblesLinksUIDs);
					fina = checkParam(departments, fina, departmentsLinksUIDs);
					
					//	Si le paramètre type est spécifié on effectue une recherche par type, ce qui
					//		est fait en dernier pour être sur du résultat.
					if (!type.equals("-1")) {
						typeLinkIDs = su.getIdsByMultipleValues(sqlUtils.INFO_LINK_ID, 
								sqlUtils.INFO_LINK_TYPE, 
								sqlUtils.LINKS, 
								type.split("_;_"));
						if (fina.isEmpty())
							fina = typeLinkIDs;
						else {
							tmp = fina;
							fina = new ArrayList<String>();
							for (String str : typeLinkIDs) {
								if (tmp.contains(str))
									fina.add(str);
							}
						}
					}
					
					ArrayList<Integer> ls = new ArrayList<Integer>();
					 
					for (String str : fina) {
						 try {
							 ls.add(Integer.parseInt(str));
						 } catch (Exception e) {}
					}
					 
					ArrayList<Integer> lss = new ArrayList<Integer>();
					
					for (int ii : ls) {
						if (!lss.contains(ii))
							lss.add(ii);
					}
					
					total = lss.size();
					
					
					//	Pagination
					int i = (pag*nbr) - nbr;
					while (i < lss.size() && i < pag*nbr) {
							ls.add(lss.get(i));
							i++;
					}
					
					o = JsonWriter.createArray(su.getInfosOf(ls, back.split("_;_")));
					
					su.close();
					
				} catch (Exception e) {
				      e.printStackTrace();
				}
			} else
				o = "null";
			
			int nbrPage = (int)(total/nbr);
			if (total%nbr != 0)
				nbrPage++;
			//	Réponses
			return ResponseEntity.ok()
			        .header("link-amount", total + "")
			        .header("page", pag + "")
			        .header("page-amount", nbrPage + "")
			        .header("Content-Type", MediaType.APPLICATION_JSON)
			        .header("Content-Range", nbr*(pag - 1)+1 + "-" + nbr*pag + "/" + total)
			        .header("Accept-Ranges", "links")
			        .header("Link", getLinks(total, nbr, pag, nbrPage, title, back, 
			        		responsibles, tags, departments, description, type))
			        .body(o);
		}
		return ResponseEntity.ok()
		        .header("link-amount", total + "")
		        .header("page", 1 + "")
		        .header("page-amount", 1 + "")
		        .header("Content-Type", content)
		        .header("Content-Range", 1 + "-" + total + "/" + total)
		        .header("Accept-Ranges", "links")
		        .body(o);
	}
	
	private String getLinks(int total, int amount, int page, int nbrPage, String title, String back, String responsibles,
			String tags, String departments, String description, String type) {
		
		String base = "/link_referencing_api/v1/links?amount=" + amount;
		
		base += checkParamForLink(back, "fields");
		base += checkParamForLink(title, "title");
		base += checkParamForLink(description, "description");
		base += checkParamForLink(responsibles, "responsible");
		base += checkParamForLink(tags, "tags");
		base += checkParamForLink(departments, "department");
		base += checkParamForLink(type, "type");
		
		base += "&page=";
		
		String regex = ", ",
				links = "",
				first = base + 1,
				previous = base,
				self = base + page,
				next = base,
				last = base + nbrPage;
		
		if (page > 1)
			previous += (page - 1);
		else
			previous += 1;
		
		if (page < nbrPage)
			next += (page + 1);
		else
			next += page;
		
		links = first + regex
				+ previous + regex
				+ self + regex
				+ next + regex
				+ last;
		
		return links;
	}
	
	/*	
	*	La méthode getLinks est plus complexe que dans les autres méthodes. Ici il y a plus de paramètres.
	*	Cette méthode permet de vérifier si un paramètre a été spécifié ou non, pour savoir lesquelles mettre
	*		dans les liens.
	*/	
	private String checkParamForLink(String value, String param) {
		String to = "";
		
		if (!value.equals("-1"))		//	Si le param n'est pas vide
			to += "&" + param + "=" + value;	
		
		return to;
	}
	
	/*	
	*	Cette méthode accepte une liste de liens.
	*	Elle renvoi une liste de 50 lien max.
	*/	
	private static ArrayList<String> getIDs(String ides) {
		ArrayList<String> ids = new ArrayList<String>();
		String[] idss = ides.split(",");
		for (String str : idss) {
			if (ids.size() < 50)
				ids.add(str);
			else
				break;
		}
		return ids;
	}
	
	/*
	 *  Récupération d'une liste d'ID correspondant à la combinaison de deux
	 *  	ArrayList, celles que l'ont passe en paramètre.
	 *  'fina' est renvoyé. Et est modifié si le paramètre n'est pas vide.
	 */
	private ArrayList<String> checkParam(String tocheck, ArrayList<String> fina, ArrayList<String> melt) {
		ArrayList<String> tmp = new ArrayList<String>();
		if (!tocheck.equals("-1")) {
			if (fina.isEmpty())
				fina = melt;
			else {
				tmp = fina;
				fina = new ArrayList<String>();
				for (String str : melt) {
					if (tmp.contains(str))
						fina.add(str);
				}
			}
		}
		return fina;
	}
	
}
