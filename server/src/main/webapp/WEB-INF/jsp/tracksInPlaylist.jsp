<%@include file="taglibs.jsp"%>
<h1>Tracks in playlist ${actionBean.playlist.name}, for user : ${actionBean.user.id}</h1>
<ul>
    <c:forEach items="${actionBean.tracksInPlaylist}" var="track">
        <li>${track.name}</li>
    </c:forEach>
</ul>