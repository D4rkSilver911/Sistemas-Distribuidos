<%--
  Created by IntelliJ IDEA.
  User: Carlos
  Date: 07/12/2019
  Time: 21:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>UcBusca - Anexar URL</title>
    <link href="CSS/anexar_url.css" rel="stylesheet">
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
    <a class="active" href="menu_admin.jsp">Home</a>
    <a href="ver_perfil_admin.jsp">Meu Perfil</a>
    <a href="privilegios.jsp">Conceder Privilégios</a>
    <a href="info_sistema.jsp">Informação do Sistema</a>
    <s:form action="logout" method="post">
        <s:submit value = "Sair"/>
    </s:form>

</div>

<h1 style="margin-left:41%; font-family: Helvetica,sans-serif; font-size:48px;">Anexar URL</h1>
<h1 style="margin-left:38%; margin-top: -50px">_______________________</h1>
<div class = "container">

        <s:form action="anexar" method="post">
            <s:textfield name="website" id = "pesquisa"/>
            <s:submit  id = "search_word" value = "Anexar URL"/>

        </s:form>



</div>




<button id = "voltar" onclick="location.href='menu_admin.jsp'"> Voltar </button>



</body>
</html>
