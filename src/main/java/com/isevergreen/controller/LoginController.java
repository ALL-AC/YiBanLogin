package com.isevergreen.controller;

import cn.yiban.open.Authorize;
import cn.yiban.open.common.User;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.isevergreen.entity.LoginUser;
import com.isevergreen.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author JIANG
 * @since 2020-08-30
 */
@RestController
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("${app.appid}")
    private String appId;

    @Value("${app.appsec}")
    private String appSec;

    @Value("${app.backurl}")
    private String backUrl;

    @PostMapping("/login")
    public void loginAction(@RequestBody LoginUser user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Authorize authorize = new Authorize(appId, appSec);
        String url = authorize.forwardurl(backUrl, "QUERY", Authorize.DISPLAY_TAG_T.MOBILE);
        // 后台直接根据提交的数据进行登陆
        String reUrl = LoginService.login(user.getUsername(), user.getPassword(), url);
        if (!reUrl.startsWith(backUrl)) {
            throw new RuntimeException("登陆失败");
        }
        String replaceUrl = reUrl.replace(backUrl, "/login/auth");
        // 转发
        request.getRequestDispatcher(replaceUrl).forward(request, response);
    }

    @RequestMapping("/login/auth")
    public String authorize(HttpServletRequest request) {
        // 获取code
        String code = request.getParameter("code");
        // 创建Authorize对象
        Authorize authorize = new Authorize(appId, appSec);
        // querytoken会抛出javax.net.ssl.SSLHandshakeException异常
        // 内部是通过https来post数据
        String text = authorize.querytoken(code, backUrl);
        // 获得返回的json数据
        JSONObject json = JSON.parseObject(text);
        // 获得accessToken授权凭证
        String accessToken = json.getString("access_token");
        // 用授权凭证获得易班的User用户对象
        User user = new User(accessToken);
        // 将User对象信息转为json数据
        JSONObject object = JSON.parseObject(user.me());
        logger.info(object.toJSONString());
        // 把json数据保存到session方便后续获取数据
        HttpSession session = request.getSession();
        session.setAttribute("user", object);
        return "登陆成功";
    }

    @GetMapping("/getInfo")
    public JSONObject getInfo(HttpServletRequest request) {
        JSONObject object = (JSONObject) request.getSession().getAttribute("user");
        return object != null ? object : JSON.parseObject("无用户数据");
    }
}
