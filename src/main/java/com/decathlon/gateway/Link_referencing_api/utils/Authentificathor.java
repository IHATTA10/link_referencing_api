package com.decathlon.gateway.Link_referencing_api.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.decathlon.gateway.Link_referencing_api.LinkReferencingApiiApplication;

public class Authentificathor {
	
	private static final String bearer = "Bearer ", 
			contentType = "application/x-www-form-urlencoded",
			param = "token=";
	private String url = "", token = "", infos = "";
	private boolean infoFresh = false;
	
	public Authentificathor() {
	}
	
	public void url(String Url) {
		this.url = Url;
	}
	
	public void token(String Token) {
		if (Token.contains(bearer))
			this.token = Token.split(bearer)[1];
	}
	
	public boolean isTokenActive() throws Exception {
		if (!this.url.isEmpty() && !this.token.isEmpty()) {		//	Si on a toutes les infos
			HttpClient client = HttpClient.newHttpClient();		//	Création d'un client HTTP
			HttpRequest post = HttpRequest.newBuilder()			//	Préparation d'une requête POST
					.uri(URI.create(this.url))					//	Définition de l'url
					.header("Authorization", LinkReferencingApiiApplication.basic)	//	Token de l'appli
					.header("Content-Type", contentType)		//	ContentType
					.POST(HttpRequest.BodyPublishers.ofString(param + this.token))	//	Request Body
					.build();
			HttpResponse<String> response = client.send(post, BodyHandlers.ofString());	//	Réponse
			if (response.statusCode() == 200 && response.body().contains("uid")) {		//	Status OK
				this.infos = response.body();
				this.infoFresh = true;
			} else
				return false;
			return true;
		} else
			return false;
	}
	
	public String getInformations()  {
		if (infoFresh) {
			infoFresh = false;
			return infos;
		}
		return "";
	}
	
}
