package com.jald.reserve.bean.http.response;

import java.io.Serializable;

public class KHighwayApplyQueryResponseBean implements Serializable {

	private static final long serialVersionUID = 3109040148938108685L;

	private String status;
	private String car_num;
	private String cust_no;
	private String comment;
	private String id_number;
	private String manager;
	private String address;
	private String id_number_front_url;
	private String id_number_reverse_url;
	private String driving_url;
	private String vehicle_url;
	private String bank_card_url;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCar_num() {
		return car_num;
	}

	public void setCar_num(String car_num) {
		this.car_num = car_num;
	}

	public String getCust_no() {
		return cust_no;
	}

	public void setCust_no(String cust_no) {
		this.cust_no = cust_no;
	}

	public String getId_number() {
		return id_number;
	}

	public void setId_number(String id_number) {
		this.id_number = id_number;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getAddress() {
		return address;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getId_number_front_url() {
		return id_number_front_url;
	}

	public void setId_number_front_url(String id_number_front_url) {
		this.id_number_front_url = id_number_front_url;
	}

	public String getId_number_reverse_url() {
		return id_number_reverse_url;
	}

	public void setId_number_reverse_url(String id_number_reverse_url) {
		this.id_number_reverse_url = id_number_reverse_url;
	}

	public String getDriving_url() {
		return driving_url;
	}

	public void setDriving_url(String driving_url) {
		this.driving_url = driving_url;
	}

	public String getVehicle_url() {
		return vehicle_url;
	}

	public void setVehicle_url(String vehicle_url) {
		this.vehicle_url = vehicle_url;
	}

	public String getBank_card_url() {
		return bank_card_url;
	}

	public void setBank_card_url(String bank_card_url) {
		this.bank_card_url = bank_card_url;
	}

}
