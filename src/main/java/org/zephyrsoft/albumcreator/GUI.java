package org.zephyrsoft.albumcreator;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zephyrsoft.albumcreator.model.Settings;
import org.zephyrsoft.albumcreator.model.SourceFile;
import org.zephyrsoft.albumcreator.model.SourceTableModel;
import org.zephyrsoft.albumcreator.model.TargetFile;
import org.zephyrsoft.albumcreator.model.TargetTableModel;

/**
 * The user interface.
 * 
 * @author Mathis Dirksen-Thedens
 */
public class GUI extends JFrame implements LogTarget {
	
	private static final Logger LOG = LoggerFactory.getLogger(GUI.class);
	private static final long serialVersionUID = -2316986129445458114L;
	
	private Service service;
	private Settings settings;
	
	private LocalDateTime filterTimestamp;
	private String filterString;
	
	private JScrollPane logScrollPane;
	private JTextArea log;
	private JTextField sourceDirField;
	private JTextField targetDirField;
	private JButton sourceDirButton;
	private JButton targetDirButton;
	private JButton createScriptButton;
	private JButton moveFilesButton;
	private JSplitPane splitPane;
	private JScrollPane sourceTableScrollPane;
	private SourceTableModel sourceTableModel;
	private JTable sourceTable;
	private JScrollPane targetTableScrollPane;
	private TargetTableModel targetTableModel;
	private JTable targetTable;
	private JPanel buttonPanel;
	private JButton clearTargetListButton;
	private JButton newTargetButton;
	
	public GUI(Service service) {
		this.service = service;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 1.0 };
		getContentPane().setLayout(gridBagLayout);
		
		JPanel globalPanel = new JPanel();
		globalPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagConstraints gbc_globalPanel = new GridBagConstraints();
		gbc_globalPanel.gridheight = 4;
		gbc_globalPanel.gridwidth = 4;
		gbc_globalPanel.fill = GridBagConstraints.BOTH;
		gbc_globalPanel.gridx = 0;
		gbc_globalPanel.gridy = 0;
		getContentPane().add(globalPanel, gbc_globalPanel);
		GridBagLayout gbl_globalPanel = new GridBagLayout();
		gbl_globalPanel.columnWeights = new double[] { 1.0, 0.0, 1.0, 0.0, 0.0 };
		gbl_globalPanel.rowWeights = new double[] { 0.0, 5.0, 0.0, 1.0 };
		globalPanel.setLayout(gbl_globalPanel);
		
		sourceDirField = new JTextField();
		sourceDirField.setEditable(false);
		GridBagConstraints gbc_sourceDirField = new GridBagConstraints();
		gbc_sourceDirField.insets = new Insets(0, 0, 5, 5);
		gbc_sourceDirField.fill = GridBagConstraints.HORIZONTAL;
		gbc_sourceDirField.gridx = 0;
		gbc_sourceDirField.gridy = 0;
		globalPanel.add(sourceDirField, gbc_sourceDirField);
		
		sourceDirButton = new JButton("Source");
		sourceDirButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectSource();
			}
		});
		GridBagConstraints gbc_sourceDirButton = new GridBagConstraints();
		gbc_sourceDirButton.insets = new Insets(0, 0, 5, 5);
		gbc_sourceDirButton.gridx = 1;
		gbc_sourceDirButton.gridy = 0;
		globalPanel.add(sourceDirButton, gbc_sourceDirButton);
		
		targetDirField = new JTextField();
		targetDirField.setEditable(false);
		GridBagConstraints gbc_targetDirField = new GridBagConstraints();
		gbc_targetDirField.insets = new Insets(0, 0, 5, 5);
		gbc_targetDirField.fill = GridBagConstraints.HORIZONTAL;
		gbc_targetDirField.gridx = 2;
		gbc_targetDirField.gridy = 0;
		globalPanel.add(targetDirField, gbc_targetDirField);
		
		targetDirButton = new JButton("Target");
		targetDirButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectTarget();
			}
		});
		
		newTargetButton = new JButton("New");
		newTargetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectNewTarget();
			}
		});
		GridBagConstraints gbc_newTargetButton = new GridBagConstraints();
		gbc_newTargetButton.insets = new Insets(0, 0, 5, 5);
		gbc_newTargetButton.gridx = 3;
		gbc_newTargetButton.gridy = 0;
		globalPanel.add(newTargetButton, gbc_newTargetButton);
		GridBagConstraints gbc_targetDirButton = new GridBagConstraints();
		gbc_targetDirButton.insets = new Insets(0, 0, 5, 0);
		gbc_targetDirButton.gridx = 4;
		gbc_targetDirButton.gridy = 0;
		globalPanel.add(targetDirButton, gbc_targetDirButton);
		
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.gridwidth = 5;
		gbc_splitPane.insets = new Insets(0, 0, 5, 0);
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 1;
		globalPanel.add(splitPane, gbc_splitPane);
		
		sourceTableScrollPane = new JScrollPane();
		splitPane.setLeftComponent(sourceTableScrollPane);
		
		sourceTable = new JTable() {
			private static final long serialVersionUID = 8417208869924403770L;
			
			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				DefaultTableCellRenderer cellRenderer = (DefaultTableCellRenderer) super.getCellRenderer(row, column);
				// grey background if already selected, white else
				if (targetTableAlreadyContainsSourceFile(sourceTableModel.get(row))) {
					cellRenderer.setBackground(Color.LIGHT_GRAY);
				} else {
					cellRenderer.setBackground(Color.WHITE);
				}
				return cellRenderer;
			}
		};
		sourceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sourceTableScrollPane.setViewportView(sourceTable);
		
		targetTableScrollPane = new JScrollPane();
		splitPane.setRightComponent(targetTableScrollPane);
		
		targetTable = new JTable();
		targetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		targetTableScrollPane.setViewportView(targetTable);
		
		buttonPanel = new JPanel();
		GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
		gbc_buttonPanel.anchor = GridBagConstraints.EAST;
		gbc_buttonPanel.gridwidth = 5;
		gbc_buttonPanel.insets = new Insets(0, 0, 5, 0);
		gbc_buttonPanel.fill = GridBagConstraints.VERTICAL;
		gbc_buttonPanel.gridx = 0;
		gbc_buttonPanel.gridy = 2;
		globalPanel.add(buttonPanel, gbc_buttonPanel);
		GridBagLayout gbl_buttonPanel = new GridBagLayout();
		gbl_buttonPanel.columnWidths = new int[] { 0, 0, 40, 0 };
		gbl_buttonPanel.rowHeights = new int[] { 0, 0 };
		gbl_buttonPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		gbl_buttonPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		buttonPanel.setLayout(gbl_buttonPanel);
		
		createScriptButton = new JButton("Create Script");
		GridBagConstraints gbc_createScriptButton = new GridBagConstraints();
		gbc_createScriptButton.insets = new Insets(0, 0, 0, 5);
		gbc_createScriptButton.gridx = 0;
		gbc_createScriptButton.gridy = 0;
		buttonPanel.add(createScriptButton, gbc_createScriptButton);
		
		moveFilesButton = new JButton("Move Files");
		GridBagConstraints gbc_moveFilesButton = new GridBagConstraints();
		gbc_moveFilesButton.insets = new Insets(0, 0, 0, 5);
		gbc_moveFilesButton.gridx = 1;
		gbc_moveFilesButton.gridy = 0;
		buttonPanel.add(moveFilesButton, gbc_moveFilesButton);
		
		clearTargetListButton = new JButton("Clear Target List");
		GridBagConstraints gbc_clearTargetListButton = new GridBagConstraints();
		gbc_clearTargetListButton.gridx = 3;
		gbc_clearTargetListButton.gridy = 0;
		buttonPanel.add(clearTargetListButton, gbc_clearTargetListButton);
		moveFilesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveFiles();
			}
		});
		createScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createScript();
			}
		});
		clearTargetListButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				targetTableModel.clear();
				targetTable.revalidate();
				LOG.debug("removed all target files");
			}
		});
		
		logScrollPane = new JScrollPane();
		GridBagConstraints gbc_logScrollPane = new GridBagConstraints();
		gbc_logScrollPane.gridwidth = 5;
		gbc_logScrollPane.fill = GridBagConstraints.BOTH;
		gbc_logScrollPane.gridx = 0;
		gbc_logScrollPane.gridy = 3;
		globalPanel.add(logScrollPane, gbc_logScrollPane);
		
		log = new JTextArea();
		logScrollPane.setViewportView(log);
		log.setLineWrap(true);
		log.setEditable(false);
		
		setBounds(100, 100, 900, 730);
		setTitle("AlbumCreator");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// save settings
				service.saveSettings(settings);
				// exit
				dispose();
				System.exit(0);
			}
		});
		
		settings = service.loadSettings();
		initComponents();
	}
	
	private void initComponents() {
		sourceTable.setShowVerticalLines(false);
		targetTable.setShowVerticalLines(false);
		
		sourceTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = sourceTable.rowAtPoint(e.getPoint());
				if (e.getClickCount() % 2 == 0 && row >= 0) {
					// double click on an existing row
					SourceFile sourceFile = sourceTableModel.get(row);
					if (targetTableAlreadyContainsSourceFile(sourceFile)) {
						log("not added \"" + sourceFile.getPath().toString()
							+ "\" because file is already in target list");
					} else {
						TargetFile targetFile = new TargetFile(sourceFile);
						targetTableModel.add(targetFile);
						targetTable.revalidate();
						LOG.debug("created target file for track " + targetFile.getTrackNumber());
					}
					sourceTable.getSelectionModel().clearSelection();
				}
			}
		});
		targetTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = targetTable.rowAtPoint(e.getPoint());
				if (e.getClickCount() % 2 == 0 && row >= 0) {
					// double click on an existing row
					targetTableModel.remove(row);
					targetTable.revalidate();
					// refresh row coloring:
					sourceTable.revalidate();
					LOG.debug("removed target file on row " + row);
				}
			}
		});
		
		sourceTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				LocalDateTime reference = LocalDateTime.now().minus(Duration.of(500, ChronoUnit.MILLIS));
				if (filterTimestamp == null || filterTimestamp.isBefore(reference)) {
					// start new filter
					filterString = String.valueOf(e.getKeyChar()).toLowerCase();
				} else {
					// append to filter
					filterString += String.valueOf(e.getKeyChar()).toLowerCase();
				}
				LOG.debug("searching for {}", filterString);
				filterTimestamp = LocalDateTime.now();
				List<SourceFile> sourceFiles = sourceTableModel.getFiles();
				for (int i = 0; i < sourceFiles.size(); i++) {
					SourceFile file = sourceFiles.get(i);
					if (file.getArtist().toLowerCase().startsWith(filterString)) {
						sourceTable.getSelectionModel().setSelectionInterval(i, i);
						sourceTable.scrollRectToVisible(sourceTable.getCellRect(i, 0, true));
						break;
					}
				}
			}
		});
		
		sourceDirField.setText(settings.getSourceDirectory());
		targetDirField.setText(settings.getTargetDirectory());
		sourceTableModel = new SourceTableModel();
		sourceTable.setModel(sourceTableModel);
		targetTableModel = new TargetTableModel();
		targetTable.setModel(targetTableModel);
		
		readSourceFiles();
	}
	
	private boolean targetTableAlreadyContainsSourceFile(SourceFile sourceFile) {
		return targetTableModel.getFiles().stream()
			.filter(targetFile -> targetFile.getSourceFile().equals(sourceFile))
			.findFirst().isPresent();
	}
	
	private void readSourceFiles() {
		sourceTableModel.clear();
		Path sourcePath = Paths.get(sourceDirField.getText());
		
		try {
			List<SourceFile> sourceFiles = service.readSourceFiles(sourcePath);
			sourceTableModel.addAll(sourceFiles);
			sourceTable.revalidate();
			log("found " + sourceFiles.size() + " music files");
		} catch (IOException | UncheckedIOException e) {
			log("ERROR: can't read files in \"" + sourcePath + "\"");
			LOG.error("exception when reading files in \"" + sourcePath + "\"", e);
		}
	}
	
	private void selectSource() {
		boolean successful = selectDirectory(sourceDirField, "Select Source Directory", "source directory", false);
		if (successful) {
			settings.setSourceDirectory(sourceDirField.getText());
			readSourceFiles();
		}
	}
	
	private void selectTarget() {
		boolean successful = selectDirectory(targetDirField, "Select Target Directory", "target directory", true);
		if (successful) {
			settings.setTargetDirectory(targetDirField.getText());
		}
	}
	
	private void selectNewTarget() {
		Path currentPath = Paths.get(targetDirField.getText());
		Path parentPath = currentPath.getParent().toAbsolutePath();
		if (Files.isDirectory(parentPath) && Files.isWritable(parentPath)) {
			String subDirectoryToCreate = JOptionPane.showInputDialog(this, "Create a new subdirectory below \""
				+ parentPath.toString() + "\" with the name:",
				currentPath.getFileName().toString().replaceAll("/", ""));
			if (subDirectoryToCreate != null && !subDirectoryToCreate.trim().isEmpty()) {
				Path pathToCreate = parentPath.resolve(Paths.get(subDirectoryToCreate));
				boolean success = service.createNewDirectory(pathToCreate, this);
				if (success) {
					String newTargetDir = pathToCreate.toAbsolutePath().toString();
					targetDirField.setText(newTargetDir);
					settings.setTargetDirectory(newTargetDir);
				}
			}
		} else {
			log("ERROR: parent directory " + parentPath.toString() + " is not writable");
		}
	}
	
	private boolean selectDirectory(JTextField field, String windowTitle, String logTitle, boolean checkWriteAccess) {
		JFileChooser fileChooser = new JFileChooser();
		String pathname = field.getText();
		if (pathname != null) {
			File currentDir = new File(pathname);
			if (currentDir.isDirectory() && currentDir.canRead()) {
				fileChooser.setSelectedFile(currentDir);
			} else if (currentDir.getParentFile().isDirectory() && currentDir.getParentFile().canRead()) {
				// try parent directory
				fileChooser.setSelectedFile(currentDir.getParentFile());
			}
		}
		fileChooser.setApproveButtonText("Select");
		fileChooser.setDialogTitle(windowTitle);
		
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int returnVal = fileChooser.showDialog(this, null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (file.isDirectory() && file.canRead() && (!checkWriteAccess || file.canWrite())) {
				try {
					field.setText(file.getCanonicalPath());
					return true;
				} catch (IOException e) {
					log("ERROR: can't validate " + logTitle + " \"" + file.getPath() + "\"");
					return false;
				}
			} else {
				log("ERROR: can't access " + logTitle + " \"" + file.getAbsolutePath() + "\" for "
					+ (checkWriteAccess ? "writing" : "reading"));
				return false;
			}
		} else {
			return false;
		}
	}
	
	private void createScript() {
		service.createScript(Paths.get(targetDirField.getText()), targetTableModel.getFiles(), this);
	}
	
	private void moveFiles() {
		List<TargetFile> files = targetTableModel.getFiles();
		int dialogResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to move " + files.size()
			+ " files to \"" + targetDirField.getText() + "\"?", "Question", JOptionPane.YES_NO_OPTION);
		if (dialogResult == JOptionPane.YES_OPTION) {
			boolean successful = service.moveFiles(Paths.get(targetDirField.getText()), files, this);
			readSourceFiles();
			if (successful) {
				targetTableModel.clear();
				targetTable.revalidate();
			}
		}
	}
	
	@Override
	public synchronized void log(String text) {
		LOG.info(text);
		log.append(text);
		log.append("\n");
		logScrollPane.getViewport()
			.setViewPosition(new Point(0, log.getSize().height - logScrollPane.getSize().height));
	}
	
}
