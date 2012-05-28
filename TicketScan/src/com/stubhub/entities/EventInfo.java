package com.stubhub.entities;

import java.util.ArrayList;
import java.util.List;

import com.stubhub.Constants;

public class EventInfo {
	private List<DeliveryOption> deliveryOptions = null;
	private List<String> disclosures = new ArrayList<String>();
	
	public List<DeliveryOption> getDeliveryOptions() {
		return deliveryOptions;
	}
	public void setDeliveryOptions(List<DeliveryOption> deliveryOptions) {
		this.deliveryOptions = deliveryOptions;
	}
	public List<String> getDisclosures() {
		return disclosures;
	}
	public void setDisclosures(List<String> disclosures) {
		//disclosures.add(Constants.disclosuresList[0]);//koson
		//disclosures.add(Constants.disclosuresList[2]);//koson
		//disclosures.add(Constants.disclosuresList[3]);//koson
		//disclosures.add(Constants.disclosuresList[4]);//koson
		//disclosures.add(Constants.disclosuresList[6]);//koson
		//disclosures.add(Constants.disclosuresList[8]);//koson
		this.disclosures = disclosures;
	}

}
