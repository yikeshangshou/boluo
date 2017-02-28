$(function() {
  // 删除。
  $('.op-delete').click(function() {
    var id = $(this).attr('data-id');
    var value = -3;

    if (confirm('确定删除？')) {
      $.getJSON('/admin/topic/' + id + '/update_status', {
        value : value
      }, function(resp) {
        if (resp && resp.e == 0) {
          $('#topic_' + id).remove();

        } else {
          Message.error('删除失败！', true);
        }
      });
    }

    return false;
  });

  $('.switch-hot').click(function() {
    var id = $(this).attr('data-id');
    var $icon = $('span', $(this));

    if (confirm('确定修改热议状态？')) {
      var value = 0;
      if ($icon.hasClass('glyphicon-volume-off')) {
        value = 1;
      } else if ($icon.hasClass('glyphicon-volume-up')) {
        value = 0;
      }

      $.getJSON('/admin/item/' + id + '/fire', {
        value : value
      }, function(resp) {
        if (resp && resp.e == 0) {
          if (resp.r == 1) {
            $icon.removeClass('glyphicon-volume-off').addClass('glyphicon-volume-up');

          } else {
            $icon.removeClass('glyphicon-volume-up').addClass('glyphicon-volume-off');
          }

        } else {
          Message.error('标记失败！', true);
        }
      });
    }

    return false;
  });

  $('.switch-latest').click(function() {
    var id = $(this).attr('data-id');
    var $icon = $('span', $(this));

    if (confirm('确定修改最新状态？')) {
      var value = 0;
      if ($icon.hasClass('glyphicon-ice-lolly-tasted')) {
        value = 1;
      } else if ($icon.hasClass('glyphicon-ice-lolly')) {
        value = 0;
      }

      $.getJSON('/admin/item/' + id + '/fire', {
        value : value
      }, function(resp) {
        if (resp && resp.e == 0) {
          if (resp.r == 1) {
            $icon.removeClass('glyphicon-ice-lolly').addClass('glyphicon-ice-lolly-tasted');

          } else {
            $icon.removeClass('glyphicon-ice-lolly-tasted').addClass('glyphicon-ice-lolly');
          }

        } else {
          Message.error('标记失败！', true);
        }
      });
    }

    return false;
  });

});

$(function() {
  // 编辑专题。
  $('#EditModal').on('show.bs.modal', function(e) {
    var $this = $(e.relatedTarget);
    var $topic = $this.parent().parent().parent();

    $('#edit-topicId').val($topic.attr('data-id'));
    $('#edit-title').val($topic.attr('data-title'));
    $('#edit-description').val($topic.attr('data-description'));
    $('#edit-link').val($topic.attr('data-link'));
    $('#edit-image').val($topic.attr('data-image'));
    if ($topic.attr('data-imageUrl')) {
      $('.image-container img').attr('src', $topic.attr('data-imageUrl')).attr('style', '');
    }
  });

  $('#EditModal').on('hidden.bs.modal', function(e) {
    $('input,textarea', $('#EditModal')).val('');
  });

  // 查看链接。
  $('.op-view-page').click(function() {
    var link = $('#edit-link').val();
    window.open(link, '_blank');

    return false;
  });

  // 下载图片处理。
  $('.op-get-image').click(function() {
    var imageUrl = $('#edit-imageUrl').val();
    if (imageUrl == null || imageUrl == '') {
      Message.error('图片链接不可以为空！', false, $('.form-group:last', $('#EditModal')));
      $('#edit-imageUrl').focus();
      return false;
    }

    $.getJSON('/api/v1/image', {
      imageUrl : imageUrl
    }, function(resp) {
      if (resp && resp.e == 0) {
        var image = resp.r;
        var path = image.path;
        var url = image.url;

        $('#edit-image').val(path);
        $('.image-container img').attr('src', url).attr('style', '');
        Message.info('图片获取完成，请检查图片是否适合使用。', false, $('.form-group:last', $('#EditModal')));

      } else {
        Message.error('图片获取失败！', false, $('.form-group:last', $('#EditModal')));
      }
    });

    return false;
  });

  $('#edit-form').validate({
    rules : {
      title : {
        required : true,
        minlength : 2,
        maxlength : 32
      },
      description : {
        maxlength : 400
      },
      image : {
        required : true
      }
    },
    messages : {
      title : {
        required : '请输入标题。',
        minlength : $.format('标题不能少于{0}个字。'),
        maxlength : $.format('标题不能超过{0}个字。')
      },
      description : {
        maxlength : $.format('描述不能超过{0}个字。')
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

      var id = $('#edit-topicId').val();
      $(form).ajaxSubmit({
        success : function(resp) {
          if (resp && resp.e == 0) {
            var item = resp.r;
            var $panel = $('.panel', $('#topic_' + id));
            if (item.title) {
              $panel.attr('data-title', item.title);
              $('.title', $panel).text(item.title);
            }
            if (item.description) {
              $panel.attr('data-description', item.description);
            }
            if (item.link) {
              $panel.attr('data-link', item.link);
            }
            if (item.image) {
              $panel.attr('data-image', item.image);
              $('.cover img', $panel).attr('src', item.imageUrl).attr('style', '');
            }

            Message.info('更新成功！', true, $('.form-group:last', $('#edit-form')));
            setTimeout(function() {
              $('#EditModal').modal('hide');
            }, 1500);

          } else {
            Message.error('更新失败：' + resp.m, false, $('.form-group:last', $('#edit-form')));
          }
        },
        error : function() {
          Message.error('更新失败！', false, $('.form-group:last', $('#edit-form')));
        }
      });
    }
  });

});