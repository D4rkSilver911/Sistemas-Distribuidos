<%--
  Created by IntelliJ IDEA.
  User: Carlos
  Date: 07/12/2019
  Time: 19:00
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>UcBusca</title>
    <link href="CSS/index.css" rel="stylesheet">
      <link href="CSS/topmenu.css" rel="stylesheet">
  </head>
  <body>
    <div class="topnav">
      <a class="active" href="#home">Home</a>
      <a href="login.jsp">Iniciar Sessão</a>
      <a href="registo.jsp">Registo</a>
    </div>

    <div style="padding-left:315px; padding-top: 50px;">
      <img src="Assets/logo.png">
    </div>

    <s:form action="pesquisa_word" method="post">
      <s:textfield name="pesquisaBean.termo" id = "pesquisa"/>
      <s:submit  id = "search_word" value = "Procurar palavra(s)"/>
    </s:form>




  </body>
</html>
