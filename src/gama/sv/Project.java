package gama.sv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class Project {

	// ====================== Instance stuff ============

	private String text;
	private boolean has_err;

	public Project() {
		has_err = false;
	}
	public Project(String input) {
		text = input;
		has_err = false;
	}
	public boolean isSuccess() { return has_err; }
	public void setText(String text) {
		this.text = text;		
	}
	public String transformLines() {
		String[] lines = text.split("\n");
		String finale = "";
		int i = 0;
		while (i < lines.length) {
			String line = lines[i];
			String transformed = transformLine(line);
			finale += transformed;
			i++;
		}

		// System.out.println("" + finale);
		return finale;
	}
	private String transformLine(String line) {
		String _line = "";
		if (line.length() == 0)
			return ""; 
		char first = line.charAt(0);
		switch (first) {
			case '>':
				_line = transformSpecial(line);
				break;
			case '#':
				_line = transformHeader(line);
				break;
			case '^':
				_line = transformUnOrdered(line);
				break;
			default:
				_line = transformWords(line);
				break;
		}
		return _line;
	}
	private String transformWords(String line)  {
		String _line = ""; 
		String sp = " ";
		for (String word: line.split(" ")) {
			String _word = transformWord(word);
			_line += _word + sp;
		}
		return _line; 
	}
	private String transformWord(String line) {
		String _line = "";
		if (line.length() == 0)
			return line; 

		char first = line.charAt(0);
		switch (first) {
			case '*':
				_line = transformStar(line);
				break;
			case '&':
				_line = transformItalics(line);
				break;
			case '[':
				_line = transformLink(line);
				break;
			case '$':
				_line = transformUnderline(line);
				break;
			case '(':
				_line = transformImage(line);
				break;
			case '{':
				_line = transformTemplate(line);
				break;
			default:
				_line = line;
				break;
		}
		return _line;
	}
	private String transformTemplate(String line) {
		int size = line.length();
		String rest = line.substring(1, size);

		int index = rest.indexOf('}');
		if (index != -1) {
			rest = takeUnitl(rest, '}');
		} else {
			has_err = true;
		}

		File fp = new File(rest);
		if (!fp.exists())
			return "<p style=\"color: red;\"> File: `" + rest + "` is not found </p><br/>";

		String out = Project.readFile(rest);
		Project pr = new Project(out); 
		out = pr.transformLines();
		System.out.println("Inject: " + rest);
		return out;
	}
	private String transformUnderline(String line) {
		int size = line.length();
		String rest = line.substring(1, size);

		int index = rest.indexOf('$');
		if (index != -1) {
			rest = takeUnitl(rest, '$');
		} else {
			has_err = true;
		}
		return "<u>".concat(transformWords(rest)).concat("</u>");
	}
	private String transformImage(String line) {
		int size = line.length();
		String rest = line.substring(1, size);

		int index = rest.indexOf(')');
		if (index != -1) {
			rest = takeUnitl(rest, ')');
		} else {
			has_err = true;
		}

		String[] splits = rest.split(",");
		String alt = splits[0].trim(); 
		String href = "#"; 
		String width = "";
		String height = "";
		if (splits.length >= 2) 
			href = splits[1].trim(); 
		
		if (splits.length >= 3)
			width = "width=\"".concat(splits[2].trim()).concat("px\"");

		if (splits.length >= 4)
			height = "width=\"".concat(splits[3].trim()).concat("px\"");

		return "<img src=\"" + href +"\" alt=\"" + alt + "\" " + width + " " + height + " />";
	}
	private String transformLink(String line) {
		int size = line.length();
		String rest = line.substring(1, size);

		int index = rest.indexOf(']');
		if (index != -1) {
			rest = takeUnitl(rest, ']');
		} else {
			has_err = true;
		}

		String[] splits = rest.split(",");
		String text = splits[0].trim(); 
		String href = "#"; 
		if (splits.length > 1) 
			href = splits[1].trim(); 
		
		return "<a ".concat("href=\"").concat(href).concat("\">").concat(text).concat("</a>");
	}
	private String transformItalics(String line) {
		int size = line.length();
		String rest = line.substring(1, size);

		int index = rest.indexOf('&');
		if (index != -1) {
			rest = takeUnitl(rest, '&');
		} else {
			has_err = true;
		}

		return "<i>".concat(transformWords(rest)).concat("</i>");
	}
	private String transformStar(String line) {
		int size = line.length();
		String rest = line.substring(1, size);

		int index = rest.indexOf('*');
		if (index != -1) {
			rest = takeUnitl(rest, '*');
		} else {
			has_err = true;
		}
		return "<b>".concat(transformWords(rest)).concat("</b>");
	}
	private String transformUnOrdered(String line) {
		int count = countChars(line, '^');
		int size = line.length();
		if (count > 0) {
			has_err = true;
		}

		String rest = line.substring(count, size);
		return "<ul><li>".concat(transformWords(rest)).concat("</li></ul>\n"); 
	}
	private String transformSpecial(String line) {
		int count = countChars(line, '>');
		int size = line.length();
		if (count > 0) {
			has_err = true;
		}

		String rest = line.substring(count, size);
		String special = _transformSpecial(rest, count - 1, 0);
		return special; 
	}
	private String _transformSpecial(String line, int start, int end) {

		if (start == 0) {
			return "<div style=\"padding-left: 3px; border-left: 3px solid black\">".concat(transformWords(line)).concat("</div>\n");
		}

		return "<div style=\"padding-left: 3px; border-left: 3px solid black\">".concat(_transformSpecial(line, start - 1, end)).concat(" </div>\n");
	}
	private String transformHeader(String line) {
		int count = countChars(line, '#');
		int size = line.length();
		if (count > 0) {
			has_err = true;
		}

		String rest = line.substring(count, size);
		String h = "h" + count;
		
		return "<"
						.concat(h)
						.concat(">")
						.concat(transformWords(rest))
						.concat("</")
						.concat(h)
						.concat(">\n");
	}
	public int countChars(String input, char c) {
		int count = 0;
		int index = 0;
		while (index < input.length() && input.charAt(index++) == c) {
			count++;
		}
		return count;
	}
	public String takeUnitl(String line, char last) {
		String text = ""; 
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i); 
			if (c != last) 
				text += c; 
			else break;
		}
		return text;
	}


	// ===================== Static stuff==========================
	private static String rootdir;
	private static ArrayList<String> files;
	private static ArrayList<String> recent;
	private static String projectName; 
	private static String temp_dir; 
	private static String project_path;

	public static void setTemp_dir(String temp_dir) {
		Project.temp_dir = temp_dir;
	}

	public static String getTemp_dir() {
		return temp_dir;
	}
	
	public static void setProjectName(String name) { projectName = name; }
	public static String getProjectName() { return projectName; }
	public static void init()  {
		if (files == null)
			files = new ArrayList<>();
	
		if (recent == null)
			recent = new ArrayList<>();
	}
	public static ArrayList<String> getFiles() { return files; }
	public static void addToProjectIfNotThere(String path) {
		if (files.contains(path)) 
			return; 
		files.add(path);
	}
	public static String getRoot() {
		return rootdir;
	}

	public static void setRoot(String rt) {
		rootdir = rt;
	}

	public static void addPathToRecentFiles(String dest, String text_path) {

		for (String file: recent) {
			if (file == text_path) {
				return; 
			}
		}
		System.err.println("Rec: " + dest);
		recent.add(text_path);
		String text = readFile(dest); 
		text += text_path + "\n";
		writeFile(dest, text);
	}
	public static String getExtension(String path) {
		String pt = path.substring(1);
		int size = pt.length();
		
		int index = pt.indexOf(".");
		if (index < 0)
			return ""; 

		String extact = pt.substring(index,size);  

		return extact;
	}
	public static boolean hasExtension(String path, String ext) {
		String pt = path.substring(1);
		int size = pt.length();
		
		int index = pt.indexOf(".");
		if (index < 0)
			return false;
		String extact = pt.substring(index,size);  

		return extact.toLowerCase().equals(ext);
	}
	public static boolean writeFile(String path, String contents) {
		createFileIfNotExist(path);
		try {
			FileWriter writer = new FileWriter(path);
			writer.write(contents);
			writer.close();
			return true;
		} catch (IOException ex) {
			Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}
	public static void createFileIfNotExist(String path) {
		File fp = new File(path);
		if (!fp.exists())
			try {
				fp.createNewFile();
		} catch (IOException ex) {
			Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	public static String readFile(String path) {
		String text = ""; 
		Scanner sc = null;
		try {
			sc = new Scanner(new File(path));
		} catch (Exception ex) {
			return "System error: File `" + path + "` might have been deleted from the project";
		}

		while (sc.hasNextLine()) 
			text += sc.nextLine() + "\n";
		return text;
	}
	public static String makePath(Object[] splits) {
		String delim = "/"; 
		String path = ""; 
		for (int i = 0; i < splits.length; i++) {
			path += splits[i].toString();
			if (i < splits.length - 1)
				path += delim; 
		}

		System.out.println("gama.sv.Utils.makePath(): " + path);
		return path;
	}
	public static void loadDirectory(JTree tree, String file) {
		File fp = new File(file);
		if (!fp.exists()) {
			System.err.println("error: " + file + " does not extit");
			return;
		}

		if (fp.isFile()) {
			return;
		}

		if (fp.isDirectory()) {
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			DefaultMutableTreeNode top = new DefaultMutableTreeNode(fp.getName());
			model.setRoot(top);
			tree.setModel(model);
			createNodes(top, fp);
		}
	}
	public static void createNodes(DefaultMutableTreeNode top, File fp) {
		DefaultMutableTreeNode folder = null;
		DefaultMutableTreeNode filed = null;

		File[] files = fp.listFiles();

		if (files == null)
			return;
		
		for (File file : files) {
			if (file.isFile()) {
				String path = file.getPath();
				if (Project.hasExtension(path, ".md") || Project.hasExtension(path, ".html") || Project.hasExtension(path, ".txt")) {
					DefaultMutableTreeNode f = new DefaultMutableTreeNode(file.getName());
					top.add(f);
				}
			} else {
				DefaultMutableTreeNode dir = new DefaultMutableTreeNode(file.getName());
				createNodes(dir, file);
				top.add(dir);
			}
		}
	}

}
