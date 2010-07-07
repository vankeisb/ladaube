<%@ page import="com.ladaube.actions.UploadTrack" %>
<%@include file="taglibs.jsp"%>
<h1>Upload a track</h1>
<s:messages/>
<s:form beanclass="<%=UploadTrack.class.getName()%>" name="uploadForm">
    <s:errors/>
    <s:file name="data" id="dataInput"/>
    <s:submit name="upload" id="uploadSubmit"/>
</s:form>
