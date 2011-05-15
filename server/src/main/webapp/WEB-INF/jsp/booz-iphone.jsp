<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ld" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
         "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<c:set var="cp" value="${pageContext.request.contextPath}"/>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>LaDaube</title>
<meta name="viewport" content="width=320; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;"/>
<style type="text/css" media="screen">@import "${cp}/iphone/iphonenav.css";</style>
<script type="application/x-javascript" src="${cp}/iphone/iphonenav.js"></script>
</head>

<body>
    <h1 id="pageTitle"></h1>
    <a id="homeButton" class="button" href="#home">Home</a>
    <a class="button" href="#searchForm">Search</a>

    <ul id="home" title="Home" selected="true">
        <li><a href="#albums">By Album</a></li>
        <li><a href="#artists">By Artist</a></li>
    </ul>
    
    <ul id="albums" title="Sorted by Album">
        <c:forEach var="t" items="${actionBean.userTracksByAlbum}">
            <li>
                <ld:track track="${t}"/>
            </li>
        </c:forEach>
    </ul>

    <div id="searchResults" class="panel" title="Search">
        <h2>Search results go here...</h2>
    </div>
    <form id="searchForm" class="dialog" action="#searchResults">
        <fieldset>
            <h1>Music Search</h1>
            <a class="button toolButton goButton" href="#searchResults">Search</a>
            
            <label>Artist:</label>
            <input type="text"/>
            <label>Song:</label>
            <input type="text"/>
        </fieldset>
    </form>
</body>
</html>
