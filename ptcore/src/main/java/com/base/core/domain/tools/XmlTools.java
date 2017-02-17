package com.base.core.domain.tools;

import com.alibaba.fastjson.JSON;
import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

/**
 * <ol>
 * date:13-10-31 editor:YangHongJian
 * <li>创建文档<li>
 * <li>xml解析辅助工具<li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 3.0
 * @since 1.4.2
 */
public class XmlTools {
    private static Logger log = Logger.getLogger(XmlTools.class);
    private static XmlTools _xmlTools = null;

    private static SAXReader saxReader = new SAXReader();

    protected XmlTools() {
    }

    /**
     * 获取xml解析辅助工具单实例
     *
     * @return 返回单实例
     */
    public static XmlTools getInstanceXmlHelp() {
        if (_xmlTools == null)
            _xmlTools = new XmlTools();
        return _xmlTools;
    }

    /**
     * 解析报文返回Element类型的节点
     *
     * @param strXml 报文
     * @return root
     */
    public Element getXmlRoot(String strXml) {
        Element root = null;
        try {
            Document doc = saxReader.read(new ByteArrayInputStream(strXml.getBytes("UTF-8")));
            root = doc.getRootElement();
        } catch (Exception e) {
            log.error(e);
        }
        return root;
    }

    /**
     * 解析报文返回Element类型的节点
     *
     * @param strXml 报文
     * @param ele    节点名称
     * @return root
     */
    private Element getXmlElement(String strXml, String ele) {
        Element element = null;
        try {
            Element root = getXmlRoot(strXml);
            if (root != null)
                element = root.element(ele);
        } catch (Exception e) {
            log.error(e);
        }
        return element;
    }

    /**
     * 解析调用存储过程或者函数返回的xml
     *
     * @param strXml 固定格式的xml字符串
     * @return 返回Map对象
     */
    public Map parserXmlRetProcOrFn(String strXml) {
        Map mapRet = new HashMap();
        try {
            Element elParams = getXmlRoot(strXml);
            Element ele;
            for (Iterator it = elParams.elementIterator(); it.hasNext(); ) {
                ele = (Element) it.next();
                mapRet.put(ele.getName(), ele.getTextTrim());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return mapRet;
    }

    /**
     * xml转换为Map对象
     *
     * @param doc xml的doc对象
     * @return 返回map对象
     */
    public static Map<String, Object> xmlToMap(Document doc) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (doc == null)
            return map;
        Element root = doc.getRootElement();
        for (Iterator iterator = root.elementIterator(); iterator.hasNext(); ) {
            Element e = (Element) iterator.next();
            List list = e.elements();
            if (list.size() > 0) {
                map.put(e.getName(), xmlToMap(e));
            } else
                map.put(e.getName(), e.getText().trim());
        }
        return map;
    }

    /**
     * xml转换为Map对象
     *
     * @param e xml的节点对象
     * @return 返回map对象
     */
    public static Map xmlToMap(Element e) {
        Map map = new HashMap();
        List list = e.elements();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Element iter = (Element) list.get(i);
                List mapList = new ArrayList();

                if (iter.elements().size() > 0) {
                    Map m = xmlToMap(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), m);
                } else {
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if (obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = (List) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), iter.getText());
                }
            }
        } else
            map.put(e.getName(), e.getText());
        return map;
    }

    /**
     * xml转换为Map对象
     *
     * @param strXml xml字符串
     * @return 返回map对象
     */
    public static Map xmlToMap(String strXml) {
        try {
//            Document docXml = DocumentHelper.parseText(strXml);
            return xmlToMap(DocumentHelper.parseText(strXml));
        } catch (DocumentException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * map对象转化为xml
     *
     * @param mapParam map对象
     * @return xml字符串
     */
    public static String mapToXml(Map mapParam) {
        String strJson = JSON.toJSONString(mapParam);

        StringReader input = new StringReader(strJson);
        StringWriter output = new StringWriter();
        JsonXMLConfig config = new JsonXMLConfigBuilder().multiplePI(false).repairingNamespaces(false).build();
        try {
            XMLEventReader reader = new JsonXMLInputFactory(config).createXMLEventReader(input);
            XMLEventWriter writer = XMLOutputFactory.newInstance().createXMLEventWriter(output);
            writer = new PrettyXMLEventWriter(writer);
            writer.add(reader);
            reader.close();
            writer.close();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                output.close();
                input.close();
            } catch (IOException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        return output.toString();
    }

    public static void main(String[] args) {
        XmlTools xmlTools = XmlTools.getInstanceXmlHelp();
        String strRet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><params><code>0</code><msg>操作成功</msg></params>";
        strRet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><RET><CODE> 0</CODE><MSG> 13800000000</MSG><DATE><ITEM>1</ITEM><ITEM>2</ITEM></DATE></RET>";

        Map map = xmlTools.parserXmlRetProcOrFn(strRet);
        System.out.println(map);

        map.clear();
        map = xmlToMap(strRet);
        System.out.println(map);

        strRet = mapToXml(map);
        System.out.println(strRet);

        StringBuffer sb  =new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><ret><code> 0</code>");
        sb.append("<af><item><name>1</name><sex>1</sex></item><item><name>2</name><sex>0</sex></item></af>")
                .append("</ret>");
        map = xmlToMap(sb.toString());
        System.out.println(map);

        strRet = "";
    }
}
