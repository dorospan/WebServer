package com.tedu.webserver.servlet;

import java.io.File;
import java.io.RandomAccessFile;

import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;

public class ShowAllUserServlet extends HttpServlet {
	@Override
	public void service(HttpRequest request, HttpResponse response) {
		
		try (
			RandomAccessFile raf = new RandomAccessFile(new File("user.dat"), "r");
		){
			
			StringBuilder builder = new StringBuilder();
			builder.append("<html>");
			builder.append("<head>");
			builder.append("<meta charset=\"utf-8\">");
			builder.append("<title>用户登录</title>");
			builder.append("</head>");
			builder.append("<body>");
			builder.append("<center>");
			builder.append("<h1>用户列表</h1>");

			builder.append("<table border=\"1\">");
			builder.append("<tr>");
			builder.append("<td>username</td>");
			builder.append("<td>password</td>");
			builder.append("<td>age</td>");
			builder.append("<td>tel</td>");
			builder.append("</tr>");
			
			for (int i=0; i<raf.length()/100; i++) {
				byte[] data = new byte[32];
				raf.read(data);
				String username = new String(data).trim();
				
				data = new byte[32];
				raf.read(data);
				String password = new String(data).trim();
				
				int age = raf.readInt();
				
				data = new byte[32];
				raf.read(data);
				String tel = new String(data).trim();
				
				builder.append("<tr>");
				builder.append("<td>"+username+"</td>");

				builder.append("<td>"+password+"</td>");
				builder.append("<td>"+age+"</td>");
				builder.append("<td>"+tel+"</td>");
				builder.append("</tr>");

			
			
			}
			
			
			builder.append("<table>");
			builder.append("</center>");
			builder.append("</body>");
			builder.append("</html>");
			
			byte[] data = builder.toString().getBytes();
			
			response.setStatusCode(200);
			response.setHeader("Content-Type", "text/html");
			response.setHeader("Content-Length", String.valueOf(data.length));
			response.setData(data);
			response.flush();
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
}
