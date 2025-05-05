package cmd;
//AFL Editor v1.3 - CMD
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import javax.swing.UIManager;

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
	public static int getNumberOfDigits(int num)
	{
		int cnt=0;
		while (num!=0)
		{
			num/=10;
			cnt++;
		}
		return cnt;
	}
	public static void fixDuplicateFileNames() throws IOException
	{
		int fileCnt, fileNameTotalCopy = fileNameTotal;
		ArrayList<String> fileNameList = new ArrayList<String>();
		afl.seek(16); //skip header
		for (int i=0; i<afl.length()-16; i+=32)
		{
			byte[] fileNameArr = new byte[32];
			afl.readFully(fileNameArr);
			afl.seek(afl.getFilePointer()-32);
			String fileName = new String(fileNameArr, StandardCharsets.ISO_8859_1);
			if (!fileNameList.contains(fileName)) fileNameList.add(fileName);
			else 
			{
				fileCnt=i/32;
				int fileCntDigits = getNumberOfDigits(fileNameTotalCopy);
				int currentDigits, numberOfZeroes=0;
				currentDigits = getNumberOfDigits(fileCnt);
				numberOfZeroes = fileCntDigits-currentDigits;
				fileName = fileName.replace("\0", "");
				fileName+=" (F";
				for (int j=0; j<numberOfZeroes; j++) fileName+=0;
				if (fileCnt!=0) fileName+=fileCnt;
				fileName+=")";
			}
			
			if (fileName.length()>32) fileName=fileName.substring(0, 32);
			fileNameArr = fileName.getBytes(StandardCharsets.ISO_8859_1);
			if (fileNameArr.length<32) //rarely happens, but if it does, set array size to 32 by copying everything over to array with zeroes
			{
				byte[] temp = fileNameArr;
				fileNameArr = new byte[32];
				for (int j=0; j<32; j++) fileNameArr[j]=0;
				System.arraycopy(temp, 0, fileNameArr, 0, temp.length);
			}
			if (fileNameArr[31]!=0) fileNameArr[31]=0; //this serves as a footer
			afl.write(fileNameArr);
			
			if (Application.nameProgBar!=null)
			{
				if (fileName.contains(" (F") && fileName.endsWith(")"))
				{
					fileNameCnt++; //only increase counter when the method actually does something
					Application.nameProgBar.setValue(fileNameCnt);
				}
				else 
				{
					fileNameTotal--; //otherwise, don't increase it, but decrease maximum file count instead
					Application.nameProgBar.setMaximum(fileNameTotal);
				}
			}
		}
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
			if (fileNameArr.length<32) //rarely happens, but if it does, set array size to 32 by copying everything over to array with zeroes
			{
				byte[] temp = fileNameArr;
				fileNameArr = new byte[32];
				for (int j=0; j<32; j++) fileNameArr[j]=0;
				System.arraycopy(temp, 0, fileNameArr, 0, temp.length);
			}
			if (fileNameArr[31]!=0) fileNameArr[31]=0; //this serves as a footer
			afl.write(fileNameArr);
		}
	}
	public static void argCheck(String[] args)
	{
		String[] argTypes = {"-ra","-rf","-fd"};
		if (args.length>0 && args.length<4)
		{
			if (args.length==1)
			{
				if (args[0].equals("-h"))
				{
					System.out.println
					("Edit AFL files by finding & replacing strings from file names."
					+ "\nThe program can take the following arguments:\n"
					+ "args[0]: -ra (Replace All), -rf (Replace First), -fd (Fix Duplicates)\n"
					+ "args[1]: Insert string to find, preferably without spaces or between quotes.\n"
					+ "args[2]: Insert string to replace, preferably without spaces or between quotes.\n\n"
					+ "Usage: java -jar afl-editor.jar args[0] args[1] args[2]");
					System.exit(0);
				}
				else if (args[0].equals("-fd"));
				else
				{
					System.out.println("Invalid argument! It must be either -h or -fd.");
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
			catch (Exception e1) 
			{
				e1.printStackTrace();
			}
		}
		else
		{
			System.out.println("Invalid number of arguments provided ("+args.length+")!");
			System.exit(2);
		}
	}
	public static void main(String[] args) 
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
			try
			{
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
						if (args[0].equals("-fd"))
						{
							System.out.println("Fixing duplicate names in "+aflName+"...");
							fixDuplicateFileNames();
						}
						else
						{
							System.out.println("Finding & replacing names in "+aflName+"...");
							writeAFL(args);
						}
					}
					else System.out.println("Skipping faulty AFL: "+aflName+"!");
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			long finish = System.currentTimeMillis();
			double time = (finish-start)/(double)1000;
			System.out.println("Time: "+time+" s");
		}
	}
}