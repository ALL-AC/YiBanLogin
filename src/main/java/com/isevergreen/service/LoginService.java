package com.isevergreen.service;

import com.alibaba.fastjson.JSONObject;
import com.isevergreen.util.RsaUtil;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JIANG
 * @since 2020-09-03
 */
public class LoginService {
    public static final String USER_AGENT_VALUE =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36";

    public static String login(String username, String password, String url) throws Exception {
        // 连接登陆页面
        Response response = Jsoup.connect(url)
                .header("User-Agent", USER_AGENT_VALUE)
                .method(Method.GET)
                .execute();

        Document page = Jsoup.parse(response.body());
        // 获取加密公钥
        String key = page.getElementById("key").val();
        // 去除公钥信息的多余内容，
        String publicKey = key.substring(26, key.length() - 26).replaceAll("[\n\r]", "");
        // 密码加密
        String encryptPassword = RsaUtil.encryptByPublicKey(password, publicKey);

        Map<String, String> data = new HashMap<>(8);
        data.put("oauth_uname", username);
        data.put("oauth_upwd", encryptPassword);

        String clientId = page.getElementById("client_id").val();
        data.put("client_id", clientId);
        String redirectUri = page.getElementById("redirect_uri").val();
        data.put("redirect_uri", redirectUri);

        // 默认数据
        data.put("state", "QUERY");
        data.put("scope", "1,2,3,");

        // 从页面获取
        String display = page.getElementById("display").val();
        data.put("display", display);

        // 提交登陆
        Response loginResponse = Jsoup.connect("https://oauth.yiban.cn/code/usersure")
                .header("User-Agent", USER_AGENT_VALUE)
                .ignoreContentType(true)
                .followRedirects(true)
                .method(Method.POST)
                .data(data)
                .cookies(response.cookies())
                .execute();

        JSONObject jsonObject = JSONObject.parseObject(loginResponse.body());
        return (String) jsonObject.get("reUrl");
    }

    public static void main(String[] args) throws Exception {
        String login = login("13169019622", "zxc13800138000", "https://oauth.yiban.cn/code/html?client_id=f78e7198a73277ef&redirect_uri=http://127.0.0.1:8087/login/auth&state=QUERY");
        System.out.println(login);
    }
}
