<%--<%@ page import="org.springframework.webflow.execution.FlowExecutionContext" %>--%>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="showChange" scope="page" value="${flowRequestContext.currentState.id == 'viewForgetPage' ? false : true}"/>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>金立用户认证中心</title>
    <link rel="stylesheet" href="css/gnif.css"/>
    <link rel='stylesheet' href="css/forget.css"/>
    <script src='amg/jquery.min.js' type='text/javascript'></script>
</head>
<body style="background:#f2f2f2;">
<h1>EOA密码${showChange ? '重置' : '找回'}</h1>
<div class="login-form">
    <!--<div class="close"> </div>-->
    <div class="head-info">
        <%--<label class="lbl-1"> </label>--%>
        <%--<label class="lbl-2"> </label>--%>
        <%--<label class="lbl-3"> </label>--%>
    </div>
    <div class="clear"></div>
    <div class="avtar">
        <!--<img src="images/avtar.png" />-->
    </div>
    <form:form method="post">
        <input type="hidden" name="execution" value="${flowExecutionKey}"/>

        <c:choose>
            <c:when test="${flowRequestContext.currentState.id=='viewForgetPage'}">
                <div id="u_key">
                    <input type="text" name="username" class="text" placeholder="用户名">
                </div>
                <div id="e_key">
                    <input type="text" name="email" placeholder="邮箱">
                </div>
            </c:when>
            <c:when test="${flowRequestContext.currentState.id=='viewChangePage'}">
                <div id="u_key">
                    <input type="password" name="password" class="text" onfocus="this.value = '';" placeholder="新密码">
                </div>
                <div id="e_key">
                    <input type="password" name="repeat_password" onfocus="this.value = '';" placeholder="重复输入新密码">
                </div>
            </c:when>
            <c:otherwise>
                <div style="color: white;margin-top: 20px;padding-bottom: 20px;">
                    <c:choose>
                        <c:when test="${flowRequestContext.currentState.id=='viewOkPage'}">
                            操作成功
                        </c:when>
                        <c:when test="${flowRequestContext.currentState.id=='viewSendOkPage'}">
                            修改密码链接已发送至邮箱，请查收
                        </c:when>
                        <c:when test="${flowRequestContext.currentState.id=='viewChangeOkPage'}">
                            密码已修改
                        </c:when>
                    </c:choose>
                </div>
            </c:otherwise>
        </c:choose>
        <div class="signin">
            <c:choose>
                <c:when test="${flowRequestContext.currentState.id.contains('Ok')}">
                    <input type="submit" value="返回首页" onclick="window.location.href='/'">
                </c:when>
                <c:otherwise>
                    <input type="submit" name="_eventId_submit" value="提交">
                </c:otherwise>
            </c:choose>
        </div>
    </form:form>
</div>
<div class="footer" style="margin-top: 50px;">
    <ul class="bottomArea01">
        <li class="floatL">@版权所有：深圳市金立通信设备有限公司</li>
        <li class="floatR">软件开发：金立集团信息中心开发部</li>
    </ul>
</div>
<script>

    $(function () {
        var va_, vb_, isChange = eval("${showChange}");
        if (isChange) {
            va_ = "password", vb_ = "repeat_password";
        } else {
            va_ = "username", vb_ = "email";
        }
        $("form").bind({
            "submit": function () {
                var flag = true;
                var va = $("input[name=" + va_ + "]", this).val();
                if (va == null || va == "" || va == "用户名" || va == "新密码") {
                    $("div#u_key", this).addClass("key");
                    flag = false;
                } else {
                    $("div#u_key", this).removeClass("key");
                }
                var vb = $("input[name=" + vb_ + "]", this).val();
                if (vb == null || vb == "" || vb == "邮箱" || vb == "重复新密码") {
                    $("div#e_key", this).addClass("key");
                    flag = false;
                } else {
                    $("div#e_key", this).removeClass("key");
                }
                if (flag && isChange && va != vb) {
                    alert("重复密码不一致");
                    flag = false;
                }
                return flag;
            }
        })

    })
</script>
</body>
</html>
