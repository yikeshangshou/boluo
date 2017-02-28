<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<a id="skippy" class="sr-only sr-only-focusable" href="#content"><div class="container">
    <span class="skiplink-text">Skip to main content</span>
  </div></a>
<header class="navbar navbar-static-top bs-docs-nav" id="header" role="banner">
  <div class="container">
    <div class="navbar-header">
      <button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target=".bs-navbar-collapse">
        <span class="sr-only">Toggle navigation</span><span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="/admin">大菠萝内容系统</a>
    </div>
    <div class="collapse navbar-collapse bs-navbar-collapse" id="navbar">
      <ul class="nav navbar-nav">
        <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">资讯审核</a>
          <ul class="dropdown-menu" role="menu">
            <li><a href="/admin/feed?source=Instagram">Instagram资讯</a></li>
            <li><a href="/admin/feed?source=Twitter">Twitter资讯</a></li>
            <li><a href="/admin/feed/user">用户投稿资讯</a></li>
            <li><a href="/admin/feed">所有资讯</a></li>
          </ul></li>
        <li><a href="/admin/item">资讯管理</a></li>
        <li><a href="/admin/article">文章管理</a></li>
        <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">专题管理</a>
          <ul class="dropdown-menu" role="menu">
            <li><a href="/admin/topic?status=1&selected=1">运营的专题</a></li>
            <li><a href="/admin/topic?status=-3">管理员删除专题</a></li>
            <li><a href="/admin/topic">所有专题</a></li>
          </ul></li>
        <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">讨论管理</a>
          <ul class="dropdown-menu" role="menu">
            <li><a href="/admin/discussion?status=1">可见讨论</a></li>
            <li><a href="/admin/discussion?status=-1">用户删除讨论</a></li>
            <li><a href="/admin/discussion?status=-3">管理员删除讨论</a></li>
            <li><a href="/admin/discussion">所有讨论</a></li>
          </ul></li>
        <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">观点管理</a>
          <ul class="dropdown-menu" role="menu">
            <li><a href="/admin/reply?status=1">可见观点</a></li>
            <li><a href="/admin/reply?status=-1">用户删除观点</a></li>
            <li><a href="/admin/reply?status=-3">管理员删除观点</a></li>
            <li><a href="/admin/reply">所有观点</a></li>
          </ul></li>
        <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">用户管理</a>
          <ul class="dropdown-menu" role="menu">
            <li><a href="/admin/user">所有用户</a></li>
            <li><a href="/admin/user?status=1">正常用户</a></li>
            <li><a href="/admin/user?status=-3">封禁用户</a></li>
            <li><a href="/admin/user?bindStatus=1">绑定手机用户</a></li>
          </ul></li>
        <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">工单管理</a>
          <ul class="dropdown-menu" role="menu">
            <li><a href="/admin/ticket">所有工单</a></li>
            <li><a href="/admin/ticket?status=1">未处理工单</a></li>
            <li><a href="/admin/ticket?status=-3">已处理工单</a></li>
          </ul></li>
        <li><a href="/admin/system/settings">系统设置</a></li>
      </ul>
    </div>
  </div>
</header>