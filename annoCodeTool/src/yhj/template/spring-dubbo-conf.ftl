<!-- ${date} ${mapProp.editor} ${tabelComments}ibatis 配置 -->
<sqlMap resource="${mapProp.sqlmapPath?replace("\\","/")}/${sqlName}.xml"/>

<!-- ${date} ${mapProp.editor} ${tabelComments}spring Dao层配置 -->
<bean id="dao${sqlName}" class="${daoPath}.impl.Dao${sqlName}Impl">
    <property name="sqlMapClient" ref="${mapProp.sqlMapClient}"/>
    <!-- 按需指定通用数据操作的sqlMap ID:SQLID_INSERT/SQLID_SAVE/SQLID_UPDATE/SQLID_DELETE/SQLID_SELECT -->
    <property name="sqlmapNamespace" value="${mapProp.sqlmap_namespace}"/>
    <property name="SQLID_INSERT" value="insert${sqlName}"/>
    <#--<property name="SQLID_SAVE" value="save${sqlName}"/>-->
    <property name="SQLID_UPDATE" value="update${sqlName}"/>
    <property name="SQLID_DELETE" value="delete${sqlName}"/>
    <property name="SQLID_SELECT" value="select${sqlName}"/>
</bean>

<!-- ${date} ${mapProp.editor} ${tabelComments}spring Bo层配置 -->
<bean id="bo${sqlName}"
      class="${boPath}.impl.Bo${sqlName}Impl">
    <!-- 必须指定通用数据操作对象 -->
    <property name="iDao" ref="dao${sqlName}"/>
</bean>

<!-- ${date} ${mapProp.editor} ${tabelComments}spring service层配置 -->
<bean id="service${sqlName}"
      class="${mapProp.servicePath}.impl.Service${sqlName}Impl">
    <!-- 必须指定通用数据操作对象 -->
    <property name="iBo" ref="bo${sqlName}"/>
</bean>

<!-- ${date} ${mapProp.editor} ${tabelComments}spring service层配置 -->
<bean id="iService${sqlName}" parent="service${sqlName}"/>

<!-- ${date} ${mapProp.editor} ${tabelComments}dubbo 接口服务提供者配置 -->
<dubbo:service interface="${mapProp.servicePath}.IService${sqlName}" ref="service${sqlName}"/>

<!-- ${date} ${mapProp.editor} ${tabelComments}dubbo 接口服务消费者配置 -->
<dubbo:reference id="iService${sqlName}" interface="${mapProp.servicePath}.IService${sqlName}"/>

    <!-- ${date} ${mapProp.editor} ${tabelComments}struts 配置 -->
    <action name="select${sqlName}" class="${mapProp.actionPath}.${sqlName}Action" method="select${sqlName}">
        <result name="success" type="jsonResult"/>
        <param name="logTpl">{bLog:1,LOGMSG:'查询${tabelComments}(${sqlName})记录'}</param>
    </action>
    <action name="select${sqlName}Count" class="${mapProp.actionPath}.${sqlName}Action" method="select${sqlName}Count">
        <result name="success" type="jsonResult"/>
        <param name="logTpl">{bLog:1,LOGMSG:'查询${tabelComments}(${sqlName})记录数'}</param>
    </action>
    <action name="insert${sqlName}" class="${mapProp.actionPath}.${sqlName}Action" method="insert${sqlName}">
        <result name="success" type="jsonResult"/>
        <param name="logTpl">{bLog:1,LOGMSG:'新增${tabelComments}(${sqlName})记录'}</param>
    </action>
    <action name="update${sqlName}" class="${mapProp.actionPath}.${sqlName}Action" method="update${sqlName}">
        <result name="success" type="jsonResult"/>
        <param name="logTpl">{bLog:1,LOGMSG:'修改${tabelComments}(${sqlName})记录'}</param>
    </action>
    <action name="delete${sqlName}" class="${mapProp.actionPath}.${sqlName}Action" method="delete${sqlName}">
        <result name="success" type="jsonResult"/>
        <param name="logTpl">{bLog:1,LOGMSG:'删除${tabelComments}(${sqlName})记录'}</param>
    </action>
  

