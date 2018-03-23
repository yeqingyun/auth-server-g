<%@ page pageEncoding="UTF-8" language="java" import="com.gionee.cas.util.ClientUtil" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<% boolean isMobile = ClientUtil.isMobileClient(request.getHeader("user-agent"));
  if (!isMobile) {
%>
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
            <input style="width:150px;" class="verification01" type="text" id="j_captcha_response" name="j_captcha_response" />
            <div class="captcha"><img src="captcha" /></div>
          </div>
          </c:if>
          <div class="lineDi selecTex"><input type="checkbox" id="rememberMe" name="rememberMe" value="true"/>两周内自动登录 <a
                  href="forget" target="_blank">忘记密码？</a></div>
          <div class="lineDi"><input id="loginsubmit" type="submit" value=""/></div>
		  <input type="hidden" name="lt" value="${loginTicket}" />
		  <input type="hidden" name="execution" value="${flowExecutionKey}" />
		  <input type="hidden" name="_eventId" value="submit" />
		</div>
		<form:errors path="*" id="msg" cssClass="lineDi login_wrong formerror" element="div" htmlEscape="false" />
      </div>
	</form:form>
  </div>
  <div class="shadowLine"></div>
  <div class="bottomArea01">
    <ul>
      <li class="floatL">&copyright;版权所有：深圳市金立通信设备有限公司</li>
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
</script>

</body>
<%
  }
  else {
%>
<!DOCTYPE html>
<html>
  <head>
    <title >登录</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="utf-8">

    <script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
    <link rel="stylesheet" href="xstwap/style.css">
    <link rel="stylesheet" href="css/gnif.css" />
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
      .header{margin-bottom:10px;width:100%;height:50px;position:relative;background-color: #214385;}
      .header h1{text-align:center;}
      .wrap{width:100%;min-width:320px;min-height:320px;-moz-transition:background 0.3s ease-in, color 0.5s
            ease-out;-webkit-transition:background 0.3s ease-in, color 0.5s ease-out;-o-transition:background 0.3s ease-in,
            color 0.5s ease-out;transition:background 0.3s ease-in, color 0.5s ease-out;}
      .item .item_box .t_i{padding:0 4%; width:92%;}
      .top_sign, .recent, .top_recom, .section{margin:0 10px 10px; padding:10px; background-color:#ffffff;}
      .captcha{position: absolute;
               top: 20px;
               right: 13px;}
      .captcha img{height: 33px;
                   width: 110px;
                   border: #ccc 1px solid;}
      .item .t_b.btn_log, .item .t_b.btn_charge, .formitem .t_b {
        background-color: #0d9aff;
      }
      .t_b {
        display: inline-block;
        width: 100%;
        height: 36px;
        line-height: 36px;
        font-size: 1em;
        text-align: center;
        color: #ffffff;
        cursor: pointer;
      }
    fieldset, img {
      border: 0;
      width: 50px;
    }
    </style>
    <script>
$(function () {
  document.onkeydown = function (e) {
    var ev = document.all ? window.event : e;
    if (ev.keyCode == 13) {
      subCheck();
    }
  }
  $("#loginButton").click(function(){
    subCheck();
  });
  $("#username").focus();
});

 function subCheck() {
   var username = $("#username").val();
   var password = $("#password").val();
   var captcha = $("#j_captcha_response").val();
   if(username==''){
     $("#msgBlock").text("请输入用户名");
     $("#username").focus();
     return false;
   }
   if(password==''){
     $("#msgBlock").text("请输入密码")
     $("#password").focus();
     return false;
   }


   $("#loginform").submit();
 }
    </script>


  </head>
  <div class="section">
    <div class="login">
      <form:form method="post" id="loginform" commandName="${commandName}" htmlEscape="true">
        <ul class="blist">
          <li><b id="msgBlock" class="wro" style="color: red"><form:errors path="*" id="msg" element="span" htmlEscape="false" /></b></li>
          <li class="item">
            <div class="item_box"><span class="icons icon_log fl"></span> <span class="item_inp">
                <input placeholder="请输入用户名" name="username" id="username" class="t_i" value="" maxlength="12"
                       required="" type="text"></span></div>
          </li>
          <li class="item">
            <div class="item_box"><span class="icons icon_pass"></span> <span class="item_inp">
                <input placeholder="请输入密码" name="password" id="password" class="t_i" value="" maxlength="15"
                       required="" type="password"></span></div>
          </li>
          <c:if test="${showCaptcha == 'true'}">
          <li class="item">
            <div class="item_box">
              <span class="icons login_code"></span><span class="item_inp">
                <input placeholder="请输入验证码" name="j_captcha_response" id="j_captcha_response" class="t_i" value=""
                       maxlength="15"   required="" type="text">
                <div class="searchbtn"><img src="captcha"width="70" height="40" /></div>
              </span>
            </div>
          </li>
          </c:if>
          <li class="item"><input id="loginButton" class="t_b btn_log" value="登录" type="button" ></li>
          <input type="hidden" name="lt" value="${loginTicket}" />
          <input type="hidden" name="execution" value="${flowExecutionKey}" />
          <input type="hidden" name="_eventId" value="submit" />
        </ul>
      </form:form>
    </div>
  </div>
  <div class="block">
    <ul class="blist">
      <div align="center">
      </div>
    </ul>

    <ul class="blist">
      <div align="center"> <p style="color:#86AECD;font-size: 11px;">©版权所有：金立通信设备有限公司 </p> <br>
        <p style="color:#86AECD;font-size: 11px;">软件开发：金立集团信息中心开发部</p>
      </div>
    </ul>

  </div>
</div>
</html>
<%
  }
%>
