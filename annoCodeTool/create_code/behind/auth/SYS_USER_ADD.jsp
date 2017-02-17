<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
    <title>新增用户表</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<!--
		加载公用组件
	-->
	<%@ include  file="/common/head.jsp"%>
  </head>
  <body>
	   <div id="addBar">
		    <form id="addForm">
		    
		    </form>
	  </div>
	  <div style="display: none;"></div>
  </body>
  <script type="text/javascript"> 
  	var addForm = null;//添加记录的表单
  	$(function (){
    	if(!$.cookie("CUR_USER")){
      			gotoLogin();
      			return;
      	}
    	init();
    });
   //页面初始化
   function init(){
	   addForm = $("#addForm").ligerForm({
   		inputWidth: 170, 
   		labelWidth: 90, 
   		space: 40,
   		validate: true,
   		fields:[
		         {display: '账号', name: 'ACCOUNT', type: "text" , newline: true, validate: { } },
		         {display: '用户名称', name: 'NAME', type: "text" , newline: true, validate: { } },
		         {display: '密码', name: 'PASSWORD', type: "text" , newline: true, validate: { } },
		         {display: '手机号', name: 'TELPHONE', type: "text" , newline: true, validate: { } },
		         {display: '状态', name: 'STATE', type: "text" , newline: true, validate: { } },
		         {display: '创建时间', name: 'MIN_CREATE_DATE', type: "date" , newline: true, validate: { date:true} , options:{showTime :true,format:"yyyy-MM-dd hh:mm:ss",cancelable:true } },
		         {display: '修改时间', name: 'MIN_UPDATE_DATE', type: "date" , newline: true, validate: { date:true} , options:{showTime :true,format:"yyyy-MM-dd hh:mm:ss",cancelable:true } }
   		],
   	   buttons: [{ text: "保存", width: 60, click: submitAddform }]
   	});
   }
   //保存记录
   function submitAddform() {
       if (!addForm.valid()) {
    	   addForm.showInvalid();
            return;
       }
       var formData = addForm.getData();
        $.extend(formData,{bLog:'1',BUSINESS_DETAIL_MSG:'新增用户表记录'});
       
       //新增
       baseTools.xhrAjax({
               url: "<%=basePath%>auth/insertSYS_USER.do",
               params:  formData,
               callback: [function (jsonObj, xhrArgs) {
               	switch (parseInt(jsonObj.code)) {
		                case -1://查询成功
		                 	$.ligerDialog.error('新增用户表记录失败:'+jsonObj.msg);
		                    break;
		                case -3://cookie 失效请重新登录
		                    $.ligerDialog.error(jsonObj.msg);
		                    //去登录
		                    gotoLogin();
		                    break;
		                default:
		                $.ligerDialog.success('恭喜您，新增用户表成功');
		                	window.parent.grid.reload();
		                 frameElement.dialog.close();//关闭对话框
           		}
               }],
               callbackError: [function (data, xhrArgs) {
                   baseTools.hideMash();//关闭遮罩
               }]
           });
   }
  </script>
</html>
