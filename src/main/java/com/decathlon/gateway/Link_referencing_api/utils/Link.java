package com.decathlon.gateway.Link_referencing_api.utils;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "" })
public class Link {
	
	private String url, img, type, id, responsibles, tags, departments, lastModif;
	
	private HashMap<String,String> titles = new HashMap<String,String>(), 
			descriptions = new HashMap<String,String>();
	
	public Link() {
		this.url = "";
		this.img = "";
		this.type = "";
		this.id = "";
		this.responsibles = "";
		this.tags = "";
		this.departments = "";
		this.lastModif = "";
	}
	
	public Link(HashMap<String,String> Title, HashMap<String,String> Description, 
						String Url, String Img, String Type, String Id, String Responsibles, 
						String Tags, String Departments, String Modif) {
		this.titles = Title;
		this.descriptions = Description;
		this.url = Url;
		this.img = Img;
		this.type = Type;
		this.id = Id;
		this.responsibles = Responsibles;
		this.tags = Tags;
		this.departments = Departments;
		this.lastModif = Modif;
	}
	
	public void setTitle(String lang, String Title) {
		this.titles.put(lang + "_title", Title);
	}
	public void setDescription(String lang, String Description)  {
		this.descriptions.put(lang + "_description", Description);
	}
	public void setUrl(String Url)  {
		this.url = Url;
	}
	public void setImg(String Img)  {
		this.img = Img;
	}
	public void setType(String Type)  {
		this.type = Type;
	}
	public void setID(String Id)  {
		this.id = Id;
	}
	public void setResponsibles(String Responsibles)  {
		this.responsibles = Responsibles;
	}
	public void setTags(String Tags)  {
		this.tags = Tags;
	}
	public void setDepartments(String Departments)  {
		this.departments = Departments;
	}
	public void setLastModif(String lastModif) {
		this.lastModif = lastModif;
	}
	
	public HashMap<String,String> getTitle() {
			return titles;
	}
	public HashMap<String,String> getDescription() {
			return descriptions;
	}
	public String getUrl() {
		return url;
	}
	public String getImg() {
		return img;
	}
	public String getType() {
		return type;
	}
	public String getID() {
		return id;
	}
	public String getResponsibles() {
		return responsibles;
	}
	public String getTags() {
		return tags;
	}
	public String getDepartments() {
		return departments;
	}
	public String getLastModif() {
		return lastModif;
	}
	
}
