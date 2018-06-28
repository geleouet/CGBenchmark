package fr.egaetan.cgbench.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class DockableTabbedPane extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	public DockableTabbedPane() {
		super();
		TabDragListener tabDragger = new TabDragListener();
		this.addMouseListener(tabDragger);
		this.addMouseMotionListener(tabDragger);
	}

	private class TabDragListener implements MouseListener, MouseMotionListener {

		Point p0;

		Component current;
		String title;

		public TabDragListener() {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			p0 = e.getPoint();

			for (int i = 0; i < getTabCount(); i++) {
				Rectangle bounds = getBoundsAt(i);
				if (bounds.contains(p0)) {
					current = DockableTabbedPane.this.getComponentAt(i);
					title = DockableTabbedPane.this.getTitleAt(i);
					getLocationOnScreen();
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			Point p = e.getPoint();
			if (current != null) {
				// check for a significant drag
				if (p.distance(p0) > 20) {

					undock(current, title);
					current = null;

				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {/**/}

		@Override
		public void mouseClicked(MouseEvent arg0) {/**/}

		@Override
		public void mouseEntered(MouseEvent arg0) {/**/}

		@Override
		public void mouseExited(MouseEvent arg0) {/**/}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			current = null;
			title = null;
		}
	}

	UndockedFrame undock(Component current, String title) {

		Point p = current.getLocationOnScreen();
		remove(current);
		UndockedFrame frame = new UndockedFrame(current, title);

		p.translate(20, 20);
		frame.setLocation(p);
		frame.setVisible(true);
		fireStateChanged();
		return frame;

	}

	private class UndockedFrame extends JFrame {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Component current;
		String title;

		public UndockedFrame(Component current, String title) {
			this.current = current;

			this.setTitle(title);

			Container content = this.getContentPane();
			content.setLayout(new BorderLayout());
			content.add(current, BorderLayout.CENTER);
			this.setBounds(current.getBounds());
			this.addWindowListener(new UndockedFrameListener());
			this.addWindowStateListener(new WindowStateListener() {
				
				@Override
				public void windowStateChanged(WindowEvent e) {
					if (e.getNewState() == WindowEvent.WINDOW_ICONIFIED) {
						Window w = e.getWindow();
						if (w instanceof UndockedFrame) {
							UndockedFrame frame = (UndockedFrame) w;
							frame.redock();
						}
					}
				}
			});
		}

		public void redock() {

			this.dispose();
			DockableTabbedPane.this.add(title, current);

		}
	}

	// Redock on close
	private class UndockedFrameListener extends WindowAdapter {
		public UndockedFrameListener() {
		}

		@Override
		public void windowClosing(WindowEvent e) {
			Window w = e.getWindow();
			if (w instanceof UndockedFrame) {
				UndockedFrame frame = (UndockedFrame) w;
				frame.redock();
			}
		}
	}

}