package com.tedu.webserver.servlet;

import java.io.File;

import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;

/** 
 * 用于处理业务的类
 * 这是个超类，所有Servlet都需要继承它 
 */
public abstract class HttpServlet {
	
	public abstract void service (HttpRequest request, HttpResponse response); 
	
	/**
	 * 跳转到指定地址
	 * @param url
	 * @param request
	 * @param response
	 */
	public void forward (String url, HttpRequest request, HttpResponse response) {
		
		File file = new File("webapps"+url); 
		response.setStatusCode(200);
		response.setHeader("Content-Type", "text/html");
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setEntity(file);
		response.flush();
		
		
	}
	
	
}
