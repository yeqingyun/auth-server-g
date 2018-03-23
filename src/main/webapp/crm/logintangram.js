$(function() {  
  $("#TANGRAM__PSP_4__verifyCodeChange").click(function() {  
    var rom = new Date();  
    $("#TANGRAM__PSP_4__verifyCodeImg").attr("src", "captcha?t="+rom);  
  });  
  $("#TANGRAM__PSP_4__form").submit(function() {
	return check();
  });
  $("#TANGRAM__PSP_4__userName").val($("#commitUsername").val().replace(/^CRM_/, ''));
}); 

/*
  function check() {
  if($('table ul')!=null || $('table ul')!="") {
  $('table ul').each(function(){
  $(this).find('li').remove() ;
  });
  }
  if( $('#TANGRAM__PSP_4__userName').val()=="" || $('#TANGRAM__PSP_4__userName').val()==null){
  $('#pwdTip').html("") ;
  $('#validateCodeTip').html("") ;
  $('#loginTip').html('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;登录账号不能为空！') ;
  return false;
  } else if( $('#TANGRAM__PSP_4__password').val()=="" || $('#TANGRAM__PSP_4__password').val()==null){
  $('#loginTip').html("") ;
  $('#validateCodeTip').html("") ;
  $('#pwdTip').html('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;密码不能为空！') ;
  return false;
  } else if( $('#TANGRAM__PSP_4__verifyCode').val()=="" || $('#TANGRAM__PSP_4__verifyCode').val()==null){
  $('#loginTip').html("") ;
  $('#pwdTip').html("") ;
  $('#validateCodeTip').html('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;验证码不能为空！') ;
  return false;   
  } else {
  $('#loginTip').html("") ;
  $('#pwdTip').html("") ;
  $('#validateCodeTip').html("") ;
  return true ;
  }    
  }*/
