/*
* @(\#) TestJsonCustomVertexInputFormat.java 1.1 22 April 2014
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import inflater.datatypes.EdgeType;
import inflater.datatypes.VertexType;
import inflater.datatypes.writable.CoordinateWritable;
import inflater.datatypes.writable.EdgeValuesWritable;
import inflater.datatypes.writable.VertexValuesWritable;
import inflater.formats.IncorrectIdException;
import inflater.formats.JsonCustomVertexInputFormat;

import java.io.IOException;
import java.util.Iterator;

import org.apache.giraph.conf.GiraphConfiguration;
import org.apache.giraph.conf.ImmutableClassesGiraphConfiguration;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.junit.Before;
import org.junit.Test;

import utils.NoOpComputation;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class TestJsonCustomVertexInputFormat extends JsonCustomVertexInputFormat {

	RecordReader<LongWritable, Text>															rr;
	ImmutableClassesGiraphConfiguration<LongWritable, VertexValuesWritable, EdgeValuesWritable>	conf;
	TaskAttemptContext																			tac;
	Gson																						gson	= new Gson();
	Vertex<LongWritable, VertexValuesWritable, EdgeValuesWritable>								actualVertex;

	@Before
	public void setUp() throws IOException, InterruptedException {
		rr = mock(RecordReader.class);
		when(rr.nextKeyValue()).thenReturn(true).thenReturn(false);
		GiraphConfiguration giraphConf = new GiraphConfiguration();
		giraphConf.setComputationClass(DummyComputation.class);
		conf = new ImmutableClassesGiraphConfiguration<LongWritable, VertexValuesWritable, EdgeValuesWritable>(
				giraphConf);
		tac = mock(TaskAttemptContext.class);
		when(tac.getConfiguration()).thenReturn(conf);
	}

	protected TextVertexReader createVertexReader(final RecordReader<LongWritable, Text> rr) {
		return new JsonCustomVertexReader() {
			@Override
			protected RecordReader<LongWritable, Text> createLineRecordReader(InputSplit inputSplit,
					TaskAttemptContext context) throws IOException, InterruptedException {
				return rr;
			}
		};
	}

	@Test
	public void testHappyRead() {
		String input = "{'id':15,'values':{'coordinate':{'x':-527.6104125976562,'y':-880.878662109375,'z':0.0},'weight':0.0},'edges':[{'targetId':5,'weight':0.0},{'targetId':6,'weight':0.0}]}";
		actualVertex = readVertex(input);
		VertexType expected = new Gson().fromJson(input, VertexType.class);

		assertEquals(expected.getId(), actualVertex.getId().get());
		assertEquals(expected.getValues().getCoordinate(), actualVertex.getValue().getCoordinate().get());
		assertEquals(expected.getEdges().size(), actualVertex.getNumEdges());
		assertEquals(expected.getValues().getWeight(), actualVertex.getValue().getWeight().get(), 0);
		Iterator<EdgeType> expectedEdgeTypeIterator = expected.getEdges().iterator();
		Iterator<Edge<LongWritable, EdgeValuesWritable>> actualEdgeIterator = actualVertex.getEdges().iterator();
		while (expectedEdgeTypeIterator.hasNext() || actualEdgeIterator.hasNext()) {
			EdgeType expectedEdgeType = expectedEdgeTypeIterator.next();
			Edge<LongWritable, EdgeValuesWritable> actualEdge = actualEdgeIterator.next();

			assertEquals(expectedEdgeType.getTargetId(), actualEdge.getTargetVertexId().get());
			assertEquals(new DoubleWritable(expectedEdgeType.getWeight()), actualEdge.getValue().getWeight());
		}
	}

	@Test(expected = JsonSyntaxException.class)
	public void testReadCorruptedJson() {
		String input = "{'id':15,'values':{'coordinate':{'x':-527.6104125976562,'y':-880.878662109375,'z':0.0},'weight':0.0},'edges':[{'targetId':5,'weight':0.0},{'targetId':6,'weight':0.0}]]}";
		readVertex(input);
	}

	@Test
	public void testReadNoEdges() {
		String input = "{'id':15,'values':{'coordinate':{'x':-527.6104125976562,'y':-880.878662109375,'z':0.0},'weight':0.0}}";
		actualVertex = readVertex(input);
		assertEquals(0, actualVertex.getNumEdges());
	}

	@Test
	public void testReadEmptyEdges() {
		String input = "{'id':15,'values':{'coordinate':{'x':-527.6104125976562,'y':-880.878662109375,'z':0.0},'weight':0.0},'edges':[]}";
		actualVertex = readVertex(input);
		assertEquals(0, actualVertex.getNumEdges());
	}

	@Test
	public void testReadNoCoordinate() {
		String input = "{'id':15,'values':{'weight':0.0},'edges':[{'targetId':5,'weight':0.0},{'targetId':6,'weight':0.0}]}";
		actualVertex = readVertex(input);
		assertEquals(new CoordinateWritable(), actualVertex.getValue().getCoordinate());
	}

	@Test
	public void testReadNoValues() {
		String input = "{'id':15,'edges':[{'targetId':5,'weight':0.0},{'targetId':6,'weight':0.0}]}";
		actualVertex = readVertex(input);
		assertEquals(new VertexValuesWritable(), actualVertex.getValue());
	}

	@Test(expected = IncorrectIdException.class)
	public void testReadMissingId() {
		String input = "{'values':{'coordinate':{'x':-527.6104125976562,'y':-880.878662109375,'z':0.0},'weight':0.0},'edges':[{'targetId':5,'weight':0.0},{'targetId':6,'weight':0.0}]}";
		readVertex(input);
	}

	@Test(expected = IncorrectIdException.class)
	public void testReadInvalidId() {
		String input = "{'id':0,'values':{'coordinate':{'x':-527.6104125976562,'y':-880.878662109375,'z':0.0},'weight':0.0},'edges':[{'targetId':5,'weight':0.0},{'targetId':6,'weight':0.0}]}";
		readVertex(input);
	}

	@Test(expected = IncorrectIdException.class)
	public void testReadEdgeMissingTargetId() {
		String input = "{'id':15,'values':{'coordinate':{'x':-527.6104125976562,'y':-880.878662109375,'z':0.0},'weight':0.0},'edges':[{'weight':0.0},{'targetId':6,'weight':0.0}]}";
		readVertex(input);
	}

	@Test(expected = IncorrectIdException.class)
	public void testReadEdgeInvalidTargetId() {
		String input = "{'id':15,'values':{'coordinate':{'x':-527.6104125976562,'y':-880.878662109375,'z':0.0},'weight':0.0},'edges':[{'targetId':-5,'weight':0.0},{'targetId':6,'weight':0.0}]}";
		readVertex(input);
	}

	public Vertex<LongWritable, VertexValuesWritable, EdgeValuesWritable> readVertex(String jsonLine) {
		try {
			when(rr.getCurrentValue()).thenReturn(new Text(jsonLine));
			TextVertexReader reader = createVertexReader(rr);
			reader.setConf(conf);
			reader.initialize(null, tac);
			return reader.getCurrentVertex();
		} catch (IOException | InterruptedException e) {
			fail("thrown exception: " + e);
			return null;
		}
	}

	public static class DummyComputation extends
			NoOpComputation<LongWritable, VertexValuesWritable, EdgeValuesWritable, NullWritable> {
	}
}
