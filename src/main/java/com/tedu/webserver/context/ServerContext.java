package com.tedu.webserver.context;
/** 服务端相关信息定义 */

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ServerContext {
	
	// Servlet与请求地址之间的映射关系  即存放请求的业务的类的映射关系 servlets.xml
	private static Map<String, String> servletMapping = new HashMap<String, String>();
	
	/** 初始化静态属性 */
	static {
		initServletMapping();
	}
	
	/** 初始化servletMapping映射的方法 */
	private static void initServletMapping () {
		
		/**
		 * 加载conf/servlets.xml
		 * 将每个<servlet>标签中的属性url值作为key，将class属性的值作为value存入到mapping中
		 */
		try {
			// 创建SAXReader
			SAXReader reader = new SAXReader();
			// reader读文件成document
			Document doc =  reader.read(new File("conf/servlets.xml")); // 此处传入的是绝对路径
			// 获取根元素
			Element root = doc.getRootElement();
			// 获取子元素
			List<Element> servlet = root.elements("servlet");
			for (Element e: servlet) {
				/** .attributeValue为获取子元素的属性参数，.elementText()为获取子元素的内容*/
				String url = e.attributeValue("url"); // 拿到servelt关键字为url的属性参数
				String className = e.attributeValue("class"); // 拿到servelt关键字为class的参数
				servletMapping.put(url, className);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static String getServlet(String key) {
		return servletMapping.get(key);
	}
	
	
	
	
}
