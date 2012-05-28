package com.stubhub.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.stubhub.Constants;

import android.util.Log;

public class MobileListing implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4485612989779763667L;
	private String price = "None";
	private String faceValue = "";
	private String ticket_id = "";
	private String row = "";
	private String seats = "";
	private String section = "";
	private String split = "None";
	private String deliveryMethod = "";
	private String tihDate = "None";
	private int quantity = 0;
	private List<String> disclosuresList = new ArrayList<String>();
	private List<String> barcodeList = new ArrayList<String>();
	private boolean isInHand = false;
	
	public void addBarcode(String barcode) {
		this.barcodeList.add(barcode);
	}
	public List<String> getBarcodeList() {
		return this.barcodeList;
	}
	public void removeBarcode(String barcode) {
		this.barcodeList.remove(barcode);
	}
	public boolean isInHand() {
		return this.isInHand;
	}
	public void setIsInHand(boolean b) {
		this.isInHand = b;
	}
	public void setDisclosuresList(List<String> disclosures) {
		this.disclosuresList = disclosures;
	}
	
	public List<String> getDisclosuresList() {
		return this.disclosuresList;
	}
	
	public String getSplit() {
		return this.split;
	}
	
	public void setSplit(String split) {
		this.split = split;
	}
	
	public String getTihDate() {
		return tihDate;
	}

	public void setTihDate(String tihDate) {
		this.tihDate = tihDate;
	}

	
	public MobileListing() {
		
	}
	
	public int getQuantity() {
		return quantity;
	}

	public void addQuantity() {
		this.quantity++;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public void removeQuantity() {
		this.quantity--;
	}

	public String getSection() {
		return (this.section == null) ? "" : this.section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getRow() {
		return (this.row == null) ? "" : this.row;
	}
	public void setRow(String row) {
		this.row = row;
	}
	public void addSeat(String seat) {
		if(seats==null || seats.equals("")) {
			seats = seat;
		}
		else {
			seats = seats + "," + seat;
		}
	}
	public void setSeats(String seats) {
		this.seats = seats;
	}
	public void removeSeats(String seat) {
		try {
			String[] seatArr = this.seats.split(",");
			seats = "";
			for(int i=0;i < seatArr.length;i++) {
				if(!seatArr[i].equals(seat)) {
					seats = seats + seatArr[i] + ",";
				}
			}
			seats = seats.substring(0, seats.length()-1);
		}
		catch(Exception e) {
			Log.e(Constants.TAG,"Error removing ticket seat " + Log.getStackTraceString(e));
		}
			
	}
	public String getFaceValue() {
		return this.faceValue;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getTicketId() {
		return this.ticket_id;
	}
	
	public String getSeats() {
		if(this.seats==null) {
			return "";
		}
		return this.seats;
	}
	public String getDeliveryMethod() {
		return this.deliveryMethod;
	}
	public void setDeliveryMethod(String deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}
	
	
}