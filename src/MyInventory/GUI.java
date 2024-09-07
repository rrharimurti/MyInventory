package MyInventory;

import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/*
 * Common Abbreviations:
 * -sh: second-hand
 * -bst: binary search tree
 */

//3-value data type for boolean filtering
enum BooleanPlus {
	TRUE,
	FALSE,
	NONE
}

//Revenue Calculation
class Revenue {
	private String path = "/Users/rasyaharimurti/eclipse-workspace/MyInventory/src/MyInventory/data/revenue.txt"; //save revenue in text file
	public void addRevenue(int sale) { //add to Revenue
		try {
			RandomAccessFile revenue = new RandomAccessFile(path, "rw");
			String s = revenue.readLine();
			if (s==null) { //if nothing on the file, set to default revenue (0)
				s = "0";
			}
			s = Parser.toString(sale + Parser.toInt(s)); //sale + old revenue = new revenue 
			revenue.setLength(0); //delete old revenue in text file
			GUI.write(path, s); //write new revenue
			revenue.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}

	}
	
	//return current revenue
	public int getRevenue() {
		try {
			RandomAccessFile revenue = new RandomAccessFile(path, "rw");
			String s = revenue.readLine();
			if (s==null) { //if nothing on the file, returns default revenue (0)
				s = "0";
			}
			revenue.close();
			return Parser.toInt(s); //returns revenue
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		throw new IllegalStateException("Revenue is Undefined."); //Illegal State Exception if revenue is undefined
	}
}

//Dynamic Stack of Double Arrays to represent Pages in the Inventory Table
class PageStack {
	private Node top; //top of the stack
	//Linked List Node Class to make Stack Dynamic
	private class Node {
		String[][] page; //a double array representing each page
		Node next; //next Node
		
		//constructor
		Node(String[][] lego) {
			this.page = lego;
		}
	}
	
	//Check if stack is empty
	public boolean isEmpty() {
		return top==null;
	}
	
	//push double array to the stack
	public void push(String[][] new_page) {
		Node new_node = new Node(new_page);
		new_node.next = top;
		top = new_node;
	}
	
	//pop double array from stack
	public String[][] pop() {
		if (isEmpty()) {
			throw new IllegalStateException("Error: Stack is Empty");
		}
		String[][] result = peek();
		top = top.next;
		return result;
	}

	//return size of the stack
	int size() {
		int size=0;
		Node temp = top;
		while (temp != null) {
			temp = temp.next;
			size++;
		}
		
		return size;
	}
	
	//peek top of stack
	public String[][] peek() {
		return top.page;
	}
}

//GUI class containing all major GUI elements
public class GUI extends JFrame {
	//declaring linked list for legos
	public LinkedList lego_list = new LinkedList();
	
	//Status variables for Sorting/Filtering
	static boolean curr_isDescending;
	static boolean curr_isYear;
	static String curr_theme;
	static BooleanPlus curr_box;
	static BooleanPlus curr_sh;
	
	//paths for RandomAccessFIle
	final String serial_no_path = "/Users/rasyaharimurti/eclipse-workspace/MyInventory/src/MyInventory/data/serialno.txt";
	final String set_name_path = "/Users/rasyaharimurti/eclipse-workspace/MyInventory/src/MyInventory/data/setname.txt";
	final String theme_path = "/Users/rasyaharimurti/eclipse-workspace/MyInventory/src/MyInventory/data/theme.txt";
	final String year_path = "/Users/rasyaharimurti/eclipse-workspace/MyInventory/src/MyInventory/data/year.txt";
	final String no_of_pieces_path = "/Users/rasyaharimurti/eclipse-workspace/MyInventory/src/MyInventory/data/pieces.txt";
	final String second_hand_path = "/Users/rasyaharimurti/eclipse-workspace/MyInventory/src/MyInventory/data/secondhand.txt";
	final String box_path = "/Users/rasyaharimurti/eclipse-workspace/MyInventory/src/MyInventory/data/box.txt";
	
	//fonts used in the app
	Font xl_font = new Font("Arial",Font.PLAIN,100);
	Font big_font = new Font("Arial",Font.PLAIN,40);
	Font medium_font = new Font("Arial",Font.PLAIN,30);
	Font small_font = new Font("Arial",Font.PLAIN,20);
	Font mini_font = new Font("Arial",Font.PLAIN,10);
	
	JFrame frame; //for confirmation message box
	
	//headings
	JLabel title;
	JLabel heading1;
	JLabel heading2;
	JLabel heading3;
	JLabel heading4;
	JLabel heading5;
	JLabel heading6;
	JLabel heading7;
	
	//text fields
	JTextField textfield1;
	JTextField textfield2;
	JTextField textfield3;
	JTextField textfield4;
	JTextField textfield5;
	
	//buttons
	JButton exit;
	JButton back;
	JButton view_inventory;
	JButton view_revenue;
	JButton confirm;
	JButton autofill;
	JButton editdelete_item;
	JButton add_item;
	JButton sell_item;
	JButton sortfilter_table;
	JButton clear;
	JButton clear2;
	JButton sort1;
	JButton sort2;
	JButton sort3;
	JButton sort4;
	JButton next;
	JButton previous;
	JButton quick_search;
	JButton reset;
	
	//check box
	JCheckBox opt1;
	JCheckBox opt2;
	JCheckBox opt3;
	JCheckBox opt4;
	
	//table
	JTable table;
	
	//write data in a new line in RandomAccessFile
	public static void write(String path, String data) {
		try {
			RandomAccessFile file = new RandomAccessFile(path,"rw");
			file.seek(file.length()); //move pointer to a new line in the end of the file
			file.writeBytes(data + "\n"); //write data and \n to move to next line
			file.close();
		}
		catch(IOException e) {
    		e.printStackTrace();
    	}
	}
	
	//delete a line at given position in RandomAccessFile
	public static void delete(String path, int position) {
		try {
			//new temporary path to assist deletion
			String temp_path = "/Users/rasyaharimurti/eclipse-workspace/MyInventory/src/MyInventory/data/temp.txt";
			//declare target RandomAccessFile
			RandomAccessFile file = new RandomAccessFile(path, "rw");
			//declare temporary RandomAccessFile
			RandomAccessFile temp = new RandomAccessFile(temp_path, "rw");
			//Empty the temporary RandomAccessFile
			temp.setLength(0);
			
			//Copy all data from file to temp
			String s;
			while((s = file.readLine()) != null) {
				write(temp_path, s);
			}
			
			//Empty file
			file.setLength(0);
			
			//Copy all Data from temp to file except for the line at position
			int i=0; s = null;
			while((s = temp.readLine()) != null) {
				if (i != position) { //Data is not written when i == position
					write(path, s);
				}
				i++;
			}
	        file.close();
	        temp.setLength(0);
	        temp.close();		
		}
		catch(IOException e) {
			e.printStackTrace();
		}

    }

	//find remainder for pagestack (but for for n>0, 25n mod 25 = 25)
	public static int mod(int a, int b) {
		if (a==0) {
			return 0;
		}
		else if (a%b == 0) {
			return 25;
		}
		else {
			return a%b;
		}
	}
	
	//divide for pagestack (but for c>0, (25n + c) / 25 = n+1
	public static int div(int a, int b) {
		if (a==0) {
			return 1;
		}
		else if (a%b == 0) {
			return a/b;
		}
		else {
			return a/b + 1;
		}
	}
}

//Main Menu Class
class Main_Menu extends GUI {
	//main method
	public static void main(String args[]) {
		new Main_Menu(); //call the Main Menu
		//default sorting/filters
		curr_isDescending = true;
		curr_isYear = true;
		curr_theme = null;
		curr_box = BooleanPlus.NONE;
		curr_sh = BooleanPlus.NONE;
	}
	//constructor
	Main_Menu() {
		setSize(1600,900);
		setLayout(null);
		
		//title
		title = new JLabel("MyInventory", SwingConstants.CENTER);
		title.setBounds(0,0,1515,400);
		title.setFont(xl_font);
		add(title);
		
		//exit
		exit = new JButton("Exit App");
		exit.setBounds(1300,20,200,80);
		exit.setFont(small_font);
		add(exit);
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to exit?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION);
                
                if (choice == JOptionPane.YES_OPTION) {
                	dispose();
                }
			}
		});
		
		//input new scores
		view_inventory = new JButton("<html><center>View<br>Inventory</center></html>");
		view_inventory.setBounds(250,350,400,300);
		view_inventory.setFont(big_font);
		add(view_inventory);
		view_inventory.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Inventory(0, curr_isDescending, curr_isYear, curr_theme, curr_sh, curr_box);
			}
		});
		
		//view scores
		view_revenue = new JButton("<html><center>View<br>Revenue</center></html>");
		view_revenue.setBounds(840,350,400,300);
		view_revenue.setFont(big_font);
		add(view_revenue);
		view_revenue.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Revenue rev = new Revenue();
				JOptionPane.showMessageDialog(null, "Total Revenue = " + rev.getRevenue(), "View Revenue", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	
		setVisible(true);
	}
}

class Inventory extends GUI {
	
	//instance variables for page stacks
	PageStack stack = new PageStack();
	PageStack stack2 = new PageStack();
	
	//Constructor
	Inventory(int current_page, boolean isDescending, boolean isYear, String themeFilter, BooleanPlus sh_filter, BooleanPlus box_filter) {
		
		//emptying page stack
		while (!stack.isEmpty()) {
			stack.pop();
		}
		while (!stack2.isEmpty()) {
			stack2.pop();
		}
		
		//emptying list
		lego_list.head = null;
		
		setSize(1600,900);
		setLayout(null);		
		
		//reading data from RandomAccessFile and transferring Lego details to linked list
		try {
			//declare RandomAccessFile
			RandomAccessFile serial_no_file = new RandomAccessFile(serial_no_path,"rw");
			RandomAccessFile set_name_file = new RandomAccessFile(set_name_path,"rw");
			RandomAccessFile theme_file = new RandomAccessFile(theme_path,"rw");
			RandomAccessFile year_file = new RandomAccessFile(year_path,"rw");
			RandomAccessFile pieces_file = new RandomAccessFile(no_of_pieces_path,"rw");
			RandomAccessFile second_hand_file = new RandomAccessFile(second_hand_path,"rw");
			RandomAccessFile box_file = new RandomAccessFile(box_path,"rw");
			
			String s;
			
			//loop through RandomAccessFile
			while((s = serial_no_file.readLine()) != null) {
				Lego lego = new Lego(); //Instantiate new lego
				lego.setSerialNumber(Parser.toInt(s));
				lego.setSetName(set_name_file.readLine()); 
				lego.setTheme(theme_file.readLine());
				lego.setYear(Parser.toInt(year_file.readLine()));
				lego.setPieces(Parser.toInt(pieces_file.readLine()));
				lego.setSecondHand(Parser.toBoolean(second_hand_file.readLine()));
				lego.setBox(Parser.toBoolean(box_file.readLine()));
				lego_list.addNext(lego); //add lego to linked list
			}
			
			
			serial_no_file.close();
			set_name_file.close();
			theme_file.close();
			year_file.close();
			pieces_file.close();
			second_hand_file.close();
			box_file.close();
		} 
		catch(IOException e) {
			e.printStackTrace();
		}
		
		//sorting linked list
		if (isYear) {
			lego_list = LinkedList.convertToList(LinkedList.sort_byYear(lego_list.convertToArray()));
		}
		else {
			lego_list = LinkedList.convertToList(LinkedList.sort_byPieces(lego_list.convertToArray()));
		}
		if (isDescending) {
			lego_list = LinkedList.convertToList(LinkedList.reverse_list(lego_list.convertToArray()));
		}
		
		//filtering linked list
		boolean showFilterError = false; //error message if filtering in cancelled
		
		//temp just in case, filtering is cancelled
		LinkedList temp = LinkedList.convertToList(lego_list.convertToArray());
		
		if (themeFilter != null) {
			lego_list.filterByTheme(themeFilter);
		}
		if (sh_filter != BooleanPlus.NONE) {
			boolean bool = sh_filter == BooleanPlus.TRUE;
			lego_list.filterBySecondHand(bool);	
		}
		if (box_filter != BooleanPlus.NONE) {
			boolean bool = box_filter == BooleanPlus.TRUE;
			lego_list.filterByBox(bool);		
		}

		if (lego_list.isEmpty()) {
			lego_list = LinkedList.convertToList(temp.convertToArray());
			showFilterError = true; //if all items are deleted, cancel filtering show an error message
		}	
		
		//title
		title = new JLabel("Inventory:", SwingConstants.LEFT);
		title.setBounds(60,0,1600,300);
		title.setFont(big_font);
		add(title);
				
		//exit
		exit = new JButton("Exit App");
		exit.setBounds(1300,20,200,80);
		exit.setFont(small_font);
		add(exit);
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(frame,
		              "Are you sure you want to exit?",
		              "Confirmation",
		              JOptionPane.YES_NO_OPTION);
		              
		              if (choice == JOptionPane.YES_OPTION) {
		               dispose();
		              }
			}
		});	
				
		//back
		back = new JButton("Back");
		back.setBounds(10,20,200,80);
		back.setFont(small_font);
		add(back);
		back.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Main_Menu();
			}
		});
		
		//sort/filter table
		sortfilter_table = new JButton("Sort/Filter Table");
		sortfilter_table.setBounds(1200,720,200,80);
		sortfilter_table.setFont(small_font);
		add(sortfilter_table);
		sortfilter_table.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
				new SortFilterTable();
			}
		});	
		
		//sell item
		sell_item = new JButton("Sell Item");
		sell_item.setBounds(980,720,200,80);
		sell_item.setFont(small_font);
		add(sell_item);
		sell_item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
				new SellItem();
			}
		});	
		
		//edit/delete item
		editdelete_item = new JButton("Edit/Delete Item");
		editdelete_item.setBounds(760,720,200,80);
		editdelete_item.setFont(small_font);
		add(editdelete_item);
		editdelete_item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
				new EditDeleteItem();
			}
		});	

		//add item
		add_item = new JButton("Add Item");
		add_item.setBounds(540,720,200,80);
		add_item.setFont(small_font);
		add(add_item);
		add_item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
				new AddItem();
			}
		});			
		
		int no_of_pages = div(lego_list.getSize(), 25); //calculate number of pages
		Lego[] temparray = lego_list.convertToArray();
		final String column[] = new String[] {"No", "Serial Number", "Set Name", "Theme", "Year", "No. of Pieces", "Second-Hand", "Box Included"};
		
		for (int j = 0; j<no_of_pages; j++) {
			//double array
			String array[][] = new String[26][8];
			
			if (j<no_of_pages-1) { //condition for the page to not be the last page
				for (int i = 1; i<= 25; i++) {
					array[i][0] = Parser.toString(j*25+i);
					array[i][1] = Parser.toString(temparray[j*25+i-1].getSerialNumber());
					array[i][2] = temparray[j*25+i-1].getSetName();
					array[i][3] = temparray[j*25+i-1].getTheme();
					array[i][4] = Parser.toString(temparray[j*25+i-1].getYear());
					array[i][5] = Parser.toString(temparray[j*25+i-1].getPieces());
					array[i][6] = Parser.toString(temparray[j*25+i-1].getSecondHand());
					array[i][7] = Parser.toString(temparray[j*25+i-1].getBox());
				}
			}
			else { //inserting data for the last page (the last page may not be full)
				for (int i = 1; i<=mod(temparray.length, 25); i++) {
					array[i][0] = Parser.toString(j*25+i);
					array[i][1] = Parser.toString(temparray[j*25+i-1].getSerialNumber());
					array[i][2] = temparray[j*25+i-1].getSetName();
					array[i][3] = temparray[j*25+i-1].getTheme();
					array[i][4] = Parser.toString(temparray[j*25+i-1].getYear());
					array[i][5] = Parser.toString(temparray[j*25+i-1].getPieces());
					array[i][6] = Parser.toString(temparray[j*25+i-1].getSecondHand());
					array[i][7] = Parser.toString(temparray[j*25+i-1].getBox());					
					
				}
			}
			//column headers
			array[0] = column;
			
			//push to page stack
			stack.push(array);
		}
		
		//default position for stacks
		while (stack.size() > 1) { //have the first page in stack (top of stack represents current page)
			stack2.push(stack.pop()); //all the remaining pages in stack2
		}
		
		//table	
		DefaultTableModel model = new DefaultTableModel(stack.peek(), column);
		table = new JTable(model);
		//stylistic choices
		TableColumn col0 = table.getColumnModel().getColumn(0);
		col0.setPreferredWidth(50);
		TableColumn col2 = table.getColumnModel().getColumn(2);
		col2.setPreferredWidth(300);
		table.setBounds(100,200,1300,500);
		add(table);
		TableColumn col3 = table.getColumnModel().getColumn(3);
		col3.setPreferredWidth(200);
		
		//next page
		next = new JButton("Next Page");
		next.setBounds(1300,160,100,40);
		next.setFont(mini_font);
		add(next);
		next.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (stack2.isEmpty()) { //signifies that there are no next pages
					JOptionPane.showMessageDialog(null, "You have Reached the Last Page. You cannot go to the Next Page.", "Warning", JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					stack.push(stack2.pop()); //go to next page, push the next page from stack 2 to stack
					DefaultTableModel newModel = new DefaultTableModel(stack.peek(), column);
			        table.setModel(newModel);
				}
			}
		});	
		
		//previous page
		previous = new JButton("Previous Page");
		previous.setBounds(1200,160,100,40);
		previous.setFont(mini_font);
		add(previous);
		previous.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (stack.size() <= 1) { //signifies that there is no previous page, stack only has 1 element (first page)
					JOptionPane.showMessageDialog(null, "You have Reached the First Page. You cannot go to the Previous Page.", "Warning", JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					stack2.push(stack.pop()); //go to previous page, pop from stack and save/move page to stack2
					DefaultTableModel newModel = new DefaultTableModel(stack.peek(), column);
			        table.setModel(newModel);
				}
			}
		});	
		
		//reset filters
		reset = new JButton("Reset Filters");
		reset.setBounds(1100,160,100,40);
		reset.setFont(mini_font);
		add(reset);
		reset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//set filters to default
				curr_theme = null;
				curr_box = BooleanPlus.NONE;
				curr_sh = BooleanPlus.NONE;
				dispose();
				new Inventory(0, curr_isDescending, curr_isYear, curr_theme, curr_sh, curr_box);
			}
		});	
		
		//quick search
		quick_search = new JButton("Quick Search");
		quick_search.setBounds(1000,160,100,40);
		quick_search.setFont(mini_font);
		add(quick_search);
		quick_search.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int response = Parser.toInt((JOptionPane.showInputDialog(null, "Enter Lego Serial Number:", "Quick Search", JOptionPane.QUESTION_MESSAGE)).trim());
				LegoTree tree = new LegoTree(lego_list); //create bst for fast search
				int position = tree.search(response); //position indicates position in linked list
				if (position == -1) { //not found
					JOptionPane.showMessageDialog(null, "Serial Number does not exist. Lego Set has not been found through Quick Search.", "Warning", JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					Lego target = lego_list.getNode(position); //get Node from linked list at position got from bst
					String sh; String box;
					if (target.getSecondHand()) {
						sh = "Yes";
					}
					else {
						sh = "No";
					}
					if (target.getBox()) {
						box = "Yes";
					}
					else {
						box = "No";
					}
					//show as message dialog
					String answer = "Lego Set has been found.\n\nSerial Number: " + target.getSerialNumber() + "\nSet Name: " + target.getSetName()+ "\nTheme: " + target.getTheme() 
					+ "\nYear: " + target.getYear() + "\nNumber of Pieces: " + target.getPieces() + "\nSecond Hand: " + sh + "\nBox Included: " + box;
					JOptionPane.showMessageDialog(null, answer, "Quick Search", JOptionPane.INFORMATION_MESSAGE);
				}	
			}
		});	
		
		setVisible(true);
		
		//filter error
		if (showFilterError) {
			JOptionPane.showMessageDialog(null, "No Lego Sets with Selected Filters Found.", "Warning", JOptionPane.INFORMATION_MESSAGE);
		}
		
	}
}

//Class for Sorting/Filtering Table Window
class SortFilterTable extends GUI {
	//Constructor
	SortFilterTable() {
		setSize(1600,900);
		setLayout(null);
		
		//title
		title = new JLabel("Sort/Filter Table:", SwingConstants.LEFT);
		title.setBounds(60,0,1600,300);
		title.setFont(big_font);
		add(title);
				
		//exit
		exit = new JButton("Exit App");
		exit.setBounds(1300,20,200,80);
		exit.setFont(small_font);
		add(exit);
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(frame,
		              "Are you sure you want to exit?",
		              "Confirmation",
		              JOptionPane.YES_NO_OPTION);
		              
		              if (choice == JOptionPane.YES_OPTION) {
		               dispose();
		              }
			}
		});	
				
		//back
		back = new JButton("Back");
		back.setBounds(10,20,200,80);
		back.setFont(small_font);
		add(back);
		back.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Inventory(0, curr_isDescending, curr_isYear, curr_theme, curr_sh, curr_box);
			}
		});
		
		//filter heading
		heading1 = new JLabel("Filters:", SwingConstants.LEFT);
		heading1.setBounds(60,140,1600,300);
		heading1.setFont(medium_font);
		add(heading1);
		
		//theme heading
		heading2 = new JLabel("Theme:", SwingConstants.LEFT);
		heading2.setBounds(60,210,1600,300);
		heading2.setFont(small_font);
		add(heading2);
		
		//second hand heading
		heading3 = new JLabel("Second-Hand:");
		heading3.setBounds(360,210,1600,300);
		heading3.setFont(small_font);
		add(heading3);
		
		//box included heading
		heading4 = new JLabel("Box Included:");
		heading4.setBounds(610,210,1600,300);
		heading4.setFont(small_font);
		add(heading4);
		
		//theme text field
		textfield1 = new JTextField();
		textfield1.setBounds(60,380,200,60);
		textfield1.setFont(small_font);
		add(textfield1);
		
		//checkboxes for second hand
		final ButtonGroup second_bg = new ButtonGroup();
		opt1 = new JCheckBox("Yes");
		opt1.setFont(small_font);
		opt1.setBounds(350,380,100,60);
		opt2 = new JCheckBox("No");
		opt2.setFont(small_font);
		opt2.setBounds(440,380,100,60);
		second_bg.add(opt1);
		second_bg.add(opt2);
		add(opt1);
		add(opt2);
		
		//checkboxes for box included
		final ButtonGroup box_bg = new ButtonGroup();
		opt3 = new JCheckBox("Yes");
		opt3.setFont(small_font);
		opt3.setBounds(600,380,100,60);
		opt4 = new JCheckBox("No");
		opt4.setFont(small_font);
		opt4.setBounds(690,380,100,60);
		box_bg.add(opt3);
		box_bg.add(opt4);
		add(opt3);
		add(opt4);

		//clear filters
		clear = new JButton("Clear");
		clear.setBounds(840,380,200,60);
		clear.setFont(small_font);
		add(clear);
		clear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				textfield1.setText("");
				second_bg.clearSelection();
				box_bg.clearSelection();
			}
		});		

		//apply filters
		confirm = new JButton("Apply Filters");
		confirm.setBounds(1080,380,200,60);
		confirm.setFont(small_font);
		add(confirm);
		confirm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (textfield1.getText().trim().equals("")) {
					curr_theme = null;
				}
				else {
					curr_theme = textfield1.getText().trim();
				}
				
				if (opt1.isSelected()) {
					curr_sh = BooleanPlus.TRUE;
				}
				else if (opt2.isSelected()) {
					curr_sh = BooleanPlus.FALSE;
				}
				else {
					curr_sh = BooleanPlus.NONE;
				}
				
				if (opt3.isSelected()) {
					curr_box = BooleanPlus.TRUE;
				}
				else if (opt4.isSelected()) {
					curr_box = BooleanPlus.FALSE;
				}
				else {
					curr_box = BooleanPlus.NONE;
				}
				dispose();
				new Inventory(0, curr_isDescending, curr_isYear, curr_theme, curr_sh, curr_box);
			}
		});
		
		//sort heading
		heading5 = new JLabel("Sort by:", SwingConstants.LEFT);
		heading5.setBounds(60,420,1600,300);
		heading5.setFont(medium_font);
		add(heading5);
		
		
		//sort by number of pieces (descending)
		sort1 = new JButton("Number of Pieces (Most to Least)");
		sort1.setBounds(60,620,500,80);
		sort1.setFont(small_font);
		add(sort1);
		sort1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				curr_isDescending = true;
				curr_isYear = false;
				dispose();
				new Inventory(0, curr_isDescending, curr_isYear, curr_theme, curr_sh, curr_box);
			}
		});
		
		//sort by number of pieces (ascending)
		sort2 = new JButton("Number of Pieces (Least to Most)");
		sort2.setBounds(60,720,500,80);
		sort2.setFont(small_font);
		add(sort2);
		sort2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				curr_isDescending = false;
				curr_isYear = false;
				dispose();
				new Inventory(0, curr_isDescending, curr_isYear, curr_theme, curr_sh, curr_box);
			}
		});		

		//sort by year (descending)
		sort3 = new JButton("Year (Most to Least Recent)");
		sort3.setBounds(600,620,500,80);
		sort3.setFont(small_font);
		add(sort3);
		sort3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
				curr_isDescending = true;
				curr_isYear = true;
				new Inventory(0, curr_isDescending, curr_isYear, curr_theme, curr_sh, curr_box);
			}
		});

		//sort year (ascending)
		sort4 = new JButton("Year (Least to Most Recent)");
		sort4.setBounds(600,720,500,80);
		sort4.setFont(small_font);
		add(sort4);
		sort4.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
				curr_isDescending = false;
				curr_isYear = true;
				new Inventory(0, curr_isDescending, curr_isYear, curr_theme, curr_sh, curr_box);
			}
		});		
		
		setVisible(true);
	}
}

//Class for Add Item Window
class AddItem extends GUI {
	AddItem() {
		setSize(1600,900);
		setLayout(null);
		
		//title
		title = new JLabel("Add Item:", SwingConstants.LEFT);
		title.setBounds(60,0,1600,300);
		title.setFont(medium_font);
		add(title);
		
		//exit
		exit = new JButton("Exit App");
		exit.setBounds(1300,20,200,80);
		exit.setFont(small_font);
		add(exit);
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to exit?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION);
                
                if (choice == JOptionPane.YES_OPTION) {
                	dispose();
                }
			}
		});	
		
		//back
		back = new JButton("Back");
		back.setBounds(10,20,200,80);
		back.setFont(small_font);
		add(back);
		back.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Inventory(0, curr_isDescending, curr_isYear, curr_theme, curr_sh, curr_box);;
			}
		});	
		
		//serial number
		heading1 = new JLabel("Serial Number:", SwingConstants.LEFT);
		heading1.setBounds(60,100,1600,300);
		heading1.setFont(small_font);
		add(heading1);
		
		textfield1 = new JTextField();
		textfield1.setBounds(50,265,500,60);
		textfield1.setFont(small_font);
		add(textfield1);
		
		//scores
		heading2 = new JLabel("Set Name:", SwingConstants.LEFT);
		heading2.setBounds(60,200,1600,300);
		heading2.setFont(small_font);
		add(heading2);
		
		textfield2 = new JTextField();
		textfield2.setBounds(50,365,500,60);
		textfield2.setFont(small_font);
		add(textfield2);
		
		//theme
		heading3 = new JLabel("Theme:", SwingConstants.LEFT);
		heading3.setBounds(60,300,1600,300);
		heading3.setFont(small_font);
		add(heading3);
		
		textfield3 = new JTextField();
		textfield3.setBounds(50,465,500,60);
		textfield3.setFont(small_font);
		add(textfield3);
		
		//year
		heading4 = new JLabel("Year:", SwingConstants.LEFT);
		heading4.setBounds(60,400,1600,300);
		heading4.setFont(small_font);
		add(heading4);
		
		textfield4 = new JTextField();
		textfield4.setBounds(50,565,500,60);
		textfield4.setFont(small_font);
		add(textfield4);
		
		//no of pieces
		heading5 = new JLabel("No. of Pieces:", SwingConstants.LEFT);
		heading5.setBounds(660,100,1600,300);
		heading5.setFont(small_font);
		add(heading5);
		
		textfield5 = new JTextField();
		textfield5.setBounds(650,265,500,60);
		textfield5.setFont(small_font);
		add(textfield5);

		//second-hand
		heading6 = new JLabel("Second-Hand:", SwingConstants.LEFT);
		heading6.setBounds(660,200,1600,300);
		heading6.setFont(small_font);
		add(heading6);
		
		final ButtonGroup second_bg = new ButtonGroup();
		opt1 = new JCheckBox("Yes");
		opt1.setFont(small_font);
		opt1.setBounds(650,365,100,60);
		opt2 = new JCheckBox("No");
		opt2.setFont(small_font);
		opt2.setBounds(740,365,100,60);
		second_bg.add(opt1);
		second_bg.add(opt2);
		add(opt1);
		add(opt2);
		
		clear = new JButton("Clear");
		clear.setBounds(840,365,150,60);
		clear.setFont(small_font);
		add(clear);
		
		//box included
		heading6 = new JLabel("Box Included:", SwingConstants.LEFT);
		heading6.setBounds(660,300,1600,300);
		heading6.setFont(small_font);
		add(heading6);
		
		final ButtonGroup box_bg = new ButtonGroup();
		opt3 = new JCheckBox("Yes");
		opt3.setFont(small_font);
		opt3.setBounds(650,465,100,60);
		opt4 = new JCheckBox("No");
		opt4.setFont(small_font);
		opt4.setBounds(740,465,100,60);
		box_bg.add(opt3);
		box_bg.add(opt4);
		add(opt3);
		add(opt4);
		
		clear2 = new JButton("Clear");
		clear2.setBounds(840,465,150,60);
		clear2.setFont(small_font);
		add(clear2);
		
		//Autofill
		autofill = new JButton("Autofill");
		autofill.setBounds(660,550,200,80);
		autofill.setFont(small_font);
		add(autofill);
		ActionListener AL_autofill = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!textfield1.getText().trim().equals("")) { //if serial number is not left blank
					try {		
						Lego lego = new Lego();
						lego = WebScraper.scrape(textfield1.getText(), lego); //call scrape method
						if(lego.getSetName()==null || lego.getTheme()==null || lego.getYear()==0 || lego.getPieces()==0) {
							JOptionPane.showMessageDialog(null, "Invalid Serial Number! Please Try Again.", "Warning", JOptionPane.INFORMATION_MESSAGE);
							return;
						}
						textfield2.setText(lego.getSetName()); //autofill all details
						textfield3.setText(lego.getTheme());
						textfield4.setText(Parser.toString(lego.getYear()));
						textfield5.setText(Parser.toString(lego.getPieces()));
						JOptionPane.showMessageDialog(null, "Autofill Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
					} catch(Exception f) { //Autofill was Unsuccessful
						f.printStackTrace();
						JOptionPane.showMessageDialog(null, "Autofill Unsuccessful. Please Try Again.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else { //if serial number was left blank
					JOptionPane.showMessageDialog(null, "Enter Serial Number to Autofill Lego Details!", "Warning", JOptionPane.INFORMATION_MESSAGE);
				}
				
			}
		};
		autofill.addActionListener(AL_autofill);

		//confirm
		confirm = new JButton("Confirm");
		confirm.setBounds(885,550,200,80);
		confirm.setFont(small_font);
		add(confirm);
		ActionListener AL_confirm = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (Parser.isInteger(textfield1.getText().trim()) && !textfield2.getText().trim().equals("") && !textfield3.getText().trim().equals("") && Parser.isInteger(textfield4.getText().trim()) && Parser.isInteger(textfield5.getText().trim()) && (opt1.isSelected() || opt2.isSelected()) && (opt3.isSelected() || opt4.isSelected())) {
						int choice = JOptionPane.showConfirmDialog(frame,
					              "Are you sure you want to add this item?",
					              "Confirmation",
					              JOptionPane.YES_NO_OPTION);
					              
			            if (choice == JOptionPane.NO_OPTION) {
			               return;
			            }
			            
			            //write details in RandomAccessFile
			            write(serial_no_path, textfield1.getText());
			            write(set_name_path, textfield2.getText());
			            write(theme_path, textfield3.getText());
			            write(year_path, textfield4.getText());
			            write(no_of_pieces_path, textfield5.getText());
						
			            //Write the sh and box details in RandomAccessFile
						String sh_string = "No";
						if (opt1.isSelected()) {
							sh_string = "Yes";
						}
						write(second_hand_path, sh_string);
		
						String box_string = "No";
						if (opt3.isSelected()) {
							box_string = "Yes";
						}
						write(box_path, box_string);
						
						dispose();
			        	new Inventory(0, curr_isDescending, curr_isYear, curr_theme, curr_sh, curr_box);
						JOptionPane.showMessageDialog(null, "Item Successfully Edited.", "Success", JOptionPane.INFORMATION_MESSAGE);
						
					}
					else {
						JOptionPane.showMessageDialog(null, "Invalid Input! Please try again.", "Warning", JOptionPane.INFORMATION_MESSAGE);
					}
				} catch(Exception f) {
					JOptionPane.showMessageDialog(null, "Invalid Input! Please try again.", "Warning", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};
		confirm.addActionListener(AL_confirm);	
		
		setVisible(true);
	}	
}

//Class for Edit/Delete Item Window
class EditDeleteItem extends GUI {
	EditDeleteItem() {
		setSize(1600,900);
		setLayout(null);
		
		//exit
		exit = new JButton("Exit App");
		exit.setBounds(1300,20,200,80);
		exit.setFont(small_font);
		add(exit);
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to exit?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION);
                
                if (choice == JOptionPane.YES_OPTION) {
                	dispose();
                }
			}
		});	
		
		//back
		back = new JButton("Back");
		back.setBounds(10,20,200,80);
		back.setFont(small_font);
		add(back);
		back.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Inventory(0, curr_isDescending, curr_isYear, curr_theme, curr_sh, curr_box);
			}
		});	
		
		//title
		title = new JLabel("Edit/Delete Item:", SwingConstants.LEFT);
		title.setBounds(60,0,1600,300);
		title.setFont(medium_font);
		add(title);
		
		//serial number
		heading1 = new JLabel("Enter Serial Number:", SwingConstants.LEFT);
		heading1.setBounds(450,230,1600,300);
		heading1.setFont(small_font);
		add(heading1);
		
		//text field for serial number
		textfield1 = new JTextField();
		textfield1.setBounds(450,395,300,60);
		textfield1.setFont(small_font);
		add(textfield1);
		
		//confirm
		confirm = new JButton("Search");
		confirm.setBounds(800,395,150,60);
		confirm.setFont(small_font);
		add(confirm);
		confirm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String target = textfield1.getText().trim();
				try {
					RandomAccessFile serial_no_file = new RandomAccessFile(serial_no_path,"rw"); //open serial no file
					
					String serial_number;
					int i = 0; //indicates position in random access file
					
					while(true) {
						serial_number = serial_no_file.readLine();
						if (serial_number == null) { //serial number not found
							JOptionPane.showMessageDialog(null, "Lego Set Not Found. Please try again.", "Warning", JOptionPane.INFORMATION_MESSAGE);
							break;
						}
						if (serial_number.equalsIgnoreCase(target)) { //serial number is found
							dispose();
							new EditDeleteItem2(i); //call Edit Delete Item window with position as parameter
							break;
						}
						i++;
					}
					serial_no_file.close();				
					
				} 
				catch(IOException f) {
					f.printStackTrace();
				}		
			}
		});	
		setVisible(true);
	}
}

//Class for Edit/Delete Item 2 Window
class EditDeleteItem2 extends AddItem {
	//inherits AddItem because of similar GUI
	
	//constructor
	EditDeleteItem2(final int position) {
		
		//additional buttons
		JButton SaveChanges;
		JButton Delete;
		JButton new_back;
		
		//title
		title.setText("Edit/Delete Item:");
		remove(autofill);
		remove(confirm);
		remove(back);

		//back
		back = new JButton("Back");
		back.setBounds(10,20,200,80);
		back.setFont(small_font);
		add(back);
		back.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
				new EditDeleteItem();
			}
		});			
		
		//represents data of lego
		String serial_no = null; String set_name = null; String theme = null; String year = null; String pieces = null; String sh = null; String box = null;
		
		//reading lego data from RandomAccessFile corresponding to given position
		try {
			RandomAccessFile serial_no_file = new RandomAccessFile(serial_no_path,"rw");
			RandomAccessFile set_name_file = new RandomAccessFile(set_name_path,"rw");
			RandomAccessFile theme_file = new RandomAccessFile(theme_path,"rw");
			RandomAccessFile year_file = new RandomAccessFile(year_path,"rw");
			RandomAccessFile pieces_file = new RandomAccessFile(no_of_pieces_path,"rw");
			RandomAccessFile second_hand_file = new RandomAccessFile(second_hand_path,"rw");
			RandomAccessFile box_file = new RandomAccessFile(box_path,"rw");
			
			int i=0;
			
			//loop to line directly before position
			while (i<position) {
				serial_no_file.readLine();
				set_name_file.readLine();
				theme_file.readLine();
				year_file.readLine();
				pieces_file.readLine();
				second_hand_file.readLine();
				box_file.readLine();
				i++;
			}
			
			//read the next line
			serial_no = serial_no_file.readLine();
			set_name = set_name_file.readLine();
			theme = theme_file.readLine();
			year = year_file.readLine();
			pieces = pieces_file.readLine();
			sh = second_hand_file.readLine();
			box = box_file.readLine();
			
			serial_no_file.close();
			set_name_file.close();
			theme_file.close();
			year_file.close();
			pieces_file.close();
			second_hand_file.close();
			box_file.close();
		} 
		catch(IOException f) {
			f.printStackTrace();
		}
		
		//set the Text Field and Checkboxes according to read details
		textfield1.setText(serial_no); 
		textfield2.setText(set_name); 
		textfield3.setText(theme);
		textfield4.setText(year);
		textfield5.setText(pieces);
		if (sh.trim().equalsIgnoreCase("Yes")) {
			opt1.setSelected(true);
		}
		else {
			opt2.setSelected(true);
		}
		if (box.trim().equalsIgnoreCase("Yes")) {
			opt3.setSelected(true);
		}
		else {
			opt4.setSelected(true);
		}		
		
		//Save Changes
		SaveChanges = new JButton("Save Changes");
		SaveChanges.setBounds(660,550,200,80);
		SaveChanges.setFont(small_font);
		add(SaveChanges);
		ActionListener AL_SaveChanges = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (Parser.isInteger(textfield1.getText().trim()) && !textfield2.getText().trim().equals("") && !textfield3.getText().trim().equals("") && Parser.isInteger(textfield4.getText().trim()) && Parser.isInteger(textfield5.getText().trim()) && (opt1.isSelected() || opt2.isSelected()) && (opt3.isSelected() || opt4.isSelected())) {
						int choice = JOptionPane.showConfirmDialog(frame,
					              "Are you sure you want to edit this item?",
					              "Confirmation",
					              JOptionPane.YES_NO_OPTION);
					              
			            if (choice == JOptionPane.NO_OPTION) {
			               return;
			            }
			            
			            //delete the details of the lego and current position
						delete(serial_no_path, position);
			        	delete(set_name_path, position);
			        	delete(theme_path, position);
			        	delete(year_path, position);
			        	delete(no_of_pieces_path, position);
			        	delete(second_hand_path, position);
			        	delete(box_path, position);
			        	
			        	//write edited version of lego with details that have been filled in
			        	write(serial_no_path, textfield1.getText());
			            write(set_name_path, textfield2.getText());
			            write(theme_path, textfield3.getText());
			            write(year_path, textfield4.getText());
			            write(no_of_pieces_path, textfield5.getText());
							
						String sh_string = "No";
						if (opt1.isSelected()) {
							sh_string = "Yes";
						}
						write(second_hand_path, sh_string);
	
						String box_string = "No";
						if (opt3.isSelected()) {
							box_string = "Yes";
						}
						write(box_path, box_string);
						
						dispose();
			        	new Inventory(0, curr_isDescending, curr_isYear, curr_theme, curr_sh, curr_box);
						JOptionPane.showMessageDialog(null, "Item Successfully Edited.", "Success", JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(null, "Invalid Input! Please try again.", "Warning", JOptionPane.INFORMATION_MESSAGE);
					}
				} catch(Exception f) {
					JOptionPane.showMessageDialog(null, "Invalid Input! Please try again.", "Warning", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};
		SaveChanges.addActionListener(AL_SaveChanges);
		
		//Delete Item
		Delete = new JButton("Delete Item");
		Delete.setBounds(885,550,200,80);
		Delete.setFont(small_font);
		add(Delete);
		ActionListener AL_Delete = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(frame,
			              "Are you sure you want to delete this item?",
			              "Confirmation",
			              JOptionPane.YES_NO_OPTION);
			              
	            if (choice == JOptionPane.NO_OPTION) {
	               return;
	            }
				
	            //delete Lego details from RandomAccessFile
	        	delete(serial_no_path, position);
	        	delete(set_name_path, position);
	        	delete(theme_path, position);
	        	delete(year_path, position);
	        	delete(no_of_pieces_path, position);
	        	delete(second_hand_path, position);
	        	delete(box_path, position);
	        	
	        	dispose();
	        	new Inventory(0, curr_isDescending, curr_isYear, curr_theme, curr_sh, curr_box);
	        	JOptionPane.showMessageDialog(null, "Item Successfully Deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
		        
		    }
		};
		Delete.addActionListener(AL_Delete);
		
		setVisible(true);
	}
}

//Class for Sell Item Window
class SellItem extends GUI{
	//constructor
	SellItem() {
		setSize(1600,900);
		setLayout(null);
		
		//exit
		exit = new JButton("Exit App");
		exit.setBounds(1300,20,200,80);
		exit.setFont(small_font);
		add(exit);
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to exit?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION);
                
                if (choice == JOptionPane.YES_OPTION) {
                	dispose();
                }
			}
		});	
		
		//back
		back = new JButton("Back");
		back.setBounds(10,20,200,80);
		back.setFont(small_font);
		add(back);
		back.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Inventory(0, curr_isDescending, curr_isYear, curr_theme, curr_sh, curr_box);
			}
		});	
		
		//title
		title = new JLabel("Sell Item:", SwingConstants.LEFT);
		title.setBounds(60,0,1600,300);
		title.setFont(medium_font);
		add(title);
		
		//serial number heading
		heading1 = new JLabel("Enter Serial Number:", SwingConstants.LEFT);
		heading1.setBounds(60,100,1600,300);
		heading1.setFont(small_font);
		add(heading1);
		
		//text field for serial number
		textfield1 = new JTextField();
		textfield1.setBounds(50,265,310,60);
		textfield1.setFont(small_font);
		add(textfield1);
		
		//confirm
		confirm = new JButton("Search");
		confirm.setBounds(375,265,150,60);
		confirm.setFont(small_font);
		add(confirm);
		confirm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String target = textfield1.getText().trim();
				//find position in RandomAccessFile
				try {
					RandomAccessFile serial_no_file = new RandomAccessFile(serial_no_path,"rw");
					
					String serial_number;
					int i = 0; //indicates position
					
					//iterate position until serial number has been found
					while(!(serial_number = serial_no_file.readLine()).equalsIgnoreCase(target)) {
						i++;
					}
					
					dispose();
					new SellItem2(i); //call Sell Item 2 Window with position
					serial_no_file.close();				
					
				} 
				catch(Exception f) { //Goes to Exception if lego set is not found
					JOptionPane.showMessageDialog(null, "Lego Set Not Found. Please try again.", "Warning", JOptionPane.INFORMATION_MESSAGE);
					f.printStackTrace();
				}		
			}
		});	

		setVisible(true);
	}
}

//class for Sell Item 2 window
class SellItem2 extends SellItem {
	//inherits SellItem because of similar GUI elements
	
	//constructor
	SellItem2(final int position) {
		
		//additional Labels
		JLabel heading8;
		JLabel heading9;
		JLabel heading10;
		JLabel rp;
		
		//Find Details of Lego in RandomAccessFile from given position
		try {
			RandomAccessFile serial_no_file = new RandomAccessFile(serial_no_path,"rw");
			RandomAccessFile set_name_file = new RandomAccessFile(set_name_path,"rw");
			RandomAccessFile theme_file = new RandomAccessFile(theme_path,"rw");
			RandomAccessFile year_file = new RandomAccessFile(year_path,"rw");
			RandomAccessFile pieces_file = new RandomAccessFile(no_of_pieces_path,"rw");
			RandomAccessFile second_hand_file = new RandomAccessFile(second_hand_path,"rw");
			RandomAccessFile box_file = new RandomAccessFile(box_path,"rw");
			
			String serial_number;
			int i=0;
			
			//iterate to line before position
			while (i<position) {
				serial_no_file.readLine();
				set_name_file.readLine();
				theme_file.readLine();
				year_file.readLine();
				pieces_file.readLine();
				second_hand_file.readLine();
				box_file.readLine();
				i++;
			}
			
		//write labels based on next readLine()
			
			//item details (main subheading)
			heading2 = new JLabel("Item Details:", SwingConstants.LEFT);
			heading2.setBounds(60,300,1600,300);
			heading2.setFont(medium_font);
			add(heading2);
			
			//serial number
			heading3 = new JLabel("Serial Number: " + serial_no_file.readLine(), SwingConstants.LEFT);
			heading3.setBounds(60,350,1600,300);
			heading3.setFont(small_font);
			add(heading3);
			
			//set name
			heading4 = new JLabel("Set Name: " + set_name_file.readLine(), SwingConstants.LEFT);
			heading4.setBounds(60,375,1600,300);
			heading4.setFont(small_font);
			add(heading4);
			
			//theme
			heading5 = new JLabel("Theme: " + theme_file.readLine(), SwingConstants.LEFT);
			heading5.setBounds(60,400,1600,300);
			heading5.setFont(small_font);
			add(heading5);
			
			//year
			heading6 = new JLabel("Year: " + year_file.readLine(), SwingConstants.LEFT);
			heading6.setBounds(60,425,1600,300);
			heading6.setFont(small_font);
			add(heading6);
			
			//no of pieces
			heading7 = new JLabel("No. of Pieces: " + pieces_file.readLine(), SwingConstants.LEFT);
			heading7.setBounds(60,450,1600,300);
			heading7.setFont(small_font);
			add(heading7);
			
			//second-hand
			heading8 = new JLabel("Second-Hand: " + second_hand_file.readLine(), SwingConstants.LEFT);
			heading8.setBounds(60,475,1600,300);
			heading8.setFont(small_font);
			add(heading8);
			
			//box included
			heading9 = new JLabel("Box Included: " + second_hand_file.readLine(), SwingConstants.LEFT);
			heading9.setBounds(60,500,1600,300);
			heading9.setFont(small_font);
			add(heading9);
			
			serial_no_file.close();
			set_name_file.close();
			theme_file.close();
			year_file.close();
			pieces_file.close();
			second_hand_file.close();
			box_file.close();
			
		}
		catch(IOException f) {
			dispose();
			f.printStackTrace();
		}
		
		//set price
		heading10 = new JLabel("Set Price:", SwingConstants.LEFT);
		heading10.setBounds(700,300,1600,300);
		heading10.setFont(medium_font);
		add(heading10);
		
		//Rupiah label
		rp = new JLabel("Rp.", SwingConstants.LEFT);
		rp.setBounds(700,360,1600,300);
		rp.setFont(small_font);
		add(rp);
		
		//text field for user to fill in value
		textfield2 = new JTextField();
		textfield2.setBounds(750,480,310,60);
		textfield2.setFont(small_font);
		add(textfield2);
		
		//Sell Button
		next = new JButton("Sell");
		next.setBounds(800,565,200,80);
		next.setFont(small_font);
		add(next);
		next.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (Parser.isInteger(textfield2.getText().trim())) {
					int choice = JOptionPane.showConfirmDialog(frame,
				              "Are you sure you want to sell this item?",
				              "Confirmation",
				              JOptionPane.YES_NO_OPTION);
				              
		            if (choice == JOptionPane.NO_OPTION) {
		               return;
		            }
		    	}
				else {
					JOptionPane.showMessageDialog(null, "Set Price Invalid. Please Enter an Appropriate Price.", "Warning", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				
				//delete item from RandomAccessFile
	        	delete(serial_no_path, position);
	        	delete(set_name_path, position);
	        	delete(theme_path, position);;
	        	delete(year_path, position);
	        	delete(no_of_pieces_path, position);
	        	delete(second_hand_path, position);
	        	delete(box_path, position);
	        	
	        	//create instance of revenue
	        	Revenue rev = new Revenue();
	        	//add set price to revenue
				rev.addRevenue(Parser.toInt(textfield2.getText()));
	        	
	        	dispose();
	        	new Main_Menu();
	        	JOptionPane.showMessageDialog(null, "Item Successfully Sold.", "Success", JOptionPane.INFORMATION_MESSAGE);
	        	
			}
		});	
		
		
		setVisible(true);
	}
}
