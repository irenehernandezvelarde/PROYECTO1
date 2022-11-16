import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class View extends JFrame {

	private JPanel contentPane;
	private Model model = new Model();
	
	public static void main(String[] args) {
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
	}
	public View() {
		//CONFIG JFRAME
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500, 500);
		this.setLocationRelativeTo(null);
		
		//CONFIG CONTENTPANE
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(4,5,0,0));
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
					
					//CONFIG GUI
					Border blackline = BorderFactory.createLineBorder(Color.black);
				    
					JPanel panelSwitches = new JPanel();
					panelSwitches.setLayout(new BoxLayout(panelSwitches,BoxLayout.Y_AXIS));
					//panelSwitches.setBorder(blackline);
					
					JPanel panelSliders = new JPanel();
					panelSliders.setLayout(new BoxLayout(panelSliders,BoxLayout.Y_AXIS));
					//panelSliders.setBorder(BorderFactory.createLineBorder(Color.black));
					
					JPanel panelComboBoxs = new JPanel();
					panelComboBoxs.setLayout(new BoxLayout(panelComboBoxs,BoxLayout.Y_AXIS));
			        
					JPanel panelLabels = new JPanel();
					panelLabels.setLayout(new BoxLayout(panelLabels,BoxLayout.Y_AXIS));
					contentPane.add(panelSliders);
			        contentPane.add(panelComboBoxs);

			        contentPane.add(panelSwitches);
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
	
	public void showErrorPopup(String errorMessage) {
		JOptionPane errorPopup = new JOptionPane();
		errorPopup.showMessageDialog(this, errorMessage);
	}
	
}
