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

package no.sintef.cvl.ui.framework.elements;

import no.sintef.cvl.ui.framework.IconPanel;

public class GroupPanel extends IconPanel {

    public GroupPanel() {
    	super("C:/Users/mjoha/workspace-CVLTool2/cvl/kepler4.3.1/no.sintef.cvl.tool/src/1349960253_triangle.png");
    }
    
    public void setCardinality(int lower, int upper) {
    	 setTitle(lower + ".." + upper);
    	 if(upper == -1)
    		 setTitle(lower + "..*");
    }

}