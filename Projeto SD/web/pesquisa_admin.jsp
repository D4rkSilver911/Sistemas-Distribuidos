<%--
  Created by IntelliJ IDEA.
  User: Carlos
  Date: 10/12/2019
  Time: 13:47
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="translate.js"></script>
<html>
<head>
    <title>UcBusca - Pesquisa</title>
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
    <a href="privilegios.jsp">Conceder Privilégios</a>
    <a href="info_sistema.jsp">Informação do Sistema</a>
    <a href="index.jsp">Sair</a>

</div>
<button onclick="detetar()">Ver Lingua</button>
<p id = "lingua"></p>
<button onclick="traduzir()">Traduzir</button>
<h1>Resultados para o termo: <c:out value="${pesquisaBean.termo}" /></h1>
<textarea id = "text" style="width:100%; height:70%; background-color: #f2f2f2; white-space: pre-wrap; resize:none;" disabled translate="yes">
    <c:forTokens items = "${pesquisaBean.menu_pesquisa()}" delims = "|XXX|" var = "name">
        <c:out  value = "${name}"/>
    </c:forTokens>
</textarea>

</body>
</html>
