<%@ page import="com.ladaube.actions.LaDaubeLogin" %>
<%@ taglib prefix="s" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>LaDaube - Please log in</title>
    <link rel="stylesheet" type="text/css" href="js/ext-3.0.3/resources/css/ext-all.css"/>
    <link rel="stylesheet" type="text/css" href="js/ext-3.0.3/resources/css/xtheme-gray.css"/>
    <link rel="stylesheet" type="text/css" href="css/ladaube.css"/>
    <style type="text/css">
        body {
            padding: 0;
            margin: 0;
        }

        td {
            font-family: tahoma,arial,verdana,sans-serif;
            font-size: 8pt;
            padding-bottom: 4px;
            padding-right: 4px;
        }

        .centerContainer {
          text-align: center;
        }

        #loginPanel {
            margin-left: auto;
            margin-right: auto;
            width: 400px;
        }

        #loginPanel table {
            padding-top: 4px;
            padding-bottom: 4px;
            padding-left: 20px;
            padding-right: 20px;
            background-color: #d3d3d3;
        }
    </style>
</head>
<body>
<div id="banner">
    <%@ include file="../logo.jsp"%>
</div>
<div class="spacer"></div>
<s:messages/>
<s:errors/>
<div class="centerContainer">
    <div id="loginPanel">
    <s:form beanclass="<%=LaDaubeLogin.class.getName()%>">
        <s:hidden name="targetUrl"/>
            <table>
                <tbody>
                <tr>
                    <td colspan="2" align="left">
                        <h1>Please log in</h1>
                    </td>
                </tr>
                <tr>
                    <td>
                        username
                    </td>
                    <td>
                        <s:text class="x-form-text x-form-field" name="username"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        password
                    </td>
                    <td>
                        <s:password class="x-form-text x-form-field" name="password"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" align="right">
                        <s:submit name="login" value="Log-in"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </s:form>
    </div>
</div>
</body>
</html>