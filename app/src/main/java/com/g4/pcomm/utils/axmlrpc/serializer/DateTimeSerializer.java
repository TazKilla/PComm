package com.g4.pcomm.utils.axmlrpc.serializer;

import java.text.SimpleDateFormat;

import org.w3c.dom.Element;

import com.g4.pcomm.utils.Iso8601Deserializer;
import com.g4.pcomm.utils.axmlrpc.XMLRPCException;
import com.g4.pcomm.utils.axmlrpc.XMLUtil;
import com.g4.pcomm.utils.axmlrpc.xmlcreator.XmlElement;

/**
 *
 * @author timroes
 */
public class DateTimeSerializer implements Serializer {

	private static final String DATETIME_FORMAT = "yyyyMMdd'T'HHmmss";
	private final SimpleDateFormat DATE_FORMATER = new SimpleDateFormat(DATETIME_FORMAT);

	@Override
	public Object deserialize(Element content) throws XMLRPCException {
		return deserialize(XMLUtil.getOnlyTextContent(content.getChildNodes()));
	}

	public Object deserialize(String dateStr) throws XMLRPCException {
		try {
			return Iso8601Deserializer.toDate(dateStr);
		} catch (Exception ex) {
			throw new XMLRPCException("Unable to parse given date.", ex);
		}
	}

	@Override
	public XmlElement serialize(Object object) {
		return XMLUtil.makeXmlTag(SerializerHandler.TYPE_DATETIME,
				DATE_FORMATER.format(object));
	}

}
