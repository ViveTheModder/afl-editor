package cmd;
//AFL Editor v1.1 - CMD
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main 
{
	static int fileNameCnt, fileNameTotal;
	static RandomAccessFile afl;
	public static boolean isValidAFL() throws IOException
	{
		boolean aflError=false;
		long size = afl.length();
		int header1 = afl.readInt();
		long header2 = afl.readLong();
		fileNameTotal = LittleEndian.getInt(afl.readInt());
		if (header1!=0x41464C00) aflError=true; //header must always start with AFL
		if (header2!=0x01000000FFFFFFFFL) aflError=true;
		if ((fileNameTotal*32)==(size-16)) aflError=true;
		return aflError;
	}
	public static void writeAFL(String[] args) throws IOException
	{
		afl.seek(16); //skip header
		for (int i=0; i<afl.length()-16; i+=32)
		{
			byte[] fileNameArr = new byte[32];
			afl.readFully(fileNameArr);
			afl.seek(afl.getFilePointer()-32);
			String fileName = new String(fileNameArr, StandardCharsets.ISO_8859_1);
			
			args[1] = args[1].replace('"'+"", ""); //remove quotes
			args[2] = args[2].replace('"'+"", ""); //remove quotes
			if (args[0].equals("-rf")) fileName = fileName.replaceFirst(args[1], args[2]);
			else if (args[0].equals("-ra")) fileName = fileName.replace(args[1], args[2]);
						
			if (Application.nameProgBar!=null)
			{
				if (fileName.contains(args[2]))
				{
					fileNameCnt++; //only increase counter when replace method actually does something
					Application.nameProgBar.setValue(fileNameCnt);
				}
				else 
				{
					fileNameTotal--; //otherwise, don't increase it, but decrease maximum file count instead
					Application.nameProgBar.setMaximum(fileNameTotal);
				}
			}
			
			if (fileName.length()>32) fileName=fileName.substring(0, 32);
			fileNameArr = fileName.getBytes(StandardCharsets.ISO_8859_1);
			if (fileNameArr[31]!=0) fileNameArr[31]=0; //this serves as a footer
			afl.write(fileNameArr);
		}
	}
	public static void argCheck(String[] args)
	{
		String[] argTypes = {"-ra","-rf"}; //replace all, replace first
		if (args.length>0 && args.length<4)
		{
			if (args.length==1)
			{
				if (args[0].equals("-h"))
				{
					System.out.println
					("Edit AFL files by finding & replacing strings from file names."
					+ "\nThe program can take the following arguments:\n"
					+ "args[0]: -ra (Replace All), -rf (Replace First)\n"
					+ "args[1]: Insert string to find, preferably without spaces or between quotes.\n"
					+ "args[2]: Insert string to replace, preferably without spaces or between quotes.\n\n"
					+ "Usage: java -jar afl-editor.jar args[0] args[1] args[2]");
					System.exit(0);
				}
				else
				{
					System.out.println("Invalid argument! It must be -h, nothing else.");
					System.exit(1);
				}
			}
			else
			{
				if (!(args[0].equals(argTypes[0]) || args[0].equals(argTypes[1])))
				{
					System.out.println("Invalid argument! It must be either "+argTypes[0]+" or "+argTypes[1]+".");
					System.exit(1);
				}					
			}
		}
		else if (args.length==0) 
		{
			try 
			{
				if (System.getProperty("os.name").contains("Win")) UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				Application.setApplication();
			} 
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) 
			{
				//I prefer this means of logging over printStackTrace(), although it is not that necessary here
				File errorLog = new File("errors.log");
				try 
				{
					FileWriter logWriter = new FileWriter(errorLog,true);
					logWriter.append(new SimpleDateFormat("dd-MM-yy-hh-mm-ss").format(new Date())+":\n"+e1.getMessage()+"\n");
					logWriter.close();
				} 
				catch (IOException e2) 
				{
					e2.printStackTrace();
				}
			}
		}
		else
		{
			System.out.println("Invalid number of arguments provided ("+args.length+")!");
			System.exit(2);
		}
	}
	public static void error(Exception e1)
	{
		File errorLog = new File("errors.log");
		try 
		{
			FileWriter logWriter = new FileWriter(errorLog,true);
			logWriter.append(new SimpleDateFormat("dd-MM-yy-hh-mm-ss").format(new Date())+":\n"+e1.getMessage()+"\n");
			logWriter.close();
		} 
		catch (IOException e2) 
		{
			e2.printStackTrace();
		}
	}
	public static void main(String[] args) throws IOException 
	{
		argCheck(args);
		if (args.length!=0)
		{
			Scanner sc = new Scanner(System.in);
			File src = new File(".");
			while (true)
			{
				System.out.println("Enter a valid path containing AFL files:");
				String path = sc.nextLine();
				src = new File(path);
				if (src.isDirectory()) break;
			}
			sc.close();
			
			long start = System.currentTimeMillis();
			File[] pathList = src.listFiles((dir, name) -> (name.toLowerCase().endsWith(".afl")));
			RandomAccessFile[] aflList = new RandomAccessFile[pathList.length];
			for (int i=0; i<aflList.length; i++)
				aflList[i] = new RandomAccessFile(pathList[i],"rw");
			for (int i=0; i<aflList.length; i++)
			{
				afl = aflList[i];
				String aflName = pathList[i].getName();
				if (isValidAFL()) 
				{
					System.out.println("Finding & replacing names in "+aflName+"...");
					writeAFL(args);
				}
				else System.out.println("Skipping faulty AFL: "+aflName+"!");
			}
			long finish = System.currentTimeMillis();
			double time = (finish-start)/(double)1000;
			System.out.println("Time: "+time+" s");
		}
	}
}