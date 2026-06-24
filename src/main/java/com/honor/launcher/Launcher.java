package com.honor.launcher;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import com.honor.blitzremake.Game;
import com.honor.blitzremake.util.Util;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Swing launcher. Temporarily retained from the 2015 build; the Swing UI will
 * be replaced by an in-window GLFW settings screen in Step 1.5.
 *
 * The only change in 1.3 is the resolution source: the old code enumerated
 * {@code org.lwjgl.opengl.Display.getAvailableDisplayModes()} (LWJGL 2),
 * which no longer exists. We now query {@code glfwGetVideoModes} for the
 * primary monitor, dedupe by width x height, and present a list of
 * {@link Resolution} values in the combo box.
 */
public class Launcher extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JComboBox<Resolution> cb_Res;
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

		cb_Res = new JComboBox<Resolution>();
		cb_Res.setBounds(34, 142, 202, 20);
		contentPane.add(cb_Res);
		WindowedClick();

		int w = Util.getProperty("Width");
		int h = Util.getProperty("Height");

		int selectedIndex = 0;
		Resolution[] modes = enumerateResolutions();
		for (int i = 0; i < modes.length; i++) {
			Resolution current = modes[i];
			cb_Res.addItem(current);
			if (w == current.width() && h == current.height())
				selectedIndex = i;
		}
		if (cb_Res.getItemCount() > 0)
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

	/** Deduped width x height pair shown in the resolution combo box. */
	public static record Resolution(int width, int height) {
		@Override
		public String toString() {
			return width + "x" + height;
		}
	}

	private static Resolution[] enumerateResolutions() {
		if (!GLFW.glfwInit()) {
			System.err.println("Could not initialize GLFW to enumerate resolutions.");
			return new Resolution[] { new Resolution(Game.WIDTH, Game.HEIGHT) };
		}
		try {
			long monitor = GLFW.glfwGetPrimaryMonitor();
			GLFWVidMode.Buffer buffer = GLFW.glfwGetVideoModes(monitor);
			Set<Resolution> dedup = new LinkedHashSet<>();
			if (buffer != null) {
				while (buffer.hasRemaining()) {
					GLFWVidMode mode = buffer.get();
					dedup.add(new Resolution(mode.width(), mode.height()));
				}
			}
			if (dedup.isEmpty()) {
				dedup.add(new Resolution(Game.WIDTH, Game.HEIGHT));
			}
			return dedup.toArray(new Resolution[0]);
		} finally {
			GLFW.glfwTerminate();
		}
	}

	private void WindowedClick() {
		if (chbWindowed.isSelected())
			cb_Res.setEnabled(false);
		else
			cb_Res.setEnabled(true);
	}

	private void save() {
		int windowed = chbWindowed.isSelected() ? 1 : 0;
		Resolution mode = (Resolution) cb_Res.getSelectedItem();
		Util.writeXml(windowed, mode.width(), mode.height());
	}

}