<!DOCTYPE HTML><%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<title><d:title default="大菠萝-轻松获取热门资讯" /></title>
<link rel="shortcut icon" type="image/png" href="http://wfenxiang.b0.upaiyun.com/static/logo.png!S" />
<link rel="stylesheet" href="/libs/bootstrap/css/bootstrap.min.css?v=20150610" />
<!-- Documentation extras -->
<link rel="stylesheet" href="/libs/bootstrap/docs.min.css?v=20150610">
<!--[if lt IE 9]><script src="/libs/bootstrap/ie8-responsive-file-warning.js?v=20140307"></script><![endif]-->
<script src="/libs/bootstrap/ie-emulation-modes-warning.js"></script>
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
<![endif]-->
<link rel="stylesheet" href="/css/dabllo.css?v=20160818" />
<script src="/libs/jquery/jquery-1.11.3.min.js?v=20150610"></script>
<script src="/libs/jquery/jquery.validate.min.js?v=20140307"></script>
<script src="/libs/jquery/jquery.form.min.js?v=20140307"></script>
<script src="/libs/bootstrap/js/bootstrap.min.js?v=20150610"></script>
<script src="/js/dabllo.js?v=20160404"></script>
<script>
  var _hmt = _hmt || [];
  (function() {
    var hm = document.createElement("script");
    hm.src = "//hm.baidu.com/hm.js?335fcc4a4ebc1159664276e0053977b4";
    var s = document.getElementsByTagName("script")[0];
    s.parentNode.insertBefore(hm, s);
  })();
</script>
<d:head />
</head>
<body>
  <c:if test="${not empty _error}">
    <div id="message" class="alert alert-danger">
      <span class="glyphicon glyphicon-remove-sign" aria-hidden="true"></span>&nbsp;${_error}
    </div>
  </c:if>
  <c:if test="${not empty _msg}">
    <div id="message" class="alert alert-info">
      <span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span>&nbsp;${_msg}
    </div>
  </c:if>
  <c:if test="${not _blank}">
    <d:body />
  </c:if>
</body>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-73297827-2', 'auto');
  ga('send', 'pageview');

</script>
</html>