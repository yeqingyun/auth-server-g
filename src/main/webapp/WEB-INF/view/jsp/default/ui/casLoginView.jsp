<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String service = request.getParameter("service");
    if (service != null && (service.contains("eoa.") || service.contains("uc."))) {
%>
<c:import url="eoa.jsp"/>
<%
} else if (service != null && (service.contains("gncrm.") || service.contains("xst."))) {
%>
<c:import url="xst.jsp"/>
<%
} else if (service != null && (service.contains("mes.") || service.contains("mes2."))) {
%>
<c:import url="mes.jsp"/>
<%
} else if (service != null && service.contains("assp.")) {
%>
<c:import url="assp.jsp"/>
<%
} else if (service != null && service.contains("bos.")) {
%>
<c:import url="default.jsp"/>
<%
} else if (service != null && service.contains("os.")) {
%>
<c:import url="amg.jsp"/>
<%
} else if (service != null && service.contains("ucrm.")) {
%>
<c:import url="default.jsp"/>
<%
} else if (service != null && service.contains("mewms.")) {
%>
<c:import url="mewms.jsp"/>
<%
} else if (service != null && service.contains("iwms.")) {
%>
<c:import url="iwms.jsp"/>
<%
} else if (service != null && service.contains("crm.")) {
%>
<c:import url="crm.jsp"/>
<%
} else {
%>
<c:import url="default.jsp"/>
<%
    }
%>
</html>