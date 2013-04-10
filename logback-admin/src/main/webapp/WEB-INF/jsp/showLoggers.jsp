<%@ page import="se.svt.logback.admin.domain.LogLevels" %>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="loggers" scope="request" type="se.svt.logback.admin.model.LoggersModel"/>
<c:set var="enumValues" value="<%=LogLevels.getNames()%>"/>
<!DOCTYPE html>
<html>
<head>
    <title>Admin - Logback</title>
    <link rel="stylesheet" type="text/css" href="${loggers.contextPath}/resources/css/style.css"/>
</head>
<body>
<h1>Logging Levels - <c:out value="${loggers.hostName}"/></h1>

<table cellspacing="0" cellpadding="1" class="log4j">
    <c:forEach var="logger" items="${loggers.loggerInfoList}" varStatus="counter">
    <tr id="${counter.count}" class="${fn:toLowerCase(logger.logLevel.logLevelName)}">
        <th>${logger.loggerName}</th>
        <c:forEach var="type" items="${enumValues}">
            <td><div class="${fn:toLowerCase(type.logLevelName)}"><a href="javascript://" onclick="updateLog4J('${logger.loggerName}', '${type.logLevelName}', '#${counter.count}')">${type.logLevelName}</a></div></td>
        </c:forEach>
    </tr>
    </c:forEach>
</table>

<br/>
<br/>
<form action="${loggers.contextPath}${loggers.addLoggerUrl}" id="f" method="POST">
    <input type="text" id="autocomplete" name="logger" size="80"/>
    <select name="level" id="level">
        <c:forEach var="type" items="${enumValues}">
            <c:choose>
                <c:when test="${type.logLevelName == 'Trace'}">
                    <option value="${type.logLevelName}" selected="selected">${type.logLevelName}</option>
                </c:when>
                <c:otherwise>
                    <option value="${type.logLevelName}">${type.logLevelName}</option>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </select>
    <input type="submit" value="save"/>
</form>

<br/><br/>
<form action="${loggers.contextPath}${loggers.reloadUrl}" method="POST">
    <input type="submit" value="Reload from file" />
</form>
<br/><br/>
<a href="${loggers.contextPath}/admin/logbackStatus">Logback status page</a>
<script src="${loggers.contextPath}/resources/js/script.js"></script>
<script src="${loggers.contextPath}/ext/jquery-1.8.2.min.js"></script>
</body>
</html>