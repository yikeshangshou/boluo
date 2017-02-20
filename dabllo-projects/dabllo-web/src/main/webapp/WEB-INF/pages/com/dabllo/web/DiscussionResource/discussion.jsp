<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<meta property="og:type" content="article" />
<meta property="og:url" content="http://www.aqwa.cn/discussion/${discussion.id}" />
<meta property="og:title" content="${discussion.title}" />
<meta property="og:description" content="${discussion.description}" />
<title>${item.title}-${discussion.title}｜大菠萝</title>
<header class="container m-header">
  <div class="row">
    <div class="col-md-6 col-md-offset-3 col-sm-10 col-sm-offset-1 logo">
      <a class="m-download" href="#"><img alt="大菠萝-轻松获取热门资讯" src="http://wfenxiang.b0.upaiyun.com/static/m-header.png"></a>
    </div>
    <div class="col-md-6 col-md-offset-3 col-sm-10 col-sm-offset-1 header-tip" style="display: none;">
      <div class="panel m-panel">
        <div class="m-body">
          <button type="button" class="close">
            <span aria-hidden="true">×</span><span class="sr-only">Close</span>
          </button>
          <p>
            请点击右上角<img src="http://wfenxiang.b0.upaiyun.com/static/icon-dot.png">
          </p>
          <p>选择“在Safari中打开”。</p>
        </div>
      </div>
    </div>
  </div>
</header>

<div class="container">
  <div class="row row-space-top-1">
    <div class="col-md-6 col-md-offset-3 col-sm-10 col-sm-offset-1 col-xs-12">
      <div class="page-header">
        <h3>
          <a href="/item/${item.id}">${item.title}</a>
        </h3>
        <input id="discussionId" type="hidden" value="${discussion.id}">
      </div>
      <div class="content">
        <div>讨论：${discussion.title}</div>
        <p>${discussion.description}</p>
      </div>
    </div>
  </div>

  <div class="row row-space-top-2">
    <div class="col-md-6 col-md-offset-3 col-sm-10 col-sm-offset-1 col-xs-12">
      <div class="panel m-list-container replies">
        <div class="panel-heading m-heading">
          <span class="m-title">讨论观点</span><a class="btn btn-default btn-xs pull-right op-open-reply" href="#">添加观点</a>
        </div>
        <div class="panel m-panel" id="reply-panel" <c:if test="${not empty replies}">style="display: none;"</c:if>>
          <div class="m-body">
            <form action="/api/v1/reply" id="reply-form" method="post" name="reply-form" role="form">
              <div class="form-group">
                <textarea class="form-control" name="content" placeholder="请填写你的观点。" rows="3"></textarea>
              </div>
              <div class="form-group m-actions">
                <input id="reply-discussionId" name="discussionId" type="hidden" value="${discussion.id}">
                <button type="submit" class="btn btn-blue">提交</button>
                <button type="button" class="btn btn-default col-space-2 op-cancel-reply">取消</button>
              </div>
            </form>
          </div>
        </div>
        <c:forEach var="reply" items="${replies}">
          <div class="panel m-panel" data-id="${reply.id}">
            <div class="m-body">
              <div class="main">${reply.content}</div>
              <div class="bottom row-space-top-1">
                <a class="btn btn-blue btn-xs op-up-reply" data-id="${reply.id}" href="#">赞同</a><span class="col-space-2 m-tip reply-up-count-${reply.id}"></span><span
                  class="pull-right m-tip m-actions">${reply.properties.user.username}</span>
              </div>
            </div>
          </div>
        </c:forEach>
        <div class="panel m-panel" id="notification-panel" style="display: none;">
          <div class="m-body"></div>
        </div>
        <div class="panel m-panel m-panel-download">
          <div class="m-body">
            <a class="btn btn-blue m-download" href="#">下载大菠萝查看更多观点</a><a class="col-space-2 icon-share social-share" data-target="weibo"><img alt="分享到新浪微博"
              src="http://wfenxiang.b0.upaiyun.com/static/icons/icon-weibo.png"></a>
          </div>
        </div>
      </div>
    </div>
  </div>

  <c:if test="${not empty discussions}">
    <div class="row row-space-top-2">
      <div class="col-md-6 col-md-offset-3 col-sm-10 col-sm-offset-1 col-xs-12">
        <div class="panel m-list-container discussions">
          <div class="panel-heading m-heading">
            <span class="m-title">这条资讯下的其他讨论</span>
          </div>
          <c:forEach var="discussion" items="${discussions}">
            <a href="/discussion/${discussion.id}">
              <div class="panel m-panel" data-id="${discussion.id}">
                <div class="m-body">
                  <div class="main">${discussion.title}</div>
                  <div class="bottom"></div>
                </div>
              </div>
            </a>
          </c:forEach>
        </div>
      </div>
    </div>
  </c:if>

  <div class="row row-space-top-2">
    <div class="col-md-6 col-md-offset-3 col-sm-10 col-sm-offset-1 col-xs-12">
      <div class="panel m-list-container items">
        <div class="panel-heading m-heading">
          <span class="m-title">热门资讯</span>
        </div>
        <c:forEach var="item" items="${items}">
          <a href="/item/${item.id}">
            <div class="panel m-panel" data-id="${item.id}">
              <div class="m-body">
                <div class="main">${item.title}</div>
                <div class="bottom"></div>
              </div>
            </div>
          </a>
        </c:forEach>
      </div>
    </div>

  </div>

  <div class="row row-space-top-2">
    <div class="col-md-6 col-md-offset-3 col-sm-10 col-sm-offset-1 col-xs-12">
      <div class="panel qr-code">
        <img src="http://wfenxiang.b0.upaiyun.com/static/m-qr-code.png">
      </div>
    </div>
  </div>
</div>

<script src="/js/m/global.js?v=20160811"></script>
<script>
  $(function() {
    var discussionId = $('#discussionId').val();

    $('.op-open-reply').click(function() {
      $('#notification-panel').hide();
      $('#reply-panel').fadeIn(100);
      $('textarea[name=content]', $('#reply-panel')).focus();
      return false;
    });

    $('.op-cancel-reply').click(function() {
      $('#reply-panel').fadeOut(100);
      $('textarea', $('#reply-panel')).val('');
      return false;
    });

    $('.op-up-reply').click(function() {
      var $target = $(this);
      var replyId = $(this).attr("data-id");
      $.getJSON('/api/v1/reply/' + replyId + '/up', {}, function(resp) {
        if (resp && resp.e == 0) {
          var count = resp.r;
          $target.attr('disabled', 'disabled').addClass('disabled').text('已赞同');
          $('.reply-up-count-' + replyId).text('附近有' + count + '人赞同过。');
        }
      });
      return false;
    });

    $('#reply-form').validate({
      rules : {
        content : {
          required : true,
          maxlength : 1000
        }
      },
      messages : {
        content : {
          required : '请输入你的观点。',
          maxlength : $.format('观点内容不能超过{0}个字。')
        }
      },
      submitHandler : function(form) {
        if (!$(form).valid()) {
          $('.error').eq(0).focus();
          return false;
        }

        $(form).ajaxSubmit({
          success : function(resp) {
            if (resp && resp.e == 0) {
              var discussionId = resp.r;
              $('textarea', $(form)).val('');
              $('#reply-panel').hide();

              var notification = '<span>观点提交成功。分享这个页面让朋友来分享他的观点吧。</span>';
              $('.m-body', $('#notification-panel')).html(notification);
              $('#notification-panel').fadeIn(100);

            } else {
              Message.error('提交失败：' + resp.m, true, $('.form-group:last'));
            }
          },
          error : function() {
            Message.error('提交失败，请稍后重试！', true, $('.form-group:last'));
          }
        });
      }
    });

  });
</script>