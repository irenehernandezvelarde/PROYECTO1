import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Model {
//Clase on es gestionen totes les dades necessaries per al funcionament de l'aplicacio
	
	//Variables
	private File file = null;
	private Document doc;
	private NodeList xml;
	private ArrayList<Object> controls = new ArrayList<Object>();
	
	public void carregarConfiguracio() {//Carrega la configuracio inicial de l'aplicacio des d'un fitxer .xml
		
		//Obre el fitxer com a xml
		try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file);
        } catch (ParserConfigurationException e) {
        	e.printStackTrace(); 
        } catch (SAXException e) { 
        	e.printStackTrace();
        } catch (IOException e) { 
        	e.printStackTrace(); 
        }
		
		//Lectura xml
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			xml = (NodeList) xPath.compile("root").evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {e.printStackTrace();}
		
		//Obtencio de valors de l'xml
		for (int a = 0 ; a < xml.getLength() ; a++) {
			Node node = xml.item(a);
			System.out.println(xml.item(0).getNodeName());
		}
		
	}// m carregarConfiguracio
	
	public void setFile(File File) {//Estableix el fitxer a on es troben les dades de l'aplicacio
		file = File;
	}// m setFile
}
