<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="Cache-Control" content="no-cache, must-revalidate"/>
<meta http-equiv="expires" content="0"/>
<title>金立CRM 登录</title>
<link href="crm/login01.css" rel="stylesheet" type="text/css" />
<script src="crm/jquery-1.3.2.min.js" type="text/javascript"></script>
<script type="text/javascript" src="crm/logintangram.js"></script>
    <script type="text/javascript">
        function check() {
            if($('#actionmessage')) {
                $('#actionmessage').remove();
            }
            if( $('#TANGRAM__PSP_4__userName').val()=="" || $('#TANGRAM__PSP_4__userName').val()==null){
                $('#TANGRAM__PSP_4__error').html("") ;
                $('#TANGRAM__PSP_4__error').html('登录账号不能为空！') ;
                return false;
            } else if( $('#TANGRAM__PSP_4__password').val()=="" || $('#TANGRAM__PSP_4__password').val()==null){
                $('#TANGRAM__PSP_4__error').html("") ;
                $('#TANGRAM__PSP_4__error').html('密码不能为空！') ;
                return false;
            } <c:if test="${showCaptcha == 'true'}">else if( $('#TANGRAM__PSP_4__verifyCode').val()=="" || $('#TANGRAM__PSP_4__verifyCode').val()==null){
                $('#TANGRAM__PSP_4__error').html("") ;
                $('#TANGRAM__PSP_4__error').html('验证码不能为空！') ;
                return false;
            }</c:if> else {
                $('#commitUsername').val('CRM_'+$('#TANGRAM__PSP_4__userName').val());
                $('#TANGRAM__PSP_4__error').html("") ;
                return true ;
            }
        }
    </script>
<script language="javascript" type="text/javascript" defer="defer">
   history.go(1); 
</script>
</head>


<body>
<div id="login-body" style="width:100%">
  <div class="header-container">
    <div class="clearfix" id="login-header">
      <div class="logo"> <a class="yun-logo" href="#" target="_blank" >金立</a> </div>
    </div>
    <div id="login-middle">
      <div class="img-content"> 
          <a hidefocus="true" href="javascript:;"> <img class="index-slide-img current" src="crm/banner01_06.jpg"/></a> 
          <a hidefocus="true" href="javascript:;"> <img class="index-slide-img" src="crm/banner02_05.jpg"/></a>
       </div>
      <div class="yunbg">
        <ul class="focus-content clearfix">
          <li class="focus-content-item"><a class="focus-anchor current" hidefocus="true" href="javascript:;" idx="0"></a></li>
          <li class="focus-content-item"><a class="focus-anchor" hidefocus="true" href="javascript:;" idx="1"></a></li>
        </ul>
      </div>
      <div class="header-login">
        <div class="pass-login-title">用户登录</div>
        <div class="tang-pass-login" id="netdisk_pass_login_form">
        <!-- <form id="TANGRAM__PSP_4__form" class="pass-form" name="form" action="Login!login.shtml" method="post" onsubmit="return check()"> -->
		<form:form method="post" id="TANGRAM__PSP_4__form" cssClass="pass-form" commandName="${commandName}" htmlEscape="true">
            <p id="TANGRAM__PSP_4__errorWrapper" class="pass-generalErrorWrapper">
            <span id="TANGRAM__PSP_4__error" class="actionMessage">
			  <form:errors path="*" id="actionmessage" cssClass="actionMessage" element="div" htmlEscape="false" />
			</span></p>
            <p id="TANGRAM__PSP_4__hiddenFields" style="display:none">
			  <input type="hidden" name="lt" value="${loginTicket}" />
			  <input type="hidden" name="execution" value="${flowExecutionKey}" />
			  <input type="hidden" name="_eventId" value="submit" />
			  <form:input id="commitUsername" type="hidden" path="username"/>
            </p>
            <p id="TANGRAM__PSP_4__userNameWrapper" class="pass-form-item pass-form-item-userName" style="display:block">
              <label for="TANGRAM__PSP_4__userName" id="TANGRAM__PSP_4__userNameLabel" class="pass-label pass-label-userName">用户名</label>
              <!-- <input id="TANGRAM__PSP_4__userName" type="text" name="usr.login" class="pass-text-input pass-text-input-userName" autocomplete="off" value=""/> -->
			  <input class="pass-text-input pass-text-input-userName" id="TANGRAM__PSP_4__userName" tabindex="1" autocomplete="off" htmlEscape="true" />
              <span id="TANGRAM__PSP_4__userName_clearbtn" class="pass-clearbtn pass-clearbtn-userName" style="display: none;"></span><span id="TANGRAM__PSP_4__userNameTip" class="pass-item-tip pass-item-tip-userName" style="display:none"><span id="TANGRAM__PSP_4__userNameTipText" class="pass-item-tiptext pass-item-tiptext-userName"></span></span></p>
            <p id="TANGRAM__PSP_4__passwordWrapper" class="pass-form-item pass-form-item-password" style="display:block">
              <label for="TANGRAM__PSP_4__password" id="TANGRAM__PSP_4__passwordLabel" class="pass-label pass-label-password">密码</label>
              <!-- <input id="TANGRAM__PSP_4__password" type="password" name="usr.pwd" class="pass-text-input pass-text-input-password"  value=""/> -->
			  <form:password cssClass="pass-text-input pass-text-input-password" id="TANGRAM__PSP_4__password" tabindex="2" path="password" htmlEscape="true" autocomplete="off" />
              <span id="TANGRAM__PSP_4__password_clearbtn" class="pass-clearbtn pass-clearbtn-password" style="display: none;"></span><span id="TANGRAM__PSP_4__passwordTip" class="pass-item-tip pass-item-tip-password" style="display:none"><span id="TANGRAM__PSP_4__passwordTipText" class="pass-item-tiptext pass-item-tiptext-password"></span></span></p>
            <c:if test="${showCaptcha == 'true'}">
            <p id="TANGRAM__PSP_4__verifyCodeImgWrapper" class="pass-form-item pass-form-item-verifyCode">
              <label for="TANGRAM__PSP_4__verifyCode" id="TANGRAM__PSP_4__verifyCodeLabel" class="pass-label pass-label-verifyCode">验证码</label>
              <input id="TANGRAM__PSP_4__verifyCode" tabindex="3" type="text" name="captcha" class="pass-text-input pass-text-input-verifyCode" maxlength="6"/>
              <span id="TANGRAM__PSP_4__verifyCode_clearbtn" class="pass-clearbtn pass-clearbtn-verifyCode" style="display:none;"></span><span id="TANGRAM__PSP_4__verifyCodeImgParent" class="pass-verifyCodeImgParent"><img id="TANGRAM__PSP_4__verifyCodeImg" class="pass-verifyCode" src="captcha"></span><a id="TANGRAM__PSP_4__verifyCodeChange" href="#" class="pass-change-verifyCode">换一张</a><span id="TANGRAM__PSP_4__verifyCodeError" class="pass-error pass-error-verifyCode"></span><span id="TANGRAM__PSP_4__verifyCodeTip" class="pass-tip pass-tip-verifyCode"></span></p>
            </c:if>
            <p id="TANGRAM__PSP_4__memberPassWrapper" class="pass-form-item pass-form-item-memberPass">
                <input id="TANGRAM__PSP_4__memberPass" type="checkbox" name="memberPass"
                       class="pass-checkbox-input pass-checkbox-memberPass">
              <label for="TANGRAM__PSP_4__memberPass" id="TANGRAM__PSP_4__memberPassLabel" class="">下次自动登录</label>
                <label style="cursor: pointer;" onclick="window.open('forget')">忘记密码</label>
            </p>
            <p id="TANGRAM__PSP_4__submitWrapper" class="pass-form-item pass-form-item-submit">
              <input id="TANGRAM__PSP_4__submit" type="submit" value="立即登录" class="pass-button pass-button-submit">
              

              <a class="pass-reglink" href="http://crm.gionee.com/UploadFile!isApkExists.do?downTyp=0" style="padding-right: 100px;">CRM手机端下载</a>
              <a class="pass-reglink" href="http://www.firefox.com.cn/" target="_blank">建议浏览器</a><a class="pass-fgtpwd" href="#" target="_blank" style="display:none;">忘记密码？</a></p>
              <!-- </form> -->
			  </form:form>
        </div>
      </div>
      <div>
        <img style="width: 120px; position:float;margin-left: 80%;margin-top:3%" src="crm/apk.jpg" />
        <span style="width:120px; position:float;margin-left: 81%">扫一扫下载手机端</span>
      </div>
    </div>
  </div>
  <div class="footer">
      <ul class="bottomArea01">
          <li class="floatL">@版权所有：深圳市金立通信设备有限公司</li>
          <li class="floatR">软件开发：金立集团信息中心开发部</li>
      </ul>
  </div>
</div>
<script type="text/javascript">
//function swithchBanner(ab){
//    var aAll = ab.parentNode.getElementsByTagName("a");
//    alert(aAll.length)
//    for(var i;i =< aAll.length; i++ ){
//        aAll[i].className = "focus-anchor";
//    }
//    ab.className = "focus-anchor current";
//}
//var a = document.getElementById("login-middle").getElementsByTagName("div");
//var b = a[1].getElementsByTagName("a");
//b[0].onclick =  function(){alert("ok");}
$(function(){
	$('.focus-anchor').click(function(){
	    $(this).addClass('current').parent().siblings().children().removeClass('current');
		var anchorNumber = $('.focus-anchor').index($(this));
		$('.index-slide-img').eq(anchorNumber).animate({'opacity':1},2000).addClass('current').parent().siblings().children('img').animate({'opacity':0},1000).removeClass('current');
	});
	var i = 0;
	function autoPlayer (){
		var ddd = $('.focus-anchor').length-1;
		i = i >= ddd ? 0:++i;$('.focus-anchor').eq(i).trigger('click');
	}
	setInterval(autoPlayer,4000);
	$('.pass-text-input').focus(function(){
	    $(this).siblings('.pass-label').css('text-indent','-999em');
	})
	$('.pass-text-input').blur(function(){
		if($(this).val() == false){
			$(this).siblings('.pass-label').removeAttr('style')
		};
	});
	var usr = $("#TANGRAM__PSP_4__userName").val();
	var pwd = $("#TANGRAM__PSP_4__password").val();
	if(usr != null && usr != '' ) {
		$('#TANGRAM__PSP_4__userNameLabel').css('text-indent','-999em');
	}
	if(pwd != null && pwd != '') {
		$('#TANGRAM__PSP_4__passwordLabel').css('text-indent','-999em');
	}
});
</script>
</body>
