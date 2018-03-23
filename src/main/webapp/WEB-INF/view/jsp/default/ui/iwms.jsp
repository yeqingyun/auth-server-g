<%@ page pageEncoding="UTF-8" language="java" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Welcome to Gionee GSC System</title>
    <link rel="stylesheet" type="text/css" href="css/iwms.css"/>
</head>
<body>
<div class="login-body">
    <!--header-->
    <div class="login-header">
        <h1><img src="iwms/logo.png" width="165" height="41"/></h1>
    </div>
    <!--middle-->
    <div class="login-middle">
        <div class="poster"><img src="iwms/poster.png" width="463" height="46"/></div>
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
                <%--<p class="input-com-t verify">--%>
                <%--<label class="verify-label"></label>--%>
                <%--<input class="login-input-t fl" type="text" id="captcha" name="captcha"/>--%>
                <%--<span class="pass-verifyCode"><img id="captchaImg" src="captcha" width="93" height="33"--%>
                <%--onclick="onCaptchaClick()"/></span></p>--%>
            <p class="remeber">
                <input type="checkbox" name="rememberMe" id="rememberMe" value="true" tabindex="5"/>
                <label for="rememberMe">remember me</label>
            </p>
            <b class="wro"><form:errors path="*" id="msg" element="span" htmlEscape="false"/>&nbsp;</b>
            <p class="log">
                <input type="submit" class="log-input" value="Log In"/>
                <input type="hidden" name="execution" value="${flowExecutionKey}"/>
                <input type="hidden" name="_eventId" value="submit"/>
                </form:form>
        </div>
    </div>
    <!--foot-->
    <div class="foot">
        <ul>
            <li>&copy;2016 Gionee, Inc.</li>
            <li>Develop By Gionee IT Department.</li>
        </ul>
    </div>
</div>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<script type="text/javascript">
    $(function () {
        $('#username').attr('placeholder', 'Username');
        $('#password').attr('placeholder', 'Password');
//        $('#j_captcha_response').attr('placeholder', 'Captcha');
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