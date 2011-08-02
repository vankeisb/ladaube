<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ attribute name="str" required="true" type="java.lang.String" %><%
    String s = str.replace("\'", "\\\'").
            replace("\n", "\\n").
            replace('(', ' ').
            replace(')',' ');
    if (s.length()>20) {
        s = s.substring(0,19);
    }
%><c:out value="<%=s%>"/>