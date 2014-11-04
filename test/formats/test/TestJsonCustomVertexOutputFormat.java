/*
* @(\#) TestJsonCustomVertexOutputFormat.java 1.1 22 April 2014
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
package formats.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import inflater.datatypes.Coordinate;
import inflater.datatypes.EdgeType;
import inflater.datatypes.VertexType;
import inflater.datatypes.VertexValues;
import inflater.datatypes.writable.EdgeValuesWritable;
import inflater.datatypes.writable.VertexValuesWritable;
import inflater.formats.JsonCustomVertexOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.giraph.conf.GiraphConfiguration;
import org.apache.giraph.conf.ImmutableClassesGiraphConfiguration;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.edge.EdgeFactory;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.junit.Before;
import org.junit.Test;

import utils.NoOpComputation;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

public class TestJsonCustomVertexOutputFormat extends JsonCustomVertexOutputFormat {

	/** Test configuration */
	private ImmutableClassesGiraphConfiguration<LongWritable, VertexValuesWritable, EdgeValuesWritable>	conf;

	/**
	 * Dummy class to allow ImmutableClassesGiraphConfiguration to be created.
	 */
	public static class DummyComputation extends
			NoOpComputation<LongWritable, VertexValuesWritable, EdgeValuesWritable, NullWritable> {
	}

	@Before
	public void setUp() {
		GiraphConfiguration giraphConfiguration = new GiraphConfiguration();
		giraphConfiguration.setComputationClass(DummyComputation.class);
		conf = new ImmutableClassesGiraphConfiguration<LongWritable, VertexValuesWritable, EdgeValuesWritable>(
				giraphConfiguration);
	}

	@Test
	public void testWriteDummy() throws IOException, InterruptedException {
		VertexType vertex = new VertexType();
		testWorker(vertex);
	}

	@Test
	public void testWriteFull() throws IOException, InterruptedException {
		List<EdgeType> edges = Lists.newArrayList();
		int numEdges = new Random().nextInt(10);
		for (int i = 0; i < numEdges; i++) {
			edges.add(new EdgeType(34, 4568.4564));
		}
		Coordinate c = new Coordinate(54.1, 0, 33);
		VertexType toWrite = new VertexType(6, new VertexValues(c, 457.22222), edges);
		testWorker(toWrite);
	}

	@Test
	public void testWriteNoEdges() throws IOException, InterruptedException {
		Coordinate c = new Coordinate(54.1, 0, 33);
		VertexType toWrite = new VertexType(2, new VertexValues(c, 457.22222), new ArrayList<EdgeType>(0));
		testWorker(toWrite);
	}

	/**
	 * Compare the json output by VertexWriter and by direct call of
	 * Gson.toJson()
	 * 
	 * @param toWrite
	 *            Used to populate Vertex in Giraph, and to produce json string
	 *            for comparison.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void testWorker(VertexType toWrite) throws IOException, InterruptedException {
		TaskAttemptContext tac = mock(TaskAttemptContext.class);
		when(tac.getConfiguration()).thenReturn(conf);

		Vertex vertex = mock(Vertex.class);
		when(vertex.getId()).thenReturn(new LongWritable(toWrite.getId()));
		Coordinate coordinateToWrite = toWrite.getValues().getCoordinate();
		double weightToWrite = toWrite.getValues().getWeight();
		when(vertex.getValue()).thenReturn(new VertexValuesWritable(coordinateToWrite, weightToWrite));

		List<Edge<LongWritable, EdgeValuesWritable>> edges = Lists.newArrayListWithCapacity(toWrite.getEdges().size());
		for (int i = 0; i < toWrite.getEdges().size(); i++) {
			EdgeType edgeType = toWrite.getEdges().get(i);
			edges.add(EdgeFactory.create(new LongWritable(edgeType.getTargetId()),
					new EdgeValuesWritable(edgeType.getWeight())));
		}

		when(vertex.getEdges()).thenReturn(edges);

		final RecordWriter<Text, Text> tw = mock(RecordWriter.class);
		JsonCustomVertexWriter writer = new JsonCustomVertexWriter() {
			@Override
			protected RecordWriter<Text, Text> createLineRecordWriter(TaskAttemptContext context) throws IOException,
					InterruptedException {
				return tw;
			}
		};

		writer.setConf(conf);
		writer.initialize(tac);
		writer.writeVertex(vertex);

		Text expected = new Text(new Gson().toJson(toWrite));
		verify(tw).write(expected, null);
	}
}
