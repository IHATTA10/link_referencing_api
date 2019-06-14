package com.decathlon.gateway.Link_referencing_api.ressources;

import javax.ws.rs.core.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.decathlon.gateway.Link_referencing_api.LinkReferencingApiiApplication;
import com.decathlon.gateway.Link_referencing_api.utils.Status;
import com.decathlon.gateway.Link_referencing_api.utils.sqlUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class HealthCheck {
	
	/*
	 * 	.../health_check
	 *	Vérifier l'état de l'api
	*/
	@RequestMapping(value="/health_check", 
			method=RequestMethod.GET, 
			produces=MediaType.APPLICATION_JSON)
    public ResponseEntity<String> getHealthCheck(
    		@RequestHeader(value="Authorization", defaultValue="-1") String token) {
					
		String o = LinkReferencingApiiApplication.verifyToken(token);	
										//	'o' Contenant de retour.
										//  'verifyToken' permet de vérifier la validité d'un token.							
		String uid = "-1";			//	uid du l'utilisateur ou du responsable de l'application		
		String content = MediaType.APPLICATION_JSON;	//	Content-type de la requête
		if (!o.contains("ERROR:") && !o.equals("-1"))	//	Si il n'y a pas d'erreur
			uid = o;							//	uid récupéré
		else											//	Si il y en a eu
			content = MediaType.TEXT_PLAIN;		//	content type --> Error TEXT
		
		sqlUtils su = new sqlUtils(uid);		//	Objet de de controle de la base de données
		Status s = new Status();				//	Status à renvoyer
		
		boolean b = false;						//	Active ou non
		String msg = "";						//	Message
		try {
			b = su.ping();						//	Si la bdd répond, true, sinon false.
		} catch (Exception e) {e.printStackTrace();}
		
		if (!b)									//	Si elle ne répond pas
			msg = "Database DOWN. Ping try returned false.";
		else									//	Si elle répond
			msg = "Database UP. Ping try rturned true";
		
		s.setDataBaseHealthy(b, msg);			//	Définition des paramètres de l'objet status
		
		s.param();								//	Initialisation de l'objet
		
		try {
			ObjectMapper mapper = new ObjectMapper();		//	JsonObject creator
			o = mapper.writeValueAsString(s);				//	'o' contient le retour, donc un objet status sous format json.
		} catch (Exception e) {e.printStackTrace();}
		
		return ResponseEntity.ok()							//	Réponse
		        .header("link-amount", 0 + "")				//	Nombre de liens : 0
		        .header("page", 1 + "")						//	Page : 1
		        .header("page-amount", 1 + "")				//	Page-max : 1
		        .header("Content-Type", content)			//	Content-type
		        .header("Content-Range", 0 + "-" + 0 + "/" + 0)	//	Content-range
		        .header("Accept-Ranges", "links")			//	Accept-Ranges
		        .header("Link", "/link_referencing_api/ping")	//	Links
		        .body(o);									//	Le fameux json.
	}
	
}
