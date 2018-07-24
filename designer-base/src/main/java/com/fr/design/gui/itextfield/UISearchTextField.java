package com.fr.design.gui.itextfield;


import com.fr.design.constants.UIConstants;
import com.fr.design.gui.ilable.UILabel;


import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;

/**
 * Created by IntelliJ IDEA.
 * Author : Richer
 * Version: 7.0.3
 * Date: 13-1-4
 * Time: 上午11:07
 * 带图标的文本框，可以用作搜索等用途
 */
public class UISearchTextField extends UITextField {

	private UILabel iconLabel = new UILabel(UIConstants.BLACK_SEARCH_ICON);
	private UILabel clearLabel = new UILabel(UIConstants.CLEAR_ICON);
	private UILabel infoLabel = new UILabel(com.fr.design.i18n.Toolkit.i18nText("Search"));
	private Dimension iconSize;
	private Dimension infoSize;

	private int iconPosition = SwingConstants.LEFT;
	private boolean showClearIcon = true;

	private CellRendererPane cellRendererPane = new CellRendererPane();

	public UISearchTextField() {
		initTextField();
	}

	public UISearchTextField(String text) {
		super(text);
		initTextField();
	}

	public UISearchTextField(int columns) {
		super(columns);
		initTextField();
	}

	public UISearchTextField(String text, int columns) {
		super(text, columns);
		initTextField();
	}

	public UISearchTextField(Document doc, String text, int columns) {
		super(doc, text, columns);
		initTextField();
	}

	private void initTextField() {
		iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		iconLabel.setToolTipText(com.fr.design.i18n.Toolkit.i18nText("Search"));
		clearLabel.setToolTipText(com.fr.design.i18n.Toolkit.i18nText("Clear"));
		clearLabel.setOpaque(false);
		iconSize = iconLabel.getPreferredSize();
		infoSize = infoLabel.getPreferredSize();
		infoLabel.setEnabled(false);
		this.addMouseListener(new MouseAdapter() {
			public void mouseExited(MouseEvent e) {
				setMouseComponent(null);
			}

			public void mouseClicked(MouseEvent e) {
				if (mouseCom == clearLabel) {
					setText("");
				} else if (mouseCom == iconLabel) {
					handleIconClick();
				}
			}
		});
		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				JComponent com = getComponentAtPoint(e.getPoint());
				setMouseComponent(com);
			}

			public void mouseDragged(MouseEvent e) {
			}
		});
		this.addFocusListener(new FocusListener() {

			public void focusLost(FocusEvent e) {
				repaint();
			}

			public void focusGained(FocusEvent e) {
				repaint();
			}
		});
	}

	private void handleIconClick() {

	}

	public String getToolTipText(MouseEvent event) {
		event.getPoint();
		if (mouseCom == null) {
			return null;
		}
		if (mouseCom == this) {
			return super.getToolTipText(event);
		}
		return mouseCom.getToolTipText(event);
	}

	private int iconGap = 3;

	public Insets getInsets() {
		Insets insets = super.getInsets();
		if (iconPosition == SwingConstants.LEFT) {
			insets.left += iconSize.width + iconGap + 2;
			if (this.getText() != null && this.getText().length() != 0 && showClearIcon) {
				insets.right += iconSize.width + iconGap + 2;
			} else {
				insets.right += iconGap;
			}
		} else {
			insets.left += iconGap;
			if (this.getText() != null && this.getText().length() != 0 && showClearIcon) {
				insets.right += iconSize.width + iconGap + 2;
			} else {
				insets.right += iconGap;
			}
		}
		return insets;
	}

	private JComponent getComponentAtPoint(Point point) {
		int x = (int) point.getX();
		int y = (int) point.getY();
		if (!contains(x, y)) {
			return null;
		}

		if (iconPosition == SwingConstants.LEFT) {
			if (x < iconSize.width + iconGap + 2) {
				return iconLabel;
			}
			if (getText() != null && getText().length() != 0 && showClearIcon) {
				if (x > getWidth() - iconSize.width - iconGap) {
					return clearLabel;
				}
			}
		} else {
			if (this.getText() == null || this.getText().length() == 0) {
				if (x > getWidth() - iconSize.width - iconGap - 2) {
					return iconLabel;
				}
			}
			if (getText() != null && getText().length() != 0 && showClearIcon) {
				if (x > getWidth() - iconSize.width - iconGap - 2) {
					return clearLabel;
				}
			}

		}
		return this;
	}

	private JComponent mouseCom;

	private void setMouseComponent(JComponent com) {
		if (mouseCom == com) {
			return;
		}
		if (mouseCom != null && mouseCom instanceof UILabel) {
			mouseCom.setBorder(null);
		}
		mouseCom = com;
		if (mouseCom != null && mouseCom instanceof UILabel) {
			mouseCom.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 1));
		}
		this.repaint();
		if (mouseCom == null || mouseCom == clearLabel) {
			setCursor(Cursor.getDefaultCursor());
			return;
		}
		if (mouseCom == this) {
			setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			return;
		}
		if (mouseCom == iconLabel) {
			setCursor(iconLabel.getCursor());
		}
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		int y = (int) (this.getHeight() / 2.0 - iconSize.getHeight() / 2);
		if (iconPosition == SwingConstants.LEFT) {
			cellRendererPane.paintComponent(g2d, iconLabel, this, iconGap, y, iconSize.width, iconSize.height);
			if (this.getText() != null && this.getText().length() != 0 && showClearIcon) {
				cellRendererPane.paintComponent(g2d, clearLabel, this, this.getWidth() - iconSize.width - iconGap, y, iconSize.width, iconSize.height);
			}
		} else {
			if (this.getText() == null || this.getText().length() == 0) {
				cellRendererPane.paintComponent(g2d, iconLabel, this, this.getWidth() - iconSize.width - iconGap, y, iconSize.width, iconSize.height);
			} else {
				if (showClearIcon) {
					cellRendererPane.paintComponent(g2d, clearLabel, this, this.getWidth() - iconSize.width - iconGap, y, iconSize.width, iconSize.height);
				}
			}
		}

		if (!this.isFocusOwner()) {
			if (this.getText() == null || this.getText().length() == 0) {
				int x = iconGap + 2;
				if (iconPosition == SwingConstants.LEFT) {
					x = iconSize.width + iconGap + 2;
				}
				y = (int) (this.getHeight() / 2.0 - infoSize.getHeight() / 2);
				cellRendererPane.paintComponent(g2d, infoLabel, this, x, y, infoSize.width, infoSize.height);
			}
		}
	}

	public Dimension getPreferredSize() {
		Insets s = super.getInsets();
		Insets t = this.getInsets();
		Dimension preferredSize = super.getPreferredSize();
		preferredSize.setSize(preferredSize.width - (t.left + t.right - s.left - s.right), preferredSize.height + 2);
		return preferredSize;
	}

	protected void paintBorder(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (this.isFocusOwner()) {
			g2d.setStroke(new BasicStroke(1.5f));
		} else {
			g2d.setStroke(new BasicStroke(1f));
		}
		RoundRectangle2D.Double rect = new RoundRectangle2D.Double(0, 0, this.getWidth() - 2, this.getHeight() - 2, 4, 4);
		g2d.setColor(UIConstants.SHADOW_GREY);
		g2d.draw(rect);
	}

	public int getIconPosition() {
		return iconPosition;
	}

	public void setIconPosition(int iconPosition) {
		this.iconPosition = iconPosition;
		this.invalidate();
		this.repaint();
	}

	public boolean isShowClearIcon() {
		return showClearIcon;
	}

	public void setShowClearIcon(boolean showClearIcon) {
		this.showClearIcon = showClearIcon;
		this.repaint();
	}

	public static void main(String[] args) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		UISearchTextField comp = new UISearchTextField(20);
		p.add(comp);
		comp = new UISearchTextField(20);
		comp.setShowClearIcon(false);
		p.add(comp);
		comp = new UISearchTextField(20);
		comp.setIconPosition(SwingConstants.RIGHT);
		p.add(comp);
		comp = new UISearchTextField(20);
		comp.setShowClearIcon(false);
		comp.setIconPosition(SwingConstants.RIGHT);
		p.add(comp);
		p.add(new JTextField(20));
		JFrame frame = new JFrame();
		frame.setTitle("UITextField");
		frame.setContentPane(p);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 200);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}