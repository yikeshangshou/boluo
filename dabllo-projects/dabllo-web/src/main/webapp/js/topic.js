$(function() {
  $('.fileupload').fileupload({
    url : '/api/v1/image',
    dataType : 'json',
    add : function(e, data) {
      data.submit();
    },
    done : function(e, data) {
      var resp = data.result;
      if (resp && resp.e == 0) {
        var image = resp.r;
        var path = image.path;
        var url = image.url;

        $('input[name=image]', $('#topic-form')).val(path);
        $('.image-container img').attr('src', url).attr('style', '');
      }
    },
    progressall : function(e, data) {
      // update progress.
      var progress = parseInt(data.loaded / data.total * 100, 10);
      $('#progress .progress-bar').css('width', progress + '%');
    }
  }).prop('disabled', !$.support.fileInput).parent().addClass($.support.fileInput ? undefined : 'disabled');

  $('#topic-form').validate({
    rules : {
      title : {
        required : true,
        maxlength : 100
      },
      description : {
        maxlength : 200
      }
    },
    messages : {
      title : {
        required : '请输入标题。',
        maxlength : $.format('标题不能超过{0}个字符。')
      },
      description : {
        maxlength : $.format('描述不能超过{0}个字符。')
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
            Message.info('提交成功！', true, $('.form-group:last'));
            $('input,textarea', $(form)).val('');

            setTimeout(function() {
              window.location.href = '/topic/' + resp.r;
            }, 1500);

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