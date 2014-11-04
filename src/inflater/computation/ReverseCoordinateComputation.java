/*
* @(\#) ReverseCoordinateComputation.java 1.1 22 April 2014
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
package inflater.computation;

import inflater.datatypes.Coordinate;
import inflater.datatypes.writable.CoordinateWritable;
import inflater.datatypes.writable.EdgeValuesWritable;
import inflater.datatypes.writable.MessageWritable;
import inflater.datatypes.writable.VertexValuesWritable;

import java.io.IOException;

import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.LongWritable;

/**
 * Simple testing algorithm. Just reverse x and y  coordinate.
 * @author Fujia Di
 *
 */
public class ReverseCoordinateComputation extends
		BasicComputation<LongWritable, VertexValuesWritable, EdgeValuesWritable, MessageWritable> {

	@Override
	public void compute(Vertex<LongWritable, VertexValuesWritable, EdgeValuesWritable> vertex,
			Iterable<MessageWritable> messages) throws IOException {
		CoordinateWritable c = vertex.getValue().getCoordinate();
		Coordinate coordinate = c.get();
		double temp = coordinate.x;
		coordinate.x = coordinate.y;
		coordinate.y = temp;
		vertex.voteToHalt();
	}

}
