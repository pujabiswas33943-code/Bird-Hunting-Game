package engine;
import entity.Arrow;
import java.awt.event.*;

public class InputHandler implements KeyListener,MouseListener,MouseMotionListener {


    public interface InputCallback {
        void onDragStart(int mouseX, int mouseY);
        void onDragUpdate(int mouseX, int mouseY);
        void onDragRelease(int mouseX, int mouseY);
        void onSelectArrow(Arrow.ArrowType type);
        void onUpgradeShop();
        void onRestart();
        void onMouseMoved(int x, int y);
        void onPause();

    }

    private InputCallback callback;

    public InputHandler(InputCallback callback) {
        this.callback = callback;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Key: " +e.getKeyCode());
        switch (e.getKeyCode()) {
            case KeyEvent.VK_1 -> callback.onSelectArrow(Arrow.ArrowType.NORMAL);
            case KeyEvent.VK_2 -> callback.onSelectArrow(Arrow.ArrowType.POWER);
            case KeyEvent.VK_3 -> callback.onSelectArrow(Arrow.ArrowType.THUNDER);
            case KeyEvent.VK_4 -> callback.onSelectArrow(Arrow.ArrowType.ICE);
            case KeyEvent.VK_5 -> callback.onSelectArrow(Arrow.ArrowType.NET);
            case KeyEvent.VK_S -> callback.onUpgradeShop();
            case KeyEvent.VK_R -> callback.onRestart();
            case KeyEvent.VK_P -> callback.onPause();

        }

    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        callback.onDragStart(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        callback.onDragRelease(e.getX(), e.getY());
    }
    @Override
    public void mouseDragged(MouseEvent e ){
        callback.onDragUpdate(e.getX(), e.getY());
    }


    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        callback.onMouseMoved(e.getX(), e.getY());
    }


}