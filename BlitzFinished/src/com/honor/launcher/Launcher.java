package com.honor.launcher;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.honor.blitzremake.Game;
import com.honor.blitzremake.util.Util;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;

public class Launcher extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JComboBox<DisplayMode> cb_Res;
	private JCheckBox chbWindowed;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Launcher frame = new Launcher();
					frame.setTitle("Blitz Launcher");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Launcher() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}

		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 277, 376);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		chbWindowed = new JCheckBox("Windowed");
		chbWindowed.setBounds(10, 80, 112, 23);
		chbWindowed.addActionListener(e -> WindowedClick());
		int wi = Util.getProperty("Windowed");
		chbWindowed.setSelected(wi == 0 ? false : true);
		contentPane.add(chbWindowed);

		cb_Res = new JComboBox<DisplayMode>();
		cb_Res.setBounds(34, 142, 202, 20);
		contentPane.add(cb_Res);
		WindowedClick();

		int w = Util.getProperty("Width");
		int h = Util.getProperty("Height");

		DisplayMode[] modes;
		int selectedIndex = 0;
		try {
			modes = Display.getAvailableDisplayModes();
			for (int i = 0; i < modes.length; i++) {
				DisplayMode current = modes[i];
				cb_Res.addItem(current);
				if (w == current.getWidth() && h == current.getHeight())
					selectedIndex = i;
				// System.out.println(current.getWidth() + "x" + current.getHeight() + "x" +
				// current.getBitsPerPixel() + " " + current.getFrequency() + "Hz");
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		cb_Res.setSelectedIndex(selectedIndex);

		JButton btnSave = new JButton("Save & Exit");
		btnSave.setBounds(10, 301, 89, 23);
		btnSave.addActionListener(e -> {
			save();
			System.exit(0);
		});
		contentPane.add(btnSave);

		JButton btnSaveLaunch = new JButton("Save & Launch");
		btnSaveLaunch.setBounds(158, 301, 103, 23);
		btnSaveLaunch.addActionListener(e -> {
			save();
			dispose();
			new Game().start();
		});
		contentPane.add(btnSaveLaunch);

		JLabel lblResolutions = new JLabel("Resolutions:");
		lblResolutions.setBounds(10, 110, 78, 23);
		contentPane.add(lblResolutions);

		JLabel lblLauncher = new JLabel("Launcher");
		lblLauncher.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblLauncher.setBounds(98, 11, 74, 39);
		contentPane.add(lblLauncher);
	}

	private void WindowedClick() {
		if (chbWindowed.isSelected())
			cb_Res.setEnabled(false);
		else
			cb_Res.setEnabled(true);
	}

	private void save() {
		int windowed = chbWindowed.isSelected() ? 1 : 0;
		DisplayMode mode = (DisplayMode) cb_Res.getSelectedItem();
		Util.writeXml(windowed, mode.getWidth(), mode.getHeight());
		// write boolean windowed
		// write resolution
		// width
		// height
		// bpp
		// fr
	}

}
