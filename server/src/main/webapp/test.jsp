<%@ taglib prefix="s" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title id="page-title">LaDaube</title>

    <script type="text/javascript" src="${pageContext.request.contextPath}/jwplayer/jwplayer.js"></script>
</head>
<body>
<div id="player"></div>
<script type="text/javascript">
        // player init
        jwplayer("player").setup({
            flashplayer: "${pageContext.request.contextPath}/jwplayer/player.swf",
            file: "${pageContext.request.contextPath}/piano.mp3",
            height: 200,
            width: 400,
            players: [
                { type: "html5" },
                { type: "flash", src: "${pageContext.request.contextPath}/jwplayer/player.swf" }
            ]
        });
</script>
</body>
</html>