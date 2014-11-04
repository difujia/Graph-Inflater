/*
* @(\#) SimpleComputation.java 1.1 22 April 2014
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
import inflater.datatypes.writable.EdgeValuesWritable;
import inflater.datatypes.writable.MessageWritable;
import inflater.datatypes.writable.VertexValuesWritable;

import java.io.IOException;
import java.util.Random;

import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.log4j.Logger;

/**
 * Implementation of Fruchterman Algorithm. 
 * Some improvements include the concept of gravity and randomised initial layout. 
 * Cooling function is implemented in MasterCompute
 * @author Pengfei Wang, Fujia Di
 *
 */
public class SimpleComputation extends
		BasicComputation<LongWritable, VertexValuesWritable, EdgeValuesWritable, MessageWritable> implements
		ComputationConstants {
	/** Logger */
	private static final Logger	LOG	= Logger.getLogger(SimpleComputation.class);

	private static long			canvasSize;
	private static double		kValue;
	private static Coordinate	gravity;

	@Override
	public void compute(Vertex<LongWritable, VertexValuesWritable, EdgeValuesWritable> vertex,
			Iterable<MessageWritable> messages) throws IOException {

		if (getSuperstep() == 0) {
			long scale = getConf().getLong(SCALE, 30);
			kValue = getConf().getDouble(KVALUE, -1);
			/*
			 * calculate canvas size, if it's not set
			 */
			canvasSize = Math.round(getTotalNumVertices() * 0.618 * 0.7 * scale);
			/*
			 * calculate kValue - the optimized distance between vertices
			 */
			if (kValue < 0) {
				kValue = Math.sqrt(canvasSize * canvasSize / getTotalNumVertices());
				getConf().setDouble(KVALUE, kValue);
			}
			gravity = new Coordinate(canvasSize / 2.0d, canvasSize / 2.0d);

			/*
			 * We randomly position vertices in a square sized half of the
			 * canvas
			 */
			Random rd = new Random();
			double x = rd.nextDouble() * canvasSize;
			double y = rd.nextDouble() * canvasSize;
			vertex.getValue().getCoordinate().get().setXY(x, y);
		} else {
			Coordinate self = vertex.getValue().getCoordinate().get();
			Coordinate disp = new Coordinate();

			for (MessageWritable message : messages) {
				/*
				 * Ignore messages from self
				 */
				if (message.getSenderId().equals(vertex.getId())) continue;

				Coordinate ref = message.getSenderCoordinate().get();
				boolean linked = message.getLinked().get();
				disp = ComputationUtil.addCoor(disp, resolveForce(self, ref, linked));
			}

			disp = ComputationUtil.addCoor(disp, resolveGravity(self));
			applyChanges(vertex, disp);
		}

		/*
		 * We send messages with "unlinked" to all vertices. Then send messages
		 * with "linked" to linked vertices.
		 */
		MessageWritable toAll = new MessageWritable(vertex.getId(), vertex.getValue().getCoordinate(),
				new BooleanWritable(false));
		for (long i = 1; i <= getTotalNumVertices(); i++) {
			sendMessage(new LongWritable(i), toAll);
		}

		MessageWritable toLinked = new MessageWritable(vertex.getId(), vertex.getValue().getCoordinate(),
				new BooleanWritable(true));
		sendMessageToAllEdges(vertex, toLinked);

	}

	private Coordinate resolveForce(Coordinate self, Coordinate ref, boolean linked) {
		Coordinate disp = new Coordinate();

		Coordinate tmpSelf = new Coordinate(self);
		double distance = ComputationUtil.distance(tmpSelf, ref);
		if (distance == 0) {
			/*
			 * Deal with the case source and target are in the same position.
			 * Modify source towards gravity by 0.1.
			 */
			Coordinate toGravityVector = ComputationUtil.subtractCoor(gravity, tmpSelf);
			double distToGravity = ComputationUtil.distance(gravity, tmpSelf);
			toGravityVector = ComputationUtil.multiplyCoor(0.1 / distToGravity, toGravityVector);
			tmpSelf = ComputationUtil.addCoor(tmpSelf, toGravityVector);

			/*
			 * Recalculate the distance after modification
			 */
			distance = ComputationUtil.distance(tmpSelf, ref);
		}

		if (linked) {
			disp = attract(tmpSelf, ref);
		} else {
			/*
			 * Ignore repulsive force when two vertices are too far away
			 */
			if (distance < 2 * kValue) {
				disp = repel(tmpSelf, ref);
			}
		}
		return disp;
	}

	private Coordinate resolveGravity(Coordinate self) {
		Coordinate disp = new Coordinate();
		if (ComputationUtil.distance(self, gravity) > 0) {
			disp = attract(self, gravity);
		}
		return disp;
	}

	private void applyChanges(Vertex<LongWritable, VertexValuesWritable, EdgeValuesWritable> vertex, Coordinate disp) {
		double t = this.<DoubleWritable> getAggregatedValue(TEMPERATURE_AGG).get();
		Coordinate oldPos = vertex.getValue().getCoordinate().get();
		Coordinate newPos;
		if (ComputationUtil.module(disp) != 0) {
			newPos = ComputationUtil.addCoor(
					oldPos,
					ComputationUtil.multiplyCoor(
							Math.min(ComputationUtil.module(disp), t) / ComputationUtil.module(disp), disp));
			newPos.x = Math.min(canvasSize, Math.max(0, newPos.x));
			newPos.y = Math.min(canvasSize, Math.max(0, newPos.y));
			vertex.getValue().getCoordinate().set(newPos);
			aggregate(MAX_CHANGE_AGG, new DoubleWritable(ComputationUtil.distance(oldPos, newPos)));
		}
	}

	private Coordinate repel(Coordinate self, Coordinate ref) {
		Coordinate repVector = ComputationUtil.subtractCoor(self, ref);
		double dist = ComputationUtil.distance(self, ref);
		double force = kValue * kValue / dist;
		return ComputationUtil.multiplyCoor(force / dist, repVector);
	}

	private Coordinate attract(Coordinate self, Coordinate ref) {
		Coordinate attVector = ComputationUtil.subtractCoor(ref, self);
		double dist = ComputationUtil.distance(self, ref);
		double force = dist * dist / kValue;
		return ComputationUtil.multiplyCoor(force / dist, attVector);
	}
}