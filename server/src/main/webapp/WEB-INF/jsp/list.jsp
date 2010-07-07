<%@include file="taglibs.jsp"%>
<h1>Track list for user : ${actionBean.user.id}</h1>
<ul>
    <c:forEach items="${actionBean.tracks}" var="track">
        <li>${track.name}</li>
    </c:forEach>
</ul>