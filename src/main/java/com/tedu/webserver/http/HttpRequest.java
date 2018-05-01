package com.tedu.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tedu.webserver.context.HttpContext;

/**
 * HttpReqest用来表示浏览器发送给服务端一个具体的http请求内容
 * 一个http请求应包括：请求头、消息行、消息正文
 *
 */
public class HttpRequest {
	
	/** 请求行相关信息定义 */
	// 请求方式
	private String method;
	
	// 请求资源路径   统一资源定位
	private String url;
	
	// 请求使用的协议版本
	private String protocol;
	
	// 对应客户端的socket
	private Socket socket;
	
	// 用于读取客户端发送过来数据的输入流
	private InputStream in;
	
	// 请求地址，通常与url属性值一样，但对于客户端get形式提交一个form表单数据（请求路径中附带数据），requestURI只保存请求路径中"?"左边实际请求内容
	private String requestURI;
	
	// 同上，保存"?"右边内容
	private String queryString;
	
	// 存储请求附带的所有参数（"?"右边的内容，如name=dorospan，以map形式拆存）
	private Map<String, String> parameters = new HashMap<String, String>();
	
	/**
	 *  消息头内容
	 *	消息头格式：headerName: headerValue(CRLF)
	 *	消息头名字：	消息头的值
	 *	key			value
	 */
	private Map<String, String> headers = new HashMap<String, String>(); 
	
	/** 构造，根据给定的socket解析对应客户端发来的请求 */
	public HttpRequest (Socket socket) {
		//System.out.println("开始解析请求");
		try {
			this.socket = socket;
			this.in = socket.getInputStream();
			
			/**
			 * 解析分三步：
			 * 1. 解析请求行
			 * 2. 解析消息头
			 * 3. 解析消息正文
			 */
			parseRequestLine(); // 1. 解析请求行 
			parseHeaders(); // 2. 解析消息头
			parseContent(); // 3. 解析消息正文
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("解析请求完毕");
	}
	
	
	/** 解析请求行 */
	private void parseRequestLine () {
		
		//System.out.println("开始解析请求行");
		
		/** 
		 * 先通过输入流读取一行字符串（CRFL结尾） 
		 * http请求第一行是：请求行
		 * 读取出来内容如下：
		 * GET / HTTP/1.1(CRLF)
		 * 请求行个事根据空格将内容分为三部分：
		 * method / url/protocol
		 * 因此读取请求行字符串后，要根据空格将字符串三部分分别拆出来，设置到HttpRequest的三个对应属性中，以完成解析请求行工作
		 * 
		 */
		// 1 读取请求行内容
		String line = readLine(); // 请求行只有一行 所以只读一行
		/**
		 * 地址栏输入：localhost:8080/index.html
		 * 测试解析结果，下面输出应为：
		 * 	GET /myweb/index.html HTTP/1.1(CRLF)
		 * method: GET
		 * url: index.html
		 * protocol: HTTP/1.1
		 */
		// 拆字段，并赋值到对应属性中
		String[] data = line.split("\\s"); 
		
		this.method = data[0];
		
		this.url = data[1];
		parseURL(); // 若url有"?"，进一步解析URL，没有的不管 
		
		this.protocol = data[2];
		
		//System.out.println("method: "+getMethod());
		//System.out.println("url: "+getUrl());
		//System.out.println("protocol: "+getProtocol());
		
		//System.out.println("解析请求行完毕");
	}
	
	
	
	/** 解析请求行中的url*/
	private void parseURL () {
		
		/**
		 * 
		 * 1. 查看url是否含有"?"
		 * 2. 若含有?, 则按照?将url拆分成两部分
		 * 		（1）requestURI, 用于保存请求路径: ?左侧内容
		 * 		（2）queryString，用于保存url中传来的数据部分 ?右侧内容，再将其拆Map parameters中：
		 * 			A. key: 按"&"左侧
		 * 			B. value: 按"&"右侧
		 * 		解析的url通常有两种情况：
		 * 		/myweb/index.html // 静态资源
		 * 		/myweb/reg?username=doros&password=djfjd&tel=1236123
		 * 		若不含有?, 直接将url内容设置到requestURI上即可
		 * 即：requestURI保存?左边内容，queryString保存?右边内容
		 * 
		 */
		//System.out.println("进一步解析url");
		int index = url.indexOf("?"); // 先拿到"?"的脚标
		if (index != -1) { // url中有"?"，即读到它的指针不为-1  url本质是String再read()进来的???
//			String[] urlSplit = url.split("\\?"); // 
//			this.requestURI = urlSplit[0];
//			this.queryString = urlSplit[1];
			
			this.requestURI = url.substring(0, index); // ? 左边
			this.queryString = url.substring(index+1); // ? 右边
			
//			String[] query = this.queryString.split("\\&"); // 按"&"砍断
//			for (String para: query) {
//				int i = para.indexOf("="); // 再找到"="脚标，一组组处理，左边key 右边value；
//										// 如果这里用String[]按"="砍断再用元素去赋值，需要注意数组越界（比如用户不填密码点注册会少一个value，报错）
//				String key = para.substring(0, i);
//				String value = para.substring(i+1);
//				//System.out.println(key+": "+value); // put之前先打桩看
//				parameters.put(key, value);
//			}

		} else { // 若不含有"?"，即/myweb/index.html，整段都是url
			this.requestURI = this.url;
//			System.out.println("requestURI: "+requestURI);
		}
		
		
		//System.out.println("url解析完毕");
		//System.out.println("requestURI: "+requestURI);
		//System.out.println("queryString: "+queryString);
		//System.out.println("parameters: "+parameters);
		
		
	}

	
	
	/** 解析消息头 */
	public void parseHeaders () {
		//System.out.println("开始解析消息行");
		
		while (true) { // 消息头不止一行
			String line = readLine();
			if ("".equals(line)) { // 如果该行只有CRLF，即已是结尾
				break;
			}
			String[] data = line.split(":\\s");
			headers.put(data[0], data[1]); // 用户请求里的消息头key value
		}
		
		
		System.out.println("解析消息头完毕");
	}
	
	
	
	/** 解析消息正文 */
	public void parseContent () {
		
		// 1. 消息头包含Content-Type即是有消息正文的 
		if (headers.containsKey("Content-Type")) { 
			int length = Integer.parseInt(headers.get("Content-Length")); // 拿到该Content-Type的对应Content-Length长度 下面in.read()定长用
			
		// 2. 从in中连续读取指定字节量，将消息正文数据全部读取出来  
			try {
				byte[] data = new byte[length];
				in.read(data); // 前面读过请求行 消息头后，后面剩下的都是消息正文
				
				// 3. 读完正文数据后根据消息头：Content-Type查看是什么类型的数据
				String contentType = headers.get("Content-Type");
				
				// 4. 判断是否为form表单在提交信息，消息正文中有它即请求为提交表单：application/x-www-form-urlencoded
				if ("application/x-www-form-urlencoded".equals(contentType)) { 
					// form表单提交的字节数据=原来使用GET请求包含在URL在地址栏中"?"右侧内容，即queryString
					String line = new String(data, "ISO8859-1");
					System.out.println("form表单数据："+line);
					
					this.queryString = line;
					parseQueryString(); // 解析消息正文（原消息头）里的用户提交的表单信息
					
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	
	/** 解析消息正文（原消息头）里用户提交的表单信息 */
	public void parseQueryString () {
		String[] query = this.queryString.split("\\&"); // 按"&"砍断
		for (String para: query) {
			int i = para.indexOf("="); // 再找到"="脚标，一组组处理，左边key 右边value；
									// 如果这里用String[]按"="砍断再用元素去赋值，需要注意数组越界（比如用户不填密码点注册会少一个value，报错）
			String key = para.substring(0, i);
			String value = para.substring(i+1);
			//System.out.println(key+": "+value); // put之前先打桩看
			parameters.put(key, value);
		}
	}
	
	
	
	
	
	
	/** 从给定的输入流中读取一行字符串，以(CRLF)结尾认定一行结束，返回字符串中不含有(CRLF) */
	private String readLine () {
		// 每个线程为一个独立对象，使用自己对象里的此方法，不存在线程安全问题（线程安全针对多个对象使用同一个方法），因此这里用builder就可以，不需要StringBuffered 
		StringBuilder builder = new StringBuilder();
		try {
			int d = -1;
			char c1 = 'a'; // 常量，随便先初始化就可以
			char c2 = 'a';
			while ((d = in.read())!=-1) {
				c2 = (char)d;
				builder.append(c2);
				if (c1==HttpContext.CR && c2==HttpContext.LF) { // 13即CR的编码，10即LR的编码
					break;
				}
//				builder.append(c2); // 放这里就等于最后的LR不拼进去，但最后也是要trim()掉CR
				c1 = c2;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString().trim(); // (CRLF) 在系统是空字符，可以被trim()
		
	}

	public String getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}

	public String getProtocol() {
		return protocol;
	}

	/** 
	 * 直接返回Map会暴露K V, .clear()会清掉它，要保护封装性？
	 * 此处只需获取get指定消息头的名字
	 */
//	public Map<String, String> getHeaders() {
//		return headers;
//	}
	
	public String getHeader(String name) {
		return headers.get(name);
	}

	public String getRequestURI() {
		return requestURI;
	}

	public String getQueryString() {
		return queryString;
	}
	/**
	 * 获取请求附带的参数对应value值
	 * @param key
	 * @return
	 */
	public String getParameter(String key) {
		return parameters.get(key);
	}
	
	
	
	
}

