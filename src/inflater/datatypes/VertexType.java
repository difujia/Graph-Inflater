/*
* @(\#) VertexType.java 1.1 22 April 2014
* 
 * Copyright (\copyright) 2014 University of York & British Telecommunications plc
* This Software is granted under the MIT License (MIT)

*

* Copyright (c) <2014> <Team S4E>

*

* Permission is hereby granted, free of charge, to any person obtaining a copy

* of this software and associated documentation files (the "Software"), to deal

* in the Software without restriction, including without limitation the rights

* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell

* copies of the Software, and to permit persons to whom the Software is

* furnished to do so, subject to the following conditions:

*

* The above copyright notice and this permission notice shall be included in

* all copies or substantial portions of the Software.

*

* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR

* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,

* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE

* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER

* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,

* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN

* THE SOFTWARE.
*
*/
package inflater.datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Define the vertex data structure.
 * @author Fujia Di
 *
 */
public class VertexType {

	private long			id;
	private VertexValues	values;
	private List<EdgeType>	edges;

	/**
	 * For reflection by Gson, using this constructor is discouraged as id should be explicitly set.
	 */
	public VertexType() {
		this(-1, new VertexValues(), new ArrayList<EdgeType>(0));
	}

	/**
	 * Construct a vertex with default values and no edges.
	 * 
	 * @param id
	 */
	public VertexType(long id) {
		this(id, new VertexValues(), new ArrayList<EdgeType>(0));
	}

	public VertexType(long id, VertexValues values, List<EdgeType> edges) {
		this.id = id;
		this.values = values;
		this.edges = edges;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public VertexValues getValues() {
		return values;
	}

	public void setValues(VertexValues values) {
		this.values = values;
	}

	public List<EdgeType> getEdges() {
		return edges;
	}

	public void setEdges(List<EdgeType> edges) {
		this.edges = edges;
	}
}
