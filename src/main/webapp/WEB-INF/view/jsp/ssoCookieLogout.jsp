<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SSO Cookie Logout</title>
    <script type="text/javascript">
        window.onload = function () {
            window.addEventListener('message', function (event) {
//                暂时移除域名限制
                <%--if (event.origin != "<%=EoaConfiguration.getEoaUrl()%>") {--%>
                <%--return;--%>
                <%--}--%>

                var server = event.data.server;
                var jwt = event.data.jwt;
                if (!jwt) {
                    clearSSOCookie(server);
                } else {
                    addJwtToBlacklistFirst(jwt, server);
                }
            }, false);
        };

        function clearSSOCookie(server) {
            var oReq = new XMLHttpRequest();
            oReq.onreadystatechange = function () {
                if (this.readyState == 4 && this.status == 200) {
                    parent.postMessage("logoutSuccess", server);
                }
            };
            oReq.open("get", "/logout", true);
            oReq.setRequestHeader("X-Request-With", "XMLHttpRequest");
            oReq.send();
        }

        function addJwtToBlacklistFirst(jwt, server) {
            var xmlHttpRequest = new XMLHttpRequest();
            xmlHttpRequest.open("delete", "/v1/jwts?service=" + server, true);
            xmlHttpRequest.setRequestHeader("Authorization", "Bearer " + jwt);
            xmlHttpRequest.setRequestHeader("X-Request-With", "XMLHttpRequest");
            xmlHttpRequest.onreadystatechange = function () {
                if (this.readyState == 4 && this.status == 200) {
                    clearSSOCookie(server);
                }
            };
            xmlHttpRequest.send();
        }
    </script>
</head>
<body>

</body>
</html>
