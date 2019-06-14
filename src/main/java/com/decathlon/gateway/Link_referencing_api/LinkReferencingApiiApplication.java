package com.decathlon.gateway.Link_referencing_api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.decathlon.gateway.Link_referencing_api.utils.Authentificathor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@ComponentScan(basePackages={"com.decathlon.gateway.Link_referencing_api"})	//	Préparer swagger
public class LinkReferencingApiiApplication {

	public static final String version = "v1";		//	Version du programme
	public static String basic = "Basic Qzg4MjVlNzhkZjcyYzFmZWYxYzNhMDhiYWNjZjQzMzg0YTNmMzg0Mzg6U05Uc0xVcFhkTGltaGtrZ3diSzBuMTkxTnBlYzlxQkIxYXFBbWJ2Tjk0V0d0dHlEU05LY3UxUnpLV2ZqVXF0Vw==",		//	Token de l'application
			env = "preprod",		//	Environement
			urlVerify = "https://" + env + ".idpdecathlon.oxylane.com/as/introspect.oauth2?";		//	URL de l'api ROPC

	public static void main(String[] args) {
		try {
			loadConfigFile();		//	Charger les fichiers de sauvegarde
		} catch (Exception e) {e.printStackTrace();}
		SpringApplication.run(LinkReferencingApiiApplication.class, args);		//	Lancement springboot
	}
	
	private static void loadConfigFile() {
		try {
			File f = new File("infos_config.txt");		//	Objet File
			if (f.exists()) {							//	Si il existe
/*Reader*/		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				env = br.readLine();		//	Environement d'execution
				basic = br.readLine();		//	Token de l'application
				br.close();					//	Fermeture Reader
				if (env.equals("prod"))		//	Si on est en prod
					urlVerify = "https://idpdecathlon.oxylane.com/as/introspect.oauth2?";	//	Si on est en prod, le "preprod." disparait.
			} else {									//	Sinon il n'existe pas encore
				f.createNewFile();			//	Création du fichier
/*Writer*/		PrintWriter pw = new PrintWriter(f);
				pw.println(env);			//	Ecriture de l'environement par défaut
				pw.println(basic);			//	Ecriture du token par défaut
				pw.println("");				//	On passe une ligne
				pw.println("<!-- First line is the executing environment, \"prod\" or \"preprod\". -->");		//	Commentaires
				pw.println("<!-- Second line is the basic token of the application. -->");						//	Commentaires
				pw.close();					//	On met des commentaires et on ferme le Writer.
				
			}
		} catch (Exception e) {e.printStackTrace();}
	}

	public static String verifyToken(String token) {
		String o = "-1";				//	Contenant de retour
		try {			
			Authentificathor auth = new Authentificathor();	//	Custom Auth Object
			auth.url(urlVerify);				//	Put the url used to verify token (dépend de
												//	l'environement d'execution (prod ou preprod)
			auth.token(token);					//	Token à vérifier
			if (auth.isTokenActive()) {			//	Si le token est valide et actif
				String infos = auth.getInformations();		//	On récupère les infos en json
				ObjectMapper mapper = new ObjectMapper();	//	On prépare un lecteur de json
				JsonNode json = mapper.readTree(infos);		//	On lis le json
				
				o = json.get("uid").toString().replace("\"", "");	//	On récupère l'uid du user
			} else
				o = "ERROR: INACTIVE TOKEN.";	//	Errors
		} catch (Exception e) { o = "ERROR: YOU HAVE TO SPECIFY AN AUTHORIZATION TOKEN.";}
		return o;			
	}
	
}
