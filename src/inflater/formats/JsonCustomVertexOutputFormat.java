/*
* @(\#) JsonCustomVertexOutputFormat.java 1.1 22 April 2014
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
import inflater.datatypes.VertexValues;
import inflater.datatypes.writable.EdgeValuesWritable;
import inflater.datatypes.writable.VertexValuesWritable;

import java.io.IOException;
import java.util.List;

import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import com.google.common.collect.Lists;

/**
 * Write json format: {id:*, values:[coordinate:[x:*,y:*,z:*], weight:*], edges:[[targetId:*, weight:*],...]}
 * @author Fujia Di
 *
 */
public class JsonCustomVertexOutputFormat extends
		TextVertexOutputFormat<LongWritable, VertexValuesWritable, EdgeValuesWritable> {

	@Override
	public TextVertexWriter createVertexWriter(TaskAttemptContext arg0) throws IOException, InterruptedException {
		return new JsonCustomVertexWriter();
	}

	public class JsonCustomVertexWriter extends TextVertexWriterToEachLine {

		@Override
		protected Text convertVertexToLine(Vertex<LongWritable, VertexValuesWritable, EdgeValuesWritable> vertex)
				throws IOException {
			// populate edges
			List<EdgeType> edgeTypeList = Lists.newArrayListWithCapacity(vertex.getNumEdges());
			for (Edge<LongWritable, EdgeValuesWritable> edge : vertex.getEdges()) {
				EdgeType edgeType = new EdgeType(edge.getTargetVertexId().get(), edge.getValue().getWeight().get());
				edgeTypeList.add(edgeType);
			}

			VertexValues values = new VertexValues(vertex.getValue().getCoordinate().get(), vertex.getValue()
					.getWeight().get());
			long id = vertex.getId().get();
			Text jsonLine = new Text(FormatUtils.parseToJsonLine(new VertexType(id, values, edgeTypeList)));
			return jsonLine;
		}

	}

}
