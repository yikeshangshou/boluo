$(function() {
  // 审核未通过功能时，准备表单数据。
  $('#RemoveModal').on('show.bs.modal', function(e) {
    var $this = $(e.relatedTarget);
    var $feed = $this.parent().parent();

    $('#remove-feedId').val($feed.attr('data-id'));
    $('#remove-title').val($feed.attr('data-title'));
  });

  // 审核通过并投递功能时，准备表单数据。
  $('#PublishModal').on('show.bs.modal', function(e) {
    var $this = $(e.relatedTarget);
    var $feed = $this.parent().parent().parent();

    $('#publish-feedId').val($feed.attr('data-id'));
    $('#publish-title').val($feed.attr('data-title'));
    $('#publish-description').val($feed.attr('data-description'));
    $('#publish-link').val($feed.attr('data-link'));
    $('#publish-image').val($feed.attr('data-image'));
    $('#publish-imageUrl').val('');
    var imageUrl = $feed.attr('data-imageUrl');
    $('.image-container img').attr('src', imageUrl).attr('style', '');
  });

  // 查看链接。
  $('.op-view-page').click(function() {
    var link = $('#publish-link').val();
    window.open(link, '_blank');

    return false;
  });

  // 下载图片处理。
  $('.op-get-image').click(function() {
    var imageUrl = $('#publish-imageUrl').val();
    if (imageUrl == null || imageUrl == '') {
      Message.error('图片链接不可以为空！', false, $('.form-group:last', $('#PublishModal')));
      $('#image-imageUrl').focus();
      return false;
    }

    $.getJSON('/api/v1/image', {
      imageUrl : imageUrl
    }, function(resp) {
      if (resp && resp.e == 0) {
        var image = resp.r;
        var path = image.path;
        var url = image.url;

        $('#publish-image').val(path);
        $('.image-container img').attr('src', url).attr('style', '');
        Message.info('图片获取完成，请检查图片是否适合使用。', false, $('.form-group:last', $('#PublishModal')));

      } else {
        Message.error('图片获取失败！', false, $('.form-group:last', $('#PublishModal')));
      }
    });

    return false;
  });

  $('#publish-form').validate({
    rules : {
      feedId : {
        required : true
      },
      title : {
        required : true,
        minlength : 5,
        maxlength : 32
      },
      description : {
        maxlength : 400
      },
      link : {
        required : true
      },
      image : {
        required : true
      }
    },
    messages : {
      feedId : {
        required : '请选择投递的资讯。'
      },
      title : {
        required : '请输入标题。',
        minlength : $.format('标题不能少于{0}个字。'),
        maxlength : $.format('标题不能超过{0}个字。')
      },
      description : {
        maxlength : $.format('描述不能超过{0}个字。')
      },
      link : {
        required : '请输入链接。'
      },
      image : {
        required : '请输入图片链接。'
      }
    },
    submitHandler : function(form) {
      if (!$(form).valid()) {
        $('.error').eq(0).focus();
        return false;
      }

      var id = $('#publish-feedId').val();
      $(form).ajaxSubmit({
        success : function(resp) {
          if (resp && resp.e == 0) {
            Message.info('投递成功。', true, $('.form-group:last', $('#publish-form')));
            setTimeout(function() {
              $('#PublishModal').modal('hide');
              $('#feed_' + id).remove();
            }, 1500);

          } else {
            Message.error('投递失败：' + resp.m, false, $('.form-group:last', $('#publish-form')));
          }
        },
        error : function() {
          Message.error('投递失败！', false, $('.form-group:last', $('#publish-form')));
        }
      });
    }
  });

  $('#remove-form').validate({
    rules : {
      feedId : {
        required : true
      }
    },
    messages : {
      feedId : {
        required : '请重新点击要删除的Feed。'
      }
    },
    submitHandler : function(form) {
      if (!$(form).valid()) {
        $('.error').eq(0).focus();
        return false;
      }

      var id = $('#remove-feedId').val();
      $(form).ajaxSubmit({
        success : function(resp) {
          if (resp && resp.e == 0) {
            Message.info('删除成功。', true, $('.form-group:last', $('#remove-form')));
            setTimeout(function() {
              $('#RemoveModal').modal('hide');
              $('#feed_' + id).remove();
            }, 1500);

          } else {
            Message.error('删除失败：' + resp.m, false, $('.form-group:last', $('#remove-form')));
          }
        },
        error : function() {
          Message.error('删除失败！', false, $('.form-group:last', $('#remove-form')));
        }
      });
    }
  });

});