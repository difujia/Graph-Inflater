/*
* @(\#) DatatypeTest.java 1.1 22 April 2014
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
package datatypes.writable.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import inflater.datatypes.Coordinate;
import inflater.datatypes.writable.CoordinateWritable;
import inflater.datatypes.writable.EdgeValuesWritable;
import inflater.datatypes.writable.VertexValuesWritable;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;
import org.junit.Test;

public class DatatypeTest {

	@Test
	public void testSaveCoordinate() {
		Random random = new Random();
		CoordinateWritable coordinate1 = new CoordinateWritable(new Coordinate(random.nextDouble(),
				random.nextDouble(), random.nextDouble()));
		DataInput buf = save(coordinate1);
		try {
			CoordinateWritable coordinate2 = CoordinateWritable.read(buf);
			assertEquals(coordinate1, coordinate2);
		} catch (IOException e) {
			e.printStackTrace();
			fail("unable to read back");
		}
	}

	@Test
	public void testSaveVertexValues() {
		Random random = new Random();
		CoordinateWritable coordinate = new CoordinateWritable(new Coordinate(random.nextDouble(), random.nextDouble(),
				random.nextDouble()));
		VertexValuesWritable vertexValue1 = new VertexValuesWritable(coordinate, new DoubleWritable(random.nextDouble()));
		DataInput buf = save(vertexValue1);
		try {
			VertexValuesWritable vertexValue2 = VertexValuesWritable.read(buf);
			assertEquals(vertexValue1, vertexValue2);
		} catch (IOException e) {
			e.printStackTrace();
			fail("unable to read back");
		}
	}

	@Test
	public void testSaveEdgeValues() {
		Random random = new Random();
		EdgeValuesWritable edgeValue1 = new EdgeValuesWritable(random.nextDouble());
		DataInput buf = save(edgeValue1);
		try {
			EdgeValuesWritable edgeValue2 = EdgeValuesWritable.read(buf);
			assertEquals(edgeValue1, edgeValue2);
		} catch (IOException e) {
			e.printStackTrace();
			fail("undable to read back");
		}
	}

	// helper
	public DataInput save(Writable original) {
		byte[] data = WritableUtils.toByteArray(original);
		DataInput buf = new DataInputStream(new ByteArrayInputStream(data));
		return buf;
	}

}
