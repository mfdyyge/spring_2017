<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
    <title>查看用户表记录</title>
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
	   <div id="showBar">
		    <form id="showForm">
		    
		    </form>
	  </div>
	  <div style="display: none;"></div>
  </body>
  <script type="text/javascript"> 
  	var showForm = null;//查看记录的表单
  	$(function (){
    	if(!$.cookie("CUR_USER")){
      			gotoLogin();
      			return;
      	}
    	init();
    });
   //页面初始化
   function init(){
	   showForm = $("#showForm").ligerForm({
   		inputWidth: 170, 
   		labelWidth: 90, 
   		space: 40,
   		readonly: true,
   		fields:[
		         {display: '用户ID', name: 'ID', type: "text" , newline: true, validate: { } },
		         {display: '账号', name: 'ACCOUNT', type: "text" , newline: true, validate: { } },
		         {display: '用户名称', name: 'NAME', type: "text" , newline: true, validate: { } },
		         {display: '密码', name: 'PASSWORD', type: "text" , newline: true, validate: { } },
		         {display: '手机号', name: 'TELPHONE', type: "text" , newline: true, validate: { } },
		         {display: '状态', name: 'STATE', type: "text" , newline: true, validate: { } },
		         {display: '创建时间', name: 'CREATE_DATE', type: "date" , newline: true, validate: { date:true} , options:{showTime :true,format:"yyyy-MM-dd hh:mm:ss",cancelable:true } },

		         {display: '修改时间', name: 'UPDATE_DATE', type: "date" , newline: true, validate: { date:true} , options:{showTime :true,format:"yyyy-MM-dd hh:mm:ss",cancelable:true } }

   		]
   	 });
	  //查询出来数据
	  var id = frameElement.dialog.get('data').ID;//获取data参数
	   
	  baseTools.xhrAjax({
          url: "<%=basePath%>auth/selectSYS_USER.do",
          async:false,
          params:  {ID:id},
          callback: [function (jsonObj, xhrArgs) {
          	switch (parseInt(jsonObj.code)) {
	                case 0://查询成功
	                	showForm.setData(jsonObj.Rows[0]);
	                    break;
	                case -1://保存出错返回标志
	                    $.ligerDialog.error(jsonObj.msg);
	                    break;
	                case -3://cookie 失效请重新登录
	                    $.ligerDialog.error(jsonObj.msg);
	                    //去登录
	                    gotoLogin();
	                    break;
	                default:
      		}
          }],
          callbackError: [function (data, xhrArgs) {
              baseTools.hideMash();//关闭遮罩
          }]
      });     
	  
   }
  </script>
</html>
