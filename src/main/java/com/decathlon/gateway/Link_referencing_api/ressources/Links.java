package com.decathlon.gateway.Link_referencing_api.ressources;

import java.util.ArrayList;

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
public class Links {
		
	/*
	 * 	.../links
	 *	Liste des "amount" premiers liens de la page "page". La taille d'une page = "amount".
	*/
	@RequestMapping(value="/" + LinkReferencingApiiApplication.version + "/links", 
			method=RequestMethod.GET, 
			produces="application/json")
    public ResponseEntity<String> getLinksList(@RequestParam(value="amount", defaultValue="50") String amount,
    						@RequestParam(value="page", defaultValue="1") String page,
    						@RequestHeader(value="fields", defaultValue="-1") String back,
    						@RequestHeader(value="Authorization", defaultValue="-1") String token) {
		
		String o = LinkReferencingApiiApplication.verifyToken(token);	//	Contenant de retour, contient les objets Json
		String uid = "-1";							//	uid du l'utilisateur ou du responsable de l'application		
		String content = MediaType.APPLICATION_JSON;	//	Content type
		if (!o.contains("ERROR:") && !o.equals("-1"))
			uid = o;								//	L'uid est définit si il le token est bon.	
		else
			content = MediaType.TEXT_PLAIN;			//	Sinon le retour sera du text
		
		int nbr = 51, pag = 1, total = 0;			//Variables de page de liens : Taille, Numéro et Total
		if (!uid.equals("-1")) {
			try {
				//	Vérification des paramètres
				nbr = Integer.parseInt(amount);
				pag = Integer.parseInt(page);
				//	Vérification de la taille des pages (max = 50, min = 0)
				if (nbr <= 50 && nbr >= 0) {
					
					//	Création d'un objet utilitaire pour les requêtes
					sqlUtils su = new sqlUtils(uid);
					//	Récupération nombre de liens total
					total = su.countAllFor(sqlUtils.LINKS); 
					//	Récupération des IDs des liens désirés selon la pagination
					ArrayList<Integer> ids = su.getIdArrayByLimits(pag, nbr);
					//	Ajout des données récupérés au contenant de retour
					o = JsonWriter.createArray(su.getInfosOf(ids, back.split("_;_")));
					su.close();
				} else
					o = "null";
			} catch (Exception e) {
				//	Si erreur, le contenant du Json est définit sur null
				e.printStackTrace();
				o = "null";
			}
		}
		/* 
		 * 		HEADER
		 * 	links_amount: 'total'
		 * 	page: 'pag'
		 * 	pages_amount: 'nbr'/'total' +1
		 */
		int nbrPage = (int)(total/nbr);
		if (total%nbr != 0)
			nbrPage++;
		return ResponseEntity.ok()
		        .header("link-amount", total + "")
		        .header("page", pag + "")
		        .header("page-amount", nbrPage + "")
		        .header("Content-Type", content)
		        .header("Content-Range", nbr*(pag - 1)+1 + "-" + nbr*pag + "/" + total)
		        .header("Accept-Ranges", "links")
		        .header("Link", getLinks(total, nbr, pag, nbrPage))
		        .body(o);
	}
	
	/*	
	*	Récupération des liens suivant, précédent, etc
	*/	
	public String getLinks(int total, int amount, int page, int nbrPage) {
		String base = "/link_referencing_api/v1/links?amount=" + amount + "&page=",
				regex = ", ",
				links = "",						//	Tous les liens
				first = base + 1,				//	Lien de la première page	
				previous = base,				//	Lien de la page précédente
				self = base + page,				//	Lien de la même page
				next = base,					// 	Lien de la page suivante
				last = base + nbrPage;			//	Lien de la dernière page
		
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
