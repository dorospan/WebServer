package com.tedu.webserver.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.tedu.webserver.context.HttpContext;

import java.util.Set;

/**
 * Http响应
 * 该类的每一个实例用于表示一个具体回复客户端的响应
 * 包括三部分：状态行、响应头、响应正文
 */
public class HttpResponse {
	
	private Socket socket;
	
	// 从socket中获取输出流，通过输出流将响应内容发回给客户端
	private OutputStream out;
	
	// 用data写，不用file写
	private byte[] data;
	
	// 响应实体文件
	private File entity; // 此处原本没file实体，要外面传进file，下面要getter setter
	
	// 存储响应头
	private Map<String, String> headers = new HashMap<String, String>();
	
	// 状态代码
	private int statusCode;
	
	/** 构造 */
	public HttpResponse (Socket socket) {
		try {
			this.socket = socket;
			this.out = socket.getOutputStream();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 将当前响应内容发回给客户端  */
	public void flush () {
		/** 
		 * 提交表单的关键字，
		 * 响应分三步：
		 * 1. 发送状态行
		 * 2. 发送响应头
		 * 3. 发送响应正文
		 * 这三个方法只单独针对每个发回给客户端的响应，不对外公开，只需在本类中使用，private就可以
		 * 
		 */
		sendStatusLine();
		sendHeaders();
		sendContent();
	}
	
	/** 1. 发送状态行 */
	private void sendStatusLine () {
		
		try {
			String line = "HTTP/1.1 "+statusCode+" "+HttpContext.getStatusReasonByCode(statusCode); 
			//System.out.println("状态行："+line);
			println(line);
			//System.out.println("已发送状态行");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 2. 发送响应头 */
	private void sendHeaders () {
			
			Set<Entry<String, String>> entrySet = headers.entrySet();
			for (Entry<String, String> e: entrySet) {
				String line = e.getKey()+": "+e.getValue();
				System.out.println("响应头："+e);
				println(line); // 遍历一行发一行
			}
			// 单独发送CRLF
			println("");
			
			//System.out.println("已发送响应头");
			
	}
	
	/** 3. 发送响应正文 */
	private void sendContent () {
		if (this.data != null) { // 若用byte[] data形式读进来再发写出去的话
			try {
				out.write(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (entity != null) { // 文件不为空的话
			// 边读边写
			try ( // JDK1.7后，可自动关闭流
				FileInputStream fis = new FileInputStream(entity);  // entity就是file
			) {
				byte[] data = new byte[1024*10];
				int len; // 一节一节读
				while ((len = fis.read(data))!=-1) {
					out.write(data, 0, len);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//System.out.println("已发送响应正文");
	}
	
	
	/** 发送重定向的资源 */
	public void sendRedirect (String url) { // 
		// 设置状态代码
		this.setStatusCode(302);
		// 设置响应头   ???????????????? Location ????
		this.setHeader("Location", url); //  url: reg_success.html   Location 是什么？？？
		// 响应客户端
		this.flush();
		
		
	}
	
	
	/** 单独按行发送 */
	public void println (String line) {
		try {
			out.write(line.getBytes("ISO8859-1"));
			out.write(HttpContext.CR);
			out.write(HttpContext.LF);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 获取当前socket的文件 */
	public File getEntity() {
		return entity;
	}

	/** 设置当前文socket的文件 */
	public void setEntity(File entity) {
		this.entity = entity;
	}
	
	/**
	 * 设置响应头信息
	 * @param name 	响应头名字
	 * @param value	响应头的值
	 */
	public void setHeader (String name, String value) {
		this.headers.put(name, value);
	}
	
	/** 设置状态代码  */ // private 属性，又要可设置？不初始化？
	public void setStatusCode (int code) {
		this.statusCode = code;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	
	
	
}
