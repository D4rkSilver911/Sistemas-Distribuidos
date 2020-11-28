<%--
  Created by IntelliJ IDEA.
  User: Carlos
  Date: 10/12/2019
  Time: 13:42
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>UcBusca - Pesquisa Url</title>

    <link href="CSS/topmenu.css" rel="stylesheet">
</head>
<body>
<div class="topnav">
    <a class="active" href="index.jsp">Home</a>
    <a href="login.jsp">Iniciar Sessão</a>
    <a href="registo.jsp">Registo</a>
</div>

<h1>Resultados para o Link: <c:out value="${pesquisaBean.termo}" /></h1>
<c:forTokens items = "${pesquisaBean.menu_pesquisa_url()}" delims = "|XXX|" var = "name">
<c:out value = "${name}"/><p>
    </c:forTokens>




</body>
</html>
