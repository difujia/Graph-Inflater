/*
* @(\#) ComputationConstants.java 1.1 22 April 2014
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

/**
 * Aggregator names and configurations names used by computation
 * @author Fujia Di
 *
 */
interface ComputationConstants {
	/** Max change aggregator name */
	static final String	MAX_CHANGE_AGG	= "computation.maxchange";
	/** Temperature aggregator name */
	static final String	TEMPERATURE_AGG	= "computation.tempreature";
	/** kValue */
	static final String	KVALUE			= "inflater.computation.kValue";
	/** size */
	static final String	SCALE			= "inflater.computation.scale";
	/** Minimun cooling rate */
	static final String	MIN_COOL_RATE	= "inflater.computation.minCoolRate";
	/** Minimun refinement */
	static final String	MIN_REFINE		= "inflater.compuatation.minRefine";
}
