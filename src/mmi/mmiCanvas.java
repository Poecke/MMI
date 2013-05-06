import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import nl.ru.nici.cai.practica.DrawingCanvas;

public class mmiCanvas extends DrawingCanvas {

	private Shape selectedShape = null;

	private int runningz = 0;

	private Mode runningMode = Mode.cursor;

	private Mode previousMode = Mode.cursor;

	private Color fillColor = Color.white;

	private Shape hoeken[] = new Shape[8];

	private int selectedHoek = -1;

	private Point dragOffset = null;

	public mmiCanvas() {
		clearScreen();

		for (int i = 0; i < 8; i++) {
			hoeken[i] = new Rectangle();
		}
	}

	public void setFillColor(Color color) {
		fillColor = color;
	}

	public void setRunningMode(Mode mode) {
		addTimestamp("setRunningMode:" + mode);
		
		runningMode = mode;
		if (mode == Mode.fill) {
			Toolkit tk = Toolkit.getDefaultToolkit();
			Image image = tk.getImage("color-fill.png");
			Cursor c = tk.createCustomCursor(image, new Point(0, 0),
					"cursorName");
			setCursor(c);
			unselectShape();
		} else if (mode == Mode.move) {
			setCursor(new Cursor(Cursor.MOVE_CURSOR));
		} else
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public void clearScreen() {
		Shape shapes[] = getSortedShapes();

		for (int i = 0; i < shapes.length; i++) {
			removeShape(shapes[i]);
		}
		setBackgroundColor(Color.white);
		setFillColor(Color.white);
		runningz = 0;
		repaint();
	}

	public void deleteSelected() {
		if (selectedShape != null) {
			removeShape(selectedShape);
			unselectShape();
			repaint();
		}
	}

	public void unselectShape() {
		for (int i = 0; i < 8; i++) {
			removeShape(hoeken[i]);
		}
		repaint();

		selectedShape = null;
	}

	public void selectShape(Shape shape) {
		unselectShape();

		if (shape != null) {

			final int blocksize = 10;

			selectedShape = shape;
			Rectangle bounds = shape.getBounds();
			hoeken[0] = addRectangle(bounds.x - blocksize / 2, bounds.y
					- blocksize / 2, 10, 10, runningz + 1);
			hoeken[1] = addRectangle(bounds.x - blocksize / 2, bounds.height
					+ bounds.y - blocksize / 2, 10, 10, runningz + 1);
			hoeken[2] = addRectangle(bounds.width + bounds.x - blocksize / 2,
					bounds.y - blocksize / 2, 10, 10, runningz + 1);
			hoeken[3] = addRectangle(bounds.width + bounds.x - blocksize / 2,
					bounds.height + bounds.y - blocksize / 2, 10, 10,
					runningz + 1);

			hoeken[4] = addRectangle(bounds.width / 2 + bounds.x - blocksize
					/ 2, bounds.y + bounds.height - blocksize / 2, 10, 10,
					runningz + 1);
			hoeken[5] = addRectangle(bounds.x - blocksize / 2, bounds.height
					/ 2 + bounds.y - blocksize / 2, 10, 10, runningz + 1);
			hoeken[6] = addRectangle(bounds.width / 2 + bounds.x - blocksize
					/ 2, bounds.y - blocksize / 2, 10, 10, runningz + 1);
			hoeken[7] = addRectangle(bounds.width + bounds.x - blocksize / 2,
					bounds.height / 2 + bounds.y - blocksize / 2, 10, 10,
					runningz + 1);

			for (int i = 0; i < 8; i++) {
				setFillColorForShape(Color.white, hoeken[i]);
			}

			repaint();
			if (runningMode != Mode.move && runningMode != Mode.resize) {
				previousMode = runningMode;
				runningMode = Mode.resize;
			}
		} else // shape == null
		{
			setRunningMode(previousMode);
		}
	}

	private Shape addTriangle(float x, float y, float w, float h, float z) {

		int[] tx = { (int) x, (int) x + (int) w / 2, (int) x + (int) w };
		int[] ty = { (int) y + (int) h, (int) y, (int) y + (int) h };

		Shape shape = addPolygon(tx, ty, 3, z);
		setFillColorForShape(fillColor, shape);
		repaint();
		return shape;
	}

	public void zSelected(int add) {
		if (selectedShape != null) {
			setZPosition(getZPosition(selectedShape) + add, selectedShape);
			repaint();
		}
	}

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		if (runningMode == Mode.move) {
			dragOffset = e.getPoint();
		} else if (runningMode == Mode.newRect) {
			addTimestamp("addRectangle");
			Shape shape = addRectangle(e.getX(), e.getY(), 1, 1, runningz++);
			setFillColorForShape(fillColor, shape);
			repaint();
			selectShape(shape);
		} else if (runningMode == Mode.newEllipse) {
			addTimestamp("addEllipse");
			Shape shape = addEllipse(e.getX(), e.getY(), 1, 1, runningz++);
			setFillColorForShape(fillColor, shape);
			repaint();
			selectShape(shape);
		} else if (runningMode == Mode.newTriangle) {
			addTimestamp("addTriangle");
			Shape shape = addTriangle(e.getX(), e.getY(), 10, 10, runningz++);
			setFillColorForShape(fillColor, shape);
			repaint();
			selectShape(shape);
		} else if (runningMode == Mode.fill) {
			addTimestamp("fill");
			Shape shape = getShapeAtPosition(e.getPoint());
			if (shape != null) {
				setFillColorForShape(fillColor, shape);
				repaint();
			} else {
				setBackgroundColor(fillColor);
				repaint();
			}
		} else if (runningMode == Mode.cursor) {
			addTimestamp("select");
			selectShape(getShapeAtPosition(e.getPoint()));
		}

	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void resizeSelected(int x, int y, int newWidth, int newHeight) {
		Shape tempShape = null;
		
		if (selectedShape != null) {
			if (selectedShape.getClass().getSuperclass().getCanonicalName() == "java.awt.geom.Rectangle2D")
				tempShape = addRectangle(x, y, newWidth, newHeight,
						getZPosition(selectedShape));
			else if (selectedShape.getClass().getSuperclass()
					.getCanonicalName() == "java.awt.geom.Ellipse2D")
				tempShape = addEllipse(x, y, newWidth, newHeight,
						getZPosition(selectedShape));
			else if (selectedShape.getClass().getCanonicalName() == "java.awt.Polygon")
				tempShape = addTriangle(x, y, newWidth, newHeight,
						getZPosition(selectedShape));
			else {
				System.out.println(selectedShape.getClass().getSuperclass()
						.getCanonicalName()
						+ " onbekend!");
			}

			setFillColorForShape(getFillColorForShape(selectedShape), tempShape);

			removeShape(selectedShape);
			selectShape(tempShape);
			repaint();
		}
	}

	public void mouseDragged(MouseEvent e) {			
		if (selectedShape != null) {
			int x = (int) selectedShape.getBounds().x, y = (int) selectedShape
					.getBounds().y, newWidth = selectedShape.getBounds().width, newHeight = selectedShape
					.getBounds().height;

			if (runningMode == Mode.resize) {
				if (selectedHoek < 4)
				{
				if (e.getX() < x && e.getY() < y)
					selectHoek(0);
				else if (e.getX() < x && e.getY() > y)
					selectHoek(1);
				else if (e.getX() > x && e.getY() < y)
					selectHoek(2);
				else if (e.getX() > x && e.getY() > y)
					selectHoek(3);
				}				
				
				switch(selectedHoek)
				{
				case 0:
					newWidth += (x - e.getX());
					newHeight += (y - e.getY());
					x = e.getX();
					y = e.getY();
					break;
				case 1:
					newWidth += (x - e.getX());
					newHeight = e.getY() - y;
					x = e.getX();
					break;
				case 2:
					newWidth = e.getX() - x;
					newHeight += y - e.getY();
					y = e.getY();
					break;
				case 3:
					newWidth = e.getX() -x;
					newHeight = e.getY() - y;
					break;
				case 4:
					newHeight = e.getY() - y;
					break;
				case 5:
					newWidth += x - e.getX();
					x = e.getX();
					break;
				case 6:
					newHeight += y - e.getY();
					y = e.getY();
					break;
				case 7:
					newWidth = e.getX() - x;
					break;
				}
					resizeSelected(x, y, newWidth, newHeight);					
				
			} else if (runningMode == Mode.move) {
				x += (e.getX() - dragOffset.x);
				y += (e.getY() - dragOffset.y);

				resizeSelected(x, y, newWidth, newHeight);
				dragOffset = new Point(e.getPoint());
			}
		}
	}
	
	public void selectHoek(int hoek)
	{
		if (selectedHoek != -1) {
			setFillColorForShape(Color.white, hoeken[selectedHoek]);			
		}
		setFillColorForShape(Color.black, hoeken[hoek]);
		selectedHoek = hoek;
	}

	public void mouseMoved(MouseEvent e) {

		if (selectedHoek != -1 && runningMode == Mode.resize) {
			setFillColorForShape(Color.white, hoeken[selectedHoek]);
			setRunningMode(previousMode);
			selectedHoek = -1;
		}

		Shape shape = getShapeAtPosition(e.getPoint());

		if (shape == null) {
			if (runningMode == Mode.move) {
				setRunningMode(previousMode);
			}
		} else {
			for (int i = 0; i < 8; i++) {
				if (hoeken[i] == shape) {
					selectHoek(i);
					if (runningMode != Mode.move && runningMode != Mode.resize)
						previousMode = runningMode;
					runningMode = Mode.resize;
					switch (i) {
					case 0:
						setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
						break;
					case 1:
						setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
						break;
					case 2:
						setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
						break;
					case 3:
						setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
						break;
					case 4:
						setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
						break;
					case 5:
						setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
						break;
					case 6:
						setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
						break;
					case 7:
						setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
						break;
					}
					break;
				} else {
					if (runningMode == Mode.resize) {
						setRunningMode(Mode.cursor);
					}
				}
			}
			if (selectedHoek == -1 && selectedShape == shape) {
				if (runningMode == Mode.cursor || runningMode == Mode.resize)
				{
					previousMode = runningMode;
					setRunningMode(Mode.move);
				}
			}
		}
		repaint();
	}

	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
