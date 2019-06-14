package com.decathlon.gateway.Link_referencing_api.ressources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.decathlon.gateway.Link_referencing_api.LinkReferencingApiiApplication;
import com.decathlon.gateway.Link_referencing_api.utils.JsonWriter;
import com.decathlon.gateway.Link_referencing_api.utils.sqlUtils;

@RestController
public class LinksAny {
	
	/*	
	 * 	.../links/any?value=maValeure&amount=...&page=...
	 *	Effectuer une recherche dans toutes les tables de la bdd.
	 *	Voir les 'amount' valeures de la page 'page';
	*/
	@RequestMapping(value="/" + LinkReferencingApiiApplication.version + "/links/any", 
			method=RequestMethod.GET, 
			produces="application/json")
	public ResponseEntity<String> any(@RequestParam(value="value", required=true, defaultValue="null") String value,
			@RequestParam(value="amount", defaultValue="50") String amount,
			@RequestParam(value="page", defaultValue="1") String page,
			@RequestParam(value="fields", defaultValue="-1") String back,
			@RequestHeader(value="Authorization", required=true, defaultValue="-1") String token) {
		
		String o = LinkReferencingApiiApplication.verifyToken(token);	//	Contenant de retour, contient les objets Json
		String uid = "-1";							//	uid du l'utilisateur ou du responsable de l'application		
		String content = MediaType.APPLICATION_JSON;		//	content type
		if (!o.contains("ERROR:") && !o.equals("-1"))
			uid = o;								//	UID définit si token valide
		else
			content = MediaType.TEXT_PLAIN;			//	sinon le retour sera du text (erreur)
		
		int nbr = 51, pag = 1, total = 0;
		if (!uid.equals("-1")) {					//	Si le token est bon
			try {
				nbr = Integer.parseInt(amount);		//	Récupération nombre lien/page
				pag = Integer.parseInt(page);		//	Récupération page
				if (nbr <= 50 && nbr >= 0) {		//	Si les paramètres sont valides
						
						sqlUtils su = new sqlUtils(uid);	//	Objet de controle de la bdd
						
						ArrayList<Integer> ids = new ArrayList<Integer>();  //	Liste des IDs
						ArrayList<Integer> ls = new ArrayList<Integer>();	//
						
						//	Récupération des IDs, en évitant les doublons dans les listes.
						
						//	Recherche par rapport au titre
						ids.addAll(su.getIdsByTypeLike(sqlUtils.INFO_TITLE, value));
						//	Recherchepar rapport à la description
						ids.addAll(su.getIdsByTypeLike(sqlUtils.INFO_DESC, value));
						//	Recherche par rapport aux responsables
						ids.addAll(su.getIdsByResponsible(value));
						//	Recherche par rapport aux départements
						ids.addAll(su.getIdsByDepartment(value));
						//	Recherche par rapport aux tags
						ids.addAll(su.getIdsByTag(value));
						
						//	Recherche par ID
						ArrayList<String> Value = new ArrayList<String>();
						Value.add(value);
						ids.addAll(su.getIdsByIds(Value));
						
						//	Vérification et suppression des doublons si il y en a.
						Set<Integer> set = new HashSet<Integer>();
						set.addAll(ids);
						ids = new ArrayList<Integer>(set);
						
						total = ids.size();	//	Total de liens
						
						//	Ici on s'occupe des paramètres de pagination : pag*nbr -nbr renvoie le numéro
						//		du premier lien de la page, lorsqu'il y a nbr éléments par page.
						int i = (pag*nbr) - nbr;
						while (i < ids.size() && i < pag*nbr) {
							ls.add(ids.get(i));
							i++;
						}
						
						//	Ici on remplit le contenant de retour avec du json.
						//	Ce json contient les informations des liens.
						o = JsonWriter.createArray(su.getInfosOf(ls, back.split("_;_")));
		
						su.close();		//	Fermeture de l'objet de bdd.
					
				} else 
					o = "null";
			} catch (Exception e) {
				e.printStackTrace();
				o = "null";
			}
		}
		
		int nbrPage = (int)(total/nbr);		//	Nombre de page
		if (total%nbr != 0)					//	Si le nombre de lien total % le nombre de lien par page est différent de 0 alors le nombre de page augmente de 1.
			nbrPage++;
		return ResponseEntity.ok()			//	Réponse
		        .header("link-amount", total + "")			//	Nombre de liens
		        .header("page", pag + "")					//	Page
		        .header("page-amount", nbrPage + "")		//	Nombre de pages
		        .header("Content-Type", content)			//	Content type
		        .header("Content-Range", nbr*(pag - 1)+1 + "-" + nbr*pag + "/" + total)	//	Content range
		        .header("Accept-Ranges", "links")			//	Accept Ranges
		        .header("Link", getLinks(total, nbr, pag, nbrPage, value, back))	//	Les liens suivants, précédents etc
		        .body(o);									//	Le fameux json
	}
	
	/*	
	*	Récupération des liens précéent, suivant etc
	*/	
	public String getLinks(int total, int amount, int page, int nbrPage, String value, String back) {
		
		String base = "/link_referencing_api/v1/links?value=" + value;
		if (!back.equals("-1"))
			base += "&fields=" + back;
		base += "&amount=" + amount + "&page=";
		
		String regex = ", ",
				links = "",
				first = base + 1,		//	Premier lien
				previous = base,		//	Lien précédent
				self = base + page,		//	Lien de la page
				next = base,			//	Lien suivant
				last = base + nbrPage;	//	Dernier lien
		
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
	
}
