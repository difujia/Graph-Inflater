/*
* @(\#) FormatUtils.java 1.1 22 April 2014
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
package inflater.formats;

import inflater.datatypes.EdgeType;
import inflater.datatypes.VertexType;

import com.google.gson.Gson;

/**
 * @author Fujia Di
 *
 */
public class FormatUtils {

	private static Gson	gson	= new Gson();

	public static VertexType parseFromJsonLine(String jsonLine) {
		// GSON throws JsonSyntaxException if JSON structure is corrupted.
		VertexType vertex = gson.fromJson(jsonLine, VertexType.class);

		if (vertex.getId() <= 0) throw new IncorrectIdException(jsonLine);

		// check if any edge miss targetId
		for (EdgeType edge : vertex.getEdges()) {
			if (edge.getTargetId() <= 0) throw new IncorrectIdException(jsonLine);
		}

		// add new validation here

		// only return valid VertexType
		return vertex;
	}

	public static String parseToJsonLine(VertexType vertex) {
		// add validation here

		String json = gson.toJson(vertex);
		return json;
	}
}
