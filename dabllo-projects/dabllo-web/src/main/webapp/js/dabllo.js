// Message.
var Message = Message
    || {
      info : function(msg, auto_close, $target) {
        if ($('#message').length > 0) {
          $('#message').remove();
        }

        if (!$target) {
          $target = $('#header');
        }

        $target
            .after('<div id="message" style="display: none;"><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span><span>'
                + msg + '</span></div>');
        $('#message').addClass('alert').addClass('alert-info').slideDown(200);
        if (auto_close) {
          setTimeout(function() {
            $('#message').slideUp(200);
          }, 3000);
        }
      },
      error : function(msg, auto_close, $target) {
        if ($('#message').length > 0) {
          $('#message').remove();
        }

        if (!$target) {
          $target = $('#header');
        }

        $target
            .after('<div id="message" style="display: none;"><span class="glyphicon glyphicon-remove-sign" aria-hidden="true"></span><span>'
                + msg + '</span></div>');
        $('#message').addClass('alert').addClass('alert-danger').slideDown(200);
        if (auto_close) {
          setTimeout(function() {
            $('#message').slideUp(200);
          }, 3000);
        }
      }
    };