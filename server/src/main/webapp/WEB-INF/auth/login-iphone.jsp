<%@ page import="com.ladaube.actions.LaDaubeLogin" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ld" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
         "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<c:set var="cp" value="${pageContext.request.contextPath}"/>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>iDaube - please log in</title>
<meta name="viewport" content="width=320; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;"/>
<style type="text/css" media="screen">

body {
    margin: 0;
    font-family: Helvetica;
    background: #FFFFFF;
    color: #000000;
    overflow: hidden;
    -webkit-text-size-adjust: none;
}

body > h1 {
    box-sizing: border-box;
    margin: 0;
    padding: 10px;
    line-height: 20px;
    font-size: 20px;
    font-weight: bold;
    text-align: center;
    text-shadow: rgba(0, 0, 0, 0.6) 0px -1px 0;
    text-overflow: ellipsis;
    color: #FFFFFF;
    background: url(${cp}/iphone/iPhoneToolbar.png) #6d84a2 repeat-x;
    border-bottom: 1px solid #2d3642;
}

</style>
</head>
<body>
    <h1 id="pageTitle">iDaube - please log in</h1>
    <stripes:form beanclass="<%=LaDaubeLogin.class.getName()%>">
        <table width="100%">
            <tr>
                <td colspan="2">
                    <img src="${cp}/images/ladaube-neon.png" alt="logo"/>
                </td>
            </tr>
            <tr>
                <td>
                    Username
                </td>
                <td>
                    <input type="text" name="username"/>
                </td>
            </tr>
            <tr>
                <td>
                    Password
                </td>
                <td>
                    <input type="password" name="password"/>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="submit" name="login" value="Log-in"/>
                </td>
            </tr>
        </table>
    </stripes:form>
</body>
</html>
