<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<title>新建一个主题｜大菠萝</title>
<div class="container">
  <div class="row row-space-top-6">
    <div class="col-md-8 col-md-offset-2 col-sm-12 col-xs-12">
      <div class="panel panel-default">
        <div class="panel-heading">新建一个主题</div>
        <div class="panel-body">
          <div class="form-group">
            <div class="row image-container">
              <div class="col-md-3 col-xs-6">
                <a href="#" class="thumbnail"><img data-src="holder.js/147x100?text=添加图片"><input accept="image/gif,image/jpeg,image/jpg,image/png" class="fileupload" name="imageFile"
                  type="file"></a>
              </div>
            </div>
          </div>
          <form action="/api/v1/topic" class="row-space-top-1" id="topic-form" method="POST" name="topic-form">
            <div class="form-group">
              <label for="title">主题名称</label><input class="form-control" id="title" name="title" type="text">
            </div>
            <div class="form-group">
              <label for="title">描述</label>
              <textarea class="form-control" id="title" name="title" placeholder="简要描述一下你要创建的主题。" rows="3" tabindex="2"></textarea>
            </div>
            <div class="form-group">
              <input name="image" type="hidden" value="">
              <button class="btn btn-primary" type="submit" tabindex="3">提交</button>
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
<script src="/js/topic.js?v=20160316"></script>