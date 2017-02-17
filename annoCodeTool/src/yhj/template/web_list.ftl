<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
    <title>${tabelComments}管理</title>
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
	  <div id="searchbar">
		    <form id="searchForm">
		    
		    </form>
	  </div>
	  <div style="display: none;"></div>
      <div id="grid"></div>
      <div style="display: none;"></div>
  </body>
  <script type="text/javascript"> 
    var grid = null;//表格grid
    var searchForm = null;//搜索表单
    $(function (){
    	if(!$.cookie("CUR_USER")){
      			gotoLogin();
      			return;
      	}
    	init();
    });
    
    //页面初始化
    function init(){
    	//定义表格属性
    	grid = $("#grid").ligerGrid({
            columns: [ 
            <#list tableCols as col>
            {display: '${col.fieldComments}', name: '${col.fieldName}', isSort: true }<#if col_index+1 != tableCols?size>,</#if>
            </#list>
            ], 
            url: '<%=basePath%>${mapProp.webActionNamespace}/select${sqlName}.do',
            parms: {bLog:'1',BUSINESS_DETAIL_MSG:'查询${tabelComments}'},
            pageSize: <%=pageSize%>, 
            sortName: '${mapPriKey?keys[0]}',
            width: 'auto', 
            height: 'auto', 
            checkbox: true,
            rownumbers:true,
            fixedCellHeight:false,
            toolbar: { 
            	items: [
						   { text: '查看', click: show, icon: 'search' },
						   { line: true },
                           { text: '增加', click: add, icon: 'add' },
                           { line: true },
                           { text: '启用', click: enable, icon: 'unlock' },
                           { line: true },
                           { text: '禁用', click: disable, icon: 'lock' },
                           { line: true },
                           { text: '修改', click: update, icon: 'modify' },
                           { line: true },
                           { text: '删除', click: del, icon: 'delete' }
                       ]
           }
        });
    	var searchGroupIcon = "<%=basePath%>js/ligerui/skins/icons/search_group.gif";
    	searchForm = $("#searchForm").ligerForm({
    		inputWidth: 170, 
    		labelWidth: 90, 
    		width: 'auto', 
            height: 'auto', 
    		space: 40,
    		validate: true,
    		fields:[
    		        <#list tableCols as col>
    		         <#if col_index+1 ==1>
    		         {display: '${col.fieldComments}', name: '${col.fieldName}', type: "text" , newline: false, validate: { } , group: "搜索", groupicon: searchGroupIcon }<#if col_index+1 != tableCols?size>,</#if>
    		         <#else>
    		         <#assign fieldSqlType='${col.fieldSqlType}'>
    		         <#if fieldSqlType=="DATE">
    		         {display: '${col.fieldComments}_开始', name: 'MIN_${col.fieldName}', type: "date" , newline: false, validate: { } , options:{showTime :true,format:"yyyy-MM-dd hh:mm:ss",cancelable:true } },
    		         {display: '${col.fieldComments}_结束', name: 'MAX_${col.fieldName}', type: "date" , newline: false, validate: { } , options:{showTime :true,format:"yyyy-MM-dd hh:mm:ss",cancelable:true } }<#if col_index+1 != tableCols?size>,</#if>
    		         <#else>
    		         {display: '${col.fieldComments}', name: '${col.fieldName}', type: "text" , newline: false, validate: { } }<#if col_index+1 != tableCols?size>,</#if></#if>
    		         </#if>
                    </#list>	
    		],
    	   buttons: [{ text: "查询", width: 60, click: submitSearchform }]
    	});
    }
	//查询
	function submitSearchform() {
        if (!searchForm.valid()) {
        	searchForm.showInvalid();
             return;
        } 
       var formData = searchForm.getData();
       $.extend(formData,{bLog:'1',BUSINESS_DETAIL_MSG:'查询${tabelComments}'});
       grid.set({ parms :formData});  
       grid.reload(); 
    }
	//查看
	function show() {
		var ids = getSelectIds();
		if(ids.length!=1){
       	 $.ligerDialog.error('请选择一条记录进行查看');
       	 return;
       	}
		$.ligerDialog.open({ 
	        url: '<%=basePath%>${mapProp.webUrlPath}/${sqlName}_SHOW.jsp',
			title:'增看${tabelComments}记录',
			height: <%=dialogHeight%>,
			width: <%=dialogWidth%> ,
			data:{${mapPriKey?keys[0]}:ids[0]},
		}); 
    }
	//增加
	function add(){
		$.ligerDialog.open({ 
	        url: '<%=basePath%>${mapProp.webUrlPath}/${sqlName}_ADD.jsp',
			title:'增加${tabelComments}记录',
			height: <%=dialogHeight%>,
			width: <%=dialogWidth%> 
		});
	}
	//启用
	function enable(){
		var ids = getSelectIds();
		if(ids.length==0){
       	 $.ligerDialog.error('至少选择一条记录');
       	 return;
       	}
		baseTools.xhrAjax({
            url: "<%=basePath%>${mapProp.webActionNamespace}/update${sqlName}.do",
            async:false,
            params:  {IDS:ids.join(','),STATE:1,bLog:'1',BUSINESS_DETAIL_MSG:'启用${tabelComments}记录'},
            callback: [function (jsonObj, xhrArgs) {
            	switch (parseInt(jsonObj.code)) {
	                case -1://保存出错返回标志
	                    $.ligerDialog.error(jsonObj.msg);
	                    break;
	                case -3://cookie 失效请重新登录
	                    $.ligerDialog.error(jsonObj.msg);
	                    //去登录
	                    gotoLogin();
	                    break;
	                default:
	                 //修改成功
	                 $.ligerDialog.success('启用成功');
	                 grid.reload(); 
        		}
            }],
            callbackError: [function (data, xhrArgs) {
                baseTools.hideMash();//隐藏遮罩
            }]
        });
	}
	//禁用
	function disable(){
		var ids = getSelectIds();
		if(ids.length==0){
       	 $.ligerDialog.error('至少选择一条记录');
       	 return;
       	}
		baseTools.xhrAjax({
            url: "<%=basePath%>${mapProp.webActionNamespace}/update${sqlName}.do",
            async:false,
            params:  {IDS:ids.join(','),STATE:0,bLog:'1',BUSINESS_DETAIL_MSG:'禁用${tabelComments}记录'},
            callback: [function (jsonObj, xhrArgs) {
            	switch (parseInt(jsonObj.code)) {
	                case -1://保存出错返回标志
	                    $.ligerDialog.error(jsonObj.msg);
	                    break;
	                case -3://cookie 失效请重新登录
	                    $.ligerDialog.error(jsonObj.msg);
	                    //去登录
	                    gotoLogin();
	                    break;
	                default:
	                 //修改成功
	                 $.ligerDialog.success('禁用成功');
	                 grid.reload(); 
        		}
            }],
            callbackError: [function (data, xhrArgs) {
                baseTools.hideMash();//隐藏遮罩
            }]
        });
	}
	//修改
	function update(){
		var ids = getSelectIds();
		if(ids.length!=1){
       	 $.ligerDialog.error('请选择一条记录进行修改');
       	 return;
       	}
		$.ligerDialog.open({ 
	        url: '<%=basePath%>${mapProp.webUrlPath}/${sqlName}_UPDATE.jsp',
			title:'修改${tabelComments}记录',
			height: <%=dialogHeight%>,
			width: <%=dialogWidth%> ,
			data:{${mapPriKey?keys[0]}:ids[0]},
		});
	}
	//删除
	function del(){
		var ids = getSelectIds();
		if(ids.length==0){
       	 $.ligerDialog.error('至少选择一条记录');
       	 return;
       	}
		baseTools.xhrAjax({
            url: "<%=basePath%>${mapProp.webActionNamespace}/delete${sqlName}.do",
            async:false,
            params:  {IDS:ids.join(','),bLog:'1',BUSINESS_DETAIL_MSG:'删除${tabelComments}记录'},
            callback: [function (jsonObj, xhrArgs) {
            	switch (parseInt(jsonObj.code)) {
	                case -1://保存出错返回标志
	                    $.ligerDialog.error('删除失败：'+jsonObj.msg);
	                    break;
	                case -3://cookie 失效请重新登录
	                    $.ligerDialog.error(jsonObj.msg);
	                    //去登录
	                    gotoLogin();
	                    break;
	                default:
	                 //修改成功
	                 $.ligerDialog.success('删除成功');
	                 grid.reload(); //刷新表格
        		}
            }],
            callbackError: [function (data, xhrArgs) {
                baseTools.hideMash();//隐藏遮罩
            }]
        });
	}
	//获取选择的主键集合
	function getSelectIds(){
		var ids = new Array();
       	var rows = grid.getSelecteds() ;
       	for(var i=0;i<rows.length;i++){
       		ids.push(rows[i].${mapPriKey?keys[0]});
       	}
       	return ids;
	}

   </script>
</html>
