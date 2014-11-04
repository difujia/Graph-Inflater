/*
* @(\#) MessageWritable.java 1.1 22 April 2014
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

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;

/**
 * Writable wrapper for message
 * @author Pengfei Wang
 *
 */
public class MessageWritable implements Writable {

	private LongWritable		senderId;
	private CoordinateWritable	senderCoordinate;
	private BooleanWritable		linked;

	public static MessageWritable read(DataInput in) throws IOException {
		MessageWritable message = new MessageWritable();
		message.readFields(in);
		return message;
	}

	public MessageWritable() {
		this(new LongWritable(), new CoordinateWritable(), new BooleanWritable());
	}

	public MessageWritable(LongWritable senderId, CoordinateWritable senderCoordinate, BooleanWritable linked) {
		this.senderId = senderId;
		this.senderCoordinate = senderCoordinate;
		this.linked = linked;
	}

	public MessageWritable(long senderId, Coordinate senderCoordinate, boolean linked) {
		this(new LongWritable(senderId), new CoordinateWritable(senderCoordinate), new BooleanWritable(linked));
	}

	public LongWritable getSenderId() {
		return senderId;
	}

	public void setSenderId(LongWritable senderId) {
		this.senderId = senderId;
	}

	public CoordinateWritable getSenderCoordinate() {
		return senderCoordinate;
	}

	public void setSenderCoordinate(CoordinateWritable senderCoordinate) {
		this.senderCoordinate = senderCoordinate;
	}

	public BooleanWritable getLinked() {
		return linked;
	}

	public void setLinked(BooleanWritable linked) {
		this.linked = linked;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		senderId.readFields(in);
		senderCoordinate.readFields(in);
		linked.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		senderId.write(out);
		senderCoordinate.write(out);
		linked.write(out);
	}
}
