/*
* @(\#) CoordinateWritable.java 1.1 22 April 2014
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
import org.apache.hadoop.io.Writable;

/**
 * Writable wrapper for Coordinate
 * @author Fujia Di
 *
 */
public class CoordinateWritable implements Writable {

	private Coordinate	coordinate;

	public static CoordinateWritable read(DataInput in) throws IOException {
		CoordinateWritable cw = new CoordinateWritable();
		cw.readFields(in);
		return cw;
	}

	public CoordinateWritable() {
		set(new Coordinate());
	}

	public CoordinateWritable(Coordinate c) {
		set(c);
	}

	public Coordinate get() {
		return coordinate;
	}

	public void set(Coordinate c) {
		coordinate = c;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		coordinate.x = in.readDouble();
		coordinate.y = in.readDouble();
		coordinate.z = in.readDouble();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeDouble(coordinate.x);
		out.writeDouble(coordinate.y);
		out.writeDouble(coordinate.z);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(coordinate).toHashCode();
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
		CoordinateWritable that = (CoordinateWritable) obj;
		return new EqualsBuilder().append(this.coordinate, that.coordinate).isEquals();
	}

	@Override
	public String toString() {
		return "[CoordinateWritable " + coordinate + "]";
	}
}
