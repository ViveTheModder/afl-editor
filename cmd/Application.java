package cmd;
//AFL Editor v1.2 - GUI
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
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class Application 
{
	static JProgressBar nameProgBar;
	private static File src;
	private static JFrame frame;
	private static JDialog loading;
	private static final Image ICON = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("img/icon.png"));
	private static final String HTML_TEXT_1 = "<html><div style='font-family: Tahoma, Geneva, sans-serif; font-weight: bold; font-size: 14px;'>";
	private static final String HTML_TEXT_2 = "<html><div style='font-family: Tahoma, Geneva, sans-serif; font-weight: bold; font-size: 14px; text-align: center;'>";
	private static final String HTML_TEXT_TIP = "<html><div style='font-family: Tahoma, Geneva, sans-serif; font-size: 12px; text-align: center;'>";
	private static final String HTML_TEXT_TITLE = "<html><div style='font-family: Tahoma, Geneva, sans-serif;  font-weight: bold; font-size: 20px; color: #20b2aa;'>";
	private static final String WINDOW_TITLE = "AFL Editor v1.2";
	private static String[] argsFromGUI = new String[3];
	public static void setProgress() throws IOException
	{
		File[] pathList = src.listFiles((dir, name) -> (name.toLowerCase().endsWith(".afl")));
		if (pathList.length==0)
		{
			JOptionPane.showMessageDialog(null, HTML_TEXT_1+"The provided AFL directory contains no AFL files!", WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
		}
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
			fileProgBar.setFont(new Font("Tahoma", Font.BOLD, 14));
			fileProgBar.setMinimumSize(new Dimension(256,16));
			fileProgBar.setMaximumSize(new Dimension(256,16));
			fileProgBar.setPreferredSize(new Dimension(256,16));
			nameProgBar.setValue(0);
			nameProgBar.setStringPainted(true);
			nameProgBar.setBorderPainted(true);
			nameProgBar.setFont(new Font("Tahoma", Font.BOLD, 14));
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
				protected Void doInBackground() throws Exception 
				{
					long start = System.currentTimeMillis();
					RandomAccessFile[] aflList = new RandomAccessFile[pathList.length];
					fileProgBar.setMaximum(aflList.length);
					for (int i=0; i<aflList.length; i++)
						aflList[i] = new RandomAccessFile(pathList[i],"rw");
					for (int i=0; i<aflList.length; i++)
					{
						Main.afl = aflList[i];
						String aflName = pathList[i].getName();
						if (Main.isValidAFL()) 
						{
							
							fileLabel.setText(HTML_TEXT_2+"Working on:<br>"+aflName+"<br>");
							Main.fileNameCnt=0;
							nameProgBar.setValue(0);
							nameProgBar.setMaximum(Main.fileNameTotal);
							Main.writeAFL(argsFromGUI);
						}
						else fileLabel.setText(HTML_TEXT_2+"Skipping:<br>"+aflName+"<br>");
						fileProgBar.setValue(i+1);
					}
					long finish = System.currentTimeMillis();
					double time = (finish-start)/(double)1000;
					loading.setVisible(false); loading.dispose();
					JOptionPane.showMessageDialog(null, HTML_TEXT_1+"AFL files have been overwritten successfully in "+time+" seconds!", 
					WINDOW_TITLE, JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
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
		JButton btn = new JButton(HTML_TEXT_1+"Apply Changes");
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
		replaceAllBtn.setToolTipText(HTML_TEXT_TIP+"Replaces all instances of the string for each file name in the AFL.");
		replaceFirstBtn.setToolTipText(HTML_TEXT_TIP+"Replaces only the first instance of the string for each file name in the AFL.");
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
		btn.addActionListener(new ActionListener()
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
						setProgress();
					} 
					catch (Exception e1) 
					{
						loading.setVisible(false); loading.dispose();
						Main.error(e1);
					}
				}
				else JOptionPane.showMessageDialog(null, HTML_TEXT_1+msg, WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
			}
		});
		//add components
		replaceBtnGrp.add(replaceAllBtn); replaceBtnGrp.add(replaceFirstBtn);
		panel.add(titleLabel);
		panel.add(findLabel); panel.add(findField);
		panel.add(replaceLabel); panel.add(replaceField);
		dirBox.add(dirLabel); dirBox.add(dirLabelAsBtn);
		panel.add(dirBox);
		panel.add(replaceAllBtn); panel.add(replaceFirstBtn);
		panel.add(btn);
		//set frame settings
		frame.add(panel);
		frame.setLayout(new GridBagLayout());
		frame.setSize(384,384);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(ICON);
		frame.setLocationRelativeTo(null); //set location to center of the screen
		frame.setVisible(true);
	}
}