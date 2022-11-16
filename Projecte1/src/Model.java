import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import javax.swing.BorderFactory;
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
	private View view = null;
	private File file = null;
	private Document doc;
	private ArrayList<ControlsBlock> controls; //controls.get(indexDeBloc).get(indexDeComponent)
	
	public Model(View view) {
		this.view = view;
	}
	
	public int carregarConfiguracio() {//Carrega la configuracio inicial de l'aplicacio des d'un fitxer .xml
		//Return per a errors (1 amb error / 0 tot correcte)
		
		//Obre el fitxer com a xml
		try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException e) {
        	view.showErrorPopup("Unknown parser error");
        	e.printStackTrace();
        	return 1;
        } catch (SAXException e) {
        	view.showErrorPopup("Missing tag in xml");
        	e.printStackTrace();
        	return 1;
        } catch (IOException e) { 
        	view.showErrorPopup("Unknown exception");
        	e.printStackTrace();
        	return 1;
        }
		
		//LECTURA DE DADES DE L'XML
		
		//Inicialitzacio del model de dades (conte els blocs de components)
		controls = new ArrayList<ControlsBlock>();
		
		//Creacio de bloc de components (conte els components)
		NodeList controlBlocksNodes = doc.getElementsByTagName("controls"); //Llista dels blocs de controls
		for(int a = 0; a < controlBlocksNodes.getLength(); a ++) {
			Element elm = (Element) controlBlocksNodes.item(a);
			
			//Creacio bloc de controls
			ControlsBlock ncontrolBlock = new ControlsBlock();
			ncontrolBlock.setName(elm.getAttribute("name"));
			
			//Creacio de components i insercio al bloc
			NodeList controlsNodes = (NodeList)controlBlocksNodes.item(a); //Llista dels controls del bloc (switch/slider/dropdown/sensor/...)
			for (int b = 0; b < controlsNodes.getLength(); b++) {
				Node node = controlsNodes.item(b); //Control específic del bloc
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					elm = (Element) node; 
					
					int id;
					String defaultString;
					Double defaultDouble;
					int defaultInt;
					String text;
					Double min;
					Double max;
					Double step;
					String unit;
					Double thresholdLow;
					Double thresholdHigh;
					
					switch (elm.getNodeName()) {
					
						case "switch":
							//VARIABLES
							id = Integer.parseInt(elm.getAttribute("id"));
							defaultString = elm.getAttribute("default");
							text = elm.getTextContent();
							//CONTROL D'ERRORS (VALORS INVALIDS)
							if (!defaultString.contentEquals("on") && !defaultString.contentEquals("off")) {
								System.out.println("\nERROR: Unknown_default_toggle_value");
					        	view.showErrorPopup("Unknown default toggle("+id+") value\nAccepted values: on/off");
								break;
							}
							//CREACIÓ COMPONENT
							CSwitch nSwitch = new CSwitch();
							nSwitch.setId(id);
							if (defaultString.contentEquals("on")) {
								nSwitch.setSelected(true);
								nSwitch.setText(defaultString.toUpperCase());
							} else {
								nSwitch.setSelected(false);
								nSwitch.setText(defaultString.toUpperCase());
							}
							nSwitch.addActionListener(new ActionListener() {
								@Override public void actionPerformed(ActionEvent e) {
									if (nSwitch.isSelected() == true) {
										nSwitch.setText("ON");
									} else {nSwitch.setText("OFF");}
								}
							});
							nSwitch.setBorder(BorderFactory.createTitledBorder(text));
							ncontrolBlock.add(nSwitch);
							break;
						
						case "slider":
							//VARIABLES
							id = Integer.parseInt(elm.getAttribute("id"));
							defaultDouble = Double.parseDouble(elm.getAttribute("default"));
							min = Double.parseDouble(elm.getAttribute("min"));
							max = Double.parseDouble(elm.getAttribute("max"));
							step = Double.parseDouble(elm.getAttribute("step"));
							text = elm.getTextContent();
							//CONTROL D'ERRORS (VALORS INVALIDS)
							if (min > max){
								System.out.println("\nERROR: Minimum_value_cant_be_greater_than_Maximum");
					        	view.showErrorPopup("Slider("+id+") minimum value can't be greater than maximum");
					        	return 1;
							}
							if (defaultDouble < min || defaultDouble > max ) {
								System.out.println("\nERROR: Default_value_out_of_bounds");
					        	view.showErrorPopup("Slider("+id+") default value out of bounds");
								return 1;
							}
							if (step > (max-min)) {
								System.out.println("\nERROR: Step_can't_be_greater_than_the_number_of_values_between_min_and_max");
					        	view.showErrorPopup("Slider("+id+") step can't be greater than the amount of values between min and max\n" + "Step: " + step + " Step max value: " + (max-min));
								System.out.println();
								return 1;
							}
							if (max%step != 0 || min%step != 0) {
								System.out.println("\nERROR: Step_uneven_relative_to_min_and_max_values");
								view.showErrorPopup("Slider("+id+") step uneven relative to min and max values");
								return 1;
							}
							//CREACIO COMPONENT
							CSlider nSlider = new CSlider();
							nSlider.setId(id);
							nSlider.setConversionFactor(step); //Necessari per a mostrar decimals
								//setMinimum i setMaximum (i tots els valors entre mitj)
							Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
							for (int c = ((int) (min*nSlider.getConversionFactor())); c <= (max*nSlider.getConversionFactor()) ; c += (step*nSlider.getConversionFactor())){
							    labelTable.put(c, new JLabel(String.valueOf(Double.valueOf(c)/nSlider.getConversionFactor())));
							}
						    nSlider.setLabelTable( labelTable );
						    nSlider.setPaintLabels(true);
								//--- fi setMinimum i setMaximum ---
						    nSlider.setValue((int) (defaultDouble*nSlider.getConversionFactor()));
							nSlider.setMinorTickSpacing((int)(step*nSlider.getConversionFactor())); nSlider.setSnapToTicks(true);
							nSlider.setBorder(BorderFactory.createTitledBorder(text));
						    ncontrolBlock.add(nSlider);
							break;
						
						case "dropdown":
							//VARIABLES
							id = Integer.parseInt(elm.getAttribute("id"));
							defaultInt = Integer.parseInt(elm.getAttribute("default"));
							text = elm.getAttribute("label");
							//CREACIO COMPONENT + CONTROL D'ERRORS (VALORS INVALIDS)
							CDropdown nDropdown = new CDropdown();
							nDropdown.setId(id);
							nDropdown.setBorder(BorderFactory.createTitledBorder(text));
								//Insercio opcions
							NodeList dropdownOpts = (NodeList) elm.getElementsByTagName("option");
							for (int c = 0; c < dropdownOpts.getLength() ; c++) {
								Node optionNode = dropdownOpts.item(c);
								Element optionElm = (Element) optionNode;
								CDropdownOption nDropdownOption = new CDropdownOption();
								nDropdownOption.setOptionId(Integer.parseInt(optionElm.getAttribute("value")));
								nDropdownOption.setText(optionElm.getTextContent());
								nDropdown.addOption(nDropdownOption);
							}
								//Control error index invalid
							if (nDropdown.getModel().getSize() < defaultInt) {
								System.out.println("\nERROR: Default_index_out_of_bounds");
								view.showErrorPopup("Dropdown("+id+") default option index out of bounds");
								return 1;
							} else { nDropdown.setSelectedIndex(defaultInt); }
							ncontrolBlock.add(nDropdown);
							break;
						
						case "sensor": //Label amb el text igual a : "valor" + "unit"
							//VARIABLES
							id = Integer.parseInt(elm.getAttribute("id"));
							unit = elm.getAttribute("units");
							thresholdLow = Double.parseDouble(elm.getAttribute("thresholdlow"));
							thresholdHigh = Double.parseDouble(elm.getAttribute("thresholdhigh"));
							text = elm.getTextContent();
							//CONTROL D'ERRORS (VALORS INVALIDS)
							if (thresholdLow.intValue() > thresholdHigh.intValue()) {
								System.out.println("\nERROR: Threshold_low_cant_be_greater_than_threshold_high");
								view.showErrorPopup("Sensor("+id+") minimum threshold can't be greater than maximum");
								return 1;
							}
							//CREACIO COMPONENT
							CSensor nSensor = new CSensor();
							nSensor.setId(id);
							nSensor.setUnit(unit);
							nSensor.setThresholdLow(thresholdLow.intValue());
							nSensor.setThresholdHigh(thresholdHigh.intValue());
							nSensor.setText("-- "+unit);
							nSensor.setBorder(BorderFactory.createTitledBorder(text));
							nSensor.setVisible(true);
							ncontrolBlock.add(nSensor);
							break;
						
						default:
							System.out.println("\nERROR: Unknown_control_type"
											 + "\nControl type: " + elm.getNodeName());
							view.showErrorPopup("Unknown control type (" + elm.getNodeName() + ")");
							return 1;
					}//switch
				}//if node es element
				
			}//for per a cada component
			controls.add(ncontrolBlock);//Insercio del bloc de controls al model
		}//for per cada bloc de components
		
		//TO REMOVE Impressio del model de dades resultant
		System.out.println("\nGENERATED DATA MODEL REPRESENTATION");
		for (int a = 0 ; a < controls.size() ; a++) {
			System.out.println("\nBloc: " + controls.get(a).getName());
			for (int b = 0 ; b < controls.get(a).size() ; b++) {
				System.out.println(controls.get(a).get(b).toString());
			}
		}
		System.out.println("DATA MODEL FINISHED PRINTING\n");
		return 0;
	}// m carregarConfiguracio

	public void setFile(File File) {//Estableix el fitxer a on es troben les dades de l'aplicacio
		file = File;
	}// m setFile
	
	public ArrayList<ControlsBlock> getControls() {
		return controls;
	}//m getControls
	
}





//BLOC DE CONTROLS

class ControlsBlock extends ArrayList<Object> {
	private String name;
	public void setName(String name) {this.name = name;}
	public String getName() {return name;}
}

//CLASES 'CUSTOM CONTROL' (cClasecontrol) Components grafics personalitzats

class CSwitch extends JToggleButton{
	private int id;
	public void setId(int id) {this.id = id;}
	public int getId() {return id;}
	
	public String toString() {return "Id: " + id + " | Default: " + this.isSelected() + " | Text: " + this.getText();}
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
	
	public String toString() {return "Id: " + id + " | Default: " + this.getValue() + " | Min: " + this.getMinimum() + " | Max: " + this.getMaximum() + " | Step: " + this.getMinorTickSpacing() + " | (SENSE APLICAR CONVERSIONFACTOR: /" + this.conversionFactor + ")";}
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
		String[] optionNames = new String[dropdownOptions.length];
		for (int a = 0; a < optionNames.length ; a++) {
			optionNames[a] = dropdownOptions[a].getText();
		}
		this.setModel(new DefaultComboBoxModel<String>(optionNames));
	}
	public CDropdownOption[] getOptions() {
		return dropdownOptions;
	}
	public String toString() {
		String OptionsToString = "";
		for (int a = 0 ; a < dropdownOptions.length ; a++) {
			OptionsToString += "\n - Option index: " + a + " | " + dropdownOptions[a].toString();
		}
		return "Id: " + id + " | Default: " + this.getSelectedIndex() + " | " + OptionsToString;}
}
class CDropdownOption {
	private int optionId;
	public void setOptionId(int id){this.optionId = id;}
	public int getOptionId(){return optionId;}
	
	private String text;
	public void setText(String text){this.text = text;}
	public String getText(){return text;}
	
	public String toString() {return "Id: " + optionId + " | Text: " + text;}
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
		return "Id: " + id + " | Unit: " + unit + " | Treshold Low: " + thresholdLow + " | Treshold High: " + thresholdHigh + " | Text: " + this.getText();
	}
}
