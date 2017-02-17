<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
    <title>用户表管理</title>
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
            {display: '用户ID', name: 'ID', isSort: true },
            {display: '账号', name: 'ACCOUNT', isSort: true },
            {display: '用户名称', name: 'NAME', isSort: true },
            {display: '密码', name: 'PASSWORD', isSort: true },
            {display: '手机号', name: 'TELPHONE', isSort: true },
            {display: '状态', name: 'STATE', isSort: true },
            {display: '创建时间', name: 'CREATE_DATE', isSort: true },
            {display: '修改时间', name: 'UPDATE_DATE', isSort: true }
            ], 
            url: '<%=basePath%>auth/selectSYS_USER.do',
            parms: {bLog:'1',BUSINESS_DETAIL_MSG:'查询用户表'},
            pageSize: <%=pageSize%>, 
            sortName: 'ID',
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
    		         {display: '用户ID', name: 'ID', type: "text" , newline: false, validate: { } , group: "搜索", groupicon: searchGroupIcon },
    		         {display: '账号', name: 'ACCOUNT', type: "text" , newline: false, validate: { } },
    		         {display: '用户名称', name: 'NAME', type: "text" , newline: false, validate: { } },
    		         {display: '密码', name: 'PASSWORD', type: "text" , newline: false, validate: { } },
    		         {display: '手机号', name: 'TELPHONE', type: "text" , newline: false, validate: { } },
    		         {display: '状态', name: 'STATE', type: "text" , newline: false, validate: { } },
    		         {display: '创建时间_开始', name: 'MIN_CREATE_DATE', type: "date" , newline: false, validate: { } , options:{showTime :true,format:"yyyy-MM-dd hh:mm:ss",cancelable:true } },
    		         {display: '创建时间_结束', name: 'MAX_CREATE_DATE', type: "date" , newline: false, validate: { } , options:{showTime :true,format:"yyyy-MM-dd hh:mm:ss",cancelable:true } },

    		         {display: '修改时间_开始', name: 'MIN_UPDATE_DATE', type: "date" , newline: false, validate: { } , options:{showTime :true,format:"yyyy-MM-dd hh:mm:ss",cancelable:true } },
    		         {display: '修改时间_结束', name: 'MAX_UPDATE_DATE', type: "date" , newline: false, validate: { } , options:{showTime :true,format:"yyyy-MM-dd hh:mm:ss",cancelable:true } }

    		],
    	   buttons: [{ text: "查询", width: 60, click: submitSearchform }]
    	});
    	
    	//查询
    	function submitSearchform() {
            if (!searchForm.valid()) {
            	searchForm.showInvalid();
                 return;
            } 
           var formData = searchForm.getData();
           $.extend(formData,{bLog:'1',BUSINESS_DETAIL_MSG:'查询用户表'});
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
    	        url: '<%=basePath%>behind/auth/SYS_USER_SHOW.jsp',
    			title:'增看用户表记录',
    			height: <%=dialogHeight%>,
    			width: <%=dialogWidth%> ,
    			data:{ID:ids[0]},
    		}); 
        }
    	//增加
    	function add(){
    		$.ligerDialog.open({ 
    	        url: '<%=basePath%>behind/auth/SYS_USER_ADD.jsp',
    			title:'增加用户表记录',
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
                url: "<%=basePath%>auth/updateSYS_USER.do",
                async:false,
                params:  {IDS:ids.join(','),STATE:1,bLog:'1',BUSINESS_DETAIL_MSG:'启用用户表记录'},
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
                url: "<%=basePath%>auth/updateSYS_USER.do",
                async:false,
                params:  {IDS:ids.join(','),STATE:0,bLog:'1',BUSINESS_DETAIL_MSG:'禁用用户表记录'},
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
    	        url: '<%=basePath%>behind/auth/SYS_USER_UPDATE.jsp',
    			title:'修改用户表记录',
    			height: <%=dialogHeight%>,
    			width: <%=dialogWidth%> ,
    			data:{ID:ids[0]},
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
                url: "<%=basePath%>auth/deleteSYS_USER.do",
                async:false,
                params:  {IDS:ids.join(','),bLog:'1',BUSINESS_DETAIL_MSG:'删除用户表记录'},
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
	       		ids.push(rows[i].ID);
	       	}
	       	return ids;
    	}
    }
    
   </script>
</html>
