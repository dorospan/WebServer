package com.tedu.webserver.core;
/** WebServer主类 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
	
	private ServerSocket server;
	
	// 线程池
	private ExecutorService threadpool;
	
	/** 构造  */
	public WebServer () {
		try {
			server = new ServerSocket(8080);
			threadpool = Executors.newFixedThreadPool(40); // 初始化线程池
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	/** 服务端开始工作 */
	public void start () {
		try {
			while (true) { // 先将while注释掉，客户端收不到响应时可能会试图多次连接服务端，导致服务端重复打桩，不利于开发查看程序执行流程，应先只限制接受一次客户端连接
				System.out.println("等待客户端连接...");
				Socket socket = server.accept();
				
				// 启动一个线程来处理该客户端
				ClientHandler handler = new ClientHandler(socket);
				threadpool.execute(handler); // 用线程池来处理任务

//				Thread t = new Thread(handler); // 这个会一直开关线程，造成系统负担
//				t.start();
				
				
				
				System.out.println("一个客户端连接了");
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 主方法 */
	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.start();
	}
	
	
}


