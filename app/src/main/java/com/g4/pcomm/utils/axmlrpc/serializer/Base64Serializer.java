package com.g4.pcomm.utils.axmlrpc.serializer;

import com.g4.pcomm.utils.axmlrpc.XMLRPCException;
import com.g4.pcomm.utils.axmlrpc.XMLUtil;
import com.g4.pcomm.utils.axmlrpc.xmlcreator.XmlElement;
import com.g4.pcomm.utils.base64.Base64;
import org.w3c.dom.Element;

/**
 *
 * @author Tim Roes
 */
public class Base64Serializer implements Serializer {

	public Object deserialize(Element content) throws XMLRPCException {
		return Base64.decode(XMLUtil.getOnlyTextContent(content.getChildNodes()));
	}

	public XmlElement serialize(Object object) {
		return XMLUtil.makeXmlTag(SerializerHandler.TYPE_BASE64,
				Base64.encode((Byte[])object));
	}

}