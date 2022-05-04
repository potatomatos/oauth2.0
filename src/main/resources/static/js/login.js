$(function () {
    var cards = document.querySelectorAll('.card');

    /* View Controller
                                                      -----------------------------------------*/
    var btns = document.querySelectorAll('.js-btn');

    btns.forEach(function (btn) {
        btn.addEventListener('click', on_btn_click, true);
        btn.addEventListener('touch', on_btn_click, true);
    });

    function on_btn_click(e) {
        var nextID = e.currentTarget.getAttribute('data-target');
        var next = document.getElementById(nextID);
        if (!next) return;
        bg_change(nextID);
        view_change(next);
        return false;
    }

    /* Add class to the body */
    function bg_change(next) {
        document.body.className = '';
        document.body.classList.add('is-' + next);
    }

    /* Add class to a card */
    function view_change(next) {
        cards.forEach(function (card) {
            card.classList.remove('is-show');
        });
        next.classList.add('is-show');
    }

    /**
     * 点击重新获取验证码
     */
    $("#captchaPic").click(function () {
        var time = new Date().getTime();
        $(this).attr('src', "/sys/captcha?time=" + time);
    });

    /**
     * 登录
     */
    $(".login-form").submit(function (ev) {
        $('.login').attr("disabled",true)
            .attr("readOnly",true)
            .css({'pointer-events':'none','cursor': 'not-allowed'})
            .html('<i class="fa fa-spinner fa-spin"></i>');
        $(".err-tips").html('');
        $.ajax({
            url: "/sys/login",
            type:'post',
            data:{
                username:$(".username").val(),
                password:$(".password").val(),
                captcha:$(".captcha").val()
            },
            success: function (res) {
                if (res.code===200){
                    $('.login').html('登录成功');
                    //保存用户信息
                    localStorage.setItem("userInfo",JSON.stringify(res.data));
                    localStorage.setItem("token",res.data.token);
                    setTimeout(function () {
                        location="/#/bookmark";
                    },2000);
                }else {
                    $('.login').attr("disabled",false)
                        .attr("readOnly",false)
                        .removeAttr("style")
                        .html('登录');
                    $(".err-tips").html(res.msg);
                }
            },
            error: function () {
                $(".err-tips").html("网络异常");
            }
        });
        ev.preventDefault();
        return false;
    });
});
