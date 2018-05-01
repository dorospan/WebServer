package com.tedu.webserver.core;
/**
 * 线程任务，处理客户端请求并响应客户端
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

import com.tedu.webserver.context.HttpContext;
import com.tedu.webserver.context.ServerContext;
import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;
import com.tedu.webserver.servlet.HttpServlet;
import com.tedu.webserver.servlet.LoginServlet;
import com.tedu.webserver.servlet.RegServlet;

public class ClientHandler implements Runnable {
	
	private Socket socket; 
	
	public ClientHandler (Socket socket) {
		this.socket = socket;
	}
	
	/** 处理客户端请求 多个线程任务*/
	public void run () {
		//System.out.println("开始处理客户端请求...");
		try {
			/**
			 * 处理客户端请求大致流程：
			 * 1. 解析请求
			 * 		将socket传递给HttpRequest用于实例化该请求（实例化过程=解析请求过程）
			 * 		内部会通过Socket获取输入流，读取客户端发来的http请求，并将内容保存在HttpRequest对象相关属性上
			 * 2. 分析请求
			 * 3. 处理请求
			 * 4. 响应客户端
			 */
			// 1
			HttpRequest request = new HttpRequest(socket); // 解析请求
			HttpResponse response = new HttpResponse(socket); // 响应客户端
			/**
			 * 2  分析请求
			 * （1）获取用户请求的URl地址
			 * （2）分析请求URL是具体资源还是业务 （以下暂作资源处理）
			 */
			// 2（1）
//			String url = request.getUrl();

			String url = request.getRequestURI(); // url已根据if else处理过，需要得出的是页面段；如果getUrl, 若地址栏是请求业务，则会连?全部给url，等于没处理过
			
			// 从server.xml中获取url对应的value，即要响应给客户端的页面从哪个类去调用
			String servletName = ServerContext.getServlet(url); // 从ServerContext的map里根据key(url)找到对应的value(完整包名.类名）

			// 根据完整包名.类名判断是否存在这个类，存在即有这个业务
			if (servletName != null) { // 2（2）判断是否请求处理业务（注册、登陆等）
				System.out.println("！！！！！开始处理业务");
				/**
				 * 处理注册业务流程：
				 * 1. 从request中获取用户输入的注册信息
				 * 2. 将用户的注册信息写入文件中
				 * 3. 将注册成功的页面发送给客户端
				 * 单独做一个类来处理这个注册业务
				 */
				// 包名.类名
				Class cls = Class.forName(servletName); // 获取业务所在的类 
//				HttpServlet servlet = (HttpServlet)cls.newInstance();
//				servlet.service(request, response);
				
				Object obj = cls.getDeclaredConstructor().newInstance(); // 用该类去new对象
				// 拿到需要用的该类的方法
				Method method = cls.getDeclaredMethod("service", new Class[] {HttpRequest.class, HttpResponse.class}); // "service"方法名需要写活的时候用这个，否则可以不用这样写
				// 执行该对象的该方法
				method.invoke(obj, new Object[] {request, response});
				
				
			} else { // 不处理业务，那就是请求静态资源（比如静态页面等） 地址没带问号"?"的
				System.out.println("！！！！！！！！！开始处理资源");
				// 2（2）
				File file = new File("webapps"+url); // 根据url地址去寻找资源，当作File类型
				if (file.exists()) {
					//System.out.println("文件已找到："+file.getName());
					
					// 设置状态代码
					response.setStatusCode(200);
					
					/** 
					 * 获取后缀名：
					 * 根据请求资源的名字获取该资源文件后缀名
					 * 根据后缀名到HttpContext中获取该后缀名对应的介质类型值
					 * 再通过下面的response将头信息Content-Type设置好
					 */
					// 为了保证url里请求的文件类型，与响应头的content-type中文件类型一致，才能保证页面样式读取正常（样式：CSS JS...）
					String fileName = file.getName(); // 获取整个文件名 
					String ex = fileName.substring(fileName.lastIndexOf(".")); // 获取文件名后缀
					String contentType = HttpContext.getMimeType(ex); // 以后缀名去HttpContext的map表里匹配key
					
					// 设置响应头信息
					response.setHeader("Content-Type", contentType);
					response.setHeader("Content-Length", String.valueOf(file.length()));
					
					// 设置响应实体文件   传入file 
					response.setEntity(file);
					
					// 响应客户端
					response.flush(); 
					
				} else {
					System.out.println("文件未找到");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	
	
}
