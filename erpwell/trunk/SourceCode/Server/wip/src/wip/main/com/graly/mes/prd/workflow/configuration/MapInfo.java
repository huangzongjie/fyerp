package com.graly.mes.prd.workflow.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.graly.mes.prd.workflow.util.XmlUtil;

public class MapInfo extends AbstractObjectInfo {

  private static final long serialVersionUID = 1L;
  
  ObjectInfo[] keyInfos = null;
  ObjectInfo[] valueInfos = null;

  public MapInfo(Element mapElement, ObjectFactoryParser configParser) {
    super(mapElement, configParser);

    List entryElements = XmlUtil.elements(mapElement);
    keyInfos = new ObjectInfo[entryElements.size()];
    valueInfos = new ObjectInfo[entryElements.size()];
    for (int i=0; i<entryElements.size(); i++) {
      Element entryElement = (Element) entryElements.get(i);
      Element keyElement = XmlUtil.element(entryElement, "key");
      Element valueElement = XmlUtil.element(entryElement, "value");
      keyInfos[i] = configParser.parse(XmlUtil.element(keyElement));
      valueInfos[i] = configParser.parse(XmlUtil.element(valueElement));
    }
  }

  public Object createObject(ObjectFactoryImpl objectFactory) {
    Map map = new HashMap();
    if (keyInfos!=null) {
      for (int i=0; i<keyInfos.length; i++) {
        Object key = objectFactory.getObject(keyInfos[i]);
        Object value = objectFactory.getObject(valueInfos[i]);
        map.put(key, value);
      }
    }
    return map;
  }
}
