<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
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
<h1>统一认证平台 密码重置</h1>
<div class="login-form">
    <div class="change-type refresh" onclick="window.location.href='${flowExecutionUrl}&_eventId=send'">重新发送</div>
    <div class="head-info"></div>
    <div class="clear"></div>
    <div class="avtar"></div>
    <form method="post">
        <input type="hidden" name="execution" value="${flowExecutionKey}"/>
        <input type="text" name="username" value="${flowRequestContext.flowScope.get("username")}" disabled="disabled">
        <input type="text" name="phone" value="${flowRequestContext.flowScope.get("phone")}" disabled="disabled">
        <div id="u_key">
            <input type="text" name="code" placeholder="验证码">
        </div>
        <div class="resultMsg">${flowRequestContext.flashScope.get("resultMsg")}</div>
        <div class="signin">
            <input type="submit" name="_eventId_submit" value="提交">
        </div>
    </form>
</div>
<script>
    var time_out_interval;
    $(function () {
        time_out_interval = self.setInterval("showOutTime('${flowRequestContext.flowScope.get("last_sms_timestamp")}')", 1000);
        $("div.refresh").html();
        $("form").bind({
            "submit": function () {
                var flag = true;
                var va = $("div#u_key > input", this).val();
                if (va == null || va == "") {
                    $("div#u_key", this).addClass("key");
                    flag = false;
                } else {
                    $("div#u_key", this).removeClass("key");
                }
                return flag;
            }
        })
    })

    var codeDiv = $("div.refresh");
    function showOutTime(last_sms_timestamp) {
        var sec = 120 - (new Date().getTime() - last_sms_timestamp) / 1000;
        codeDiv.html(parseInt(sec) + "s后重试");
        if (sec <= 0) {
            codeDiv.html("获取验证码");
            //倒计数为零，停止倒计时
            window.clearInterval(time_out_interval);
        }
    }
</script>
</body>
</html>
