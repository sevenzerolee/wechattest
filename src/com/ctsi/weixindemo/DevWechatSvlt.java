package com.ctsi.weixindemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ctsi.weixindemo.util.AesException;
import com.ctsi.weixindemo.util.WXBizMsgCrypt;

/**
 * Servlet implementation class DevWechatSvlt
 */
@WebServlet("/devWechatSvlt")
public class DevWechatSvlt extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(DevWechatSvlt.class.getSimpleName());
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DevWechatSvlt() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("############################");
//		String para = request.getParameter("para");
//		System.out.println("para = " + para);
		
		String msgSignature = request.getParameter("msg_signature");
		System.out.println(msgSignature);
		String dMsgSignature = URLDecoder.decode(msgSignature);
		System.out.println(dMsgSignature);
		
		String timeStamp = request.getParameter("timestamp");
		System.out.println(timeStamp);
		String dTimeStamp = URLDecoder.decode(timeStamp);
		System.out.println(dTimeStamp);
		
		String nonce = request.getParameter("nonce");
		System.out.println(nonce);
		String dNonce = URLDecoder.decode(nonce);
		System.out.println(dNonce);
		
		String echostr = request.getParameter("echostr");
		System.out.println(echostr);
//		String dEchostr = URLDecoder.decode(echostr);
//		System.out.println(dEchostr);
		
		StringBuffer sb = new StringBuffer();
		try (
				InputStream is = request.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				) {
			String line = null;
			while ( (line = br.readLine()) != null) {
				sb.append(line);
			}
			
			System.out.println(sb.toString());
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		
		
		
		String sToken = "JeL9Gb5uMHJlEPmQJRPFB4x8YRmjOpJ";
		String sEncodingAESKey = "tHkKPYUgCkFAASvlTCLhN6uNykRSF8mWcckKYE7XX6o";
		String sCorpID = "ww09b6410908371219";
		WXBizMsgCrypt wxcpt = null;
		try {
			wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
		} 
		catch (AesException e) {
			e.printStackTrace();
		}
		
		// 验证URL是否有效
		if (null != echostr) {
			try {
				String result = wxcpt.VerifyURL(dMsgSignature, dTimeStamp, dNonce, echostr);
				System.out.println(result);
				
				response.getWriter().write(result);
			} 
			catch (AesException e) {
				e.printStackTrace();
			}
		}
		// 解密数据
		else {
			try {
				String result = wxcpt.DecryptMsg(dMsgSignature, dTimeStamp, dNonce, sb.toString());
				System.out.println(result);
				
				// 数据xml解析
				XmlObj obj = parse(result);
				log.info(obj.getContent());
				
				String time = String.valueOf(System.currentTimeMillis());
				
				// 发送信息
				XmlObj res = new XmlObj();
				res.setToUserName(obj.getFromUserName());
				res.setFromUserName(obj.getToUserName());
				res.setContent("我收到了你的信息");
				res.setCreateTime(time);
				res.setMsgType("text");
				res.setMsgId("00" + obj.getMsgId());
				res.setAgentId(obj.getAgentId());
				String xmlStr = getResponseString(res);
				
				if (null != xmlStr && !"".equals(xmlStr)) {
					String encrypt = wxcpt.EncryptMsg(xmlStr, time , nonce);
					
					response.getWriter().write(encrypt);
				}
			} 
			catch (AesException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	String getResponseString(XmlObj obj) {
		String xmlStr = "";
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.newDocument();
			
			Element xml = document.createElement("xml");
			
			Element toUserName = document.createElement("ToUserName");
			toUserName.setTextContent(obj.getToUserName());
			
			Element fromUserName = document.createElement("FromUserName");
			fromUserName.setTextContent(obj.getFromUserName());
			
			Element createTime = document.createElement("CreateTime");
			createTime.setTextContent(obj.getCreateTime());
			
			Element msgType = document.createElement("MsgType");
			msgType.setTextContent(obj.getMsgType());
			
			Element content = document.createElement("Content");
			content.setTextContent(obj.getContent());
			
			Element msgId = document.createElement("MsgId");
			msgId.setTextContent(obj.getMsgId());
			
			Element agentID = document.createElement("AgentID");
			agentID.setTextContent(obj.getAgentId());
			
			xml.appendChild(toUserName);
			xml.appendChild(fromUserName);
			xml.appendChild(createTime);
			xml.appendChild(msgType);
			xml.appendChild(content);
			xml.appendChild(msgId);
			xml.appendChild(agentID);
			
			document.appendChild(xml);
			
			TransformerFactory tff = TransformerFactory.newInstance();
			Transformer tf = tff.newTransformer();
			StringWriter sw = new StringWriter();
			tf.transform(new DOMSource(document), new StreamResult(sw));
			xmlStr = sw.getBuffer().toString();
			log.info(xmlStr);
			
			return xmlStr;
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		} 
		catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} 
		catch (TransformerException e) {
			e.printStackTrace();
		} 
		
		return xmlStr;
	}
	
	XmlObj parse(String xml) {
		if (null == xml || "".equals(xml)) {
			throw new NullPointerException("");
		}
		
		XmlObj obj = new XmlObj();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			StringReader sr = new StringReader(xml);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);

			Element root = document.getDocumentElement();
			
			NodeList nToUserName = root.getElementsByTagName("ToUserName");
			obj.setToUserName(nToUserName.item(0).getTextContent());
			
			obj.setFromUserName(getContentFromNode(root, "FromUserName"));
			obj.setCreateTime(getContentFromNode(root, "CreateTime"));
			obj.setMsgType(getContentFromNode(root, "MsgType"));
			obj.setContent(getContentFromNode(root, "Content"));
			obj.setMsgId(getContentFromNode(root, "MsgId"));
			obj.setAgentId(getContentFromNode(root, "AgentID"));
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		} 
		catch (SAXException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return obj;
	}
	
	String getContentFromNode(Element e, String tagName) {
		return e.getElementsByTagName(tagName).item(0).getTextContent();
	}

}

class XmlObj {

	private String toUserName;
	private String fromUserName;
	private String createTime;
	private String msgType;
	private String content;
	private String msgId;
	private String agentId;

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

}
