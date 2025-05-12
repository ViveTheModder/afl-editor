package cmd;
//AFL Editor v1.4 - GUI
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

public class Application 
{
	static JProgressBar nameProgBar;
	private static File src;
	private static JFrame frame;
	private static JDialog loading;
	private static final Font BOLD = new Font("Tahoma", Font.BOLD, 14);
	private static final Font PLAIN = new Font("Tahoma", Font.PLAIN, 14);
	private static final Toolkit DEF_TOOLKIT = Toolkit.getDefaultToolkit();
	private static final Image ICON = DEF_TOOLKIT.getImage(ClassLoader.getSystemResource("img/icon.png"));
	private static final String HTML_HEADER = "<html><div style='font-family: Tahoma, Geneva, sans-serif; ";
	private static final String HTML_TEXT_1 = HTML_HEADER+"font-weight: bold; font-size: 14px;'>";
	private static final String HTML_TEXT_2 = HTML_HEADER+"font-weight: bold; font-size: 14px; text-align: center;'>";
	private static final String HTML_TEXT_BTN = HTML_TEXT_1.replace("font-weight: bold; ", "");
	private static final String HTML_TEXT_TIP = HTML_HEADER+"font-size: 12px; text-align: center;'>";
	private static final String HTML_TEXT_TITLE = HTML_HEADER+"font-weight: bold; font-size: 20px; color: #20b2aa;'>";
	private static final String WINDOW_TITLE = "AFL Editor v1.4";
	private static String[] argsFromGUI = new String[3];
	
	private static void errorBeep()
	{
		Runnable runWinErrorSnd = (Runnable) DEF_TOOLKIT.getDesktopProperty("win.sound.exclamation");
		if (runWinErrorSnd!=null) runWinErrorSnd.run();
	}
	private static void setDisplayNames(JButton displayBtn) throws IOException
	{
		File[] pathList = src.listFiles((dir, name) -> (name.toLowerCase().endsWith(".afl")));
		if (pathList.length==0)
			JOptionPane.showMessageDialog(null, HTML_TEXT_BTN+"The provided AFL directory contains no AFL files!", WINDOW_TITLE, 0);
		else
		{
			int[] invalidAflIndices = new int[pathList.length], aflNameCnts = new int[pathList.length];
			String[] aflNames = new String[pathList.length];
			String[][] allFileNames = new String[pathList.length][65535];
			RandomAccessFile[] afls = new RandomAccessFile[pathList.length];
			for (int i=0; i<pathList.length; i++)
				afls[i] = new RandomAccessFile(pathList[i],"rw");
			for (int i=0; i<pathList.length; i++) //go through AFLs and only get the file names of correct ones
			{
				Main.afl = afls[i];
				if (Main.isValidAFL()) 
				{
					allFileNames[i] = Main.getFileNames();
					aflNames[i] = pathList[i].getName();
					aflNameCnts[i] = Main.fileNameTotal;
					invalidAflIndices[i]=-1;
				}
				else invalidAflIndices[i]=i;
			}
			for (int i=0; i<pathList.length; i++) //shift a bunch of AFL-related info to effectivly skip faulty AFLs
			{
				if (invalidAflIndices[i]!=-1)
				{
					int indexOfInvalidAfl = invalidAflIndices[i];
					int[] newAflNameCnts = new int[aflNames.length-1];
					String[] newAflNames = new String[aflNames.length-1];
					RandomAccessFile[] newAfls = new RandomAccessFile[aflNames.length-1];
					//shift AFL names
					System.arraycopy(aflNames, 0, newAflNames, 0, invalidAflIndices[i]);
					System.arraycopy(aflNames, invalidAflIndices[i]+1, newAflNames, invalidAflIndices[i], aflNames.length-invalidAflIndices[i]-1);
					//shift AFL references
					System.arraycopy(afls, 0, newAfls, 0, invalidAflIndices[i]);
					System.arraycopy(afls, invalidAflIndices[i]+1, newAfls, invalidAflIndices[i], aflNames.length-invalidAflIndices[i]-1);
					//shift AFL name counts
					System.arraycopy(aflNameCnts, 0, newAflNameCnts, 0, invalidAflIndices[i]);
					System.arraycopy(aflNameCnts, invalidAflIndices[i]+1, newAflNameCnts, invalidAflIndices[i], aflNames.length-invalidAflIndices[i]-1);
					//apply shifted AFL names to the double array
					while (indexOfInvalidAfl+1<aflNames.length)
					{
						System.arraycopy(allFileNames[indexOfInvalidAfl+1], 0, allFileNames[indexOfInvalidAfl], 0, 65535);
						indexOfInvalidAfl++;
					}
					aflNames=newAflNames;
					afls=newAfls;
					aflNameCnts=newAflNameCnts;
				}
			}
			final RandomAccessFile[] aflCopies = afls;
			final int[] aflNameCntsCopy = aflNameCnts;
			//finally, some GUI code...
			displayBtn.setEnabled(false);
			JComboBox<String> dropdown = new JComboBox<String>(aflNames);
			DefaultTableModel model = new DefaultTableModel();
			model.addColumn("File Names",allFileNames[0]);
			JButton applyBtn = new JButton(HTML_TEXT_BTN+"Apply Changes");
			JFrame display = new JFrame();
			JLabel aflNamesLbl = new JLabel(HTML_TEXT_2+"Number of Files: "+aflNameCnts[0]);
			JPanel panel = new JPanel();
			JTable table = new JTable(model);
			JScrollPane pane = new JScrollPane(table);
			//set components
			dropdown.setFont(PLAIN);
			applyBtn.setToolTipText(HTML_TEXT_TIP+"If any file name contains more than 32 characters,<br>only its first 32 characters will be applied to the AFL.");
			table.setFont(PLAIN);
			table.getTableHeader().setFont(BOLD);
			table.setRowHeight(20);
			//add action listeners
			dropdown.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					int selIndex = dropdown.getSelectedIndex();
					Main.afl = aflCopies[selIndex];
					int rowCnt = allFileNames[selIndex].length;
					aflNamesLbl.setText(HTML_TEXT_2+"Number of Files: "+aflNameCntsCopy[selIndex]);
					model.setRowCount(rowCnt);
					for (int i=0; i<rowCnt; i++)
						model.setValueAt(allFileNames[selIndex][i], i, 0);
				}
			});
			display.addWindowListener(new WindowAdapter()
			{
				 @Override
			     public void windowClosing(WindowEvent e) 
				 {
					 displayBtn.setEnabled(true);
			     }
			});
			applyBtn.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e) 
				{
					try
					{
						int selIndex = dropdown.getSelectedIndex();
						String[] aflNamesFromModel = new String[aflNameCntsCopy[selIndex]];
						for (int i=0; i<aflNamesFromModel.length; i++)
						{
							String name = (String)model.getValueAt(i, 0);
							if (name.length()>32) name.substring(0, 32);
							aflNamesFromModel[i] = name;
						}
						Main.writeAflByRenaming(aflNamesFromModel);
						DEF_TOOLKIT.beep();
						JOptionPane.showMessageDialog(null, HTML_TEXT_BTN+"Any changes to the file names have been saved!", WINDOW_TITLE, 1);
					}
					catch (Exception ex)
					{
						errorBeep();
						JOptionPane.showMessageDialog(null, ex.getClass().getSimpleName()+": "+ex.getMessage(), "Exception", 0);
					}
				}
			});
			//add components
			panel.add(dropdown);
			panel.add(aflNamesLbl);
			panel.add(pane);
			panel.add(applyBtn);
			display.add(panel);
			//set dialog properties
			display.setTitle(WINDOW_TITLE);
			display.setLayout(new GridLayout(0,1));
			display.setSize(512,544);
			display.setResizable(false);
			display.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			display.setIconImage(ICON);
			display.setLocationRelativeTo(null); //set location to center of the screen
			display.setVisible(true);
		}
	}
	private static void setProgress(boolean isApplyBtnClicked) throws IOException
	{
		File[] pathList = src.listFiles((dir, name) -> (name.toLowerCase().endsWith(".afl")));
		if (pathList.length==0)
			JOptionPane.showMessageDialog(null, HTML_TEXT_BTN+"The provided AFL directory contains no AFL files!", WINDOW_TITLE, 0);
		else
		{
			frame.setVisible(false); frame.dispose();
			//change settings for all progress bars (must be done before declaring them)
		    UIManager.put("ProgressBar.background", Color.WHITE);
		    UIManager.put("ProgressBar.foreground", Color.GREEN);
		    UIManager.put("ProgressBar.selectionBackground", Color.BLACK);
		    UIManager.put("ProgressBar.selectionForeground", Color.BLACK);
			//initialize components
			loading = new JDialog();
			JProgressBar fileProgBar = new JProgressBar();
			nameProgBar = new JProgressBar();
			JPanel panel = new JPanel();
			JLabel fileLabel = new JLabel(HTML_TEXT_2+"Working on:<br>");
			JLabel fileProgLabel = new JLabel(HTML_TEXT_TIP+"Total AFL Progress:");
			JLabel nameProgLabel = new JLabel(HTML_TEXT_TIP+"Current AFL Progress:");
			//set component properties
			fileProgBar.setValue(0);
			fileProgBar.setStringPainted(true);
			fileProgBar.setBorderPainted(true);
			fileProgBar.setFont(BOLD);
			fileProgBar.setMinimumSize(new Dimension(256,16));
			fileProgBar.setMaximumSize(new Dimension(256,16));
			fileProgBar.setPreferredSize(new Dimension(256,16));
			nameProgBar.setValue(0);
			nameProgBar.setStringPainted(true);
			nameProgBar.setBorderPainted(true);
			nameProgBar.setFont(BOLD);
			nameProgBar.setMinimumSize(new Dimension(256,16));
			nameProgBar.setMaximumSize(new Dimension(256,16));
			nameProgBar.setPreferredSize(new Dimension(256,16));
			fileLabel.setHorizontalAlignment(SwingConstants.CENTER);
			fileProgLabel.setHorizontalAlignment(SwingConstants.CENTER);
			nameProgLabel.setHorizontalAlignment(SwingConstants.CENTER);
			panel.setLayout(new GridLayout(0,1));
			//add components
			panel.add(fileLabel);
			panel.add(fileProgLabel);
			panel.add(fileProgBar);
			panel.add(nameProgLabel);
			panel.add(nameProgBar);
			loading.add(panel);
			//set dialog properties
			loading.setTitle(WINDOW_TITLE);
			loading.setLayout(new GridBagLayout());
			loading.setSize(384,384);
			loading.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			loading.setIconImage(ICON);
			loading.setLocationRelativeTo(null); //set location to center of the screen
			loading.setVisible(true);
			
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>()
			{
				@Override //stripped down version of main method from Main class
				protected Void doInBackground() 
				{
					try
					{
						long start = System.currentTimeMillis();
						RandomAccessFile[] afls = new RandomAccessFile[pathList.length];
						fileProgBar.setMaximum(afls.length);
						for (int i=0; i<afls.length; i++)
							afls[i] = new RandomAccessFile(pathList[i],"rw");
						for (int i=0; i<afls.length; i++)
						{
							Main.afl = afls[i];
							String aflName = pathList[i].getName();
							if (Main.isValidAFL()) 
							{
								fileLabel.setText(HTML_TEXT_2+"Working on:<br>"+aflName+"<br>");
								Main.fileNameCnt=0;
								nameProgBar.setValue(0);
								nameProgBar.setMaximum(Main.fileNameTotal);
								if (isApplyBtnClicked) Main.writeAfl(argsFromGUI);
								else Main.fixDuplicateFileNames();
							}
							else fileLabel.setText(HTML_TEXT_2+"Skipping:<br>"+aflName+"<br>");
							fileProgBar.setValue(i+1);
						}
						long finish = System.currentTimeMillis();
						double time = (finish-start)/(double)1000;
						
						loading.setVisible(false); loading.dispose();
						DEF_TOOLKIT.beep();
						JOptionPane.showMessageDialog(null, HTML_TEXT_BTN+"AFL files have been overwritten successfully in "+time+" seconds!", WINDOW_TITLE, 1);
						System.exit(0);
					}
					catch (Exception e)
					{
						errorBeep();
						JOptionPane.showMessageDialog(null, e.getClass().getSimpleName()+": "+e.getMessage(), "Exception", 0);
					}
					return null;
				}
			};
			worker.execute();
		}
	}
	public static void setApplication() 
	{
		//initialize components
		frame = new JFrame(WINDOW_TITLE);
		JButton applyBtn = new JButton(HTML_TEXT_BTN+"Apply Changes");
		JButton displayBtn = new JButton(HTML_TEXT_BTN+"Display/Edit Names");
		JButton noDupliBtn = new JButton(HTML_TEXT_BTN+"Fix Duplicate Names");
		JPanel panel = new JPanel(new GridLayout(0,1));
		JLabel titleLabel = new JLabel(HTML_TEXT_TITLE+WINDOW_TITLE);
		JLabel findLabel = new JLabel(HTML_TEXT_1+"String to Find:");
		JLabel replaceLabel = new JLabel(HTML_TEXT_1+"String to Replace:");
		JLabel dirLabel = new JLabel(HTML_TEXT_1+"AFL Directory:");
		JLabel dirLabelAsBtn = new JLabel(" ");
		JTextField findField = new JTextField();
		JTextField replaceField = new JTextField();
		JRadioButton replaceAllBtn = new JRadioButton(HTML_TEXT_1+"Replace All");
		JRadioButton replaceFirstBtn = new JRadioButton(HTML_TEXT_1+"Replace First");
		ButtonGroup replaceBtnGrp = new ButtonGroup();
		Box dirBox = Box.createHorizontalBox();
		Image glass = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("img/glass.png"));
		glass = glass.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
		ImageIcon glassIcon = new ImageIcon(glass);
		//set component settings
		dirLabelAsBtn.setIcon(glassIcon);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		findLabel.setHorizontalAlignment(SwingConstants.CENTER);
		replaceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		dirLabel.setHorizontalAlignment(SwingConstants.CENTER);
		findField.setHorizontalAlignment(SwingConstants.CENTER);
		findField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		replaceField.setHorizontalAlignment(SwingConstants.CENTER);
		replaceField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		//set tooltips
		displayBtn.setToolTipText(HTML_TEXT_TIP+"Opens up a dialog displaying all the file names from the AFL (which can also be edited).");
		replaceAllBtn.setToolTipText(HTML_TEXT_TIP+"Replaces all instances of the string for each file name in the AFL.");
		replaceFirstBtn.setToolTipText(HTML_TEXT_TIP+"Replaces only the first instance of the string for each file name in the AFL.");
		noDupliBtn.setToolTipText(HTML_TEXT_TIP+"Adds the file ID at the end of each duplicate file name,<br>since AFS Explorer overwrites files with the same name.");
		titleLabel.setToolTipText(HTML_TEXT_TIP+"Made by ViveTheModder.");
		//make label behave like button via mouse listener
		dirLabelAsBtn.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = chooser.showOpenDialog(chooser);
				if (result==0) 
				{
					dirLabel.setText(HTML_TEXT_1.replace("'>", "color: #3cb371;'>")+"AFL Directory: ");
					src = chooser.getSelectedFile();
				}
			}
		});
		//assign replace argument by clicking either radio button
		replaceAllBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				argsFromGUI[0]="-ra";
			}
		});
		replaceFirstBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				argsFromGUI[0]="-rf";
			}
		});
		applyBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				boolean[] invalidArgs = new boolean[3];
				int validArgCnt=0;
				String msg="";
				argsFromGUI[1]='"'+findField.getText()+'"';
				argsFromGUI[2]='"'+replaceField.getText()+'"';
				if (argsFromGUI[0]==null)
				{
					invalidArgs[0]=true;
					msg+="Replacement type has not been specified!<br>";
				}
				if (findField.getText().equals("") || replaceField.getText().equals(""))
				{
					invalidArgs[1]=true;
					msg+="Strings to find or replace are required!<br>";
				}
				if (src==null) 
				{
					invalidArgs[2]=true;
					msg+="No AFL directory has been selected!<br>";
				}
				for (int i=0; i<3; i++)
				{
					if (!invalidArgs[i]) validArgCnt++;
				}
				if (validArgCnt==3)
				{
					try 
					{
						setProgress(true);
					} 
					catch (Exception e1) 
					{
						loading.setVisible(false); loading.dispose();
						e1.printStackTrace();
					}
				}
				else 
				{
					errorBeep();
					JOptionPane.showMessageDialog(null, HTML_TEXT_BTN+msg, WINDOW_TITLE, 0);
				}
			}
		});
		displayBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					if (src==null) 
					{
						errorBeep();
						JOptionPane.showMessageDialog(null, HTML_TEXT_BTN+"No AFL directory has been selected!", WINDOW_TITLE, 0);
					}
					else 
					{
						setDisplayNames(displayBtn);
					}
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
		});
		noDupliBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					if (src==null) 
					{
						errorBeep();
						JOptionPane.showMessageDialog(null, HTML_TEXT_BTN+"No AFL directory has been selected!", WINDOW_TITLE, 0);
					}
					else setProgress(false);
				} 
				catch (IOException e1) 
				{
					loading.setVisible(false); loading.dispose();
					e1.printStackTrace();
				}
			}
		});
		//add components
		replaceBtnGrp.add(replaceAllBtn); 
		replaceBtnGrp.add(replaceFirstBtn);
		panel.add(titleLabel);
		panel.add(findLabel); 
		panel.add(findField);
		panel.add(replaceLabel); 
		panel.add(replaceField);
		dirBox.add(dirLabel); 
		dirBox.add(dirLabelAsBtn);
		panel.add(dirBox);
		panel.add(replaceAllBtn); 
		panel.add(replaceFirstBtn);
		panel.add(Box.createHorizontalGlue());
		panel.add(displayBtn);
		panel.add(applyBtn);
		panel.add(noDupliBtn);
		//set frame settings
		frame.add(panel);
		frame.setLayout(new GridBagLayout());
		frame.setSize(384,512);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(ICON);
		frame.setLocationRelativeTo(null); //set location to center of the screen
		frame.setVisible(true);
	}
}