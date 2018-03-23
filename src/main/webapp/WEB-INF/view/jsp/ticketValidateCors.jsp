<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Ticket Validate CORS</title>
    <script type="text/javascript">
        window.onload = function () {
            window.addEventListener('message', function (event) {
//                暂时移除域名限制
                <%--if (event.origin != "<%=EoaConfiguration.getEoaUrl()%>") {--%>
                <%--return;--%>
                <%--}--%>

                var server = event.data.server;
                var validateUrl = event.data.validateUrl;
                var ticket = event.data.ticket;

                var oReq = new XMLHttpRequest();
                oReq.onreadystatechange = function () {
                    if (this.readyState == 4 && this.status == 200) {
                        parent.postMessage(this.responseText, server);
                    }
                };
                oReq.open("get", "/p3/serviceValidate?service=" + validateUrl + "&format=JWT&ticket=" + ticket, true);
                oReq.send();
            }, false);
        };
    </script>
</head>
<body>
</body>
</html>
