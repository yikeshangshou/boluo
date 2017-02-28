<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<div class="container row-space-top-2">
  <ol class="breadcrumb">
    <li>动态草稿</li>
    <li><a href="/admin/feed_draft">所有</a></li>
    <li><a href="/admin/feed_draft?source=36Kr">36Kr</a></li>
    <li><a href="/admin/feed_draft?source=36KrFlash">36KrFlash</a></li>
    <li><a href="/admin/feed_draft?source=Sina">Sina</a></li>
    <li><a href="/admin/feed_draft?source=Geekpark">Geekpark</a></li>
  </ol>
  <div class="row row-space-top-4">
    <c:forEach var="item" items="${feeds}">
      <div class="col-md-4 col-sm-6 col-xs-6 feed_draft" id="feed_draft_${item.id}">
        <div class="panel panel-default" data-id="${item.id}" data-title="${item.title}" data-description="${item.description}">
          <div class="panel-heading">
            <a href="/admin/feed_draft?source=${item.source}"><strong>${item.source}</strong></a>
            <jsp:useBean id="createDate" class="java.util.Date" />
            <jsp:setProperty name="createDate" property="time" value="${item.createTime}" />
            <fmt:formatDate value="${createDate}" pattern="MM-dd HH:mm" />
            <span class="pull-right"><a href="${item.link}" target="_blank" title="查看原文"><span class="glyphicon glyphicon-link" aria-hidden="true">原文</span></a></span>
          </div>
          <div class="panel-body">
            <div class="feed-box">
              <div class="cover">
                <c:choose>
                  <c:when test="${not empty item.image}">
                    <img src="http://wfenxiang.b0.upaiyun.com/${item.image}!M">
                  </c:when>
                  <c:otherwise>
                    <img data-src="holder.js/160x80">
                  </c:otherwise>
                </c:choose>
              </div>
              <div class="title">${item.title}</div>
            </div>
          </div>
          <div class="panel-footer">
            <a class="col-space-1" data-toggle="modal" data-target="#PublishModal" href="#" title="投递到主题"><span class="glyphicon glyphicon-check" aria-hidden="true">投递</span></a><a
              class="col-space-1 pull-right op-delete" data-id="${item.id}" href="#" title="删除"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a>
          </div>
        </div>
      </div>
    </c:forEach>
  </div>
  <div class="row">
    <div class="col-md-12">
      <ul class="pagination">
        <li><a href="/admin/feed_draft?page=1">首页</a></li>
        <c:forEach var="page" items="${requestScope.pages}">
          <c:choose>
            <c:when test="${page eq currentPage}">
              <li class="active"><a href="#">${page}</a></li>
            </c:when>
            <c:otherwise>
              <li><a href="/admin/feed_draft?page=${page}">${page}</a></li>
            </c:otherwise>
          </c:choose>
        </c:forEach>
        <li><a href="/admin/feed_draft?page=${lastPage}">尾页</a></li>
      </ul>
    </div>
  </div>
</div>

<div class="modal fade" id="PublishModal" tabindex="-1" role="dialog" aria-labelledby="PublishModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title" id="PublishModalLabel">投递到主题</h4>
      </div>
      <form action="/admin/feed_draft/publish" class="form-horizontal" id="publish-form" name="publish-form" method="post" role="form">
        <div class="modal-body">
          <div class="form-group">
            <label class="col-md-2 control-label" for="categoryId">主题</label>
            <div class="col-md-4">
              <select class="form-control" id="categoryId" name="categoryId">
                <option value="-1">请选择类别</option>
                <c:forEach var="category" items="${categories}">
                  <option value="${category.id}">${category.name}</option>
                </c:forEach>
                <option value="0">其他</option>
              </select>
            </div>
            <div class="col-md-4">
              <select class="form-control" id="topicId" name="topicId">
              </select>
            </div>
          </div>
          <div class="form-group">
            <label class="col-md-2 control-label" for="title">动态</label>
            <div class="col-md-8">
              <textarea class="form-control" id="title" name="title" rows="5"></textarea>
            </div>
          </div>
          <div class="form-group">
            <label class="col-md-2 control-label" for="description">参考信息</label>
            <div class="col-md-8">
              <textarea class="form-control" id="description" rows="6"></textarea>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <input id="feedDraftId" name="feedDraftId" type="hidden">
          <button type="submit" class="btn btn-success">提交</button>
          <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script src="/libs/holder.min.js"></script>
<script>
  $(function() {
    $('#PublishModal').on('show.bs.modal', function(e) {
      var $this = $(e.relatedTarget);
      var $feedDraft = $this.parent().parent();

      $('#feedDraftId').val($feedDraft.attr('data-id'));
      $('#title').val($feedDraft.attr('data-title'));
      $('#description').val($feedDraft.attr('data-description'));
    });

    $('#categoryId').change(function() {
      $('#topicId').html('');

      var categoryId = $(this).val();
      $.getJSON('/api/v1/topic', {
        categoryId : categoryId
      }, function(resp) {
        if (resp && resp.e == 0) {
          var topics = resp.r;
          for (var i = 0; i < topics.length; i++) {
            var topic = topics[i];
            $('<option value="' + topic.id + '">' + topic.title + '</option>').appendTo('#topicId');
          }
        }
      });
    });

    $('.op-delete').click(function() {
      var id = $(this).attr('data-id');

      if (confirm('你确定删除吗？')) {
        $.getJSON('/admin/feed_draft/' + id + '/delete', {}, function(resp) {
          if (resp && resp.e == 0) {
            $('#feed_draft_' + id).remove();

          } else {
            Message.error('删除失败！', true);
          }
        });
      }

      return false;
    });

    $('#publish-form').validate({
      rules : {
        topicId : {
          required : true
        },
        feedDraftId : {
          required : true
        },
        title : {
          required : true,
          maxlength : 200
        }
      },
      messages : {
        topicId : {
          required : "请选择Topic。"
        },
        feedDraftId : {
          required : "请选择投递的Feed。"
        },
        title : {
          required : "请输入标题。",
          maxlength : $.format('标题不能超过{0}个字符。')
        }
      },
      submitHandler : function(form) {
        if (!$(form).valid()) {
          $('.error').eq(0).focus();
          return false;
        }

        var id = $('#feedDraftId').val();
        $(form).ajaxSubmit({
          success : function(resp) {
            if (resp && resp.e == 0) {
              Message.info('投递成功。', true, $('.form-group:last'));
              setTimeout(function() {
                $('#PublishModal').modal('hide');
                $('#feed_draft_' + id).remove();
              }, 1500);

            } else {
              Message.error('投递失败：' + resp.m, false, $('.form-group:last'));
            }
          },
          error : function() {
            Message.error('投递失败！', false, $('.form-group:last'));
          }
        });
      }
    });
  });
</script>