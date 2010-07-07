<%@include file="taglibs.jsp"%>
<h1>Buddy list for user : ${actionBean.user.id}</h1>
<ul>
    <c:forEach items="${actionBean.buddies}" var="buddy">
        <li>${buddy.id}</li>
    </c:forEach>
</ul>