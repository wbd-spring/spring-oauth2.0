package com.wbd.oauth.controllers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wbd.oauth.jedis.OauthRedisService;
import com.wbd.oauth.utils.HttpClientUtils;
import com.wbd.oauth.utils.JsonUtils;

/**
 * 测试 github与qq授权登录， 利用natapp外网映射工具
 * 
 * ctrl+shift+x 小写转大写， ctrl+shift+y大转小
 * 
 * @author jwh
 *
 */
@Controller
public class OAuthController {

	/***************** github相关信息 **********/
	// Github认证服务器地址
	private static final String AUTHORIZE_URL = "https://github.com/login/oauth/authorize";
	// 2、向GitHub认证服务器申请令牌的地址
	private static final String ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
	// 3、向资源服务器请求用户信息，携带access_token和tokenType
	private static final String RESROUCE_URL = "https://api.github.com/user";
	// 4.GITHUB_CLIENT_ID，GITHUB_CLIENT_SECRETgithub提供
	private static final String GITHUB_CLIENT_ID = "92874537bb13f60d1ec7";

	private static final String GITHUB_CLIENT_SECRET = "285d15c6bf68fb334d0ec15d9b14acf700c5275d";
	// 5.GitHub最后的回调地址
	private static final String GITHUB_REDIRECT_URL = "http://3grg7h.natappfree.cc/githubCallback";

	/***************** qq相关信息 **********/
	// 1. QQ认证服务器地址
	private static final String QQ_AUTHORIZE_URL = "https://graph.qq.com/oauth2.0/authorize";
	// 2.向qq认证服务器申请令牌的地址
	private static final String QQ_ACCESS_TOKEN_URL = "https://graph.qq.com/oauth2.0/token";

	// 3、使用Access Token来获取用户的OpenID
	private static final String QQ_ME_URL = "https://graph.qq.com/oauth2.0/me";

	// 4、使用Access Token以及OpenID来访问和修改用户数据
	private static final String QQ_USER_INFO_UURL = "https://graph.qq.com/user/get_user_info";
	
	private static final String QQ_APP_ID = "101681867";
	private static final String QQ_APP_KEY = "7b31c830e66767c0f64ce2045f239731";
	private static final String QQ_REDIRECT_URL = "http://vga8vt.natappfree.cc/qqCallback";

	@Autowired
	private OauthRedisService ors;
	
	
	/*******************github第三方登录***********************************************************************/

	/**
	 * 请求认证服务器
	 * 
	 * @param response
	 */
	@RequestMapping("/githubLogin")
	public void githubLogin(HttpServletResponse response) {

		// 生成并保存state，忽略该参数有可能导致CSRF攻击

		String state = ors.genState();

		// 参数传递 ，reponse_type,client_id,redirect_uri, state

		String param = "response_type=code&client_id=" + GITHUB_CLIENT_ID + "&state=" + state + "&redirect_uri="
				+ GITHUB_REDIRECT_URL;

		// 请求认证服务器

		try {
			response.sendRedirect(AUTHORIZE_URL + "?" + param);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * github回调的方法，然后再通过我们自己的服务器向认证服务器发送申请令牌的POST请求
	 * 
	 * 服务器回应客户端的URI，包含以下参数：
	 * code：表示授权码，必选项。该码的有效期应该很短，通常设为10分钟，客户端只能使用该码一次，否则会被授权服务器拒绝。该码与客户端ID和重定向URI，是一一对应关系。
	 * state：如果客户端的请求中包含这个参数，认证服务器的回应也必须一模一样包含这个参数。
	 * 
	 * 
	 * @param code
	 *            github返回的
	 * @param state
	 *            github返回的，跟请求时必须一样
	 * @param response
	 * @throws Exception
	 */
	@GetMapping("/githubCallback")
	public void githubCallback(String code, String state, HttpServletResponse response) throws Exception {

		// 验证state，如果不一致，可能被CSRF攻击
		if (!ors.checkState(state)) {
			throw new Exception("state验证失败");
		}

		// 向github认证服务器发送申请令牌的请求，传递参数
		String param = "grant_type=authorization_code&code=" + code + "&redirect_uri=" + GITHUB_REDIRECT_URL
				+ "&client_id=" + GITHUB_CLIENT_ID + "&client_secret=" + GITHUB_CLIENT_SECRET;

		// 申请令牌， 此时为post请求
		String result = HttpClientUtils.sendPostRequest(ACCESS_TOKEN_URL, param);

		System.out.println("result====" + result);
		Map<String, String> map = HttpClientUtils.params2Map(result);

		// 如果返回的map中包含error，表示失败，错误原因存储在error_description
		if (map.containsKey("error")) {
			throw new Exception(map.get("error_description"));
		}

		// 如果返回结果中包含access_token，表示成功
		if (!map.containsKey("access_token")) {
			throw new Exception("获取token失败");
		}

		String accessToken = map.get("access_token");
		String tokenType = map.get("token_type");

		// 通过令牌和tokenType参数向资源服务器申请资源

		String userParam = "access_token=" + accessToken + "&token_type=" + tokenType;

		String userResult = HttpClientUtils.sendGetRequest(RESROUCE_URL, userParam);
		response.setContentType("text/html;charset=utf-8");
		response.getWriter().write(userResult);

	}
	
	
	
	/*******************qq第三方登录***********************************************************************/
	
	

	
	
	/**
	 * 请求认证服务器
	 * 
	 * @param response
	 */
	@RequestMapping("/qqLogin")
	public void qqLogin(HttpServletResponse response) {
		
		//1.生存state
		String state = ors.genState();
		
		//2.拼接参数
		String param = "response_type=code&client_id=" + QQ_APP_ID + "&state=" + state + "&redirect_uri="
				+ QQ_REDIRECT_URL;
		
		//3.请求QQ认证服务器
		
		try {
			response.sendRedirect(QQ_AUTHORIZE_URL+"?"+param);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * qq的回调
	 * @param code  qq认证服务器返回的
	 * @param state   这个是我们自己提交到qq认证服务器，qq认证服务器原样返回给我们 
	 * @param response
	 * @throws Exception 
	 */
	@GetMapping("/qqCallback")
	public void qqCallback(String code,String state,HttpServletResponse response) throws Exception {
		
		//1.验证state，如果不一致，可能被csrf攻击
		if(!ors.checkState(state)) {
			
			throw new Exception("State验证失败");
		}
		//2.拼接参数，利用code等相关参数，POST请求qq认证服务器，返回token等其他的值
		String param = "grant_type=authorization_code&code=" + code + "&redirect_uri=" +
	            QQ_REDIRECT_URL + "&client_id=" + QQ_APP_ID + "&client_secret=" + QQ_APP_KEY;
	
		 //3. QQ获取到的access token具有3个月有效期，用户再次登录时自动刷新。
		String result = HttpClientUtils.sendPostRequest(QQ_ACCESS_TOKEN_URL, param);
		
		
		//4.对返回的参数进行解析
		 Map<String, String> resultMap = HttpClientUtils.params2Map(result);
		    // 如果返回结果中包含access_token，表示成功
		    if(!resultMap.containsKey("access_token")) {
		        throw  new Exception("获取token失败");
		    }
		 //5. 得到token
		  String accessToken = resultMap.get("access_token");
          
		  String qqParam = "access_token="+accessToken;
		  
		  //6.使用accessToken获取用户的openid
	   String qqResult=HttpClientUtils.sendGetRequest(QQ_ME_URL, qqParam);
     	
	   // 成功返回如下：callback( {"client_id":"YOUR_APPID","openid":"YOUR_OPENID"} );
	    // 取出openid
	   String openid = getQQOpenid(qqResult);
	   
	    //7.使用access_token和openid以及oauth_consumer_key來访问和修改用户数据
	   String userParam = "access_token="+accessToken+"&oauth_consumer_key="+ QQ_APP_ID + "&openid=" + openid;
	   String userInfo = HttpClientUtils.sendGetRequest(QQ_USER_INFO_UURL, userParam);
	   // 8、输出用户信息
	    response.setContentType("text/html;charset=utf-8");
	    response.getWriter().write(userInfo);
	}
	
	/**
	 * 提取Openid
	 * @param str 形如：callback( {"client_id":"YOUR_APPID","openid":"YOUR_OPENID"} );
	 * @author zgh
	 * @since 2018/5/22 21:37
	 */
	private String getQQOpenid(String str) {
	    // 获取花括号内串
	    String json = str.substring(str.indexOf("{"), str.indexOf("}") + 1);
	    // 转为Map
	    Map<String, String> map = JsonUtils.jsonToPojo(json, Map.class);
	    return map.get("openid");
	}
	
}
