package com.jald.reserve.bean.normal;

import android.util.Log;

import com.jald.reserve.bean.http.response.KLoginResponseBean;
import com.jald.reserve.util.MD5Tools;
import com.jald.reserve.util.RSAUtil;

import java.io.Serializable;

public class KUserInfoStub implements Serializable {

    public static final String TAG = KUserInfoStub.class.getSimpleName();

    private static final long serialVersionUID = -311310208226563663L;
    private String time;
    private String custId;
    private String name;
    private String state;
    private String telephone;
    private boolean isTelphoneBinding;
    private String pwdStatus;
    private String lastLoginTime;
    private String lastLoginIp;
    public String loginName;
    public String uuid;
    public String userName;
    public String password;
    public String payPassword;
    public String tempPassword;
    public String smsCode;
    private String lice_id;
    private String token;
    private String manager;
    private String id_number;
    private String user_lng;
    private String user_lat;
    private String active_ny;
    private String mtpId;
    private String stpId;
    private String acct_no_id;
    private String tp_id;
    private String stplist;

    private String supplier_note;

    public String getSupplier_note() {
        return supplier_note;
    }

    public void setSupplier_note(String supplier_note) {
        this.supplier_note = supplier_note;
    }

    public String getStplist() {
        return stplist;
    }

    public void setStplist(String stplist) {
        this.stplist = stplist;
    }

    public KUserInfoStub() {
        this.uuid = "SYSTEM";
    }

    public KUserInfoStub(String loginName, String loginPassword) throws Exception {
        this.uuid = loginName;
        this.password = RSAUtil.encryptByPublicKey(MD5Tools.MD5(loginPassword));
    }

    public KUserInfoStub(String loginName, String loginPassword, KLoginResponseBean bean) {
        this.loginName = loginName;
        try {
            this.password = RSAUtil.encryptByPublicKey(MD5Tools.MD5(loginPassword));
        } catch (Exception e) {
            Log.e(TAG, "将登录后的密码公钥加密存放到KUserInfoStub时失败,将不保存密码到KUserInfoStub.");
            this.password = "";
        }
        this.time = bean.getTime();
        this.custId = bean.getCust_id();
        this.name = bean.getName();
        this.state = bean.getState();
        this.isTelphoneBinding = bean.getIs_tel_bind().equals("1");
        this.pwdStatus = bean.getPwd_status();
        this.lastLoginTime = bean.getLast_login_time();
        this.lastLoginIp = bean.getLast_login_ip();
        this.uuid = bean.getUuid();
        this.userName = bean.getName();
        this.tempPassword = bean.getTemp_password();
        this.telephone = bean.getTelephone();
        this.lice_id = bean.getLice_id();
        this.token = bean.getToken();
        this.id_number = bean.getId_number();
        this.manager = bean.getManager();
        this.user_lat = bean.getUser_lat();
        this.user_lng = bean.getUser_lng();
        this.active_ny = bean.getActive_ny();
        this.acct_no_id = bean.getAcct_no_id();
        this.tp_id = bean.getTp_id();

    }

    public String getStpId() {
        return stpId;
    }

    public void setStpId(String stpId) {
        this.stpId = stpId;
    }

    public String getMtpId() {
        return mtpId;
    }

    public void setMtpId(String mtpId) {
        this.mtpId = mtpId;
    }

    public String getUser_lng() {
        return user_lng;
    }

    public void setUser_lng(String user_lng) {
        this.user_lng = user_lng;
    }

    public String getUser_lat() {
        return user_lat;
    }

    public void setUser_lat(String user_lat) {
        this.user_lat = user_lat;
    }

    public void setPwdStatus(String pwdStatus) {
        this.pwdStatus = pwdStatus;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getActive_ny() {
        return active_ny;
    }

    public void setActive_ny(String active_ny) {
        this.active_ny = active_ny;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isTelphoneBinding() {
        return isTelphoneBinding;
    }

    public void setTelphoneBinding(boolean isTelphoneBinding) {
        this.isTelphoneBinding = isTelphoneBinding;
    }

    public String getPwdStatus() {
        return pwdStatus;
    }

    public void setPwdDtatus(String pwdStatus) {
        this.pwdStatus = pwdStatus;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPayPassword() {
        return payPassword;
    }

    public void setPayPassword(String payPassword) {
        this.payPassword = payPassword;
    }

    public String getTempPassword() {
        return tempPassword;
    }

    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getLice_id() {
        return lice_id;
    }

    public void setLice_id(String lice_id) {
        this.lice_id = lice_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getAcct_no_id() {
        return acct_no_id;
    }

    public void setAcct_no_id(String acct_no_id) {
        this.acct_no_id = acct_no_id;
    }

    public String getTp_id() {
        return tp_id;
    }

    public void setTp_id(String tp_id) {
        this.tp_id = tp_id;
    }
}