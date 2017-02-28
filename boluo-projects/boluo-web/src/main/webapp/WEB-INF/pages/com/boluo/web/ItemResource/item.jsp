<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<head>
<meta property="og:type" content="article" />
<meta property="og:url" content="http://www.aqwa.cn/item/${item.id}" />
<meta property="og:title" content="${item.title}" />
<meta property="og:image" content="http://wfenxiang.b0.upaiyun.com/${item.image}" />
<meta property="og:description" content="${item.description}" />
</head>
<title>${item.title}｜大菠萝</title>
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
        <h3>${item.title}</h3>
        <input id="itemId" type="hidden" value="${item.id}">
      </div>
      <div class="content">${item.description}<a href="${item.link}" target="_blank"><span class="glyphicon glyphicon-link" aria-hidden="true"></span>原文</a>
      </div>
    </div>
  </div>

  <div class="row row-space-top-2">
    <div class="col-md-6 col-md-offset-3 col-sm-10 col-sm-offset-1 col-xs-12">
      <div class="panel m-list-container discussions">
        <div class="panel-heading m-heading">
          <span class="m-title">关于这条资讯的讨论</span><a class="btn btn-default btn-xs pull-right op-open-discussion" href="#">发起讨论</a>
        </div>
        <div class="panel m-panel" id="discussion-panel" <c:if test="${not empty discussions}">style="display: none;"</c:if>>
          <div class="m-body">
            <form action="/api/v1/discussion" id="discussion-form" method="post" name="discussion-form" role="form">
              <div class="form-group">
                <textarea class="form-control" name="title" placeholder="关于这条资讯，你想和大家讨论什么？" rows="3"></textarea>
              </div>
              <div class="form-group m-actions">
                <input id="discussion-itemId" name="itemId" type="hidden" value="${item.id}">
                <button type="submit" class="btn btn-blue">提交</button>
                <button type="button" class="btn btn-default col-space-2 op-cancel-discussion">取消</button>
              </div>
            </form>
          </div>
        </div>
        <c:forEach var="discussion" items="${discussions}">
          <a href="/discussion/${discussion.id}">
            <div class="panel m-panel" data-id="${discussion.id}">
              <div class="m-body">
                <div class="main">${discussion.title}</div>
                <div class="bottom row-space-top-1 m-tip m-actions">
                  <span>${discussion.properties.followCount}人关注</span><span class="col-space-2">${discussion.properties.replyCount}个观点</span>
                </div>
              </div>
            </div>
          </a>
        </c:forEach>
        <div class="panel m-panel" id="notification-panel" style="display: none;">
          <div class="m-body"></div>
        </div>
        <div class="panel m-panel m-panel-download">
          <div class="m-body">
            <a class="btn btn-blue m-download" href="#">下载大菠萝查看更多讨论</a><a class="col-space-2 icon-share social-share" data-target="weibo"><img alt="分享到新浪微博"
              src="http://wfenxiang.b0.upaiyun.com/static/icons/icon-weibo.png"></a>
          </div>
        </div>
      </div>
    </div>
  </div>

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
    $('.op-open-discussion').click(function() {
      $('#notification-panel').hide();
      $('#discussion-panel').fadeIn(100);
      $('textarea[name=title]', $('#discussion-panel')).focus();
      return false;
    });

    $('.op-cancel-discussion').click(function() {
      $('#discussion-panel').fadeOut(100);
      $('textarea', $('#discussion-panel')).val('');
      return false;
    });

    $('#discussion-form').validate({
      rules : {
        title : {
          required : true,
          minlength : 5,
          maxlength : 50
        },
        description : {
          maxlength : 400
        }
      },
      messages : {
        title : {
          required : '请输入要讨论的内容。',
          minlength : $.format('讨论内容不能少于{0}个字。'),
          maxlength : $.format('讨论内容不能超过{0}个字。')
        },
        description : {
          maxlength : $.format('讨论描述不能超过{0}个字。')
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
              $('#discussion-panel').hide();

              var notification = '<span>你已经成功发起了讨论，现在进入讨论与朋友分享你的观点吧。</span>';
              notification += '<a class="btn btn-blue" href="/discussion/'+discussionId+'">进入讨论</a>';
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