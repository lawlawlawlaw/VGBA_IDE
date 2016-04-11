import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.regex.Matcher;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.JScrollPane;


public class Browser {
	
	MainWin mainWin;
	FileTree fileTree;
	JScrollPane scrollPane;
	
	public Browser(final MainWin mainWin)
	{
		this.mainWin = mainWin;
		this.scrollPane = new JScrollPane();
		this.scrollPane.setBounds((int)(mainWin.factx*10), (int)(mainWin.facty*30), (int)(mainWin.factx*150), (int)(mainWin.facty*493));
		
		this.buildTree(this.mainWin.pathFromFile.toString());
		
		// different clicks on tree handler
		fileTree.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e) {
				// Right click on tree handler
				if (SwingUtilities.isRightMouseButton(e))
				{
					int row = fileTree.getClosestRowForLocation(e.getX(), e.getY());
			        fileTree.setSelectionRow(row);
			        File fft = getFileFromTree();
			        // If source isn't selected, don't show new file option
			        if (!(fft.getName().equals("source"))) {
			        		for (Component child: mainWin.popupMenu.getComponents())
			        		{
			        				if (((JMenuItem)child).getText()=="New..."){
			        					for(Component leaf : ((JMenu)child).getPopupMenu().getComponents()){
			        						if (((JMenuItem)leaf).getText()=="File..."){
			        							leaf.setEnabled(false);
			        						}
			        					}
			        					
			        				}
			        		}
			        } else { // If source is selected, show all possible options
			        	for (Component child: mainWin.popupMenu.getComponents())
		        		{
		        				if (((JMenuItem)child).getText()=="New..."){
		        					for(Component leaf : ((JMenu)child).getPopupMenu().getComponents()){
		        						if (((JMenuItem)leaf).getText()=="File..."){
		        							leaf.setEnabled(true);
		        						}
		        					}
		        					
		        				}
		        			
		        		}
			        }
			        mainWin.popupMenu.show(e.getComponent(), e.getX(), e.getY());
				} // Double click on tree handler
				else if (e.getClickCount()==2)
				{	
					OpenFile(getFileFromTree().toString());
				}	
			}
		});
		this.scrollPane.setViewportView(this.fileTree);
		mainWin.contentPane.add(this.scrollPane);
	}
	
	public File getFileFromTree(){
		String parent = mainWin.pathFromFile.getParentFile().toString();
		File selected = new File(parent);
		try
		{		
			String file = fileTree.getSelectionPath().toString();
			file=file.replaceAll(", ",Matcher.quoteReplacement(File.separator));
			file=File.separator+file.substring(1,file.length()-1);
			file=parent+file;
			selected = new File(file);}
		catch(Exception NullPointerException)
		{
		}
		return selected;
	}
	
	public void OpenFile(String pathTofile)
	{
		File file_F = new File(pathTofile);
		String file_ext = "";
		if (file_F.isFile())
		{
			if (file_F.getName().equals("Makefile"))
			{
				mainWin.editor.loadFile(file_F,file_ext);
				String file_P = file_F.getParent();
				mainWin.label.setText(file_P.substring(file_P.lastIndexOf(File.separator)+1,file_P.length())+ " : " + file_F.getName());
			}
			else{
			file_ext=getFileExtension(file_F);
			if ((file_ext.equals("c")) || (file_ext.equals("s")) || (file_ext.equals("h")))
			{
				mainWin.editor.loadFile(file_F,file_ext);
				String file_P = file_F.getParentFile().getParent();
				mainWin.label.setText(file_P.substring(file_P.lastIndexOf(File.separator)+1,file_P.length())+ " : " + file_F.getName());
			} else {
				new MessageBox("Can't open this type of file", "info");
			}
			}
		}
	}
	
	public String getFileExtension(File file) {
	    String name = file.getName();
	    try {
	        return name.substring(name.lastIndexOf(".") + 1);
	    } catch (Exception e) {
	        return "";
	    }
	}
	
	public void buildTree(String path) {
		this.fileTree = new FileTree(path);
	}
	
}