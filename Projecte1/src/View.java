import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
		this.setTitle("IETI Industry - By: the Work'o'matic team");
		this.setExtendedState(MAXIMIZED_BOTH);
		
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
					//PINTEM GUI
					loadGuiFromFile();
				}
			}});
		menuArxiu.add(itemCarregaConf);		
	}// c View
	
	//CARREGAR GUI DE FITXER
	public void loadGuiFromFile() {
		contentPane.removeAll();
		
		//CONFIGUREM LA GUI A PARTIR DE LES DADES
		contentPane.setLayout(new GridLayout(2,2,5,5));
		
		JScrollPane scroll;
	    
		JPanel panelSwitches = new JPanel();
		panelSwitches.setLayout(new BoxLayout(panelSwitches,BoxLayout.Y_AXIS));
		panelSwitches.setBorder(BorderFactory.createTitledBorder("SWITCHES"));
		scroll = new JScrollPane(panelSwitches);
        contentPane.add(scroll);
		
		JPanel panelSliders = new JPanel();
		panelSliders.setLayout(new BoxLayout(panelSliders,BoxLayout.Y_AXIS));
		panelSliders.setBorder(BorderFactory.createTitledBorder("SLIDERS"));
		scroll = new JScrollPane(panelSliders);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPane.add(scroll);
		
		JPanel panelDropdowns = new JPanel();
		panelDropdowns.setLayout(new BoxLayout(panelDropdowns,BoxLayout.Y_AXIS));
		panelDropdowns.setBorder(BorderFactory.createTitledBorder("DROPDOWNS"));
		scroll = new JScrollPane(panelDropdowns);
        contentPane.add(scroll);
        
		JPanel panelSensors = new JPanel();
		panelSensors.setLayout(new BoxLayout(panelSensors,BoxLayout.Y_AXIS));
		panelSensors.setBorder(BorderFactory.createTitledBorder("SENSORS"));
		scroll = new JScrollPane(panelSensors);
        contentPane.add(scroll);
        //end GUI
		
		ArrayList<ControlsBlock> controls = model.getControls();
		
		for(ArrayList a: controls) {
			for (int b = 0; b<a.size(); b++) {
				System.out.println(a.get(b).getClass());
				switch(String.valueOf(a.get(b).getClass())) {
				case "class CSwitch":
					CSwitch toggle = (CSwitch) a.get(b);
					panelSwitches.add(new titledPane(toggle));
					break;
				case "class CSlider":
					CSlider slider = (CSlider) a.get(b);
					slider.setSize(slider.getSize());
					panelSliders.add(new titledPane(slider));
					break;
				case "class CDropdown":
					CDropdown dropdown =  (CDropdown) a.get(b);
					panelDropdowns.add(new titledPane(dropdown));
			        break;
				case "class CSensor":
					CSensor sensor = (CSensor) a.get(b);
					panelSensors.add(new titledPane(sensor));
			        break;
				}
			}
		}
		contentPane.revalidate();
	}// m loadGuiFromFile
	
	//POPUP ERRORS
	public void showErrorPopup(String errorMessage) {
		JOptionPane.showMessageDialog(this, errorMessage);
	}//m showErrorPopup
	
}

class titledPane extends JPanel{
	titledPane(CSwitch component){
		this.setLayout(new GridLayout(2,1));
		
		JLabel title = new JLabel(component.getTitle());
		title.setSize(this.getWidth(),title.getHeight());
		this.add(title);
		
		this.add(component);
	}
	titledPane(CSlider component){
		this.setLayout(new GridLayout(2,1));
		
		JLabel title = new JLabel(component.getTitle());
		title.setSize(this.getWidth(),title.getHeight());
		this.add(title);
		
		this.add(component);
	}
	titledPane(CDropdown component){
		this.setLayout(new GridLayout(2,1));
		
		JLabel title = new JLabel(component.getTitle());
		title.setSize(this.getWidth(),title.getHeight());
		this.add(title);
		
		this.add(component);
	}
	titledPane(CSensor component){
		this.setLayout(new GridLayout(2,1));
		
		JLabel title = new JLabel(component.getTitle());
		title.setSize(this.getWidth(),title.getHeight());
		this.add(title);
		
		this.add(component);
	}
}
