package com.graly.mes.prd.workflow.configuration;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.graly.mes.prd.workflow.util.XmlUtil;

public class ListInfo extends AbstractObjectInfo {

  private static final long serialVersionUID = 1L;

  ObjectInfo[] elementInfos = null;

  public ListInfo(Element listElement, ObjectFactoryParser configParser) {
    super(listElement, configParser);

    List elementElements = XmlUtil.elements(listElement);
    elementInfos = new ObjectInfo[elementElements.size()];
    for (int i=0; i<elementElements.size(); i++) {
      Element elementElement = (Element) elementElements.get(i);
      elementInfos[i] = configParser.parse(elementElement);
    }
  }

  public Object createObject(ObjectFactoryImpl objectFactory) {
    List list = new ArrayList();
    if (elementInfos!=null) {
      for (int i=0; i<elementInfos.length; i++) {
        Object element = objectFactory.getObject(elementInfos[i]);
        list.add(element);
      }
    }
    return list;
  }

}
