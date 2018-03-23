<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>金立用户认证中心</title>
<link rel="stylesheet" href="css/gnif.css" />
</head>
<body style="background:#f2f2f2;">
  <div class="loginArea01">
    <img src="picture/login_logo_03.png" />
  </div>
  <div class="loginArea02">
    <div class="loginTit01">
      
    </div>
    <form:form method="post" id="fm1" commandName="${commandName}" htmlEscape="true">
      <div class="loginBor01">
		<div class="adImg01"><img src="picture/loginAd.jpg" /></div>
		<div class="forms">
          <div class="lineDi">
            <span class="labe">用户名</span>
			<c:choose>
			  <c:when test="${not empty sessionScope.openIdLocalId}">
				<strong>${sessionScope.openIdLocalId}</strong>
				<input type="hidden" id="username" name="username" value="${sessionScope.openIdLocalId}" />
			  </c:when>
			  <c:otherwise>
				<form:input cssClass="required" cssErrorClass="error" id="username" size="25" tabindex="1" path="username" autocomplete="off" htmlEscape="true" />
			  </c:otherwise>
			</c:choose>
            <div class="inpIco userIco"></div>
          </div>
          <div class="lineDi" id="pass">
            <span class="labe">密码</span>
			<form:password cssClass="required" cssErrorClass="error" id="password" size="25" tabindex="2" path="password" htmlEscape="true" autocomplete="off" />			
            <div class="inpIco passIco"></div>
          </div>
            <c:if test="${showCaptcha == 'true'}">
                <div class="lineDi">
                    <span class="labe">验证码</span>
                    <input style="width:150px;" tabindex="3" class="verification01" type="text" id="captcha"
                           name="captcha"/>
                    <div class="captcha"><img id="captchaImg" src="captcha" onclick="onCaptchaClick()"/></div>
                </div>
            </c:if>
          <div class="lineDi selecTex"><input type="checkbox" id="rememberMe" name="rememberMe" value="true" />两周内自动登录 <a href="forget" target="_blank"/>">忘记密码？</a></div>
          <div class="lineDi"><input id="loginsubmit" type="submit" value=""/></div>
		  <input type="hidden" name="lt" value="${loginTicket}" />
		  <input type="hidden" name="execution" value="${flowExecutionKey}" />
		  <input type="hidden" name="_eventId" value="submit" />
		</div>
		<form:errors path="*" id="msg" cssClass="lineDi login_wrong formerror" element="div" htmlEscape="false" />
      </div>
	</form:form>
	<div style="position:absolute;right:0;bottom:-130px;"><span style="font-size:14px;color:#333;margin-right:20px;">扫一扫下载手机客户端 &gt;&gt;</span><img src="/assp/assp_apk.png" style="width:130px;height:130px;float:right;"/></div>
  </div>
  <div class="shadowLine"></div>
  <div class="bottomArea01">
    <ul>
      <li class="floatL">@版权所有：深圳市金立通信设备有限公司</li>
      <li class="floatR">软件开发：金立集团信息中心开发部</li>
    </ul>
  </div>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<script type="text/javascript">
  $(function(){
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
