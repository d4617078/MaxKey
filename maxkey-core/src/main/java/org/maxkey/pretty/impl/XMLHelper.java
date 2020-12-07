package org.maxkey.pretty.impl;

import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;
import org.xml.sax.InputSource;

import net.shibboleth.utilities.java.support.collection.LazyMap;

public class XMLHelper {

    /**
     * A string which contains the valid delimiters for the XML Schema 'list' type. These are: space, newline, carriage
     * return, and tab.
     */
    public static final String LIST_DELIMITERS = " \n\r\t";
    
    /** DOM configuration parameters used by LSSerializer in pretty print format output. */
    private static Map<String, Object> prettyPrintParams;
    

    /**
     * Converts a Node into a String using the DOM, level 3, Load/Save serializer.
     * 
     * @param node the node to be written to a string
     * 
     * @return the string representation of the node
     */
    public static String nodeToString(Node node) {
        StringWriter writer = new StringWriter();
        writeNode(node, writer);
        return writer.toString();
    }
    
    /**
     * Pretty prints the XML node.
     * 
     * @param node xml node to print
     * 
     * @return pretty-printed xml
     */
    public static String prettyPrintXML(Node node) {
        StringWriter writer = new StringWriter();
        writeNode(node, writer,  getPrettyPrintParams());
        return writer.toString();
    }
    
	public static String prettyPrintXML(String xmlString){
		try{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		    Document document = documentBuilder.parse(new InputSource(new StringReader(xmlString)));
			return prettyPrintXML(document);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
    
    /**
     * Create the parameters set used in pretty print formatting of an LSSerializer.
     * 
     * @return the params map
     */
    private static Map<String, Object> getPrettyPrintParams() {
        if (prettyPrintParams == null) {
            prettyPrintParams = new LazyMap<String, Object>();
            prettyPrintParams.put("format-pretty-print", Boolean.TRUE);
        }
        return prettyPrintParams;
    }

    /**
     * Writes a Node out to a Writer using the DOM, level 3, Load/Save serializer. The written content is encoded using
     * the encoding specified in the writer configuration.
     * 
     * @param node the node to write out
     * @param output the writer to write the XML to
     */
    public static void writeNode(Node node, Writer output) {
        writeNode(node, output, null);
    }
    
    /**
     * Writes a Node out to a Writer using the DOM, level 3, Load/Save serializer. The written content is encoded using
     * the encoding specified in the writer configuration.
     * 
     * @param node the node to write out
     * @param output the writer to write the XML to
     * @param serializerParams parameters to pass to the {@link DOMConfiguration} of the serializer
     *         instance, obtained via {@link LSSerializer#getDomConfig()}. May be null.
     */
    public static void writeNode(Node node, Writer output, Map<String, Object> serializerParams) {
        DOMImplementationLS domImplLS = getLSDOMImpl(node);
        
        LSSerializer serializer = getLSSerializer(domImplLS, serializerParams);

        LSOutput serializerOut = domImplLS.createLSOutput();
        serializerOut.setCharacterStream(output);

        serializer.write(node, serializerOut);
    }
    
    /**
     * Writes a Node out to an OutputStream using the DOM, level 3, Load/Save serializer. The written content
     * is encoded using the encoding specified in the output stream configuration.
     * 
     * @param node the node to write out
     * @param output the output stream to write the XML to
     */
    public static void writeNode(Node node, OutputStream output) {
        writeNode(node, output, null);
    }


    /**
     * Writes a Node out to an OutputStream using the DOM, level 3, Load/Save serializer. The written content 
     * is encoded using the encoding specified in the output stream configuration.
     * 
     * @param node the node to write out
     * @param output the output stream to write the XML to
     * @param serializerParams parameters to pass to the {@link DOMConfiguration} of the serializer
     *         instance, obtained via {@link LSSerializer#getDomConfig()}. May be null.
     */
    public static void writeNode(Node node, OutputStream output, Map<String, Object> serializerParams) {
        DOMImplementationLS domImplLS = getLSDOMImpl(node);
        
        LSSerializer serializer = getLSSerializer(domImplLS, serializerParams);

        LSOutput serializerOut = domImplLS.createLSOutput();
        serializerOut.setByteStream(output);

        serializer.write(node, serializerOut);
    }
    
    /**
     * Obtain a the DOM, level 3, Load/Save serializer {@link LSSerializer} instance from the
     * given {@link DOMImplementationLS} instance.
     * 
     * <p>
     * The serializer instance will be configured with the parameters passed as the <code>serializerParams</code>
     * argument. It will also be configured with an {@link LSSerializerFilter} that shows all nodes to the filter, 
     * and accepts all nodes shown.
     * </p>
     * 
     * @param domImplLS the DOM Level 3 Load/Save implementation to use
     * @param serializerParams parameters to pass to the {@link DOMConfiguration} of the serializer
     *         instance, obtained via {@link LSSerializer#getDomConfig()}. May be null.
     *         
     * @return a new LSSerializer instance
     */
    public static LSSerializer getLSSerializer(DOMImplementationLS domImplLS, Map<String, Object> serializerParams) {
        LSSerializer serializer = domImplLS.createLSSerializer();
        
        serializer.setFilter(new LSSerializerFilter() {

            public short acceptNode(Node arg0) {
                return FILTER_ACCEPT;
            }

            public int getWhatToShow() {
                return SHOW_ALL;
            }
        });
        
        
        if (serializerParams != null) {
            DOMConfiguration serializerDOMConfig = serializer.getDomConfig();
            for (String key : serializerParams.keySet()) {
                serializerDOMConfig.setParameter(key, serializerParams.get(key));
            }
        }
        
        return serializer;
    }
    
    /**
     * Get the DOM Level 3 Load/Save {@link DOMImplementationLS} for the given node.
     * 
     * @param node the node to evaluate
     * @return the DOMImplementationLS for the given node
     */
    public static DOMImplementationLS getLSDOMImpl(Node node) {
        DOMImplementation domImpl;
        if (node instanceof Document) {
            domImpl = ((Document) node).getImplementation();
        } else {
            domImpl = node.getOwnerDocument().getImplementation();
        }

        DOMImplementationLS domImplLS = (DOMImplementationLS) domImpl.getFeature("LS", "3.0");
        return domImplLS;
    }
    
    
}
