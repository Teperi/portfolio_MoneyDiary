package com.blogspot.teperi31.moneydiary;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class DataUser {
	public String UID;
	public String NickName;
	public String Email;
	public String Photo;
	public boolean isCurrent;
	
	public DataUser () {}
	
	public DataUser(String UID, String NickName, String email, String photouri, boolean isCurrent) {
		this.UID = UID;
		this.NickName = NickName;
		this.Email = email;
		this.Photo = photouri;
		this.isCurrent = isCurrent;
	}
	
	public Map<String, Object> toMap() {
		HashMap<String, Object> result = new HashMap<>();
		result.put("UID", UID);
		if(NickName == null){
			result.put("NickName",Email);
		} else {
			result.put("NickName", NickName);
		}
		result.put("Email", Email);
		result.put("Photo", Photo);
		result.put("isCurrent",isCurrent);
		
		return result;
	}
	
}
