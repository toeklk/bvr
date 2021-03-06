/*******************************************************************************
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
 ******************************************************************************/
package splar.plugins.configuration.bdd.javabdd.catalog;

import java.util.LinkedHashMap;
import java.util.Map;

public class Product {
	
	protected ProductCatalog catalog;
	protected String name;
	protected String id;
	protected Map<String,String> attributes;
	protected Map<String,String> components;
	
	public Product(ProductCatalog catalog, String id, String name) {
		this.catalog = catalog;
		this.id = id;
		this.name = name;		
		attributes = new LinkedHashMap<String, String>();
		components = new LinkedHashMap<String, String>();
	}
	
	public String getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void addAttribute(String attName, String attValue) {
		attributes.put(attName, attValue);
	}
	
	public String getComponent(String absComponentID) {
		return components.get(absComponentID);
	}
	
	public void addComponent(String compID, String compType) throws Exception {
		
		if ( !catalog.containsComponent(compID) ) {
			throw new Exception("Product refers to a component not described in the catalog: " + compID);
		}
		if ( !catalog.getComponent(compID).getTypes().contains(compType) && compType.length() > 0 ) {
			throw new Exception("Component type " + compType + " is not valid for component " + compID);
		}
		components.put(compID, compType);
	}
	
	public Map<String,String> getAttributes() {
		return attributes;
	}
	
	public Map<String,String> getComponents() {
		return components;
	}
	
	public String toString() {
		String toString = "Product: " + name + " [id=" + id + "]\r\n";
		toString += "   * Attributes:\r\n";
		for( String attKey : attributes.keySet() ) {
			toString += "     - " + attKey + ": " + attributes.get(attKey)+ "\r\n";
		}
		toString += "   * Concrete Components:\r\n";
		for( String componentKey : components.keySet() ) {
			toString += "     - " + componentKey + ": " + components.get(componentKey)+ "\r\n";
		}
		return toString;
	}		
	
}
 
