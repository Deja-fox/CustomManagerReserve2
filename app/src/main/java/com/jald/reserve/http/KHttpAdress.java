package com.jald.reserve.http;

public class KHttpAdress {

    public static final int PUBLIC_LEVEL = 0;
    public static final int ENCRYPT_LEVEL = 1;
    public static final int LOGIN_LEVEL = 4;
    public static final int SMS_LEVEL = 5;

    // 烟草订货测试环境
    //public static final String TOBOCCO_URL = "http://221.214.50.59:7000/user/oauth?token={#1}&redirect=mtobacco&from=app";
    // 烟草订货生产环境
    public static final String TOBOCCO_URL = "http://www.youmkt.com/user/oauth?token={#1}&redirect=mtobacco&from=app";
    // 内网开发环境
    // public static final String DOMAIN = "http://192.168.1.96:8080";
    // 外正式环境
//    public static final String DOMAIN = "http://221.214.50.60:9080";
    // 外测试环境
    public static final String DOMAIN = "http://221.214.50.59:9088";
    //外正式需加密，外测试不需要加密
    public static final boolean isEncrypt = true;

    private static final String PERFIX_PATH = "/mbx/rest/resource/";
    public static final String PERFIX_URL = DOMAIN + PERFIX_PATH;

    private static final String SYS_INFO_PORT = PERFIX_URL + "system/config/" + PUBLIC_LEVEL + "/v2";
    private static final String LOGIN_PORT = PERFIX_URL + "user/login/" + LOGIN_LEVEL + "/v2";
    private static final String ACCOUNT_QUERY_PORT = PERFIX_URL + "account/query/" + ENCRYPT_LEVEL + "/v2";
    private static final String BANK_LIST_PORT = PERFIX_URL + "bank/list/" + PUBLIC_LEVEL + "/v2";
    private static final String UPDATE_LOGIN_PASSWORD_PORT = PERFIX_URL + "loginpwd/update/" + ENCRYPT_LEVEL + "/v2";
    private static final String SEND_SMS_PORT = PERFIX_URL + "sms/send/" + PUBLIC_LEVEL + "/v2";
    private static final String BIND_TELPHONE_PORT = PERFIX_URL + "tel/bind/" + ENCRYPT_LEVEL + "/v2";
    private static final String UPDATE_PAY_PASSWORD_PORT = PERFIX_URL + "paypwd/update/" + ENCRYPT_LEVEL + "/v2";
    private static final String FINANCING_RECENTLY_PORT = PERFIX_URL + "financing/recently/" + ENCRYPT_LEVEL + "/v2";
    private static final String PAY_LIST_POST = PERFIX_URL + "pay/list/" + ENCRYPT_LEVEL + "/v2";
    private static final String RECENTLY_LIST_POST = PERFIX_URL + "financing/list/" + ENCRYPT_LEVEL + "/v2";
    private static final String ORDER_INFO_POST = PERFIX_URL + "financing/detail/" + ENCRYPT_LEVEL + "/v2";
    private static final String BANKS_QUERY_PORT = PERFIX_URL + "debitaccount/list/" + ENCRYPT_LEVEL + "/v2";
    private static final String BANK_CARD_BIND_PORT = PERFIX_URL + "debitaccount/bind/" + ENCRYPT_LEVEL + "/v2";
    private static final String RECHARGE_PORT = PERFIX_URL + "account/recharge/" + ENCRYPT_LEVEL + "/v2";
    private static final String REPAYMENT_POST = PERFIX_URL + "account/repayment/" + ENCRYPT_LEVEL + "/v2";
    private static final String RECHARGE_RECORD_POST = PERFIX_URL + "recharge/list/" + ENCRYPT_LEVEL + "/v2";
    private static final String USER_UPDATE_POST = PERFIX_URL + "user/update/" + ENCRYPT_LEVEL + "/v2";
    private static final String USER_REGISTER_PORT = PERFIX_URL + "user/register/" + SMS_LEVEL + "/v2";
    private static final String TEL_CHARGE_AMOUNTLIST_PORT = PERFIX_URL + "tel/fee/" + PUBLIC_LEVEL + "/v2";
    private static final String TEL_CHARGE_VALIDATE_PORT = PERFIX_URL + "tel/rechargeVer/" + ENCRYPT_LEVEL + "/v2";
    private static final String TEL_CHARGE_PORT = PERFIX_URL + "tel/recharge/" + ENCRYPT_LEVEL + "/v2";
    private static final String TEL_CHARGE_LIST_PORT = PERFIX_URL + "tel/rechargeTransList/" + ENCRYPT_LEVEL + "/v2";
    private static final String LOGIN_PWD_UPDATE_PORT = PERFIX_URL + "loginpwd/update/" + ENCRYPT_LEVEL + "/v2";
    private static final String PAY_PWD_UPDATE_PORT = PERFIX_URL + "paypwd/update/" + ENCRYPT_LEVEL + "/v2";
    private static final String FINANCING_TRANSLIST_PORT = PERFIX_URL + "finance/transList/" + ENCRYPT_LEVEL + "/v2";
    private static final String AD_LIST_PORT = PERFIX_URL + "user/advertising/" + PUBLIC_LEVEL + "/v2";
    private static final String HIGHWAY_APPLY_QUERY_PORT = PERFIX_URL + "sdhs/businessApplyQuery/" + ENCRYPT_LEVEL + "/v2";
    private static final String HIGHWAY_APPLY_UPDATE_PORT = PERFIX_URL + "sdhs/businessApplyUpdate/" + ENCRYPT_LEVEL + "/v2";
    private static final String HIGHWAY_APPLY_FILEUPLOAD_PORT = PERFIX_URL + "sdhs/businessFileupload/" + PUBLIC_LEVEL + "/v2";
    private static final String VIOLATION_QUERY_PORT = PERFIX_URL + "violation/query/" + ENCRYPT_LEVEL + "/v2";
    private static final String TIBACCO_BILL_QUERY_PORT = PERFIX_URL + "tobacco/ordering/" + ENCRYPT_LEVEL + "/v2";
    private static final String TOBACCO_ORDER_PAY_CHANNEL_PORT = PERFIX_URL + "tobacco/payChannel/" + ENCRYPT_LEVEL + "/v2";
    private static final String TOBACCO_NANYUE_PAY_PORT = PERFIX_URL + "tobacco/nanYuePay/" + ENCRYPT_LEVEL + "/v2";
    private static final String TOBACCO_HISTORY_ORDER_QUERY_PORT = PERFIX_URL + "tobacco/orders/" + ENCRYPT_LEVEL + "/v2";
    private static final String TOBACCO_PER_MONTH_ORDER_INFO_PORT = PERFIX_URL + "tobacco/orderMonth/" + ENCRYPT_LEVEL + "/v2";
    private static final String TOBACCO_COMMODITY_CITY_RANK_PORT = PERFIX_URL + "tobacco/itemComRanking/" + ENCRYPT_LEVEL + "/v2";
    private static final String TOBACCO_COMMODITY_CUST_RANK_PORT = PERFIX_URL + "tobacco/itemCustRanking/" + ENCRYPT_LEVEL + "/v2";
    private static final String TOBACCO_CUST_RANK_PORT = PERFIX_URL + "tobacco/custRanking/" + ENCRYPT_LEVEL + "/v2";
    private static final String TOBACCO_TOPAY_ORDER_COUNT_PORT = PERFIX_URL + "tobacco/orderingCount/" + ENCRYPT_LEVEL + "/v2";
    private static final String TOBACCO_CUST_INFO_PORT = PERFIX_URL + "tobacco/custInfo/" + ENCRYPT_LEVEL + "/v2";
    private static final String ITEM_WARNING_PORT = PERFIX_URL + "tobacco/itemWarning/" + ENCRYPT_LEVEL + "/v2";
    private static final String WAIT_TO_RECEIVE_ORDER_QUERY_PORT = PERFIX_URL + "harvested/list/" + ENCRYPT_LEVEL + "/v2";
    private static final String SET_LAT_LNG_PORT = PERFIX_URL + "user/lngLatUpdate/" + ENCRYPT_LEVEL + "/v2";
    private static final String RECEIVE_CONFIRM_PORT = PERFIX_URL + "harvested/confirm/" + ENCRYPT_LEVEL + "/v2";
    // 网页接口
    // 注册的时候设置南粤电子账户支付密码和绑卡接口
    private static final String SET_NANYUE_EACCOUNT_PWD_PORT = DOMAIN + "/mbx/nanyue/nanYueServlet.html?method=setPasswordReq";
    private static final String NANYUE_EACCOUNT_BIND_CARD_PORT = DOMAIN + "/mbx/nanyue/nanYueServlet.html?method=cardReq";
    // 南粤我的银行卡接口
    private static final String NANYUE_EACCOUNT_MY_CARD_PORT = DOMAIN + "/mbx/nanyue/nanYueServlet.html?method=myCardReq";
    // 修改交易密码
    private static final String NANYUE_EACCOUNT_CHANGE_PWD_PORT = DOMAIN + "/mbx/nanyue/nanYueServlet.html?method=updatePasswordReq";
    // 重置交易密码
    private static final String NANYUE_EACCOUNT_RESET_PWD_PORT = DOMAIN + "/mbx/nanyue/nanYueServlet.html?method=resetPasswordReq";
    // 充值接口
    private static final String NANYUE_EACCOUNT_CHARGE_PORT = DOMAIN + "/mbx/nanyue/nanYueServlet.html?method=rechargeReq";
    // 取现接口
    private static final String NANYUE_EACCOUNT_WITHDRAW_PORT = DOMAIN + "/mbx/nanyue/nanYueServlet.html?method=withdrawReq";
    // 南粤电子银行绑卡开户接口
    private static final String NANYUE_OPEN_EACCOUNT_PORT = PERFIX_URL + "nanyue/openAccount/" + ENCRYPT_LEVEL + "/v2";
    //  网页接口：登录后在账户管理模块的设置南粤电子账户支付密码和绑卡接口
    private static final String SET_NANYUE_EACCOUNT_PWD_SESSION_PORT = DOMAIN + "/mbx/nanyue/nanYueServlet.html?method=setPasswordSessionReq";
    private static final String NANYUE_EACCOUNT_BIND_CARD_SESSION_PORT = DOMAIN + "/mbx/nanyue/nanYueServlet.html?method=cardSessionReq";

    private static final String NANYUE_LOAN_LIST_PORT = PERFIX_URL + "loan/list/" + ENCRYPT_LEVEL + "/v2";
    // 南粤还款发送短信验证码
    private static final String NANYUE_PAY_SMS_PORT = PERFIX_URL + "nanyue/sms/" + ENCRYPT_LEVEL + "/v2";
    // 南粤还款
    private static final String NANYUE_LOAN_PAY_PORT = PERFIX_URL + "nanyue/repayment/" + ENCRYPT_LEVEL + "/v2";
    // 优市网订单查询接口
    private static final String YOUMKT_ORDER_LIST_PORT = PERFIX_URL + "youmkt/orderList/" + ENCRYPT_LEVEL + "/v2";
    private static final String ORDER_PAY_MODEL_LIST_PORT = PERFIX_URL + "pay/modeList/" + ENCRYPT_LEVEL + "/v2";
    // 新南粤支付接口
    private static final String NANYUE_PAY_PORT = PERFIX_URL + "nanyue/pay/" + ENCRYPT_LEVEL + "/v2";
    // 白条以及货到付款列表查询接口
    private static final String GRANT_TRANS_LIST_PORT = PERFIX_URL + "grant/transList/" + ENCRYPT_LEVEL + "/v2";
    // 白条以及货到付款支付
    private static final String GRANT_PAY_PORT = PERFIX_URL + "grant/pay/" + ENCRYPT_LEVEL + "/v2";
    // 获取客户模糊信息接口(重置密码用)
    private static final String USER_INFO_QUERY_PORT = PERFIX_URL + "user/infoQuery/" + PUBLIC_LEVEL + "/v2";
    // 重置登录密码接口
    private static final String RESET_LOGIN_PWD_PORT = PERFIX_URL + "user/resetLoginPassword/" + PUBLIC_LEVEL + "/v2";


    //######################### 客户经理订货相关的接口 #####################################
    private static final String SLSMAN_CUST_LIST_PORT = PERFIX_URL + "youmkt/slsmanCustList/" + ENCRYPT_LEVEL + "/v2";
    private static final String SLSMAN_ORDER_LIST_PORT = PERFIX_URL + "youmkt/slsmanOrderList/" + ENCRYPT_LEVEL + "/v2";
    private static final String GOODS_TYPE_LIST_PORT = PERFIX_URL + "youmkt/itemTypeList/" + +ENCRYPT_LEVEL + "/v2";
    private static final String GET_GOODS_LIST_BY_TYPE_PORT = PERFIX_URL + "youmkt/typeItemList/" + +ENCRYPT_LEVEL + "/v2";
    private static final String ORDER_ADD_PORT = PERFIX_URL + "youmkt/slsmanOrderAdd/" + +ENCRYPT_LEVEL + "/v2";
    private static final String SLSMAN_ORDER_DELETE_PORT = PERFIX_URL + "youmkt/slsmanOrderDelete/" + +ENCRYPT_LEVEL + "/v2";
    private static final String GET_ALL_GOODS_LIST_PORT = PERFIX_URL + "youmkt/allItemList/" + +ENCRYPT_LEVEL + "/v2";
    //##################################################################################
    //公共接口
    private static final String COMMON_INFO_PORT = PERFIX_URL + "dkcommon/dkcommoninfo/" + PUBLIC_LEVEL + "/v2";
    private static final String CHECK_INSURANCE_PORT = PERFIX_URL + "user/insuranceCheck/" + ENCRYPT_LEVEL + "/v2";


    public static final int SYS_INFO = 0;
    public static final int LOGIN = 1;
    public static final int ACCOUNT_QUERY = 2;
    public static final int BANK_LIST = 3;
    public static final int UPDATE_LOGIN_PASSWORD = 4;
    public static final int SEND_SMS = 5;
    public static final int BIND_TELPHONE = 6;
    public static final int UPDATE_PAY_PASSWORD = 7;
    public static final int FINANCING_RECENTLY = 8;
    public static final int PAY_LIST = 9;
    public static final int RECENTLY_LIST = 10;
    public static final int ORDER_INFO = 11;
    public static final int BANKS_QUERY = 12;
    public static final int BANK_CARD_BIND = 13;
    public static final int RECHARGE = 14;
    public static final int REPAYMENT = 15;
    public static final int RECHARGE_RECORD = 16;
    public static final int USER_UPDATE = 17;
    public static final int USER_REGISTER = 18;
    public static final int TEL_CHARGE_AMOUNTLIST = 19;
    public static final int TEL_CHARGE_VALIDATE = 20;
    public static final int TEL_CHARGE = 21;
    public static final int TEL_CHARGE_LIST = 22;
    public static final int LOGIN_PWD_UPDATE = 23;
    public static final int PAY_PWD_UPDATE = 24;
    public static final int FINANCING_TRANSLIST = 25;
    public static final int AD_LIST = 26;
    public static final int HIGHWAY_APPLY_QUERY = 27;
    public static final int HIGHWAY_APPLY_UPDATE = 28;
    public static final int HIGHWAY_APPLY_FILEUPLOAD = 29;
    public static final int VIOLATION_QUERY = 30;
    public static final int TOBACCO_ORDER_QUERY = 31;
    public static final int TOBACCO_ORDER_PAY_CHANNEL = 32;
    public static final int TOBACCO_NANYUE_PAY = 33;
    public static final int TOBACCO_HISTORY_ORDER_QUERY = 34;
    public static final int TOBACCO_PER_MONTH_ORDER_INFO = 35;
    public static final int TOBACCO_COMMODITY_CITY_RANK = 36;
    public static final int TOBACCO_COMMODITY_CUST_RANK = 37;
    public static final int TOBACCO_CUST_RANK = 38;
    public static final int TOBACCO_TOPAY_ORDER_COUNT = 39;
    public static final int TOBACCO_CUST_INFO = 40;
    public static final int ITEM_WARNING = 41;
    public static final int WAIT_TO_RECEIVE_ORDER_QUERY = 42;
    public static final int SET_LAT_LNG = 43;
    public static final int RECEIVE_CONFIRM = 44;
    public static final int SET_NANYUE_EACCOUNT_PWD = 45;
    public static final int NANYUE_EACCOUNT_BIND_CARD = 46;
    public static final int NANYUE_OPEN_EACCOUNT = 47;
    public static final int SET_NANYUE_EACCOUNT_PWD_SESSION = 48;
    public static final int NANYUE_EACCOUNT_BIND_CARD_SESSION = 49;
    public static final int NANYUE_LOAN_LIST = 50;
    public static final int NANYUE_PAY_SMS = 51;
    public static final int NANYUE_LOAN_PAY = 52;
    public static final int NANYUE_EACCOUNT_MY_CARD = 53;
    public static final int NANYUE_EACCOUNT_CHANGE_PWD = 54;
    public static final int NANYUE_EACCOUNT_RESET_PWD = 55;
    public static final int NANYUE_EACCOUNT_CHARGE = 56;
    public static final int NANYUE_EACCOUNT_WITHDRAW = 57;
    public static final int YOUMKT_ORDER_LIST = 58;
    public static final int ORDER_PAY_MODEL_LIST = 59;
    public static final int NANYUE_PAY = 60;
    public static final int GRANT_TRANS_LIST = 61;
    public static final int GRANT_PAY = 62;
    public static final int USER_INFO_QUERY = 63;
    public static final int RESET_LOGIN_PWD = 64;

    public static final int SLSMAN_CUST_LIST = 65;
    public static final int SLSMAN_ORDER_LIST = 66;
    public static final int GOODS_TYPE_LIST = 67;
    public static final int GET_GOODS_LIST_BY_TYPE = 68;
    public static final int ORDER_ADD = 69;
    public static final int SLSMAN_ORDER_DELETE = 70;
    public static final int GET_ALL_GOODS_LIST = 71;
    public static final int COMMON_INFO = 72;
    public static final int CHECK_INSURANCE = 73;


    public static final String[] URLS = {SYS_INFO_PORT, LOGIN_PORT, ACCOUNT_QUERY_PORT, BANK_LIST_PORT, UPDATE_LOGIN_PASSWORD_PORT, SEND_SMS_PORT, BIND_TELPHONE_PORT, UPDATE_PAY_PASSWORD_PORT, FINANCING_RECENTLY_PORT, PAY_LIST_POST, RECENTLY_LIST_POST, ORDER_INFO_POST, BANKS_QUERY_PORT, BANK_CARD_BIND_PORT, RECHARGE_PORT, REPAYMENT_POST, RECHARGE_RECORD_POST, USER_UPDATE_POST, USER_REGISTER_PORT, TEL_CHARGE_AMOUNTLIST_PORT, TEL_CHARGE_VALIDATE_PORT, TEL_CHARGE_PORT, TEL_CHARGE_LIST_PORT, LOGIN_PWD_UPDATE_PORT, PAY_PWD_UPDATE_PORT, FINANCING_TRANSLIST_PORT, AD_LIST_PORT, HIGHWAY_APPLY_QUERY_PORT, HIGHWAY_APPLY_UPDATE_PORT, HIGHWAY_APPLY_FILEUPLOAD_PORT, VIOLATION_QUERY_PORT, TIBACCO_BILL_QUERY_PORT, TOBACCO_ORDER_PAY_CHANNEL_PORT, TOBACCO_NANYUE_PAY_PORT, TOBACCO_HISTORY_ORDER_QUERY_PORT, TOBACCO_PER_MONTH_ORDER_INFO_PORT, TOBACCO_COMMODITY_CITY_RANK_PORT, TOBACCO_COMMODITY_CUST_RANK_PORT, TOBACCO_CUST_RANK_PORT, TOBACCO_TOPAY_ORDER_COUNT_PORT, TOBACCO_CUST_INFO_PORT, ITEM_WARNING_PORT, WAIT_TO_RECEIVE_ORDER_QUERY_PORT, SET_LAT_LNG_PORT, RECEIVE_CONFIRM_PORT, SET_NANYUE_EACCOUNT_PWD_PORT, NANYUE_EACCOUNT_BIND_CARD_PORT, NANYUE_OPEN_EACCOUNT_PORT, SET_NANYUE_EACCOUNT_PWD_SESSION_PORT, NANYUE_EACCOUNT_BIND_CARD_SESSION_PORT, NANYUE_LOAN_LIST_PORT, NANYUE_PAY_SMS_PORT, NANYUE_LOAN_PAY_PORT, NANYUE_EACCOUNT_MY_CARD_PORT, NANYUE_EACCOUNT_CHANGE_PWD_PORT, NANYUE_EACCOUNT_RESET_PWD_PORT, NANYUE_EACCOUNT_CHARGE_PORT, NANYUE_EACCOUNT_WITHDRAW_PORT, YOUMKT_ORDER_LIST_PORT, ORDER_PAY_MODEL_LIST_PORT, NANYUE_PAY_PORT, GRANT_TRANS_LIST_PORT, GRANT_PAY_PORT, USER_INFO_QUERY_PORT, RESET_LOGIN_PWD_PORT, SLSMAN_CUST_LIST_PORT, SLSMAN_ORDER_LIST_PORT, GOODS_TYPE_LIST_PORT, GET_GOODS_LIST_BY_TYPE_PORT, ORDER_ADD_PORT, SLSMAN_ORDER_DELETE_PORT, GET_ALL_GOODS_LIST_PORT, COMMON_INFO_PORT, CHECK_INSURANCE_PORT};

    public static final int[] LEVELS = {PUBLIC_LEVEL// SYS_INFO_PORT
            , LOGIN_LEVEL // LOGIN_PORT
            , ENCRYPT_LEVEL// ACCOUNT_QUERY_PORT
            , PUBLIC_LEVEL // BANK_LIST_PORT
            , ENCRYPT_LEVEL // UPDATE_LOGIN_PASSWORD_PORT
            , PUBLIC_LEVEL // SEND_SMS_PORT
            , ENCRYPT_LEVEL // BIND_TELPHONE_PORT
            , ENCRYPT_LEVEL // UPDATE_PAY_PASSWORD_PORT
            , ENCRYPT_LEVEL// FINANCING_RECENTLY_PORT
            , ENCRYPT_LEVEL // PAY_LIST_POST
            , ENCRYPT_LEVEL // RECENTLY_LIST_POST
            , ENCRYPT_LEVEL // ORDER_INFO_POST
            , ENCRYPT_LEVEL // BANKS_QUERY_POST
            , ENCRYPT_LEVEL // BANK_CARD_BIND_POST
            , ENCRYPT_LEVEL // RECHARGE_POST
            , ENCRYPT_LEVEL // REPAYMENT_POST
            , ENCRYPT_LEVEL // RECHARGE_RECORD_POST 1
            , ENCRYPT_LEVEL // USER_UPDATE_POST
            , SMS_LEVEL // USER_REGISTER_PORT
            , PUBLIC_LEVEL // TEL_CHARGE_AMOUNTLIST_PORT
            , ENCRYPT_LEVEL // TEL_CHARGE_VALIDATE_PORT
            , ENCRYPT_LEVEL // TEL_CHARGE_PORT
            , ENCRYPT_LEVEL // TEL_CHARGE_LIST_PORT
            , ENCRYPT_LEVEL // LOGIN_PWD_UPDATE_PORT
            , ENCRYPT_LEVEL // PAY_PWD_UPDATE_PORT
            , ENCRYPT_LEVEL // FINANCING_TRANSLIST_PORT
            , PUBLIC_LEVEL // AD_LIST_PORT
            , ENCRYPT_LEVEL // AD_LIST_PORT
            , ENCRYPT_LEVEL // HIGHWAY_APPLY_UPDATE_PORT
            , PUBLIC_LEVEL // HIGHWAY_APPLY_FILEUPLOAD_PORT
            , ENCRYPT_LEVEL // VIOLATION_QUERY_PORT
            , ENCRYPT_LEVEL // TIBACCO_BILL_QUERY_PORT
            , ENCRYPT_LEVEL // TOBACCO_ORDER_PAY_CHANNEL_PORT
            , ENCRYPT_LEVEL // TOBACCO_NANYUE_PAY_PORT
            , ENCRYPT_LEVEL // TIBACCO_HISTORY_ORDER_QUERY_PORT
            , ENCRYPT_LEVEL // TOBACCO_PER_MONTH_ORDER_INFO_PORT
            , ENCRYPT_LEVEL // TOBACCO_COMMODITY_CITY_RANK_PORT
            , ENCRYPT_LEVEL // TOBACCO_COMMODITY_CUST_RANK_PORT
            , ENCRYPT_LEVEL // TOBACCO_CUST_RANK_PORT
            , ENCRYPT_LEVEL // TOBACCO_TOPAY_ORDER_COUNT_PORT
            , ENCRYPT_LEVEL // TOBACCO_CUST_INFO_PORT
            , ENCRYPT_LEVEL // ITEM_WARNING
            , ENCRYPT_LEVEL // WAIT_TO_RECEIVE_ORDER_QUERY_PORT
            , ENCRYPT_LEVEL // SET_LAT_LNG_PORT
            , ENCRYPT_LEVEL // RECEIVE_CONFIRM_PORT
            , ENCRYPT_LEVEL // SET_NANYUE_EACCOUNT_PWD_PORT
            , ENCRYPT_LEVEL // NANYUE_EACCOUNT_BIND_CARD_PORT
            , ENCRYPT_LEVEL // NANYUE_OPEN_EACCOUNT_PORT
            , ENCRYPT_LEVEL // SET_NANYUE_EACCOUNT_PWD_SESSION_PORT
            , ENCRYPT_LEVEL // NANYUE_EACCOUNT_BIND_CARD_SESSION_PORT
            , ENCRYPT_LEVEL // NANYUE_LOAN_LIST_PORT
            , ENCRYPT_LEVEL // NANYUE_PAY_SMS_PORT
            , ENCRYPT_LEVEL // NANYUE_LOAN_PAY_PORT
            , ENCRYPT_LEVEL // NANYUE_EACCOUNT_MY_CARD_PORT
            , ENCRYPT_LEVEL // NANYUE_EACCOUNT_CHANGE_PWD_PORT
            , ENCRYPT_LEVEL // NANYUE_EACCOUNT_RESET_PWD_PORT
            , ENCRYPT_LEVEL // NANYUE_EACCOUNT_CHARGE_PORT
            , ENCRYPT_LEVEL // NANYUE_EACCOUNT_WITHDRAW_PORT
            , ENCRYPT_LEVEL // YOUMKT_ORDER_LIST_PORT
            , ENCRYPT_LEVEL // ORDER_PAY_MODEL_LIST_PORT
            , ENCRYPT_LEVEL // NANYUE_PAY_PORT
            , ENCRYPT_LEVEL // GRANT_TRANS_LIST_PORT
            , ENCRYPT_LEVEL // GRANT_PAY_PORT
            , PUBLIC_LEVEL // USER_INFO_QUERY_PORT
            , PUBLIC_LEVEL // RESET_LOGIN_PWD_PORT
            , ENCRYPT_LEVEL // SLSMAN_CUST_LIST_PORT
            , ENCRYPT_LEVEL // SLSMAN_ORDER_LIST_PORT
            , ENCRYPT_LEVEL // GOODS_TYPE_LIST_PORT
            , ENCRYPT_LEVEL // GET_GOODS_LIST_BY_TYPE_PORT
            , ENCRYPT_LEVEL // ORDER_ADD_PORT
            , ENCRYPT_LEVEL // SLSMAN_ORDER_DELETE_PORT
            , ENCRYPT_LEVEL // GET_ALL_GOODS_LIST_PORT
            , PUBLIC_LEVEL //COMMON_INFO_PORT
            , ENCRYPT_LEVEL //CHECK_INSURANCE_PORT
    };
}