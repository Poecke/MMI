import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.JToolBar;

public class mmiPaint extends JPanel implements ActionListener {

	protected mmiCanvas canvas;
	
	public mmiPaint() {
		super(new BorderLayout());

		// Create the toolbars.
		JButton button = null;
		
		JToolBar toolBar = new JToolBar("Tools");
		toolBar.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		button = makeToolBarButton("document-new", "Nieuwe tekening", "Nieuw");
		toolBar.add(button);
		toolBar.addSeparator(new Dimension(50,1));
		button = makeToolBarButton("edit-delete", "Verwijder een vorm", "Verwijderen");
		//toolBar.add(button);
		//button = makeToolBarButton("go-down", "Verplaats de vorm naar beneden", "Lager");
		//toolBar.add(button);
		//button = makeToolBarButton("go-up", "Verplaats de vorm naar boven", "Hoger");
		toolBar.add(button);
		toolBar.addSeparator(new Dimension(50,1));
		button = makeToolBarButton("mouse", "Gebruik de muis om vormen te vergroten of verkleinen, of ze te verplaatsen", "Muis");
		toolBar.add(button);
		button = makeToolBarButton("color-fill", "Vul een gebied met een kleur", "Kleur"); 
		toolBar.add(button);
		toolBar.addSeparator(new Dimension(50,1));
                button = makeToolBarButton("draw-triangle", "Teken een driehoek", "Driehoek");
		toolBar.add(button);
		//JToolBar shapeBar = new JToolBar("Shapes");
		button = makeToolBarButton("draw-rectangle", "Teken een Rechthoek of Vierkant", "Rechthoek");
		//shapeBar.add(button);
		toolBar.add(button);
		button = makeToolBarButton("draw-ellipse", "Teken een ellips of cirkel", "Ellips");
		toolBar.add(button);


		// create the canvas		
		canvas = new mmiCanvas();
		JScrollPane scrollPane = new JScrollPane(canvas);		
		JPanel colorsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		colorsPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		makeColorButton(Color.red, colorsPanel);
                makeColorButton(Color.green, colorsPanel);
		makeColorButton(Color.blue, colorsPanel);
		makeColorButton(Color.yellow, colorsPanel);
                makeColorButton(Color.magenta, colorsPanel); 
                makeColorButton(Color.pink, colorsPanel);
		makeColorButton(new Color(160, 32, 240), colorsPanel); // purple
		makeColorButton(Color.orange, colorsPanel);
		makeColorButton(Color.black, colorsPanel);
                makeColorButton(Color.white, colorsPanel);
		makeColorButton(new Color(165, 42, 42), colorsPanel); // brown	
	
		// Lay out the main panel.
		setPreferredSize(new Dimension(800,	600));
		//add(toolBarsPanel, BorderLayout.PAGE_START);
		add(toolBar, BorderLayout.PAGE_START);
		add(scrollPane, BorderLayout.CENTER);
		add(colorsPanel, BorderLayout.PAGE_END);
	}
	
	private void makeColorButton(Color color, JPanel colorsPanel){
         	JButton colorButton = new JButton();
            colorButton.setBackground(color);
            colorButton.setAction(setColor);
            //colorButton.setText(" ");
            colorButton.setPreferredSize(new Dimension(30,30));
            colorsPanel.add(colorButton);
	}

	Action setColor  = new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
            JButton button = (JButton)evt.getSource();
            canvas.setFillColor(button.getBackground());
        }
    };
	
	protected JButton makeToolBarButton(String actionCommand, String toolTipText, String altText) {
		// Look for the image.
		String imgLocation = actionCommand + ".png";
		URL imageURL = mmiPaint.class.getResource(imgLocation);

		// Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else { // no image found
			button.setText(altText);
			System.err.println("Pictogram niet gevonden: " + imgLocation);
		}

		return button;
	}	

	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("mmiPaint");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("face-monkey.png"));

		// Create and set up the content pane.
		mmiPaint newContentPane = new mmiPaint();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});

	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
        String cmd = e.getActionCommand();
        canvas.addTimestamp("KNOP: " + cmd);
       if (cmd == "draw-rectangle")
       {
    	   canvas.setRunningMode(Mode.newRect);
       }
       else if (cmd == "draw-ellipse")
       {
    	   canvas.setRunningMode(Mode.newEllipse);
       }
       else if (cmd == "draw-triangle")
       {
    	   canvas.setRunningMode(Mode.newTriangle);
       }
       else if (cmd == "document-new")
       {    	   
    	   int filename = (int)(999999*Math.random()) + 1;
    	   canvas.writeTimestampsToFile(filename + ".txt");
    	   canvas.clearScreen();
       }
       else if (cmd == "color-fill")
       {
    	   canvas.setRunningMode(Mode.fill);    		
       }
       else if (cmd == "edit-delete")
       {
    	   canvas.deleteSelected();
       }
       else if (cmd == "go-down")
       {
    	  // canvas.zSelected(-1);
       }
       else if (cmd == "go-up")
       {
    	   //canvas.zSelected(1);
    	   
       }
       else if (cmd == "mouse")
       {
    	   canvas.setRunningMode(Mode.cursor);
    	   canvas.unselectShape();
       }

	}
}
