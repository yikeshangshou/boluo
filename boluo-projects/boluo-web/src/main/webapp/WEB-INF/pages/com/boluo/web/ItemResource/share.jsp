<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<title>资讯投稿｜大菠萝</title>
<div class="container">
  <div class="row row-space-top-6">
    <div class="col-md-8 col-md-offset-2 col-sm-12 col-xs-12">
      <div class="panel panel-default">
        <div class="panel-heading">资讯投稿</div>
        <div class="panel-body">
          <div class="form-group">
            <div class="row image-container">
              <div class="col-md-3 col-xs-6" id="image-finder">
                <a class="thumbnail" href="#"><img data-src="holder.js/147x100?text=添加图片"><input accept="image/gif,image/jpeg,image/jpg,image/png" class="fileupload" name="imageFile"
                  type="file"></a>
              </div>
            </div>
          </div>
          <form action="/api/v1/item" id="item-form" method="POST" name="item-form">
            <div class="form-group">
              <label for="title">标题</label> <input class="form-control" id="title" name="title" tabindex="1" type="text">
            </div>
            <div class="form-group">
              <label for="description">描述</label>
              <textarea class="form-control" id="description" name="description" rows="3" tabindex="2"></textarea>
            </div>
            <div class="form-group">
              <label for="link">链接</label><input class="form-control" id="link" name="link" tabindex="3" type="text">
            </div>
            <div class="form-group">
              <input name="image" type="hidden" value="">
              <button class="btn btn-primary" tabindex="4" type="submit">提交</button>
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
<script src="/js/item.js?v=20160707"></script>