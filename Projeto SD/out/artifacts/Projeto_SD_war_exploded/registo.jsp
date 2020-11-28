<%--
  Created by IntelliJ IDEA.
  User: Carlos
  Date: 07/12/2019
  Time: 21:20
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>UcBusca - Registo</title>
    <link href="CSS/topmenu.css" rel="stylesheet">
</head>
<body>
<div class="topnav">
    <a class="active" href="index.jsp">Home</a>
    <a href="login.jsp">Iniciar Sessão</a>
</div>

<h1 style="margin-left:44.5%; font-family: Helvetica,sans-serif; font-size:48px;">Registo</h1>
<h1 style="margin-left:38%; margin-top: -50px">_______________________</h1>


<s:form  style="margin-left:38%; " action="registar" method="post">
    <s:textfield name="nome_registo" placeholder = "Nome"/>
    <s:textfield name="username_registo" placeholder = "Username"/>
    <s:textfield type = "password" name="password_registo"  placeholder = "Password"/>
    <s:submit onclick="form.action='callback'" value = "Registar pelo Facebook"/>
    <s:submit value = "Registar"/>

</s:form>



</body>
</html>
