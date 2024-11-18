package comandos;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Pegar_en {
	private static Robot robot;

	public static void main(String[] args) {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_SHIFT);
		robot.keyPress(KeyEvent.VK_2);
		robot.delay(10);
		robot.keyRelease(KeyEvent.VK_2);
		robot.keyRelease(KeyEvent.VK_SHIFT);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
