package test;

import com.ctsi.weixindemo.util.AesException;
import com.ctsi.weixindemo.util.WXBizMsgCrypt;

/**
 * 
 * @author lb
 * @version 1.0.1
 * @Description
 * @date 2017年10月17日
 *
 */
public class WeixinTest {

	public static void main(String[] args) {
		
		String dMsgSignature = "405899f799461586ae8ff49c169346283fc4af94";
		String dTimeStamp = "1508205159";
		String dNonce = "112835882";
		String echostr   = "tNzUXP66+RS8prNvLnebP2foXECYC/ogUEVLKUFf8YeBD89gAGIMjFov5F+efSiRrU69ykosbX7hw3fOdfaA7A==";
		String dEchostr = "tNzUXP66 RS8prNvLnebP2foXECYC/ogUEVLKUFf8YeBD89gAGIMjFov5F efSiRrU69ykosbX7hw3fOdfaA7A==";
		
		String sToken = "JeL9Gb5uMHJlEPmQJRPFB4x8YRmjOpJ";
		String sEncodingAESKey = "tHkKPYUgCkFAASvlTCLhN6uNykRSF8mWcckKYE7XX6o";
		String sCorpID = "ww09b6410908371219";
		try {
			WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
			String result = wxcpt.VerifyURL(dMsgSignature, dTimeStamp, dNonce, echostr);
			System.out.println(result);
		} 
		catch (AesException e) {
			e.printStackTrace();
		}

	}

}
