/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.plugins;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 */
public class XmlMarshallable {

    public String xmlName() throws Exception {
        Class c = this.getClass().getSuperclass();
        if (!c.isAnnotationPresent(XmlName.class)) {
            Class d = this.getClass();
            if (!d.isAnnotationPresent(XmlName.class)) {
                throw new Exception("SuperClass " + c.getName() + " has no @XmlName annotation" + " as well as Class " + d.getName());
            }
            XmlName xmlname = this.getClass().getAnnotation(XmlName.class);
            return xmlname.name();

        }
        XmlName xmlname = this.getClass().getSuperclass().getAnnotation(XmlName.class);
        return xmlname.name();

    }

    public Element toXml(Document doc) throws Exception {
        return doc.createElement(xmlName());
    }
}
