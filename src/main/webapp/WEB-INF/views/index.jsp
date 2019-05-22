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
    <script type="text/javascript" src="http://libs.baidu.com/jquery/1.9.1/jquery.js"></script>
    <script type="application/javascript">

        $(function () {
            var button = $('#btn');

            $('#export').click(function () {
                window.location.href = 'http://localhost:8075/springdemo/home/export';
            });

            $('#unport').click(function () {
                window.location.href = 'http://localhost:8075/springdemo/home/unport';
            });


            $('#btn').click(function () {
                //var formdata = new FormData();
                //var fromfile = document.getElementById("upload-form");
                var formData = new FormData($("#upload-form")[0]);
                console.log(formData);
                //alert(fromfile);
                //formdata.append('updateFile',fromfile);
                //alert(formdata);
                $.ajax({
                    url: 'http://172.16.127.188:8088/nss-cloud2-api/api/v2/upload_file',
                    type: 'post',
                    //contentType: "application/json;charset=utf-8",
                    contentType: false,
                    processData: false,//这个很有必要，不然不行
                    //dataType:"json",
                    headers: {
                        "accessToken": 'cc803668-9658-4eef-9c73-db09d5822105'
                    },
                    data: formData,
                    xhrFields: {
                        withCredentials: true
                    },
                    success: function (data, status, req) {

                        alert(req.getResponseHeader('content-type'));
                    },
                    error: function () {

                    }
                });

            });

            //alert("111");
        });
    </script>
</head>
<body>
<h1>hellow sample spring</h1>
<form id="upload-form" enctype="multipart/form-data">
    <input name="updateFile" id="updateFile" type="file"/>
    <input type="button" id="btn" value="上传">
    <input type="button" id="export" value="导出报表"/>
    <input type="button" id="unport" value="导入报表"/>
</form>
</body>
</html>
