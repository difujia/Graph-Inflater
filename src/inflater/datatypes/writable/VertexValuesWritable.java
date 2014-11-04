/*
* @(\#) VertexWritable.java 1.1 22 April 2014
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
package inflater.datatypes.writable;

import inflater.datatypes.Coordinate;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Writable;

/**
 * Writable wrapper for vertex values
 * @author Fujia Di
 *
 */
public class VertexValuesWritable implements Writable {

	private CoordinateWritable	coordinate;
	private DoubleWritable		weight;

	public static VertexValuesWritable read(DataInput in) throws IOException {
		VertexValuesWritable instance = new VertexValuesWritable();
		instance.readFields(in);
		return instance;
	}

	public VertexValuesWritable() {
		this(new CoordinateWritable(), new DoubleWritable());
	}

	public VertexValuesWritable(Coordinate coordinate, double weight) {
		this(new CoordinateWritable(coordinate), new DoubleWritable(weight));
	}

	public VertexValuesWritable(CoordinateWritable coordinate, DoubleWritable weight) {
		setCoordinate(coordinate);
		setWeight(weight);
	}

	public CoordinateWritable getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(CoordinateWritable coordinate) {
		this.coordinate = coordinate;
	}

	public DoubleWritable getWeight() {
		return weight;
	}

	public void setWeight(DoubleWritable weight) {
		this.weight = weight;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		coordinate.readFields(in);
		weight.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		coordinate.write(out);
		weight.write(out);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(coordinate).append(weight).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		VertexValuesWritable that = (VertexValuesWritable) obj;
		return new EqualsBuilder().append(this.coordinate, that.coordinate)
								  .append(this.weight, that.weight).isEquals();
	}
	
	@Override
	public String toString() {
		return "[VertexDataWritable coordinate: " + coordinate + " weight: " + weight + "]";
	}
}