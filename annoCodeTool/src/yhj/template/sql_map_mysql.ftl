<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE sqlMap PUBLIC "-//ibatis.apache.org//DTD SQL Map 2.0//EN"
        "http://ibatis.apache.org/dtd/sql-map-2.dtd">
<sqlMap namespace="${mapProp.sqlmap_namespace}">
    <!--
    创建日期:${date}
    描述:${tabelComments}对应的sqlmap
    作者:<a href="${mapProp.author}">${mapProp.editor}</a>
    版本:1.0
    备注:
    -->
    <cacheModel id="cache${sqlName}" type="LRU">
        <flushInterval hours="24"/>
        <flushOnExecute statement="${mapProp.sqlmap_namespace}.insert${sqlName}"/>
        <flushOnExecute statement="${mapProp.sqlmap_namespace}.update${sqlName}"/>
        <flushOnExecute statement="${mapProp.sqlmap_namespace}.delete${sqlName}"/>
        <property name="cache-size" value="50"/>
    </cacheModel>

    <!-- ${tabelComments}通用查询条件 -->
    <sql id="${sqlName}_WHERE">
        <dynamic prepend="WHERE">
    <#list tableCols as col>
        <#assign fieldSqlType='${col.fieldSqlType}'>
        <#if fieldSqlType=="DATE">
            <!--按时间段查询-->
            <isNotEmpty prepend="AND" property="MIN_${col.fieldName}">
                <![CDATA[
                ${colAlias}.${col.fieldName}  >= DATE_FORMAT(CONCAT(#MIN_${col.fieldName}#,' 00:00:00'),'%Y-%m-%d %H:%i:%s')
                ]]>
            </isNotEmpty>
            <isNotEmpty prepend="AND" property="MAX_${col.fieldName}">
                <![CDATA[
                ${colAlias}.${col.fieldName} <= DATE_FORMAT(CONCAT(#MAX_${col.fieldName}#,' 23:59:59'),'%Y-%m-%d %H:%i:%s')
                ]]>
            </isNotEmpty>
        <#--<isNotEmpty prepend="AND" property="RQQ">-->
        <#--<![CDATA[-->
        <#--${colAlias}.${col.fieldName}  between TO_DATE(#RQQ#||' 00:00:00','YYYY-MM-DD hh24:mi:ss')-->
        <#--]]>-->
        <#--<isNotEmpty prepend="AND" property="RQZ">-->
        <#--<![CDATA[-->
        <#--TO_DATE(#RQZ#||' 23:59:59','YYYY-MM-DD hh24:mi:ss')-->
        <#--]]>-->
        <#--</isNotEmpty>-->
        <#--</isNotEmpty>-->
        <#else>
            <isNotEmpty prepend="AND" property="${col.fieldName}">
                ${colAlias}.${col.fieldName} = #${col.fieldName}#
            </isNotEmpty>
        </#if>
    </#list>
     </dynamic>  
    </sql>

    <!-- ${tabelComments}通用查询 需要缓存时添加cacheModel="cache${sqlName}" -->
    <select id="select${sqlName}" parameterClass="map" resultClass="resultHashMap">
        SELECT (@rowNum:=@rowNum+1) AS ROWNO,
        (SELECT COUNT(1) FROM ${sqlName} ${colAlias} <include refid="${sqlName}_WHERE"/>) AS TOTAL,
        E.* FROM (
        SELECT <#list tableCols as col><#assign fieldSqlType='${col.fieldSqlType}'><#if fieldSqlType=="DATE">
        STR_TO_DATE(${colAlias}.${col.fieldName},'%Y-%m-%d %H:%i:%s') ${col.fieldName}<#if col_index+1 != tableCols?size>,</#if><#else>${colAlias}.${col.fieldName}<#if col_index+1 != tableCols?size>,</#if></#if><#if (col_index+1)%5==0>${'\r\n\t\t\t'}</#if></#list>
        <#--SELECT <#list tableCols as col><#assign fieldSqlType='${col.fieldSqlType}'><#if fieldSqlType=="DATE">STR_TO_DATE(${mapProp.TABLE_ALIAS}${col.fieldName},'%Y-%m-%d %H:%i:%s') ${col.fieldName}<#if col_index+1 != tableCols?size>,</#if><#else>${mapProp.TABLE_ALIAS}${col.fieldName}<#if col_index+1 != tableCols?size>,</#if></#if><#if (col_index+1)%5==0>${'\r\n\t\t\t'}</#if></#list>-->
        FROM ${sqlName} ${colAlias}
        <include refid="${sqlName}_WHERE"/>
        <isNotEmpty property="SORTNAME">
            	 ORDER BY  ${colAlias}.$SORTNAME$
            	 <isNotEmpty property="SORTORDER">
            	 	 $SORTORDER$
            	 </isNotEmpty>
        </isNotEmpty>
        <isNotNull property="PAGESIZE">
            LIMIT $ROWS$,$PAGESIZE$
        </isNotNull>
         )E,${sqlName} T
        <isNotEmpty property="PAGESIZE">
            ,(SELECT(@rowNum :=$ROWS$))TEM_TB
        </isNotEmpty>
        <isEmpty property="PAGESIZE">
            ,(SELECT(@rowNum :=0))TEM_TB
        </isEmpty>
        WHERE <#list mapPriKey?keys as col>E.${col} = T.${col}<#if col_index+1 != mapPriKey?keys?size> AND </#if></#list>
    </select>

    <!-- ${tabelComments}通用查询记录数 需要缓存时添加cacheModel="cache${sqlName}" -->
    <select id="select${sqlName}Count" parameterClass="map" resultClass="java.lang.Integer">
        SELECT COUNT(1) FROM ${sqlName} ${colAlias}
        <include refid="${sqlName}_WHERE"/>
    </select>

    <!-- ${tabelComments}通用添加 -->
    <insert id="insert${sqlName}" parameterClass="map">
        INSERT INTO ${sqlName}(<#list tableCols as col>${col.fieldName}<#if col_index+1 != tableCols?size>,</#if><#if (col_index+1)%5==0 >${'\r\n\t\t\t'}</#if></#list>)
        VALUES(<#list tableCols as col><#assign fieldSqlType='${col.fieldSqlType}'><#if fieldSqlType=="DATE">NOW()<#else>#${col.javaAtrriber}:${col.sqlMapType}#</#if><#if col_index+1 != tableCols?size>,</#if><#if (col_index+1)%5==0 >${'\r\n\t\t\t'}</#if></#list>)
    </insert>
    <#--<insert id="insert${sqlName}" parameterClass="map">-->
        <#--INSERT INTO ${sqlName}(<#list tableCols as col>${col.fieldName}<#if col_index+1 != tableCols?size>,</#if><#if (col_index+1)%5==0 >${'\r\n\t\t\t'}</#if></#list>)-->
        <#--VALUES(<#list tableCols as col><#assign fieldSqlType='${col.fieldSqlType}'><#if fieldSqlType=="DATE">STR_TO_DATE(CONCAT(SUBSTR(#${col.javaAtrriber}#,1,10),DATE_FORMAT(NOW(),' %H:%i:%s')),'%Y-%m-%d %H:%i:%s')<#else>#${col.javaAtrriber}:${col.sqlMapType}#</#if><#if col_index+1 != tableCols?size>,</#if><#if (col_index+1)%5==0 >${'\r\n\t\t\t'}</#if></#list>)-->
    <#--</insert>-->
    <#--<insert id="insert${sqlName}" parameterClass="map">-->
       <#--INSERT INTO ${sqlName}(<#list tableCols as col>${col.fieldName}<#if col_index+1 != tableCols?size>,</#if><#if (col_index+1)%5==0 >${'\r\n\t\t\t'}</#if></#list>)-->
       <#--VALUES(-->
        <#--<#assign dh =','>-->
        <#--<#list tableCols as col>-->
        <#--<#assign fieldSqlType='${col.sqlMapType}'>-->
        <#--<#if col_index+1 == tableCols?size><#assign dh=''></#if>-->
        <#--<#if fieldSqlType == "CHAR" || fieldSqlType == "CHAR" || fieldSqlType == "VARCHAR" || fieldSqlType == "VARCHAR2">-->
        <#--<isNull property="${col.fieldName}">'${col.default}'${dh}</isNull>-->
        <#--<isNotNull property="${col.fieldName}">-->
            <#--<isEmpty property="${col.fieldName}">'${col.default}'${dh}</isEmpty>-->
            <#--<isNotEmpty property="${col.fieldName}">#${col.javaAtrriber}:${col.sqlMapType}#${dh}</isNotEmpty>-->
        <#--</isNotNull>-->

        <#--<#elseif fieldSqlType=="DATE" || fieldSqlType == "DATETIME">-->
        <#--<isNull property="${col.fieldName}">${col.default}${dh}</isNull>-->
        <#--<isNotNull property="${col.fieldName}">-->
            <#--<isEmpty property="${col.fieldName}">${col.default}${dh}</isEmpty>-->
            <#--<isNotEmpty property="${col.fieldName}">STR_TO_DATE(CONCAT(SUBSTR(#${col.javaAtrriber}#,1,10),DATE_FORMAT(NOW(),' %H:%i:%s')),'%Y-%m-%d %H:%i:%s')${dh}</isNotEmpty>-->
        <#--</isNotNull>-->

        <#--<#elseif fieldSqlType == "DECIMAL" || fieldSqlType == "NUMBER">-->
                <#--&lt;#&ndash;特别处理&ndash;&gt;-->
        <#--<isNull property="${col.fieldName}">${col.default}${dh}</isNull>-->
        <#--<isNotNull property="${col.fieldName}">-->
            <#--<isEmpty property="${col.fieldName}">${col.default}${dh}</isEmpty>-->
            <#--<isNotEmpty property="${col.fieldName}">#${col.javaAtrriber}:${col.sqlMapType}#${dh}</isNotEmpty>-->
        <#--</isNotNull>-->

        <#--<#else>-->

        <#--<isNull property="${col.fieldName}">#${col.javaAtrriber}:${col.sqlMapType}#${dh}</isNull>-->
        <#--<isNotNull property="${col.fieldName}">#${col.javaAtrriber}:${col.sqlMapType}#${dh}</isNotNull>-->

        <#--</#if>-->
    <#--</#list>-->
        <#--)-->
    <#--</insert>-->

    <!-- ${tabelComments}通用更新 -->
    <update id="update${sqlName}" parameterClass="map">
        UPDATE ${sqlName}  
        <dynamic prepend="SET">
    <#list tableCols as col>
        <#assign fieldSqlType='${col.fieldSqlType}'>
        <#if fieldSqlType=="DATE">
            <isNotNull prepend="," property="${col.fieldName}"> ${col.fieldName} = NOW()</isNotNull>
        <#else>
            <isNotNull prepend="," property="${col.fieldName}"> ${col.fieldName} = #${col.javaAtrriber}:${col.sqlMapType}#</isNotNull>
        </#if>
    </#list>
        </dynamic>
        WHERE <#list mapPriKey?keys as col>${col} = #${col}#<#if col_index+1 != mapPriKey?keys?size> AND </#if></#list>
    </update>
    <#--<update id="update${sqlName}" parameterClass="map">-->
        <#--UPDATE ${sqlName}-->
        <#--<dynamic prepend="set">-->
        <#--<#list tableCols as col>-->
        <#--<#assign constraintType='${col.constraintType}'>-->
        <#--<#if constraintType != "P">-->
        <#--&lt;#&ndash;<#assign fieldSqlType='${col.fieldSqlType}'>&ndash;&gt;-->
        <#--<#assign fieldSqlType='${col.sqlMapType}'>-->
        <#--<#if fieldSqlType == "CHAR" || fieldSqlType == "CHAR" || fieldSqlType == "VARCHAR" || fieldSqlType == "VARCHAR2">-->
        <#--<isNotNull property="${col.fieldName}">-->
            <#--<isEmpty prepend="," property="${col.fieldName}">${col.fieldName} = NULL</isEmpty>-->
            <#--<isNotEmpty prepend="," property="${col.fieldName}">${col.fieldName} = #${col.javaAtrriber}:${col.sqlMapType}#</isNotEmpty>-->
        <#--</isNotNull>-->
        <#--<#elseif fieldSqlType=="DATE" || fieldSqlType == "DATETIME">-->
        <#--<isNotNull property="${col.fieldName}">-->
            <#--<isNotEmpty prepend="," property="${col.fieldName}">${col.fieldName} = STR_TO_DATE(CONCAT(SUBSTR(#${col.javaAtrriber}#,1,10),DATE_FORMAT(NOW(),' %H:%i:%s')),'%Y-%m-%d %H:%i:%s')</isNotEmpty>-->
            <#--<!--为日期型字段赋空值 可以修改为当前值NOW()或者其它日期型值&ndash;&gt;-->
            <#--<isEmpty prepend="," property="${col.fieldName}">${col.fieldName} = null</isEmpty>-->
        <#--</isNotNull>-->
        <#--<#elseif  fieldSqlType == "DECIMAL" || fieldSqlType == "NUMBER">-->
                <#--&lt;#&ndash;特别处理&ndash;&gt;-->
        <#--<isNotNull property="${col.fieldName}">-->
            <#--<isNotEmpty prepend="," property="${col.fieldName}">${col.fieldName} = #${col.javaAtrriber}:${col.sqlMapType}#</isNotEmpty>-->
            <#--<isEmpty prepend="," property="${col.fieldName}">${col.fieldName} = NULL</isEmpty>-->
        <#--</isNotNull>-->
        <#--<#else>-->
        <#--<isNotNull property="${col.fieldName}">-->
            <#--<isNotEmpty prepend="," property="${col.fieldName}">${col.fieldName} = #${col.javaAtrriber}:${col.sqlMapType}#</isNotEmpty>-->
            <#--<isEmpty prepend="," property="${col.fieldName}">${col.fieldName} = ${col.default}</isEmpty>-->
        <#--</isNotNull>-->
        <#--</#if>-->
        <#--</#if>-->
        <#--</#list>-->
        <#--</dynamic>-->
        <#--WHERE <#list mapPriKey?keys as col>${col} = #${col}#<#if col_index+1 != mapPriKey?keys?size> AND </#if></#list>-->
    <#--</update>-->

    <!-- 根据主键删除${tabelComments}数据 -->
    <delete id="delete${sqlName}" parameterClass="map">
        DELETE FROM ${sqlName} WHERE <#list mapPriKey?keys as col>${col} = #${col}#<#if col_index+1 != mapPriKey?keys?size> AND </#if></#list>
    </delete>
</sqlMap>
