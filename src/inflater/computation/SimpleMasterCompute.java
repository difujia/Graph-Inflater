/*
* @(\#) SimpleMasterCompute.java 1.1 22 April 2014
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

import org.apache.giraph.aggregators.DoubleMaxAggregator;
import org.apache.giraph.aggregators.DoubleMinAggregator;
import org.apache.giraph.master.DefaultMasterCompute;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.log4j.Logger;

/**
 * MasterCompute for Fruchterman's algorithm. This includes Cooling functions and termination condition.
 * @author Pengfei Wang, Fujia Di
 *
 */
public class SimpleMasterCompute extends DefaultMasterCompute implements ComputationConstants {
	private static final Logger	LOG	= Logger.getLogger(SimpleMasterCompute.class);

	@Override
	public void initialize() throws InstantiationException, IllegalAccessException {
		System.out.println(DoubleMaxAggregator.class.getName());
		registerAggregator(MAX_CHANGE_AGG, DoubleMaxAggregator.class);
		registerPersistentAggregator(TEMPERATURE_AGG, DoubleMinAggregator.class);
	}

	@Override
	public void compute() {

		double kValue = getConf().getDouble(KVALUE, -1);
		LOG.info("SuperStep: " + getSuperstep() + ". Temperature = " + getAggregatedValue(TEMPERATURE_AGG)
				+ ". MAX change = " + getAggregatedValue(MAX_CHANGE_AGG) + ". k value = " + kValue);
		LOG.info(getTotalNumVertices() + " vertices.");

		if (getSuperstep() > 1) {
			double maxChange = this.<DoubleWritable> getAggregatedValue(MAX_CHANGE_AGG).get();
			double temperature = this.<DoubleWritable> getAggregatedValue(TEMPERATURE_AGG).get();
			double coolRate = getConf().getDouble(MIN_COOL_RATE, 0.9);
			double minRefine = getConf().getDouble(MIN_REFINE, 0.1);

			if (maxChange < temperature) {
				setAggregatedValue(TEMPERATURE_AGG, new DoubleWritable(maxChange));
			} else {
				setAggregatedValue(TEMPERATURE_AGG, new DoubleWritable(temperature * coolRate));
			}

			if (this.<DoubleWritable> getAggregatedValue(TEMPERATURE_AGG).get() <= kValue * minRefine) {
				// stop when temperature is low enough
				haltComputation();
			}
		}
	}

}
