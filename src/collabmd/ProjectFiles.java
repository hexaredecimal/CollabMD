package collabmd;

import gama.sv.Project;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.util.Arrays;
import javax.swing.JDesktopPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;


public class ProjectFiles extends javax.swing.JInternalFrame {

	private static ProjectFiles files;

	private ProjectFiles() {
		initComponents();
		Project.loadDirectory(jTree1, Project.getRoot());

		ProjectFiles self = this;
		jTree1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = jTree1.getRowForLocation(e.getX(), e.getY());
				TreePath tp = jTree1.getPathForLocation(e.getX(), e.getY());

				if (tp == null) {
					return;
				}

				String path = Project.makePath(tp.getPath());
				switch (e.getButton()) {
					case 1: {
						int clicks = e.getClickCount();
						if (clicks == 2) {
							if (Project.hasExtension(path, ".md") || Project.hasExtension(path, ".html") || Project.hasExtension(path, ".txt")) {

								File fileToOpen = new File(path);
								String name = fileToOpen.getName();
								String[] splits = name.split("\\+");

								String title = splits[0];
								String author = "author";
								if (splits.length > 1) {
									author = splits[0];
									title = splits[1];
								}

								String contents = Project.readFile(path);
								Editor ed = new Editor(title, contents, path, author, Project.getExtension(path));
								JDesktopPane desktop = (JDesktopPane) self.getParent();
								ed.setBounds(desktop.getWidth() / 4, desktop.getHeight() / 4, ed.getWidth(), ed.getHeight());
								ed.setAction("Openned a file from project files window, by double clicking");
								Project.addToProjectIfNotThere(path);
								desktop.add(ed);

								String recent = Project.getTemp_dir();
								Project.addPathToRecentFiles(recent, fileToOpen.getAbsolutePath());
							}
						}
					}
					break;
					case 3:
						createPopUp(path).show(e.getComponent(), e.getX(), e.getY());
						break;
				}
			}
		});
	}

	private JPopupMenu createPopUp(String path) {
		JPopupMenu popuop = new JPopupMenu("File actions");
		JMenuItem new_file = new JMenuItem("New");

		JMenuItem open_file = new JMenuItem("Open");
		ProjectFiles self = this;
		open_file.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (Project.hasExtension(path, ".md") || Project.hasExtension(path, ".html") || Project.hasExtension(path, ".txt")) {

					File fileToOpen = new File(path);
					String name = fileToOpen.getName();
					String[] splits = name.split("\\+");

					String title = splits[0];
					String author = "author";
					if (splits.length > 1) {
						author = splits[0];
						title = splits[1];
					}

					String contents = Project.readFile(path);
					Editor ed = new Editor(title, contents, path, author, Project.getExtension(path));
					JDesktopPane desktop = (JDesktopPane) self.getParent();
					ed.setBounds(desktop.getWidth() / 4, desktop.getHeight() / 4, ed.getWidth(), ed.getHeight());
					ed.setAction("Openned a file from project files window, by using the context menu");
					Project.addToProjectIfNotThere(path);
					desktop.add(ed);

					String recent = Project.getTemp_dir();
					Project.addPathToRecentFiles(recent, fileToOpen.getAbsolutePath());
				}
			}
		});

		JMenuItem delete_file = new JMenuItem("Delete");
		delete_file.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				int option = JOptionPane.showConfirmDialog(self, "Are you sure you want to delete `" + path + "`");
				if (option != 0) {
					return;
				}

				ArrayList<String> files = Project.getFiles(); 
				if (files.contains(path)) {
					files.remove(path);
				}
					
				File fp = new File(path);
				fp.delete();
				jTree1.removeAll();
				Project.loadDirectory(jTree1, Project.getRoot());
			}
		});

		JMenuItem file_path = new JMenuItem("Show path");
		file_path.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				File fp = new File(path); 
				String p = fp.getAbsolutePath();
				JOptionPane.showInputDialog(jTree1, "Copy path: ", p);
			}
		});
		
		popuop.add(new_file);
		popuop.add(open_file);
		popuop.add(new JSeparator());
		popuop.add(delete_file);
		popuop.add(new JSeparator());
		popuop.add(file_path);


		return popuop;
	}

	public static ProjectFiles tree() {
		if (files == null) {
			files = new ProjectFiles();
		}
		return files;
	}

	public static boolean isDead() {
		return files == null;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPopupMenu1 = new javax.swing.JPopupMenu();
    jScrollPane1 = new javax.swing.JScrollPane();
    jTree1 = new javax.swing.JTree();

    setClosable(true);
    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setIconifiable(true);
    setResizable(true);
    setTitle("Project files");
    setVisible(true);
    addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
      public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
      }
      public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
      }
      public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
        formInternalFrameClosing(evt);
      }
      public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
      }
      public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
      }
      public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
      }
      public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
      }
    });

    javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
    jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
    jScrollPane1.setViewportView(jTree1);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
		files = null;
  }//GEN-LAST:event_formInternalFrameClosing


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPopupMenu jPopupMenu1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTree jTree1;
  // End of variables declaration//GEN-END:variables
}
