package com.tedu.webserver.context;

/** HttpContext用来定义HTTP协议中相关定义内容 */

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class HttpContext {
	
	// 回车符
	public static final int CR = 13;
	
	// 换行符
	public static final int LF = 10;
	
	/** 
	 * 介质类型映射 
	 * key: 资源类型名  如：html
	 * value: Context_Type对应值  如：text/html
	 */
	// final：不能修改查询表（本质是集合）中元素的地址，但依然可以增删元素，所以要private，不允许外界clear()
	private static final Map<String, String> MIME_TYPE_MAPPING = new HashMap<String, String>(); 
	
	/**
	 * 状态代码与描述映射
	 * key: 状态代码
	 * value: 状态描述
	 */
	// private理由同上
	private static final Map<Integer, String> STATUS_CODE_REASON_MAPPING = new HashMap<Integer, String>(); 
	
	/** 静态块 */
	static {
		initMimeTypeMapping();
		initStatusCodeMapping();
	}
	
	/** 
	 * 初始化介质类型映射 ；查tomcat-*.xml可知  
	 * 读取conf/web.xml文件，将该文件中所有的子标签<mine-mapping>解析出来：
	 * 		将其中子标签<extension>中间的文本作为key
	 * 		将子标签<mime-type>中间的文本作为value存入MIME_TYPE_MAPPING中，以完成初始化
	 */
	private static void initMimeTypeMapping () {
		
		try {
			// 创建SAXReadom
			SAXReader reader = new SAXReader();
			
			// 读取并生成document
			Document doc = reader.read(new File("conf/web.xml"));
			
			// 获取根元素
			Element root = doc.getRootElement();
			
			// 获取所有子元素
			List<Element> mimeList = root.elements("mime-mapping"); 
			
			// 将extension的文本作为key, 将mime=type的文本作为value, put进map中
//			System.out.println(mimeList.size()); // 打桩，看多少个
			for (Element e: mimeList) {
				String key = e.elementText("extension");
				String value = e.elementText("mime-type");
//				System.out.println(key+": "+value); // 打桩
				MIME_TYPE_MAPPING.put(key, value); 
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	
	/** 初始化状态代码与描述映射；此处暂用常用的做出功能  */
	public static void initStatusCodeMapping () {
		STATUS_CODE_REASON_MAPPING.put(200, "OK");
		STATUS_CODE_REASON_MAPPING.put(302, "Move Temporarily");
		STATUS_CODE_REASON_MAPPING.put(404, "Not Found");
		STATUS_CODE_REASON_MAPPING.put(500, "Internal Server Error");
	}
	
	
	/** 用公开方法 根据后缀名ex获取介质类型映射*/
	public static String getMimeType (String ex) {
		return MIME_TYPE_MAPPING.get(ex);
	}

	
	/** 根据给定状态代码code获取对应状态描述 */
	public static String getStatusReasonByCode (int code) {
		return STATUS_CODE_REASON_MAPPING.get(code);
	}
	
	public static void main(String[] args) {
		initMimeTypeMapping();
	}
	

	
}
