<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="track" required="true" type="java.lang.Object" %>
<a href="${pageContext.request.contextPath}/stream/${track._id}">
    <c:set var="name" value="${track.name}"/>
    <c:if test="${name==null}">
        <c:set var="name" value="Unknown"/>
    </c:if>
    <div class="trackName">${name}</div>
    <c:set var="album" value="${track.album}"/>
    <c:if test="${album==null || album==''}">
        <c:set var="album" value="Unknown album"/>
    </c:if>
    <span class="albumName">${album}</span>
    <c:set var="artist" value="${track.artist}"/>
    <c:if test="${artist==null || artist==''}">
        <c:set var="artist" value="Unknown artist"/>
    </c:if>
    /
    <span class="artistName">${artist}</span>
</a>