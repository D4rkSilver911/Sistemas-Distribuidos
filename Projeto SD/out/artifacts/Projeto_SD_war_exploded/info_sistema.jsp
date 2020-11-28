<%--
  Created by IntelliJ IDEA.
  User: Carlos
  Date: 07/12/2019
  Time: 21:20
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<html>
<head>
    <title>UcBusca - Informação do Sistema</title>
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
    <a href="ver_perfil.jsp">Meu Perfil</a>
    <a href="anexa_url.jsp">Anexar URL</a>
    <a href="privilegios.jsp">Conceder Privilégios</a>

    <s:form action="logout" method="post">
        <s:submit value = "Sair"/>
    </s:form>

</div>

<h1 style="margin-left:32%; font-family: Helvetica,sans-serif; font-size:48px;">Informação do Sistema</h1>
<h1 style="margin-left:30%; margin-top: -50px">______________________________________</h1>
<div class = "container">
    <form>
        <c:forTokens items = "${pesquisaBean.system_info()}" delims="|XXX|" var = "name">
        <c:out value = "${name}"/><p>
        </c:forTokens>


    </form>
</div>




<button id = "voltar" onclick="location.href='menu_admin.jsp'"> Voltar </button>



</body>
</html>
