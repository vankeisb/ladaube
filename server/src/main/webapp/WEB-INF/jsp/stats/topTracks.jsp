<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<table class="stats" border="0" cellpadding="0" cellspacing="0">
    <thead>
    <tr>
        <th class="x-list-header">Ranking</th>
        <th class="x-list-header">Downloads</th>
        <th class="x-list-header">Name</th>
        <th class="x-list-header">Artist</th>
        <th class="x-list-header">Album</th>
    </tr>
    </thead>
    <tbody>
        <c:forEach var="tc" items="${actionBean.tracksAndCounts}">
            <tr>
                <td>${tc.index}</td>
                <td>${tc.count}</td>
                <td><a onclick="ladaube.doFilter('<l:jsEscape str="${tc.track.name}"/>');">${tc.track.name}</a></td>
                <td><a onclick="ladaube.doFilter('<l:jsEscape str="${tc.track.artist}"/>');">${tc.track.artist}</a></td>
                <td><a onclick="ladaube.doFilter('<l:jsEscape str="${tc.track.album}"/>');">${tc.track.album}</a></td>
            </tr>
        </c:forEach>
    </tbody>
</table>
