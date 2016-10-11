/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.plugins;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 */
public class XmlMarshallable {

    public String xmlName() throws Exception {
        Class c = this.getClass();
        if (!c.isAnnotationPresent(XmlName.class))
            throw new Exception("Class " + c.getName() + " has no @XmlName annotation");
        XmlName xmlname = this.getClass().getAnnotation(XmlName.class);
        return xmlname.name();
    }

    public Element toXml(Document doc) throws Exception {
        return doc.createElement(xmlName());
    }
}
