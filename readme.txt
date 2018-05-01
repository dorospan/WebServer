WebServer是一个web容器
1. 主要工作：搭建起服务器应用程序，负责与客户端交互（客户端通常指：“浏览器”）
	（1）浏览器与服务器通讯的底层协议及上层应用层协议是固定的，即：
		底层传输协议：	TCP，	负责定义两台机器间如何传递数据（定义可沟通）
		应用层协议：	HTTP，	负责定义传递的数据的格式（定义沟通的语言）
	（2）WebServer基于上述两个协议完成客户端的请求并响应客户端结果
2. 实际开发中常使用的Web容器：Tomcat
3. 通过这个项目理解：
		（1）客户端与服务端之间如何基于TCP协议、HTTP协议进行数据交互
		（2）如何处理客户端不同种类的请求
		（3）如何响应客户端


v1:
	搭建服务端程序结构，测试浏览器能否正常连接服务端，并在服务端输出客户端发来的信息（HTTP请求内容）
	URL 统一资源定位
	http://localhost:8080   
		localhost(IP地址)找到该主机，8080找到该主机中的程序

v2:
	在ClientHandler中添加一个用于读取请求内容的方法
	请求中，请求行与消息头都是按行发送的 (CRLF表示一行结束),因此此处添加的方法按行读取字符串
	定义方法：
		// 根据给定的输入流读取一行字符串并返回
		private String readLine(InputStream in) {}
		
v3:
	在com.tedu.webserver包中新建一个包http, 这个包中将来用于存放http协议相关的内容
	由于客户端（浏览器）是按照http协议请求标准发送请求的，一个请求中含有信息很多，比如请求行信息、消息头信息等
	服务端需要读取这个请求并根据请求进行处理，因此在读取请求时需将它们保存以便获取请求中相关信息
	因此在http包中定义一个类：HttpRequest
	使用HttpRequest的每一个实例表示一个客户端发来的具体请求信息
	解析请 --> 分析请求 --> 处理请求 --> 响应客户端

v4:
	目标功能：
		此版本要实现用户在浏览器的地址栏输入：localhost:8080/index.html  可以正常看到该页面内容
	to do:
		需要添加一个index.html页面，并且在服务端添加响应客户端的功能
	学习目的：
		通过此版本了解HTTP协议中【响应】的相关定义，以及如何通过响应将客户端请求内容-->发回给客户端.
	步骤：
		1. 在项目目录下新建一个目录:webapps
			这个目录用于存放服务端中所有提供给用户的应用资源.
		2. 在webapps下建立一个子目录:myweb    
		3. 在myweb下新建一个页面文件:index.html   
		4. 修改ClientHandler,通过HttpRequest获取用户请求的资源，然后从webapps下找到对应路径的资源，并将该资源通过[响应]发送给客户端.

		浏览器地址格式通常为:
		protocol://host/path
		protocol：	表示应用层协议
		host：		是找到服务端所在地址，可以是一个域名(通过DNS解析为IP地址和端口号)，也可以直接使用IP+port.
		/path：		实际请求服务端资源的路径，这是一个相对路径，该相对路径的根有服务端内部自行决定.

		此项目中定义"/"根目录为webapps.
		例如用户输入:
		http://localhost:8080/myweb/index.html
		我们在请求行中得到的url部分为:/myweb/index.html
		那么我们就从webapps中找对应内容,路径应当为:
		webapps/myweb/index.html

v5:
	目标功能：
		增加对请求中消息头的解析工作
	步骤:
		1. 在HttpRequest中添加一个Map类型的属性headers，用于存放所有请求中包含的消息头内容，其中Key存消息头的名字，value存消息头对应的值
			消息头格式：headerName: headerValue(CRLF)
					消息头名字：	消息头的值
					key			value
		2. 在HttpRequest中添加方法：parseHeaders
			该方法在构造方法中parseRequestLine()后调用，即：解析完一个请求的请求行之后开始解析消息头
			此方法会顺序读取若干行（CRLF结尾），每行为一个消息头内容，根据“：”分为两部分：左边：消息头名字；右边：对应消息头的值
			解析后将它们存入headers的map中，当单独读取了CRLF则表示所有头都解析完毕，该方法完成工作

v6:
	目标：重构代码
	步骤：在http包中添加一个新的类HttpResponse，使用这个类的每一个实例表示一个具体要发回给客户端的响应内容
		高内聚 低耦合
		
				Method m3 = cls.getDeclaredMethod("sayHi", new Class[] {String.class}); // String.class 参数是什么类型，就该类型.class
		
v7:
	1. 在com.tedu.webserver中建立一个新包：context
		在context包中建立一个类：HttpContext
							用于定义关于HTTP协议相关内容
		e.g: HttpRequest, HttpResponse中都用到两个字符：CR==13， LF==10，
				这两个字符都是HTTP协议规定的，因此应将它们按常量定义在HttpContext中，request & response引用它们即可
	2. 添加一个Map类型的常量：MIME_TYPE_MAPPING
					用于记录不同的介质类型映射
		客户端在请求一个静态资源时，服务端响应该资源时应当发送一个描述该资源类型的头：Context-Type，此头的值应由客户端请求的静态资源类型来定
											ClientHandler中的：Content-Type 是响应头中的信息内容
		e.g: html文件的值：text/html			
			png文件的值：image/png
			以上都被规定在HTTP协议中，所有介质类型映射可参考tomcat安装目录-conf目录-web.xml文件


v8:
	使用tomcat安装目录-conf目录-web.xml文件，将其中的介质类型影射读取出来，初始化HttpContext类中：MIME_TYPE_MAPPING
	修改HttpContext中方法：initMineTypeMapping
		用于读取web.xml文件，将介质影射解析出来，并初始化MIME_TYPE_MAPPING这个map


v9:
	1. 在webapps/myweb目录中添加一个注册页面：reg.html
		在页面中添加form表单，action指定地址为"reg"
		表单提交地址为：http://localhost:8080/myweb/reg
	2. 当提交到服务器端，解析请求行时得到的url部分为：/myweb/reg?username=doros&password=djfjd&tel=1236123
		对这样的请求，客户端不再是请求一个静态资源，而是请求一个注册业务处理并附带用户输入内容，为此要实现一段逻辑来完成对于注册业务的处理工作
		目标功能：
			在HttpRequest中添加针对form表单get形式提交地址的解析工作，将请求与用户数据分离，以获取相关信息完成业务操作
		步骤：
			在HttpRequest中添加三个新属性
			1. requestURI, 用于保存请求路径
			2. queryString，用于保存url中传来的数据部分
			即：requestURI保存?左边内容，queryString保存?右边内容
			3. parameters ，是一个map, 用于保存传递过来的数据，将queryString进一步拆分保存
	3. 在ClientHandler中添加对业务请求的分支判断
	4. 在servlet包中添加用于处理注册业务的RegServlet并实现用于处理注册逻辑的方法
	5. 在ClientHandler的业务分支中实例化RegServlet并调用service方法处理注册业务
		
	按照注册功能流程完成登陆功能：
		1. 在webapps/myweb/中添加一个登陆页面login.html，该页面的form表单中action="login"
			表单中应当有两个输入框：用户名、密码
			提交按钮上文字为：登陆
		2. 该表单提交后，地址栏中请求地址如：http://localhost:8080/myweb/login?username=doros&password=123
			在ClientHandler判断业务分支中添加一个新分支判断，查看url内容是否为"/myweb.login"
				若是，则实例化LoginServlet并调用其service方法处理登陆业务
		3. 在servlet包中添加一个用于处理登陆业务的类：LoginServlet，并实现service功能，完成登陆功能
		4. 在webapps/myweb中添加两个页面：login_success.html, login_fail.html
			分别表示登陆成功与登陆失败的页面
		提示：
		LoginServlet中的登陆实现思路：
		1. 通过request获取用户输入的用户名及密码
		2. 使用RandomAccessFIle读取user.dat文件
			循环读取该文件，先读取32字节转换为字符串，这是密码然后匹配与用户输入的密码是否一致，若一致则调转登陆成功页面，若不一致则调转登录失败页面
			若读取这条纪录中用户名与用户输入的不一致，则说明不是这个用户，那么将指针跳转到下一条记录用户名位置继续读取 

v10:
	重构代码：
		1. 在servlet包中添加一个超类HttpServlet，用于规范所有世纪处理业务逻辑的Servlet必须有的方法，
			如service，且定义所有Servlet共用的方法；如forward，后期有方法可再添加
		2. LoginServlet, RegServlet继承HttpServelet，并将重复代码改为调用方法
		
		在ClientHandler中使用反射机制，根据具体请求找到对应Servlet的名字加载这个类并实例化，再调用对应service方法处理业务
		实现思路：
			使用一个Map，来保存请求地址与处理该请求的Servlet的关系，
			当ClientHandler通过请求得到对应url后，作为key在这个Map中找到对应Servlet，利用反射机制加载并实例化它，然后调用service方法进行处理
			该map中有多个地址与Servlet的对应，可使用一个xml文件来进行配置
			好处：后期再添加新业务，只需实现Servlet后修改该xml文件，添加对应请求地址与这个Servlet的对应关系即可
				ClientHandler无需再添加分支来判断请求与不同Servlet的关系

v11
	完成ShowAllUserServlet，实现动态页面
	修改HttpResponse, 添加byte[]属性data
	发送响应正文的方法sendContent中添加一个分支判断????
	
v12
	在WebServer类中添加使用线程池来维护线程
	
	
v13
	当页面中需要传递给服务端的数据中含有用户隐私信息、或有上传附件等操作时，form表单中的请求方式应当使用post请求
	post请求会将用户数据放在消息正文部分传递给服务端
	步骤：
		1. reg.html 将GET 改为 POST
		2. 修改后，HttpRequest中Content-Type内容会显示在消息正文，将其方法独立成parseQueryString()，并在消息正文中被调用
	
	对于重定向的支持
		内部跳转：
			一次请求：当服务端处理完用户某个请求，需要给用户反馈一个结果时，若服务端直接将结果响应给客户端，此为内部跳转（一次连接，直接访问到另一个资源）
		重定向：
			处理请求后，在响应中告知客户端需要再次发起请求访问服务端，并希望看到其他资源，这时浏览器会再次发起向服务端发起请求，并请求到服务端给定路径所对应的资源
			（两次或以上连接才访问到另一个资源）
			响应状态代码：302
			在响应头中包含一个头：Location,. 该值为希望客户端再次发起连接所访问的路径
	
	
	






