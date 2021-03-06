/*
* @(\#) SimpleComputationTest.java 1.1 22 April 2014
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
package computation.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;

import inflater.computation.SimpleComputation;
import inflater.computation.SimpleMasterCompute;
import inflater.datatypes.writable.CoordinateWritable;
import inflater.datatypes.writable.EdgeValuesWritable;
import inflater.datatypes.writable.MessageWritable;
import inflater.datatypes.writable.VertexValuesWritable;
import inflater.formats.JsonCustomVertexInputFormat;
import inflater.formats.JsonCustomVertexInputFormat.JsonCustomVertexReader;
import inflater.formats.JsonCustomVertexOutputFormat;

import org.apache.giraph.aggregators.Aggregator;
import org.apache.giraph.conf.GiraphConfiguration;
import org.apache.giraph.edge.EdgeFactory;
import org.apache.giraph.graph.DefaultVertex;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.master.MasterAggregatorUsage;
import org.apache.giraph.utils.InternalVertexRunner;
import org.apache.giraph.worker.WorkerAggregatorUsage;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;
import org.junit.Test;

public class SimpleComputationTest {


		MasterAggregatorUsage mockAggregator= new MasterAggregatorUsage()

		{
			
			@Override
			public <A extends Writable> boolean registerAggregator(String arg0,
					Class<? extends Aggregator<A>> arg1)
					throws InstantiationException, IllegalAccessException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public <A extends Writable> boolean registerPersistentAggregator(
					String arg0, Class<? extends Aggregator<A>> arg1)
					throws InstantiationException, IllegalAccessException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public <A extends Writable> void setAggregatedValue(String arg0,
					A arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public <A extends Writable> A getAggregatedValue(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

		};


		/**
		 * Test superstep0

		 */
		@Test
		public void testSuperstep0() throws Exception

		{

			Vertex<LongWritable, VertexValuesWritable, EdgeValuesWritable> vertex = 

					new DefaultVertex<LongWritable, VertexValuesWritable, EdgeValuesWritable>();


			SimpleComputation computation = new SimpleComputation();
			SimpleMasterCompute master = new SimpleMasterCompute();

//			computation.setAggregator(mockAggregator);


//			MockUtils.prepareVertexAndComputation(vertex, new LongWritable(1),
//
//							new VertexValuesWritable(), false,
//							computation, 0L);

			vertex.setValue(new VertexValuesWritable());


			vertex.addEdge(EdgeFactory.create(new LongWritable(2),

					new EdgeValuesWritable()));
			vertex.addEdge(EdgeFactory.create(new LongWritable(3),

					new EdgeValuesWritable()));

			computation.compute(vertex, new ArrayList<MessageWritable>());


			assertTrue(vertex.isHalted());

			//cool should not be called in superstep0
			//verify(computation, never()).aggregate("T", new DoubleWritable(-100));

			//random positions should be between -50 and 50
			assertEquals(0,vertex.getValue().getCoordinate().get().x,50);

			assertEquals(0,vertex.getValue().getCoordinate().get().y,50);


		}

		/**
		 * Test the messages after finishes superstep1

		 */
		@Test
		public void testSuperstep1() throws Exception 

		{

			//messages: positions of other vertices
			ArrayList<MessageWritable> messages = new ArrayList<MessageWritable>();


			messages.add(new MessageWritable(new LongWritable(2),new CoordinateWritable(),new BooleanWritable()));

			messages.add(new MessageWritable(new LongWritable(3),new CoordinateWritable(),new BooleanWritable()));


			Vertex<LongWritable, VertexValuesWritable, EdgeValuesWritable> vertex = 

					new DefaultVertex<LongWritable, VertexValuesWritable, EdgeValuesWritable>();


			SimpleComputation computation = new SimpleComputation();
			SimpleMasterCompute master = new SimpleMasterCompute();
			master.initialize();


			MockUtils.MockedEnvironment<LongWritable, VertexValuesWritable, EdgeValuesWritable, MessageWritable> env = 

					MockUtils.prepareVertexAndComputation(vertex, new LongWritable(1L),

							new VertexValuesWritable(), true,
							computation, 1L);


			vertex.setValue(new VertexValuesWritable(new CoordinateWritable(),new DoubleWritable(0)));


			vertex.addEdge(EdgeFactory.create(new LongWritable(2L),

					new EdgeValuesWritable()));
			vertex.addEdge(EdgeFactory.create(new LongWritable(3L),

					new EdgeValuesWritable()));



			computation.compute(vertex, messages);


			assertTrue(vertex.isHalted());

			env.verifyMessageSent(new LongWritable(2), new MessageWritable(new LongWritable(1),vertex.getValue().getCoordinate(),new BooleanWritable()));

			env.verifyMessageSent(new LongWritable(3), new MessageWritable(new LongWritable(1),vertex.getValue().getCoordinate(),new BooleanWritable()));

		}

//
//		/**
//		 * Test the messages after finishes superstep2
//
//		 */
//		@Test
//		public void testSuperstep2() throws Exception 
//
//		{
//
//			//messages: positions of other vertices
//			ArrayList<MessageWritable> messages = new ArrayList<MessageWritable>();
//
//
//			messages.add(new MessageWritable(new LongWritable(2),new CoordinateWritable(),new BooleanWritable()));
//
//			messages.add(new MessageWritable(new LongWritable(3),new CoordinateWritable(),new BooleanWritable()));
//
//
//			Vertex<LongWritable, VertexValuesWritable, EdgeValuesWritable> vertex = 
//
//					new DefaultVertex<LongWritable, VertexValuesWritable, EdgeValuesWritable>();
//
//
//			SimpleComputation computation = new SimpleComputation();
//			SimpleComputation computation = new SimpleComputation();
//			SimpleMasterCompute master = new SimpleMasterCompute();
//			master.initialize();
//
//
//			MockUtils.MockedEnvironment<LongWritable, VertexValueWritable, EdgeValueWritable, MessageWritable> env = 
//
//					MockUtils.prepareVertexAndComputation(vertex, new LongWritable(1L),
//
//							new VertexValueWritable(), false,
//							computation, 2L);
//
//
//			vertex.setValue(new VertexValueWritable(new VectorWritable(10,20),new VectorWritable(0,0)));
//
//
//			vertex.addEdge(EdgeFactory.create(new LongWritable(2L),
//
//					new EdgeValueWritable()));
//
//			vertex.addEdge(EdgeFactory.create(new LongWritable(3L),
//
//					new EdgeValueWritable()));
//
//
//
//			computation.compute(vertex, messages);
//
//
//			//assertTrue(vertex.isHalted());
//			env.verifyMessageSent(new LongWritable(2L), new MessageWritable(new LongWritable(1),new VectorWritable(10,20)));
//
//			env.verifyMessageSent(new LongWritable(3L), new MessageWritable(new LongWritable(1),new VectorWritable(10,20)));
//
//		}
//
//
//		/**
//		 * Test the messages after finishes superstep3
//
//		 */
//		@Test
//		public void testSuperstep3() throws Exception
//
//		{
//			//messages: positions of other vertices
//			ArrayList<MessageWritable> messages = new ArrayList<MessageWritable>();
//
//
//			messages.add(new MessageWritable(new LongWritable(2L),new VectorWritable(10,20)));
//
//			messages.add(new MessageWritable(new LongWritable(3L),new VectorWritable(10,20)));
//
//
//			Vertex<LongWritable, VertexValueWritable, EdgeValueWritable> vertex = 
//
//					new DefaultVertex<LongWritable, VertexValueWritable, EdgeValueWritable>();
//
//
//			FruchtermanReingoldGraphVis computation = new FruchtermanReingoldGraphVis();
//
//			computation.setAggregator(mockAggregator);
//
//
//			MockUtils.MockedEnvironment<LongWritable, VertexValueWritable, EdgeValueWritable, MessageWritable> env = 
//
//					MockUtils.prepareVertexAndComputation(vertex, new LongWritable(1L),
//
//							new VertexValueWritable(), false,
//							computation, 3L);
//
//
//			//Mockito.when(SOURCE_ID.get(env.getConfiguration())).thenReturn(2L);
//			vertex.setValue(new VertexValueWritable(new VectorWritable(10,20),new VectorWritable(0,0)));
//
//
//			vertex.addEdge(EdgeFactory.create(new LongWritable(2L),
//
//					new EdgeValueWritable()));
//			vertex.addEdge(EdgeFactory.create(new LongWritable(3L),
//
//					new EdgeValueWritable()));
//
//
//
//			computation.compute(vertex, messages);
//
//
//			//assertTrue(vertex.isHalted());
//
//			env.verifyMessageSent(new LongWritable(2L), new MessageWritable(new LongWritable(1L),new VectorWritable()));
//
//			env.verifyMessageSent(new LongWritable(3L), new MessageWritable(new LongWritable(1L),new VectorWritable()));
//
//		}
//
//
//		/**
//		 * Test the messages after finishes superstep4
//
//		 */
//		@Test
//		public void testSuperstep4() throws Exception 
//
//		{
//
//			//messages: positions of other vertices
//			ArrayList<MessageWritable> messages = new ArrayList<MessageWritable>();
//
//
//			messages.add(new MessageWritable(new LongWritable(2),new VectorWritable(545,234)));
//
//			messages.add(new MessageWritable(new LongWritable(3),new VectorWritable(242,677)));
//
//
//			Vertex<LongWritable, VertexValueWritable, EdgeValueWritable> vertex = 
//
//					new DefaultVertex<LongWritable, VertexValueWritable, EdgeValueWritable>();
//
//
//			FruchtermanReingoldGraphVis computation = new FruchtermanReingoldGraphVis();
//
//			MockUtils.MockedEnvironment<LongWritable, VertexValueWritable, EdgeValueWritable, MessageWritable> env = 
//
//					MockUtils.prepareVertexAndComputation(vertex, new LongWritable(1L),
//
//							new VertexValueWritable(), false,
//							computation, 4L);
//
//
//			vertex.setValue(new VertexValueWritable(new VectorWritable(10,20),new VectorWritable(0,0)));
//
//
//			vertex.addEdge(EdgeFactory.create(new LongWritable(2L),
//
//					new EdgeValueWritable()));
//			vertex.addEdge(EdgeFactory.create(new LongWritable(3L),
//
//					new EdgeValueWritable()));
//
//
//
//			computation.compute(vertex, messages);
//
//
//			assertTrue(vertex.isHalted());
//
//
//			env.verifyMessageSent(new LongWritable(2), new MessageWritable(new LongWritable(1L),new VectorWritable(10,20)));
//
//			env.verifyMessageSent(new LongWritable(3), new MessageWritable(new LongWritable(1L),new VectorWritable(10,20)));
//
//		}
//
//
//		/**
//		 * Test superstep5
//
//		 */
//		@Test
//		public void testSuperstep5() throws Exception 
//
//		{
//
//			//messages: positions of other vertices
//			ArrayList<MessageWritable> messages = new ArrayList<MessageWritable>();
//
//
//			messages.add(new MessageWritable(new LongWritable(2L),new VectorWritable(545,234)));
//
//			messages.add(new MessageWritable(new LongWritable(3L),new VectorWritable(242,677)));
//
//
//			Vertex<LongWritable, VertexValueWritable, EdgeValueWritable> vertex = 
//
//					new DefaultVertex<LongWritable, VertexValueWritable, EdgeValueWritable>();
//
//
//			FruchtermanReingoldGraphVis computation = new FruchtermanReingoldGraphVis();
//
//
//			MockUtils.prepareVertexAndComputation(vertex, new LongWritable(1L),
//
//							new VertexValueWritable(), false,
//							computation, 5L);
//
//
//			vertex.setValue(new VertexValueWritable(new VectorWritable(10,20),new VectorWritable(0,0)));
//
//
//			vertex.addEdge(EdgeFactory.create(new LongWritable(2L),
//
//					new EdgeValueWritable()));
//			vertex.addEdge(EdgeFactory.create(new LongWritable(3L),
//
//					new EdgeValueWritable()));
//
//
//
//			computation.compute(vertex, messages);
//
//
//			assertTrue(vertex.isHalted());
//
//			assertEquals(new VectorWritable(545,234),vertex.getEdgeValue(new LongWritable(2L)).getTargetPos());
//
//			assertEquals(new VectorWritable(242,677),vertex.getEdgeValue(new LongWritable(3L)).getTargetPos());
//
//		}
//
//		/**
//		 * Test superstep6
//
//		 */
//		@Test
//		public void testSuperstep6() throws Exception 
//
//		{
//
//			Vertex<LongWritable, VertexValueWritable, EdgeValueWritable> vertex = 
//
//					new DefaultVertex<LongWritable, VertexValueWritable, EdgeValueWritable>();
//
//
//			FruchtermanReingoldGraphVis computation = mock(FruchtermanReingoldGraphVis.class);
//
//
//			when(computation.getAggregatedValue("T")).thenReturn(new DoubleWritable(1000));
//
//			when(computation.getAggregatedValue("k")).thenReturn(new DoubleWritable(4000000));
//
//
//			//double tBefore = computation.getT();
//
//			MockUtils.prepareVertexAndComputation(vertex, new LongWritable(1L),
//
//							new VertexValueWritable(), false,
//							computation, 6L);
//
//
//			vertex.setValue(new VertexValueWritable());
//
//
//			vertex.addEdge(EdgeFactory.create(new LongWritable(2),
//
//					new EdgeValueWritable()));
//			vertex.addEdge(EdgeFactory.create(new LongWritable(3),
//
//					new EdgeValueWritable()));
//
//
//
//			computation.compute(vertex, new ArrayList<MessageWritable>());
//
//			//double tAfter = computation.getT();
//
//			//assertTrue(vertex.isHalted());
//
//			//cool should be called in superstep6
//			//assertEquals(10,tBefore-tAfter,0);
//
//
//
//		}
//
//		/**
//		 * Test superstep606
//
//		 */
//		@Test
//		public void testSuperstep606() throws Exception 
//
//		{
//
//			Vertex<LongWritable, VertexValueWritable, EdgeValueWritable> vertex = 
//
//					new DefaultVertex<LongWritable, VertexValueWritable, EdgeValueWritable>();
//
//
//			FruchtermanReingoldGraphVis computation = mock(FruchtermanReingoldGraphVis.class);
//
//
//			when(computation.getAggregatedValue("T")).thenReturn(new DoubleWritable(1000));
//
//			when(computation.getAggregatedValue("k")).thenReturn(new DoubleWritable(4000000));
//
//
//			//double tBefore = computation.getT();
//
//			MockUtils.MockedEnvironment<LongWritable, VertexValueWritable, EdgeValueWritable, MessageWritable> env = 
//
//					MockUtils.prepareVertexAndComputation(vertex, new LongWritable(1L),
//
//							new VertexValueWritable(), false,
//							computation, 606L);
//
//
//			vertex.setValue(new VertexValueWritable());
//
//
//			vertex.addEdge(EdgeFactory.create(new LongWritable(2L),
//
//					new EdgeValueWritable()));
//			vertex.addEdge(EdgeFactory.create(new LongWritable(3L),
//
//					new EdgeValueWritable()));
//
//
//
//			computation.compute(vertex, new ArrayList<MessageWritable>());
//
//			//double tAfter = computation.getT();
//
//			//assertTrue(vertex.isHalted());
//
//			//cool should be called in superstep6
//			//assertEquals(10,tBefore-tAfter,0);
//			//computation should stop after this superstep, so no messages should be sent
//
//			env.verifyNoMessageSent();
//
//		}
//
//
//
//
//
//
//
//
//



	
}
