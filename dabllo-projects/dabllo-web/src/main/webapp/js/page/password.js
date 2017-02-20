var wait = 60;
  function countdown(target) {
    if (wait == 0) {
      target.removeAttr('disabled');
      target.text("获取验证码");
      wait = 60;

    } else {
      target.attr('disabled', 'disabled');
      target.text("重新发送（" + wait + "）");
      wait--;
      setTimeout(function() {
        countdown(target);
      }, 1000)
    }
  }

  function showFormFieldError(target, message) {
    var html = '<label class="error" for="' + target + '" >' + message + '</label>';
    $('#' + target).parent().append(html);
  }

  $(function() {
    $('.btn-send-sms', $('#password-form')).click(function() {
      var $btn = $(this);
      $btn.attr('disabled', 'disabled');
      $('.error', $('#password-form')).remove();

      var phone = $('input[name=phone]', $('#password-form')).val();
      if (phone == null || phone.length != 11) {
        $btn.removeAttr('disabled');
        showFormFieldError('phone', '请输入一个有效的11位手机号。');
        return false;
      }

      $.getJSON('/api/v1/sms/send', {
        phone : phone,
        type : 'reset_password'
      }, function(resp) {
        if (resp && resp.e == 0) {
          $btn.text('验证码已发送！');
          setTimeout(function() {
            countdown($btn);
          }, 200);

        } else {
          $btn.removeAttr('disabled');
          showFormFieldError('phone', resp.m);
        }
      });

      return false;
    });

    $('#password-form').validate({
      rules : {
        phone : {
          required : true
        },
        securityCode : {
          required : true
        },
        password : {
          required : true,
          minlength : 8,
          maxlength : 16
        },
        confirmPassword : {
          required : true,
          equalTo : "#password"
        }
      },
      messages : {
        phone : {
          required : '请输入手机号。'
        },
        securityCode : {
          required : '请输入验证码。'
        },
        password : {
          required : '请输入密码。',
          minlength : $.format('密码长度必须大于{0}位。'),
          maxlength : $.format('密码长度不能超过{0}位。')
        },
        confirmPassword : {
          required : '请重新输入一次密码。',
          equalTo : '两次输入的密码不一致。'
        }
      },
      submitHandler : function(form) {
        if (!$(form).valid()) {
          $('.error').eq(0).focus();
          $('#password').val('');
          $('#confirmPassword').val('');
          return false;
        }

        var md5 = $('#password').val();
        for (var i = 0; i < 3; i++) {
          md5 = $.md5(md5);
        }
        $('#password').val(md5);

        md5 = $('#confirmPassword').val();
        for (var i = 0; i < 3; i++) {
          md5 = $.md5(md5);
        }
        $('#confirmPassword').val(md5);

        $(form).ajaxSubmit({
          success : function(resp) {
            if (resp && resp.e == 0) {
              Message.info('密码重新设置成功。', false, $('.form-group:last', $(form)));
              setTimeout(function() {
                window.location.href = '/page/result_ok';
              }, 3000);

            } else {
              $('#password').val('');
              $('#confirmPassword').val('');
              Message.error('密码重新设置失败：' + resp.m, false, $('.form-group:last', $(form)));
            }
          },
          error : function() {
            $('#password').val('');
            $('#confirmPassword').val('');
            Message.error('密码重新设置失败！', false, $('.form-group:last', $(form)));
          }
        });
      }
    });

  });