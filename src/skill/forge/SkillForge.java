package skill.forge;

import javax.swing.SwingUtilities;
import ui.LoginFrame;

public class SkillForge {

   public static void main(String[] args) {
        // Launch the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
    
}
