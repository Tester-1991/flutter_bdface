package com.baidu;

/**
 * 常量类
 * Created by LL on 2018/6/14/014.
 */

public class Constant {

    //token
    public static final String TOKEN = "Token";

    //需要改动的东西 友盟不需要改变
    public static final String BASEURL = getBASEURL();

    //websocket的地址
    public static final String WEBSOCEKTURL = getWEBSOCEKTURL();

    //友盟appKey
    public static final String UMAPPKEY = "5b95e220b27b0a6669000125";

    //友盟secret
    public static final String UmSecret = "4cec15d1222273b049f7fa9319c2c0cc";

    //最后登录手机号
    public static final String LASTMOBILE = "lastmobile";

    //手机号
    public static final String MOBILE = "mobile";

    //userId
    public static final String USERID = "userId";

    //真实姓名
    public static final String IDENTITYNAME = "identityName";

    //头像地址
    public static final String USERPICURL = "userPicUrl";

    //是否进行了人脸注册
    public static final String FACEREG = "faceReg";

    //人脸已注册
    public static final int FACEALREADREG = 1;

    //飞手接受了飞行公司的邀请的公司名称
    public static final String PERSONALBELONGTOCOMPANYNAME = "personalBelongToCompanyName";

    //消息推送：空域类型(新加空域)
    public static final String AIRSPACE = "AIRSPACE";

    //消息推送：邀请类型
    public static final String INVITATION_CONFIRM = "INVITATION_CONFIRM";

    //消息推送:认证结果
    public static final String INDETITY_RESULT = "IDENTITY_RESULT";

    //消息推送：删除飞手
    public static final String DELETE_FLYER = "DELETE_FLYER";

    //消息推送：空域状态改变
    public static final String CALLCENTER = "CALLCENTER";

    //消息推送  有飞手报飞计划
    public static final String APPLY_FOR_FLIGHT = "APPLY_FOR_FLIGHT";

    //实名认证状态
    public static final String IDENTIFYSTATUS = "identityStatus";

    //未认证
    public static final int NOTYPE = 0;

    //等待认证
    public static final int PENDING = 1;

    //认证成功
    public static final int PASSED = 2;

    //认证失败
    public static final int REJECTED = 3;

    //禁飞区
    public static final String JFQ = "jfq";

    //机场
    public static final String AIRPORT = "airport";

    //危险区
    public static final String WXQ = "wxq";

    //限制区
    public static final String XZQ = "xzq";

    //固定飞场
    public static final String GDFC = "gdfc";

    //临时任务区
    public static final String LSRWQ = "lsrwq";

    //临时禁飞区
    public static final String LSJFQ = "lsjfq";

    //在飞无人机
    public static final String FLYPLANE = "flyplane";

    //登录状态
    public static final String lOGINSTATUS = "loginStatus";

    //客服电话
    public static final String AKPHONE = "4009009110";

    public static final String FINISHALL = "finishAll";

    //分页加载一次请求多少条数据
    public static final int PAGESIZE = 20;

    /**
     * 服务器地址
     */
    public static String getBASEURL() {

        if (BuildConfig.DEBUG) {

            return "http://testapp.airspace.cn/api/";
        } else {

            return "http://app.airspace.cn/api/";
        }
    }

    /**
     * wms地址
     */
    public static String getWMSBASEURL() {

        if (BuildConfig.DEBUG) {

            return "http://map-api-test.airspace.cn/";
        } else {

            return "http://map-api.airspace.cn/";
        }
    }

    /**
     * websocket地址
     */
    public static String getWEBSOCEKTURL() {

        if (BuildConfig.DEBUG) {

            return "http://testapp.airspace.cn/api/message/notice/";
        } else {

            return "http://app.airspace.cn/api/message/notice/";
        }
    }
}
