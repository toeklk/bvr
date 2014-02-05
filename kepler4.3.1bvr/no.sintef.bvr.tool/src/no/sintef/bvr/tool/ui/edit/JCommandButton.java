package no.sintef.bvr.tool.ui.edit;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

import com.explodingpixels.macwidgets.plaf.HudButtonUI;

import no.sintef.bvr.tool.ui.command.Command;
import no.sintef.bvr.tool.ui.loader.BVRView;

public class JCommandButton extends JButton {

	protected Command command;
	
    public JCommandButton(final String title, final Command command){
    	this.command = command;
        this.setText(title);
        this.setOpaque(false);
        this.addMouseListener(new MouseAdapter() {
        @Override
            public void mouseClicked(MouseEvent e) {
        		command.execute();
            }
        });

    }
}