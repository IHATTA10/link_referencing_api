package com.decathlon.gateway.Link_referencing_api.ressources;

import javax.ws.rs.core.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.decathlon.gateway.Link_referencing_api.LinkReferencingApiiApplication;
import com.decathlon.gateway.Link_referencing_api.utils.sqlUtils;

@RestController
public class LinksDelete {
	
	@RequestMapping(value="/" + LinkReferencingApiiApplication.version + "/links/{id}", 
			method=RequestMethod.DELETE, 
			produces=MediaType.TEXT_PLAIN)
	public ResponseEntity<String> remove(@PathVariable("id") String id,
			@RequestHeader(value="Authorization", required=true) String token) {
		
		String o = LinkReferencingApiiApplication.verifyToken(token);	//	Contenant de retour, contient les objets Json
		String uid = "-1";							//	uid du l'utilisateur ou du responsable de l'application		
		String content = MediaType.TEXT_PLAIN;		//	Content type
		if (!o.contains("ERROR:") && !o.equals("-1"))
			uid = o;						//	Définition uid si token bon	
		
		if (!uid.equals("-1")) {			//	Si le token est bon
			try {
				sqlUtils su = new sqlUtils(uid);		//	Objet gestion bdd
				
				int i = Integer.parseInt(id);			//	Récupération de l'ID
				if (su.removeIfExists(i))				//	Si le lien existe il est supprimé. 
					o = "REMOVED";						//	Et le message est ...
				else
					o = "ERROR";						//	Sinon ...
				
				su.close();								//	Fermeture objet bdd
			} catch (Exception e) {e.printStackTrace(); o = "null";}
		}
		
		return ResponseEntity.ok()						//	Réponse
		        .header("link-amount", 0 + "")			//	Nombre liens total
		        .header("page", 1 + "")					//	Page
		        .header("page-amount", 1 + "")			//	Nombre pages
		        .header("Content-Type", content)		//	Content type
		        .header("Content-Range", 0 + "-" + 0 + "/" + 0)		//	Content range
		        .header("Accept-Ranges", "links")		//	Content ranges
		        .body(o);								//	Retour : ERROR ou REMOVED
	}
	
	
}
