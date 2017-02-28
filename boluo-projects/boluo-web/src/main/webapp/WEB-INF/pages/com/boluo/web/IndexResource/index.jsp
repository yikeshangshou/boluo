<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<div id="carousel-dabllo" class="carousel slide" data-ride="carousel">
  <!-- Indicators -->
  <ol class="carousel-indicators">
    <li data-target="#carousel-dabllo" data-slide-to="0" class="active"></li>
    <li data-target="#carousel-dabllo" data-slide-to="1"></li>
    <li data-target="#carousel-dabllo" data-slide-to="2"></li>
    <li data-target="#carousel-dabllo" data-slide-to="3"></li>
    <li data-target="#carousel-dabllo" data-slide-to="4"></li>
  </ol>
  <!-- Wrapper for slides -->
  <div class="carousel-inner" role="listbox">
    <div class="item item-blue active">
      <div class="row">
        <div class="col-md-4 col-md-offset-2 col-sm-5 col-sm-offset-1 col-xs-8">
          <img alt="easy-get" src="http://wfenxiang.b0.upaiyun.com/static/v2/easy-get.jpg!L">
        </div>
        <div class="col-md-2 col-sm-2 col-xs-3 carousel-content">
          <a href="https://itunes.apple.com/cn/app/da-bo-luo-fen-xiang-ni-guan/id1130904670?mt=8" target="_blank"><img src="http://wfenxiang.b0.upaiyun.com/static/download-ios.png!M"></a><img
            class="row-space-top-1" src="http://wfenxiang.b0.upaiyun.com/static/qr-code.png!M">
        </div>
      </div>
    </div>
    <div class="item item-green">
      <div class="row">
        <div class="col-md-4 col-md-offset-2 col-sm-5 col-sm-offset-1 col-xs-8">
          <img alt="quick-preview" src="http://wfenxiang.b0.upaiyun.com/static/v2/quick-preview.jpg!L">
        </div>
        <div class="col-md-2 col-sm-2 col-xs-3 carousel-content">
          <a href="https://itunes.apple.com/cn/app/da-bo-luo-fen-xiang-ni-guan/id1130904670?mt=8" target="_blank"><img src="http://wfenxiang.b0.upaiyun.com/static/download-ios.png!M"></a><img
            class="row-space-top-1" src="http://wfenxiang.b0.upaiyun.com/static/qr-code.png!M">
        </div>
      </div>
    </div>
    <div class="item item-yellow">
      <div class="row">
        <div class="col-md-4 col-md-offset-2 col-sm-5 col-sm-offset-1 col-xs-8">
          <img alt="social-share" src="http://wfenxiang.b0.upaiyun.com/static/v2/social-share.jpg!L">
        </div>
        <div class="col-md-2 col-sm-2 col-xs-3 carousel-content">
          <a href="https://itunes.apple.com/cn/app/da-bo-luo-fen-xiang-ni-guan/id1130904670?mt=8" target="_blank"><img src="http://wfenxiang.b0.upaiyun.com/static/download-ios.png!M"></a><img
            class="row-space-top-1" src="http://wfenxiang.b0.upaiyun.com/static/qr-code.png">
        </div>
      </div>
    </div>
    <div class="item item-red">
      <div class="row">
        <div class="col-md-4 col-md-offset-2 col-sm-5 col-sm-offset-1 col-xs-8">
          <img alt="one-day-review" src="http://wfenxiang.b0.upaiyun.com/static/v2/one-day-review.jpg!L">
        </div>
        <div class="col-md-2 col-sm-2 col-xs-3 carousel-content">
          <a href="https://itunes.apple.com/cn/app/da-bo-luo-fen-xiang-ni-guan/id1130904670?mt=8" target="_blank"><img src="http://wfenxiang.b0.upaiyun.com/static/download-ios.png!M"></a><img
            class="row-space-top-1" src="http://wfenxiang.b0.upaiyun.com/static/qr-code.png">
        </div>
      </div>
    </div>
    <div class="item item-orange">
      <div class="row">
        <div class="col-md-4 col-md-offset-2 col-sm-5 col-sm-offset-1 col-xs-8">
          <img alt="time-section" src="http://wfenxiang.b0.upaiyun.com/static/v2/time-section.jpg!L">
        </div>
        <div class="col-md-2 col-sm-2 col-xs-3 carousel-content">
          <a href="https://itunes.apple.com/cn/app/da-bo-luo-fen-xiang-ni-guan/id1130904670?mt=8" target="_blank"><img src="http://wfenxiang.b0.upaiyun.com/static/download-ios.png!M"></a><img
            class="row-space-top-1" src="http://wfenxiang.b0.upaiyun.com/static/qr-code.png">
        </div>
      </div>
    </div>
  </div>
  <!-- Controls -->
  <a class="left carousel-control" href="#carousel-dabllo" data-slide="prev"><span class="glyphicon glyphicon-chevron-left"></span></a><a class="right carousel-control" href="#carousel-dabllo"
    data-slide="next"><span class="glyphicon glyphicon-chevron-right"></span></a>
</div>
<script>
  $(function() {
    $('.carousel').carousel({
      interval : 5000
    })
  });
</script>