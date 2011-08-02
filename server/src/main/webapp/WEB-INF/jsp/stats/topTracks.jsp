<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<table>
    <thead>
    <tr>
        <th class="x-list-header">Ranking</th>
        <th>Downloads</th>
        <th>Name</th>
        <th>Artist</th>
        <th>Album</th>
    </tr>
    </thead>
    <tbody>
        <c:forEach var="tc" items="${actionBean.tracksAndCounts}">
            <tr>
                <td>${tc.index}</td>
                <td>${tc.count}</td>
                <td>${tc.track.name}</td>
                <td>${tc.track.artist}</td>
                <td>${tc.track.album}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>
