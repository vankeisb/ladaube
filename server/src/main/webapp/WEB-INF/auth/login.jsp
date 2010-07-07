<%@ page import="com.ladaube.actions.LaDaubeLogin" %>
<%@ taglib prefix="s" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>LaDaube - Please log in</title>
    <link rel="stylesheet" type="text/css" href="js/ext-3.0.3/resources/css/ext-all.css"/>
    <link rel="stylesheet" type="text/css" href="js/ext-3.0.3/resources/css/xtheme-gray.css"/>
    <link rel="stylesheet" type="text/css" href="css/ladaube.css"/>
    <script type="text/javascript" src="js/ext-3.0.3/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="js/ext-3.0.3/ext-all-debug.js"></script>
    <script type="text/javascript" src="js/ext-3.0.3/examples.js"></script>

    <script type="text/javascript">

        Ext.BLANK_IMAGE_URL = 'js/ext-3.0.3/resources/images/default/s.gif';
        Ext.onReady(function() {

            var loginPanel = new Ext.Panel({
                title: 'Authentication required',
                contentEl: 'loginPanelContent',
                renderTo: 'loginPanel',
                width: '250px',                
                frame: true
            });
            
        });
    </script>
    <style type="text/css">
        body {
            padding: 0;
            margin: 0;
        }

        .x-panel-body td {
            font-family: tahoma,arial,verdana,sans-serif;
            font-size: 8pt;
            padding-bottom: 4px;
            padding-right: 4px;
        }

        #loginPanel {
            margin-left: 40px;
        }
    </style>
</head>
<body>
<div id="banner">
    <%@ include file="../logo.jsp"%>
</div>
<s:messages/>
<s:errors/>
<s:form beanclass="<%=LaDaubeLogin.class.getName()%>">
    <s:hidden name="targetUrl"/>
    <div id="loginPanel" class="x-panel-body"></div>
    <div id="loginPanelContent" class="x-hidden">
        <table style="width: 100%;">
            <tbody>
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
    </div>
</s:form>

</body>
</html>