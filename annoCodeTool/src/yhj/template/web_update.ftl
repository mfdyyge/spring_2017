<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
    <title>修改${tabelComments}记录</title>
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
	   <div id="updateBar">
		    <form id="updateForm">
		    
		    </form>
	  </div>
	  <div style="display: none;"></div>
  </body>
  <script type="text/javascript"> 
  	var updateForm = null;//修改记录的表单
  	$(function (){
    	if(!$.cookie("CUR_USER")){
      			gotoLogin();
      			return;
      	}
    	init();
    });
   //页面初始化
   function init(){
	   updateForm = $("#updateForm").ligerForm({
   		inputWidth: 170, 
   		labelWidth: 90, 
   		space: 40,
   		validate: true,
   		fields:[
   			  <#list tableCols as col>
   			  <#list mapPriKey?keys as colKey>
   				<#if '${colKey}'!='${col.fieldName}'>
   				 <#assign fieldSqlType='${col.fieldSqlType}'>
		         <#if fieldSqlType=="DATE">
		         {display: '${col.fieldComments}', name: '${col.fieldName}', type: "date" , newline: true, validate: { date:true} , options:{showTime :true,format:"yyyy-MM-dd hh:mm:ss",cancelable:true } }<#if col_index+1 != tableCols?size>,</#if>
		         <#else>
		         {display: '${col.fieldComments}', name: '${col.fieldName}', type: "text" , newline: true, validate: { } }<#if col_index+1 != tableCols?size>,</#if>
		         </#if>
		         <#else>
		         {display: '${col.fieldComments}', name: '${col.fieldName}', type: "text" , newline: true, readonly:true }<#if col_index+1 != tableCols?size>,</#if>
   				</#if>
   			   </#list>
              </#list>
   		],
   		buttons: [{ text: "保存", width: 60, click: submitUpdateform }]
   	 });
	  //查询出来数据
	  var id = frameElement.dialog.get('data').${mapPriKey?keys[0]};//获取data参数 通知ID 
	  baseTools.xhrAjax({
          url: "<%=basePath%>${mapProp.webActionNamespace}/select${sqlName}.do",
          async:false,
          params:  {${mapPriKey?keys[0]}:id},
          callback: [function (jsonObj, xhrArgs) {
          	switch (parseInt(jsonObj.code)) {
	                case 0://查询成功
	                	var formData = jsonObj.Rows[0];
	                	<#list tableCols as col>
   			   			<#assign fieldSqlType='${col.fieldSqlType}'>
   			   			<#if fieldSqlType=="DATE">
   			   			$.extend(formData,{${col.fieldName}:new Date(formData.${col.fieldName})});
		                </#if>
              			</#list>
 	                	updateForm.setData(formData);
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
   //保存记录
   function submitUpdateform() {
       if (!updateForm.valid()) {
    	   updateForm.showInvalid();
            return;
       }
       var formData = updateForm.getData();
        $.extend(formData,{bLog:"1",BUSINESS_DETAIL_MSG:'修改${tabelComments}记录'});
       
       //新增
       baseTools.xhrAjax({
               url: "<%=basePath%>${mapProp.webActionNamespace}/update${sqlName}.do",
               params:  formData,
               callback: [function (jsonObj, xhrArgs) {
               	switch (parseInt(jsonObj.code)) {
		                case -1://查询成功
		                 	$.ligerDialog.error('修改${tabelComments}记录失败:'+jsonObj.msg);
		                    break;
		                case -3://cookie 失效请重新登录
		                    $.ligerDialog.error(jsonObj.msg);
		                    //去登录
		                    gotoLogin();
		                    break;
		                default:
		                $.ligerDialog.success('恭喜您，修改${tabelComments}记录成功');
		                window.parent.grid.reload();
		                frameElement.dialog.close();//关闭对话框
           		}
               }],
               callbackError: [function (data, xhrArgs) {
                   baseTools.hideMash();//隐藏遮罩
               }]
           });
   }
  </script>
</html>
