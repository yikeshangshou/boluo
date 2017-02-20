<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<a id="skippy" class="sr-only sr-only-focusable" href="#content"><div class="container">
    <span class="skiplink-text">Skip to main content</span>
  </div></a>
<header class="navbar navbar-static-top bs-docs-nav" id="header" role="banner">
  <div class="rainbow">
    <div class="rainbow-item rainbow-1"></div>
    <div class="rainbow-item rainbow-2"></div>
    <div class="rainbow-item rainbow-3"></div>
    <div class="rainbow-item rainbow-4"></div>
  </div>
  <div class="container">
    <div class="row">
      <div class="col-md-12">
        <div class="navbar-header">
          <button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target=".bs-navbar-collapse">
            <span class="sr-only">Toggle navigation</span><span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="/">大菠萝</a>
        </div>
        <nav class="collapse navbar-collapse bs-navbar-collapse">
          <ul class="nav navbar-nav">
            <li><a href="/section"><span class="glyphicon glyphicon-globe" aria-hidden="true"></span><span>发现</span></a></li>
          </ul>
          <ul class="nav navbar-nav navbar-right">
            <c:choose>
              <c:when test="${not empty _user}">
                <li><a href="/item/share"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span><span>资讯投稿</span></a></li>
                <li><a href="#"><c:choose>
                      <c:when test="${not empty _user.username}">${_user.username}</c:when>
                      <c:otherwise>${_user.phone}</c:otherwise>
                    </c:choose></a></li>
                <!-- <li><a href="/message"><span class="glyphicon glyphicon-envelope" aria-hidden="true"></span><span>消息</span></a></li> -->
                <li><a href="/signout"><span class="glyphicon glyphicon-log-out" aria-hidden="true"></span><span>退出</span></a></li>
              </c:when>
              <c:otherwise>
                <li><a href="/signin"><span class="glyphicon glyphicon-log-in" aria-hidden="true"></span><span>登录</span></a></li>
                <li><a href="/signup"><span class="glyphicon glyphicon-user" aria-hidden="true"></span><span>注册</span></a></li>
              </c:otherwise>
            </c:choose>
          </ul>
        </nav>
      </div>
    </div>
  </div>
</header>
<script>
  $(function() {
    var path = window.location.pathname;
    $('header a[href="' + path + '"]').parent().addClass('active');
  });
</script>