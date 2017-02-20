<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<div class="container">
  <div class="row row-space-10 row-space-top-10">
    <div class="col-md-8 col-md-offset-2 col-sm-12 col-xs-12">
      <div class="panel panel-default">
        <div class="panel-heading">重新设置密码</div>
        <div class="panel-body">
          <form action="/api/v1/password/reset" class="form-horizontal" id="password-form" method="post" name="password-form">
            <div class="form-group">
              <label class="col-sm-3 col-xs-3 control-label" for="phone">手机</label>
              <div class="col-sm-5 col-xs-9">
                <input class="form-control" id="phone" name="phone" tabindex="1" />
              </div>
            </div>
            <div class="form-group">
              <label class="col-sm-3 control-label" for="captchaCode">图片验证码</label>
              <div class="col-sm-3">
                <input class="form-control" id="captchaCode" name="captchaCode" tabindex="2" type="text" />
              </div>
              <div class="col-sm-2">
                <img src="/api/v1/captcha" onclick="this.src='/api/v1/captcha?v='+new Date()*1">
              </div>
            </div>
            <div class="form-group">
              <label class="col-sm-3 col-xs-3 control-label" for="securityCode">手机验证码</label>
              <div class="col-sm-2 col-xs-4">
                <input class="form-control" id="securityCode" name="securityCode" tabindex="3" type="text" />
              </div>
              <div class="col-sm-3 col-xs-5">
                <a class="btn btn-warning btn-send-sms" href="#">获取验证码</a>
              </div>
            </div>
            <div class="form-group">
              <label class="col-sm-3 col-xs-3 control-label" for="password">密码</label>
              <div class="col-sm-5 col-xs-9">
                <input class="form-control" id="password" name="password" tabindex="4" type="password" />
              </div>
            </div>
            <div class="form-group">
              <label class="col-sm-3 col-xs-3 control-label" for="confirmPassword">确认密码</label>
              <div class="col-sm-5 col-xs-9">
                <input class="form-control" id="confirmPassword" name="confirmPassword" tabindex="5" type="password" />
              </div>
            </div>
            <div class="form-group">
              <div class="col-sm-5 col-sm-offset-3  col-xs-6 col-xs-offset-3">
                <button class="btn btn-primary" tabindex="6" type="submit">提交</button>
                <a class="col-space-3" href="/signin">登录</a>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<script src="/libs/jquery/jquery.md5.js?v=20160404"></script>
<script src="/js/password.js?v=20160801"></script>