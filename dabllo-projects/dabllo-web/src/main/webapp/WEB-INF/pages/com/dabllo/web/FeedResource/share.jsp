<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<title>发布最新的动态｜大菠萝</title>
<div class="container">
  <div class="row row-space-top-6">
    <div class="col-md-8 col-md-offset-2 col-sm-12 col-xs-12">
      <div class="panel panel-default">
        <div class="panel-heading">发布最新的动态</div>
        <div class="panel-body">
          <div class="form-group">
            <div class="row image-container">
              <div class="col-md-3 col-xs-6">
                <a href="#" class="thumbnail"><img data-src="holder.js/147x100?text=添加图片"><input accept="image/gif,image/jpeg,image/jpg,image/png" class="fileupload" name="imageFile"
                  type="file"></a>
              </div>
            </div>
          </div>
          <form id="feed-form" name="feed-form" action="/api/v1/feed" method="POST">
            <div class="form-group">
              <label for="title">动态</label>
              <textarea class="form-control" id="title" name="title" placeholder="请简单介绍一下你分享的链接或图片的内容，或者为什么分享这个链接或图片。" rows="3" tabindex="1"></textarea>
            </div>
            <div class="form-group">
              <label for="link">链接</label><input class="form-control" id="link" name="link" tabindex="2" type="text">
            </div>
            <div class="form-group">
              <input id="topicId" name="topicId" type="hidden" value="${param.topicId}"><input name="image" type="hidden" value="">
              <button class="btn btn-primary" tabindex="3" type="submit">提交</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>
<script src="/libs/jquery/jquery.ui.widget.js"></script>
<script src="/libs/jquery/jquery.iframe-transport.js"></script>
<script src="/libs/jquery/jquery.fileupload.js"></script>
<script src="/libs/holder.min.js"></script>
<script src="/js/feed.js?v=20160527"></script>