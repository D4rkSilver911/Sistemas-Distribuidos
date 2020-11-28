<%--
  Created by IntelliJ IDEA.
  User: Carlos
  Date: 07/12/2019
  Time: 19:00
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>UcBusca</title>
    <link href="CSS/index.css" rel="stylesheet">
    <link href="CSS/topmenu.css" rel="stylesheet">
</head>
<body>
<c:choose>
    <c:when test="${session.logged== true}">
    </c:when>
    <c:otherwise>
        <c:redirect url="/index.jsp"/>
    </c:otherwise>
</c:choose>
<div class="topnav">
    <a class="active" href="menu_user.jsp">Home</a>
    <a href="ver_perfil.jsp">Meu Perfil</a>
    <s:form action="logout" method="post">
        <s:submit value = "Sair"/>
    </s:form>
</div>

<div style="padding-left:315px; padding-top: 50px;">
    <img src="Assets/logo.png">
</div>
<s:form action="pesquisa_word_user" method="post">
    <s:textfield name="pesquisaBean.termo" id = "pesquisa"/>
    <s:submit  id = "search_word" value = "Procurar palavra(s)"/>
    <s:submit  id = "search_word" onclick="form.action='pesquisa_url_user';" value = "Procurar Link"/>

</s:form>

</body>
</html>
