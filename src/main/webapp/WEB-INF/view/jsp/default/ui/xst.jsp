<%@ page pageEncoding="UTF-8" language="java" import="com.gionee.cas.util.ClientUtil" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<% boolean isMobile = ClientUtil.isMobileClient(request.getHeader("user-agent"));
    if (!isMobile) {
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>欢迎使用销售通</title>
    <link rel="stylesheet" type="text/css" href="css/xst.css"/>
</head>
<body>
<div class="login-body">
    <!--header-->
    <div class="login-header">
        <h1><img src="xst/logo.png" width="165" height="41"/></h1>
    </div>
    <!--middle-->
    <div class="login-middle">
        <div class="poster"><img src="xst/poster.png" width="463" height="46"/></div>
        <div class="login-log">
            <form:form method="post" id="fm1" commandName="${commandName}" htmlEscape="true">
                <p class="h74"></p>
                <p class="input-com">
                    <label class="pass-label"></label>
                    <c:choose>
                        <c:when test="${not empty sessionScope.openIdLocalId}">
                            <strong>${sessionScope.openIdLocalId}</strong>
                            <input type="hidden" id="username" name="username" value="${sessionScope.openIdLocalId}"/>
                        </c:when>
                        <c:otherwise>
                            <form:input cssClass="login-input" id="username" size="25" tabindex="1" path="username"
                                        autocomplete="off" htmlEscape="true"/>
                        </c:otherwise>
                    </c:choose>
                </p>
                <p class="input-com password">
                    <label class="password-label"></label>
                    <form:password cssClass="login-input" id="password" size="25" tabindex="2" path="password"
                                   htmlEscape="true" autocomplete="off"/>
                </p>
                <c:if test="${showCaptcha == 'true'}">
                    <p class="input-com-t verify">
                        <label class="verify-label"></label>
                        <input class="login-input-t fl" tabindex="3" type="text" id="captcha" name="captcha"/>
                        <span class="pass-verifyCode"><img id="captchaImg" src="captcha" width="93" height="33"
                                                           onclick="onCaptchaClick()"/></span></p>
                </c:if>

                <p class="remeber" style="float: left">
                    <input type="checkbox" name="rememberMe" id="rememberMe" value="true" tabindex="5"/>
                    <label for="rememberMe"><spring:message code="screen.rememberme.checkbox.title"/></label>
                </p>
                <p class="remeber"  style="float: left;margin-left: 20px;">
                    <label onclick="window.open('forget')" style="cursor:pointer;">忘记密码</label>
                </p>
                <b class="wro"><form:errors path="*" id="msg" element="span" htmlEscape="false"/>&nbsp;</b>
                <p class="log">
                    <input type="submit" class="log-input" value="登录"/>
                    <a class="telp" style="padding-right:100px;" href="http://xst.gionee.com/client.html">销售通手机端下载</a>
                    <a class="telp" href="http://www.baidu.com/s?ie=UTF-8&amp;wd=chrome">建议浏览器</a></p>
                <input type="hidden" name="lt" value="${loginTicket}"/>
                <input type="hidden" name="execution" value="${flowExecutionKey}"/>
                <input type="hidden" name="_eventId" value="submit"/>
            </form:form>
        </div>
    </div>
    <!--foot-->
    <div class="foot">
        <ul>
            <li>
                <dl>
                    <dt><img src="xst/apk.png" width="120" height="120"/></dt>
                    <dd>扫一扫下载手机端</dd>
                </dl>
            </li>
            <li>&copy;版权所有：深圳市金立通信设备有限公司</li>
            <li>软件开发：金立集团信息中心开发部</li>
        </ul>
    </div>
</div>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<script type="text/javascript">
    $(function () {
        if (!$('#password').val()) {
            $('#password').focus();
        }
        if (!$('#username').val()) {
            $('#username').focus();
        }
    });

    function onCaptchaClick() {
        var now = new Date();
        $('#captchaImg').attr('src', "captcha?" + now.getTime());
    }
</script>
</body>
</html>
<%
} else {
%>
<!DOCTYPE html>
<html>
<head>
    <base href="${base}"></base>
    <title>登录</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="utf-8">
    <!-- Bootstrap -->
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="http://cdn.bootcss.com/html5shiv/3.7.0/html5shiv.min.js"></script>
    <script src="http://cdn.bootcss.com/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="xstwap/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="xstwap/bootstrap.min.js"></script>
    <link rel="stylesheet" href="xstwap/style.css">
    <style type="text/css">
        .code {
            font-family: Arial;
            font-style: italic;
            color: Red;
            border: 0;
            padding: 2px 3px;
            letter-spacing: 3px;
            font-weight: bolder;
        }

        .unchanged {
            border: 0;
        }
    </style>
    <script>
        window.onload = function () {
            if (window.applicationCache) {

            } else {
                alert("你的浏览器不支持HTML5,请下载最新版本的浏览器");
                location.href = "http://www.baidu.com";
            }
        }

    </script>
    <script>
        //加载用户信息
        $(function () {
            var user = localStorage.getItem("userInfo");
            if (user != null) {
                var userInfo = JSON.parse(user);
                $("#username").val(userInfo.username);
                $("#password").val(userInfo.password);
                console.info(userInfo.username);
            }
            //单击回车
            document.onkeydown = function (e) {
                var ev = document.all ? window.event : e;
                if (ev.keyCode == 13) {
                    subCheck();
                }
            }

        });

        function subCheck() {
            var username = $("#username").val();
            var password = $("#password").val();
            if (username == '') {
                alert("请输入用户名");
                return false;
            }
            if (password == '') {
                alert("请输入密码");
                return false;
            }
            if (password.length < 6) {
                alert("密码长度不能小于六位");
                return false;
            }


            var user = new Object;
            user.username = username;
            user.password = password;
            localStorage.setItem("userInfo", JSON.stringify(user));
            /*   if (!validate()) {
             return false;
             } */
            $("#loginform").submit();
            //location.href = "/wapres/html5/middle.html";
        }
    </script>

    <script language="javascript" type="text/javascript">
        /*   console.info(Math.floor(Math.random() * 10));
         var code; //在全局 定义验证码
         function createCode() {
         } */

        /*     function validate() {
         var inputCode = $("#input1").val();
         if (inputCode.length <= 0) {
         alert("请输入验证码！");
         } else if (inputCode != code) {
         createCode();
         alert("验证码输入错误！");
         //刷新验证码
         return false;
         } else {
         //alert("^-^ OK");
         return true;
         }
         } */
    </script>

</head>
<div class="header">
    <h1>登录</h1></div>
<div class="wrap">
    <div class="t_p"><p></p></div>
    <div class="section">
        <div class="login">
            <form:form method="post" id="fm1" commandName="${commandName}" htmlEscape="true">
                <ul class="blist">
                    <li class="item">
                        <div class="item_box"><span class="icons icon_log fl"></span> <span class="item_inp"><input
                                placeholder="请输入用户名" name="username" id="username" class="t_i" value="" maxlength="12"
                                required="" type="text"></span></div>
                    </li>
                    <li class="item">
                        <div class="item_box"><span class="icons icon_pass"></span> <span class="item_inp"><input
                                placeholder="请输入密码" name="password" id="password" class="t_i" value="" maxlength="15"
                                required="" type="password"></span></div>
                    </li>
                    <c:if test="${showCaptcha == 'true'}">
                    <li class="item">
                        <div class="item_box">
                            <span class="icons login_code"></span><span class="item_inp"/>
                            <input placeholder="请输入验证码" name="j_captcha_response" id="j_captcha_response" class="t_i"
                                   value="" maxlength="15"
                                   required="" type="text">
                            <div class="searchbtn"><img src="captcha" style="margin-left: -20px" width="70"
                                                        height="40"/>
							</div>
                        </div>
                    </li>
                    </c:if>
                    <li><b class="wro" style="color: red"><form:errors path="*" id="msg" element="span"
                                                                       htmlEscape="false"/>&nbsp;</b></li>
                    <li class="item"><input class="t_b btn_log" value="登录" type="submit"></li>
                    <input type="hidden" name="lt" value="${loginTicket}"/>
                    <input type="hidden" name="execution" value="${flowExecutionKey}"/>
                    <input type="hidden" name="_eventId" value="submit"/>
                </ul>
            </form:form>
        </div>
    </div>
    <div class="block">
        <ul class="blist">
            <div align="center">
                <li class="item"><a href="http://xst.gionee.com/client.html">android客户端下载</a></p></li>
            </div>
        </ul>
        <ul class="blist">
            <div align="center"><p style="color:#86AECD;font-size: 11px;">©版权所有：深圳市金立通信设备有限公司 </p> <br>
                <p style="color:#86AECD;font-size: 11px;">软件开发：金立集团信息中心开发部</p>
            </div>
        </ul>
    </div>
</div>
</html>
<%
    }
%>
