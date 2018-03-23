<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
  <head>
    <meta content='width=device-width,initial-scale=1.0, maximum-scale=1.0,user-scalable=no' name='viewport'>
    <meta content='zh-CN' http-equiv='content-language'>
    <meta content='text/html; charset=utf-8' http-equiv='Content-Type'>
    <meta content='webkit' name='renderer'>
    <meta content='IE=edge' http-equiv='X-UA-Compatible'>
    <title>登录 - 阿米哥信息系统</title>
    <link href='amg/favicon.ico' rel='shortcut icon' type='image/x-icon'>
    <link href='amg/application.css' rel='stylesheet' type='text/css'>
    <link href='amg/bootstrap.min.css' rel='stylesheet'>
    <script src='amg/jquery.min.js' type='text/javascript'></script>
    <script src='amg/application.js' type='text/javascript'></script>
    <script src='amg/respond.min.js' type='text/javascript'></script>
      <script type="text/javascript">
          function onCaptchaClick() {
              var now = new Date();
              $('#captchaImg').attr('src', "captcha?" + now.getTime());
          }
      </script>
  </head>
  <body>
    <div class='container'>
      <div class='logo'>
        <img src='/amg/logo-new.png'>
      </div>
    </div>
    <div class='banner'>
      <div class='container'>
        <div class='row'>
          <div class='showing col-md-8'>
            <div class='showing-ad'>
            </div>
          </div>
          <div class='col-md-4'>
			<form:form method="post" id="fm" commandName="${commandName}" htmlEscape="true">
              <div class='login'>
				<div class='login-head'>
                  <img src='amg/head.png'>
				  <div class='alert alert-danger hidden' id='clientmsg'></div>
				  <form:errors path="*" id="msg" cssClass="alert alert-danger" element="div" htmlEscape="false" />
				</div>
				<div class='login-form'>
                  <div class='login-account'>
                    <div class='login-account-image'></div>
					<form:input cssClass="login-input" id="username" size="25" tabindex="1" placeholder="用户名" path="username" autocomplete="off" htmlEscape="true" />
                  </div>
                  <div class='login-pass clearfix'>
                    <div class='login-pass-image'></div>
					<form:password cssClass="login-input" id="password" size="25" tabindex="2" placeholder="密码" path="password" htmlEscape="true" autocomplete="off" />
                  </div>
                    <c:if test="${showCaptcha == 'true'}">
                        <div class='login-code clearfix'>
                            <input maxlength='4' name='captcha' placeholder='请输入右侧验证码' type='text'>
                            <div class='login-code-image'>
                                <img id="captchaImg" src="captcha" onclick="onCaptchaClick()"/>
                            </div>
                        </div>
                    </c:if>
                  <div class='login-remember clearfix'>
                    <div class='login-forgetpass'>
                      <a href='forget' target="_blank">忘记密码？</a>
                    </div>
                    <div class='login-autologin'>
                      <input name='rememberMe' type='checkbox' value='true'>
                      <span>两周内自动登录</span>
                    </div>
                  </div>
                  <div class='login-submit'>
                    <input type='submit' value='登  录'>
                  </div>
                  <div class='login-other'>
					<input type="hidden" name="lt" value="${loginTicket}" />
					<input type="hidden" name="execution" value="${flowExecutionKey}" />
					<input type="hidden" name="_eventId" value="submit" />
                  </div>
				</div>
			  </div>
			</form:form>
		  </div>
		</div>
	  </div>
	</div>
<div class='footer'>
  <div class='container'>
    <p>2017 &copy; 深圳市金立通信设备有限公司</p>
  </div>
</div>
</body>
</html>
