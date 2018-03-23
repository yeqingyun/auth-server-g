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
    <div class="head-info"></div>
    <div class="clear"></div>
    <div class="avtar"></div>
    <form method="post">
        <input type="hidden" name="execution" value="${flowExecutionKey}"/>
        <input type="text" name="username" value="${flowRequestContext.flowScope.get("username")}" disabled="disabled">
        <div id="u_key">
            <input type="password" name="password" class="text" onfocus="this.value = '';" placeholder="新密码">
        </div>
        <div id="e_key">
            <input type="password" name="repeat_password" onfocus="this.value = '';" placeholder="重复输入新密码">
        </div>
        <div class="resultMsg">${flowRequestContext.flashScope.get("resultMsg")}</div>
        <div class="signin">
            <input type="submit" name="_eventId_submit" value="提交">
        </div>
    </form>
</div>
<script>
    $(function () {
        $("form").bind({
            "submit": function () {
                var flag = true;
                var va = $("input[name=password]", this).val();
                if (va == null || va == "") {
                    $("div#u_key", this).addClass("key");
                    flag = false;
                } else {
                    $("div#u_key", this).removeClass("key");
                }
                var vb = $("input[name=repeat_password]", this).val();
                if (vb == null || vb == "") {
                    $("div#e_key", this).addClass("key");
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
