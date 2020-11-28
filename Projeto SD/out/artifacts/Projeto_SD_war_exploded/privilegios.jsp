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
    <title>UcBusca - Privilégios</title>
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
    <a href="anexa_url.jsp">Anexar URL</a>
    <a href="info_sistema.jsp">Informação do Sistema</a>
    <s:form action="logout" method="post">
        <s:submit value = "Sair"/>
    </s:form>

</div>

<h1 style="margin-left:36%; font-family: Helvetica,sans-serif; font-size:48px;">Conceder Privilégios</h1>
<h1 style="margin-left:35%; margin-top: -50px">____________________________________</h1>
<div class = "container">
    <s:form action="privilegio" method="post">
        <s:textfield name="username_privilegios" id = "pesquisa"/>
        <s:submit  id = "search_word" value = "Insira Username"/>

    </s:form>
</div>




<button id = "voltar" onclick="location.href='menu_admin.jsp'"> Voltar </button>



</body>
</html>
