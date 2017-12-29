package com.jald.reserve.bean.http.response;

import java.io.Serializable;
import java.util.List;

public class KViolationPaymentQueryResponseBean implements Serializable {
	private static final long serialVersionUID = -2379989538887157847L;

	private String mail_type_name;
	private String postage;
	private String violations_mlatefee;
	private String total_amount;
	private List<ViolationPaymentItem> list;

	public static class ViolationPaymentItem implements Serializable {
		private static final long serialVersionUID = 1L;

		public ViolationPaymentItem() {

		}

		public ViolationPaymentItem(String temp_id, String shop_sign, String voiture_type_name, String regulation_name, String fine_amount,
				String mlate_fee, String pay_charge, String charge) {
			super();
			this.temp_id = temp_id;
			this.shop_sign = shop_sign;
			this.voiture_type_name = voiture_type_name;
			this.regulation_name = regulation_name;
			this.fine_amount = fine_amount;
			this.mlate_fee = mlate_fee;
			this.pay_charge = pay_charge;
			this.charge = charge;
		}

		private String temp_id;
		private String shop_sign;
		private String voiture_type_name;
		private String regulation_name;
		private String fine_amount;
		private String mlate_fee;
		private String pay_charge;
		private String charge;

		public String getTemp_id() {
			return temp_id;
		}

		public void setTemp_id(String temp_id) {
			this.temp_id = temp_id;
		}

		public String getShop_sign() {
			return shop_sign;
		}

		public void setShop_sign(String shop_sign) {
			this.shop_sign = shop_sign;
		}

		public String getVoiture_type_name() {
			return voiture_type_name;
		}

		public void setVoiture_type_name(String voiture_type_name) {
			this.voiture_type_name = voiture_type_name;
		}

		public String getRegulation_name() {
			return regulation_name;
		}

		public void setRegulation_name(String regulation_name) {
			this.regulation_name = regulation_name;
		}

		public String getFine_amount() {
			return fine_amount;
		}

		public void setFine_amount(String fine_amount) {
			this.fine_amount = fine_amount;
		}

		public String getMlate_fee() {
			return mlate_fee;
		}

		public void setMlate_fee(String mlate_fee) {
			this.mlate_fee = mlate_fee;
		}

		public String getPay_charge() {
			return pay_charge;
		}

		public void setPay_charge(String pay_charge) {
			this.pay_charge = pay_charge;
		}

		public String getCharge() {
			return charge;
		}

		public void setCharge(String charge) {
			this.charge = charge;
		}

	}

	public String getMail_type_name() {
		return mail_type_name;
	}

	public void setMail_type_name(String mail_type_name) {
		this.mail_type_name = mail_type_name;
	}

	public String getPostage() {
		return postage;
	}

	public void setPostage(String postage) {
		this.postage = postage;
	}

	public String getViolations_mlatefee() {
		return violations_mlatefee;
	}

	public void setViolations_mlatefee(String violations_mlatefee) {
		this.violations_mlatefee = violations_mlatefee;
	}

	public String getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}

	public List<ViolationPaymentItem> getList() {
		return list;
	}

	public void setList(List<ViolationPaymentItem> list) {
		this.list = list;
	}

}
