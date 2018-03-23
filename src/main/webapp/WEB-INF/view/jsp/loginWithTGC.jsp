<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Auth Login CORS</title>
    <script type="text/javascript">
        window.onload = function () {
            window.addEventListener('message', function (event) {
//                暂时移除域名限制
                <%--if (event.origin != "<%=EoaConfiguration.getEoaUrl()%>") {--%>
                <%--return;--%>
                <%--}--%>

                var domain = event.data.domain;
                var service = event.data.validateUrl;
                if (domain == null || service == null || service == "") {
                    return;
                }

                var oReq = new XMLHttpRequest();
                var response = {};
                oReq.onreadystatechange = function () {
                    if (this.readyState == 4 && this.status == 200) {
                        var responseText = this.responseText;
                        var reg = new RegExp(/.*name="ticket">(.*)<\/textarea>.*/);
                        reg.test(responseText);
                        var serviceTicket = RegExp.$1;
                        if (serviceTicket != "") {
                            response.serviceTicket = serviceTicket;
                            response.isSuccess = true;
                        } else {
                            response.isSuccess = false;
                        }

                        parent.postMessage(response, domain);
                    }
                };
                oReq.open("post", "/login?method=post&service=" + service, true);
                oReq.setRequestHeader("X-Request-With", "XMLHttpRequest");
                oReq.send();
            }, false);
        };
    </script>
</head>
<body>

</body>
</html>
