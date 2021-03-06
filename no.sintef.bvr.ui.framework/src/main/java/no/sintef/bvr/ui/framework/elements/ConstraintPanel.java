/**
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.sintef.bvr.ui.framework.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.SwingConstants;

import no.sintef.bvr.ui.framework.ErrorHighlightableElement;
import no.sintef.bvr.ui.framework.OptionalElement.OPTION_STATE;
import no.sintef.bvr.ui.framework.SelectElement;
import no.sintef.bvr.ui.framework.ThreePartRectanglePanel;
import no.sintef.bvr.ui.framework.TitledElement;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXTitledSeparator;

public class ConstraintPanel extends ThreePartRectanglePanel implements SelectElement, TitledElement, ErrorHighlightableElement {

	JXLabel name = new JXLabel();
	JXTitledSeparator separatorbar = new JXTitledSeparator();
	JXLabel constraint = new JXLabel();

	BVRModelPanel model;

	public ConstraintPanel(BVRModelPanel model) {
		this.model = model;

		backgroundColor = Color.WHITE;
		
		name.setForeground(Color.BLACK);
		name.setHorizontalAlignment(SwingConstants.CENTER);
		name.setVisible(false);

		separatorbar.setForeground(Color.BLACK);
		separatorbar.setTitle("");
		separatorbar.setHorizontalAlignment(SwingConstants.CENTER);
				
		constraint.setForeground(Color.BLACK);
		constraint.setLineWrap(true);
		constraint.setHorizontalAlignment(SwingConstants.LEFT);
		constraint.setFont(new Font(null, Font.ITALIC, 10));
		
		addCenter(name);
		addCenter(separatorbar);
		addCenter(constraint);
	}

	public void setConstraint(String constraint) {
		this.constraint.setText(constraint);
	}

	public void removeAttribute(String name) {

	}

	public void setNameAndCardinality(String name, String cardinality) {
		setTitle(name + " : " + cardinality);
	}

	@Override
	public void setTitle(String title) {
		name.setText(title);
		this.setToolTipText("Constraint "+title);
		
		name.setVisible(true);
		separatorbar.setVisible(true);
	}

	private Boolean selected = false;

	public void setSelected(Boolean _selected) {
		selected = _selected;
		active = _selected;
	}

	public Boolean isSelected() {
		return selected;
	}

	private STATE _state = STATE.NO_ERROR;

	public void setState(STATE state) {
		_state = state;
		if (_state.equals(STATE.IN_ERROR)) {
			this.setBackground(new Color(239, 50, 50, 180));
		} else {
			this.setBackground(new Color(255, 255, 255));
		}
	}

	public STATE getCurrentState() {
		return _state;
	}

	/*@Override
	protected void paintComponent(Graphics g) {
		((Graphics2D)g).shear(0.1,0);
		super.paintComponents(g);
	}*/
}
