
<!-- ${date} ${mapProp.editor} ${tabelComments}dubbo 接口服务提供者配置 -->
<dubbo:service interface="${mapProp.servicePath}.IService${sqlName}" ref="service${sqlName}"/>

<!-- ${date} ${mapProp.editor} ${tabelComments}dubbo 接口服务消费者配置 -->
<dubbo:reference id="service${sqlName}" interface="${mapProp.servicePath}.IService${sqlName}"/>


