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
    <div class="head-info"></div>
    <div class="clear"></div>
    <div class="avtar"></div>
    <div style="color: white;margin-top: 20px;padding-bottom: 20px;line-height: 50px;font-size: 20px;">
        ${flowRequestContext.flashScope.get("resultMsg")}
    </div>
    <div class="signin">
        <input type="submit" value="返回首页" onclick="window.location.href='${flowRequestContext.flowScope.get('url')}'">
    </div>
</div>
</body>
</html>
