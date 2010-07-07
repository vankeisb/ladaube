<%@ page import="com.ladaube.actions.TrackImage" %>
<%@include file="taglibs.jsp"%>
<s:messages/>
<s:form beanclass="<%=TrackImage.class.getName()%>" name="uploadForm">
    <s:errors/>
    <s:select name="track">
        <s:options-collection collection="${actionBean.userTracks}" value="id" label="name"/>
    </s:select>
    <s:file name="data" id="dataInput"/>
    <s:submit name="upload" id="uploadSubmit"/>
</s:form>
