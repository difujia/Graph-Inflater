/*
* @(\#) ComputationUtil.java 1.1 22 April 2014
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

/**
 * @author Pengfei Wang
 *
 */
public class ComputationUtil {

	/** Return the module of a vector */
	public static double module(Coordinate c) {
		double m = 0;
		m = Math.sqrt(c.x * c.x + c.y * c.y);
		return m;
	}

	/** Adding 2 vectors */
	public static Coordinate addCoor(Coordinate c1, Coordinate c2) {
		Coordinate c = new Coordinate();
		c.setXY(c1.x + c2.x, c1.y + c2.y);
		return c;
	}

	/** Subtraction of 2 vectors */
	public static Coordinate subtractCoor(Coordinate c1, Coordinate c2) {
		Coordinate c = new Coordinate();
		c.setXY(c1.x - c2.x, c1.y - c2.y);
		return c;
	}

	/** A number multiply a vector */
	public static Coordinate multiplyCoor(double a, Coordinate c) {
		return new Coordinate(c.x * a, c.y * a);
	}

	/** Return the distance of two points */
	public static double distance(Coordinate c1, Coordinate c2) {
		double d = 0;
		d = Math.sqrt((c1.x - c2.x) * (c1.x - c2.x) + (c1.y - c2.y) * (c1.y - c2.y));
		return d;
	}
}
