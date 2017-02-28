<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<title>重置密码｜大菠萝</title>
<div class="container">
  <div class="row">
    <div class="col-md-8 col-md-offset-2 col-sm-12 col-xs-12 row-space-top-4">
      <div class="page-header">
        <h4>重置密码</h4>
      </div>
    </div>
  </div>
  <div class="row row-space-8">
    <div class="col-md-8 col-md-offset-2 col-sm-12 col-xs-12">
      <form action="/api/v1/password/reset" class="form-horizontal" id="password-form" method="post" name="password-form">
        <div class="form-group">
          <label class="col-sm-3 col-xs-3 control-label" for="phone">手机</label>
          <div class="col-sm-5 col-xs-9">
            <input class="form-control" id="phone" name="phone" tabindex="1" />
          </div>
        </div>
        <div class="form-group">
          <label class="col-sm-3 col-xs-3 control-label" for="securityCode">验证码</label>
          <div class="col-sm-2 col-xs-4">
            <input class="form-control" id="securityCode" name="securityCode" tabindex="2" type="text" />
          </div>
          <div class="col-sm-3 col-xs-5">
            <a class="btn btn-warning btn-send-sms" href="#">获取验证码</a>
          </div>
        </div>
        <div class="form-group">
          <label class="col-sm-3 col-xs-3 control-label" for="password">密码</label>
          <div class="col-sm-5 col-xs-9">
            <input class="form-control" id="password" name="password" tabindex="3" type="password" />
          </div>
        </div>
        <div class="form-group">
          <label class="col-sm-3 col-xs-3 control-label" for="confirmPassword">确认密码</label>
          <div class="col-sm-5 col-xs-9">
            <input class="form-control" id="confirmPassword" name="confirmPassword" tabindex="4" type="password" />
          </div>
        </div>
        <div class="form-group">
          <div class="col-sm-5 col-sm-offset-3 col-xs-9 col-xs-offset-3">
            <button class="btn btn-primary" tabindex="5" type="submit">提交</button>
          </div>
        </div>
      </form>
    </div>
  </div>
</div>

<script src="/libs/jquery/jquery.md5.js?v=20160404"></script>
<script src="/js/page/password.js?v=20160702"></script>