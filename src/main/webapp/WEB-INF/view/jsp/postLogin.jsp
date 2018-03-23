<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Auth Login CORS</title>
    <script type="text/javascript">
        // replace some special character
        window.onload = function () {
            window.addEventListener('message', function (event) {
//                暂时移除域名限制
                <%--if (event.origin != "<%=EoaConfiguration.getEoaUrl()%>") {--%>
                <%--return;--%>
                <%--}--%>
                var params = {
                    username: event.data.username,
                    password: event.data.password,
                    service: event.data.service
                };

                var paramsData = "username=" + params.username + "&password="
                    + encodeURIComponent(params.password) + "&rememberMe=true&service=" + params.service;
                var oReq = new XMLHttpRequest();
                var response = {};
                oReq.onreadystatechange = function () {
                    if (this.readyState == 4) {
                        if (this.status == 200) {
                            var responseText = this.responseText;
                            response.isSuccess = true;
                            response.serviceTicket = this.responseText;
                            parent.postMessage(response, event.origin);
                        }

                        if (this.status == 401) {
                            var exceptions = JSON.parse(this.responseText);
                            var details = exceptions.authentication_exceptions;
                            if (details != null && details.length > 0) {
                                var exceptionType = details[0];
                                response.isSuccess = false;
                                if (exceptionType == "FailedLoginException") {
                                    response.message = "用户名或密码错误";
                                } else {
                                    response.message = "登录失败，请联系管理员处理";
                                }

                                parent.postMessage(response, event.origin);
                            }
                        }
                    }
                };
                oReq.open("post", "/spaLogin", true);
                oReq.setRequestHeader("X-Request-With", "XMLHttpRequest");
                oReq.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                oReq.send(paramsData);
            }, false);
        };
    </script>
</head>
<body>

</body>
</html>
