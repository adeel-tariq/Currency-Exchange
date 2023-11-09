package com.example.currencycoverter.data.liveRate;

import com.google.gson.annotations.SerializedName;

public class Quotes{

	@SerializedName("EURMXN")
	private Double eURMXN;

	@SerializedName("EURAUD")
	private Double eURAUD;

	@SerializedName("EURHKD")
	private Double eURHKD;

	public void setEURMXN(Double eURMXN){
		this.eURMXN = eURMXN;
	}

	public Double getEURMXN(){
		return eURMXN;
	}

	public void setEURAUD(Double eURAUD){
		this.eURAUD = eURAUD;
	}

	public Double getEURAUD(){
		return eURAUD;
	}

	public void setEURHKD(Double eURHKD){
		this.eURHKD = eURHKD;
	}

	public Double getEURHKD(){
		return eURHKD;
	}

	@Override
 	public String toString(){
		return 
			"Quotes{" + 
			"eURMXN = '" + eURMXN + '\'' + 
			",eURAUD = '" + eURAUD + '\'' + 
			",eURHKD = '" + eURHKD + '\'' + 
			"}";
		}
}