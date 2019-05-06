<%--
  Created by IntelliJ IDEA.
  User: nss
  Date: 2019/4/30
  Time: 13:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>hellow sample spring</h1>
<form action="/springdemo/api/uploadFile" method="post" enctype="multipart/form-data">
    <input name="myfile" id="myfile" type="file" />
    <input type="submit" value="上传">
</form>
</body>
</html>
