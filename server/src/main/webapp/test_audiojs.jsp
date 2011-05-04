<%@ taglib prefix="s" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title id="page-title">LaDaube</title>

    <script src="${pageContext.request.contextPath}/js/audiojs/audiojs/audio.min.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/audiojs/includes/index.css" media="screen">

    <script>
      audiojs.events.ready(function() {
        audiojs.createAll();
      });
    </script>

</head>
<body>
<audio src="${pageContext.request.contextPath}/piano.mp3" preload="auto"></audio>
</body>
</html>