<%@include file="taglibs.jsp"%>
<h1>Playlists for user : ${actionBean.user.id}</h1>
<ul>
    <c:forEach items="${actionBean.playlists}" var="pl">
        <li>${pl.id} - ${pl.name}</li>
    </c:forEach>
</ul>