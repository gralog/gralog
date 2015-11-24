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
 * @author viktor
 */
public class XmlMarshallable {
    
    public String XmlName() throws Exception {
        Class c = this.getClass();
        if(!c.isAnnotationPresent(XmlName.class))
            throw new Exception("Class " + c.getName() + " has no @XmlName annotation");
        XmlName xmlname = this.getClass().getAnnotation(XmlName.class);
        return xmlname.name();
    }
    
    public Element ToXml(Document doc) throws Exception
    {
        return doc.createElement(XmlName());
    }
    
}
