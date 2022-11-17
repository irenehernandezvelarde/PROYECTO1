import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class View extends JFrame {
	
	//VARIABLES
	private Model model = new Model(this);
	private JPanel contentPane;
	
	//METODE MAIN
	public static void main(String[] args) {
		try {
			Server.connecta();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					View frame = new View();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}// m main 
	
	//CONSTRUCTOR (PART IMPORTANT)
	public View() {
		
		//CONFIG JFRAME
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500, 500);
		this.setLocationRelativeTo(null);
		
		//CONFIG CONTENTPANE
		contentPane = new JPanel();
		setContentPane(contentPane);
		
		//CONFIG JMENUBAR
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		//Menu Arxiu
		JMenu menuArxiu = new JMenu("Arxiu");
		menuBar.add(menuArxiu);
		
		//Menu Visualitzacions
		JMenu menuVisualitzacions = new JMenu("Visualitzaci\u00F3ns");
		menuBar.add(menuVisualitzacions);
		
		//Item carregar configuracio
		JMenuItem itemCarregaConf = new JMenuItem("Carregar configuració");
		itemCarregaConf.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				
				//SELECCIO DEL FITXER
				JFileChooser fileChooser = new JFileChooser();
				//Establiment del directori de fileChooser a la carpeta de l'aplicació
				fileChooser.setCurrentDirectory(new File(String.valueOf(System.getProperty("user.dir") + "/")));
				//Seleccio fitxer
				int seleccion=fileChooser.showOpenDialog(contentPane);
				if(seleccion==JFileChooser.APPROVE_OPTION){
					System.out.println("File selected: " + fileChooser.getSelectedFile().getAbsolutePath());
					model.setFile(fileChooser.getSelectedFile());
				}
				
				//CARREGUEM DADES DEL FITXER
				if (model.carregarConfiguracio() == 0) {
					contentPane.removeAll();
					
					//CONFIGUREM LA GUI A PARTIR DE LES DADES
					contentPane.setLayout(new GridLayout(4,1,5,5));
				    
					JPanel panelSwitches = new JPanel();
					panelSwitches.setLayout(new BoxLayout(panelSwitches,BoxLayout.Y_AXIS));
					panelSwitches.setBorder(BorderFactory.createTitledBorder("SWITCHES"));
			        contentPane.add(panelSwitches);
					
					JPanel panelSliders = new JPanel();
					panelSliders.setLayout(new BoxLayout(panelSliders,BoxLayout.Y_AXIS));
					panelSliders.setBorder(BorderFactory.createTitledBorder("SLIDERS"));
					contentPane.add(panelSliders);
					
					JPanel panelComboBoxs = new JPanel();
					panelComboBoxs.setLayout(new BoxLayout(panelComboBoxs,BoxLayout.Y_AXIS));
					panelComboBoxs.setBorder(BorderFactory.createTitledBorder("DROPDOWNS"));
			        contentPane.add(panelComboBoxs);
			        
					JPanel panelLabels = new JPanel();
					panelLabels.setLayout(new BoxLayout(panelLabels,BoxLayout.Y_AXIS));
					panelLabels.setBorder(BorderFactory.createTitledBorder("SENSORS"));
			        contentPane.add(panelLabels);
			        //end GUI
					
					ArrayList<ControlsBlock> controls = model.getControls();
					
					for(ArrayList a: controls) {
						for (int b = 0; b<a.size(); b++) {
							System.out.println(a.get(b).getClass());
							switch(String.valueOf(a.get(b).getClass())) {
							case "class CSwitch":
								JToggleButton toggleButton = (JToggleButton) a.get(b);
								panelSwitches.add(toggleButton);
								break;
							case "class CSlider":
								CSlider n = (CSlider) a.get(b);
								panelSliders.add(n);
								break;
							case "class CDropdown":
								CDropdown combo =  (CDropdown) a.get(b);
								panelComboBoxs.add(combo);
						        break;
							case "class CSensor":
								CSensor label = (CSensor) a.get(b);
								panelLabels.add(label);
						        break;
							}
						}
					}
					contentPane.revalidate();
				}
			}});
		menuArxiu.add(itemCarregaConf);		
	}// c View
	
	//POPUP ERRORS
	public void showErrorPopup(String errorMessage) {
		JOptionPane.showMessageDialog(this, errorMessage);
	}//m showErrorPopup
	
}
