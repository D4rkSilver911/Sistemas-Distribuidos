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
    <title>UcBusca - Iniciar Sessão</title>
    <link href="CSS/topmenu.css" rel="stylesheet">
</head>
<body>
    <div class="topnav">
        <a class="active" href="index.jsp">Home</a>
        <a href="registo.jsp">Registo</a>
    </div>

    <h1 style="margin-left:40%; font-family: Helvetica,sans-serif; font-size:48px;">Iniciar Sessão</h1>
    <h1 style="margin-left:38%; margin-top: -50px">_______________________</h1>


    <s:form  style="margin-left:38%; " action="login" method="post">
        <s:textfield name="username" placeholder = "Username"/>
        <s:textfield type = "password" name="password"  placeholder = "Password"/>
        <s:submit value = "Login"/>
        <s:submit onclick="form.action='callback_login'" value = "Login com o Facebook"/>

    </s:form>




</body>
</html>
