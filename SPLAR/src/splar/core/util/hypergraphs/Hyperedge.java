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
package splar.core.util.hypergraphs;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Hyperedge extends Vertex {
	
	private Set<Vertex> edges;
	private Map<String,Object> properties;
	
	public Hyperedge() {
		this("");
	}
	
	public Hyperedge(String name) {
		super(name);
		edges = new LinkedHashSet<Vertex>();
		properties = new LinkedHashMap<String, Object>();
	}
	
	public void setProperty(String name, Object value) {
		properties.put(name,value);
	}
	
	public Object getProperty(String name) {
		return properties.get(name);
	}
	
	public void addVertex(Vertex vertex) {
		edges.add(vertex);
	}
	
	public void addAllHyperedgeVertices(Hyperedge edge) {
		edges.addAll(edge.getVertices());
	}
	
	public Set<Vertex> getVertices() {
		return edges;
	}
	
	public boolean containsVertex(Vertex vertex) {
		return edges.contains(vertex);
	}
	
	public int hashCode() {
		return 0;
	}
	
	public boolean equals(Object edge) {
		if ( edge != null && edge instanceof Hyperedge) {
			Hyperedge hedge = (Hyperedge)edge;
			if ( (hedge.getName().trim().length() == 0) || (getName().trim().length() == 0)) {
				return edge == this;
			}
			else {
				return getName().equals(hedge.getName());
			}
		}
		return false;
	}
	
	public String toString() {
		StringBuffer toString = new StringBuffer(100);
		if ( getName().trim().length() > 0 ) {
			toString.append(getName());
			toString.append(":");
		}
		toString.append("(");
		for( Iterator<Vertex> it2 = edges.iterator(); it2.hasNext() ; ) {
			Vertex vertex = it2.next();
			toString.append(vertex);
			if ( it2.hasNext() ) {
				toString.append(",");
			}
		}
		toString.append(")");
		return toString.toString();
	}	
}
