<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<div class="container">
  <div class="row row-space-10 row-space-top-10">
    <div class="col-md-8 col-md-offset-2 col-sm-12 col-xs-12">
      <div class="panel panel-default">
        <div class="panel-heading">从这里登录</div>
        <div class="panel-body">
          <form action="/api/v1/user/signin" class="form-horizontal" id="signin-form" method="post" name="signin-form">
            <div class="form-group">
              <label class="col-sm-3 control-label" for="phone">手机</label>
              <div class="col-sm-4">
                <input class="form-control" id="phone" name="phone" tabindex="1" />
              </div>
            </div>
            <div class="form-group">
              <label class="col-sm-3 control-label" for="password">密码</label>
              <div class="col-sm-4">
                <input class="form-control" id="password" name="password" tabindex="2" type="password" />
              </div>
            </div>
            <div class="form-group">
              <div class="col-sm-6 col-sm-offset-3">
                <input type="hidden" id="to" name="to" value="${param.to}" />
                <button class="btn btn-primary" tabindex="3" type="submit">登录</button>
                <a class="col-space-3" href="/password/reset">忘记密码？</a><a class="col-space-3" href="/signup">新注册用户</a>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<script src="/libs/jquery/jquery.md5.js?v=20160404"></script>
<script src="/js/user.js?v=20160510"></script>