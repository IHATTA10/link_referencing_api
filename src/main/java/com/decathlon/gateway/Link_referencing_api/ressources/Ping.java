package com.decathlon.gateway.Link_referencing_api.ressources;

import javax.ws.rs.core.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Ping {
	
	/*
	 * 	.../ping
	 *	Ping --> pong !
	*/
	@RequestMapping(value="/ping", 
			method=RequestMethod.GET, 
			produces=MediaType.TEXT_PLAIN)
    public ResponseEntity<String> getLinksList() {
		return ResponseEntity.ok()
		        .header("link-amount", 0 + "")
		        .header("page", 1 + "")
		        .header("page-amount", 1 + "")
		        .header("Content-Type", MediaType.TEXT_PLAIN)
		        .header("Content-Range", 0 + "-" + 0 + "/" + 0)
		        .header("Accept-Ranges", "links")
		        .header("Link", "/link_referencing_api/ping")
		        .body("pong");
	}
	
}
