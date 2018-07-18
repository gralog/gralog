/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.plugins;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 */
public class XmlMarshallable {

    public String xmlName() throws Exception {
        Class c = this.getClass();
        if (!c.isAnnotationPresent(XmlName.class)) {
        	Class d = c.getSuperclass();
        	if (!c.isAnnotationPresent(XmlName.class)) {
            	throw new Exception("Class " + c.getName() + " has no @XmlName annotation");        		
        	}
            XmlName xmlname = this.getClass().getSuperclass().getAnnotation(XmlName.class);
            return xmlname.name();

        }
        XmlName xmlname = this.getClass().getAnnotation(XmlName.class);
        return xmlname.name();

    }

    public Element toXml(Document doc) throws Exception {
        return doc.createElement(xmlName());
    }
}
