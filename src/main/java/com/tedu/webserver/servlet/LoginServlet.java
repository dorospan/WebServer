package com.tedu.webserver.servlet;

import java.io.File;
import java.io.RandomAccessFile;

import com.tedu.webserver.context.HttpContext;
import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;

/** 处理用户登陆业务 */

public class LoginServlet extends HttpServlet {
	
	public void service (HttpRequest request, HttpResponse response) {
		
		System.out.println("LoginServlet: 开始处理登陆业务");
		
		/**
		 * 处理登陆业务流程：
		 * 1. 从request中获取用户输入的注册信息
		 * 2. 读取匹配注册信息 
		 * 3. 将注册成功的页面发送给客户端
		 
		 * LoginServlet中的登陆实现思路：
		1. 通过request获取用户输入的username password
		2. 使用RandomAccessFIle读取user.dat文件
			循环读取该文件，先读取32字节转换为字符串，这是密码然后匹配与用户输入的密码是否一致，若一致则调转登陆成功页面，若不一致则调转登录失败页面
			若读取这条纪录中用户名与用户输入的不一致，则说明不是这个用户，那么将指针跳转到下一条记录用户名位置继续读取 
		 */
		
		String username = request.getParameter("username"); // 获取用户输入的username
		String password = request.getParameter("password"); // 获取用户输入的password
		
		try (
			RandomAccessFile raf = new RandomAccessFile("user.dat", "r");
		){
			boolean check = false;
			for (int i=0; i<raf.length()/100; i++) {
				// 先匹配username
				raf.seek(i*100); // 放好指针
				byte[] data = new byte[32]; // 指定了32字节
				raf.read(data);
				String nameInput = new String(data).trim(); // 将文档里的username转成String, 砍掉空白
				if (nameInput.equals(username)) { //若用户输入的等于.dat里的,即匹配 
					data = new byte[32];
					raf.read(data); // 与用户名同样长度
					String passwordInput = new String(data).trim();// 将文档里的password转成String,砍空白
					if (passwordInput.equals(password)) {
						check = true; // 两者匹配则跳出循环
						break;  // 测试下有重复用户名，要匹配用户名后再匹配密码，密码不对再找下一同样用户名
					}
//					break; // 正常情况：用户名唯一，只要用户名不匹配就break, 不需要匹配密码
				}
			}
			
			if (check) {
				forward("/myweb/login_success.html", request, response);
				System.out.println("登陆成功");
				
			} else {
				forward("/myweb/login_fail.html", request, response);
				System.out.println("登录失败");
			}
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		System.out.println("LoginServlet: 登陆业务处理完毕");
		
		
	}
	
}
