package com.example.currencycoverter.data.liveRate;

import com.google.gson.annotations.SerializedName;

public class ExcahngeLiveRateResponse{

	@SerializedName("terms")
	private String terms;

	@SerializedName("success")
	private boolean success;

	@SerializedName("privacy")
	private String privacy;

	@SerializedName("source")
	private String source;

	@SerializedName("timestamp")
	private int timestamp;

	@SerializedName("quotes")
	private Quotes quotes;

	public void setTerms(String terms){
		this.terms = terms;
	}

	public String getTerms(){
		return terms;
	}

	public void setSuccess(boolean success){
		this.success = success;
	}

	public boolean isSuccess(){
		return success;
	}

	public void setPrivacy(String privacy){
		this.privacy = privacy;
	}

	public String getPrivacy(){
		return privacy;
	}

	public void setSource(String source){
		this.source = source;
	}

	public String getSource(){
		return source;
	}

	public void setTimestamp(int timestamp){
		this.timestamp = timestamp;
	}

	public int getTimestamp(){
		return timestamp;
	}

	public void setQuotes(Quotes quotes){
		this.quotes = quotes;
	}

	public Quotes getQuotes(){
		return quotes;
	}

	@Override
 	public String toString(){
		return 
			"ExcahngeLiveRateResponse{" + 
			"terms = '" + terms + '\'' + 
			",success = '" + success + '\'' + 
			",privacy = '" + privacy + '\'' + 
			",source = '" + source + '\'' + 
			",timestamp = '" + timestamp + '\'' + 
			",quotes = '" + quotes + '\'' + 
			"}";
		}
}