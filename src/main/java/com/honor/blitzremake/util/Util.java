package com.honor.blitzremake.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Util {

	public static float Clamp(float value, float min, float max) {
		if (value > max)
			return max;
		if (value < min)
			return min;
		return value;
	}

	public static int abs(float value) {
		if (value > 0)
			return 1;
		if (value < 0)
			return -1;
		return 0;
	}

	public static void setBlankCursor() {
		try {
			Cursor emptyCursor = new Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null);
			Mouse.setNativeCursor(emptyCursor);
		} catch (LWJGLException e) {
			System.err.println("I'm not able to set the blank cursor!");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void writeXml(int windowedIN, int widthIN, int heightIN) {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Properties");
			doc.appendChild(rootElement);

			// staff elements
			Element windowed = doc.createElement("Windowed");
			windowed.appendChild(doc.createTextNode(Integer.toString(windowedIN)));
			rootElement.appendChild(windowed);

			Element resolution = doc.createElement("Resolution");
			rootElement.appendChild(resolution);

			// firstname elements
			Element width = doc.createElement("Width");
			width.appendChild(doc.createTextNode(Integer.toString(widthIN)));
			resolution.appendChild(width);

			// lastname elements
			Element height = doc.createElement("Height");
			height.appendChild(doc.createTextNode(Integer.toString(heightIN)));
			resolution.appendChild(height);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("Config.xml"));

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			System.out.println("File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

	}

	private static Document getDocument(String path) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			// factory.setValidating(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			// Document doc = builder.parse(new File(Util.class.getResource(path).toURI()));
			Document doc = builder.parse(new InputSource(path));
			return doc;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			System.err.println("XML file not found!");
			e.printStackTrace();
		}

		return null;
	}

	public static int getProperty(String string) {
		Document doc = getDocument("Config.xml");
		String result = doc.getElementsByTagName(string).item(0).getTextContent();
		return Integer.parseInt(result);
	}

}
