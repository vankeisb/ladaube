<%@ page import="com.ladaube.actions.Users" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="taglibs.jsp"%>
<html>
  <head><title>LaDaube users (temporary)</title></head>
  <body>
    <s:messages/>
    <s:errors/>
    <h1>Users</h1>
    <table border="1">
        <tbody>
            <tr>
                <th>username</th>
                <th>email</th>
                <th>buddies</th>
            </tr>
            <c:forEach var="u" items="${actionBean.users}">
                <tr>
                    <td>${u.id}</td>
                    <td>${u.email}</td>
                    <td>
                        <ul>
                            <c:forEach var="b" items="${actionBean.usersMap[u]}">
                                <li>${b.id}</li>
                            </c:forEach>
                        </ul>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <h1>Add user</h1>
    <s:form beanclass="<%=Users.class%>">
        Username : <s:text name="username"/><br/>
        Email : <s:text name="email"/><br/>
        <s:hidden name="password"/>
        <s:submit name="addUser" value="Add user"/>
    </s:form>

    <h1>Make buddies</h1>
    <s:form beanclass="<%=Users.class%>">
        User : <s:text name="username"/><br/>
        Buddy : <s:text name="buddy"/><br/>
        <s:hidden name="password"/>
        <s:submit name="makeBuddies" value="Make buddies"/>
    </s:form>

  </body>
</html>