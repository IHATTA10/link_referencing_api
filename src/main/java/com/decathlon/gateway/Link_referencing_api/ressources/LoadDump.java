package com.decathlon.gateway.Link_referencing_api.ressources;

import java.io.File;

import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.decathlon.gateway.Link_referencing_api.LinkReferencingApiiApplication;
import com.decathlon.gateway.Link_referencing_api.utils.Dump;

@RestController
public class LoadDump {
	
	@RequestMapping(value="/dump", 
			method=RequestMethod.GET, 
			produces=MediaType.TEXT_PLAIN)
	public String any(@RequestHeader(value="Authorization", defaultValue="-1") String token) {
		
		String o = LinkReferencingApiiApplication.verifyToken(token);	//	Contenant de retour, contient les objets Json
		String uid = "-1";							//	uid du l'utilisateur ou du responsable de l'application		
		if (!o.contains("ERROR:") && !o.equals("-1"))
			uid = o;
		
		if (!uid.equals("-1")) {
			if (new File("dump.txt").exists()) {
				Dump d = new Dump(new File("dump.txt"));		//	Objet de dump
				try {d.getDataAmount();							//	Méthodes pour récupérer et préparer les données, puis les injecter dans la bdd.
				d.prepareData();} catch (Exception e) {e.printStackTrace();}
				o = "OK";
			} else
				o = "No file founded.";
		}
		return o;
	}
	
}
