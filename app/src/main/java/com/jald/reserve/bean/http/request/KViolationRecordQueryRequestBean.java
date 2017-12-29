package com.jald.reserve.bean.http.request;

import java.io.Serializable;

public class KViolationRecordQueryRequestBean implements Serializable {

	private static final long serialVersionUID = 2851855219134727237L;

	private String shop_sign;
	private String owner_car;
	private String voiture_no;
	private String engine_no;
	private String car_type;
	private String province;
	private String city_name;
	private String channel;

	public String getShop_sign() {
		return shop_sign;
	}

	public void setShop_sign(String shop_sign) {
		this.shop_sign = shop_sign;
	}

	public String getOwner_car() {
		return owner_car;
	}

	public void setOwner_car(String owner_car) {
		this.owner_car = owner_car;
	}

	public String getVoiture_no() {
		return voiture_no;
	}

	public void setVoiture_no(String voiture_no) {
		this.voiture_no = voiture_no;
	}

	public String getEngine_no() {
		return engine_no;
	}

	public void setEngine_no(String engine_no) {
		this.engine_no = engine_no;
	}

	public String getCar_type() {
		return car_type;
	}

	public void setCar_type(String car_type) {
		this.car_type = car_type;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

}
