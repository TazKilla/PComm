package com.g4.pcomm.utils.axmlrpc.serializer;

import com.g4.pcomm.utils.axmlrpc.XMLRPCException;
import com.g4.pcomm.utils.axmlrpc.xmlcreator.XmlElement;
import org.w3c.dom.Element;

/**
 * A Serializer is responsible to serialize a specific type of data to
 * an xml tag and deserialize the content of this xml tag back to an object.
 * 
 * @author Tim Roes
 */
public interface Serializer {

	/**
	 * This method takes an xml type element and deserialize it to an object.
	 *
	 * @param content Must be an xml element of a specific type.
	 * @return The deserialized content.
	 * @throws XMLRPCException Will be thrown when the deserialization fails.
	 */
	public Object deserialize(Element content) throws XMLRPCException;

	/**
	 * This method takes an object and returns a representation as a string
	 * containing the right xml type tag. The returning string must be usable
	 * within a value tag.
	 *
	 * @param object The object that should be serialized.
	 * @return An XmlElement representation of the object.
	 */
	public XmlElement serialize(Object object);

}