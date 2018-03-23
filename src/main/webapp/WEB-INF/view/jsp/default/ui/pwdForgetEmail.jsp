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
<h1>统一认证平台 密码找回</h1>
<div class="login-form">
    <div class="change-type phone" onclick="window.location.href='${flowExecutionUrl}&_eventId=phone'">手机验证</div>
    <div class="head-info" style=""></div>
    <div class="clear"></div>
    <div class="avtar"></div>
    <form method="post">
        <input type="hidden" name="execution" value="${flowExecutionKey}"/>
        <div id="u_key">
            <input type="text" name="username" class="text" placeholder="工号">
        </div>
        <div id="v_key">
            <input type="text" name="email" placeholder="邮箱">
        </div>
        <div class="remark">注：邮箱须与OA通讯录中匹配</div>
        <div class="resultMsg">${flowRequestContext.flashScope.get("resultMsg")}</div>
        <div class="signin">
            <input type="submit" name="_eventId_submit" value="提交">
        </div>
    </form>
</div>
<script>
    var usernameREG = /\d+/;
    var mailREG = /.*@.*/;
    $(function () {
        $("form").bind({
            "submit": function () {
                var flag = true;
                var va = $("div#u_key > input", this).val();
                if (va == null || va == "" || !usernameREG.test(va)) {
                    $("div#u_key", this).addClass("key");
                    flag = false;
                } else {
                    $("div#v_key", this).removeClass("key");
                }
                var vb = $("div#v_key > input", this).val();
                if (vb == null || vb == "" || !mailREG.test(vb)) {
                    $("div#v_key", this).addClass("key");
                    flag = false;
                } else {
                    $("div#e_key", this).removeClass("key");
                }
                return flag;
            }
        })
    })

</script>
</body>
</html>
