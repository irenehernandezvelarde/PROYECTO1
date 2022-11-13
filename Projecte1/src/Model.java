import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;





public class Model {
//Clase on es gestionen totes les dades necessaries per al funcionament de l'aplicacio
	
	//Variables
	private File file = null;
	private Document doc;
	private ArrayList<ControlsBlock> controls; //controls.get(indexDeBloc).get(indexDeComponent)
	
	public void carregarConfiguracio() {//Carrega la configuracio inicial de l'aplicacio des d'un fitxer .xml
		
		//Obre el fitxer com a xml
		try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException e) {
        	e.printStackTrace(); 
        } catch (SAXException e) { 
        	e.printStackTrace();
        } catch (IOException e) { 
        	e.printStackTrace(); 
        }
		
		//LECTURA DE DADES DE L'XML
		
		//Inicialitzacio del model de dades (conte els blocs de components)
		controls = new ArrayList<ControlsBlock>();
		
		//Creacio de bloc de components (conte els components)
		NodeList controlBlocksNodes = doc.getElementsByTagName("controls"); //Llista dels blocs de controls
		for(int a = 0; a < controlBlocksNodes.getLength(); a ++) {
			Element elm = (Element) controlBlocksNodes.item(a);
			ControlsBlock ncontrolBlock = new ControlsBlock();
			ncontrolBlock.setName(elm.getAttribute("name"));
			
			//Insercio de components
			NodeList controlsNodes = (NodeList)controlBlocksNodes.item(a); //Llista dels controls del bloc (switch/slider/dropdown/sensor/...)
			for (int b = 0; b < controlsNodes.getLength(); b++) {
				Node node = controlsNodes.item(b); //Control específic del bloc
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					elm = (Element) node;
					
					System.out.println(elm.getNodeName());
					
					switch (elm.getNodeName()) {
					
					case "switch":
						CSwitch nSwitch = new CSwitch();
						nSwitch.setId(Integer.parseInt(elm.getAttribute("id")));
						if (elm.getAttribute("default").contentEquals("on")) {nSwitch.setSelected(true);
						} else {nSwitch.setSelected(false);}
						nSwitch.setText(elm.getTextContent());
						ncontrolBlock.add(nSwitch);
						break;
					
					case "slider":
						CSlider nSlider = new CSlider();
						nSlider.setId(Integer.parseInt(elm.getAttribute("id")));
						nSlider.setConversionFactor(Double.parseDouble(elm.getAttribute("step")));
						
						//setMaximum i setMinimum
						Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
						for (int c = ((int) (Double.parseDouble(elm.getAttribute("min"))*nSlider.getConversionFactor())); c < ((int) (Double.parseDouble(elm.getAttribute("max"))*nSlider.getConversionFactor())) ; c += (Double.parseDouble(elm.getAttribute("step"))*nSlider.getConversionFactor())){
						    labelTable.put(c, new JLabel(String.valueOf(c/nSlider.getConversionFactor())));
						}
					    nSlider.setLabelTable( labelTable );
					    nSlider.setPaintLabels(true);
						
						nSlider.setMinorTickSpacing((int) (Double.parseDouble(elm.getAttribute("step"))*nSlider.getConversionFactor())); nSlider.setSnapToTicks(true);
						nSlider.setValue((int) (Double.parseDouble(elm.getAttribute("default"))*nSlider.getConversionFactor()));
					    nSlider.setName(elm.getTextContent());
					    ncontrolBlock.add(nSlider);
						break;
					
					case "dropdown":
						CDropdown nDropdown = new CDropdown();
						nDropdown.setId(Integer.parseInt(elm.getAttribute("id")));
						NodeList dropdownOpts = (NodeList) elm.getElementsByTagName("option");
						for (int c = 0; c < dropdownOpts.getLength() ; c++) {
							Node optionNode = dropdownOpts.item(c);
							Element optionElm = (Element) optionNode;
							CDropdownOption nDropdownOption = new CDropdownOption();
							nDropdownOption.setOptionId(Integer.parseInt(optionElm.getAttribute("value")));
							nDropdownOption.setText(optionElm.getTextContent());
							nDropdown.addOption(nDropdownOption);
						}
						//nDropdown.setSelectedIndex((int)Double.parseDouble(elm.getAttribute("default")));
						ncontrolBlock.add(nDropdown);
						break;
					
					case "sensor": //Label con el valor + ºC
						CSensor nSensor = new CSensor();
						nSensor.setId(Integer.parseInt(elm.getAttribute("id")));
						nSensor.setUnit(elm.getAttribute("units"));
						nSensor.setThresholdLow((int)Double.parseDouble(elm.getAttribute("thresholdlow")));
						nSensor.setThresholdHigh((int)Double.parseDouble(elm.getAttribute("thresholdhigh")));
						nSensor.setText(elm.getTextContent());
						ncontrolBlock.add(nSensor);
						break;
					
					default:
						System.out.println("\nERROR: Unknown_control_type"
										 + "\nControl type: " + elm.getNodeName());
						break;
					}//switch
				}//if node es element
				
			}//for per a cada component
			controls.add(ncontrolBlock);
		}//for per cada bloc de components
		
		//TO REMOVE Impressio del model de dades resultant
		System.out.println("DATA MODEL");
		for (int a = 0 ; a < controls.size() ; a++) {
			System.out.println("\nBloc: " + controls.get(a).getName());
			for (int b = 0 ; b < controls.get(a).size() ; b++) {
				System.out.println(controls.get(a).get(b).toString());
			}
		}
		System.out.println("DATA MODEL FINISHED PRINTING");
		
	}// m carregarConfiguracio

	public void setFile(File File) {//Estableix el fitxer a on es troben les dades de l'aplicacio
		file = File;
	}// m setFile
	
}





//BLOC DE CONTROLS

class ControlsBlock extends ArrayList<Object> {
	private String name;
	public void setName(String name) {this.name = name;}
	public String getName() {return name;}
}

//CLASES 'CUSTOM CONTROL' (cClasecontrol)

class CSwitch extends JToggleButton{
	private int id;
	public void setId(int id) {this.id = id;}
	public int getId() {return id;}
	
	public String toString() {return "Id: " + id + " 	Default: " + this.isSelected() + " 	Text: " + this.getText();}
}

//CSlider pot representar nombres decimals, pero s'ha de definir el ConversionFactor (metode setConversionFactor) a partir del minorTickSpacing
//i utilitzar (sobre el valor, abans d'utilitzarlo al metode (ex .setValue(intValue*CSlider.getConversionFactor) )
class CSlider extends JSlider{ 
	private int id;
	public void setId(int id) {this.id = id;}
	public int getId() {return id;}
	
	private int conversionFactor = 1;
	public void setConversionFactor(Double minorTickSpacing) {
		//Conversio decimals a enters (nomes als valors)
		for (int c = 0; c < BigDecimal.valueOf(minorTickSpacing).scale() ; c++) {
			conversionFactor = conversionFactor*10;
		}
		if (conversionFactor == 0) {conversionFactor = 1;}
	}
	public int getConversionFactor() {return conversionFactor;}
	
	public String toString() {return "Id: " + id + " 	Default: " + this.getValue() + " 	Min: " + this.getMinimum() + " 	Max: " + this.getMaximum() + " 	Step: " + this.getMinorTickSpacing() + " 	(SENSE APLICAR CONVERSIONFACTOR)";}
}

class CDropdown extends JComboBox{
	private int id;
	public void setId(int id) {this.id = id;}
	public int getId() {return id;}
	
	private CDropdownOption[] dropdownOptions = {};
	public void addOption(CDropdownOption option) {
		ArrayList<CDropdownOption> editableOptions = new ArrayList<CDropdownOption>(Arrays.asList(dropdownOptions));
		editableOptions.add(option);
		CDropdownOption[] arr = new CDropdownOption[editableOptions.size()];
        arr = editableOptions.toArray(arr);
		dropdownOptions = arr;
		updateDropdownOptions();
	}
	public void removeOption(CDropdownOption option) { //Si no funciona por la conversion del array, mira metodo addOption
		ArrayList<CDropdownOption> editableOptions = (ArrayList<CDropdownOption>) Arrays.asList(dropdownOptions);
		editableOptions.remove(editableOptions.indexOf(option));
		dropdownOptions = (CDropdownOption[]) editableOptions.toArray();
		updateDropdownOptions();
	}
	public void removeOption(int optionId) { //Si no funciona por la conversion del array, mira metodo addOption
		ArrayList<CDropdownOption> editableOptions = (ArrayList<CDropdownOption>) Arrays.asList(dropdownOptions);
		CDropdownOption optionToRemove;
		for (CDropdownOption a : dropdownOptions) {
			if (a.getOptionId() == optionId) {
				optionToRemove = a;
				editableOptions.remove(optionToRemove);
			}
		}
		dropdownOptions = (CDropdownOption[]) editableOptions.toArray();
		updateDropdownOptions();
	}
	private void updateDropdownOptions() {
		String[] optionNames = new String[dropdownOptions.length-1];
		for (int a = 0; a < optionNames.length ; a++) {
			optionNames[a] = dropdownOptions[a].getText();
		}
		this.setModel(new DefaultComboBoxModel(optionNames));
	}
	public CDropdownOption[] getOptions() {
		return dropdownOptions;
	}
	public String toString() {
		String OptionsToString = "";
		for (int a = 0 ; a < dropdownOptions.length ; a++) {
			OptionsToString += "\n	Option index: " + a + " 	" + dropdownOptions[a].toString();
		}
		return "Id: " + id + " 	Default: " + this.getSelectedIndex() + " 	" + OptionsToString;}
}
class CDropdownOption {
	private int optionId;
	public void setOptionId(int id){this.optionId = id;}
	public int getOptionId(){return optionId;}
	
	private String text;
	public void setText(String text){this.text = text;}
	public String getText(){return text;}
	
	public String toString() {return "Id: " + optionId + " 	Text: " + text;}
}

class CSensor extends JLabel{
	private int id;
	public void setId(int id) {this.id = id;}
	public int getId() {return id;}
	
	private String unit;
	public void setUnit(String unit) {this.unit = unit;}
	public String getUnit() {return unit;}
	
	private int thresholdLow;
	public void setThresholdLow(int thresholdLow) {this.thresholdLow = thresholdLow;}
	public int getThresholdLow() {return thresholdLow;}
	
	private int thresholdHigh;
	public void setThresholdHigh(int thresholdHigh) {this.thresholdHigh = thresholdHigh;}
	public int getThresholdHigh() {return thresholdHigh;}
	
	public String toString() {
		return "Id: " + id + " 	Unit: " + unit + " 	Treshold Low: " + thresholdLow + " 	Treshold High: " + thresholdHigh + " 	Text: " + this.getText();
	}
}
