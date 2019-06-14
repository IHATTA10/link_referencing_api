package com.decathlon.gateway.Link_referencing_api.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

public class sqlUtils {

	// Constantes
	public static final String LINKS = "LINKS", LINK_INFOS = "LINK_INFOS", RESPONSIBLE = "RESPONSIBLE", TAGS = "TAGS",
			LANGUAGES = "LANGUAGES", DEPARTMENTS = "DEPARTMENT", DEPARTMENT_LIST = "DEPARTMENT_LIST", LOGS = "LOGS",
			TYPE_LIST = "TYPE_LIST", INFO_LINK_URL = "Link_url", INFO_LINK_MODIF = "Link_last_modifiction",
			INFO_LINK_IMG = "Link_img_path", INFO_LINK_TYPE = "Link_type", INFO_LINK_ID = "Link_ID",
			INFO_TITLE = "info_title_wording", INFO_DESC = "info_description_wording",
			INFO_LANGUAGE_ID_LIST = "Language_ID", INFO_RESPONSIBLE_LINK_ID = "Responsible_link_ID",
			INFO_RESPONSIBLE_UID = "Responsible_uid", INFO_TAG_LINK_ID = "Tag_link_ID", INFO_TAG = "Tag_wording",
			INFO_DEPARTMENT_LINK_ID = "Department_link_ID", INFO_DEPARTMENT = "Department_id",
			INFO_DEPARTMENT_NAME = "Department_name", INFO_LANGUAGE_ID = "Info_language_ID",
			INFO_LINKING_ID = "Info_link_ID", INFO_LOGS_UID = "User_uid", INFO_LOGS_ACTION = "Action",
			INFO_LOGS_ACTION_TIME = "Action_time", INFO_LOGS_LINK_ID = "Log_link_ID", INFO_TYPE = "type_wording";
	public static final String[] backInfos = { "title", "description", "type", "last-modification", "img", "url",
			"responsible", "tag", "department" };

	private Connection con = null;
	private String URL = "", USER = "", PASSWORD = "", uid = "";
	private static ArrayList<String> tables = null;
	private static ArrayList<String> infos = null;

	/*
	 * Constructeur, r�cup�ration informations de connexion, initialisation de
	 * 'con', objet de connexion � la bdd. Initialisation des listes de constante :
	 * 'tables' et 'infos'.
	 */
	public sqlUtils(String uid) {
		try {
			File f = new File("db_config.txt");		//	Fichier de configuration
			if (f.exists()) {						//	Si il existe
/*Reader*/		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				URL = br.readLine();		//	Récupération URL
				USER = br.readLine();		//	Récupération USERNAME
				PASSWORD = br.readLine();	//	Récupération PASSWORD
				this.uid = uid;				//	Définition de l'interlocuteur à la base de données
				br.close();					//	Fermeture Reader
				con = DriverManager.getConnection(URL, USER, PASSWORD);	//	Creation objet connexion
				initUtils();				//	Initialisation de variables d'enrivonement ...
			} else {								//	Si il n'existe pas
				f.createNewFile();			//	Création fichier
/*Writer*/		PrintWriter pw = new PrintWriter(f);
				pw.println(					//	Ecriture, commentaire et fermeture du Writer
						"jdbc:mysql://127.0.0.1:3306/links_references?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC");
				pw.println("root");
				pw.println("");
				pw.println("");
				pw.println("<!-- First Line --> connexion Url for database. -->");
				pw.println("<!-- Second Line --> connexion UserName for database. -->");
				pw.println("<!-- First Line --> connexion PassWord for database. -->");
				pw.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Initialisation des listes de constante : 'tables' et 'infos'.
	 */
	private void initUtils() {
		tables = new ArrayList<String>();
		tables.add(DEPARTMENTS);
		tables.add(LANGUAGES);
		tables.add(LINKS);
		tables.add(LINK_INFOS);
		tables.add(RESPONSIBLE);
		tables.add(TAGS);
		tables.add(DEPARTMENT_LIST);
		tables.add(LOGS);
		infos = new ArrayList<String>();
		infos.add(INFO_DESC);
		infos.add(INFO_TITLE);
		infos.add(INFO_RESPONSIBLE_LINK_ID);
		infos.add(INFO_RESPONSIBLE_UID);
		infos.add(INFO_TAG_LINK_ID);
		infos.add(INFO_TAG);
		infos.add(INFO_DEPARTMENT_LINK_ID);
		infos.add(INFO_DEPARTMENT);
		infos.add(INFO_LINK_URL);
		infos.add(INFO_LINK_MODIF);
		infos.add(INFO_LINK_IMG);
		infos.add(INFO_LINK_TYPE);
		infos.add(INFO_LINK_ID);
		infos.add(INFO_LANGUAGE_ID_LIST);
		infos.add(INFO_LANGUAGE_ID);
		infos.add(INFO_LINKING_ID);
		infos.add(INFO_DEPARTMENT_NAME);
		infos.add(INFO_LOGS_ACTION);
		infos.add(INFO_LOGS_ACTION_TIME);
		infos.add(INFO_LOGS_LINK_ID);
		infos.add(INFO_LOGS_UID);
	}

	/*
	 * Effectuer un "COUNT(*)" sur la table demand�.
	 */
	public int countAllFor(String TABLE) throws Exception {
		int i = 0;			//	Contenant de retour

		if (tables.contains(TABLE)) {		//	Si la table entré est reconnu dans la liste interne
			Statement sCount = con.createStatement();	//	Création d'un Statement
			ResultSet rCount = sCount.executeQuery("SELECT COUNT(*) FROM " + TABLE);//	Récupération ResultSet

			rCount.next();			//	Placer le curseur du ResultSet sur le premier élément
			i = rCount.getInt(1);	//	Récupération du résultat de la requête

			closeUnits(sCount, rCount);	//	Fermeture du Statement et du ResultSet
		}
		return i;			//	Return i
	}

	/*
	 * Remove all data of a link from the data base.
	 */
	public boolean removeIfExists(int id) throws Exception {

		Statement s = con.createStatement();
		ResultSet r = s.executeQuery("SELECT COUNT(*) FROM " + LINKS + " WHERE " + INFO_LINK_ID + "=" + id);

		int i = 0;
		while (r.next())
			i = r.getInt(1);

		closeUnits(s, r);

		if (i != 1)
			return false;

		Statement sl = con.createStatement();
		Statement st = con.createStatement();
		Statement sd = con.createStatement();
		Statement sr = con.createStatement();
		Statement sli = con.createStatement();

		sl.executeUpdate("DELETE FROM " + LINKS + " WHERE " + INFO_LINK_ID + " = " + id);
		st.executeUpdate("DELETE FROM " + TAGS + " WHERE " + INFO_TAG_LINK_ID + " = " + id);
		sd.executeUpdate("DELETE FROM " + DEPARTMENTS + " WHERE " + INFO_DEPARTMENT_LINK_ID + " = " + id);
		sr.executeUpdate("DELETE FROM " + RESPONSIBLE + " WHERE " + INFO_RESPONSIBLE_LINK_ID + " = " + id);
		sli.executeUpdate("DELETE FROM " + LINK_INFOS + " WHERE " + INFO_LINKING_ID + " = " + id);

		sl.close();
		st.close();
		sd.close();
		sr.close();
		sli.close();

		return true;

	}

	/*
	 * R�cup�ration d'une liste d'ID. Ces derniers sont au nombre de 'limit'. Les
	 * param�tres 'min' et 'max' servent � d�liliter ka page.
	 */
	public ArrayList<Integer> getIdArrayByLimits(int pag, int limit) throws Exception {
		ArrayList<Integer> ls = new ArrayList<Integer>();
		Statement s = con.createStatement();
		ResultSet r = s.executeQuery("SELECT " + INFO_LINK_ID + " FROM " + LINKS);

		while (r.next()) {
			int i = r.getInt(1);
			ls.add(i);
		}

		ArrayList<Integer> to = new ArrayList<Integer>();
		int i = (pag * limit) - limit;
		while (i < ls.size() && i < pag * limit) {
			to.add(ls.get(i));
			i++;
		}

		closeUnits(s, r);
		return to;
	}

	/*
	 * R�cup�rer toutes les informations, ou les 'back' informations, des liens contenus
	 * dans une liste, sous forme d'IDs.
	 */
	public ArrayList<String> getInfosOf(ArrayList<Integer> ids, String[] back) throws Exception {
		ArrayList<String> obs = new ArrayList<String>();
		ArrayList<Integer> security = new ArrayList<Integer>();

		if (back[0].equals("-1")) {
			for (int i : ids) {
				if (!security.contains(i)) {
					security.add(i);

					HashMap<String, String> titles = new HashMap<String, String>();
					HashMap<String, String> descriptions = new HashMap<String, String>();

					for (String lang : getLanguages()) {

						Statement sCount = con.createStatement();
						ResultSet rCount = sCount.executeQuery("SELECT COUNT(*) FROM " + LINK_INFOS + " WHERE "
								+ INFO_LANGUAGE_ID + " = '" + lang + "' AND " + INFO_LINKING_ID + " = " + i);

						rCount.next();
						if (rCount.getInt(1) > 0) {

							Statement sTitle = con.createStatement();
							ResultSet rTitle = sTitle.executeQuery(
									"SELECT " + INFO_TITLE + " FROM " + LINK_INFOS + " WHERE " + INFO_LANGUAGE_ID
											+ " = '" + lang + "' AND " + INFO_LINKING_ID + " = " + i);
							Statement sDesc = con.createStatement();
							ResultSet rDesc = sDesc.executeQuery(
									"SELECT " + INFO_DESC + " FROM " + LINK_INFOS + " WHERE " + INFO_LANGUAGE_ID
											+ " = '" + lang + "' AND " + INFO_LINKING_ID + " = " + i);

							rTitle.next();
							rDesc.next();
							titles.put(lang, rTitle.getString(1));
							descriptions.put(lang, rDesc.getString(1));

							closeUnits(sTitle, rTitle);
							closeUnits(sDesc, rDesc);

						}

						closeUnits(sCount, rCount);

					}

					Statement sUrl = con.createStatement();
					ResultSet rUrl = sUrl.executeQuery(
							"SELECT " + INFO_LINK_URL + " FROM " + LINKS + " WHERE " + INFO_LINK_ID + " = " + i);
					Statement sType = con.createStatement();
					ResultSet rType = sType.executeQuery(
							"SELECT " + INFO_LINK_TYPE + " FROM " + LINKS + " WHERE " + INFO_LINK_ID + " = " + i);
					Statement sImg = con.createStatement();
					ResultSet rImg = sImg.executeQuery(
							"SELECT " + INFO_LINK_IMG + " FROM " + LINKS + " WHERE " + INFO_LINK_ID + " = " + i);
					Statement sLastModif = con.createStatement();
					ResultSet rLastModif = sLastModif.executeQuery(
							"SELECT " + INFO_LINK_MODIF + " FROM " + LINKS + " WHERE " + INFO_LINK_ID + " = " + i);

					Statement stag = con.createStatement();
					ResultSet rtag = stag.executeQuery(
							"SELECT " + INFO_TAG + " FROM " + TAGS + " WHERE " + INFO_TAG_LINK_ID + " = " + i);
					ArrayList<String> ls = new ArrayList<String>();
					while (rtag.next())
						ls.add(rtag.getString(1));

					Statement sResponsibles = con.createStatement();
					ResultSet rResponsibles = sResponsibles.executeQuery("SELECT " + INFO_RESPONSIBLE_UID + " FROM "
							+ RESPONSIBLE + " WHERE " + INFO_RESPONSIBLE_LINK_ID + " = " + i);
					ArrayList<String> lsr = new ArrayList<String>();
					while (rResponsibles.next())
						lsr.add(rResponsibles.getString(1));

					Statement sDepartments = con.createStatement();
					ResultSet rDepartments = sDepartments.executeQuery("SELECT " + INFO_DEPARTMENT + " FROM "
							+ DEPARTMENTS + " WHERE " + INFO_DEPARTMENT_LINK_ID + " = " + i);
					ArrayList<String> lsd = new ArrayList<String>();
					while (rDepartments.next())
						lsd.add(rDepartments.getString(1));

					rType.next();
					rImg.next();
					rLastModif.next();

					String type = rType.getString(1);
					String img = rImg.getString(1);
					String lastModif = rLastModif.getString(1);

					String tags = "";
					for (String str : ls) {
						if (ls.indexOf(str) != ls.size() - 1)
							tags += str + ",";
						else
							tags += str;
					}

					String responsibles = "";
					for (String str : lsr) {
						if (lsr.indexOf(str) != lsr.size() - 1)
							responsibles += str + ",";
						else
							responsibles += str;
					}

					String departments = "";
					for (String str : lsd) {
						if (lsd.indexOf(str) != lsd.size() - 1)
							departments += str + ",";
						else
							departments += str;
					}

					ObjectMapper mapper = new ObjectMapper();

					while (rUrl.next()) {
						Link l = new Link(titles, descriptions, rUrl.getString(1), img, type, i + "", responsibles,
								tags, departments, lastModif);

						String val = mapper.writeValueAsString(l);
						obs.add(val);
					}
					closeUnits(sUrl, rUrl);
					closeUnits(sType, rType);
					closeUnits(sImg, rImg);
					closeUnits(stag, rtag);
					closeUnits(sResponsibles, rResponsibles);
					closeUnits(sDepartments, rDepartments);
				}
			}
		} else {
			ObjectMapper mapper = new ObjectMapper();
			ArrayList<String> backs = new ArrayList<String>();
			for (String str : back)
				backs.add(str);
			for (int i : ids) {
				if (backs.contains("id")) {
					Link l = new Link();
					l.setID(i + "");
					String val = mapper.writeValueAsString(l);
					obs.add(val);
				}
				if (backs.contains("title")) {

					Link l = new Link();

					for (String lang : getLanguages()) {

						Statement sCount = con.createStatement();
						ResultSet rCount = sCount.executeQuery("SELECT COUNT(*) FROM " + LINK_INFOS + " WHERE "
								+ INFO_LANGUAGE_ID + " = '" + lang + "' AND " + INFO_LINKING_ID + " = " + i);

						rCount.next();
						if (rCount.getInt(1) > 0) {

							Statement sTitle = con.createStatement();
							ResultSet rTitle = sTitle.executeQuery(
									"SELECT " + INFO_TITLE + " FROM " + LINK_INFOS + " WHERE " + INFO_LANGUAGE_ID
											+ " = '" + lang + "' AND " + INFO_LINKING_ID + " = " + i);

							rTitle.next();
							l.setTitle(lang, rTitle.getString(1));

							closeUnits(sTitle, rTitle);

						}

						closeUnits(sCount, rCount);

					}

					String val = mapper.writeValueAsString(l.getTitle());
					obs.add(val);

				}
				if (backs.contains("description")) {

					Link l = new Link();

					for (String lang : getLanguages()) {

						Statement sCount = con.createStatement();
						ResultSet rCount = sCount.executeQuery("SELECT COUNT(*) FROM " + LINK_INFOS + " WHERE "
								+ INFO_LANGUAGE_ID + " = '" + lang + "' AND " + INFO_LINKING_ID + " = " + i);

						rCount.next();
						if (rCount.getInt(1) > 0) {

							Statement sDesc = con.createStatement();
							ResultSet rDesc = sDesc.executeQuery(
									"SELECT " + INFO_DESC + " FROM " + LINK_INFOS + " WHERE " + INFO_LANGUAGE_ID
											+ " = '" + lang + "' AND " + INFO_LINKING_ID + " = " + i);

							rDesc.next();
							l.setDescription(lang, rDesc.getString(1));

							closeUnits(sDesc, rDesc);

						}

						closeUnits(sCount, rCount);

					}

					String val = mapper.writeValueAsString(l.getDescription());
					obs.add(val);

				}
				if (backs.contains("type")) {
					Statement sType = con.createStatement();
					ResultSet rType = sType.executeQuery(
							"SELECT " + INFO_LINK_TYPE + " FROM " + LINKS + " WHERE " + INFO_LINK_ID + " = " + i);
					Link l = new Link();
					if (rType.next())
						l.setType(rType.getString(1));
					String val = mapper.writeValueAsString(l);
					obs.add(val);
					closeUnits(sType, rType);
				}
				if (backs.contains("last-modification")) {
					Statement sModif = con.createStatement();
					ResultSet rModif = sModif.executeQuery(
							"SELECT " + INFO_LINK_MODIF + " FROM " + LINKS + " WHERE " + INFO_LINK_ID + " = " + i);
					Link l = new Link();
					if (rModif.next())
						l.setLastModif(rModif.getString(1));
					String val = mapper.writeValueAsString(l);
					obs.add(val);
					closeUnits(sModif, rModif);
				}
				if (backs.contains("img")) {
					Statement sImg = con.createStatement();
					ResultSet rImg = sImg.executeQuery(
							"SELECT " + INFO_LINK_IMG + " FROM " + LINKS + " WHERE " + INFO_LINK_ID + " = " + i);
					Link l = new Link();
					if (rImg.next())
						l.setImg(rImg.getString(1));
					String val = mapper.writeValueAsString(l);
					obs.add(val);
					closeUnits(sImg, rImg);
				}
				if (backs.contains("url")) {
					Statement sUrl = con.createStatement();
					ResultSet rUrl = sUrl.executeQuery(
							"SELECT " + INFO_LINK_URL + " FROM " + LINKS + " WHERE " + INFO_LINK_ID + " = " + i);
					Link l = new Link();
					if (rUrl.next())
						l.setUrl(rUrl.getString(1));
					String val = mapper.writeValueAsString(l);
					obs.add(val);
					closeUnits(sUrl, rUrl);
				}
				if (backs.contains("responsible")) {
					Statement sResponsibles = con.createStatement();
					ResultSet rResponsibles = sResponsibles.executeQuery("SELECT " + INFO_RESPONSIBLE_UID + " FROM "
							+ RESPONSIBLE + " WHERE " + INFO_RESPONSIBLE_LINK_ID + " = " + i);
					ArrayList<String> lsr = new ArrayList<String>();
					while (rResponsibles.next())
						lsr.add(rResponsibles.getString(1));

					String responsibles = "";
					for (String str : lsr) {
						if (lsr.indexOf(str) != lsr.size() - 1)
							responsibles += str + ",";
						else
							responsibles += str;
					}
					Link l = new Link();
					l.setResponsibles(responsibles);
					String val = mapper.writeValueAsString(l);
					obs.add(val);
					closeUnits(sResponsibles, rResponsibles);
				}
				if (backs.contains("tag")) {
					Statement sTags = con.createStatement();
					ResultSet rTags = sTags.executeQuery(
							"SELECT " + INFO_TAG + " FROM " + TAGS + " WHERE " + INFO_TAG_LINK_ID + " = " + i);
					ArrayList<String> lsr = new ArrayList<String>();
					while (rTags.next())
						lsr.add(rTags.getString(1));

					String tags = "";
					for (String str : lsr) {
						if (lsr.indexOf(str) != lsr.size() - 1)
							tags += str + ",";
						else
							tags += str;
					}
					Link l = new Link();
					l.setTags(tags);
					String val = mapper.writeValueAsString(l);
					obs.add(val);
					closeUnits(sTags, rTags);
				}
				if (backs.contains("department")) {
					Statement sDep = con.createStatement();
					ResultSet rDep = sDep.executeQuery("SELECT " + INFO_DEPARTMENT + " FROM " + DEPARTMENTS + " WHERE "
							+ INFO_DEPARTMENT_LINK_ID + " = " + i);
					ArrayList<String> lsr = new ArrayList<String>();
					while (rDep.next())
						lsr.add(rDep.getString(1));

					String departments = "";
					for (String str : lsr) {
						if (lsr.indexOf(str) != lsr.size() - 1)
							departments += str + ",";
						else
							departments += str;
					}
					Link l = new Link();
					l.setDepartments(departments);
					String val = mapper.writeValueAsString(l);
					obs.add(val);
					closeUnits(sDep, rDep);
				}
			}
		}
		return obs;
	}

	/*
	 * R�cup�rer une liste d'ID en fonction de l'information 'type', pour la valeure
	 * 'value'. Le param�tre 'type' reflette soit le titre, soit la description.
	 * Seuls les recherche par titre ou description acceptent une valeure
	 * approximative ('%value%').
	 */
	public Collection<? extends Integer> getIdsByTypeLike(String INFO_CONDITION, String value) throws Exception {
		ArrayList<Integer> ids = new ArrayList<Integer>();

		if (infos.contains(INFO_CONDITION)) {
			Statement sType = con.createStatement();
			ResultSet rType = sType.executeQuery("SELECT " + INFO_LINKING_ID + " FROM " + LINK_INFOS + " WHERE "
					+ INFO_CONDITION + " LIKE \"%" + value + "%\"");

			while (rType.next()) {
				int i = rType.getInt(1);
				if (!ids.contains(i))
					ids.add(i);
			}

			closeUnits(sType, rType);
		}

		return ids;
	}

	/*
	 * R�cup�rer une liste d'ID en fonction de l'information 'type' et de la langue
	 * 'language', pour la valeure 'value'. Le param�tre 'type' reflette soit le
	 * titre, soit la description. Seuls les recherche par titre ou description
	 * acceptent une valeure approximative ('%value%').
	 */
	public ArrayList<String> getIdsByTypeLikeARRAY(String INFO_CONDITION, String value) throws Exception {
		ArrayList<String> ids = new ArrayList<String>();

		if (infos.contains(INFO_CONDITION)) {
			Statement sType = con.createStatement();
			ResultSet rType = sType.executeQuery("SELECT " + INFO_LINKING_ID + " FROM " + LINK_INFOS + " WHERE "
					+ INFO_CONDITION + " LIKE \"%" + value + "%\"");

			while (rType.next()) {
				String i = rType.getString(1);
				if (!ids.contains(i))
					ids.add(i);
			}

			closeUnits(sType, rType);
		}

		return ids;
	}

	/*
	 * Cette fonction peut parra�tre inutile si on a d�j� les IDs. Elle sert �
	 * v�rifier et r�cup�rer les IDs valides.
	 */
	public Collection<? extends Integer> getIdsByIds(ArrayList<String> Ids) throws Exception {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (String str : Ids) {
			Statement sId = con.createStatement();
			ResultSet rId = sId.executeQuery(
					"SELECT " + INFO_LINK_ID + " FROM " + LINKS + " WHERE " + INFO_LINK_ID + " = '" + str + "'");

			while (rId.next())
				ids.add(rId.getInt(1));

			closeUnits(sId, rId);
		}
		return ids;
	}

	/*
	 * R�cup�rer une liste d'ID en fonction d'un uid et responsable. Les liens
	 * r�cup�r�s sont ceux dont l'uid est responsable (seul, ou non).
	 */
	public Collection<? extends Integer> getIdsByResponsible(String value) throws Exception {
		ArrayList<Integer> ids = new ArrayList<Integer>();

		Statement sResponsible = con.createStatement();
		ResultSet rResponsible = sResponsible.executeQuery("SELECT " + INFO_RESPONSIBLE_LINK_ID + " FROM " + RESPONSIBLE
				+ " WHERE " + INFO_RESPONSIBLE_UID + " = \"" + value + "\"");

		while (rResponsible.next())
			ids.add(rResponsible.getInt(1));

		closeUnits(sResponsible, rResponsible);
		return ids;
	}

	/*
	 * R�cup�rer une liste d'ID en fonction d'un d�partment. Les liens r�cup�r�s
	 * sont ceux qui appartiennent (enti�rement ou non) au d�partement stipul�.
	 */
	public Collection<? extends Integer> getIdsByDepartment(String value) throws Exception {
		ArrayList<Integer> ids = new ArrayList<Integer>();

		Statement sDepartment = con.createStatement();
		ResultSet rDepartment = sDepartment.executeQuery("SELECT " + INFO_DEPARTMENT + " FROM " + DEPARTMENT_LIST
				+ " WHERE " + INFO_DEPARTMENT_NAME + " LIKE \"%" + value + "%\"");

		while (rDepartment.next())
			value = rDepartment.getString(1);

		Statement s1Department = con.createStatement();
		ResultSet r1Department = s1Department.executeQuery("SELECT " + INFO_DEPARTMENT_LINK_ID + " FROM " + DEPARTMENTS
				+ " WHERE " + INFO_DEPARTMENT + " = \"" + value + "\"");

		while (r1Department.next())
			ids.add(r1Department.getInt(1));

		closeUnits(sDepartment, rDepartment);
		closeUnits(s1Department, r1Department);
		return ids;
	}

	/*
	 * R�cup�rer une liste d'ID en fonction d'un tag. Les liens r�cup�r�s sont ceux
	 * qui ont pour tag (unique ou non) la valeure stipul�.
	 */
	public Collection<? extends Integer> getIdsByTag(String value) throws Exception {
		ArrayList<Integer> ids = new ArrayList<Integer>();

		Statement sTag = con.createStatement();
		ResultSet rTag = sTag.executeQuery(
				"SELECT " + INFO_TAG_LINK_ID + " FROM " + TAGS + " WHERE " + INFO_TAG + " LIKE \"%" + value + "%\";");

		while (rTag.next())
			ids.add(rTag.getInt(1));

		closeUnits(sTag, rTag);
		return ids;
	}

	/*
	 * R�cup�rer une liste d' 'INFO' dans la table 'TABLE' ; l� o� 'INFO_CONDITION'
	 * LIKE 'values'. Cette fonction effectue la recherche suivante : Donnez moi l'
	 * 'INFO' de 'TABLE' l� o� tous les param�tres (en m�me temps) 'values' sont
	 * valid�s.
	 */
	public ArrayList<String> getIdsByLikeMultipleValues(String INFO, String INFO_CONDITION, String TABLE,
			String[] values) throws Exception {
		ArrayList<String> ids = new ArrayList<String>(), // Contient les id des liens dont les uid saisies sont
															// responsables
				tmp2 = new ArrayList<String>(), // Simple tmp
				tmp = new ArrayList<String>(); // Simple tmp
		for (String str : values) {
			Statement idsState = con.createStatement();
			ResultSet idsResult = idsState.executeQuery(
					"SELECT " + INFO + " FROM " + TABLE + " WHERE " + INFO_CONDITION + " LIKE \"%" + str + "%\"");

			while (idsResult.next())
				tmp.add(idsResult.getString(1) + "");

			closeUnits(idsState, idsResult);

			if (values.length > 1 && !tmp.isEmpty()) {
				if (ids.isEmpty())
					ids = tmp;
				else {
					for (String strr : ids) {
						if (!tmp.contains(strr))
							tmp2.add(strr);
					}
					for (String strr : tmp2)
						ids.remove(strr);
				}
				tmp = new ArrayList<String>();
			} else if (tmp.isEmpty())
				ids = new ArrayList<String>();
			else
				ids = tmp;
		}

		Set<String> set = new HashSet<String>();
		set.addAll(ids);
		ids = new ArrayList<String>(set);

		return ids;
	}

	/*
	 * R�cup�rer une liste d' 'INFO' dans la table 'TABLE' ; l� o� 'INFO_CONDITION'
	 * = 'values'. Cette fonction effectue la recherche suivante : Donnez moi l'
	 * 'INFO' de 'TABLE' l� o� tous les param�tres (en m�me temps) 'values' sont
	 * valid�s.
	 */
	public ArrayList<String> getIdsByMultipleValues(String INFO, String INFO_CONDITION, String TABLE, String[] values)
			throws Exception {
		ArrayList<String> ids = new ArrayList<String>(), // Contient les id des liens dont les uid saisies sont
															// responsables
				tmp2 = new ArrayList<String>(), // Simple tmp
				tmp = new ArrayList<String>(); // Simple tmp
		for (String str : values) {
			Statement idsState = con.createStatement();
			ResultSet idsResult = idsState.executeQuery(
					"SELECT " + INFO + " FROM " + TABLE + " WHERE " + INFO_CONDITION + " = '" + str + "'");

			while (idsResult.next())
				tmp.add(idsResult.getString(1) + "");

			closeUnits(idsState, idsResult);

			if (values.length > 1 && !tmp.isEmpty()) {
				if (ids.isEmpty())
					ids = tmp;
				else {
					for (String strr : ids) {
						if (!tmp.contains(strr))
							tmp2.add(strr);
					}
					for (String strr : tmp2)
						ids.remove(strr);
				}
				tmp = new ArrayList<String>();
			} else if (tmp.isEmpty())
				ids = new ArrayList<String>();
			else
				ids = tmp;
		}

		Set<String> set = new HashSet<String>();
		set.addAll(ids);
		ids = new ArrayList<String>(set);

		return ids;
	}

	/*
	 * R�cup�rer la liste des langues existantes
	 */
	public ArrayList<String> getLanguages() throws Exception {
		ArrayList<String> to = new ArrayList<String>();

		Statement languagesState = con.createStatement();
		ResultSet languagesResult = languagesState
				.executeQuery("SELECT " + INFO_LANGUAGE_ID_LIST + " FROM " + LANGUAGES);

		while (languagesResult.next())
			to.add(languagesResult.getString(1) + "");

		closeUnits(languagesState, languagesResult);

		return to;
	}

	/*
	 * Récupérer la liste des Types
	 */
	public ArrayList<String> getTypes() throws Exception {
		ArrayList<String> to = new ArrayList<String>();

		Statement typesState = con.createStatement();
		ResultSet typesResult = typesState.executeQuery("SELECT " + INFO_TYPE + " FROM " + TYPE_LIST);

		while (typesResult.next())
			to.add(typesResult.getString(1) + "");

		closeUnits(typesState, typesResult);

		return to;
	}

	/*
	 * R�cup�rer la liste des départements existants
	 */
	public ArrayList<String> getDepartments() throws Exception {
		ArrayList<String> to = new ArrayList<String>();

		Statement languagesState = con.createStatement();
		ResultSet languagesResult = languagesState
				.executeQuery("SELECT " + INFO_DEPARTMENT + " FROM " + DEPARTMENT_LIST);

		while (languagesResult.next())
			to.add(languagesResult.getString(1) + "");

		closeUnits(languagesState, languagesResult);

		return to;
	}

	/*
	 * Enregistrer un lien.
	 */
	public int registerLink(String type, String img, String url) throws Exception {
		Statement countState = con.createStatement();
		ResultSet countResult = countState
				.executeQuery("SELECT COUNT(*) FROM " + LINKS + " WHERE " + INFO_LINK_URL + " = \"" + url + "\"");
		countResult.next();
		int i = countResult.getInt(1);
		closeUnits(countState, countResult);
		if (i == 0) {
			Statement frTitleState = con.createStatement();
			frTitleState.executeUpdate("INSERT INTO " + LINKS + " (" + INFO_LINK_MODIF + ", " + INFO_LINK_TYPE + ", "
					+ INFO_LINK_IMG + ", " + INFO_LINK_URL + ") VALUES (\"" + LocalDate.now() + "\", \"" + type
					+ "\", \"" + img + "\", \"" + url + "\");");
			frTitleState.close();
			Statement idState = con.createStatement();
			ResultSet idResult = idState.executeQuery(
					"SELECT " + INFO_LINK_ID + " FROM LINKS WHERE " + INFO_LINK_URL + " = \"" + url + "\"");
			idResult.next();
			int id = idResult.getInt(1);
			closeUnits(idState, idResult);
			registerAction("REGISTER LINK", uid, id + "");
			return id;
		}
		return 0;
	}

	/*
	 * Enregistrer les infos sur un lien (Titre et description) par langue.
	 */
	public boolean registerTitleAndDescription(String[] titles, String[] descriptions, String[] languages, int id)
			throws Exception {
		Statement countState = con.createStatement();
		ResultSet countResult = countState
				.executeQuery("SELECT COUNT(*) FROM " + LINKS + " WHERE " + INFO_LINK_ID + " = " + id);
		countResult.next();
		int i = countResult.getInt(1);
		closeUnits(countState, countResult);
		if (i == 1) {
			if (languages[0].equals("-1")) {
				Statement frInfoState = con.createStatement();
				frInfoState.executeUpdate("INSERT INTO " + LINK_INFOS + "(" + INFO_DESC + ", " + INFO_TITLE + " , "
						+ INFO_LANGUAGE_ID + ", " + INFO_LINKING_ID + ") VALUES (\"" + descriptions[0] + "\", \""
						+ titles[0] + "\", \"fr\", " + id + ");");
				frInfoState.close();
				Statement enInfoState = con.createStatement();
				enInfoState.executeUpdate("INSERT INTO " + LINK_INFOS + "(" + INFO_DESC + ", " + INFO_TITLE + " , "
						+ INFO_LANGUAGE_ID + ", " + INFO_LINKING_ID + ") VALUES (\"" + descriptions[1] + "\", \""
						+ titles[1] + "\", \"en\", " + id + ");");
				enInfoState.close();
				closeUnits(countState, countResult);
				registerAction("REGISTER TITLE AND DESCRIPTION", uid, id + "");
				return true;
			} else {
				for (int ii = 0; ii < languages.length; ii++) {
					Statement infoState = con.createStatement();
					infoState.executeUpdate("INSERT INTO " + LINK_INFOS + "(" + INFO_DESC + ", " + INFO_TITLE + " , "
							+ INFO_LANGUAGE_ID + ", " + INFO_LINKING_ID + ") VALUES (\"" + descriptions[ii] + "\", \""
							+ titles[ii] + "\", \"" + languages[ii] + "\", " + id + ");");
					infoState.close();
				}
				registerAction("REGISTER DESCRIPTION AND TITLE IN MANY LANGUAGES", uid, id + "");
				return true;
			}
		}
		return false;
	}

	/*
	 * Enregistrer un/des tag pour un lien.
	 */
	public boolean registerTags(String[] tags, int id) throws Exception {
		Statement countState = con.createStatement();
		ResultSet countResult = countState
				.executeQuery("SELECT COUNT(*) FROM " + LINKS + " WHERE " + INFO_LINK_ID + " = " + id);
		countResult.next();
		int i = countResult.getInt(1);
		closeUnits(countState, countResult);
		if (i == 1) {
			for (String str : tags) {
				if (str != "") {
					Statement tagState = con.createStatement();
					tagState.executeUpdate("INSERT INTO " + TAGS + "(" + INFO_TAG + ", " + INFO_TAG_LINK_ID
							+ ") VALUES (\"" + str + "\", " + id + ");");
					tagState.close();
				}
			}
			registerAction("REGISTER TAGS", uid, id + "");
			return true;
		}
		return false;
	}

	/*
	 * Enregistrer un/des responsables pour un lien.
	 */
	public boolean registerResponsibles(String[] responsibles, int id) throws Exception {
		Statement countState = con.createStatement();
		ResultSet countResult = countState
				.executeQuery("SELECT COUNT(*) FROM " + LINKS + " WHERE " + INFO_LINK_ID + " = " + id);
		countResult.next();
		int i = countResult.getInt(1);
		closeUnits(countState, countResult);
		if (i == 1) {
			for (String str : responsibles) {
				if (str != "") {
					Statement responsibleState = con.createStatement();
					responsibleState.executeUpdate("INSERT INTO " + RESPONSIBLE + " (" + INFO_RESPONSIBLE_UID + ", "
							+ INFO_RESPONSIBLE_LINK_ID + ") VALUES (\"" + str + "\", " + id + ");");
					responsibleState.close();
				}
			}
			registerAction("REGISTER RESPONSIBLES", uid, id + "");
			return true;
		}
		return false;
	}

	/*
	 * Enregistrer un/des d�partements pour un lien.
	 */
	public boolean registerDepartments(String[] departments, int id) throws Exception {
		Statement countState = con.createStatement();
		ResultSet countResult = countState
				.executeQuery("SELECT COUNT(*) FROM " + LINKS + " WHERE " + INFO_LINK_ID + " = " + id);
		countResult.next();
		int i = countResult.getInt(1);
		closeUnits(countState, countResult);
		if (i == 1) {
			for (String str : departments) {
				if (str != "") {
					Statement departmentState = con.createStatement();
					departmentState.executeUpdate("INSERT INTO " + DEPARTMENTS + " (" + INFO_DEPARTMENT + ", "
							+ INFO_DEPARTMENT_LINK_ID + ") VALUES (\"" + str + "\", " + id + ");");
					departmentState.close();
				}
			}
			registerAction("REGISTER DEPARTMENTS", uid, id + "");
			return true;
		}
		return false;
	}

	public boolean updateLastModificationDate(String id) throws Exception {

		Statement sDate = con.createStatement();
		sDate.executeUpdate("UPDATE " + LINKS + " SET `" + INFO_LINK_MODIF + "` = '" + LocalDate.now() + "' WHERE "
				+ LINKS + "." + INFO_LINK_ID + " = " + id);
		sDate.close();

		return true;
	}

	public boolean updateLinkType(String value, String id) throws Exception {

		Statement sDate = con.createStatement();
		sDate.executeUpdate("UPDATE " + LINKS + " SET " + INFO_LINK_TYPE + " = '" + value + "' WHERE " + LINKS + "."
				+ INFO_LINK_ID + " = " + id);
		sDate.close();

		registerAction("UPDATE LINK TYPE", uid, id);

		return true;
	}

	public boolean updateLinkImg(String value, String id) throws Exception {

		Statement sDate = con.createStatement();
		sDate.executeUpdate("UPDATE " + LINKS + " SET " + INFO_LINK_IMG + " = '" + value + "' WHERE " + LINKS + "."
				+ INFO_LINK_ID + " = " + id);
		sDate.close();

		registerAction("UPDATE LINK IMG URL", uid, id);

		return true;
	}

	public boolean updateLinkUrl(String value, String id) throws Exception {

		Statement sDate = con.createStatement();
		sDate.executeUpdate("UPDATE " + LINKS + " SET " + INFO_LINK_URL + " = \"" + value + "\" WHERE " 
										+ LINKS + "."
				+ INFO_LINK_ID + " = " + id);
		sDate.close();

		registerAction("UPDATE LINK URL", uid, id);

		return true;
	}

	public boolean updateTitleAndDescription(String title, String desc, String id, String lang) throws Exception {

		Statement sCount = con.createStatement();
		ResultSet rCount = sCount.executeQuery("SELECT COUNT(*) FROM " + LINK_INFOS + " WHERE " + INFO_LINKING_ID
				+ " = " + id + " AND " + INFO_LANGUAGE_ID + " = '" + lang + "'");
		rCount.next();
		if (rCount.getInt(1) == 1) {
			Statement sTitle = con.createStatement();
			sTitle.executeUpdate("UPDATE " + LINK_INFOS + " SET " + INFO_TITLE + " = \"" + title + "\" WHERE "
					+ INFO_LANGUAGE_ID + " = '" + lang + "' AND " + INFO_LINKING_ID + " = " + id);
			sTitle.close();
			Statement sDesc = con.createStatement();
			sDesc.executeUpdate("UPDATE " + LINK_INFOS + " SET " + INFO_DESC + " = \"" + desc + "\" WHERE "
					+ INFO_LANGUAGE_ID + " = '" + lang + "' AND " + INFO_LINKING_ID + " = " + id);
			sDesc.close();
			registerAction("UPDATE TITLE AND DESCRIPTION IN " + lang, uid, id);
		} else {
			Statement sDate = con.createStatement();
			sDate.executeUpdate("INSERT INTO " + LINK_INFOS + "(" + INFO_DESC + ", " + INFO_TITLE + " , "
					+ INFO_LANGUAGE_ID + ", " + INFO_LINKING_ID + ") VALUES (\"" + desc + "\", \"" + title + "\", \""
					+ lang + "\", " + id + ");");
			registerAction("REGISTER TITLE AND DESCRIPTION IN NEW LANGUAGE", uid, id);
			sDate.close();
		}
		closeUnits(sCount, rCount);

		return true;
	}

	public boolean updateMultiValues(String values[], String id, String TABLE, String INFO_CONDITION) throws Exception {

		Statement sDrop = con.createStatement();
		sDrop.executeUpdate("DELETE FROM " + TABLE + " WHERE " + INFO_CONDITION + " = " + id);
		sDrop.close();

		int sid = 0;
		try {
			sid = Integer.parseInt(id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (TABLE.equals(TAGS)) {
			registerTags(values, sid);
			registerAction("UPDATE TAGS", uid, id);
		} else if (TABLE.equals(DEPARTMENTS)) {
			registerDepartments(values, sid);
			registerAction("UPDATE DEPARTMENTS", uid, id);
		} else if (TABLE.equals(RESPONSIBLE)) {
			registerResponsibles(values, sid);
			registerAction("UPDATE RESPONSIBLES", uid, id);
		}

		return true;
	}
	
	/*
	 * Enregistrer des actions dans les logs
	 */
	public boolean registerAction(String action, String uid, String id) throws Exception {

		Statement sAction = con.createStatement();
		sAction.executeUpdate("INSERT INTO " + LOGS + " (" + INFO_LOGS_UID + ", " + INFO_LOGS_ACTION_TIME + ", "
				+ INFO_LOGS_ACTION + ", " + INFO_LOGS_LINK_ID + ") VALUES (\"" + uid + "\", \"" + LocalDate.now()
				+ "\", \"" + action + "\", " + id + ");");
		sAction.close();
		updateLastModificationDate(id);

		return true;
	}

	/*
	 * Fermer l'objet Connection 'con' de l'instance.
	 */
	public void close() throws Exception {
		con.close();
	}

	/*
	 * Fermer un Statement et un ResultSet. Etant donn� le nombre de fois ou ces
	 * lignes apparaissent, une fonction �tait n�cessaire.
	 */
	public void closeUnits(Statement s, ResultSet r) throws Exception {
		s.close();
		r.close();
	}

	public ArrayList<String> getIdsByType(String type) {

		return null;
	}

	public boolean ping() throws Exception {
		int i = 0;

		Statement sCount = con.createStatement();
		ResultSet rCount = sCount.executeQuery("SELECT 1");

		rCount.next();
		i = rCount.getInt(1);

		if (i == 1)
			return true;
		return false;
	}
}
