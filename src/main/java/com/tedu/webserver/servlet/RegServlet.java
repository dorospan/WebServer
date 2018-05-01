package com.tedu.webserver.servlet;

/** 用于处理用户注册业务 处理的数据来自于request, reponse*/

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;

public class RegServlet extends HttpServlet {
	
	public void service (HttpRequest request, HttpResponse response) {
		System.out.println("RegServlet: 开始处理注册");
		/**
		 * 处理注册业务流程：
		 * 1. 从request中获取用户输入的注册信息
		 * 2. 将用户的注册信息写入文件中
		 * 3. 将注册成功的页面发送给客户端
		 */
		
		// 1. 从request中获取用户输入的注册信息
		/**
		 * 此处request.getParameter()中传递的参数为注册页面中对应输入框中name属性的值
		 * 如：<input type="text" size=36 name="age"> 即age
		 * 具体看reg.html
		 */
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		int age = Integer.parseInt(request.getParameter("age"));
		String tel = request.getParameter("tel");
		
		// 打桩
		System.out.println("username: "+username);
		System.out.println("password: "+password);
		System.out.println("age: "+age);
		System.out.println("tel: "+tel);
		
		//2. 将用户的注册信息写入文件中
		/**
		 * 将用户数据写入到文件user.dat中
		 * 格式：
		 * username, password, age, tel
		 * username/password/tel: String utf-8 长度32字节
		 * age: int, 长度4字节
		 * 每条用户记录占用100字节
		 */
		try {
			RandomAccessFile raf = new RandomAccessFile("user.dat", "rw");
			raf.seek(raf.length()); // 移动指针到文件末尾才开始写
			
			// 写username
			byte[] data = username.getBytes("utf-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			// password
			data = password.getBytes("utf-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			// age
			raf.writeInt(age); // 直接是4个字节写入，不需要另外扩容
			
			// tel
			data = tel.getBytes("utf-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			// 3. 将注册成功的页面发送给客户端（内部跳转，一次连接的）
//			forward("/myweb/reg_success.html", request, response);
			
			
			/** 
			 * 路径是给客户端看的，因此是相对路径
			 * 即：客户端注册时路径：
			 * 		http://localhost:8080/myweb/reg.html
			 * 相对路径为：
			 * 		http://localhost:8080/myweb/
			 * 若希望客户端myweb中的reg_success.html页面，则应当在以下方法直接传入页面名，即：
			 * 		http://localhost:8080/myweb/reg_success.html
			 */
			// 注册成功后重定向到 “注册成功”页面
			response.sendRedirect("reg_success.html"); // ????????
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("RegServlet: 处理注册完毕");
		
		
	}
	
	
	
}
